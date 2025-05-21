package com.srgbrl.laba.service;

import com.srgbrl.laba.dao.ApplicantDao;
import com.srgbrl.laba.entity.Applicant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicantServiceTest {

    @Mock
    private ApplicantDao applicantDao;
    
    private ApplicantService applicantService;

    @BeforeEach
    void setUp() throws Exception {
        // Get the singleton instance
        applicantService = ApplicantService.getInstance();
        
        // Use reflection to replace the applicantDao field with our mock
        Field applicantDaoField = ApplicantService.class.getDeclaredField("applicantDao");
        applicantDaoField.setAccessible(true);
        applicantDaoField.set(applicantService, applicantDao);
    }

    @Test
    void testHasAlreadyAppliedTrue() {
        // Setup
        int userId = 1;
        int facultyId = 2;
        Applicant applicant = new Applicant(
                1, 
                "John Doe", 
                4.5, 
                facultyId, 
                userId, 
                Arrays.asList(90, 85, 95),
                90.45f
        );
        
        // Mock behavior
        when(applicantDao.findByUserIdFacultyId(userId, facultyId)).thenReturn(applicant);
        
        // Execute
        boolean result = applicantService.hasAlreadyApplied(userId, facultyId);
        
        // Verify
        assertTrue(result, "User should have already applied");
        
        // Verify interactions
        verify(applicantDao).findByUserIdFacultyId(userId, facultyId);
    }
    
    @Test
    void testHasAlreadyAppliedFalse() {
        // Setup
        int userId = 1;
        int facultyId = 2;
        
        // Mock behavior
        when(applicantDao.findByUserIdFacultyId(userId, facultyId)).thenReturn(null);
        
        // Execute
        boolean result = applicantService.hasAlreadyApplied(userId, facultyId);
        
        // Verify
        assertFalse(result, "User should not have already applied");
        
        // Verify interactions
        verify(applicantDao).findByUserIdFacultyId(userId, facultyId);
    }
    
    @Test
    void testSaveApplicant() {
        // Setup
        Applicant applicant = new Applicant(
                "Jane Smith", 
                4.8, 
                2, 
                1, 
                Arrays.asList(95, 90, 85)
        );
        
        // Execute
        applicantService.saveApplicant(applicant);
        
        // Verify interactions
        verify(applicantDao).save(applicant);
    }
    
    @Test
    void testFindAllByFacultyId() {
        // Setup
        int facultyId = 2;
        List<Applicant> applicants = Arrays.asList(
                new Applicant(1, "John Doe", 4.5, facultyId, 1, Arrays.asList(90, 85, 95), 90.45f),
                new Applicant(2, "Jane Smith", 4.8, facultyId, 2, Arrays.asList(95, 90, 85), 91.48f)
        );
        
        // Mock behavior
        when(applicantDao.findAllByFacultyId(facultyId)).thenReturn(applicants);
        
        // Execute
        List<Applicant> result = applicantService.findAllByFacultyId(facultyId);
        
        // Verify
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should find 2 applicants");
        assertEquals("John Doe", result.get(0).getFullName(), "First applicant name should match");
        assertEquals("Jane Smith", result.get(1).getFullName(), "Second applicant name should match");
        
        // Verify interactions
        verify(applicantDao).findAllByFacultyId(facultyId);
    }
    
    @Test
    void testFindAllByFacultyIdEmpty() {
        // Setup
        int facultyId = 999;
        
        // Mock behavior
        when(applicantDao.findAllByFacultyId(facultyId)).thenReturn(Collections.emptyList());
        
        // Execute
        List<Applicant> result = applicantService.findAllByFacultyId(facultyId);
        
        // Verify
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result should be empty");
        
        // Verify interactions
        verify(applicantDao).findAllByFacultyId(facultyId);
    }
}