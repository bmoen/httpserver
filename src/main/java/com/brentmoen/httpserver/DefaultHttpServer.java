package com.brentmoen.httpserver;

import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Function;

class DefaultHttpServer implements HttpServer {
    private final List<DefaultHttpRoute> routes = new ArrayList<>();
    private int port;
    private com.sun.net.httpserver.HttpServer server;
    private final Map<String, String> fileCache = new HashMap<>();
    private final Map<String, File> staticFolders = new HashMap<>();

    DefaultHttpServer() {}

    @Override
    public HttpServer setPort(int port) {
        this.port = port;
        return this;
    }

    @Override
    public HttpRoute route() {
        DefaultHttpRoute route = new DefaultHttpRoute();
        routes.add(route);
        return route;
    }

    @Override
    public HttpServer routeHandler(HttpRouteHandler handler) {
        route()
            .path(handler.path())
            .method(handler.method())
            .handler(request -> {
                try {
                    return handler.handleRequest(request);
                } catch (HttpStatus status) {
                    return new HttpResponse().setStatus(status.getStatus());
                } catch (Throwable t) {
                    t.printStackTrace();
                    return new HttpResponse().setStatus(500);
                }
            });

        return this;
    }

    @Override
    public HttpServer staticFolder(String path, File folder) {
        this.staticFolders.put(path, folder);
        return this;
    }

    @Override
    public HttpServer start() {
        try {
            server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server.setExecutor(Executors.newFixedThreadPool(10));

        for (Map.Entry<String, File> staticFolder : staticFolders.entrySet()) {
            createContextsForFolder(staticFolder.getKey(), staticFolder.getValue());
        }

        for (DefaultHttpRoute route : routes) {
            server.createContext(route.getPath(), exchange -> handleExchange(exchange, route));
        }

        server.start();
        return this;
    }

    @Override
    public HttpServer stop() {
        if (server != null) {
            server.stop(10);
            server = null;
        }

        return this;
    }

    private void createContextsForFolder(String prefix, File folder) {
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }

        for (File file : FileUtils.listFiles(folder, null, false)) {
            if (file.isFile()) {
                String contextPath;

                if (file.getName().equalsIgnoreCase("index.html") || file.getName().equalsIgnoreCase("index.htm")) {
                    contextPath = "/";
                } else {
                    contextPath = prefix + file.getName();
                }

                server.createContext(contextPath, exchange -> {
                    if (exchange.getRequestURI().toString().equalsIgnoreCase(contextPath)) {
                        handleExchangeWithStaticFile(exchange, file.getAbsolutePath(), getContentTypeForFile(file.getAbsolutePath()));
                        exchange.getResponseBody().flush();
                        exchange.getResponseBody().close();
                    } else {
                        handle404(exchange);
                    }
                });
            }
        }
    }

    private String getContentTypeForFile(String file) {
        String lowerName = file.toLowerCase();

        if (lowerName.endsWith(".js")) {
            return "application/javascript";
        } else if (lowerName.endsWith(".js.map")) {
            return "application/octet-stream";
        }  else if (lowerName.endsWith(".json")) {
            return "application/json";
        } else if (lowerName.endsWith(".css")) {
            return "text/css";
        } else if (lowerName.endsWith(".html") || lowerName.endsWith(".htm")) {
            return "text/html";
        } else if (lowerName.endsWith(".txt")) {
            return "text/plain";
        } else if (lowerName.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerName.endsWith(".png")) {
            return "image/png";
        } else {
            return null;
        }
    }

    private void handleExchange(HttpExchange exchange, DefaultHttpRoute route) {
        try {
            if (!exchange.getRequestMethod().equalsIgnoreCase(route.getMethod())) {
                if (route.getHandler() != null) {
                    handleExchangeWithHandler(exchange, route);
                } else if (route.getStaticFile() != null) {
                    handleExchangeWithStaticFile(exchange, route.getStaticFile(), getContentTypeForFile(route.getStaticFile()));
                } else {
                    handleExchangeWithStaticContent(exchange, route);
                }

                exchange.getResponseBody().flush();
                exchange.getResponseBody().close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleExchangeWithHandler(HttpExchange exchange, DefaultHttpRoute route) throws IOException {
        Function<HttpRequest, HttpResponse> handler = route.getHandler();
        Map<String, String> headers = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : exchange.getRequestHeaders().entrySet()) {
            headers.put(entry.getKey(), entry.getValue().get(0));
        }

        String path = exchange.getRequestURI().getPath();
        Map<String, String> query = new HashMap<>();
        String queryStr = exchange.getRequestURI().getQuery();

        if (queryStr != null) {
            for (String queryPart : StringUtils.split(exchange.getRequestURI().getQuery(), "&")) {
                String[] parts = StringUtils.split(queryPart, "=");
                query.put(parts[0], parts[1]);
            }
        }

        String body = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
        HttpRequest request = new DefaultHttpRequest(HttpMethod.valueOf(exchange.getRequestMethod()), path, headers, query, body);
        HttpResponse response;

        try {
            response = handler.apply(request);
        } catch (Throwable t) {
            response = new HttpResponse();
            response.setStatus(500);
            t.printStackTrace();
        }

        String responseBody = response.getBody();

        for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
            exchange.getResponseHeaders().add(header.getKey(), header.getValue());
        }

        if (responseBody != null) {
            exchange.sendResponseHeaders(response.getStatus(), responseBody.length());
            exchange.getResponseBody().write(responseBody.getBytes());
        } else {
            exchange.sendResponseHeaders(response.getStatus(), 0);
        }
    }

    private void handleExchangeWithStaticFile(HttpExchange exchange, String file, String contentType) throws IOException {
        try (InputStream in = getInputStream(file)) {
            byte[] content = IOUtils.toString(in, StandardCharsets.UTF_8).getBytes();

            if (contentType != null) {
                exchange.getResponseHeaders().add("Content-Type", contentType);
            }

            exchange.sendResponseHeaders(200, content.length);
            exchange.getResponseBody().write(content);
        }
    }

    private void handleExchangeWithStaticContent(HttpExchange exchange, DefaultHttpRoute route) throws IOException {
        byte[] content = route.getStaticContent().getBytes();
        exchange.getResponseHeaders().add("Content-Type", route.getContentType());
        exchange.sendResponseHeaders(200, content.length);
        exchange.getResponseBody().write(content);
    }

    private void handle404(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.getResponseBody().close();
    }

    private static InputStream getInputStream(String resourceOrFileName) throws FileNotFoundException {
        File file = new File(resourceOrFileName);

        if (file.exists()) {
            return new FileInputStream(file);
        }

        if (!resourceOrFileName.startsWith("/")) {
            resourceOrFileName = "/" + resourceOrFileName;
        }

        InputStream is = DefaultHttpServer.class.getResourceAsStream(resourceOrFileName);

        if (is == null) {
            throw new FileNotFoundException();
        }

        return is;
    }
}
