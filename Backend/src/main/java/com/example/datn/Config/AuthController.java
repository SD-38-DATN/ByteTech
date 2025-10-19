package com.example.datn.Config;




import com.example.datn.DTO.TrangMuaHang.LoginRequest;
import com.example.datn.Model.Role;
import com.example.datn.Model.Users;
import com.example.datn.Repository.RoleRepository;
import com.example.datn.Repository.UsersRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private final  TokenBlacklistService tokenBlacklistService ;


    @Autowired
    private UsersRepository usersRepository;


    @Autowired
    private RoleRepository roleRepository ;





@PostMapping("/login")
public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
    // Xác thực username + password
    authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPasswords())
    );

    // Lấy thông tin user + role
    UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getUsername());
    String role = userDetails.getAuthorities().iterator().next().getAuthority();

    // Lấy user để có tenHienThi
    Users user = usersRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("User không tồn tại"));

    // 🔹 Nếu chưa có tenHienThi thì fallback từ username và lưu lại DB
    String tenHienThi = user.getTenHienThi();
    if (tenHienThi == null || tenHienThi.isBlank()) {
        tenHienThi = user.getUsername().contains("@")
                ? user.getUsername().substring(0, user.getUsername().indexOf("@"))
                : user.getUsername();
        user.setTenHienThi(tenHienThi);
        usersRepository.save(user); // ✅ update lại DB
    }

    String hoTen = user.getHoTen();
    if (hoTen == null || hoTen.isBlank()) {
        // Nếu họ tên rỗng, tạm thời dùng tenHienThi để thay thế
        hoTen = tenHienThi;
        user.setHoTen(hoTen);
    }
    // Tạo token
    String token = jwtUtil.generateToken(user.getUsername(), role);

    // Giải mã để lấy thời gian issuedAt và expiration
    Claims claims = Jwts.parserBuilder()
            .setSigningKey(jwtUtil.getSecretKey())
            .build()
            .parseClaimsJws(token)
            .getBody();

    // Format sang giờ VN (GMT+7)
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

    Function<Date, String> getDayOrNight = (date) -> {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour < 18) ? "Day" : "Night";
    };

    String issuedAtStr = formatter.format(claims.getIssuedAt()) + " (" + getDayOrNight.apply(claims.getIssuedAt()) + ")";
    String expirationStr = formatter.format(claims.getExpiration()) + " (" + getDayOrNight.apply(claims.getExpiration()) + ")";

    Map<String, Object> response = new HashMap<>();
    response.put("token", token);
    response.put("role", role);
    response.put("username", user.getUsername());
    response.put("hoTen", user.getHoTen());
    response.put("tenHienThi", tenHienThi); // ✅ luôn trả về tên hiển thị
    response.put("hoTen", hoTen); // ✅ Luôn trả về họ tên
    response.put("issuedAt", issuedAtStr);
    response.put("expiration", expirationStr);

    return ResponseEntity.ok(response);
}



    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.addToken(token);
            return ResponseEntity.ok("Logout thành công, token đã bị vô hiệu hoá.");
        }
        return ResponseEntity.badRequest().body("Không tìm thấy token.");
    }

    @GetMapping("/blacklist")
    public ResponseEntity<Object> getBlacklist() {
        return ResponseEntity.ok(tokenBlacklistService.getAllBlacklistedTokens());
    }


    @GetMapping("/token-info")
    public ResponseEntity<Object> getTokenInfo(@RequestParam String token) {
        try {
            // Giải mã token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtUtil.getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            // Format sang giờ VN (GMT+7)
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

            // Hàm xác định ngày/đêm
            Function<Date, String> getDayOrNight = (date) -> {
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                cal.setTime(date);
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                return (hour >= 6 && hour < 18) ? "Day" : "Night";
            };

            // Gộp ngày giờ + ngày/đêm
            String issuedAtStr = formatter.format(claims.getIssuedAt())
                    + " (" + getDayOrNight.apply(claims.getIssuedAt()) + ")";
            String expirationStr = formatter.format(claims.getExpiration())
                    + " (" + getDayOrNight.apply(claims.getExpiration()) + ")";

            Map<String, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("role", role);
            response.put("issuedAt", issuedAtStr);
            response.put("expiration", expirationStr);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Token không hợp lệ hoặc đã hết hạn!");
        }
    }



}
