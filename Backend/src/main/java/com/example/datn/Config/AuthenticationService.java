package com.example.datn.Config;



import com.example.datn.Model.Users;

import com.example.datn.Repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UsersRepository userRepository;

    public Users getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + userDetails.getUsername()));
    }

    public Integer getCurrentUserId() {
        Users currentUser = getCurrentUser();
        log.debug("Current user ID: {}", currentUser.getId());
        return currentUser.getId();
    }

    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities().isEmpty()) {
            throw new RuntimeException("No role found for authenticated user");
        }
        return authentication.getAuthorities().iterator().next().getAuthority();
    }

    public boolean isAdmin() {
        try {
            String role = getCurrentUserRole();
            return "ADMIN".equals(role);
        } catch (Exception e) {
            log.error("Error checking admin role: ", e);
            return false;
        }
    }

    public boolean isUser() {
        try {
            String role = getCurrentUserRole();
            return "USER".equals(role);
        } catch (Exception e) {
            log.error("Error checking user role: ", e);
            return false;
        }
    }

    public void validateUserAccess(Integer userId) {
        Integer currentUserId = getCurrentUserId();

        if (!isAdmin() && !currentUserId.equals(userId)) {
            throw new RuntimeException("Access denied: You can only access your own data");
        }

        log.debug("Access validated for user {} accessing user {}", currentUserId, userId);
    }
}