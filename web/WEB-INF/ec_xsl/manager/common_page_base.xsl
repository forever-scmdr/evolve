<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br /&gt;</xsl:text></xsl:template>

	<!-- <TITLE> -->
	
	<xsl:template name="TITLE">Белтесто - администратор</xsl:template>


	<xsl:variable name="is_users" select="page/@name = ('users_all', 'users')"/>
	<xsl:variable name="is_all" select="page/@name = ('users_all', 'orders_all')"/>

	<xsl:template name="CONTENT"/>


	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="success" select="page/variables/success = 'true'"/>


	<!-- ****************************    СТРАНИЦА    ******************************** -->






	<xsl:template match="/">
	<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html"&gt;
	</xsl:text>
	<html xmlns:f="f:f" lang="en">

		<head>
			<base href="{page/base}"/>
			<meta charset="utf-8"/>
			<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
			<meta name="viewport" content="width=device-width, initial-scale=1"/>
			<title><xsl:call-template name="TITLE"/></title>
			<link href="https://fonts.googleapis.com/css?family=Roboto+Slab:100,300,400,700&amp;subset=cyrillic,cyrillic-ext" rel="stylesheet" />
			<link rel="stylesheet" href="css/app.css"/>
			<link rel="stylesheet" type="text/css" href="slick/slick.css"/>
			<link rel="stylesheet" type="text/css" href="slick/slick-theme.css"/>
			<link rel="stylesheet" href="fotorama/fotorama.css"/>
			<link rel="stylesheet" href="admin/jquery-ui/jquery-ui.css"/>
			<script defer="defer" src="js/font_awesome_all.js"/>
			<script type="text/javascript" src="admin/js/jquery-3.2.1.min.js"/>
		</head>
		<body>
			<div class="content-container">
				<div class="container">
					<div class="adm-header">
						<div class="adm-logo">
							<img class="adm-logo__image" src="img/logo_big.svg" alt=""></img>
						</div>
						<div class="adm-user">
							<xsl:value-of select="page/user/@name"/>
							<a href="{page/logout_link}" class="adm-button">Выход</a>
						</div>
					</div>
					<!-- alert-info, alert-warning alert-danger -->
					<xsl:if test="$message">
						<div class="alert {'alert-success'[$success]}{'alert-danger'[not($success)]}" role="alert">
							<xsl:value-of select="$message"/>
						</div>
					</xsl:if>
					<div class="tabs adm-tabs">
						<ul class="nav nav-tabs adm-tabs-container" role="tablist">
							<li role="presentation" class="{if ($is_users) then 'adm-tab_secondary' else 'active'} adm-tab">
								<a href="{page/orders_link}">Заказы</a>
							</li>
							<li role="presentation" class="{if ($is_users) then 'active' else 'adm-tab_secondary'} adm-tab">
								<a href="{page/users_link}">Пользователи</a>
							</li>
						</ul>
					</div>
					<xsl:call-template name="CONTENT"/>
				</div>
			</div>
			<script type="text/javascript" src="js/bootstrap.js"/>
			<script type="text/javascript" src="admin/ajax/ajax.js"/>
			<script type="text/javascript" src="admin/js/jquery.form.min.js"/>
			<script type="text/javascript" src="admin/jquery-ui/jquery-ui.js"/>
			<script type="text/javascript" src="js/fwk/common.js"/>
		</body>

	</html>
	</xsl:template>


</xsl:stylesheet>