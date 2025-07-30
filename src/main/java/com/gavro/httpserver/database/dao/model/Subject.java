package com.gavro.httpserver.database.dao.model;

import com.gavro.httpserver.utils.JsonResponseBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Subject fromJson(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
            String[] entries = json.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            for (String entry : entries) {
                String[] kv = entry.split(":(?=([^\"]*\"[^\"]*\")*[^\"]*$)", 2);
                if (kv.length == 2) {
                    String key = kv[0].trim().replaceAll("^\"|\"$", "");
                    String value = kv[1].trim().replaceAll("^\"|\"$", "");
                    map.put(key, value);
                }
            }
        }
        return new Subject(
                map.getOrDefault("name", ""),
                map.getOrDefault("code", ""),
                map.getOrDefault("abstract", "")
        );
    }

}
