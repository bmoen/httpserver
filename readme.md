
# Introduction

Provides an API for managing an HTTP Server service. Ships with a default implementation based on [HttpServer](https://docs.oracle.com/javase/8/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/HttpServer.html).

# Example Usage

```java
HttpServer server = HttpServerFactory.getInstance().create();

server.route()
    .method("GET")
    .path("/exampleroute")
    .handler(request -> {
        return new HttpResponse()
            .setBody("<p>Hello World</p>")
            .setStatus(200);
    });

server.start();
```
