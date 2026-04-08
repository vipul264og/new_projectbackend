package com.edulib.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String error;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ApiResponse() {}

    public ApiResponse(boolean success, String message, T data, String error) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }

    // ── Static factory methods ───────────────────────────────────────────────

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, null);
    }

    public static <T> ApiResponse<T> error(String error) {
        return new ApiResponse<>(false, null, null, error);
    }

    public static <T> ApiResponse<T> error(String message, String error) {
        return new ApiResponse<>(false, message, null, error);
    }

    // Builder support for GlobalExceptionHandler validation errors
    public static <T> Builder<T> builder() { return new Builder<>(); }

    public static class Builder<T> {
        private boolean success;
        private String message;
        private T data;
        private String error;

        public Builder<T> success(boolean success)  { this.success = success; return this; }
        public Builder<T> message(String message)   { this.message = message; return this; }
        public Builder<T> data(T data)              { this.data = data; return this; }
        public Builder<T> error(String error)       { this.error = error; return this; }

        public ApiResponse<T> build() {
            return new ApiResponse<>(success, message, data, error);
        }
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public boolean isSuccess()                      { return success; }
    public void setSuccess(boolean success)         { this.success = success; }
    public String getMessage()                      { return message; }
    public void setMessage(String message)          { this.message = message; }
    public T getData()                              { return data; }
    public void setData(T data)                     { this.data = data; }
    public String getError()                        { return error; }
    public void setError(String error)              { this.error = error; }
    public LocalDateTime getTimestamp()             { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
