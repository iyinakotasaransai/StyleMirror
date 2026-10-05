/**
 * StyleMirror - Shopping Cart Client Manager
 */

import { api } from "./api.js";
import { utils } from "./utils.js";
import { auth } from "./auth.js";

export const cart = {
  async getCart() {
    if (!auth.isLoggedIn()) return null;
    try {
      const res = await api.get("/cart");
      this.updateCartBadge(res.data ? res.data.totalItems : 0);
      return res.data;
    } catch (err) {
      console.error("Failed to load cart:", err);
      return null;
    }
  },

  async addToCart(variantId, quantity = 1) {
    if (!auth.isLoggedIn()) {
      utils.showToast("Please log in to add items to your cart", "warning");
      setTimeout(() => { window.location.href = "/pages/login.html"; }, 1500);
      return;
    }

    try {
      const res = await api.post("/cart/items", { variantId, quantity });
      utils.showToast("Item added to cart", "success");
      this.updateCartBadge(res.data ? res.data.totalItems : 0);
      return res.data;
    } catch (err) {
      utils.showToast(err.message, "error");
      throw err;
    }
  },

  async updateQuantity(itemId, quantity) {
    try {
      const res = await api.put(`/cart/items/${itemId}`, { quantity });
      this.updateCartBadge(res.data ? res.data.totalItems : 0);
      return res.data;
    } catch (err) {
      utils.showToast(err.message, "error");
      throw err;
    }
  },

  async removeItem(itemId) {
    try {
      const res = await api.delete(`/cart/items/${itemId}`);
      utils.showToast("Item removed from cart", "info");
      this.updateCartBadge(res.data ? res.data.totalItems : 0);
      return res.data;
    } catch (err) {
      utils.showToast(err.message, "error");
      throw err;
    }
  },

  async clearCart() {
    try {
      await api.delete("/cart");
      this.updateCartBadge(0);
    } catch (err) {
      console.error("Failed to clear cart:", err);
    }
  },

  updateCartBadge(count = 0) {
    const badge = document.getElementById("cart-badge");
    if (badge) {
      badge.textContent = count;
      badge.style.display = count > 0 ? "flex" : "none";
    }
  },
};
