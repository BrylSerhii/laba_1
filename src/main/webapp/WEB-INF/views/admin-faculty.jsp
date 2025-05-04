<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.srgbrl.laba.entity.Faculty" %>
<%@ page import="com.srgbrl.laba.entity.Applicant" %>
<%@ page import="java.util.List" %>

<html>
<head><title>Faculty Details</title></head>
<header>
    <form method="post" action="${pageContext.request.contextPath}/logout">
        <button type="submit">Logout</button>
    </form>
</header>
<body>

<%
    Faculty faculty = (Faculty) request.getAttribute("faculty");
    List<Applicant> applicants = (List<Applicant>) request.getAttribute("applicants");
%>

<h1>Факультет: <%= faculty.getName() %></h1>
<p>Кількість місць: <%= faculty.getLimit() %></p>
<p>Статус: <%= faculty.getStatus() %></p>

<% if ("CLOSED".equals(faculty.getStatus().name())) { %>
<p style="color:grey">Набір на цей факультет завершено.</p>
<% } else { %>
<form method="post" action="${pageContext.request.contextPath}/faculties/<%= faculty.getId() %>">
    <button type="submit">Закрити відбір</button>
</form>
<% } %>

<% if (request.getAttribute("message") != null) { %>
<p style="color:green"><%= request.getAttribute("message") %></p>
<% } %>

<% if (request.getAttribute("error") != null) { %>
<p style="color:red"><%= request.getAttribute("error") %></p>
<% } %>

<h2>Список заявок</h2>
<% if (applicants != null && !applicants.isEmpty()) { %>
<ul>
    <% for (Applicant a : applicants) { %>
    <li>
        <strong><%= a.getFullName() %></strong> — Сума балів: <%= a.getSum() %>
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
