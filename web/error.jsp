<%@page import="ecommander.pages.ValidationResults.LineMessage"%>
<%@page import="ecommander.pages.ValidationResults.StructureMessage"%>
<%@page import="ecommander.controllers.BasicServlet"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>Страница не найдена</title>
	<script defer="defer"  src="https://code.jquery.com/jquery-2.2.4.min.js"  integrity="sha256-BbhdlvQf/xTY9gja0Dq3HiwQF8LaCRTXxZKRutelT44=" crossorigin="anonymous"></script>
	<script defer="defer" type="text/javascript" src="http://test9.must.by/admin/js/jquery.form.min.js"></script>
	<script defer="defer" type="text/javascript" src="http://test9.must.by/admin/js/ajax.js"></script>
	<script defer="defer" type="text/javascript">
		$(document).ready(function(){
			insertAjax("http://test9.must.by/bug_feedback","",function(){
				$("#stack").val($(".bug-info").text());
				$("#url").val(document.location.href);
			});
		});
		<!-- -->
	</script>
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
	.bug-report-form {margin: 10px 0; padding: 10px 0; border-top: 1px solid #ccc; border-bottom: 1px solid #ccc;}
	.bug-report-form label, .bug-report-form textarea{display: block;}
	.bug-report-form textarea{border: 1px solid #ccc; width: 700px; height: 200px; padding: 4px;}
	.bug-report-form label{margin-bottom: 7px;}
	.bug-report-form h4{font-size: 20px;}
	.bug-report-form .send-button{background: #53A746; display: block; margin-top: 7px; color: #fff; border:0; padding: 5px 10px; font-size: 18px; cursor: pointer;}
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
		Попробуйте перейти на <a href="/">главную страницу</a> сайта или зайти сюда позже.
	</p>



	<div id="fform" class="bug-report-form">
		<!-- ВСТАВЛЯЕТСЯ ИЗ центра поддрежки -->
	</div>


	<div class="link" onclick="$('.content').toggle()">
		<span>Отладочная информация</span>
	</div>
	<div class="content bug-info" style="display: block;">
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