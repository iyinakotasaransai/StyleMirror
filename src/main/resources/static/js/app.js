/**
 * StyleMirror - Main Application Bootstrap & Common Nav Shell
 */

import { auth } from "./auth.js";
import { cart } from "./cart.js";

export function initNavShell() {
  const headerContainer = document.getElementById("site-header-container");
  const footerContainer = document.getElementById("site-footer-container");

  const user = auth.getUser();
  const isLoggedIn = auth.isLoggedIn();
  const isSeller = auth.hasRole("SELLER");
  const isAdmin = auth.hasRole("ADMIN");

  if (headerContainer) {
    headerContainer.innerHTML = `
      <header class="site-header">
        <div class="container header-inner">
          <a href="/index.html" class="brand-logo">
            <div class="brand-icon">S</div>
            <span>STYLE<span class="gold-text">MIRROR</span></span>
          </a>

          <nav class="main-nav">
            <a href="/index.html" class="nav-link">Home</a>
            <a href="/pages/products.html" class="nav-link">Atelier Shop</a>
            <a href="/pages/try-on.html?productId=1" class="nav-link" style="color: var(--accent-cyan); font-weight: 700;">
              AR Try-On
            </a>

            ${isSeller ? `
              <a href="/pages/seller/dashboard.html" class="nav-link">Seller Studio</a>
            ` : ""}

            ${isAdmin ? `
              <a href="/pages/admin/dashboard.html" class="nav-link">Admin Portal</a>
            ` : ""}
          </nav>

          <div class="header-actions">
            ${isLoggedIn ? `
              <a href="/pages/wishlist.html" class="icon-btn" title="Wishlist">
                ♥
              </a>
              <a href="/pages/cart.html" class="icon-btn" title="Shopping Cart">
                🛒
                <span id="cart-badge" class="badge-count" style="display: none;">0</span>
              </a>
              <a href="/pages/profile.html" class="btn btn-secondary btn-sm" title="My Account">
                ${user.fullName ? user.fullName.split(" ")[0] : "Account"}
              </a>
              <button onclick="window.smLogout()" class="btn btn-outline btn-sm">
                Logout
              </button>
            ` : `
              <a href="/pages/login.html" class="btn btn-outline btn-sm">Sign In</a>
              <a href="/pages/register.html" class="btn btn-primary btn-sm">Join</a>
            `}
          </div>
        </div>
      </header>
    `;
  }

  if (footerContainer) {
    footerContainer.innerHTML = `
      <footer class="site-footer">
        <div class="container">
          <div class="footer-grid">
            <div>
              <div class="brand-logo" style="margin-bottom: 14px;">
                <div class="brand-icon">S</div>
                <span>STYLE<span class="gold-text">MIRROR</span></span>
              </div>
              <p style="color: var(--text-muted); font-size: 0.92rem; max-width: 320px;">
                Next-generation luxury digital fashion portal powered by real-time MediaPipe body landmark try-on and Google Gemini AI fashion styling.
              </p>
            </div>
            <div>
              <h4 style="margin-bottom: 14px; font-size: 0.95rem;">Explore</h4>
              <ul style="list-style: none; display: flex; flex-direction: column; gap: 8px; color: var(--text-muted); font-size: 0.9rem;">
                <li><a href="/pages/products.html">Haute Couture Collection</a></li>
                <li><a href="/pages/try-on.html?productId=1">Virtual Try-On Studio</a></li>
                <li><a href="/pages/products.html?categoryId=3">Outerwear & Jackets</a></li>
                <li><a href="/pages/products.html?categoryId=5">Streetwear & Casual</a></li>
              </ul>
            </div>
            <div>
              <h4 style="margin-bottom: 14px; font-size: 0.95rem;">Portals</h4>
              <ul style="list-style: none; display: flex; flex-direction: column; gap: 8px; color: var(--text-muted); font-size: 0.9rem;">
                <li><a href="/pages/seller/dashboard.html">Boutique Seller Hub</a></li>
                <li><a href="/pages/admin/dashboard.html">System Administrator</a></li>
                <li><a href="/pages/orders.html">Track Order History</a></li>
                <li><a href="/pages/wishlist.html">Curated Wishlist</a></li>
              </ul>
            </div>
            <div>
              <h4 style="margin-bottom: 14px; font-size: 0.95rem;">Innovation</h4>
              <p style="color: var(--text-muted); font-size: 0.88rem; line-height: 1.6;">
                Spring Boot 3.x &bull; MySQL 8 &bull; MediaPipe Pose &bull; Three.js 3D &bull; Google Gemini AI &bull; Web Speech API
              </p>
            </div>
          </div>
          <div class="footer-bottom">
            <span>&copy; ${new Date().getFullYear()} StyleMirror Portal. Final Year Project.</span>
            <span>All rights reserved. Designed for Production Deployment.</span>
          </div>
        </div>
      </footer>
    `;
  }

  // Load live cart count if logged in
  if (isLoggedIn) {
    cart.getCart();
  }
}

window.smLogout = function () {
  auth.logout();
};

document.addEventListener("DOMContentLoaded", () => {
  initNavShell();
});
