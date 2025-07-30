package com.gavro.httpserver.database.dao.model;

class Prerequisites{
    private int[] subjectIds;
    private int credits;
}

public class SubjectInfo {
    private int subject_id; // pk and fk
    private int level;
    private Prerequisites prerequisites;
    private boolean activated;
    private int[] participants;
    private boolean mandatory;
    private String[] mandatory_for;
    private int semester;
    private String season;
    private String[] elective_for;
    private String[] professors;
    private String[] assistants;
    private String[] tags;
    private String[] evaluation;
    private String[] technologies;
    private boolean is_easy;
}
