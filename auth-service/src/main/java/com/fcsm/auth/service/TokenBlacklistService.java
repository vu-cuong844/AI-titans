package com.fcsm.auth.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {
    
    private final Set<String> blacklistedTokens = new HashSet<>();
    
    /**
     * Thêm token vào blacklist khi user logout
     * @param token JWT token cần blacklist
     */
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }
    
    /**
     * Kiểm tra xem token có trong blacklist không
     * @param token JWT token cần kiểm tra
     * @return true nếu token bị blacklist, false nếu không
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
    
    /**
     * Xóa token khỏi blacklist (có thể dùng cho cleanup)
     * @param token JWT token cần xóa
     */
    public void removeFromBlacklist(String token) {
        blacklistedTokens.remove(token);
    }
    
    /**
     * Lấy số lượng token trong blacklist
     * @return số lượng token
     */
    public int getBlacklistSize() {
        return blacklistedTokens.size();
    }
    
    /**
     * Xóa tất cả token khỏi blacklist
     */
    public void clearBlacklist() {
        blacklistedTokens.clear();
    }
}
