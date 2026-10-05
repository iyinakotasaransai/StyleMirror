/**
 * StyleMirror - SpeechSynthesis & Synchronized Subtitles System
 */

export const voice = {
  synth: window.speechSynthesis,
  isMuted: false,
  selectedVoice: null,
  activeUtterance: null,

  init() {
    if (!this.synth) {
      console.warn("Browser SpeechSynthesis is not supported on this platform.");
      return;
    }

    const loadVoices = () => {
      const voices = this.synth.getVoices();
      // Select a natural English voice if available
      this.selectedVoice = voices.find(v => v.lang.startsWith("en") && (v.name.includes("Natural") || v.name.includes("Google") || v.name.includes("Samantha") || v.name.includes("Female"))) || voices[0];
    };

    loadVoices();
    if (speechSynthesis.onvoiceschanged !== undefined) {
      speechSynthesis.onvoiceschanged = loadVoices;
    }
  },

  speak(text, onStartCallback = null, onEndCallback = null) {
    if (!this.synth || this.isMuted || !text) {
      this.displaySubtitles(text);
      if (onStartCallback) onStartCallback();
      setTimeout(() => {
        this.hideSubtitles();
        if (onEndCallback) onEndCallback();
      }, 4000);
      return;
    }

    this.stop(); // Stop any pending speech

    const utterance = new SpeechSynthesisUtterance(text);
    if (this.selectedVoice) {
      utterance.voice = this.selectedVoice;
    }
    utterance.rate = 1.0;
    utterance.pitch = 1.05;

    this.activeUtterance = utterance;

    utterance.onstart = () => {
      this.displaySubtitles(text);
      if (onStartCallback) onStartCallback();
    };

    utterance.onend = () => {
      setTimeout(() => this.hideSubtitles(), 1500);
      if (onEndCallback) onEndCallback();
      this.activeUtterance = null;
    };

    utterance.onerror = (e) => {
      console.warn("Speech error:", e);
      this.hideSubtitles();
      if (onEndCallback) onEndCallback();
    };

    this.synth.speak(utterance);
  },

  stop() {
    if (this.synth) {
      this.synth.cancel();
    }
    this.hideSubtitles();
  },

  toggleMute() {
    this.isMuted = !this.isMuted;
    if (this.isMuted) {
      this.stop();
    }
    return this.isMuted;
  },

  displaySubtitles(text) {
    const subtitleEl = document.getElementById("tryon-subtitles");
    const textEl = document.getElementById("tryon-subtitle-text");
    if (subtitleEl && textEl) {
      textEl.textContent = text;
      subtitleEl.style.display = "block";
    }
  },

  hideSubtitles() {
    const subtitleEl = document.getElementById("tryon-subtitles");
    if (subtitleEl) {
      subtitleEl.style.display = "none";
    }
  },
};

voice.init();
