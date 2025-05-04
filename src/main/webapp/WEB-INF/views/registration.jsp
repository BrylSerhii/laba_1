<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Registration</title>
</head>
<body>
<header>REG</header>
<main>
    <form method="post" action="${pageContext.request.contextPath}/reg">
        <label>Login:
            <input type="text" name="login" placeholder="login" required>
        </label>
        <label>Password:
            <input id="password" type="password" name="password" placeholder="password" required minlength="1"
                   maxlength="20">
        </label>

        <button type="submit">Register</button>


    </form>
    <c:if test="${requestScope.error != null}">
        <p style="color:red">${requestScope.error}</p>
    </c:if>
</main>
</body>
</html>
