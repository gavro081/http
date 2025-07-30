package com.gavro.httpserver.database.dao.interfaces;

import com.gavro.httpserver.database.dao.model.Subject;

import java.util.List;

public interface SubjectDao {
    // GET api/subjects/
    List<Subject> getAll();
    // GET api/subjects?limit=<number> -> potentially extend to pagination
    List<Subject> getN(int n);
    // GET api/subjects/code/{code}
    Subject getByCode(String code);
    // GET api/subjects/{id}
    Subject getById(int id);
    // POST api/subjects/
    // body: {subject: [abstract: <abs>, code: <code>: name: <name>] }
    void insert(Subject subject);
    // PUT api/subjects/
    // body: {subject: [abstract: <abs>, code: <code>: name: <name>] }
    void update(Subject subject);
    // DELETE api/subjects/{id}
    void delete(String code);
    // DELETE api/subjects/code/{code}
    void delete(int id);
}