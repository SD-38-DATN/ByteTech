package com.example.datn.Config;



import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {

    private final Set<String> blacklist = new HashSet<>();

    private LocalDateTime addedAt;   // Thời gian bắt đầu bị blacklist
    private LocalDateTime expiresAt; // Thời gian kết thúc (token hết hạn)

    public void addToken(String token) {
        blacklist.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }

    public Set<String> getAllBlacklistedTokens() {
        return blacklist;
    }


}

