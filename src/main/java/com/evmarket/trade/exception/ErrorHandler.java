package com.evmarket.trade.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorHandler {
    LIST_EMPTY(200, "Danh sách rỗng", HttpStatus.OK),
    UNCATEGORIZED_EXCEPTION(999, "Lỗi không xác thực", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_EXISTED(404, "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(401, "Lỗi xác thực", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(403, "Bạn bị cấm truy cập vào trang này", HttpStatus.FORBIDDEN),
    INVALID_INPUT(402, "Đầu vào không hợp lệ", HttpStatus.BAD_REQUEST),
    VALIDATION_FAILED(402, "Dữ liệu không hợp lệ", HttpStatus.BAD_REQUEST),

    USERNAME_EXIST(405, "Tên đăng nhập đã tồn tại", HttpStatus.BAD_REQUEST),
    EMAIL_EXIST(405, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    PHONE_EXIST(405, "Số điện thoại đã tồn tại", HttpStatus.BAD_REQUEST),
    IDENTITY_CARD_EXIST(405, "Số CCCD/CMND đã tồn tại", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(400, "Email không hợp lệ", HttpStatus.BAD_REQUEST),
    PHONE_INVALID(400, "Số điện thoại không hợp lệ", HttpStatus.BAD_REQUEST),
    IDENTITY_CARD_INVALID(400, "CCCD/CMND không hợp lệ", HttpStatus.BAD_REQUEST),
    PASSWORD_WEAK(400, "Mật khẩu chưa đủ mạnh", HttpStatus.BAD_REQUEST),
    PASSWORD_CONFIRM_NOT_MATCH(400, "Mật khẩu xác nhận không khớp", HttpStatus.BAD_REQUEST),
    GENDER_INVALID(400, "Giới tính không hợp lệ", HttpStatus.BAD_REQUEST),
    DATE_OF_BIRTH_INVALID(400, "Ngày sinh không hợp lệ", HttpStatus.BAD_REQUEST),
    CREDENTIALS_INVALID(401, "Tài khoản hoặc mật khẩu không đúng", HttpStatus.UNAUTHORIZED);

    ErrorHandler(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}


