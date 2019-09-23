<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="registered" select="page/user/group/@name = 'registered'"/>

	<xsl:template match="/">
		<xsl:if test="not($registered)">
			<div class="result" id="personal_desktop">
				<i class="fas fa-lock"></i><a href="" id="login_click">Вход</a> / <a href="/register/">Регистрация</a>
			</div>
			<div class="result" id="personal_mobile">
				<i class="fas fa-lock"></i>
				<a href="{page/login_link}"> Вход / Регистрация</a>
			</div>
		</xsl:if>
		<xsl:if test="$registered">
			<div class="result" id="personal_desktop">
				<i class="fas fa-lock"></i><a href="{page/personal_link}"> Анкета</a> / <a href="{page/purchase_history_link}">Заказы</a> / <a href="logout.login?target=index" title="Выход"><i class="fas fa-times"/></a>
			</div>
			<div class="result" id="personal_mobile">
				<i class="fas fa-lock"></i>
				<a href="{page/personal_link}"> Анкета</a> / <a href="{page/purchase_history_link}">Заказы</a> / <a href="logout.login?target=index">Выход</a>
			</div>
		</xsl:if>
	</xsl:template>


	<xsl:template name="PERSONAL_DESKTOP">
		<div id="personal_desktop" ajax-href="{page/personal_ajax_link}">
			<i class="fas fa-lock"></i><a href="#" id="login_click" mouseenter="$('.login-popup').show('fade', 150)">Вход</a> / <a href="/register/">Регистрация</a>
		</div>
	</xsl:template>

	<xsl:template name="PERSONAL_MOBILE">
		<div id="personal_mobile">
			<i class="fas fa-lock"></i>
			<a href="{page/login_link}"> Вход / Регистрация</a>
		</div>
	</xsl:template>


</xsl:stylesheet>