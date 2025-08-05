package com.gavro.httpserver.database.dao.model;

import java.util.List;

import org.json.JSONObject;

import com.gavro.httpserver.utils.JsonResponseBuilder;

public class Subject {
    private int id;
    private final String name;
    private final String code;
    private final String abstract_;

    public Subject(int id, String abstract_, String code, String name) {
        this.id = id;
        this.abstract_ = validateNotEmpty(abstract_, "abstract");
        this.code = validateNotEmpty(code, "code");
        this.name = validateNotEmpty(name, "name");
    }

    public Subject(String abstract_, String code, String name){
        this.abstract_ = validateNotEmpty(abstract_, "abstract");
        this.code = validateNotEmpty(code, "code");
        this.name = validateNotEmpty(name, "name");
    }

    private String validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
        return value.trim();
    }

    public int getId() {
        return id;
    }

    public String getAbstract() {
        return abstract_;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String toJson(Subject subject){
        if (subject == null) {
            throw new IllegalArgumentException("Subject cannot be null");
        }
        return String.format(
                "{\"id\":\"%s\",\"name\":\"%s\",\"code\":\"%s\",\"abstract\":\"%s\"}",
                JsonResponseBuilder.escapeJson(Integer.toString(subject.getId())),
                JsonResponseBuilder.escapeJson(subject.getName()),
                JsonResponseBuilder.escapeJson(subject.getCode()),
                JsonResponseBuilder.escapeJson(subject.getAbstract())
        );
    }

    public static String toJson(List<Subject> subjects) {
        if (subjects == null) {
            throw new IllegalArgumentException("Subjects list cannot be null");
        }
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Subject s : subjects){
            if (!first) sb.append(",");
            else first = false;
            sb.append(toJson(s));
        }
        sb.append("]");
        return sb.toString();
    }

    public static Subject fromJson(String jsonInput) {
        if (jsonInput == null || jsonInput.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON input cannot be null or empty");
        }
        
        try {
            JSONObject json = new JSONObject(jsonInput);
            String name = json.getString("name");
            String code = json.getString("code");
            String abstractText = json.getString("abstract");

            return new Subject(abstractText, code, name);
        } catch (org.json.JSONException e) {
            throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw e; 
        } catch (Exception e) {
            throw new IllegalArgumentException("Unexpected error parsing JSON", e);
        }
    }

    public static Subject fromJsonWithId(String jsonInput) {
        if (jsonInput == null || jsonInput.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON input cannot be null or empty");
        }
        
        try {
            JSONObject json = new JSONObject(jsonInput);
            int id = json.getInt("id");
            String name = json.getString("name");
            String code = json.getString("code");
            String abstractText = json.getString("abstract");

            return new Subject(id, abstractText, code, name);
        } catch (org.json.JSONException e) {
            throw new IllegalArgumentException("Invalid JSON format: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw e; 
        } catch (Exception e) {
            throw new IllegalArgumentException("Unexpected error parsing JSON", e);
        }
    }
}
