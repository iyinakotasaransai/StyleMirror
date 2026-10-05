/**
 * StyleMirror - AI Styling Recommendation Client Pipeline
 * Interacts with backend Gemini integration and coordinates voice & 3D avatar.
 */

import { api } from "./api.js";
import { utils } from "./utils.js";
import { voice } from "./voice.js";

export const ai = {
  currentRequestId: null,
  isRequestPending: false,

  async requestRecommendation(productId, variantContext, avatarManager = null) {
    if (this.isRequestPending) {
      console.log("Cancelling prior pending AI request...");
    }

    const requestId = "REQ-" + Date.now() + "-" + Math.random().toString(36).substring(2, 7);
    this.currentRequestId = requestId;
    this.isRequestPending = true;

    // Trigger thinking animation on 3D avatar
    if (avatarManager) {
      avatarManager.setAnimation("thinking");
    }

    const statusBadge = document.getElementById("ai-status-indicator");
    if (statusBadge) statusBadge.textContent = "AI Stylist Analyzing...";

    try {
      const payload = {
        requestId,
        productId,
        variantId: variantContext.variantId || null,
        size: variantContext.size || "M",
        color: variantContext.color || "Onyx Black",
        occasion: variantContext.occasion || "Evening Casual",
        userContext: "Live camera virtual try-on session",
      };

      const res = await api.post("/ai/recommendation", payload);

      // Stale response rejection: ensure this matches the latest request
      if (this.currentRequestId !== requestId) {
        console.warn("Discarding stale AI response for request ID:", requestId);
        return null;
      }

      this.isRequestPending = false;
      const data = res.data;

      // Update UI components
      const recTextEl = document.getElementById("ai-recommendation-text");
      const reasonEl = document.getElementById("ai-reason-text");
      const moodEl = document.getElementById("ai-mood-badge");

      if (recTextEl) recTextEl.textContent = data.recommendation;
      if (reasonEl) reasonEl.textContent = `Rationale: ${data.reason}`;
      if (moodEl) moodEl.textContent = `Mood: ${data.mood}`;
      if (statusBadge) statusBadge.textContent = "Stylist Ready";

      // Trigger 3D Avatar Animation
      if (avatarManager) {
        avatarManager.setAnimation(data.animation || "thumbsUp");
      }

      // Voice recommendation & synchronized subtitles
      const spokenText = data.spokenText || data.recommendation;
      voice.speak(
        spokenText,
        () => {
          if (avatarManager) avatarManager.startSpeaking();
        },
        () => {
          if (avatarManager) avatarManager.stopSpeaking();
        }
      );

      return data;
    } catch (err) {
      this.isRequestPending = false;
      console.error("AI Recommendation error:", err);
      if (statusBadge) statusBadge.textContent = "Stylist Idle";
      if (avatarManager) avatarManager.setAnimation("idle");
      utils.showToast("Stylist note: " + err.message, "info");
      return null;
    }
  },
};
