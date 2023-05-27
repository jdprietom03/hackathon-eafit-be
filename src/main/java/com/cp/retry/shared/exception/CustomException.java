package com.cp.retry.shared.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatusCode;

public abstract class CustomException extends RuntimeException {
    protected HttpStatusCode statusCode;
    protected String message;

    public Response getResponse() {
        return new Response(message, statusCode.value());
    }

    public static class Response {
        public String error;
        public int status;
        public LocalDateTime timestamp;

        public Response(String message, int status) {
            this.status = status;
            this.error = message;
            this.timestamp = LocalDateTime.now();
        }
    }
}
