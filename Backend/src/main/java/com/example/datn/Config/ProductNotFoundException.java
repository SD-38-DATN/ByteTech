package com.example.datn.Config;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception này sẽ được ném ra khi không tìm thấy một tài nguyên nào đó (Sản phẩm, Biến thể, v.v.).
 * Annotation @ResponseStatus(HttpStatus.NOT_FOUND) sẽ tự động
 * trả về mã lỗi HTTP 404 NOT FOUND cho client mà không cần xử lý thêm ở Controller.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}