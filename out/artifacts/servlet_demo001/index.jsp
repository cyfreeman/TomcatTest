<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <title>$Title$</title>
</head>
<body>
<%
  DateFormat dateFormat = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
  String format = dateFormat.format(new Date());
%>
Hello , Java Server Page 。。。。
<br/>
<%= format %>
</body>
</html>