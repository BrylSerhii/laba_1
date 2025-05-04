<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Головна сторінка</title>
</head>
<body>
<h1>Головна сторінка</h1>
<ul>
  <li><a href="${pageContext.request.contextPath}/login">Увійти</a></li>
  <li><a href="${pageContext.request.contextPath}/reg">Реєстрація</a></li>
  <li><a href="${pageContext.request.contextPath}/faculties">Факультети (лише після регістрації)</a></li>
</ul>
</body>
</html>
