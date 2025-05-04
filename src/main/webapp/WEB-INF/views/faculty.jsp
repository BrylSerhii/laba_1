<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.srgbrl.laba.entity.Faculty" %>
<%@ page import="com.srgbrl.laba.entity.Applicant" %>
<%@ page import="com.srgbrl.laba.entity.User" %>
<%@ page import="java.util.*" %>

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
    User user = (User) session.getAttribute("user");

    Applicant currentApplicant = null;
    if (user != null) {
        for (Applicant a : applicants) {
            if (a.getUserId() != null && a.getUserId().equals(user.getId())) {
                currentApplicant = a;
                break;
            }
        }
    }

    int limit = faculty.getLimit();
    boolean isPassed = currentApplicant != null && applicants.indexOf(currentApplicant) < limit;
    boolean isFacultyClosed = "CLOSED".equals(faculty.getStatus().name()); // Перевіряємо статус факультету
%>

<h1>Факультет: <%= faculty.getName() %></h1>
<p>Кількість місць: <%= faculty.getLimit() %></p>

<% if (isFacultyClosed) { %>
<p style="color:grey">Набір на цей факультет завершено.</p>

<% if (user != null && currentApplicant != null) { %>
<% if (isPassed) { %>
<p style="color:green">Вітаємо, ви пройшли на цей факультет!</p>
<% } else { %>
<p style="color:red">На жаль, ви не пройшли на цей факультет.</p>
<% } %>
<% } %>
<% } else if (user != null && currentApplicant == null) { %>
<h2>Подати заявку</h2>
<c:if test="${requestScope.error != null}">
    <p style="color:red">${requestScope.error}</p>
</c:if>
<c:if test="${requestScope.message != null}">
    <p style="color:green">${requestScope.message}</p>
</c:if>
<form method="post" action="${pageContext.request.contextPath}/apply">
    <input type="hidden" name="facultyId" value="<%= faculty.getId() %>" />

    <label for="fullName">ПІБ:</label>
    <input type="text" id="fullName" name="fullName" required /><br/>

    <label for="avgGrade">Середній бал атестату:</label>
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
<% } %>

<h2>Список заявок</h2>
<% if (applicants != null && !applicants.isEmpty()) { %>
<ul>
    <% for (Applicant a : applicants) { %>
    <%
        // Перевірка, чи це аплікант поточного користувача
        boolean isCurrentUser = currentApplicant != null && currentApplicant.equals(a);
        boolean isPassedApplicant = applicants.indexOf(a) < limit;
        String highlightClass = "";

        if (isCurrentUser && isPassedApplicant) {
            highlightClass = " style='background-color: lightblue;'";
        } else if (isPassedApplicant) {
            highlightClass = " style='background-color: lightgreen;'";
        } else if (isCurrentUser) {
            highlightClass = " style='background-color: lightyellow;'";
        }
    %>
    <li <%= highlightClass %>>
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
