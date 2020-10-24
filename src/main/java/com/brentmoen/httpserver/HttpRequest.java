package com.brentmoen.httpserver;

import java.util.Map;

public interface HttpRequest {
    HttpMethod getMethod();

    String getPath();

    Map<String, String> getHeaders();

    Map<String, String> getQuery();

    String getBody();
}
