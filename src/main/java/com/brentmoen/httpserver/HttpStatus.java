package com.brentmoen.httpserver;

public class HttpStatus extends Exception {
    private final int status;

    public HttpStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
