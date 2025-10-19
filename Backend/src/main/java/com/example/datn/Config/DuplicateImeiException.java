package com.example.datn.Config;



import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception này sẽ được ném ra khi cố gắng tạo một tài nguyên đã tồn tại (ví dụ: IMEI trùng lặp).
 * Annotation @ResponseStatus(HttpStatus.CONFLICT) sẽ tự động trả về lỗi HTTP 409.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateImeiException extends RuntimeException {
    public DuplicateImeiException(String message) {
        super(message);
    }
}