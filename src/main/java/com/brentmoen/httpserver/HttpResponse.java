package com.brentmoen.httpserver;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private int status;
    private String body;
    private final Map<String, String> headers = new HashMap<>();

    public int getStatus() {
        return status;
    }

    public HttpResponse setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getBody() {
        return body;
    }

    public HttpResponse setBody(String body) {
        this.body = body;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpResponse addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }
}
