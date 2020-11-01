package com.brentmoen.httpserver;

import com.brentmoen.httpclient.HttpClient;
import com.brentmoen.httpclient.HttpClientFactory;
import com.brentmoen.httpclient.HttpMethod;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {
//    private final HttpServerFactory serverFactory = new DefaultHttpServerFactory();
//
//    @Test
//    public void testCreateRoute() throws Exception {
//        HttpServer server = serverFactory.create();
//        server.route()
//            .method("GET")
//            .handler(request -> new HttpResponse()
//                .setBody("Hello World")
//                .setStatus(200));
//
//        server.setPort(7070);
//        server.start();
//
//        try {
//            HttpClient client = HttpClientFactory.getInstance().create();
//
//            com.brentmoen.httpclient.HttpResponse response = client.request()
//                .method(HttpMethod.GET)
//                .path("http://localhost:7070/")
//                .send();
//
//            assertEquals(200, response.status());
//            assertEquals("Hello World", "" + response.body());
//        } finally {
//            server.stop();
//        }
//    }
}
