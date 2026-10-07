package com.librarymanagement.review1;

import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcReview1Dao {
    private final DataSource dataSource;
    public JdbcReview1Dao(DataSource dataSource) { this.dataSource = dataSource; }

    public List<Map<String, Object>> searchBooks(String search) throws SQLException {
        String sql = "SELECT id, title, author, isbn, genre, available_copies " +
                "FROM books WHERE (? = '' OR LOWER(title) LIKE LOWER(?) OR LOWER(author) LIKE LOWER(?) OR isbn LIKE ?) " +
                "ORDER BY title LIMIT 50";
        String term = search == null ? "" : search.trim();
        String like = "%" + term + "%";
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, term);
            statement.setString(2, like);
            statement.setString(3, like);
            statement.setString(4, like);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", rs.getLong("id"));
                    row.put("title", rs.getString("title"));
                    row.put("author", rs.getString("author"));
                    row.put("isbn", rs.getString("isbn"));
                    row.put("genre", rs.getString("genre"));
                    row.put("availableCopies", rs.getInt("available_copies"));
                    result.add(row);
                }
            }
        }
        return result;
    }
}