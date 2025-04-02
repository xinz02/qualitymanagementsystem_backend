package com.qualitymanagementsystemfc.qualitymanagementsystem.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonApiResult<T> {
    private int status;
    private String message;
    private T data;

//    public static <T> CommonResult<T> success(String message, T data) {
//        return new CommonResult<>(200, message, data);
//    }
//
//    public static <T> CommonResult<T> error(int status, String message, T data) {
//        return new CommonResult<>(status, message, data);
//    }
}
