package com.brentmoen.httpserver;

import java.io.File;

public interface HttpServer {
    HttpServer setPort(int port);

    HttpRoute route();

    HttpServer staticFolder(String path, File folder);

    HttpServer start();

    HttpServer stop();

    HttpServer routeHandler(HttpRouteHandler route);
}
