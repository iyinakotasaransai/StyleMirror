/**
 * StyleMirror - Administrator Management & Moderation Client
 */

import { api } from "./api.js";
import { utils } from "./utils.js";

export const admin = {
  async getDashboard() {
    try {
      const res = await api.get("/admin/dashboard");
      return res.data;
    } catch (err) {
      console.error("Failed to load admin dashboard:", err);
      return null;
    }
  },

  async getUsers() {
    try {
      const res = await api.get("/admin/users");
      return res.data || [];
    } catch (err) {
      console.error("Failed to load users:", err);
      return [];
    }
  },

  async toggleUser(userId, active) {
    try {
      const res = await api.put(`/admin/users/${userId}/toggle`, { active });
      utils.showToast("User account status updated", "success");
      return res.data;
    } catch (err) {
      utils.showToast(err.message, "error");
    }
  },

  async getSellers() {
    try {
      const res = await api.get("/admin/sellers");
      return res.data || [];
    } catch (err) {
      console.error("Failed to load sellers:", err);
      return [];
    }
  },

  async approveSeller(sellerId) {
    try {
      await api.put(`/admin/sellers/${sellerId}/approve`, {});
      utils.showToast("Seller store approved successfully", "success");
      window.location.reload();
    } catch (err) {
      utils.showToast(err.message, "error");
    }
  },

  async blockSeller(sellerId) {
    try {
      await api.put(`/admin/sellers/${sellerId}/block`, {});
      utils.showToast("Seller store blocked", "warning");
      window.location.reload();
    } catch (err) {
      utils.showToast(err.message, "error");
    }
  },

  async getProducts() {
    try {
      const res = await api.get("/admin/products");
      return res.data || [];
    } catch (err) {
      console.error("Failed to load platform products:", err);
      return [];
    }
  },

  async deleteProduct(productId) {
    if (!confirm("Are you sure you want to delete/deactivate this product?")) return;
    try {
      await api.delete(`/products/${productId}`);
      utils.showToast("Product deactivated", "info");
      window.location.reload();
    } catch (err) {
      utils.showToast(err.message, "error");
    }
  },

  async getOrders() {
    try {
      const res = await api.get("/admin/orders");
      return res.data || [];
    } catch (err) {
      console.error("Failed to load platform orders:", err);
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

  async getCategories() {
    try {
      const res = await api.get("/categories");
      return res.data || [];
    } catch (err) {
      console.error("Failed to load categories:", err);
      return [];
    }
  },

  async createCategory(categoryData) {
    try {
      const res = await api.post("/categories", categoryData);
      utils.showToast("Category created successfully", "success");
      return res.data;
    } catch (err) {
      utils.showToast(err.message, "error");
      throw err;
    }
  },

  async updateCategory(categoryId, categoryData) {
    try {
      const res = await api.put(`/categories/${categoryId}`, categoryData);
      utils.showToast("Category updated successfully", "success");
      return res.data;
    } catch (err) {
      utils.showToast(err.message, "error");
      throw err;
    }
  },

  async deleteCategory(categoryId) {
    if (!confirm("Are you sure you want to deactivate this category?")) return;
    try {
      await api.delete(`/categories/${categoryId}`);
      utils.showToast("Category deactivated", "info");
      window.location.reload();
    } catch (err) {
      utils.showToast(err.message, "error");
    }
  },

  async getReports() {
    try {
      const res = await api.get("/reports");
      return res.data;
    } catch (err) {
      console.error("Failed to load system reports:", err);
      return null;
    }
  },
};
