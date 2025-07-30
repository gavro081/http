package com.gavro.httpserver.database.dao.interfaces;

import com.gavro.httpserver.database.dao.model.SubjectInfo;

import java.util.List;

public interface SubjectInfoDao {
    SubjectInfo getValue(String key);
    void insert(SubjectInfo info);
    void update(SubjectInfo info);
}
