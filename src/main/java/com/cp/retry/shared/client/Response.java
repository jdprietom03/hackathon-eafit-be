package com.cp.retry.shared.client;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;


public class Response {

    private String bodyString;
    private ResponseEntity<?> response;

    private ObjectMapper objectMapper;

    public Response(ResponseEntity<?> entity) {
        this.bodyString = (String) entity.getBody();
        this.response = entity;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public <T extends Exception> Response validateData(ThrowingConsumer<T> consumer) throws T {
        if (!this.response.getStatusCode().is2xxSuccessful()) {
            consumer.accept();
        }

        return this;
    }

    @FunctionalInterface
    public interface ThrowingConsumer<T extends Exception> {
        void accept() throws T;
    }

    public <O> O getData(Class<O> responseType) {
        return getData(responseType, null, null);
    }

    public <O, K, V> O getData(Class<O> responseType,
            Class<K> keyType, Class<V> valueType) {
        Optional<String> response = Optional.ofNullable(bodyString);

        if (response.isPresent()) {
            try {
                if (Map.class.isAssignableFrom(responseType)) {
                    JavaType mapType = TypeFactory.defaultInstance().constructMapType(Map.class, keyType, valueType);

                    return responseType.cast(
                            objectMapper.readValue(response.get(), mapType));
                }

                return objectMapper.readValue(response.get(), responseType);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        return null;
    }
}
