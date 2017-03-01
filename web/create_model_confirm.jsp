<%@page import="java.util.ArrayList"%>
<%@ page import="ecommander.controllers.MetaServlet" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Подтверждение изменения модели данных</title>
</head>
<style>
	body {font: normal 12px arial; margin: 0; padding: 0; color: #363636;}
	.mainwrap {width: 1000px; margin: 0 auto;}
	h1, h2 {font-weight: normal; margin-bottom: 10px;}
	h1 {font-size: 30px; margin-top: 50px}
	h2 {font-size: 18px; margin-top: 30px}
	p.text {width: 500px; font-size: 1.1em;}
	table {margin-left: 30px;}
	td {padding-right: 30px; padding-bottom: 10px; vertical-align: top;}
	.structure .side {width: 70px;}
	pre {padding-left: 30px;}
	.link {margin-top: 80px; color: #b7b7b7;}
	.link span {border-bottom: #b7b7b7 1px dotted; cursor: pointer;}
	.content {display: none;}
	a {color: #0071bc;}
</style>
<body>
	<%
	ArrayList<String> toDelete = (ArrayList<String>)request.getAttribute(MetaServlet.ITEMS_TO_BE_DELETED_ATTR);
	%>
	<div class="mainwrap">
		<h1>Подтвердите обновление модели данных</h1>
		<p class="text">
			Как результат изменения модели данных (файл model.xml), должны быть удалены некоторые айтемы и параметры.
			При подтверждении обновления модели они будут удалены безвозвратно.
		</p>
		<p class="text">
			<b>Возможно</b> изменения model.xml являются <b>ошибочными</b>.
		</p>
		<p class="text">
			Для подтверждения обновления модели данных нажмите ссылку "Подтвердить обновление".
		</p>
		<div class="link">
			<a href="meta?q=force_model">Подтвердить обновление</a>
		</div>
		<div class="content">
			<h2>Удаляемые айтемы и параметры</h2>
			<table class="structure">
				<%
				for (String deleted : toDelete) {
				%>
				<tr>
					<td class="side"></td>
					<td class="info"><%=deleted%></td>
				</tr>
				<%
				}
				%>
			</table>
		</div>
	</div>
</body>