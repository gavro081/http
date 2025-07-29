package com.gavro.httpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
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

    private void handleBasicGet() throws IOException {
        try (Connection conn = Database.getConnection()){
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM subject LIMIT 5;");
            StringBuilder bodySb = new StringBuilder("{\"data\":[");
            boolean first = true;
            while (rs.next()) {
                if (!first) bodySb.append(",");
                else first = false;

                String name = rs.getString("name");
                String code = rs.getString("code");
                String subjectAbstract = rs.getString("abstract");

                bodySb.append("{")
                        .append("\"name\":\"").append(escapeJson(name)).append("\",")
                        .append("\"code\":\"").append(escapeJson(code)).append("\",")
                        .append("\"abstract\":\"").append(escapeJson(subjectAbstract)).append("\"")
                        .append("}");
            }

            bodySb.append("]}");
            
            byte[] message = bodySb.toString().getBytes();
            writeHeadersWithBody(writer, 200, message.length, "application/json", null);
            outputStream.write(message);
            outputStream.flush();
        } catch (SQLException e) {
            e.printStackTrace();
            handleInternalServerError();
        }

    }

    private static String escapeJson(String s){
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
