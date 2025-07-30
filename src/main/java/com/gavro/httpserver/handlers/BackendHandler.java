package com.gavro.httpserver.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.gavro.httpserver.database.Database;
import com.gavro.httpserver.database.dao.implementation.SubjectDaoImpl;
import com.gavro.httpserver.database.dao.service.SubjectService;
import com.gavro.httpserver.exceptions.BadRequestException;
import com.gavro.httpserver.exceptions.HttpVersionNotSupportedException;
import com.gavro.httpserver.http.HttpMethod;
import com.gavro.httpserver.routes.SubjectRouter;
import com.gavro.httpserver.utils.JsonResponseBuilder;
import com.gavro.httpserver.utils.JsonRouteResult;

public class BackendHandler extends RequestHandler{
    private final String route;
    private final String requestBody;


    protected BackendHandler(String requestLine, Map<String, String> headers, OutputStream outputStream)
    throws BadRequestException, HttpVersionNotSupportedException {
        super(requestLine, headers, outputStream);
        requestBody = headers.remove("request-body");
        // ex. GET /api/subjects/{id} HTTP/1.1 -> /api/subjects/{id} -> subjects/{id}
//        route = requestLine.split("\\s+")[1].split("api/")[1];
        route = requestLine.split("\\s++")[1];

    }

    public void dispatchRequest() throws IOException, BadRequestException {
        Map<String, String> queryParams = extractQueryParams(route);
        // todo fix issue that occurs because of params extraction
        String path = extractPath(route);
//        String path = route;
        if (path.startsWith("/api/subjects")){
            try {
                JsonRouteResult response = new SubjectRouter(new SubjectService(new SubjectDaoImpl(Database.getConnection())))
                        .handle(method, path, queryParams, requestBody);
                int statusCode = response.statusCode();
                if (statusCode >= 400){
                    JsonResponseBuilder.sendErrorResponse(writer, outputStream, statusCode, response.jsonBody());
                } else {
                    JsonResponseBuilder.sendJsonResponse(writer, outputStream, statusCode,
                            response.jsonBody(), Map.of());
                }
            } catch (SQLException e) {
                JsonResponseBuilder.sendErrorResponse(writer, outputStream, 500, "");
            }
        }
    }

    @Override
    public void handleRequest() throws IOException, BadRequestException {
        dispatchRequest();
//        switch (method) {
//            case HttpMethod.GET -> handleBasicGet();
//            default -> handleUnsupportedMethod(new ArrayList<>());
//        }
    }

    private void handleBasicGet() throws IOException {
        try (Connection conn = Database.getConnection();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery("SELECT * FROM subject LIMIT 5")){
            
            StringBuilder bodySb = new StringBuilder("{\"data\":[");
            boolean first = true;
            while (rs.next()) {
                if (!first) bodySb.append(",");
                else first = false;

                String name = rs.getString("name");
                String code = rs.getString("code");
                String subjectAbstract = rs.getString("abstract");

                bodySb.append("{")
                        .append("\"name\":\"").append(JsonResponseBuilder.escapeJson(name)).append("\",")
                        .append("\"code\":\"").append(JsonResponseBuilder.escapeJson(code)).append("\",")
                        .append("\"abstract\":\"").append(JsonResponseBuilder.escapeJson(subjectAbstract)).append("\"")
                        .append("}");
            }

            bodySb.append("]}");

            JsonResponseBuilder.sendJsonResponse(writer, outputStream, 200, bodySb.toString(), null);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while fetching subjects", e);
            handleInternalServerError();
        }
    }

    public static Map<String, String> extractQueryParams(String uri) {
        Map<String, String> params = new HashMap<>();
        if (uri.contains("?")) {
            String query = uri.split("\\?", 2)[1];
            for (String pair : query.split("&")) {
                String []kv = pair.split("=");
                if (kv.length == 2) {
                    params.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
                }
            }
        }
        return params;
    }

    public static String extractPath(String route){
        if (route.contains("?")) {
            route = route.split("\\?", 2)[0];
        }
        return route;
    }
}
