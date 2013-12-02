package com.itrustcambodia.restclient;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

public class ResponseEntity<T> {

    private HttpResponse response;

    private int statusCode;

    private T body;

    public ResponseEntity(HttpResponse response) {
        this.response = response;
        this.statusCode = response.getStatusLine().getStatusCode();
    }

    public ResponseEntity(HttpResponse response, T body) {
        this.response = response;
        this.statusCode = response.getStatusLine().getStatusCode();
        this.body = body;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public T getBody() {
        return this.body;
    }

    public ResponseEntity(int statusCode) {
        this.statusCode = statusCode;
    }

    public Header[] getHeaders(String name) {
        if (this.response == null) {
            return null;
        }
        return response.getHeaders(name);
    }

    public Header getFirstHeader(String name) {
        if (this.response == null) {
            return null;
        }
        return response.getFirstHeader(name);
    }

    public Header getLastHeader(String name) {
        if (this.response == null) {
            return null;
        }
        return response.getLastHeader(name);
    }
}
