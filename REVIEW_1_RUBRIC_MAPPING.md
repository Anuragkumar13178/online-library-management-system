# Review 1 Rubric Mapping

## 1. Problem Understanding & Solution Design — 8 marks

Implemented evidence:
- Role.java: LIBRARIAN and MEMBER.
- BookController.java and LibraryService.java: catalog and business rules.
- MemberController.java and User.java: member management.
- TransactionController.java, LibraryService.java and Loan.java: borrow/return workflow.
- NotificationController.java plus notification entities: alerts and preferences.
- ReportController.java: inventory and borrowing reports.
- SearchHistory.java, SearchHistoryRepository.java and SearchHistoryController.java: member search history.
- Layered controller/service/repository/entity architecture.

## 2. Core Java Concepts — 10 marks

- Encapsulation: private fields and public methods in entities.
- Abstraction/modularity: controllers, services and repository contracts.
- Collections: List, Map, Set and Streams.
- Exception handling: IllegalArgumentException, IllegalStateException, NoSuchElementException and AccessDeniedException.
- Java Date/Time API: LocalDate and Instant.
- Concurrency: LibraryService.borrow() uses a per-book synchronized critical section.
- LibraryServiceTest.concurrentBorrowingOfLastCopyAllowsOnlyOneWinner() creates two Java threads and verifies only one can borrow the last copy.
- BCrypt and JWT demonstrate practical Java security handling.

## 3. Database Integration (JDBC) — 8 marks

The main application uses Spring Data JPA, but a real raw-JDBC path has been added for direct Review 1 demonstration.

`backend/src/main/java/com/librarymanagement/review1/JdbcReview1Dao.java` uses:
- DataSource
- Connection
- PreparedStatement
- ResultSet
- try-with-resources

The query reads real data from the books table and is parameterized.

Review 1 JDBC flow:
Browser → Review1Servlet → JdbcReview1Dao → MySQL → Review1Servlet → JSP

## 4. Servlets & Web Integration — 7 marks

`Review1Servlet.java` is a genuine Jakarta HttpServlet with:
- doGet()
- doPost()
- HttpServletRequest
- HttpServletResponse
- RequestDispatcher
- request attributes

`Review1ServletConfig.java` registers the servlet at `/review1/library`.

`review1-library.jsp` is a genuine JSP using JSTL/EL and rendering database results.

## Viva note

The production application remains Spring Boot + REST + JPA + React. The Review 1 path adds genuine Servlet/JSP/JDBC implementation inside the same project so the academic concepts are directly demonstrable.

Do not claim that the whole production application is raw JDBC/JSP. Show the dedicated Review 1 path honestly.