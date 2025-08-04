package com.gavro.httpserver.database.dao.implementation;

import com.gavro.httpserver.database.dao.interfaces.SubjectDao;
import com.gavro.httpserver.database.dao.model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDaoImpl implements SubjectDao {
    private final Connection connection;

    public SubjectDaoImpl(Connection connection){
        this.connection = connection;
    }

    private List<Subject> subjectsRsToList(ResultSet rs) throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        while (rs.next()){
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String code = rs.getString("code");
            String subjectAbstract = rs.getString("abstract");
            subjects.add(new Subject(id, subjectAbstract, code, name));
        }
        return subjects;
    }

    @Override
    public List<Subject> getAll() throws SQLException{
        String sql = "SELECT * FROM subject";
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)
        ) {
            return subjectsRsToList(rs);
        }
    }

    @Override
    public List<Subject> getN(int n) throws SQLException{
        String sql = "SELECT * FROM subject LIMIT ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, n);
            try (ResultSet rs = statement.executeQuery()) {
                return subjectsRsToList(rs);
            }
        }
    }
    @Override
    public Subject getByCode(String code) throws SQLException{
        String sql = "SELECT * FROM subject WHERE code = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, code);
            try (ResultSet rs = statement.executeQuery()) {
                List<Subject> list = subjectsRsToList(rs);
                return list.isEmpty() ? null : list.getFirst();
            }
        }
    }
    @Override
    public Subject getById(int id) throws SQLException{
        String sql = "SELECT * FROM subject WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                List<Subject> list = subjectsRsToList(rs);
                return list.isEmpty() ? null : list.getFirst();
            }
        }
    }
    @Override
    public Subject insert(Subject subject) throws SQLException{
        String sql = """
                INSERT INTO subject (name, code, abstract)
                values (?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, subject.getName());
            statement.setString(2, subject.getCode());
            statement.setString(3, subject.getAbstract());
            boolean success = statement.executeUpdate() > 0;
            return success ? subject : null;
        }
    }
    @Override
    public boolean update(Subject subject) throws SQLException{
        String sql = """
                UPDATE subject SET name = ?, code = ?, abstract = ?
                WHERE id = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, subject.getName());
            statement.setString(2, subject.getCode());
            statement.setString(3, subject.getAbstract());
            statement.setInt(4, subject.getId());
            int rows = statement.executeUpdate();
            return rows > 0;
        }
    }
    @Override
    public boolean delete(String code) throws SQLException{
        String sql = """
                DELETE FROM subject WHERE code = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, code);
            int rows = statement.executeUpdate();
            return rows > 0;
        }
    }
    @Override
    public boolean delete(int id) throws SQLException{
        String sql = """
                DELETE FROM subject WHERE id = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, id);
            int rows = statement.executeUpdate();
            return rows > 0;
        }
    }

}
