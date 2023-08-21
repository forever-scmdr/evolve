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
	<link rel="stylesheet" type="text/css" href="admin/css/reset.css" />
	<link rel="stylesheet" type="text/css" href="admin/css/style.css" />
	
	<title>Управление доменами</title>
	<script src="admin/js/jquery-2.2.4.min.js"></script>
	<script type="text/javascript" src="admin/js/admin.js"></script>
</head>
<body>
	
	<!-- ************************ Основная форма **************************** -->
	<%
		DomainAdminServlet domainsBean = (DomainAdminServlet)request.getAttribute("data");
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
				<a href="<%=request.getServerName() + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort()) + request.getContextPath()%>" class="gray">
					<%=request.getServerName() + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort()) + request.getContextPath()%>
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
			<b>Управление доменами</b>
		</div>
		<div class="mid">
			<div class="left-col">
				<div class="list">
					<h4>Созадать новый домен</h4>
					<ul class="create-domain">
						<li>
							<form name="addForm" action="admin_create_domain.domain" method="post">
								<label>
									Название
									<input name="name" type="text" />
								</label>
								<label>
									Тип
									<select name="viewType">
										<option value="<%=DomainRegistry.CHECKBOX%>">Галка</option>
										<option value="<%=DomainRegistry.COMBOBOX%>">Выпадающий список</option>
										<option value="<%=DomainRegistry.RADIOGROUP%>">Список с точками</option>
									</select>
								</label>
								<label>
									Формат
									<input name="format" type="text" />
								</label>
								<a href="javascript:document.addForm.submit()" class="create-link" title="Создать">Создать</a>
							</form>
						</li>
					</ul>
					
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
				<div class="list">
					<h4>Существующие домены</h4>
					<ul class="edit">
						<%	for (String domainName: DomainRegistry.getDomainNames()) { %>
							<li class="drop-zone"></li>
							<li class=" visible single">
								<a href="javascript:selectDomain('<%=domainName%>')" class="name"><%=domainName%></a>
								<a href="javascript:deleteDomain('<%=domainName%>')" class="delete">удалить</a>
							</li>
						<% } %>
						<li class="drop-zone"></li>
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
					<h1><%=(domainsBean.hasDomainSet() ? "Домен: " + domainsBean.getCurrentName() : "Создайте или выберите домен")%></h1>
					<div class="edit-arena">
						<div class="wide">
							<div class="margin">
								<% if (domainsBean.hasDomainSet()) { %>
									<% Domain domain = DomainRegistry.getDomain(domainsBean.getCurrentName()); %>
									<form name="editForm" action="admin_update_domain.domain" method="post">
										<input type="hidden" name="currentName" value="<%=domain.getName()%>" />
										<div class="form-item text">
											<label>
												Название
												<input name="name" type="text" value="<%=domain.getName()%>"/>
											</label>
										</div>
										<div class="form-item">
											<label>
												Тип
												<select name="viewType" class="domain-type">
													<option value="<%=DomainRegistry.CHECKBOX%>"<%=(domain.getViewType().equalsIgnoreCase(DomainRegistry.CHECKBOX) ? " selected='selected'" : "")%>>Галка</option>
													<option value="<%=DomainRegistry.COMBOBOX%>"<%=(domain.getViewType().equalsIgnoreCase(DomainRegistry.COMBOBOX) ? " selected='selected'" : "")%>>Выпадающий список</option>
													<option value="<%=DomainRegistry.RADIOGROUP%>"<%=(domain.getViewType().equalsIgnoreCase(DomainRegistry.RADIOGROUP) ? " selected='selected'" : "")%>>Список с точками</option>
												</select>
											</label>
										</div>
										<div class="form-item text">
											<label>
												Формат
												<input name="format" type="text" value="<%=domain.getFormat()%>"/>
											</label>
										</div>
									</form>
									<h2>Добавить значение в домен</h2>
										<form name="editForm" action="admin_add_domain_value.domain" method="post">
											<input type="hidden" name="currentName" value="<%=domain.getName()%>"/>
											<div style="position: relative;">
												<input type="text" name="value"/>
												<input type="submit" class="add-domain-value" value="Добавить" />
											</div>
										</form>
										<h2>Значения домена</h2>
										<ul class="domain-values">
										<% 
										for (String value : domain.getValues()) {
											String href = "admin_delete_domain_value.domain?value="+value.trim()+"&currentName="+domain.getName();
											%>
											<li><%=value%><a href="<%=href%>" class="delete"></a>
											</li>
											<%
										}
										%>
										</ul>
								<% } %>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>