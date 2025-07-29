package com.gavro.httpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;

import com.gavro.httpserver.exceptions.BadRequestException;

public class BackendHandler extends RequestHandler{
    BackendHandler(String requestLine, Map<String, String> headers, OutputStream outputStream)
    throws BadRequestException {
        super(requestLine, headers, outputStream);
    }
    @Override
    public void handleRequest() throws IOException {
        switch (method) {
            case HttpMethod.GET -> handleBasicGet();
            default -> handleUnsupportedMethod(new ArrayList<>());
        }
    }

    private void handleBasicGet() throws IOException{
        String message = """
                         {"data": "hello from backend server"}
                         """;
        writeHeadersWithBody(writer, 200, message.length(), "application/json", null);
        writer.write(message);
        writer.flush();
    }
}
