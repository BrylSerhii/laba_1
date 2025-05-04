<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Faculty" %>
<%@ page import="model.Subject" %>

<html>
<head><title>Applicant Registration</title></head>
<body>
<h2>Applicant Registration</h2>

<form method="post" action="/register">
    Full Name: <input type="text" name="fullName"/><br>
    Average Grade: <input type="text" name="averageGrade"/><br>

    Faculty:
    <select name="facultyId">
        <% List<Faculty> faculties = (List<Faculty>) request.getAttribute("faculties");
            for (Faculty faculty : faculties) { %>
        <option value="<%= faculty.getId() %>"><%= faculty.getName() %></option>
        <% } %>
    </select><br>

    <h3>Subjects</h3>
    <% List<Subject> subjects = (List<Subject>) request.getAttribute("subjects");
        for (Subject subject : subjects) { %>
    <label><%= subject.getName() %>:
        <input type="number" name="subject_<%= subject.getId() %>" min="0" max="200"/></label><br>
    <% } %>

    <button type="submit">Submit</button>
</form>

</body>
</html>
