package com.srgbrl.laba.servlet;

import com.srgbrl.laba.entity.Faculty;
import com.srgbrl.laba.entity.User;
import com.srgbrl.laba.service.FacultiesService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@WebServlet("/faculties/*")
public class FacultiesServlet extends HttpServlet {

    private final FacultiesService facultiesService = FacultiesService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        var session = req.getSession();
        var user = (User) session.getAttribute("user");

        if (user == null) {
            resp.sendRedirect("/");
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            List<Faculty> faculties = facultiesService.getAllFaculties();
            req.setAttribute("faculties", faculties);

            var view = Objects.equals(user.getRole(), "admin") ?
                    "/WEB-INF/views/admin-faculties.jsp" :
                    "/WEB-INF/views/faculties.jsp";

            req.getRequestDispatcher(view).forward(req, resp);
        } else {
            try {
                int facultyId = Integer.parseInt(pathInfo.substring(1));
                Faculty faculty = facultiesService.getFacultyById(facultyId);
                if (faculty == null) {
                    resp.sendRedirect("/");
                    return;
                }

                req.setAttribute("faculty", faculty);
                req.setAttribute("applicants", facultiesService.getApplicantsByFacultyId(facultyId));

                var view = Objects.equals(user.getRole(), "admin") ?
                        "/WEB-INF/views/admin-faculty.jsp" :
                        "/WEB-INF/views/faculty.jsp";

                req.getRequestDispatcher(view).forward(req, resp);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid faculty ID");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        var session = req.getSession();
        var user = (User) session.getAttribute("user");

        if (user == null || !Objects.equals(user.getRole(), "admin")) {
            resp.sendRedirect("/");
            return;
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            String name = req.getParameter("name");
            int limit = Integer.parseInt(req.getParameter("limit"));

            facultiesService.createFaculty(name, limit);
            resp.sendRedirect("/faculties");
        } else {
            try {
                int facultyId = Integer.parseInt(pathInfo.substring(1));
                Faculty faculty = facultiesService.getFacultyById(facultyId);

                if (faculty != null) {
                    facultiesService.closeFaculty(faculty);
                }

                resp.sendRedirect("/faculties/" + facultyId);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid faculty ID");
            } catch (Exception e) {
                resp.sendRedirect("/");
            }
        }
    }
}
