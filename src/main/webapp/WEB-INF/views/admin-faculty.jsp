<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.srgbrl.laba.entity.Faculty" %>
<%@ page import="com.srgbrl.laba.entity.Applicant" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<html>
<head><title>Faculty Details</title></head>
<body>

<%
    Faculty faculty = (Faculty) request.getAttribute("faculty");
    List<Applicant> applicants = (List<Applicant>) request.getAttribute("applicants");
%>

<h1>Факультет: <%= faculty.getName() %></h1>
<p>Кількість місць: <%= faculty.getLimit() %></p>

<h2>Подати заявку</h2>
<c:if test="${requestScope.error != null}">
    <p style="color:red">${requestScope.error}</p>
</c:if>
<c:if test="${requestScope.message != null}">
    <p style="color:green">${requestScope.message}</p>
</c:if>
<form method="post" action="${pageContext.request.contextPath}/apply">
    <input type="hidden" name="facultyId" value="<%= faculty.getId() %>" />

    <label for="fullName">Full Name:</label>
    <input type="text" id="fullName" name="fullName" required /><br/>

    <label for="avgGrade">середній бал атестату:</label>
    <input type="number" id="avgGrade" name="avgGrade" step="0.01" min="0" max="12" required /><br/>

    <h3>ЗНО результати:</h3>
    <label for="ukrainian">Українська мова:</label>
    <input type="number" id="ukrainian" name="results[0]" min="100" max="200" required /><br/>

    <label for="math">Математика:</label>
    <input type="number" id="math" name="results[1]" min="100" max="200" required /><br/>

    <label for="history">Історія України:</label>
    <input type="number" id="history" name="results[2]" min="100" max="200" required /><br/>

    <button type="submit">Подати заявку</button>
</form>

<h2>Applicants</h2>
<% if (applicants != null && !applicants.isEmpty()) { %>
<ul>
    <% for (Applicant a : applicants) { %>
    <li>
        <strong><%= a.getFullName() %></strong> — Бал атестату: <%= a.getAvgGrade() %>
        <br/>Бали ЗНО:
        <ul>
            <li>Українська мова: <%= a.getResults().get(0) %></li>
            <li>Математика: <%= a.getResults().get(1) %></li>
            <li>Історія України: <%= a.getResults().get(2) %></li>
        </ul>
    </li>
    <% } %>
</ul>
<% } else { %>
<p>Заявок поки нема.</p>
<% } %>

</body>
</html>
