package com.srgbrl.laba.service;

import com.srgbrl.laba.dao.FacultyDao;
import com.srgbrl.laba.entity.Faculty;
import com.srgbrl.laba.entity.Status;
import com.srgbrl.laba.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class FacultiesService {

    static FacultiesService INSTANCE = new FacultiesService();

    private FacultiesService() {

    }

    public static FacultiesService getInstance() {
        return INSTANCE;
    }

    private final FacultyDao facultyDao = FacultyDao.getInstance();
    private final ApplicantService applicantService = ApplicantService.getInstance();

    public void getFaculties(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var session = req.getSession();
        if (session.getAttribute("user") != null) {
            List<Faculty> faculties = facultyDao.findAll();
            req.setAttribute("faculties", faculties);
            if (Objects.equals(((User) session.getAttribute("user")).getRole(), "admin")) {
                var dispatcher = req.getRequestDispatcher("/WEB-INF/views/admin-faculties.jsp");
                dispatcher.forward(req, resp);
            } else {
                var dispatcher = req.getRequestDispatcher("/WEB-INF/views/faculties.jsp");
                dispatcher.forward(req, resp);
            }
        } else {
            resp.sendRedirect("/");
        }
    }

    public void save(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var session = req.getSession();
        if (session.getAttribute("user") != null) {
            if (Objects.equals(((User) session.getAttribute("user")).getRole(), "admin")) {
                var name = req.getParameter("name");
                var limit = Integer.parseInt(req.getParameter("limit"));
                facultyDao.save(new Faculty(name,limit,Status.OPEN));
            } else {
                resp.sendRedirect("/faculties");
            }
            resp.sendRedirect("/faculties");
        }
    }

    public void getFacultyDetails(HttpServletRequest req, HttpServletResponse resp, Integer facultyId) throws IOException, ServletException {
        var session = req.getSession();
        if (session.getAttribute("user") != null) {
            Faculty faculty = facultyDao.findById(facultyId);
            if (faculty == null) {
                resp.sendRedirect("/");
            } else {
                req.setAttribute("faculty", faculty);
                req.setAttribute("applicants", applicantService.findAllByFacultyId(facultyId));
                if (Objects.equals(((User) session.getAttribute("user")).getRole(), "admin")) {
                    var dispatcher = req.getRequestDispatcher("/WEB-INF/views/admin-faculty.jsp");
                    dispatcher.forward(req, resp);
                } else {
                    var dispatcher = req.getRequestDispatcher("/WEB-INF/views/faculty.jsp");
                    dispatcher.forward(req, resp);
                }
            }
        } else {
            resp.sendRedirect("/");
        }
    }

    public void closeApplying(HttpServletRequest req, HttpServletResponse resp, Integer facultyId) throws IOException, ServletException {
        var session = req.getSession();
        if (session.getAttribute("user") != null) {
            Faculty faculty = facultyDao.findById(facultyId);
            if (faculty == null) {
                resp.sendRedirect("/");
            } else {
                if (Objects.equals(((User) session.getAttribute("user")).getRole(), "admin")) {
                    try {
                        facultyDao.closeFaculty(faculty);
                    } catch (SQLException e) {
                        System.out.println("faculty " + faculty.getName() +  " failed to close");
                        req.setAttribute("error", "faculty failed to close");
                        resp.sendRedirect("/");
                        return;
                    }
                    System.out.println("faculty " + faculty.getName() + " successfully closed");
                    req.setAttribute("message", "faculty successfully closed");
                    resp.sendRedirect("/faculties/" + facultyId);
                } else {
                    resp.sendRedirect("/faculties/" + facultyId);
                }
            }
        } else {
            resp.sendRedirect("/");
        }
    }
}
