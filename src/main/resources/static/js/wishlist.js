/**
 * StyleMirror - Wishlist Client Manager
 */

import { api } from "./api.js";
import { utils } from "./utils.js";
import { auth } from "./auth.js";

export const wishlist = {
  async getWishlist() {
    if (!auth.isLoggedIn()) return null;
    try {
      const res = await api.get("/wishlist");
      return res.data;
    } catch (err) {
      console.error("Failed to load wishlist:", err);
      return null;
    }
  },

  async add(productId) {
    if (!auth.isLoggedIn()) {
      utils.showToast("Please log in to save items to your wishlist", "warning");
      setTimeout(() => { window.location.href = "/pages/login.html"; }, 1500);
      return;
    }
    try {
      const res = await api.post(`/wishlist/${productId}`);
      utils.showToast("Saved to wishlist", "success");
      return res.data;
    } catch (err) {
      utils.showToast(err.message, "error");
      throw err;
    }
  },

  async remove(productId) {
    try {
      const res = await api.delete(`/wishlist/${productId}`);
      utils.showToast("Removed from wishlist", "info");
      return res.data;
    } catch (err) {
      utils.showToast(err.message, "error");
      throw err;
    }
  },

  async check(productId) {
    if (!auth.isLoggedIn()) return false;
    try {
      const res = await api.get(`/wishlist/check/${productId}`);
      return res.data && res.data.inWishlist;
    } catch {
      return false;
    }
  },
};

window.toggleWishlist = async function (productId, event) {
  if (event) event.stopPropagation();
  try {
    const isSaved = await wishlist.check(productId);
    if (isSaved) {
      await wishlist.remove(productId);
      if (event && event.currentTarget) event.currentTarget.classList.remove("active");
    } else {
      await wishlist.add(productId);
      if (event && event.currentTarget) event.currentTarget.classList.add("active");
    }
  } catch (err) {
    console.error("Toggle wishlist error:", err);
  }
};
