/**
 * StyleMirror - Seller Dashboard & Product Management Client
 */

import { api } from "./api.js";
import { utils } from "./utils.js";
import { auth } from "./auth.js";

export const seller = {
  async getDashboard() {
    try {
      const res = await api.get("/seller/dashboard");
      return res.data;
    } catch (err) {
      console.error("Failed to load seller dashboard:", err);
      return null;
    }
  },

  async getProducts() {
    try {
      const res = await api.get("/seller/products");
      return res.data || [];
    } catch (err) {
      console.error("Failed to load seller products:", err);
      return [];
    }
  },

  async getOrders() {
    try {
      const res = await api.get("/seller/orders");
      return res.data || [];
    } catch (err) {
      console.error("Failed to load seller orders:", err);
      return [];
    }
  },

  async saveProduct(productData, isUpdate = false, productId = null) {
    try {
      let res;
      if (isUpdate && productId) {
        res = await api.put(`/products/${productId}`, productData);
        utils.showToast("Product updated successfully", "success");
      } else {
        res = await api.post("/products", productData);
        utils.showToast("Product published successfully", "success");
      }
      return res.data;
    } catch (err) {
      utils.showToast(err.message, "error");
      throw err;
    }
  },

  async deleteProduct(productId) {
    if (!confirm("Are you sure you want to deactivate this product?")) return;
    try {
      await api.delete(`/products/${productId}`);
      utils.showToast("Product deactivated", "info");
      window.location.reload();
    } catch (err) {
      utils.showToast(err.message, "error");
    }
  },

  async uploadImage(file, type = "product") {
    const formData = new FormData();
    formData.append("file", file);

    const endpoint = type === "overlay"
      ? "/uploads/tryon-overlay"
      : (type === "model" ? "/uploads/avatar" : "/uploads/product-image");

    const res = await api.post(endpoint, formData);
    return res.data ? res.data.url : null;
  },
  async getProductById(productId) {
    try {
      const res = await api.get(`/products/${productId}`);
      return res.data;
    } catch (err) {
      console.error("Failed to load product:", err);
      return null;
    }
  },

  async getCategories() {
    try {
      const res = await api.get("/categories");
      return res.data || [];
    } catch (err) {
      console.error("Failed to load categories:", err);
      return [];
    }
  },

  async updateOrderStatus(orderId, status) {
    try {
      const res = await api.put(`/orders/${orderId}/status`, { status });
      utils.showToast("Order status updated successfully", "success");
      return res.data;
    } catch (err) {
      utils.showToast(err.message, "error");
      throw err;
    }
  },
};
