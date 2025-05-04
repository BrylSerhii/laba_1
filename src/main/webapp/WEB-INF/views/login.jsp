<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Login</title></head>
<body>
<h2>Login</h2>
<form method="post" action="${pageContext.request.contextPath}/login">
    <label>Login: <input type="text" name="login"/></label><br>
    <label>Password: <input type="password" name="password"/></label><br>
    <button type="submit">Login</button>
</form>
<c:if test="${requestScope.error != null}">
    <p style="color:red">${requestScope.error}</p>
</c:if>
</body>
</html>
