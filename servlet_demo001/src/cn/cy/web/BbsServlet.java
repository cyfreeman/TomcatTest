package cn.cy.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BbsServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("bbs init...");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("----------------------> get: BbsServlet find all........");

        String project_param_01 = req.getServletContext().getInitParameter("project_param_01");
        System.out.println(project_param_01);

        String id = req.getSession().getId();
        System.out.println("sessionID: " + id);

        int i = 1/0;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("----------------------> post: BbsServlet find all........");
    }
}
