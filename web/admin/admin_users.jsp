<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="ecommander.admin.UserAdminServlet"%>
<%@page import="ecommander.model.User"%>
<%@page import="ecommander.controllers.SessionContext"%>
<%@page import="ecommander.fwk.Strings"%>
<%@ page import="ecommander.controllers.AppContext" %>
<%@include file="login_import.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="Pragma" content="no-cache"/>
	<meta http-equiv="Expires" content="-1"/>
	<link rel="stylesheet" type="text/css" href="admin/css/reset.css" />
	<link rel="stylesheet" type="text/css" href="admin/css/style.css" />
	
	<title>Управление доменами</title>
	<script src="admin/js/jquery-2.2.4.min.js"></script>
	<script type="text/javascript" src="admin/js/admin.js"></script>
</head>
<body>
	<!-- ************************ Основная форма **************************** -->
	<% 
	UserAdminServlet usersData = (UserAdminServlet)request.getAttribute("data");
	User admin = SessionContext.createSessionContext(request).getUser();
	%>
	<!-- ******************************************************************** -->
	
	<div class="mainwrap">
		<div class="header">
			<div class="left-col">
				<div class="logo">
					<a href="admin_initialize.action">
						<img src="admin/img/forever_logo.png" alt="forever" />
					</a>
				</div>
			</div>
			<div class="right-col">
				<a href="<%=AppContext.getServerNamePort(request) + request.getContextPath()%>" class="gray">
					<%=AppContext.getServerNamePort(request) + request.getContextPath()%>
				</a>
				<a href="#">
					<img src="admin/img/visual_btn.png" alt="visual editor" />
				</a>
				<a href="#" title="сделать диз"> F1 - справка.</a>
				<a href="logout.login"	class="logout"><%=admin.getName()%></a>
			</div>
		</div>
		<div class="path">
			<span class="pad"></span>
			<a href="admin_initialize.action">Корень</a>
			<span class="sep"></span>
			<b>Управление пользователями</b>
		</div>
		<div class="mid">
			<div class="left-col">
				<div class="list">
					<h4>Созадать новую учетную запись</h4>
					<ul class="create">
						<li class="drop-zone"></li>
						<li class="visible">
							<a href="admin_users_initialize.user" >
								<span class="name">Новый пользователь</span>
							</a>
						</li>
						<li class="drop-zone"></li>
					</ul>
				</div>
				<div class="list">
					<h4>Редактировать:</h4>
					<ul class="edit">
						<li class="drop-zone"></li>
						<%
							for (User user : usersData.getUsers()) {
								String deleteHref = "admin_delete_user.user?userId=" + user.getUserId();
								String selectHref = "admin_set_user.user?userId=" + user.getUserId();
						%>
							<li class="visible single">
								<a href="<%=selectHref%>" class="name" title="редактировать">
									<span class="description">[<%=user.getGroupRolesStr()%>]</span><br/>
									<%=user.getName()%>
								</a>
								<a href="#" onclick="confirmLink('<%=deleteHref%>')" class="delete" title="удалить">удалить</a>
							</li>
							<li class="drop-zone" ></li>
						<%
							}
						%>
					</ul>
				</div>
				<div class="list">
					<a href="admin_initialize.action" title="назад к структуре сайта" style="text-align: center; display: block; padding: 10px; text-decoration: none; background: #E8E8E8;">
						<img alt="back" src="admin/img/back.png" style="width: 100px;"/><br/>
						<span style="padding-top: 10px;">назад к структуре сайта</span>
					</a>
				</div>
			</div>
			
			<div class="right-col">
				<div class="inner">
					<h1><%=(StringUtils.isBlank(usersData.getUserName()) ? "Создать пользователя" : "Пользователь: " + usersData.getUserName())%></h1>
					<div class="edit-arena">
						<div class="wide">
							<div class="margin">
								<form name="mainForm" action="admin_save_user.user" method="post">
									<input type="hidden" name="userId" value="<%=usersData.getUserId()%>" />
									<div class="form-item">
										<label>
											Группа пользователей
											<select name="userGroup" class="domain-type">
												<% 
												for (String groupName : usersData.getGroupNames()) {
													%><option value="<%=groupName%>"><%=groupName%></option><%
												}
												%>
											</select>
										</label>
									</div>
									<div class="form-item">
										<label>
											Имя пользователя
											<input class="field" type="text" name="userName" value="<%=usersData.getUserName()%>"/>
										</label>
									</div>
									<div class="form-item">
										<label>
											Пароль
											<input class="field" type="text" name="userName" value="<%=usersData.getPassword()%>"/>
										</label>
									</div>
									<div class="form-item">
										<label>
											Описание
											<textarea name="description" cols="" rows="" style="width: 800px; height: 300px; margin-top: 4px;"><%=usersData.getDescription()%></textarea>
										</label>
									</div>
									<div class="footer">
										<div class="save-links save">
											<a href="javascript:document.mainForm.submit()">Сохранить</a>
											<a href="javascript:document.mainForm.submit(); document.location.href = 'admin_initialize.action'">Сохранить и выйти</a>
										</div>
									</div>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>

		</div>
	</div>
</body>
</html>