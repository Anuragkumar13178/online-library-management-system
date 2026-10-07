<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><title>Review 1 - JDBC Library Search</title>
<style>
body{font-family:Arial,sans-serif;margin:40px;background:#f5f7fb;color:#1f2937}
.card{max-width:1000px;margin:auto;background:white;padding:28px;border-radius:12px;box-shadow:0 8px 30px rgba(0,0,0,.08)}
form{display:flex;gap:10px;margin:20px 0}input{flex:1;padding:11px;border:1px solid #d1d5db;border-radius:8px}
button{padding:11px 18px;border:0;border-radius:8px;cursor:pointer}
table{width:100%;border-collapse:collapse;margin-top:18px}th,td{text-align:left;padding:11px;border-bottom:1px solid #e5e7eb}
.pill{display:inline-block;padding:4px 9px;border-radius:999px;background:#eef2ff}.note{font-size:13px;color:#6b7280}
</style></head><body><div class="card">
<h1>Campus Library — Review 1 Search</h1>
<p class="note">Real Jakarta Servlet + raw JDBC: Connection, PreparedStatement and ResultSet.</p>
<form method="post" action="${pageContext.request.contextPath}/review1/library">
<input name="search" value="<c:out value='${search}'/>" placeholder="Search title, author or ISBN"><button type="submit">Search</button>
</form>
<table><thead><tr><th>ID</th><th>Title</th><th>Author</th><th>ISBN</th><th>Genre</th><th>Available</th></tr></thead><tbody>
<c:forEach var="book" items="${books}"><tr>
<td><c:out value="${book.id}"/></td><td><c:out value="${book.title}"/></td><td><c:out value="${book.author}"/></td>
<td><c:out value="${book.isbn}"/></td><td><span class="pill"><c:out value="${book.genre}"/></span></td>
<td><c:out value="${book.availableCopies}"/></td></tr></c:forEach>
<c:if test="${empty books}"><tr><td colspan="6">No matching books found.</td></tr></c:if>
</tbody></table></div></body></html>