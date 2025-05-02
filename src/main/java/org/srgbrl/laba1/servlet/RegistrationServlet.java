package org.srgbrl.laba1.servlet;

import com.srgbrl.laba.dao.UserDAO;
import com.srgbrl.laba.entity.User;
import com.srgbrl.laba.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/reg")
public class RegistrationServlet extends HttpServlet {

    private final AuthService validation = AuthService.getInstance();
    private final UserDAO userDao = UserDAO.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));
        resp.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));
        req.getRequestDispatcher("/WEB-INF/views/registration.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var user = validation.register(new User(req.getParameter("login"),req.getParameter("password"),"applicant"));
        if (user != null) {
           req.getSession().setAttribute("user", user);
           resp.sendRedirect("/");
        } else {
            req.setAttribute("error", "unexpected error, check login or database");
            doGet(req, resp);
        }
    }
}
