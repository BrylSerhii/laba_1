package com.srgbrl.laba.servlet;

import com.srgbrl.laba.entity.Applicant;
import com.srgbrl.laba.entity.User;
import com.srgbrl.laba.service.ApplicantService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/apply")
public class ApplyServlet extends HttpServlet {

    private final ApplicantService applicantService = ApplicantService.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());

        var session = req.getSession();
        var userObj = session.getAttribute("user");

        if (userObj == null) {
            resp.sendRedirect("/");
            return;
        }

        var user = (User) userObj;
        int facultyId = Integer.parseInt(req.getParameter("facultyId"));

        if (applicantService.hasAlreadyApplied(user.getId(), facultyId)) {
            resp.sendRedirect("/faculties/" + facultyId + "?error=Заявка вже подана");
            return;
        }

        Applicant applicant = new Applicant(
                req.getParameter("fullName"),
                Double.parseDouble(req.getParameter("avgGrade")),
                facultyId,
                user.getId(),
                List.of(
                        Integer.parseInt(req.getParameter("results[0]")),
                        Integer.parseInt(req.getParameter("results[1]")),
                        Integer.parseInt(req.getParameter("results[2]"))
                )
        );

        applicantService.saveApplicant(applicant);
        resp.sendRedirect("/faculties/" + facultyId + "?message=Заявка+успішно+подана");
    }
}
