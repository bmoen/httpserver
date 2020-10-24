package com.brentmoen.httpserver;

import java.util.function.Function;

public interface HttpRoute {
    HttpRoute method(String method);

    HttpRoute path(String path);

    HttpRoute staticContent(String content, String contentType);

    HttpRoute staticContentFile(String fileOrResource, String contentType);

    HttpRoute handler(Function<HttpRequest, HttpResponse> handler);
}
