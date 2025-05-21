package com.srgbrl.laba.service;

import com.srgbrl.laba.dao.ApplicantDao;
import com.srgbrl.laba.entity.Applicant;

import java.util.List;

public class ApplicantService {

    static ApplicantService INSTANCE = new ApplicantService();

    private ApplicantService() {}

    public static ApplicantService getInstance() {
        return INSTANCE;
    }

    private final ApplicantDao applicantDao = ApplicantDao.getInstance();

    public boolean hasAlreadyApplied(int userId, int facultyId) {
        return applicantDao.findByUserIdFacultyId(userId, facultyId) != null;
    }

    public void saveApplicant(Applicant applicant) {
        applicantDao.save(applicant);
    }

    public List<Applicant> findAllByFacultyId(Integer facultyId) {
        return applicantDao.findAllByFacultyId(facultyId);
    }
}
