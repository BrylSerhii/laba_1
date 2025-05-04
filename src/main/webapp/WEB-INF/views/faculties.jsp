<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.srgbrl.laba.entity.Faculty" %>

<html>
<head><title>Факультети</title></head>
<header>
    <form method="post" action="${pageContext.request.contextPath}/logout">
        <button type="submit">Logout</button>
    </form>
</header>
<body>
<h2>Факультети</h2>

<ul>
    <%
        List<Faculty> faculties = (List<Faculty>) request.getAttribute("faculties");
        if (faculties != null) {
            for (Faculty faculty : faculties) {
    %>
    <li>
        <%= faculty.getName() %> (Capacity: <%= faculty.getLimit() %>)
        <form method="get" action="${pageContext.request.contextPath}/faculties/<%= faculty.getId() %>"
              style="display:inline;">
            <button type="submit">Details</button>
        </form>
    </li>
    <% }
    }
    %>
</ul>
</body>
</html>
