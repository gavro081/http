package com.gavro.httpserver.database.dao.service;

import com.gavro.httpserver.database.dao.implementation.SubjectDaoImpl;
import com.gavro.httpserver.database.dao.interfaces.SubjectDao;
import com.gavro.httpserver.database.dao.model.Subject;

import java.util.List;

public class SubjectService {
    private final SubjectDao subjectDao;

    public SubjectService(SubjectDao subjectDao) {
        this.subjectDao = subjectDao;
    }

    public List<Subject> getSubjects(){
        return subjectDao.getAll();
    }

    public List<Subject> getSubjects(int n){
        return subjectDao.getN(n);
    }

    public Subject getSubject(int id){
        return subjectDao.getById(id);
    }

    public Subject getSubject(String code){
        return subjectDao.getByCode(code);
    }

    public void addSubject(Subject s){
        // todo: make return boolean status or the added subject
        subjectDao.insert(s);
    }

    public void updateSubject(Subject s){
        subjectDao.update(s);
    }

    public void deleteSubject(int id){
        subjectDao.delete(id);
    }

    public void deleteSubject(String code){
        subjectDao.delete(code);
    }
}
