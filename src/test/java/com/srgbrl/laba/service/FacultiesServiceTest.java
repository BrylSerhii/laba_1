package com.srgbrl.laba.service;

import com.srgbrl.laba.dao.FacultyDao;
import com.srgbrl.laba.entity.Applicant;
import com.srgbrl.laba.entity.Faculty;
import com.srgbrl.laba.entity.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacultiesServiceTest {

    @Mock
    private FacultyDao facultyDao;
    
    @Mock
    private ApplicantService applicantService;
    
    private FacultiesService facultiesService;

    @BeforeEach
    void setUp() throws Exception {
        // Get the singleton instance
        facultiesService = FacultiesService.getInstance();
        
        // Use reflection to replace the facultyDao field with our mock
        Field facultyDaoField = FacultiesService.class.getDeclaredField("facultyDao");
        facultyDaoField.setAccessible(true);
        facultyDaoField.set(facultiesService, facultyDao);
        
        // Use reflection to replace the applicantService field with our mock
        Field applicantServiceField = FacultiesService.class.getDeclaredField("applicantService");
        applicantServiceField.setAccessible(true);
        applicantServiceField.set(facultiesService, applicantService);
    }

    @Test
    void testGetAllFaculties() {
        // Setup
        List<Faculty> faculties = Arrays.asList(
                new Faculty(1, "Faculty 1", 50, Status.OPEN),
                new Faculty(2, "Faculty 2", 75, Status.CLOSED)
        );
        
        // Mock behavior
        when(facultyDao.findAll()).thenReturn(faculties);
        
        // Execute
        List<Faculty> result = facultiesService.getAllFaculties();
        
        // Verify
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should find 2 faculties");
        assertEquals("Faculty 1", result.get(0).getName(), "First faculty name should match");
        assertEquals("Faculty 2", result.get(1).getName(), "Second faculty name should match");
        
        // Verify interactions
        verify(facultyDao).findAll();
    }
    
    @Test
    void testGetFacultyById() {
        // Setup
        int facultyId = 1;
        Faculty faculty = new Faculty(facultyId, "Test Faculty", 50, Status.OPEN);
        
        // Mock behavior
        when(facultyDao.findById(facultyId)).thenReturn(faculty);
        
        // Execute
        Faculty result = facultiesService.getFacultyById(facultyId);
        
        // Verify
        assertNotNull(result, "Result should not be null");
        assertEquals(facultyId, result.getId(), "Faculty ID should match");
        assertEquals("Test Faculty", result.getName(), "Faculty name should match");
        assertEquals(50, result.getLimit(), "Faculty limit should match");
        assertEquals(Status.OPEN, result.getStatus(), "Faculty status should match");
        
        // Verify interactions
        verify(facultyDao).findById(facultyId);
    }
    
    @Test
    void testCreateFaculty() {
        // Setup
        String name = "New Faculty";
        int limit = 100;
        
        // Execute
        facultiesService.createFaculty(name, limit);
        
        // Verify interactions
        verify(facultyDao).save(argThat(faculty -> 
                faculty.getName().equals(name) && 
                faculty.getLimit() == limit && 
                faculty.getStatus() == Status.OPEN));
    }
    
    @Test
    void testCloseFaculty() throws SQLException {
        // Setup
        Faculty faculty = new Faculty(1, "Test Faculty", 50, Status.OPEN);
        
        // Execute
        facultiesService.closeFaculty(faculty);
        
        // Verify interactions
        verify(facultyDao).closeFaculty(faculty);
    }
    
    @Test
    void testFindAll() {
        // Setup
        List<Faculty> faculties = Arrays.asList(
                new Faculty(1, "Faculty 1", 50, Status.OPEN),
                new Faculty(2, "Faculty 2", 75, Status.CLOSED)
        );
        
        // Mock behavior
        when(facultyDao.findAll()).thenReturn(faculties);
        
        // Execute
        List<Faculty> result = facultiesService.findAll();
        
        // Verify
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should find 2 faculties");
        assertEquals("Faculty 1", result.get(0).getName(), "First faculty name should match");
        assertEquals("Faculty 2", result.get(1).getName(), "Second faculty name should match");
        
        // Verify interactions
        verify(facultyDao).findAll();
    }
    
    @Test
    void testGetApplicantsByFacultyId() {
        // Setup
        int facultyId = 1;
        List<Applicant> applicants = Arrays.asList(
                new Applicant(1, "John Doe", 4.5, facultyId, 1, Arrays.asList(90, 85, 95), 90.45f),
                new Applicant(2, "Jane Smith", 4.8, facultyId, 2, Arrays.asList(95, 90, 85), 91.48f)
        );
        
        // Mock behavior
        when(applicantService.findAllByFacultyId(facultyId)).thenReturn(applicants);
        
        // Execute
        List<?> result = facultiesService.getApplicantsByFacultyId(facultyId);
        
        // Verify
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should find 2 applicants");
        
        // Verify interactions
        verify(applicantService).findAllByFacultyId(facultyId);
    }
    
    @Test
    void testGetApplicantsByFacultyIdEmpty() {
        // Setup
        int facultyId = 999;
        
        // Mock behavior
        when(applicantService.findAllByFacultyId(facultyId)).thenReturn(Collections.emptyList());
        
        // Execute
        List<?> result = facultiesService.getApplicantsByFacultyId(facultyId);
        
        // Verify
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result should be empty");
        
        // Verify interactions
        verify(applicantService).findAllByFacultyId(facultyId);
    }
}