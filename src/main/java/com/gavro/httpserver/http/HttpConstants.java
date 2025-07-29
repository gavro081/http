package com.gavro.httpserver.http;

public final class HttpConstants {
    
    // content types
    public static final String CONTENT_TYPE_JSON = "application/json; charset=utf-8";
    public static final String CONTENT_TYPE_HTML = "text/html; charset=utf-8";
    public static final String CONTENT_TYPE_CSS = "text/css; charset=utf-8";
    public static final String CONTENT_TYPE_JS = "application/javascript; charset=utf-8";
    public static final String CONTENT_TYPE_PLAIN = "text/plain; charset=utf-8";
    public static final String CONTENT_TYPE_OCTET_STREAM = "application/octet-stream";
    public static final String CONTENT_TYPE_JPEG = "image/jpeg";
    public static final String CONTENT_TYPE_PNG = "image/png";
    public static final String CONTENT_TYPE_GIF = "image/gif";
    public static final String CONTENT_TYPE_SVG = "image/svg+xml";
    public static final String CONTENT_TYPE_X_ICON = "image/x-icon";
    public static final String CONTENT_TYPE_PDF = "application/pdf";
    
    // HTTP headers
    public static final String HEADER_ALLOW = "Allow";
    public static final String HEADER_ETAG = "Etag";
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_IF_NONE_MATCH = "If-None-Match";
    
    // cache control values
    public static final String CACHE_CONTROL_PUBLIC_MAX_AGE_0 = "public, max-age=0";
    
    private HttpConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
}
