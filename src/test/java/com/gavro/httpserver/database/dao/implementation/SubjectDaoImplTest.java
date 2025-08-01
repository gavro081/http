package com.gavro.httpserver.database.dao.implementation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.gavro.httpserver.database.Database;
import com.gavro.httpserver.database.dao.interfaces.SubjectDao;
import com.gavro.httpserver.database.dao.model.Subject;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SubjectDaoImplTest {
    private static SubjectDao subjectDao;
    private static Connection connection;
    
    private static final Subject TEST_SUBJECT_1 = new Subject("Test Abstract 1", "TEST001", "Test Subject 1");
    private static final Subject TEST_SUBJECT_2 = new Subject("Test Abstract 2", "TEST002", "Test Subject 2");
    private static final Subject TEST_SUBJECT_3 = new Subject("Test Abstract 3", "TEST003", "Test Subject 3");
    
    @BeforeAll
    static void setUpClass() throws SQLException {
        Database.init();
        connection = Database.getConnection();
        subjectDao = new SubjectDaoImpl(connection);
        
        cleanupTestData();
    }
    
    @AfterAll
    static void tearDownClass() throws SQLException {
        cleanupTestData();
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        Database.shutdown();
    }
    
    @AfterEach
    void tearDown() {
        cleanupTestData();
    }
    
    private static void cleanupTestData() {
        try {
            subjectDao.delete("TEST001");
            subjectDao.delete("TEST002");
            subjectDao.delete("TEST003");
            subjectDao.delete("UPDATED_CODE");
            subjectDao.delete("TEST_DELETE");
        } catch (Exception e) {
            // data might not exist, ignore
        }
    }
    
    @Test
    @Order(1)
    @DisplayName("Test delete by code function works properly")
    void testDeleteByCode() {
        subjectDao.insert(TEST_SUBJECT_1);

        Subject retrieved = subjectDao.getByCode("TEST001");
        assertNotNull(retrieved, "Subject should exist before deletion");
        assertEquals("TEST001", retrieved.getCode());

        subjectDao.delete("TEST001");

        Subject deletedSubject = subjectDao.getByCode("TEST001");
        assertNull(deletedSubject, "Subject should be null after deletion");
    }
    
    @Test
    @Order(2)
    @DisplayName("Test delete by ID function works properly")
    void testDeleteById() {
        subjectDao.insert(TEST_SUBJECT_2);

        Subject retrieved = subjectDao.getByCode("TEST002");
        assertNotNull(retrieved, "Subject should exist before deletion");
        int id = retrieved.getId();

        subjectDao.delete(id);

        Subject deletedSubject = subjectDao.getById(id);
        assertNull(deletedSubject, "Subject should be null after deletion by ID");
    }
    
    @Test
    @Order(3)
    @DisplayName("Test insert function")
    void testInsert() {
        subjectDao.insert(TEST_SUBJECT_1);

        Subject retrieved = subjectDao.getByCode("TEST001");
        assertNotNull(retrieved, "Inserted subject should be retrievable");
        assertEquals("Test Subject 1", retrieved.getName());
        assertEquals("TEST001", retrieved.getCode());
        assertEquals("Test Abstract 1", retrieved.getAbstract());
        assertTrue(retrieved.getId() > 0, "Subject should have a valid ID");
    }
    
    @Test
    @Order(4)
    @DisplayName("Test getByCode function")
    void testGetByCode() {
        subjectDao.insert(TEST_SUBJECT_1);

        Subject retrieved = subjectDao.getByCode("TEST001");
        assertNotNull(retrieved, "Should retrieve existing subject");
        assertEquals("TEST001", retrieved.getCode());
        assertEquals("Test Subject 1", retrieved.getName());
        assertEquals("Test Abstract 1", retrieved.getAbstract());

        Subject nonExistent = subjectDao.getByCode("NONEXISTENT");
        assertNull(nonExistent, "Should return null for non-existent code");
    }
    
    @Test
    @Order(5)
    @DisplayName("Test getById function")
    void testGetById() {
        subjectDao.insert(TEST_SUBJECT_1);

        Subject inserted = subjectDao.getByCode("TEST001");
        assertNotNull(inserted, "Test setup failed - subject should exist");
        int id = inserted.getId();

        Subject retrieved = subjectDao.getById(id);
        assertNotNull(retrieved, "Should retrieve existing subject by ID");
        assertEquals(id, retrieved.getId());
        assertEquals("TEST001", retrieved.getCode());
        assertEquals("Test Subject 1", retrieved.getName());
        assertEquals("Test Abstract 1", retrieved.getAbstract());

        Subject nonExistent = subjectDao.getById(99999);
        assertNull(nonExistent, "Should return null for non-existent ID");
    }
    
    @Test
    @Order(6)
    @DisplayName("Test getAll function")
    void testGetAll() {
        subjectDao.insert(TEST_SUBJECT_1);
        subjectDao.insert(TEST_SUBJECT_2);
        subjectDao.insert(TEST_SUBJECT_3);

        List<Subject> allSubjects = subjectDao.getAll();
        assertNotNull(allSubjects, "Should return a list");
        assertTrue(allSubjects.size() >= 3, "Should contain at least our 3 test subjects");

        boolean foundSubject1 = allSubjects.stream().anyMatch(s -> "TEST001".equals(s.getCode()));
        boolean foundSubject2 = allSubjects.stream().anyMatch(s -> "TEST002".equals(s.getCode()));
        boolean foundSubject3 = allSubjects.stream().anyMatch(s -> "TEST003".equals(s.getCode()));
        
        assertTrue(foundSubject1, "Should contain TEST001");
        assertTrue(foundSubject2, "Should contain TEST002");
        assertTrue(foundSubject3, "Should contain TEST003");
    }
    
    @Test
    @Order(7)
    @DisplayName("Test getN function")
    void testGetN() {
        subjectDao.insert(TEST_SUBJECT_1);
        subjectDao.insert(TEST_SUBJECT_2);
        subjectDao.insert(TEST_SUBJECT_3);

        List<Subject> limitedSubjects = subjectDao.getN(2);
        assertNotNull(limitedSubjects, "Should return a list");
        assertEquals(2, limitedSubjects.size(), "Should return exactly 2 subjects");

        List<Subject> allAvailable = subjectDao.getN(1000);
        assertNotNull(allAvailable, "Should return a list");
        assertTrue(allAvailable.size() >= 3, "Should return at least our 3 test subjects");

        List<Subject> zeroSubjects = subjectDao.getN(0);
        assertNotNull(zeroSubjects, "Should return a list");
        assertEquals(0, zeroSubjects.size(), "Should return empty list for limit 0");
    }
    
    @Test
    @Order(8)
    @DisplayName("Test update function")
    void testUpdate() {
        subjectDao.insert(TEST_SUBJECT_1);

        Subject original = subjectDao.getByCode("TEST001");
        assertNotNull(original, "Test setup failed - subject should exist");

        Subject updatedSubject = new Subject(original.getId(), "Updated Abstract", "UPDATED_CODE", "Updated Name");

        subjectDao.update(updatedSubject);

        Subject retrieved = subjectDao.getByCode("UPDATED_CODE");
        assertNotNull(retrieved, "Updated subject should be retrievable with new code");
        assertEquals(original.getId(), retrieved.getId(), "ID should remain the same");
        assertEquals("Updated Name", retrieved.getName(), "Name should be updated");
        assertEquals("UPDATED_CODE", retrieved.getCode(), "Code should be updated");
        assertEquals("Updated Abstract", retrieved.getAbstract(), "Abstract should be updated");

        Subject oldCodeSubject = subjectDao.getByCode("TEST001");
        assertNull(oldCodeSubject, "Subject with old code should no longer exist");

        Subject retrievedById = subjectDao.getById(original.getId());
        assertNotNull(retrievedById, "Subject should still be retrievable by ID");
        assertEquals("UPDATED_CODE", retrievedById.getCode(), "Retrieved by ID should have updated code");
    }
    
    @Test
    @Order(9)
    @DisplayName("Test database consistency across multiple operations")
    void testDatabaseConsistency() {
        subjectDao.insert(TEST_SUBJECT_1);

        List<Subject> beforeUpdate = subjectDao.getAll();
        int initialCount = beforeUpdate.size();

        Subject original = subjectDao.getByCode("TEST001");
        Subject updated = new Subject(original.getId(), "Modified Abstract", "TEST001", "Modified Name");
        subjectDao.update(updated);

        List<Subject> afterUpdate = subjectDao.getAll();
        assertEquals(initialCount, afterUpdate.size(), "Count should remain same after update");

        Subject retrievedAfterUpdate = subjectDao.getByCode("TEST001");
        assertEquals("Modified Name", retrievedAfterUpdate.getName(), "Update should be reflected");

        subjectDao.delete("TEST001");

        List<Subject> afterDelete = subjectDao.getAll();
        assertEquals(initialCount - 1, afterDelete.size(), "Count should decrease after delete");
    }
    
    @Test
    @Order(10)
    @DisplayName("Test edge cases and error conditions")
    void testEdgeCases() {
        assertNull(subjectDao.getByCode("NONEXISTENT"), "Should return null for non-existent code");
        assertNull(subjectDao.getById(-1), "Should return null for negative ID");
        assertNull(subjectDao.getById(0), "Should return null for zero ID");

        assertDoesNotThrow(() -> subjectDao.delete("NONEXISTENT"), "Deleting non-existent code should not throw");
        assertDoesNotThrow(() -> subjectDao.delete(-1), "Deleting non-existent ID should not throw");

        Subject specialCharSubject = new Subject("Abstract with 'quotes' and \"double quotes\" as well as букви на кирилица!@#$%\n\t^&(",
                                                "TEST_SPECIAL", "Name with symbols ВГД!@#$%");
        assertDoesNotThrow(() -> subjectDao.insert(specialCharSubject), "Should handle special characters");
        
        Subject retrieved = subjectDao.getByCode("TEST_SPECIAL");
        assertNotNull(retrieved, "Should retrieve subject with special characters");
        assertEquals("Name with symbols ВГД!@#$%", retrieved.getName(), "Special characters should be preserved");

        subjectDao.delete("TEST_SPECIAL");
    }
}
