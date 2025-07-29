package com.gavro.httpserver.exceptions;

public class HttpVersionNotSupportedException extends Exception{

    public HttpVersionNotSupportedException() {
        super("Server only supports HTTP/1.1.");
    }

    public HttpVersionNotSupportedException(Throwable cause) {
        super("Server only supports HTTP/1.1.", cause);
    }
}
