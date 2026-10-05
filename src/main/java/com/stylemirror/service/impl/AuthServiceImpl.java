package com.stylemirror.service.impl;

import com.stylemirror.dto.AuthRequest;
import com.stylemirror.dto.AuthResponse;
import com.stylemirror.dto.RegisterRequest;
import com.stylemirror.dto.UserDto;
import com.stylemirror.entity.Cart;
import com.stylemirror.entity.Role;
import com.stylemirror.entity.Seller;
import com.stylemirror.entity.User;
import com.stylemirror.entity.Wishlist;
import com.stylemirror.enums.ApprovalStatus;
import com.stylemirror.enums.RoleType;
import com.stylemirror.exception.BadRequestException;
import com.stylemirror.exception.ResourceNotFoundException;
import com.stylemirror.repository.CartRepository;
import com.stylemirror.repository.RoleRepository;
import com.stylemirror.repository.SellerRepository;
import com.stylemirror.repository.UserRepository;
import com.stylemirror.repository.WishlistRepository;
import com.stylemirror.security.JwtTokenProvider;
import com.stylemirror.security.UserPrincipal;
import com.stylemirror.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SellerRepository sellerRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().trim(), request.getPassword())
        );

        String jwt = tokenProvider.generateToken(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Set<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        Long sellerId = null;
        String sellerStatus = null;
        Optional<Seller> sellerOpt = sellerRepository.findByUserId(user.getId());
        if (sellerOpt.isPresent()) {
            sellerId = sellerOpt.get().getId();
            sellerStatus = sellerOpt.get().getApprovalStatus().name();
        }

        return AuthResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(roles)
                .sellerId(sellerId)
                .sellerStatus(sellerStatus)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email is already registered: " + email);
        }

        RoleType roleType = RoleType.fromString(request.getRole());
        final RoleType finalRole = (roleType == RoleType.ROLE_ADMIN) ? RoleType.ROLE_BUYER : roleType;

        Role role = roleRepository.findByName(finalRole)
                .orElseGet(() -> roleRepository.save(Role.builder().name(finalRole).build()));

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .phone(request.getPhone())
                .active(true)
                .roles(new HashSet<>(Collections.singletonList(role)))
                .build();

        User savedUser = userRepository.save(user);

        // Initialize personal cart and wishlist
        cartRepository.save(Cart.builder().user(savedUser).build());
        wishlistRepository.save(Wishlist.builder().user(savedUser).build());

        Long sellerId = null;
        String sellerStatus = null;

        if (finalRole == RoleType.ROLE_SELLER) {
            String storeName = request.getStoreName() != null && !request.getStoreName().isBlank()
                    ? request.getStoreName().trim()
                    : request.getFullName().trim() + "'s Boutique";
            String storeDesc = request.getStoreDescription() != null
                    ? request.getStoreDescription()
                    : "Curated boutique on StyleMirror";
            String businessAddr = request.getBusinessAddress() != null && !request.getBusinessAddress().isBlank()
                    ? request.getBusinessAddress().trim()
                    : "Global Digital Atelier";

            Seller seller = Seller.builder()
                    .user(savedUser)
                    .storeName(storeName)
                    .storeDescription(storeDesc)
                    .businessAddress(businessAddr)
                    .gstNumber(request.getGstNumber())
                    .approvalStatus(ApprovalStatus.PENDING) // Sellers require admin approval
                    .build();

            Seller savedSeller = sellerRepository.save(seller);
            sellerId = savedSeller.getId();
            sellerStatus = savedSeller.getApprovalStatus().name();
        }

        // Authenticate new user automatically
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );
        String jwt = tokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .roles(Collections.singleton(role.getName().name()))
                .sellerId(sellerId)
                .sellerStatus(sellerStatus)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Set<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        Long sellerId = null;
        String sellerStatus = null;
        Optional<Seller> sellerOpt = sellerRepository.findByUserId(user.getId());
        if (sellerOpt.isPresent()) {
            sellerId = sellerOpt.get().getId();
            sellerStatus = sellerOpt.get().getApprovalStatus().name();
        }

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .active(user.getActive())
                .roles(roles)
                .sellerId(sellerId)
                .sellerStatus(sellerStatus)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
