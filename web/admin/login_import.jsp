<%
	User currentUser = SessionContext.createSessionContext(request).getUser();
	if (currentUser == null || currentUser.isAnonimous() || !currentUser.getGroup().equalsIgnoreCase("common")) {
		response.sendRedirect("login.jsp?basic_url=admin_initialize.action");
		return;
	}
%>
