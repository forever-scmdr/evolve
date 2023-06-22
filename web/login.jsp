<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="admin/css/login.css" rel="stylesheet" type="text/css" />
<title>Вход в систему</title>
</head>
<body>
	<%
	String target = request.getParameter("q");
		if (StringUtils.isBlank(target))
			target = "admin_initialize.action";
	%>
	<form action="login.login" method="post">
		<input type="hidden" name="target" value="<%=target%>"/>
		<table class="login">
			<tr>
				<td>Логин:</td>
				<td><input name="name" type="text" /></td>
			</tr>
			<tr>
				<td>Пароль:</td>
				<td><input name="password" type="password" /></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit" title="Войти"/></td>
			</tr>
		</table>
	</form>
</body>
</html>