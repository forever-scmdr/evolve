<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="ecommander.controllers.admin.UserAdminServlet"%>
<%@page import="ecommander.users.User"%>
<%@page import="ecommander.controllers.SessionContext"%>
<%@page import="ecommander.common.Strings"%>
<%@include file="login_import.jsp"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="Pragma" content="no-cache"/>
	<meta http-equiv="Expires" content="-1"/>
	<link href="admin/css/main_admin.css" rel="stylesheet" type="text/css" />
	<link href="facebox/facebox.css" media="screen" rel="stylesheet" type="text/css" />
	<!--[if lte IE 7]>
		<link href="css/ie.css" rel="stylesheet" type="text/css" />
	<![endif]-->
	<title>CMS</title>
</head>
<body>
	<script language="javascript" type="text/javascript" src="admin/js/admin.js"></script>
	<!-- ************************ Основная форма **************************** -->
	<% 
	UserAdminServlet usersData = (UserAdminServlet)request.getAttribute("data");
	User admin = SessionContext.createSessionContext(request).getUser();
	%>
	<!-- ******************************************************************** -->
	<div class="mainwrap">
		<div class="header">
			<div class="logo">
				<a href=""><img src="admin/admin_img/logo.png" alt="" /></a>
			</div>
			<div class="domain">
				<div class="domain_right">
					<%=request.getServerName() + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort()) + request.getContextPath()%>
				</div>
			</div>
			<a href=""><img class="help" src="admin/admin_img/help.png" alt="" /></a>
			<table class="user">
				<tr>
					<td><%=admin.getName()%></td>
					<td class="logout">
						<a href="admin_exit.action"><img src="admin/admin_img/logout.png" alt="" /></a>
					</td>
				</tr>
			</table>
		</div>
		<!-- ************************ Путь к текущему айтему **************************** -->
		<div class="path">
			<a href="admin_initialize.action">Корень</a>
		</div>
		<!-- **************************************************************************** -->
		<table class="main_table">
			<tr>
				<td class="side_col">
					<div class="side_block">
						<div class="head">
							<span>Создать:</span>
						</div>
						<div class="items">
							<div class="exist_item">
								<table>
									<tr>
										<td class="link">
											<a href="admin_users_initialize.user">Пользователь</a>
										</td>
										<td class="action">
											<a href="admin_users_initialize.user"><img class="add" src="admin/admin_img/action_add.png"></a>
										</td>
									</tr>
								</table>
							</div>
							<div class="spacer"><div></div></div>
						</div>
						<div class="bottom">
						</div>
					</div>
					<div class="side_block">
						<div class="head">
							<span>Редактировать:</span>
						</div>
						<div class="items">
							<!-- ************************ Уже существующие сабайтемы текущего **************************** -->
							<%
							for (User user : usersData.getUsers()) {
								String deleteHref = "admin_delete_user.user?userId=" + user.getUserId();
								String selectHref = "admin_set_user.user?userId=" + user.getUserId();
								%>
								<div class="spacer"></div>
								<div class="exist_item">
									<div class="item_type">
										[<%=user.getGroup()%>]
									</div>
									<table>
										<tr>
											<td class="link">
												<a href="<%=selectHref%>"><%=user.getName()%></a>
											</td>
											<td class="action">
												<a href="#" onclick="confirmLink('<%=deleteHref%>')"><img src="admin/admin_img/action_delete.png" /></a>
											</td>
										</tr>
									</table>
								</div>
								<%
							}
							%>
							<div class="spacer"><div></div></div>
							<!-- ***************************************************************************************** -->							
						</div>
						<div class="bottom">
						
						</div>
					</div>
					<!-- functions -->
					<div class="side_block">
						<div class="head">
							<span>Дополнительно</span>
						</div>
						<div class="items">
							<!-- item -->
							<div class="exist_item">
								<table>
									<tr>
										<td class="link">
											<a href="admin_initialize.action">Структура сайта</a>
										</td>
									</tr>
								</table>
							</div>
						</div>
						<div class="bottom">
						
						</div>
					</div>
					<!-- /functions -->
				</td>
				<td class="main">
					<!-- warning -->
					<div class="warning">
						<div class="tl">
							<div class="bl">
								<div class="message">
									<span>
										<%=request.getAttribute("message")%>
									</span>
								</div>
							</div>
						</div>
					</div>
					<!-- /warning -->
					<h1 class="title">
					<%=(StringUtils.isBlank(usersData.getUserName()) ? "Создать пользователя" : "Пользователь: " + usersData.getUserName())%>
					</h1>
					<table class="type_1">
						<tr>
							<!-- ************************ Основные параметры айтема **************************** -->
							<td class="left">
								<div class="forms">
									<form name="mainForm" action="admin_save_user.user" method="post">
										<input type="hidden" name="userId" value="<%=usersData.getUserId()%>">
										<div class="form_item">
											<p class="form_title">
												Группа пользователей
											</p>
											<select name="userGroup">
												<% 
												for (String groupName : usersData.getGroupNames()) {
													%><option value="<%=groupName%>"><%=groupName%></option><%
												}
												%>
											</select>
											<p class="form_title">
												Имя ползователя
											</p>
											<input class="field" type="text" name="userName" value="<%=usersData.getUserName()%>"/>
											<p class="form_title">
												Пароль
											</p>
											<input class="field" type="text" name="password" value="<%=usersData.getPassword()%>"/>
											<p class="form_title">
												Описание
											</p>
											<p class="form_comment">
												[можно указать имя, фамилию и т.д.]
											</p>
											<textarea name="description" cols="" rows=""><%=usersData.getDescription()%></textarea>												
										</div>
									</form>
								</div>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>	
	</div>
	<div class="save">
		<a href="javascript:document.mainForm.submit()"><img src="admin/admin_img/save.png" alt=""></a>
	</div>

</body>
</html>