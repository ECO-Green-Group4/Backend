package com.evmarket.trade.response.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseResponse<T> {
    private String message;
    private boolean success;
    private T data;
    
    public static <T> BaseResponse<T> success(T data, String message) {
        return BaseResponse.<T>builder()
                .data(data)
                .message(message)
                .success(true)
                .build();
    }
    
    public static <T> BaseResponse<T> error(String message) {
        return BaseResponse.<T>builder()
                .message(message)
                .success(false)
                .build();
    }
}
