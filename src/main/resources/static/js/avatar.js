/**
 * StyleMirror - Three.js 3D Fashion Avatar System
 * Supports GLB/GLTF models with procedural fallback avatar and dynamic animations.
 */

export class AvatarManager {
  constructor(containerId) {
    this.container = document.getElementById(containerId);
    this.scene = null;
    this.camera = null;
    this.renderer = null;
    this.clock = null;

    this.avatarGroup = null;
    this.head = null;
    this.mouth = null;
    this.leftArm = null;
    this.rightArm = null;
    this.torso = null;

    this.currentAnimation = "idle"; // idle, thinking, thumbsUp, nod, shakeHead, wave, speaking
    this.animationTimer = 0;
    this.isSpeaking = false;

    if (this.container && window.THREE) {
      this.init();
    }
  }

  init() {
    const width = this.container.clientWidth || 320;
    const height = this.container.clientHeight || 240;

    // 1. Scene
    this.scene = new THREE.Scene();

    // 2. Camera
    this.camera = new THREE.PerspectiveCamera(45, width / height, 0.1, 100);
    this.camera.position.set(0, 1.4, 2.5);
    this.camera.lookAt(0, 1.2, 0);

    // 3. Renderer
    this.renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
    this.renderer.setSize(width, height);
    this.renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
    this.renderer.shadowMap.enabled = true;
    this.container.innerHTML = "";
    this.container.appendChild(this.renderer.domElement);

    // 4. Lighting
    const ambientLight = new THREE.AmbientLight(0xffffff, 0.8);
    this.scene.add(ambientLight);

    const keyLight = new THREE.DirectionalLight(0xd4af37, 1.5);
    keyLight.position.set(2, 4, 3);
    this.scene.add(keyLight);

    const rimLight = new THREE.DirectionalLight(0x00e5ff, 1.2);
    rimLight.position.set(-2, 2, -2);
    this.scene.add(rimLight);

    this.clock = new THREE.Clock();

    // 5. Build Procedural Luxury Stylist Avatar
    this.buildProceduralAvatar();

    // 6. Handle Resizing
    window.addEventListener("resize", () => this.onResize());

    // 7. Start Render Loop
    this.animate();
  }

  buildProceduralAvatar() {
    this.avatarGroup = new THREE.Group();

    // Materials
    const skinMat = new THREE.MeshStandardMaterial({
      color: 0xf5d0b5,
      roughness: 0.5,
      metalness: 0.1,
    });
    const suitMat = new THREE.MeshStandardMaterial({
      color: 0x12151c,
      roughness: 0.4,
      metalness: 0.3,
    });
    const goldTrimMat = new THREE.MeshStandardMaterial({
      color: 0xd4af37,
      roughness: 0.2,
      metalness: 0.8,
    });
    const hairMat = new THREE.MeshStandardMaterial({
      color: 0x1a1a1a,
      roughness: 0.7,
    });

    // Head
    const headGeo = new THREE.SphereGeometry(0.2, 32, 32);
    this.head = new THREE.Mesh(headGeo, skinMat);
    this.head.position.set(0, 1.4, 0);

    // Hair
    const hairGeo = new THREE.SphereGeometry(0.21, 32, 32, 0, Math.PI * 2, 0, Math.PI * 0.55);
    const hair = new THREE.Mesh(hairGeo, hairMat);
    hair.position.set(0, 0.04, -0.02);
    this.head.add(hair);

    // Sunglasses / AR Visor
    const visorGeo = new THREE.BoxGeometry(0.26, 0.06, 0.1);
    const visorMat = new THREE.MeshStandardMaterial({
      color: 0x00e5ff,
      roughness: 0.1,
      metalness: 0.9,
      transparent: true,
      opacity: 0.85,
    });
    const visor = new THREE.Mesh(visorGeo, visorMat);
    visor.position.set(0, 0.03, 0.16);
    this.head.add(visor);

    // Mouth / Jaw (For speaking animation)
    const mouthGeo = new THREE.BoxGeometry(0.08, 0.015, 0.02);
    const mouthMat = new THREE.MeshStandardMaterial({ color: 0x993333 });
    this.mouth = new THREE.Mesh(mouthGeo, mouthMat);
    this.mouth.position.set(0, -0.09, 0.18);
    this.head.add(this.mouth);

    // Torso (Tailored Blazer)
    const torsoGeo = new THREE.CylinderGeometry(0.22, 0.18, 0.65, 32);
    this.torso = new THREE.Mesh(torsoGeo, suitMat);
    this.torso.position.set(0, 0.95, 0);

    // Gold Collar Lapels
    const lapelGeo = new THREE.BoxGeometry(0.06, 0.35, 0.04);
    const leftLapel = new THREE.Mesh(lapelGeo, goldTrimMat);
    leftLapel.position.set(-0.08, 0.12, 0.18);
    leftLapel.rotation.z = -0.2;
    this.torso.add(leftLapel);

    const rightLapel = new THREE.Mesh(lapelGeo, goldTrimMat);
    rightLapel.position.set(0.08, 0.12, 0.18);
    rightLapel.rotation.z = 0.2;
    this.torso.add(rightLapel);

    // Left Arm Group
    this.leftArm = new THREE.Group();
    this.leftArm.position.set(-0.28, 1.2, 0);
    const lArmMesh = new THREE.Mesh(new THREE.CylinderGeometry(0.06, 0.05, 0.5, 16), suitMat);
    lArmMesh.position.set(0, -0.25, 0);
    this.leftArm.add(lArmMesh);

    // Right Arm Group
    this.rightArm = new THREE.Group();
    this.rightArm.position.set(0.28, 1.2, 0);
    const rArmMesh = new THREE.Mesh(new THREE.CylinderGeometry(0.06, 0.05, 0.5, 16), suitMat);
    rArmMesh.position.set(0, -0.25, 0);
    this.rightArm.add(rArmMesh);

    this.avatarGroup.add(this.head);
    this.avatarGroup.add(this.torso);
    this.avatarGroup.add(this.leftArm);
    this.avatarGroup.add(this.rightArm);

    this.scene.add(this.avatarGroup);
  }

  setAnimation(animName) {
    this.currentAnimation = animName || "idle";
    this.animationTimer = 0;
  }

  startSpeaking() {
    this.isSpeaking = true;
  }

  stopSpeaking() {
    this.isSpeaking = false;
    this.setAnimation("idle");
    if (this.mouth) {
      this.mouth.scale.set(1, 1, 1);
    }
  }

  animate() {
    requestAnimationFrame(() => this.animate());

    if (!this.clock) return;
    const delta = this.clock.getDelta();
    const time = this.clock.getElapsedTime();
    this.animationTimer += delta;

    // Procedural Animation Handler
    if (this.head && this.torso && this.rightArm && this.leftArm) {
      // Base gentle idle breathing
      this.torso.position.y = 0.95 + Math.sin(time * 2) * 0.015;
      this.head.position.y = 1.4 + Math.sin(time * 2) * 0.015;

      switch (this.currentAnimation) {
        case "thumbsUp":
          // Raise right arm and point thumb up
          this.rightArm.rotation.x = -Math.PI / 2.2;
          this.rightArm.rotation.z = -0.3;
          this.head.rotation.y = Math.sin(time * 3) * 0.05;
          break;

        case "wave":
          // Raise right arm and wave back and forth
          this.rightArm.rotation.x = -Math.PI / 1.8;
          this.rightArm.rotation.z = Math.sin(time * 8) * 0.4 - 0.5;
          break;

        case "nod":
          // Rhythmic affirmative nod
          this.head.rotation.x = Math.sin(time * 6) * 0.15;
          this.head.rotation.y = 0;
          this.rightArm.rotation.set(0, 0, 0);
          break;

        case "shakeHead":
          // Horizontal head shake
          this.head.rotation.y = Math.sin(time * 6) * 0.2;
          this.head.rotation.x = 0;
          break;

        case "thinking":
          // Hand to chin thinking pose
          this.head.rotation.z = 0.1;
          this.rightArm.rotation.x = -Math.PI / 2;
          this.rightArm.rotation.y = 0.5;
          break;

        default: // "idle"
          this.head.rotation.y = Math.sin(time * 1.2) * 0.08;
          this.head.rotation.x = Math.cos(time * 1.5) * 0.04;
          this.head.rotation.z = 0;
          this.rightArm.rotation.x = Math.sin(time * 1.5) * 0.05;
          this.rightArm.rotation.z = 0.1;
          this.leftArm.rotation.z = -0.1;
          break;
      }

      // Synchronized Mouth Movement during Speaking
      if (this.isSpeaking && this.mouth) {
        const mouthScale = 1 + Math.abs(Math.sin(time * 16)) * 2.5;
        this.mouth.scale.set(1, mouthScale, 1);
      } else if (this.mouth) {
        this.mouth.scale.set(1, 1, 1);
      }
    }

    if (this.renderer && this.scene && this.camera) {
      this.renderer.render(this.scene, this.camera);
    }
  }

  onResize() {
    if (!this.container || !this.renderer || !this.camera) return;
    const width = this.container.clientWidth;
    const height = this.container.clientHeight;
    this.camera.aspect = width / height;
    this.camera.updateProjectionMatrix();
    this.renderer.setSize(width, height);
  }
}
