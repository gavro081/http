package com.gavro.httpserver.routes;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.gavro.httpserver.database.dao.model.Subject;
import com.gavro.httpserver.database.dao.service.SubjectService;
import com.gavro.httpserver.http.HttpMethod;
import com.gavro.httpserver.utils.JsonRouteResult;

public class SubjectRouter {
    // message constants
    // errors
    private static final String ERROR_INVALID_LIMIT = "{\"error\": \"Invalid limit parameter.\"}";
    private static final String ERROR_INTERNAL_SERVER = "{\"error\": \"Internal server error.\"}";
    private static final String ERROR_METHOD_NOT_ALLOWED = "{\"error\": \"Method not allowed.\"}";
    private static final String ERROR_BAD_REQUEST = "{\"error\": \"Bad request.\"}";
    private static final String ERROR_SUBJECT_NOT_FOUND = "{\"error\": \"Subject not found\"}";
    private static final String ERROR_PATH_NOT_FOUND = "{\"error\": \"Path not found.\"}";
    private static final String ERROR_INVALID_DATA = "{\"error\": \"Invalid data\"}";
    private static final String ERROR_SUBJECT_EXISTS = "{\"error\": \"Subject with this code already exists\"}";
    private static final String ERROR_FAILED_CREATE = "{\"error\": \"Failed to create subject\"}";
    
    // success
    private static final String MESSAGE_SUBJECT_ADDED = "{\"message\": \"Subject added\"}";
    private static final String MESSAGE_SUBJECT_UPDATED = "{\"message\": \"Subject updated\"}";
    private static final String MESSAGE_SUBJECT_DELETED = "{\"message\": \"Subject deleted\"}";
    
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
                            return new JsonRouteResult(400, ERROR_INVALID_LIMIT);
                        } catch (SQLException e){
                            return new JsonRouteResult(500, ERROR_INTERNAL_SERVER);
                        }
                    } else {
                        try {
                            List<Subject> subjects = subjectService.getSubjects();
                            return new JsonRouteResult(200, "{\"data\":" + Subject.toJson(subjects) + "}");
                        } catch (SQLException e) {
                            return new JsonRouteResult(500, ERROR_INTERNAL_SERVER);
                        }
                    }
                }
                case POST -> {
                    try {
                        boolean res = subjectService.addSubject(Subject.fromJson(requestBody));
                        if (res) {
                            return new JsonRouteResult(201, MESSAGE_SUBJECT_ADDED);
                        } else {
                            return new JsonRouteResult(500, ERROR_FAILED_CREATE);
                        }
                    } catch (SQLException e) {
                        String errorMessage = e.getMessage().toLowerCase();
                        if (errorMessage.contains("duplicate") || errorMessage.contains("unique")) {
                            return new JsonRouteResult(409, ERROR_SUBJECT_EXISTS);
                        }
                        return new JsonRouteResult(500, ERROR_INTERNAL_SERVER);
                    } catch (Exception e){
                        return new JsonRouteResult(400, ERROR_INVALID_DATA);
                    }
                }
                case PUT -> {
                    try {
                        Subject s = Subject.fromJsonWithId(requestBody);
                        boolean updated = subjectService.updateSubject(s);
                        if (updated) {
                            return new JsonRouteResult(200, MESSAGE_SUBJECT_UPDATED);
                        } else {
                            return new JsonRouteResult(404, ERROR_SUBJECT_NOT_FOUND);
                        }
                    } catch (SQLException e) {
                        return new JsonRouteResult(500, ERROR_INTERNAL_SERVER);
                    } catch (Exception e){
                        return new JsonRouteResult(400, ERROR_INVALID_DATA);
                    }
                }
                default -> {
                    return new JsonRouteResult(405, ERROR_METHOD_NOT_ALLOWED);
                }
            }

        } else if (route.startsWith("/api/subjects/code/")) {
            String code = route.substring("/api/subjects/code/".length());
            if (code.isEmpty()) {
                return new JsonRouteResult(400, ERROR_BAD_REQUEST);
            } else {
                switch (method) {
                    case GET -> {
                        try {
                            Subject s = subjectService.getSubject(code);
                            if (s == null) return new JsonRouteResult(404, ERROR_SUBJECT_NOT_FOUND);
                            return new JsonRouteResult(200, "{\"data\":" + Subject.toJson(s) + "}");
                        } catch (SQLException e) {
                            return new JsonRouteResult(500, ERROR_INTERNAL_SERVER);
                        }
                    }
                    case DELETE -> {
                        try {
                            boolean success = subjectService.deleteSubject(code);
                            if (success)
                                return new JsonRouteResult(200, MESSAGE_SUBJECT_DELETED);
                            else
                                return new JsonRouteResult(404, ERROR_SUBJECT_NOT_FOUND);
                        } catch (SQLException e) {
                            return new JsonRouteResult(500, ERROR_INTERNAL_SERVER);
                        }
                    }
                    default -> {
                        return new JsonRouteResult(405, ERROR_METHOD_NOT_ALLOWED);
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
                            if (s == null) return new JsonRouteResult(404, ERROR_SUBJECT_NOT_FOUND);
                            return new JsonRouteResult(200, "{\"data\":" + Subject.toJson(s) + "}");
                        } catch (SQLException e) {
                            return new JsonRouteResult(500, ERROR_INTERNAL_SERVER);
                        }
                    }
                    case DELETE -> {
                        try {
                            boolean success = subjectService.deleteSubject(id);
                            if (success)
                                return new JsonRouteResult(200, MESSAGE_SUBJECT_DELETED);
                            else
                                return new JsonRouteResult(404, ERROR_SUBJECT_NOT_FOUND);
                        } catch (SQLException e) {
                            return new JsonRouteResult(500, ERROR_INTERNAL_SERVER);
                        }
                    }
                    default -> {
                        return new JsonRouteResult(405, ERROR_METHOD_NOT_ALLOWED);
                    }
                }
            } catch (NumberFormatException e) {
                return new JsonRouteResult(404, ERROR_SUBJECT_NOT_FOUND);
            }
        } else {
            return new JsonRouteResult(404, ERROR_PATH_NOT_FOUND);
        }
    }
}
