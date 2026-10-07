package com.librarymanagement.review1;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class Review1Servlet extends HttpServlet {
    private final JdbcReview1Dao jdbcDao;

    public Review1Servlet(JdbcReview1Dao jdbcDao) { this.jdbcDao = jdbcDao; }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        render(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        render(request, response);
    }

    private void render(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("search");
        if (search == null) search = "";
        try {
            request.setAttribute("search", search);
            request.setAttribute("books", jdbcDao.searchBooks(search));
            request.getRequestDispatcher("/WEB-INF/views/review1-library.jsp").forward(request, response);
        } catch (SQLException ex) {
            throw new ServletException("JDBC search failed", ex);
        }
    }
}