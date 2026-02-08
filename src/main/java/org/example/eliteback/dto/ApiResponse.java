package org.example.eliteback.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private T data;

    public ApiResponse() {}
    public ApiResponse(T data) { this.data = data; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(data);
    }
}
