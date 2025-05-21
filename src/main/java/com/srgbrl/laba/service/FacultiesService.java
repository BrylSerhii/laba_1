package com.srgbrl.laba.service;

import com.srgbrl.laba.dao.FacultyDao;
import com.srgbrl.laba.entity.Faculty;
import com.srgbrl.laba.entity.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class FacultiesService {

    private static final Logger logger = LoggerFactory.getLogger(FacultiesService.class);

    static FacultiesService INSTANCE = new FacultiesService();

    private final FacultyDao facultyDao = FacultyDao.getInstance();
    private final ApplicantService applicantService = ApplicantService.getInstance();

    private FacultiesService() {
    }

    public static FacultiesService getInstance() {
        return INSTANCE;
    }

    public List<Faculty> getAllFaculties() {
        return facultyDao.findAll();
    }

    public Faculty getFacultyById(int facultyId) {
        return facultyDao.findById(facultyId);
    }

    public void createFaculty(String name, int limit) {
        facultyDao.save(new Faculty(name, limit, Status.OPEN));
        logger.info("Faculty '{}' created with limit {}", name, limit);
    }

    public void closeFaculty(Faculty faculty) throws SQLException {
        facultyDao.closeFaculty(faculty);
        logger.info("Faculty '{}' closed", faculty.getName());
    }

    public List<Faculty> findAll() {
        return facultyDao.findAll();
    }

    public List<?> getApplicantsByFacultyId(int facultyId) {
        return applicantService.findAllByFacultyId(facultyId);
    }
}
