/**
 * StyleMirror - Product Catalog & Details Management
 */

import { api } from "./api.js";
import { utils } from "./utils.js";
import { cart } from "./cart.js";
import { wishlist } from "./wishlist.js";

export const products = {
  async getProducts(categoryId = null, search = null) {
    let endpoint = "/products";
    const params = [];
    if (categoryId) params.push(`categoryId=${categoryId}`);
    if (search) params.push(`search=${encodeURIComponent(search)}`);
    if (params.length > 0) endpoint += `?${params.join("&")}`;

    const res = await api.get(endpoint);
    return res.data || [];
  },

  async getProductById(id) {
    const res = await api.get(`/products/${id}`);
    return res.data;
  },

  async getCategories() {
    const res = await api.get("/categories");
    return res.data || [];
  },

  renderProductCard(product) {
    const hasTryOn = product.tryOnAsset && product.tryOnAsset.enabled;
    const imgUrl = (product.imageUrls && product.imageUrls.length > 0)
      ? product.imageUrls[0]
      : "/assets/products/placeholder.svg";

    return `
      <div class="product-card" data-product-id="${product.id}">
        <div class="product-thumb-container">
          <a href="/pages/product-details.html?id=${product.id}">
            <img src="${imgUrl}" alt="${product.name}" class="product-thumb" loading="lazy" />
          </a>
          ${hasTryOn ? `<span class="product-badge-tryon">AR TRY-ON</span>` : ""}
          <button class="product-wishlist-btn" onclick="window.toggleWishlist(${product.id}, event)" title="Save to Wishlist">
            ♥
          </button>
        </div>
        <div class="product-info">
          <span class="product-brand">${product.brand || "MIRROR"}</span>
          <a href="/pages/product-details.html?id=${product.id}">
            <h3 class="product-title">${product.name}</h3>
          </a>
          <div class="product-price-row">
            <span class="product-price">${utils.formatCurrency(product.basePrice)}</span>
            <span style="font-size: 0.8rem; color: var(--text-muted);">${product.categoryName || ""}</span>
          </div>
          <div class="product-actions-bar">
            <a href="/pages/product-details.html?id=${product.id}" class="btn btn-secondary btn-sm" style="flex: 1;">
              View Details
            </a>
            ${hasTryOn ? `
              <a href="/pages/try-on.html?productId=${product.id}" class="btn btn-cyber btn-sm" title="Try On in Live Camera">
                Try On
              </a>
            ` : ""}
          </div>
        </div>
      </div>
    `;
  },
};
