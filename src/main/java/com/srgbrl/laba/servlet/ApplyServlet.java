package com.srgbrl.laba.servlet;

import com.srgbrl.laba.service.ApplicantService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/apply")
public class ApplyServlet extends HttpServlet {

    private final ApplicantService applicantService = ApplicantService.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        applicantService.createApplication(req, resp);
    }
}
