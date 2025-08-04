package com.gavro.httpserver.database.dao.model;

import com.gavro.httpserver.utils.JsonResponseBuilder;
import org.json.JSONObject;

import java.util.List;

public class Subject {
    private int id;
    private final String name;
    private final String code;
    private final String abstract_;

    public Subject(int id, String abstract_, String code, String name) {
        this.id = id;
        this.abstract_ = abstract_;
        this.code = code;
        this.name = name;
    }

    public Subject(String abstract_, String code, String name){
        this.abstract_ = abstract_;
        this.code = code;
        this.name = name;
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
        return String.format(
                "{\"name\":\"%s\",\"code\":\"%s\",\"abstract\":\"%s\"}",
                JsonResponseBuilder.escapeJson(subject.getName()),
                JsonResponseBuilder.escapeJson(subject.getCode()),
                JsonResponseBuilder.escapeJson(subject.getAbstract())
        );
    }

    public static String toJson(List<Subject> subjects) {
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
        JSONObject json = new JSONObject(jsonInput);
        String name = json.getString("name");
        String code = json.getString("code");
        String abstractText = json.getString("abstract");

        return new Subject(abstractText, code, name);
    }

    public static Subject fromJsonWithId(String jsonInput) {
        JSONObject json = new JSONObject(jsonInput);
        int id = json.getInt("id");
        String name = json.getString("name");
        String code = json.getString("code");
        String abstractText = json.getString("abstract");

        return new Subject(id, abstractText, code, name);
    }

}
