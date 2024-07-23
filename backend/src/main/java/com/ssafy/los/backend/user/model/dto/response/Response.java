package com.ssafy.los.backend.user.model.dto.response;

public class Response<T> {

    private String resultCode;
    private T data;

    // 생성자
    public Response(String resultCode, T data) {
        this.resultCode = resultCode;
        this.data = data;
    }

    // 성공 응답을 생성하는 메소드
    public static <T> Response<T> success(T data) {
        return new Response<>("SUCCESS", data);
    }

    // 실패 응답을 생성하는 메소드
    public static <T> Response<T> failure(String resultCode) {
        return new Response<>(resultCode, null);
    }

    // 게터와 세터
    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

