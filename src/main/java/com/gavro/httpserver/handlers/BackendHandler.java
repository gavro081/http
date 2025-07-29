package com.gavro.httpserver.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

import com.gavro.httpserver.database.Database;
import com.gavro.httpserver.exceptions.BadRequestException;
import com.gavro.httpserver.exceptions.HttpVersionNotSupportedException;
import com.gavro.httpserver.http.HttpMethod;
import com.gavro.httpserver.utils.JsonResponseBuilder;

public class BackendHandler extends RequestHandler{
    protected BackendHandler(String requestLine, Map<String, String> headers, OutputStream outputStream)
    throws BadRequestException, HttpVersionNotSupportedException {
        super(requestLine, headers, outputStream);
    }
    @Override
    public void handleRequest() throws IOException {
        switch (method) {
            case HttpMethod.GET -> handleBasicGet();
            default -> handleUnsupportedMethod(new ArrayList<>());
        }
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
}
