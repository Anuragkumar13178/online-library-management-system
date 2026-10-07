# Review 1 Viva Questions

## JDBC

1. What is JDBC? — Java API for communicating with relational databases.
2. Where is JDBC used? — JdbcReview1Dao.java.
3. What is Connection? — The active database connection.
4. What is PreparedStatement? — Parameterized SQL statement that safely accepts values.
5. What is ResultSet? — Rows returned by a SELECT query.
6. Why try-with-resources? — It closes JDBC resources automatically.
7. Why not concatenate user input into SQL? — It creates SQL-injection risk.

## Servlets/JSP

8. What is a Servlet? — A server-side Java component that handles HTTP requests.
9. Where is your Servlet? — Review1Servlet extends HttpServlet.
10. What is doGet()? — Handles GET requests.
11. What is doPost()? — Handles POST requests such as the search form.
12. What is RequestDispatcher? — It forwards a request to the JSP.
13. Why JSP? — It provides the server-side presentation layer for the Review 1 path.
14. Should database logic be inside JSP? — No; JSP is for presentation.

## Core Java

15. What is synchronization? — Protection of a critical section from concurrent access.
16. Where is synchronization used? — LibraryService.borrow().
17. Why is it needed? — Multiple members may compete for the last available copy.
18. How is concurrency tested? — Two Java Thread instances attempt the same one-copy borrow operation.
19. What is a race condition? — Incorrect behavior caused by timing-dependent concurrent access.
20. Why is database locking also used? — Synchronization protects one JVM; the database lock protects the authoritative row.

## Project

21. Explain borrowing. — Validate member and limit, lock the book, decrement availability, create the loan, and create a due-date notification.
22. Explain return. — Verify ownership or librarian role, ensure the loan is active, set return date/status, and restore availability.
23. Can a member return another member's book? — No.
24. How is search history stored? — SearchHistory entity and search_history table, exposed through /api/search-history.
25. How are notifications stored? — Database-backed notification entities with recipient, type, message, read status and timestamp.
26. What is your architecture? — Controller → Service → Repository/DAO → Database; the Review 1 path additionally demonstrates Servlet → JDBC DAO → JSP.
27. What is the difference between JPA and JDBC? — JDBC exposes SQL/resource handling directly; JPA provides ORM abstractions.
28. Why did you keep both? — The existing application uses JPA, while the Review 1 path makes JDBC and Servlet/JSP concepts directly visible for academic evaluation.