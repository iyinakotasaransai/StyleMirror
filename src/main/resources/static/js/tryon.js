/**
 * StyleMirror - Photorealistic Virtual Try-On Pipeline
 * Anatomical body landmark tracking with MediaPipe Pose, tailored garment fitting
 * for Men and Women dresses/suits, real-time user fit adjustments,
 * dynamic garment swapping, and photo-fitting support.
 */

import { api } from "./api.js";
import { utils } from "./utils.js";
import { ai } from "./ai.js";

export class VirtualTryOnStudio {
  constructor(options) {
    this.video = document.getElementById(options.videoId);
    this.canvas = document.getElementById(options.canvasId);
    this.ctx = this.canvas ? this.canvas.getContext("2d") : null;
    this.productId = options.productId;
    this.avatarManager = options.avatarManager;

    this.garmentImage = new Image();
    this.garmentLoaded = false;
    this.currentGarmentCategory = "Default";
    this.pose = null;

    // Landmark Smoothing (Exponential Moving Average)
    this.smoothedTorso = { x: 0, y: 0, width: 0, height: 0, angle: 0 };
    this.smoothingAlpha = 0.30; // Smooth natural interpolation

    // Interactive Fit Controls (Adjustable in Real Time)
    this.userScale = 1.0;       // Scale: 0.8x to 1.3x
    this.userOffsetY = 0;       // Vertical alignment: -80px to +80px
    this.userSpan = 1.0;        // Shoulder span: 0.8x to 1.3x
    this.userOpacity = 0.95;    // Fabric blend: 0.7 to 1.0

    // Stable Frame Detection
    this.stableFramesCount = 0;
    this.stableThreshold = 18;
    this.tryonReadyTriggered = false;
    this.lastCenter = { x: 0, y: 0 };

    this.currentVariant = {
      size: "M",
      color: "Default",
    };

    this.isPhotoMode = false;
    this.uploadedPhoto = null;

    this.init();
  }

  async init() {
    await this.loadGarmentAsset(this.productId);
    this.initMediaPipePose();
    this.startWebcam();
  }

  async loadGarmentAsset(prodId) {
    this.garmentLoaded = false;
    try {
      const res = await api.get(`/tryon/product/${prodId}/asset`);
      if (res.success && res.data && res.data.overlayImagePath) {
        this.garmentImage.crossOrigin = "anonymous";
        this.garmentImage.src = res.data.overlayImagePath;
        this.garmentImage.onload = () => {
          this.garmentLoaded = true;
          console.log("Try-on garment overlay loaded:", res.data.overlayImagePath);
        };
      } else {
        this.fallbackGarment();
      }
    } catch (err) {
      console.warn("Try-on asset load error:", err.message);
      this.fallbackGarment();
    }
  }

  fallbackGarment() {
    this.garmentImage.crossOrigin = "anonymous";
    this.garmentImage.src = "/assets/overlays/velvet_dress.png";
    this.garmentImage.onload = () => {
      this.garmentLoaded = true;
    };
  }

  /**
   * Switch active garment in the live fitting room without restarting camera
   */
  async switchGarment(newProdId, overlayPath = null, category = "Default") {
    this.productId = newProdId;
    this.currentGarmentCategory = category;
    this.tryonReadyTriggered = false;
    this.stableFramesCount = 0;

    if (overlayPath) {
      this.garmentLoaded = false;
      this.garmentImage.crossOrigin = "anonymous";
      this.garmentImage.src = overlayPath;
      this.garmentImage.onload = () => {
        this.garmentLoaded = true;
        utils.showToast("Garment switched in Fitting Studio", "success");
      };
    } else {
      await this.loadGarmentAsset(newProdId);
    }
  }

  initMediaPipePose() {
    if (!window.Pose) {
      console.warn("MediaPipe Pose library is not loaded.");
      return;
    }

    this.pose = new window.Pose({
      locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/pose@0.5.1675469404/${file}`,
    });

    this.pose.setOptions({
      modelComplexity: 1,
      smoothLandmarks: true,
      enableSegmentation: false,
      minDetectionConfidence: 0.5,
      minTrackingConfidence: 0.5,
    });

    this.pose.onResults((results) => this.onPoseResults(results));
  }

  async startWebcam() {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { width: { ideal: 1280 }, height: { ideal: 720 }, facingMode: "user" },
        audio: false,
      });

      this.video.srcObject = stream;
      await this.video.play();

      this.canvas.width = this.video.videoWidth || 640;
      this.canvas.height = this.video.videoHeight || 480;

      this.updateHudStatus("CALIBRATING", "Stand in view of the mirror to align shoulders");

      // Frame Processing Loop
      const processFrame = async () => {
        if (!this.isPhotoMode && this.pose && this.video.readyState >= 2) {
          await this.pose.send({ image: this.video });
        }
        requestAnimationFrame(processFrame);
      };
      requestAnimationFrame(processFrame);
    } catch (err) {
      console.error("Camera access denied or error:", err);
      this.updateHudStatus("CAMERA_DENIED", "Camera access blocked. Use photo upload below.");
      const fallbackEl = document.getElementById("photo-fallback-panel");
      if (fallbackEl) fallbackEl.style.display = "block";
    }
  }

  onPoseResults(results) {
    if (!this.ctx || !this.canvas) return;

    this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

    // If photo mode, draw background photo first
    if (this.isPhotoMode && this.uploadedPhoto) {
      this.ctx.drawImage(this.uploadedPhoto, 0, 0, this.canvas.width, this.canvas.height);
    }

    if (!results.poseLandmarks) {
      this.stableFramesCount = 0;
      this.updateHudStatus("SEARCHING", "Position your upper body inside the frame");
      return;
    }

    const landmarks = results.poseLandmarks;

    // MediaPipe Key Upper Body Landmarks:
    // 11: Left Shoulder, 12: Right Shoulder
    // 23: Left Hip, 24: Right Hip
    // 0: Nose (head elevation reference)
    const leftShoulder = landmarks[11];
    const rightShoulder = landmarks[12];
    const leftHip = landmarks[23];
    const rightHip = landmarks[24];

    if (!leftShoulder || !rightShoulder || leftShoulder.visibility < 0.35 || rightShoulder.visibility < 0.35) {
      this.stableFramesCount = 0;
      this.updateHudStatus("SEARCHING", "Step back slightly so shoulders and chest are visible");
      return;
    }

    const W = this.canvas.width;
    const H = this.canvas.height;

    // Pixel coordinates in unmirrored video frame
    const lsX = leftShoulder.x * W;
    const lsY = leftShoulder.y * H;
    const rsX = rightShoulder.x * W;
    const rsY = rightShoulder.y * H;

    // Shoulder Span (Euclidean distance)
    const rawDx = lsX - rsX;
    const rawDy = rsY - lsY;
    const shoulderDist = Math.max(30, Math.sqrt(rawDx * rawDx + rawDy * rawDy));

    // Anatomical Shoulder Tilt Angle:
    // When mirrored, tilt angle rotates relative to horizontal line:
    let angle = Math.atan2(rawDy, Math.abs(rawDx));
    // Clamp angle to natural human range (-30 to +30 degrees)
    angle = Math.max(-0.52, Math.min(0.52, angle));

    // Collar / Clavicle Center Anchor
    const rawCenterX = (lsX + rsX) / 2;
    const rawCenterY = (lsY + rsY) / 2;

    // Torso Height calculation (using hips if visible, or anatomical head/shoulder ratio)
    let torsoHeight = shoulderDist * 1.55;
    if (leftHip && rightHip && leftHip.visibility > 0.35 && rightHip.visibility > 0.35) {
      const hipMidY = ((leftHip.y + rightHip.y) / 2) * H;
      const detectedTorso = hipMidY - rawCenterY;
      if (detectedTorso > shoulderDist * 0.8) {
        torsoHeight = detectedTorso;
      }
    }

    // Determine Garment Dimensions based on Category / Style
    let fitWidthFactor = 1.68 * this.userSpan;
    let fitHeightFactor = 1.35;

    // Dresses (Evening gowns, cocktail dresses) extend down past the hips
    const isDress = this.currentGarmentCategory.toLowerCase().includes("dress") ||
                    (this.garmentImage.src && this.garmentImage.src.includes("dress")) ||
                    (this.garmentImage.src && this.garmentImage.src.includes("gown"));

    if (isDress) {
      fitWidthFactor = 1.55 * this.userSpan;
      fitHeightFactor = 2.15; // Extends gracefully to knees/calves
    }

    const targetWidth = shoulderDist * fitWidthFactor * this.userScale;
    const targetHeight = torsoHeight * fitHeightFactor * this.userScale;

    // Smoothing with Exponential Moving Average (EMA)
    if (this.smoothedTorso.width === 0) {
      this.smoothedTorso.x = rawCenterX;
      this.smoothedTorso.y = rawCenterY;
      this.smoothedTorso.width = targetWidth;
      this.smoothedTorso.height = targetHeight;
      this.smoothedTorso.angle = angle;
    } else {
      const a = this.smoothingAlpha;
      this.smoothedTorso.x = a * rawCenterX + (1 - a) * this.smoothedTorso.x;
      this.smoothedTorso.y = a * rawCenterY + (1 - a) * this.smoothedTorso.y;
      this.smoothedTorso.width = a * targetWidth + (1 - a) * this.smoothedTorso.width;
      this.smoothedTorso.height = a * targetHeight + (1 - a) * this.smoothedTorso.height;
      this.smoothedTorso.angle = a * angle + (1 - a) * this.smoothedTorso.angle;
    }

    // Check Frame Stability for AI Stylist Trigger
    const movement = Math.hypot(rawCenterX - this.lastCenter.x, rawCenterY - this.lastCenter.y);
    this.lastCenter = { x: rawCenterX, y: rawCenterY };

    if (movement < 4.5) {
      this.stableFramesCount++;
    } else {
      this.stableFramesCount = Math.max(0, this.stableFramesCount - 2);
    }

    if (this.stableFramesCount >= this.stableThreshold) {
      this.updateHudStatus("TRYON_READY", "Fit stabilized. Styling analysis active.");
      if (!this.tryonReadyTriggered) {
        this.tryonReadyTriggered = true;
        this.triggerAiStyling();
      }
    } else {
      const pct = Math.min(100, Math.round((this.stableFramesCount / this.stableThreshold) * 100));
      this.updateHudStatus("CALIBRATING", `Aligning garment to body contours (${pct}%)`);
    }

    // Render Fitted Garment Overlay onto Canvas
    this.drawGarmentOverlay(isDress);
  }

  drawGarmentOverlay(isDress) {
    if (!this.garmentLoaded) return;

    const { x, y, width, height, angle } = this.smoothedTorso;

    this.ctx.save();
    this.ctx.globalAlpha = this.userOpacity;

    // Natural shadow for depth and layering over the user's clothes
    this.ctx.shadowColor = "rgba(0, 0, 0, 0.35)";
    this.ctx.shadowBlur = 16;
    this.ctx.shadowOffsetY = 6;

    // Translate to clavicle/shoulder midpoint
    this.ctx.translate(x, y);

    // Apply shoulder tilt rotation
    this.ctx.rotate(angle);

    // Precise vertical offset:
    // Garment neckline sits right at or slightly above the clavicle line
    // -height * 0.08 places the top collar nicely at the neck level
    const collarTopOffset = isDress ? (-height * 0.06) : (-height * 0.09);
    const finalOffsetY = collarTopOffset + this.userOffsetY;

    this.ctx.drawImage(
      this.garmentImage,
      -width / 2,
      finalOffsetY,
      width,
      height
    );

    this.ctx.restore();
  }

  triggerAiStyling() {
    console.log("TRYON_READY -> Triggering AI Stylist Consultation...");
    ai.requestRecommendation(this.productId, this.currentVariant, this.avatarManager);
  }

  updateHudStatus(state, message) {
    const indicator = document.getElementById("tryon-status-indicator");
    const label = document.getElementById("tryon-status-text");
    const inst = document.getElementById("tryon-instructions");

    if (label) label.textContent = state;
    if (inst) inst.textContent = message;

    if (indicator) {
      if (state === "TRYON_READY") {
        indicator.className = "status-indicator ready";
      } else {
        indicator.className = "status-indicator";
      }
    }
  }

  // Handle Photo Upload (for trying on photo of oneself)
  async handlePhotoUpload(file) {
    this.isPhotoMode = true;
    const reader = new FileReader();

    reader.onload = (e) => {
      const img = new Image();
      img.onload = async () => {
        this.uploadedPhoto = img;
        this.canvas.width = img.width;
        this.canvas.height = img.height;

        this.updateHudStatus("CALIBRATING", "Analyzing body landmarks on photo...");

        if (this.pose) {
          // Process pose landmarks on the uploaded photo
          await this.pose.send({ image: img });
          this.updateHudStatus("TRYON_READY", "Garment fitted to photograph.");
          this.triggerAiStyling();
        } else {
          // Fallback static composite if pose library is unavailable
          this.ctx.drawImage(img, 0, 0);
          if (this.garmentLoaded) {
            const gw = img.width * 0.55;
            const gh = img.height * 0.65;
            this.ctx.drawImage(this.garmentImage, (img.width - gw) / 2, img.height * 0.22, gw, gh);
          }
          this.updateHudStatus("TRYON_READY", "Photo try-on rendered.");
          this.triggerAiStyling();
        }
      };
      img.src = e.target.result;
    };
    reader.readAsDataURL(file);
  }
}
