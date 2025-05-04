package com.srgbrl.laba.servlet;

import com.srgbrl.laba.service.FacultiesService;
import com.srgbrl.laba.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/faculties/*")
public class FacultiesServlet extends HttpServlet {

    private final FacultiesService facultiesService = FacultiesService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            facultiesService.getFaculties(req, resp);
        } else {
            try {
                int facultyId = Integer.parseInt(pathInfo.substring(1));
                facultiesService.getFacultyDetails(req, resp, facultyId);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid faculty ID");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            facultiesService.save(req,resp);
        } else {
            try {
                int facultyId = Integer.parseInt(pathInfo.substring(1));
                facultiesService.closeApplying(req, resp, facultyId);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid faculty ID");
            }
        }
    }



}