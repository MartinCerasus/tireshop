package com.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String city = request.getParameter("city");
        String year = request.getParameter("year");
        String month = request.getParameter("month");

        if ("London".equals(city)) {
            response.sendRedirect("london?year=" + year + "&month=" + month);
        } else if ("Manchester".equals(city)) {
            response.sendRedirect("manchester");
        }
    }
}
