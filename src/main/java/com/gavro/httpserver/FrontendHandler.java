package com.gavro.httpserver;

import com.gavro.httpserver.config.ServerConfig;
import com.gavro.httpserver.exceptions.BadRequestException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class FrontendHandler extends RequestHandler{
    private final File requestedResource;

    FrontendHandler(String requestLine, Map<String, String> headers, OutputStream outputStream)
    throws BadRequestException {
        super(requestLine, headers, outputStream);
        this.requestedResource = resolveResource(requestLine.split("\\s+")[1]);
    }

    @Override
    public void handleRequest() throws IOException{
        switch (method) {
            case GET, HEAD -> handleGetOrHead();
            default -> handleUnsupportedMethod(List.of("GET", "HEAD"));
        }
    }

    private void handleGetOrHead() throws IOException {
        try {
            if (!requestedResource.exists() || !requestedResource.canRead()) {
                handleNotFound();
                return;
            }

            String contentType = determineContentType(requestedResource);
            byte[] body = Files.readAllBytes(requestedResource.toPath());

            writeHeaders(writer, 200, body.length, contentType, null);

            if (method == HttpMethod.GET) {
                outputStream.write(body);
                outputStream.flush();
            }
        } catch (IOException e) {
            LOGGER.severe("Error serving file: " + requestedResource.getPath());
            handleInternalServerError();
        }
    }

    private static File resolveResource(String requestTarget) throws BadRequestException {
        try {
            Path basePath = Path.of(ServerConfig.DEFAULT_CONTENT_ROOT).toAbsolutePath().normalize();
            String relativePath = "/".equals(requestTarget) ? ServerConfig.DEFAULT_INDEX_FILE : requestTarget.substring(1);
            Path resolvedPath = basePath.resolve(relativePath).normalize();

            if (!resolvedPath.startsWith(basePath)) {
                throw new BadRequestException("Forbidden: Directory traversal attempt");
            }

            File targetFile = resolvedPath.toFile();
            if (!targetFile.exists()) {
                return basePath.resolve(ServerConfig.DEFAULT_INDEX_FILE).toFile();
            }

            if (targetFile.isDirectory()) {
                File indexFile = new File(targetFile, ServerConfig.DEFAULT_INDEX_FILE);
                return indexFile.exists() ? indexFile : basePath.resolve(ServerConfig.DEFAULT_INDEX_FILE).toFile();
            }

            return targetFile;
        } catch (Exception e) {
            throw new BadRequestException("Invalid request target: " + requestTarget);
        }
    }
}
