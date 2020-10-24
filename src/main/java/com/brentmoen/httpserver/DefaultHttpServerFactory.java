package com.brentmoen.httpserver;

class DefaultHttpServerFactory implements HttpServerFactory {
    @Override
    public HttpServer create() {
        return new DefaultHttpServer();
    }
}
