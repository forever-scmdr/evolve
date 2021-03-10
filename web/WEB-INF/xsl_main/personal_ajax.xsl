<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="registered" select="page/user/group/@name = 'registered'"/>

	<xsl:template match="/">
		<xsl:if test="not($registered)">
			<div class="result" id="personal_desktop">
				<div class="header-icon__icon">
					<img src="img/icon-user.png" alt="" />
				</div>
				<div class="dropdown-menu">
					<div class="dropdown-menu__container">
						<a class="dropdown-menu__link" href="{page/login_link}">Вход / регистрация</a>
					</div>
				</div>
			</div>
		</xsl:if>
		<xsl:if test="$registered">
			<div class="result" id="personal_desktop">
				<div class="header-icon__icon">
					<img src="img/icon-user.png" alt="" />
				</div>
				<div class="dropdown-menu">
					<div class="dropdown-menu__container">
						<a class="dropdown-menu__link" href="{page/personal_link}">Аккаунт</a>
						<a class="dropdown-menu__link" href="{page/purchase_history_link}">История заказов</a>
						<a class="dropdown-menu__link" href="/logout.login?target=index">Выход</a>
					</div>
				</div>
			</div>
		</xsl:if>
	</xsl:template>



</xsl:stylesheet>
