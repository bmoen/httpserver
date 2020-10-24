package com.brentmoen.httpserver;

public interface HttpServerFactory {
    static HttpServerFactory instance = new DefaultHttpServerFactory();

    static HttpServerFactory getInstance() {
        return instance;
    }

    HttpServer create();
}
