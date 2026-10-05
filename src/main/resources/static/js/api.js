/**
 * StyleMirror - Centralized API Service Layer
 * All frontend requests route strictly through this module.
 */

const API_BASE_URL = "/api";

export const api = {
  getToken() {
    return localStorage.getItem("sm_token");
  },

  setToken(token) {
    if (token) {
      localStorage.setItem("sm_token", token);
    } else {
      localStorage.removeItem("sm_token");
    }
  },

  async request(endpoint, options = {}) {
    const url = endpoint.startsWith("http") ? endpoint : `${API_BASE_URL}${endpoint}`;
    const headers = {
      ...options.headers,
    };

    const token = this.getToken();
    if (token) {
      headers["Authorization"] = `Bearer ${token}`;
    }

    if (!(options.body instanceof FormData) && !headers["Content-Type"]) {
      headers["Content-Type"] = "application/json";
    }

    try {
      const response = await fetch(url, {
        ...options,
        headers,
      });

      const contentType = response.headers.get("content-type");
      let data = null;
      if (contentType && contentType.includes("application/json")) {
        data = await response.json();
      } else {
        data = await response.text();
      }

      if (!response.ok) {
        if (response.status === 401) {
          // Token expired or unauthenticated
          this.setToken(null);
          localStorage.removeItem("sm_user");
          if (!window.location.pathname.includes("/pages/login.html") && !window.location.pathname.endsWith("index.html") && window.location.pathname !== "/") {
            window.location.href = "/pages/login.html";
          }
        }
        const errorMsg = data && data.message ? data.message : `Request failed with status ${response.status}`;
        throw new Error(errorMsg);
      }

      return data;
    } catch (err) {
      console.error(`API Error on [${options.method || "GET"} ${endpoint}]:`, err);
      throw err;
    }
  },

  get(endpoint) {
    return this.request(endpoint, { method: "GET" });
  },

  post(endpoint, body) {
    const isFormData = body instanceof FormData;
    return this.request(endpoint, {
      method: "POST",
      body: isFormData ? body : JSON.stringify(body),
    });
  },

  put(endpoint, body) {
    const isFormData = body instanceof FormData;
    return this.request(endpoint, {
      method: "PUT",
      body: isFormData ? body : JSON.stringify(body),
    });
  },

  delete(endpoint) {
    return this.request(endpoint, { method: "DELETE" });
  },
};
