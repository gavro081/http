package com.gavro.httpserver.database.dao.service;

import com.gavro.httpserver.database.dao.interfaces.SubjectDao;
import com.gavro.httpserver.database.dao.model.Subject;

import java.sql.SQLException;
import java.util.List;

public class SubjectService {
    private final SubjectDao subjectDao;

    public SubjectService(SubjectDao subjectDao) {
        this.subjectDao = subjectDao;
    }

    public List<Subject> getSubjects() throws SQLException{
        return subjectDao.getAll();
    }

    public List<Subject> getSubjects(int n) throws SQLException {
        return subjectDao.getN(n);
    }

    public Subject getSubject(int id) throws SQLException{
        return subjectDao.getById(id);
    }

    public Subject getSubject(String code) throws SQLException{
        return subjectDao.getByCode(code);
    }

    public Subject addSubject(Subject s) throws SQLException{
        return subjectDao.insert(s);
    }

    public boolean updateSubject(Subject s) throws SQLException{
        return subjectDao.update(s);
    }

    public boolean deleteSubject(int id) throws SQLException{
        return subjectDao.delete(id);
    }

    public boolean deleteSubject(String code) throws SQLException{
        return subjectDao.delete(code);
    }
}
