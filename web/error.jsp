<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="ecommander.pages.ValidationResults.StructureMessage"%>
<%@page import="ecommander.pages.ValidationResults.StructureMessage"%>
<%@page import="ecommander.controllers.BasicServlet"%>
<%@page import="ecommander.pages.ValidationResults.LineMessage"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Страница не найдена</title>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
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
	ArrayList<LineMessage> lineErrors = (ArrayList<LineMessage>)request.getAttribute(BasicServlet.MODEL_ERRORS_NAME);
	ArrayList<StructureMessage> structErrors = (ArrayList<StructureMessage>)request.getAttribute(BasicServlet.PAGES_ERRORS_NAME);
	String e = (String)request.getAttribute(BasicServlet.EXCEPTION_NAME);
	%>
	<div class="mainwrap">
		<h1>404 - Страница не найдена</h1>
		<p class="text">
			Запрашиваемой страницы не существует. Возможно, вы ошиблись, набирая адрес страницы или перешли по ссылке, которая устарела.
		</p>
		<p class="text">
			Попробуйте перейти на <a href="index.htm">главную страницу</a> сайта или зайти сюда позже.
		</p>
		<div class="link" onclick="$('.content').toggle()">
			<span>Отладочная информация</span>
		</div>
		<div class="content">
			<%
			if (lineErrors != null && lineErrors.size() > 0) {
			%>
			<h2>Информационная модель сайта</h2>
			<table class="structure">
				<%
				for (LineMessage error : lineErrors) {
				%>
				<tr>
					<td class="side"><%=error.lineNumber%>:</td>
					<td class="info"><%=error.message%></td>
				</tr>
				<%
				}
				%>
			</table>
			<%
			}
			if (structErrors != null && structErrors.size() > 0) {
			%>
			<h2>Страницы сайта</h2>
			<table class="pages">
				<%
				for (StructureMessage error : structErrors) {
				%>
				<tr>
					<td class="side"><%=error.originator%></td>
					<td class="info"><%=error.message%></td>
				</tr>
				<%
				}
				%>
			</table>
			<% 
			}
			if (e != null) {
			%>
			<table class="exeption">
				<h2>Exception</h2>
				<p>
					<pre><%=e %></pre>
				</p>
			</table>
			<% } %>
		</div>
	</div>
</body>