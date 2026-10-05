/**
 * StyleMirror - Order & Checkout Client Management
 */

import { api } from "./api.js";
import { utils } from "./utils.js";
import { cart } from "./cart.js";

export const orders = {
  async checkout(shippingAddress, contactPhone, paymentMethod = "SIMULATED_CREDIT_CARD") {
    try {
      const res = await api.post("/orders", {
        shippingAddress,
        contactPhone,
        paymentMethod,
      });

      if (res.success && res.data) {
        cart.updateCartBadge(0);
        utils.showToast("Order placed successfully! Transaction verified.", "success");
        return res.data;
      }
      throw new Error(res.message || "Failed to place order");
    } catch (err) {
      utils.showToast(err.message, "error");
      throw err;
    }
  },

  async getUserOrders() {
    try {
      const res = await api.get("/orders");
      return res.data || [];
    } catch (err) {
      console.error("Failed to load user orders:", err);
      return [];
    }
  },

  async getOrderById(id) {
    const res = await api.get(`/orders/${id}`);
    return res.data;
  },

  async updateOrderStatus(id, status) {
    const res = await api.put(`/orders/${id}/status`, { status });
    utils.showToast("Order status updated", "success");
    return res.data;
  },
};
