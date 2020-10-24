package com.brentmoen.httpserver;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.Function;

class DefaultHttpRoute implements HttpRoute {
    private String method = "GET";
    private String path = "/";
    private String staticContent = "";
    private String staticFile;
    private String contentType = "text/plain";
    private Function<HttpRequest, HttpResponse> handler;

    @Override
    public HttpRoute method(String method) {
        if (StringUtils.isBlank(Objects.requireNonNull(method))) {
            throw new RuntimeException("Method must not be blank");
        }

        this.method = method;
        return this;
    }

    @Override
    public HttpRoute path(String path) {
        if (StringUtils.isBlank(Objects.requireNonNull(path))) {
            throw new RuntimeException("Path must not be blank");
        }

        this.path = path;
        return this;
    }

    @Override
    public HttpRoute staticContent(String content, String contentType) {
        if (StringUtils.isBlank(Objects.requireNonNull(contentType))) {
            throw new RuntimeException("Content type must not be blank");
        }

        if (content == null) {
            this.staticContent = "";
        } else {
            this.staticContent = content;
        }

        this.contentType = contentType;
        this.staticFile = null;
        this.handler = null;
        return this;
    }

    @Override
    public HttpRoute staticContentFile(String fileOrResource, String contentType) {
        if (StringUtils.isBlank(Objects.requireNonNull(contentType))) {
            throw new RuntimeException("Content type must not be blank");
        }

        this.staticFile = Objects.requireNonNull(fileOrResource);
        this.staticContent = null;
        this.handler = null;
        return this;
    }

    @Override
    public HttpRoute handler(Function<HttpRequest, HttpResponse> handler) {
        this.handler = handler;
        this.staticContent = null;
        this.staticFile = null;
        return this;
    }

    String getMethod() {
        return method;
    }

    String getPath() {
        return path;
    }

    String getStaticContent() {
        return staticContent;
    }

    String getStaticFile() {
        return staticFile;
    }

    String getContentType() {
        return contentType;
    }

    Function<HttpRequest, HttpResponse> getHandler() {
        return handler;
    }
}
