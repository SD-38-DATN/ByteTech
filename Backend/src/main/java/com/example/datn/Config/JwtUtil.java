//package com.example.datn.Config;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.util.Date;
//
//@Component
//public class JwtUtil {
//
//    private final SecretKey secretKey = Keys.hmacShaKeyFor("summer_25-block_2-sof3062-sd20201".getBytes());
////
////    public String generateToken(String username) {
////        return Jwts.builder()
////                .setSubject(username)
////                .setIssuedAt(new Date())
////                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 giờ
////                .signWith(secretKey)
////                .compact();
////    }
//
//    public String generateToken(String username, String role) {
//        return Jwts.builder()
//                .setSubject(username) // lưu username vào "sub"
//                .claim("role", role)   // thêm claim role
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 giờ
//                .signWith(secretKey)
//                .compact();
//    }
//
//
//
//
//
//
//
//
//    public String extractUsername(String token) {
//        return parseClaims(token).getSubject();
//    }
//
//    public boolean validateToken(String token, String username) {
//        String extractedUsername = extractUsername(token);
//        return extractedUsername.equals(username) && !isTokenExpired(token);
//    }
//
//    private boolean isTokenExpired(String token) {
//        Date expiration = parseClaims(token).getExpiration();
//        return expiration.before(new Date());
//    }
//
//    private Claims parseClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(secretKey)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    // ⚡ Bổ sung: public method để lấy tất cả claims từ token
//    public Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(secretKey)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    public SecretKey getSecretKey() {
//        return secretKey;
//    }
//
//}


package com.example.datn.Config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey = Keys.hmacShaKeyFor("summer_25-block_2-sof3062-sd20201".getBytes());

    // ✅ BƯỚC 1: Tạo một hằng số để quản lý thời gian hết hạn tập trung
    private static final long EXPIRATION_TIME_MS = 1000 * 60 * 60 * 10; // 10 giờ

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                // ✅ BƯỚC 3: Dùng hằng số ở đây để đảm bảo tính nhất quán
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(secretKey)
                .compact();
    }

    // ✅ BƯỚC 2: Tạo phương thức public để bên ngoài có thể gọi
    public long getExpirationMs() {
        return EXPIRATION_TIME_MS;
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token, String username) {
        String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

}