//package com.srgbrl.laba.router;
//
//import com.srgbrl.laba.api.AuthController;
//import com.srgbrl.laba.api.FacultiesController;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//import java.sql.SQLException;
//
//public class Router {
//    private final AuthController authController = new AuthController();
//    private final FacultiesController facultiesController = new FacultiesController();
//
//    public void route(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
//        String path = req.getPathInfo();
//        String method = req.getMethod();
//
//        if ("/index.jsp".equals(path)) {
//            req.getRequestDispatcher("/index.jsp").forward(req, resp);
//            return;
//        }
//
//
//        switch (path) {
//            case "/":
//            case "/index.jsp":
//                req.getRequestDispatcher("/index.jsp").forward(req, resp);
//                break;
//
//            case "/r/login":
//                if ("GET".equalsIgnoreCase(method)) {
//                    authController.showLoginForm(req, resp);
//                } else if ("POST".equalsIgnoreCase(method)) {
//                    authController.login(req, resp);
//                }
//                break;
//
//            case "/r/logout":
//                if ("POST".equalsIgnoreCase(method)) {
//                    authController.logout(req, resp);
//                }
//                break;
//
//            case "/r/faculties":
//                if ("GET".equalsIgnoreCase(method)) {
//                    try {
//                        facultiesController.showFaculties(req, resp);
//                    } catch (SQLException | ClassNotFoundException | ServletException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//                break;
//
//            default:
//                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Page not found");
//        }
//
//    }
//}
