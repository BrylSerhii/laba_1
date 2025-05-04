package com.srgbrl.laba.service;

import com.srgbrl.laba.dao.ApplicantDao;
import com.srgbrl.laba.entity.Applicant;
import com.srgbrl.laba.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ApplicantService {

    static ApplicantService INSTANCE = new ApplicantService();

    private ApplicantService() {

    }

    public static ApplicantService getInstance() {
        return INSTANCE;
    }

    private final ApplicantDao applicantDao = ApplicantDao.getInstance();

    public void createApplication(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var session = req.getSession();
        var user = session.getAttribute("user");
        if (session.getAttribute("user") != null) {
            Applicant applicant = applicantDao.findByUserIdFacultyId(((User) user).getId(), Integer.parseInt(req.getParameter("facultyId")));
            if (applicant != null) {
                req.setAttribute("error", "заявка в цей факультет вже подана");
                resp.sendRedirect("/faculties/" + req.getParameter("facultyId"));
            } else {
                applicantDao.save(new Applicant(
                        req.getParameter("fullName"),
                        Double.parseDouble(req.getParameter("avgGrade")),
                        Integer.parseInt(req.getParameter("facultyId")),
                        ((User) user).getId(),
                        List.of(
                                Integer.parseInt(req.getParameter("results[0]")),
                                Integer.parseInt(req.getParameter("results[1]")),
                                Integer.parseInt(req.getParameter("results[2]")))));
                req.setAttribute("message", "заявка успішно подана!");
                resp.sendRedirect("/faculties/" + req.getParameter("facultyId"));
            }
        } else {
            resp.sendRedirect("/");
        }
    }

    public List<Applicant> findAllByFacultyId(Integer facultyId) {
        return applicantDao.findAllByFacultyId(facultyId);
    }
}
