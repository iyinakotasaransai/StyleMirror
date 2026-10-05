/**
 * StyleMirror - MediaPipe Live Virtual Try-On Pipeline
 * Tracks body landmarks (shoulders, hips, torso), smooths movements,
 * calculates garment scale/rotation, renders overlay, detects stable frame,
 * and triggers AI styling. Includes photo-upload fallback.
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
    this.pose = null;
    this.camera = null;

    // Landmark Smoothing (Exponential Moving Average)
    this.smoothedTorso = { x: 0, y: 0, width: 0, height: 0, angle: 0 };
    this.smoothingAlpha = 0.35; // 0 = rigid past, 1 = raw instant

    // Stable Frame Detection
    this.stableFramesCount = 0;
    this.stableThreshold = 20; // Number of consecutive stable frames required
    this.tryonReadyTriggered = false;
    this.lastCenter = { x: 0, y: 0 };

    this.currentVariant = {
      size: "M",
      color: "Default",
    };

    this.init();
  }

  async init() {
    await this.loadGarmentAsset();
    this.initMediaPipePose();
    this.startWebcam();
  }

  async loadGarmentAsset() {
    try {
      const res = await api.get(`/tryon/product/${this.productId}/asset`);
      if (res.success && res.data && res.data.overlayImagePath) {
        this.garmentImage.crossOrigin = "anonymous";
        this.garmentImage.src = res.data.overlayImagePath;
        this.garmentImage.onload = () => {
          this.garmentLoaded = true;
          console.log("Try-on garment overlay loaded successfully.");
        };
      } else {
        this.showNotice("This garment does not have an active try-on overlay asset.");
      }
    } catch (err) {
      console.warn("Try-on asset error:", err.message);
      this.showNotice("Try-on asset unavailable. Demonstrating with default silhouette.");
      // Fallback default overlay
      this.garmentImage.src = "/assets/overlays/bomber_jacket.png";
      this.garmentImage.onload = () => { this.garmentLoaded = true; };
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
        video: { width: 640, height: 480, facingMode: "user" },
        audio: false,
      });

      this.video.srcObject = stream;
      await this.video.play();

      this.canvas.width = this.video.videoWidth || 640;
      this.canvas.height = this.video.videoHeight || 480;

      this.updateHudStatus("DETECTING_USER", "Please stand in front of the camera");

      // Frame Processing Loop
      const processFrame = async () => {
        if (this.pose && this.video.readyState >= 2) {
          await this.pose.send({ image: this.video });
        }
        requestAnimationFrame(processFrame);
      };
      requestAnimationFrame(processFrame);
    } catch (err) {
      console.error("Camera access denied or error:", err);
      this.updateHudStatus("CAMERA_DENIED", "Camera access denied. Use photo upload fallback below.");
      const fallbackEl = document.getElementById("photo-fallback-panel");
      if (fallbackEl) fallbackEl.style.display = "block";
    }
  }

  onPoseResults(results) {
    if (!this.ctx || !this.canvas) return;

    this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

    if (!results.poseLandmarks) {
      this.stableFramesCount = 0;
      this.updateHudStatus("SEARCHING", "Please position yourself inside the frame");
      return;
    }

    const landmarks = results.poseLandmarks;

    // Body Landmark Indices (MediaPipe)
    // 11: Left Shoulder, 12: Right Shoulder
    // 23: Left Hip, 24: Right Hip
    const leftShoulder = landmarks[11];
    const rightShoulder = landmarks[12];
    const leftHip = landmarks[23];
    const rightHip = landmarks[24];

    if (!leftShoulder || !rightShoulder || leftShoulder.visibility < 0.4 || rightShoulder.visibility < 0.4) {
      this.stableFramesCount = 0;
      this.updateHudStatus("SEARCHING", "Step back so upper torso and shoulders are visible");
      return;
    }

    const W = this.canvas.width;
    const H = this.canvas.height;

    // Calculate Screen Coordinates
    const lsX = leftShoulder.x * W;
    const lsY = leftShoulder.y * H;
    const rsX = rightShoulder.x * W;
    const rsY = rightShoulder.y * H;

    // Shoulder Width & Angle
    const dx = rsX - lsX;
    const dy = rsY - lsY;
    const shoulderDist = Math.sqrt(dx * dx + dy * dy);
    const angle = Math.atan2(dy, dx);

    // Torso Center
    const rawCenterX = (lsX + rsX) / 2;
    const rawCenterY = (lsY + rsY) / 2;

    // Garment Scaling (Shoulder span factor + hip length estimation)
    let torsoHeight = shoulderDist * 1.35;
    if (leftHip && rightHip && leftHip.visibility > 0.4 && rightHip.visibility > 0.4) {
      const hipMidY = ((leftHip.y + rightHip.y) / 2) * H;
      torsoHeight = Math.max(torsoHeight, (hipMidY - rawCenterY) * 1.25);
    }

    const targetWidth = shoulderDist * 1.85;
    const targetHeight = torsoHeight * 1.3;

    // Smoothing with Exponential Moving Average
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

    // Check Frame Stability
    const movement = Math.hypot(rawCenterX - this.lastCenter.x, rawCenterY - this.lastCenter.y);
    this.lastCenter = { x: rawCenterX, y: rawCenterY };

    if (movement < 4.0) {
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
      this.updateHudStatus("CALIBRATING", `Hold position to calibrate (${Math.round((this.stableFramesCount / this.stableThreshold) * 100)}%)`);
    }

    // Render Garment Overlay onto Canvas
    this.drawGarmentOverlay();
  }

  drawGarmentOverlay() {
    if (!this.garmentLoaded) return;

    const { x, y, width, height, angle } = this.smoothedTorso;

    this.ctx.save();
    this.ctx.translate(x, y + height * 0.22); // Center over chest and torso
    this.ctx.rotate(angle);

    this.ctx.drawImage(
      this.garmentImage,
      -width / 2,
      -height / 2,
      width,
      height
    );

    this.ctx.restore();
  }

  triggerAiStyling() {
    console.log("TRYON_READY triggered -> Calling AI Stylist Pipeline...");
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

  showNotice(msg) {
    utils.showToast(msg, "info");
  }

  // Handle Photo Fallback File Upload
  handlePhotoUpload(file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      const img = new Image();
      img.onload = () => {
        if (this.ctx && this.canvas) {
          this.canvas.width = img.width;
          this.canvas.height = img.height;
          this.ctx.drawImage(img, 0, 0);
          // If garment is available, draw at center
          if (this.garmentLoaded) {
            this.ctx.drawImage(
              this.garmentImage,
              img.width * 0.25,
              img.height * 0.25,
              img.width * 0.5,
              img.height * 0.55
            );
          }
          this.updateHudStatus("TRYON_READY", "Photo try-on rendered successfully.");
          this.triggerAiStyling();
        }
      };
      img.src = e.target.result;
    };
    reader.readAsDataURL(file);
  }
}
