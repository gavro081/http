package com.gavro.httpserver.database.dao.interfaces;

import com.gavro.httpserver.database.dao.model.Subject;

import java.sql.SQLException;
import java.util.List;

public interface SubjectDao {
    // GET api/subjects/
    List<Subject> getAll() throws SQLException;
    // GET api/subjects?limit=<number> -> TODO*: add pagination
    List<Subject> getN(int n) throws SQLException;
    // GET api/subjects/code/{code}
    Subject getByCode(String code) throws SQLException;
    // GET api/subjects/{id}
    Subject getById(int id) throws SQLException;
    // POST api/subjects/
    // body: {subject: [abstract: <abs>, code: <code>: name: <name>] }
    Subject insert(Subject subject) throws SQLException;
    // PUT api/subjects
    // body: {subject: [id: <pk>, abstract: <abs>, code: <code>: name: <name>] }
    boolean update(Subject subject) throws SQLException;
    // DELETE api/subjects/{id}
    boolean delete(String code) throws SQLException;
    // DELETE api/subjects/code/{code}
    boolean delete(int id) throws SQLException;
}