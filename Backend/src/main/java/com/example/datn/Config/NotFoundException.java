package com.example.datn.Config;



import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception này sẽ được ném ra khi không tìm thấy một tài nguyên.
 * Annotation @ResponseStatus(HttpStatus.NOT_FOUND) sẽ tự động trả về lỗi HTTP 404.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}