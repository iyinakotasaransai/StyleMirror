package com.stylemirror.config;

import com.stylemirror.entity.*;
import com.stylemirror.enums.ApprovalStatus;
import com.stylemirror.enums.RoleType;
import com.stylemirror.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final CategoryRepository categoryRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting StyleMirror Data Initializer...");

        // 1. Initialize Roles
        Role buyerRole = getOrCreateRole(RoleType.ROLE_BUYER);
        Role sellerRole = getOrCreateRole(RoleType.ROLE_SELLER);
        Role adminRole = getOrCreateRole(RoleType.ROLE_ADMIN);

        // 2. Initialize / Synchronize Admin User
        initUser("admin@stylemirror.com", "Admin@123", "StyleMirror Administrator", "+1-555-0100", adminRole);

        // 3. Initialize / Synchronize Seller User
        User sellerUser = initUser("seller@stylemirror.com", "Seller@123", "StyleMirror Premier Boutique", "+1-555-0101", sellerRole);

        // Ensure Seller Entity exists
        if (sellerUser != null && sellerRepository.findByUserId(sellerUser.getId()).isEmpty()) {
            Seller seller = Seller.builder()
                    .user(sellerUser)
                    .storeName("StyleMirror Signature Store")
                    .storeDescription("Flagship digital atelier offering luxury ready-to-wear garments optimized for augmented reality and real-time virtual try-on.")
                    .businessAddress("742 Evergreen Fashion Blvd, New York, NY 10001")
                    .gstNumber("GST-US-9948210")
                    .approvalStatus(ApprovalStatus.APPROVED)
                    .build();
            sellerRepository.save(seller);
            log.info("Initialized Seller Profile for: {}", sellerUser.getEmail());
        }

        // 4. Initialize / Synchronize Buyer User
        User buyerUser = initUser("buyer@stylemirror.com", "Buyer@123", "Alex Rivera (Buyer)", "+1-555-0102", buyerRole);

        // Ensure Cart and Wishlist exist for buyer
        if (buyerUser != null) {
            if (cartRepository.findByUserId(buyerUser.getId()).isEmpty()) {
                cartRepository.save(Cart.builder().user(buyerUser).build());
            }
            if (wishlistRepository.findByUserId(buyerUser.getId()).isEmpty()) {
                wishlistRepository.save(Wishlist.builder().user(buyerUser).build());
            }
        }

        // 5. Initialize Categories if empty
        if (categoryRepository.count() == 0) {
            createCategory("Men's Fashion", "mens-fashion", "Contemporary tailored shirts, urban jackets, and versatile daily menswear.");
            createCategory("Women's Fashion", "womens-fashion", "Elegantly sculpted dresses, chic outerwear, and modern tops for every occasion.");
            createCategory("Outerwear & Jackets", "outerwear-jackets", "Performance bombers, structured blazers, leather coats, and winter layers.");
            createCategory("Formal & Business", "formal-business", "Precision-crafted blazers, formal shirts, and sharp boardroom ensembles.");
            createCategory("Streetwear & Casual", "streetwear-casual", "Heavyweight oversized tees, techwear hoodies, and relaxed streetwear cuts.");
            log.info("Initialized default categories.");
        }

        log.info("StyleMirror Data Initializer completed successfully.");
    }

    private Role getOrCreateRole(RoleType roleType) {
        return roleRepository.findByName(roleType)
                .orElseGet(() -> roleRepository.save(Role.builder().name(roleType).build()));
    }

    private User initUser(String email, String defaultPassword, String fullName, String phone, Role role) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Verify and synchronize standard demo password for reliable evaluation
            user.setPassword(passwordEncoder.encode(defaultPassword));
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                user.setRoles(new HashSet<>(Collections.singletonList(role)));
            }
            return userRepository.save(user);
        } else {
            User newUser = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(defaultPassword))
                    .fullName(fullName)
                    .phone(phone)
                    .active(true)
                    .roles(new HashSet<>(Collections.singletonList(role)))
                    .build();
            User saved = userRepository.save(newUser);
            log.info("Created demo user: {} with password: {}", email, defaultPassword);
            return saved;
        }
    }

    private void createCategory(String name, String slug, String description) {
        categoryRepository.save(Category.builder()
                .name(name)
                .slug(slug)
                .description(description)
                .active(true)
                .build());
    }
}
