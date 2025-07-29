package com.gavro.httpserver;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.gavro.httpserver.config.ServerConfig;
import com.gavro.httpserver.exceptions.BadRequestException;

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
            long length = requestedResource.length();
            long lastModified = requestedResource.lastModified();
            String eTag = "W/\"" + length + "-" + lastModified + "\"";

            if (checkIfCacheHit(eTag)) return;

            Map<String, String> extraHeaders = Map.ofEntries(
                    Map.entry("Etag", eTag),
                    Map.entry("Cache-Control", "public, max-age=0")
            );
            writeHeadersWithBody(writer, 200, body.length, contentType, extraHeaders);

            if (method == HttpMethod.GET) {
                outputStream.write(body);
                outputStream.flush();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error serving file: {0}", requestedResource.getPath());
            handleInternalServerError();
        }
    }

    private boolean checkIfCacheHit(String eTag) throws IOException{
        String ifNoneMatch = requestHeaders.get("if-none-match");
        if (eTag.equals(ifNoneMatch)) {
            writeHeadersWithoutBody(writer, 304, Map.of("Etag", eTag));
            return true;
        }
        return false;
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
        } catch (BadRequestException e) {
            throw new BadRequestException("Invalid request target: " + requestTarget);
        }
    }
}
