package org.srgbrl.laba1.service;

import com.srgbrl.laba.dao.ApplicantDao;
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
            var dispatcher = req.getRequestDispatcher("/WEB-INF/views/faculties.jsp");
            dispatcher.forward(req, resp);
        } else {
            resp.sendRedirect("/");
        }
    }

    public void createFaculty(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException, ClassNotFoundException {
        var name = req.getParameter("name");
        var limit = Integer.parseInt(req.getParameter("limit"));

        Faculty faculty = new Faculty();
        faculty.setName(name);
        faculty.setLimit(limit);
        faculty.setStatus(Status.OPEN);

        facultyDao.save(faculty);
        resp.sendRedirect("/faculties");
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
}
