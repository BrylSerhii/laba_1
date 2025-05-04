//package com.srgbrl.laba.servlet;
//
//import com.srgbrl.laba.router.Router;
//import jakarta.servlet.*;
//import jakarta.servlet.http.*;
//
//import java.io.IOException;
//
//public class DispatcherServlet extends HttpServlet {
//    private Router router;
//
//    @Override
//    public void init() {
//        router = new Router();
//    }
//
//    @Override
//    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        router.route(req, resp);
//    }
//}
