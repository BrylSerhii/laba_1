package org.srgbrl.laba1.servlet;

import com.srgbrl.laba.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AuthService validation = AuthService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));
        resp.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        var user = validation.login(req.getParameter("login"), req.getParameter("password"));
        if (user != null) {
            req.getSession().setAttribute("user", user);
            resp.sendRedirect("/");
        } else {
            req.setAttribute("error", "Login or password is incorrect");
            doGet(req, resp);
        }
    }
}