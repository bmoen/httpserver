package com.brentmoen.httpserver;

public interface HttpRouteHandler {
    String method();

    String path();

    HttpResponse handleRequest(HttpRequest request) throws Exception;
}
