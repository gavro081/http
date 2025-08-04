package com.gavro.httpserver.routes;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.gavro.httpserver.database.dao.model.Subject;
import com.gavro.httpserver.database.dao.service.SubjectService;
import com.gavro.httpserver.http.HttpMethod;
import com.gavro.httpserver.utils.JsonRouteResult;

public class SubjectRouter {
    private final SubjectService subjectService;

    public SubjectRouter(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    public JsonRouteResult handle(HttpMethod method, String route, Map<String, String> queryParams, String requestBody) {
        if (route.equals("/api/subjects")) {
            switch (method) {
                case GET -> {
                    if (queryParams.containsKey("limit")) {
                        try {
                            int limit = Integer.parseInt(queryParams.get("limit"));
                            List<Subject> subjects = subjectService.getSubjects(limit);
                            return new JsonRouteResult(200, "{\"data\":" + Subject.toJson(subjects) + "}");
                        } catch (NumberFormatException e) {
                            return new JsonRouteResult(400, "{\"error\": \"Invalid limit parameter.\"}");
                        } catch (SQLException e){
                            return new JsonRouteResult(500, "{\"error\": \"Internal server error.\"}");
                        }
                    } else {
                        try {
                            List<Subject> subjects = subjectService.getSubjects();
                            return new JsonRouteResult(200, "{\"data\":" + Subject.toJson(subjects) + "}");
                        } catch (SQLException e) {
                            return new JsonRouteResult(500, "{\"error\": \"Internal server error.\"}");
                        }
                    }
                }
                case POST -> {
                    try {
                        Subject s = Subject.fromJson(requestBody);
                        Subject res = subjectService.addSubject(s);
                        if (res != null) {
                            return new JsonRouteResult(201, "{\"message\": \"Subject added\", " +
                                    "\"subject\": "+ Subject.toJson(res) +" }");
                        } else {
                            return new JsonRouteResult(500, "{\"error\": \"Failed to create subject\"}");
                        }
                    } catch (SQLException e) {
                        String errorMessage = e.getMessage().toLowerCase();
                        if (errorMessage.contains("duplicate") || errorMessage.contains("unique")) {
                            return new JsonRouteResult(409, "{\"error\": \"Subject with this code already exists\"}");
                        }
                        return new JsonRouteResult(500, "{\"error\": \"Internal server error.\"}");
                    } catch (Exception e){
                        return new JsonRouteResult(400, "{\"error\": \"Invalid data\"}");
                    }
                }
                case PUT -> {
                    try {
                        Subject s = Subject.fromJsonWithId(requestBody);
                        boolean updated = subjectService.updateSubject(s);
                        if (updated) {
                            return new JsonRouteResult(200, "{\"message\": \"Subject updated\"}");
                        } else {
                            return new JsonRouteResult(404, "{\"error\": \"Subject not found\"}");
                        }
                    } catch (SQLException e) {
                        return new JsonRouteResult(500, "{\"error\": \"Internal server error.\"}");
                    } catch (Exception e){
                        return new JsonRouteResult(400, "{\"error\": \"Invalid data\"}");
                    }
                }
                default -> {
                    return new JsonRouteResult(405, "{\"error\": \"Method not allowed.\"}");
                }
            }

        } else if (route.startsWith("/api/subjects/code/")) {
            String code = route.substring("/api/subjects/code/".length());
            if (code.isEmpty()) {
                return new JsonRouteResult(400, "{\"error\": \"Bad request.\"}");
            } else {
                switch (method) {
                    case GET -> {
                        try {
                            Subject s = subjectService.getSubject(code);
                            if (s == null) return new JsonRouteResult(404, "{\"error\": \"Subject not found\"}");
                            return new JsonRouteResult(200, "{\"data\":" + Subject.toJson(s) + "}");
                        } catch (SQLException e) {
                            return new JsonRouteResult(500, "{\"error\": \"Internal server error.\"}");
                        }
                    }
                    case DELETE -> {
                        try {
                            boolean success = subjectService.deleteSubject(code);
                            if (success)
                                return new JsonRouteResult(200, "{\"message\": \"Subject deleted\"}");
                            else
                                return new JsonRouteResult(404, "{\"error\": \"Subject not found.\"}");
                        } catch (SQLException e) {
                            return new JsonRouteResult(500, "{\"error\": \"Internal server error.\"}");
                        }
                    }
                    default -> {
                        return new JsonRouteResult(405, "{\"error\": \"Method not allowed.\"}");
                    }
                }
            }
        } else if (route.startsWith("/api/subjects")) {
            try {
                int id = Integer.parseInt(route.substring("/api/subjects/".length()));
                switch (method) {
                    case GET -> {
                        try {
                            Subject s = subjectService.getSubject(id);
                            if (s == null) return new JsonRouteResult(404, "{\"error\": \"Subject not found\"}");
                            return new JsonRouteResult(200, "{\"data\":" + Subject.toJson(s) + "}");
                        } catch (SQLException e) {
                            return new JsonRouteResult(500, "{\"error\": \"Internal server error.\"}");
                        }
                    }
                    case DELETE -> {
                        try {
                            boolean success = subjectService.deleteSubject(id);
                            if (success)
                                return new JsonRouteResult(200, "{\"message\": \"Subject deleted\"}");
                            else
                                return new JsonRouteResult(404, "{\"error\": \"Subject not found.\"}");
                        } catch (SQLException e) {
                            return new JsonRouteResult(500, "{\"error\": \"Internal server error.\"}");
                        }
                    }
                    default -> {
                        return new JsonRouteResult(405, "{\"error\": \"Method not allowed.\"}");
                    }
                }
            } catch (NumberFormatException e) {
                return new JsonRouteResult(404, "{\"error\": \"Subject not found.\"}");
            }
        } else {
            return new JsonRouteResult(404, "{\"error\": \"Path not found.\"}");
        }
    }
}
