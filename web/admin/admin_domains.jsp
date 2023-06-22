<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="ecommander.users.User"%>
<%@page import="ecommander.controllers.SessionContext"%>
<%@page import="ecommander.common.Strings"%>
<%@include file="login_import.jsp"%>

<%@page import="ecommander.controllers.admin.DomainAdminServlet"%>
<%@page import="ecommander.view.domain.DomainRegistry"%>
<%@page import="ecommander.view.domain.Domain"%><html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="Pragma" content="no-cache"/>
	<meta http-equiv="Expires" content="-1"/>
	<link href="admin/css/main_admin.css" rel="stylesheet" type="text/css" />
	<!--[if lte IE 7]>
		<link href="css/ie.css" rel="stylesheet" type="text/css" />
	<![endif]-->
	<title>CMS</title>
</head>
<body>
	<script language="javascript" type="text/javascript" src="admin/js/admin.js"></script>
	<!-- ************************ Основная форма **************************** -->
	<%
		DomainAdminServlet domainsBean = (DomainAdminServlet)request.getAttribute("data");
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
						<a href="logout.login"><img src="admin/admin_img/logout.png" alt="" /></a>
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
							<span>Создать новый домен:</span>
						</div>
						<div class="items">
							<div class="exist_item">
								<form name="addForm" action="admin_create_domain.domain" method="post">
									<table>
										<tr>
											<td class="link">
												<p>Название</p>
												<input name="name" type="text" />
												<p>Тип</p>
												<select name="viewType">
													<option value="<%=DomainRegistry.CHECKBOX%>">Галка</option>
													<option value="<%=DomainRegistry.COMBOBOX%>">Выпадающий список</option>
													<option value="<%=DomainRegistry.RADIOGROUP%>">Список с точками</option>
												</select>
												<p>Формат</p>
												<input name="format" type="text" />
											</td>
											<td class="action">
												<a href="javascript:document.addForm.submit()">
													<img class="add" src="admin/admin_img/action_add.png">
												</a>
											</td>
										</tr>
									</table>
								</form>
								<form name="actionForm" action="" method="post">
									<input name="name" type="hidden"/>
								</form>
								<script type="text/javascript">
									function selectDomain(domainName) {
										document.actionForm.name.value = domainName;
										document.actionForm.action = "admin_set_domain.domain";
										document.actionForm.submit();
									}
									function deleteDomain(domainName) {
										if (confirm("Для подтверждения нажмите 'Ok'") && confirm("Для подтверждения нажмите 'Ok'")) {
											document.actionForm.name.value = domainName;
											document.actionForm.action = "admin_delete_domain.domain";
											document.actionForm.submit();
										}
									}
								</script>
							</div>
							<div class="spacer"><div></div></div>
						</div>
						<div class="bottom">
						</div>
					</div>
					<div class="side_block">
						<div class="head">
							<span>Существующие домены:</span>
						</div>
						<div class="items">
							<!-- ********************** Существующие домены: ссылки редактировать и удалть домен ************************* -->
							<%
							for (String domainName: DomainRegistry.getDomainNames()) {
								%>
								<div class="spacer"></div>
								<div class="exist_item">
									<table>
										<tr>
											<td class="link">
												<a href="javascript:selectDomain('<%=domainName%>')"><%=domainName%></a>
											</td>
											<td class="action">
												<a href="javascript:deleteDomain('<%=domainName%>')">
													<img src="admin/admin_img/action_delete.png" />
												</a>
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
					<%=(domainsBean.hasDomainSet() ? "Домен: " + domainsBean.getCurrentName() : "Создайте или выберите домен")%>
					</h1>
					<% if (domainsBean.hasDomainSet()) { %>
					<table class="type_1">
						<tr>
							<!-- ************************ Редактирование домена **************************** -->
							<td class="left">
								<div class="forms">
									<% Domain domain = DomainRegistry.getDomain(domainsBean.getCurrentName()); %>
									<div class="form_item">
										<form name="editForm" action="admin_update_domain.domain" method="post">
											<input type="hidden" name="currentName" value="<%=domain.getName()%>">
											<p>Название</p>
											<input name="name" type="text" value="<%=domain.getName()%>"/>
											<p>Тип</p>
											<select name="viewType">
												<option value="<%=DomainRegistry.CHECKBOX%>"<%=(domain.getViewType().equalsIgnoreCase(DomainRegistry.CHECKBOX) ? " selected='selected'" : "")%>>Галка</option>
												<option value="<%=DomainRegistry.COMBOBOX%>"<%=(domain.getViewType().equalsIgnoreCase(DomainRegistry.COMBOBOX) ? " selected='selected'" : "")%>>Выпадающий список</option>
												<option value="<%=DomainRegistry.RADIOGROUP%>"<%=(domain.getViewType().equalsIgnoreCase(DomainRegistry.RADIOGROUP) ? " selected='selected'" : "")%>>Список с точками</option>
											</select>
											<p>Формат</p>
											<input name="format" type="text" value="<%=domain.getFormat()%>"/>											
											<br />
											<input type="submit" value="сохранить" />
										</form>
										<h2>Добавить значение в домен</h2>
										<form name="editForm" action="admin_add_domain_value.domain" method="post">
											<input type="hidden" name="currentName" value="<%=domain.getName()%>"/>
											<input type="text" name="value"/>
											<input type="submit" value="Добавить" />
										</form>
										<h2>Значения домена</h2>
										<% 
										for (String value : domain.getValues()) {
											%>
											<div class="item">
												<div class="name"><%=value%></div>
												<a 
													href="admin_delete_domain_value.domain?value=<%=value%>&currentName=<%=domain.getName() %>" 
													class="delete">
												</a>
											</div>
											<%
										}
										%>
									</div>
								</div>
							</td>
						</tr>
					</table>
					<% } %>
				</td>
			</tr>
		</table>	
	</div>

</body>
</html>