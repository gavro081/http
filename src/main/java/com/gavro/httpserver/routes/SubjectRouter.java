package com.gavro.httpserver.routes;

import com.gavro.httpserver.database.dao.model.Subject;
import com.gavro.httpserver.database.dao.service.SubjectService;
import com.gavro.httpserver.exceptions.BadRequestException;
import com.gavro.httpserver.http.HttpMethod;
import com.gavro.httpserver.utils.JsonRouteResult;

import java.util.List;
import java.util.Map;

public class SubjectRouter {
    private final SubjectService subjectService;

    public SubjectRouter(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    public JsonRouteResult handle(HttpMethod method, String route, Map<String, String> queryParams, String requestBody) {
        if (!route.startsWith("/api/subjects")) {
            return new JsonRouteResult(404, "{\"error\": \"Path not found.\"}");
        }
        // TODO: clean up code so that only one of these appears
        if (route.equals("/api/subjects/") || route.equals("/api/subjects")) {
            switch (method) {
                case GET -> {
                    if (queryParams.containsKey("limit")) {
                        try {
                            int limit = Integer.parseInt(queryParams.get("limit"));
                            List<Subject> subjects = subjectService.getSubjects(limit);
                            return new JsonRouteResult(200, Subject.toJson(subjects));
                        } catch (NumberFormatException e) {
                            return new JsonRouteResult(400, "Invalid parameters.");
                        }
                    } else {
                        List<Subject> subjects = subjectService.getSubjects();
                        return new JsonRouteResult(200, Subject.toJson(subjects));
                    }
                }
                case POST -> {
                    Subject s = Subject.fromJson(requestBody);
                    subjectService.addSubject(s);
                    return new JsonRouteResult(201, "{\"message\": \"Subject added\"}");
                }
                case PUT -> {
                    Subject s = Subject.fromJson(requestBody);
                    subjectService.updateSubject(s);
                    return new JsonRouteResult(200, "{\"message\": \"Subject updated\"}");
                }
                default -> {
                    return new JsonRouteResult(405, "{\"error\": \"Method not allowed.\"}");
                }
            }

        } else if (route.startsWith("/api/subjects/code/")) {
            String code = route.substring("/api/subjects/code/".length());
            if (code.isEmpty()) {
                return new JsonRouteResult(400, "Bad request.");
            } else {
                switch (method) {
                    case GET -> {
                        Subject s = subjectService.getSubject(code);
                        return new JsonRouteResult(200, Subject.toJson(s));
                    }
                    case DELETE -> {
                        subjectService.deleteSubject(code);
                        return new JsonRouteResult(200, "{\"message\": \"Subject deleted\"}");
                    }
                    default -> {
                        return new JsonRouteResult(405, "{\"error\": \"Method not allowed.\"}");
                    }
                }
            }
            // todo: never makes it here because of query extraction, fix it
        } else if (route.startsWith("/api/subjects/")) {
            try {
                int id = Integer.parseInt(route.substring("/api/subjects/".length()));
                switch (method) {
                    case GET -> {
                        Subject s = subjectService.getSubject(id);
                        return new JsonRouteResult(200, Subject.toJson(s));
                    }
                    case DELETE -> {
                        subjectService.deleteSubject(id);
                        return new JsonRouteResult(200, "{\"message\": \"Subject updated\"}");
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
