package com.cp.retry.shared.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class RestClient {
    // Use property
    // @Value("${api.endpoint}")
    protected String URL = "";
    private RestTemplate restTemplate;

    public RestClient() {
        this.restTemplate = new RestTemplate();
    }

    public Response GET(String path) {
        return call(path, null, HttpMethod.GET);
    }

    public <I> Response POST(String path, I body, HttpHeaders headers) {
        HttpEntity<I> request = new HttpEntity<I>(body, headers);

        return call(path, request, HttpMethod.POST);
    }

    private String getEndpoint(String path) {
        return String.format("%s/%s", getBaseEndpoint(), path);
    }

    private Response call(String path, HttpEntity request, HttpMethod method) {
        String response;
        HttpStatusCode statusCode;
        ResponseEntity<String> entity;

        try {
            entity = restTemplate.exchange(getEndpoint(path), method, request, String.class);
            response = entity.getBody();
            statusCode = entity.getStatusCode();
        } catch (HttpClientErrorException e) {
            HttpClientErrorException httpClientErrorException = e;
            response = httpClientErrorException.getResponseBodyAsString();
            statusCode =httpClientErrorException.getStatusCode();
        } catch(HttpServerErrorException e) {
            HttpServerErrorException httpServerErrorException = e;
            response = httpServerErrorException.getResponseBodyAsString();
            statusCode =httpServerErrorException.getStatusCode();
        }

        entity = new ResponseEntity<String>(response, statusCode);

        return new Response(entity);
    }

    protected String getBaseEndpoint() {
        return this.URL;
    }
}
