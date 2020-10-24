package com.brentmoen.httpserver;

import java.util.Map;
import java.util.Objects;

class DefaultHttpRequest implements HttpRequest {
    private final HttpMethod method;
    private final String path;
    private final Map<String, String> headers;
    private final Map<String, String> query;
    private final String body;

    DefaultHttpRequest(HttpMethod method, String path, Map<String, String> headers, Map<String, String> query, String body) {
        this.method = Objects.requireNonNull(method);
        this.path = Objects.requireNonNull(path);
        this.headers = Objects.requireNonNull(headers);
        this.query = Objects.requireNonNull(query);
        this.body = Objects.requireNonNull(body);
    }

    @Override
    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public Map<String, String> getQuery() {
        return query;
    }

    @Override
    public String getBody() {
        return body;
    }
}
