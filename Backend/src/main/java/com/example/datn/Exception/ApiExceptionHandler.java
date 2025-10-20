package com.example.datn.Exception;

import com.example.datn.Model.Users;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "BAD_REQUEST",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOther(Exception ex) {
        // DEBUG: in dev bạn có thể log ex.printStackTrace();
        return ResponseEntity.status(500).body(Map.of(
                "error", "INTERNAL_ERROR",
                "message", ex.getMessage()
        ));
    }
    public static Specification<Users> statusEq(String status) {
        if (status == null || status.isBlank()) return null;
        String s = status.trim().toUpperCase();
        if ("BLOCKED".equals(s)) s = "BANNED";
        if ("DISABLED".equals(s)) s = "INACTIVE";
        final String finalS = s;
        return (root, cq, cb) -> cb.equal(root.get("trangThai"), finalS);
    }
}