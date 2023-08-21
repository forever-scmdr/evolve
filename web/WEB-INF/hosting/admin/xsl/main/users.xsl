<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" />
	<xsl:strip-space elements="*" />

	

	<xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
			"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;</xsl:text>
	</xsl:template>

	<xsl:template name="BR">
		<xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text>
	</xsl:template>

	<xsl:template name="HEAD">
		<head>
			<base href="{admin-page/domain}" />
			<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
			<meta http-equiv="Pragma" content="no-cache" />
			<link rel="stylesheet" type="text/css" href="admin/css/reset.css" />
			<link rel="stylesheet" type="text/css" href="admin/css/style.css" />
			<link rel="stylesheet" type="text/css" href="admin/css/main_admin.css" />
			<link rel="stylesheet" type="text/css" href="admin/jquery-ui/jquery-ui.css" media="screen" />
			<link rel="stylesheet" type="text/css" href="admin/js/jquery.fancybox.min.css" media="screen" />
			<title>
				Список пользователей
			</title>
		</head>
	</xsl:template>

	<xsl:template name="JS">
		<script type="text/javascript" src="admin/js/jquery-3.2.1.min.js" />
		<!-- UI -->
		<script type="text/javascript" src="admin/jquery-ui/jquery-ui.min.js"/>
		<!-- FORM -->
		<script type="text/javascript" src="admin/js/jquery.form.min.js"/>
		<!-- MCE -->
		<script type="text/javascript" src="admin/tinymce/tinymce.min.js"/>
		<script type="text/javascript" src="admin/js/regional-ru.js"/>
		<!-- FANCYBOX -->
		<script type="text/javascript" src="admin/js/jquery.fancybox.min.js"/>
		<!-- AJAX -->
		<script type="text/javascript" src="admin/ajax/ajax.js"/>
		<!-- ADMIN -->
		<script type="text/javascript" src="admin/js/admin.js"/>
	</xsl:template>
	
	<!-- **************************** СТРАНИЦА ******************************** -->

	<xsl:template match="/">
		<xsl:call-template name="DOCTYPE" />
		<html>
			<xsl:call-template name="HEAD" />
			<body>
				<div class="list position-relative" style="margin-top: 40px; width: 300px;">
					<form id="search-form" action="{admin-page/search-link}" method="POST"><!-- class="ajax-form"-->
						<input type="text" id="key_search" name="key_search" value="{admin-page/key_search}" placeholder="поиск"/>
						<a onclick="$(this).closest('form').submit()">искать</a>
					</form>
					<a href="{admin-page/search-link}">Очистить поиск</a>
				</div>
				<div class="deleted_items">
					<div class="item">
						<div style="clear: both"></div>
						<div class="main">
							<span>
								[группа не меняется]
							</span>
							<a class="name" title="открыть" href="{admin-page/no-user/update-link}" target="_parent">Нет владельца</a>
							<span>доступ только для суперюзера</span>
						</div>
					</div>
					<xsl:for-each select="admin-page/user">
						<div class="item">
							<!--
							<a class="delete control" title="удалить навсегда">удалить</a>
							<a class="move  control" title="переместить">переместить</a>
							<a class="restore  control" title="восстановить">восстановить</a>
							-->
							<div style="clear: both"></div>
							<div class="main">
								<span>
									[
									<xsl:if test="group[@is-admin = '1']">
										Админ: <xsl:value-of select="string-join(group[@is-admin = '1']/@name, ', ')"/>
									</xsl:if>
									<xsl:if test="group[@is-admin = '0']">
										Юзер: <xsl:value-of select="string-join(group[@is-admin = '0']/@name, ', ')"/>
									</xsl:if>
									]
								</span>
								<a class="name" title="открыть" href="{update-link}" target="_parent"><xsl:value-of select="@name"/></a>
								<span><xsl:if test="description"><xsl:value-of select="description"/></xsl:if></span>
							</div>
						</div>
					</xsl:for-each>
				</div>
				<xsl:call-template name="JS"/>
			</body>
		</html>
	</xsl:template>


</xsl:stylesheet>