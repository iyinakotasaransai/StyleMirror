/**
 * StyleMirror - Authentication & Authorization State Manager
 */

import { api } from "./api.js";

export const auth = {
  getUser() {
    const raw = localStorage.getItem("sm_user");
    return raw ? JSON.parse(raw) : null;
  },

  setUser(user) {
    if (user) {
      localStorage.setItem("sm_user", JSON.stringify(user));
    } else {
      localStorage.removeItem("sm_user");
    }
  },

  isLoggedIn() {
    return !!api.getToken() && !!this.getUser();
  },

  hasRole(role) {
    const user = this.getUser();
    if (!user || !user.roles) return false;
    const cleanRole = role.startsWith("ROLE_") ? role : `ROLE_${role}`;
    return user.roles.includes(cleanRole);
  },

  async login(email, password) {
    const res = await api.post("/auth/login", { email, password });
    if (res.success && res.data) {
      api.setToken(res.data.token);
      this.setUser(res.data);
      return res.data;
    }
    throw new Error(res.message || "Login failed");
  },

  async register(registerData) {
    const res = await api.post("/auth/register", registerData);
    if (res.success && res.data) {
      api.setToken(res.data.token);
      this.setUser(res.data);
      return res.data;
    }
    throw new Error(res.message || "Registration failed");
  },

  logout() {
    api.setToken(null);
    this.setUser(null);
    window.location.href = "/pages/login.html";
  },

  protectPage(requiredRole = null) {
    if (!this.isLoggedIn()) {
      window.location.href = "/pages/login.html";
      return false;
    }

    if (requiredRole && !this.hasRole(requiredRole)) {
      alert("Access Denied: You do not possess the required permissions for this area.");
      window.location.href = "/pages/home.html";
      return false;
    }

    return true;
  },
};
