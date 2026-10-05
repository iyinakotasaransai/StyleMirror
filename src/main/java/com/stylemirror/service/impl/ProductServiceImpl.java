package com.stylemirror.service.impl;

import com.stylemirror.dto.ProductCreateDto;
import com.stylemirror.dto.ProductDto;
import com.stylemirror.dto.ProductVariantDto;
import com.stylemirror.dto.TryOnAssetDto;
import com.stylemirror.entity.*;
import com.stylemirror.enums.RoleType;
import com.stylemirror.exception.BadRequestException;
import com.stylemirror.exception.ForbiddenException;
import com.stylemirror.exception.ResourceNotFoundException;
import com.stylemirror.repository.*;
import com.stylemirror.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final TryOnAssetRepository tryOnAssetRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        return mapToDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepository.searchProducts(query.trim()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> searchProducts(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return getAllProducts(pageable);
        }
        return productRepository.searchProducts(query.trim(), pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryIdAndActiveTrue(categoryId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductCreateDto dto, Long userId) {
        Seller seller = getSellerForUser(userId);
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + dto.getCategoryId()));

        Product product = Product.builder()
                .seller(seller)
                .category(category)
                .name(dto.getName().trim())
                .description(dto.getDescription())
                .brand(dto.getBrand().trim())
                .basePrice(dto.getBasePrice())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();

        Product savedProduct = productRepository.save(product);

        // Add variants if provided
        if (dto.getVariants() != null && !dto.getVariants().isEmpty()) {
            List<ProductVariant> variants = new ArrayList<>();
            for (ProductVariantDto vDto : dto.getVariants()) {
                variants.add(ProductVariant.builder()
                        .product(savedProduct)
                        .size(vDto.getSize().trim())
                        .color(vDto.getColor().trim())
                        .price(vDto.getPrice() != null ? vDto.getPrice() : dto.getBasePrice())
                        .stock(vDto.getStock() != null ? vDto.getStock() : 0)
                        .sku(vDto.getSku().trim())
                        .active(vDto.getActive() != null ? vDto.getActive() : true)
                        .build());
            }
            productVariantRepository.saveAll(variants);
            savedProduct.setVariants(variants);
        }

        // Add images if provided
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            List<ProductImage> images = new ArrayList<>();
            for (int i = 0; i < dto.getImageUrls().size(); i++) {
                images.add(ProductImage.builder()
                        .product(savedProduct)
                        .imageUrl(dto.getImageUrls().get(i))
                        .altText(savedProduct.getName() + " image " + (i + 1))
                        .sortOrder(i)
                        .build());
            }
            productImageRepository.saveAll(images);
            savedProduct.setImages(images);
        }

        // Add try-on asset if provided
        if (dto.getTryOnAsset() != null) {
            TryOnAsset asset = TryOnAsset.builder()
                    .product(savedProduct)
                    .overlayImagePath(dto.getTryOnAsset().getOverlayImagePath())
                    .glbPath(dto.getTryOnAsset().getGlbPath())
                    .enabled(dto.getTryOnAsset().getEnabled() != null ? dto.getTryOnAsset().getEnabled() : true)
                    .build();
            tryOnAssetRepository.save(asset);
            savedProduct.setTryOnAsset(asset);
        }

        return mapToDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Long id, ProductCreateDto dto, Long userId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        verifyProductOwnership(product, userId);

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + dto.getCategoryId()));

        product.setCategory(category);
        product.setName(dto.getName().trim());
        product.setDescription(dto.getDescription());
        product.setBrand(dto.getBrand().trim());
        product.setBasePrice(dto.getBasePrice());
        if (dto.getActive() != null) {
            product.setActive(dto.getActive());
        }

        return mapToDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id, Long userId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        verifyProductOwnership(product, userId);

        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantDto> getProductVariants(Long productId) {
        return productVariantRepository.findByProductIdAndActiveTrue(productId).stream()
                .map(this::mapVariantToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductVariantDto addProductVariant(Long productId, ProductVariantDto dto, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        verifyProductOwnership(product, userId);

        if (productVariantRepository.existsBySku(dto.getSku().trim())) {
            throw new BadRequestException("SKU already exists: " + dto.getSku());
        }

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .size(dto.getSize().trim())
                .color(dto.getColor().trim())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .sku(dto.getSku().trim())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();

        return mapVariantToDto(productVariantRepository.save(variant));
    }

    @Override
    @Transactional
    public ProductVariantDto updateProductVariant(Long variantId, ProductVariantDto dto, Long userId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with ID: " + variantId));

        verifyProductOwnership(variant.getProduct(), userId);

        variant.setSize(dto.getSize().trim());
        variant.setColor(dto.getColor().trim());
        variant.setPrice(dto.getPrice());
        variant.setStock(dto.getStock());
        if (dto.getActive() != null) {
            variant.setActive(dto.getActive());
        }

        return mapVariantToDto(productVariantRepository.save(variant));
    }

    @Override
    @Transactional
    public void deleteProductVariant(Long variantId, Long userId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with ID: " + variantId));

        verifyProductOwnership(variant.getProduct(), userId);

        variant.setActive(false);
        productVariantRepository.save(variant);
    }

    private Seller getSellerForUser(Long userId) {
        return sellerRepository.findByUserId(userId)
                .orElseThrow(() -> new ForbiddenException("Current user does not have an active seller profile"));
    }

    private void verifyProductOwnership(Product product, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName() == RoleType.ROLE_ADMIN);

        if (isAdmin) {
            return; // Admins can manage all products
        }

        Seller seller = sellerRepository.findByUserId(userId)
                .orElseThrow(() -> new ForbiddenException("You are not registered as a seller"));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new ForbiddenException("You do not own this product");
        }
    }

    public ProductDto mapToDto(Product entity) {
        List<ProductVariantDto> variantDtos = entity.getVariants() != null
                ? entity.getVariants().stream().filter(v -> Boolean.TRUE.equals(v.getActive())).map(this::mapVariantToDto).collect(Collectors.toList())
                : new ArrayList<>();

        List<String> images = entity.getImages() != null
                ? entity.getImages().stream().map(ProductImage::getImageUrl).collect(Collectors.toList())
                : new ArrayList<>();

        TryOnAssetDto assetDto = null;
        if (entity.getTryOnAsset() != null) {
            assetDto = TryOnAssetDto.builder()
                    .id(entity.getTryOnAsset().getId())
                    .productId(entity.getId())
                    .overlayImagePath(entity.getTryOnAsset().getOverlayImagePath())
                    .glbPath(entity.getTryOnAsset().getGlbPath())
                    .enabled(entity.getTryOnAsset().getEnabled())
                    .build();
        }

        return ProductDto.builder()
                .id(entity.getId())
                .sellerId(entity.getSeller() != null ? entity.getSeller().getId() : null)
                .sellerStoreName(entity.getSeller() != null ? entity.getSeller().getStoreName() : null)
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : null)
                .name(entity.getName())
                .description(entity.getDescription())
                .brand(entity.getBrand())
                .basePrice(entity.getBasePrice())
                .active(entity.getActive())
                .variants(variantDtos)
                .imageUrls(images)
                .tryOnAsset(assetDto)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private ProductVariantDto mapVariantToDto(ProductVariant variant) {
        return ProductVariantDto.builder()
                .id(variant.getId())
                .productId(variant.getProduct().getId())
                .size(variant.getSize())
                .color(variant.getColor())
                .price(variant.getPrice())
                .stock(variant.getStock())
                .sku(variant.getSku())
                .active(variant.getActive())
                .build();
    }
}
