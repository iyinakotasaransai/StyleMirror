-- Active: 1790744512452@@127.0.0.1@3306@stylemirror
-- ==============================================================
-- StyleMirror: Seed Data (MySQL 8.x)
-- ==============================================================

USE `stylemirror`;

-- Insert Roles if not present
INSERT IGNORE INTO `roles` (`id`, `name`) VALUES 
(1, 'ROLE_BUYER'),
(2, 'ROLE_SELLER'),
(3, 'ROLE_ADMIN');

-- Note: Passwords will be automatically synchronized on application startup by DataInitializer:
-- Admin: admin@stylemirror.com / Admin@123
-- Seller: seller@stylemirror.com / Seller@123
-- Buyer: buyer@stylemirror.com / Buyer@123
INSERT IGNORE INTO `users` (`id`, `email`, `password`, `full_name`, `phone`, `active`, `created_at`, `updated_at`) VALUES
(1, 'admin@stylemirror.com', '$2a$10$8SWHuPbudh0.210yjc2bau.yiCXHtcB2eSFIJTulQRnPKoT4aMHNu', 'StyleMirror Administrator', '+1-555-0100', 1, NOW(), NOW()),
(2, 'seller@stylemirror.com', '$2a$10$YkL7lWBgnlz4mBfNbP4kCeZAXqBI0PiF7FWmqLar3Yy32tZB.DyKS', 'StyleMirror Premier Boutique', '+1-555-0101', 1, NOW(), NOW()),
(3, 'buyer@stylemirror.com', '$2a$10$ko.D1f6kIDojMNtncvkobufNVYHz4LRAhx/vRvc3TTnEQgD7odab6', 'Alex Rivera (Buyer)', '+1-555-0102', 1, NOW(), NOW());

INSERT IGNORE INTO `user_roles` (`user_id`, `role_id`) VALUES
(1, 3), -- Admin -> ROLE_ADMIN
(2, 2), -- Seller -> ROLE_SELLER
(3, 1); -- Buyer -> ROLE_BUYER

INSERT IGNORE INTO `sellers` (`id`, `user_id`, `store_name`, `store_description`, `business_address`, `gst_number`, `approval_status`, `created_at`, `updated_at`) VALUES
(1, 2, 'StyleMirror Signature Store', 'Flagship digital atelier offering luxury ready-to-wear garments optimized for augmented reality and real-time virtual try-on.', '742 Evergreen Fashion Blvd, New York, NY 10001', 'GST-US-9948210', 'APPROVED', NOW(), NOW());

INSERT IGNORE INTO `categories` (`id`, `name`, `slug`, `description`, `active`) VALUES
(1, 'Men\'s Fashion', 'mens-fashion', 'Contemporary tailored shirts, urban jackets, and versatile daily menswear.', 1),
(2, 'Women\'s Fashion', 'womens-fashion', 'Elegantly sculpted dresses, chic outerwear, and modern tops for every occasion.', 1),
(3, 'Outerwear & Jackets', 'outerwear-jackets', 'Performance bombers, structured blazers, leather coats, and winter layers.', 1),
(4, 'Formal & Business', 'formal-business', 'Precision-crafted blazers, formal shirts, and sharp boardroom ensembles.', 1),
(5, 'Streetwear & Casual', 'streetwear-casual', 'Heavyweight oversized tees, techwear hoodies, and relaxed streetwear cuts.', 1);

INSERT IGNORE INTO `products` (`id`, `seller_id`, `category_id`, `name`, `description`, `brand`, `base_price`, `active`, `created_at`, `updated_at`) VALUES
(1, 1, 3, 'Cyberpunk Techwear Bomber Jacket', 'Water-resistant tactical bomber featuring magnetic zip closures, ergonomic articulated sleeves, and reflective trim accents.', 'NEO-AURA', 149.99, 1, NOW(), NOW()),
(2, 1, 1, 'Classic Tailored Oxford Shirt', 'Handcrafted from 100% Egyptian long-staple cotton with a crisp button-down collar and mother-of-pearl buttons.', 'MIRROR ATELIER', 69.99, 1, NOW(), NOW()),
(3, 1, 2, 'Midnight Velvet Cocktail Dress', 'Opulent silk-velvet wrap dress featuring a structured silhouette, deep V-neckline, and graceful asymmetrical hem.', 'LUMIÈRE PARIS', 139.99, 1, NOW(), NOW()),
(4, 1, 5, 'Heavyweight Streetwear Graphic Hoodie', '450 GSM French terry cotton hoodie with relaxed dropped shoulders, double-layered hood, and minimal embroidered branding.', 'STREETPULSE', 89.99, 1, NOW(), NOW()),
(5, 1, 4, 'Italian Wool Slim-Fit Blazer', 'Single-breasted two-button blazer crafted from Super 130s Italian virgin wool with natural canvas construction.', 'SARTORIA MILANO', 229.99, 1, NOW(), NOW()),
(6, 1, 1, 'Minimalist Linen Summer Tee', 'Breathable organic linen-cotton blend short-sleeve tee with raw edge detailing and relaxed modern drape.', 'AURA ESSENTIALS', 39.99, 1, NOW(), NOW()),
(7, 1, 2, 'Floral Silk Wrap Midi Dress', 'Pure mulberry silk dress adorned with botanical watercolor illustrations, adjustable waist tie, and flutter sleeves.', 'FLEUR & MODE', 124.99, 1, NOW(), NOW()),
(8, 1, 3, 'Vintage Washed Denim Trucker Jacket', '14 oz selvedge denim jacket finished with subtle distressed abrasions, copper shank buttons, and dual chest pockets.', 'RETROGRADE', 99.99, 1, NOW(), NOW()),
(9, 1, 4, 'Monochrome Executive Formal Shirt', 'Wrinkle-resistant pinpoint oxford dress shirt designed for sharp executive styling and all-day comfort.', 'MIRROR ATELIER', 74.99, 1, NOW(), NOW()),
(10, 1, 5, 'Acid Wash Acid-Black Boxy Tee', 'Heavy boxy fit streetwear t-shirt with custom acid-wash patina and ribbed crewneck collar.', 'STREETPULSE', 45.99, 1, NOW(), NOW());

INSERT IGNORE INTO `product_variants` (`id`, `product_id`, `size`, `color`, `price`, `stock`, `sku`, `active`) VALUES
(1, 1, 'M', 'Onyx Black', 149.99, 45, 'JKT-CYBER-BLK-M', 1),
(2, 1, 'L', 'Onyx Black', 149.99, 30, 'JKT-CYBER-BLK-L', 1),
(3, 1, 'M', 'Olive Green', 149.99, 20, 'JKT-CYBER-OLV-M', 1),
(4, 1, 'L', 'Olive Green', 149.99, 15, 'JKT-CYBER-OLV-L', 1),
(5, 2, 'S', 'Classic White', 69.99, 50, 'SHT-OXF-WHT-S', 1),
(6, 2, 'M', 'Classic White', 69.99, 65, 'SHT-OXF-WHT-M', 1),
(7, 2, 'L', 'Classic White', 69.99, 40, 'SHT-OXF-WHT-L', 1),
(8, 2, 'M', 'Sky Blue', 69.99, 35, 'SHT-OXF-BLU-M', 1),
(9, 3, 'S', 'Midnight Navy', 139.99, 25, 'DRS-VELV-NVY-S', 1),
(10, 3, 'M', 'Midnight Navy', 139.99, 30, 'DRS-VELV-NVY-M', 1),
(11, 3, 'M', 'Crimson Ruby', 139.99, 20, 'DRS-VELV-RED-M', 1),
(12, 4, 'M', 'Charcoal Heather', 89.99, 40, 'HD-STP-CHR-M', 1),
(13, 4, 'L', 'Charcoal Heather', 89.99, 50, 'HD-STP-CHR-L', 1),
(14, 4, 'XL', 'Charcoal Heather', 89.99, 25, 'HD-STP-CHR-XL', 1),
(15, 4, 'L', 'Bone White', 89.99, 30, 'HD-STP-BNE-L', 1),
(16, 5, '38R', 'Navy Charcoal', 229.99, 15, 'BLZ-ITL-NVY-38', 1),
(17, 5, '40R', 'Navy Charcoal', 229.99, 20, 'BLZ-ITL-NVY-40', 1),
(18, 5, '42R', 'Navy Charcoal', 229.99, 12, 'BLZ-ITL-NVY-42', 1),
(19, 6, 'M', 'Sand Beige', 39.99, 60, 'TEE-LIN-SND-M', 1),
(20, 6, 'L', 'Sand Beige', 39.99, 45, 'TEE-LIN-SND-L', 1),
(21, 7, 'S', 'Emerald Rose', 124.99, 18, 'DRS-FLR-EMR-S', 1),
(22, 7, 'M', 'Emerald Rose', 124.99, 22, 'DRS-FLR-EMR-M', 1),
(23, 8, 'M', 'Washed Indigo', 99.99, 30, 'JKT-DNM-IND-M', 1),
(24, 8, 'L', 'Washed Indigo', 99.99, 35, 'JKT-DNM-IND-L', 1),
(25, 9, 'M', 'French Blue', 74.99, 40, 'SHT-EXEC-BLU-M', 1),
(26, 9, 'L', 'French Blue', 74.99, 35, 'SHT-EXEC-BLU-L', 1),
(27, 10, 'M', 'Vintage Washed Black', 45.99, 50, 'TEE-ACD-BLK-M', 1),
(28, 10, 'L', 'Vintage Washed Black', 45.99, 45, 'TEE-ACD-BLK-L', 1);

INSERT IGNORE INTO `product_images` (`id`, `product_id`, `image_url`, `alt_text`, `sort_order`) VALUES
(1, 1, '/assets/products/bomber_jacket.svg', 'Cyberpunk Techwear Bomber Jacket front view', 1),
(2, 2, '/assets/products/oxford_shirt.svg', 'Classic Tailored Oxford Shirt front view', 1),
(3, 3, '/assets/products/velvet_dress.svg', 'Midnight Velvet Cocktail Dress front view', 1),
(4, 4, '/assets/products/graphic_hoodie.svg', 'Heavyweight Streetwear Graphic Hoodie front view', 1),
(5, 5, '/assets/products/slim_blazer.svg', 'Italian Wool Slim-Fit Blazer front view', 1),
(6, 6, '/assets/products/linen_tee.svg', 'Minimalist Linen Summer Tee front view', 1),
(7, 7, '/assets/products/silk_dress.svg', 'Floral Silk Wrap Midi Dress front view', 1),
(8, 8, '/assets/products/denim_jacket.svg', 'Vintage Washed Denim Trucker Jacket front view', 1),
(9, 9, '/assets/products/executive_shirt.svg', 'Monochrome Executive Formal Shirt front view', 1),
(10, 10, '/assets/products/acid_tee.svg', 'Acid Wash Acid-Black Boxy Tee front view', 1);

INSERT IGNORE INTO `tryon_assets` (`id`, `product_id`, `overlay_image_path`, `glb_path`, `enabled`, `created_at`, `updated_at`) VALUES
(1, 1, '/assets/overlays/bomber_jacket.png', '/models/jacket.glb', 1, NOW(), NOW()),
(2, 2, '/assets/overlays/oxford_shirt.png', '/models/shirt.glb', 1, NOW(), NOW()),
(3, 3, '/assets/overlays/velvet_dress.png', '/models/dress.glb', 1, NOW(), NOW()),
(4, 4, '/assets/overlays/graphic_hoodie.png', '/models/hoodie.glb', 1, NOW(), NOW()),
(5, 5, '/assets/overlays/slim_blazer.png', '/models/blazer.glb', 1, NOW(), NOW()),
(6, 6, '/assets/overlays/linen_tee.png', '/models/shirt.glb', 1, NOW(), NOW()),
(7, 7, '/assets/overlays/silk_dress.png', '/models/dress.glb', 1, NOW(), NOW()),
(8, 8, '/assets/overlays/denim_jacket.png', '/models/jacket.glb', 1, NOW(), NOW()),
(9, 9, '/assets/overlays/executive_shirt.png', '/models/shirt.glb', 1, NOW(), NOW()),
(10, 10, '/assets/overlays/acid_tee.png', '/models/shirt.glb', 1, NOW(), NOW());
