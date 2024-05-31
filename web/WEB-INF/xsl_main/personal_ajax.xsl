<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="registered" select="page/user/group/@name = 'registered'"/>

	<xsl:template match="/">
		<xsl:if test="not($registered)">
			<div class="result" id="personal_desktop_login">
				<a href="{page/login_link}" class="icon-link">
					<div class="icon"><img src="img/icon-lock.svg" alt="" /></div>
					<span class="icon-link__item">Вход / Регистрация</span>
				</a>
				<script>var REGISTERED = false;</script>
			</div>
		</xsl:if>
		<xsl:if test="$registered">
			<div class="result" id="personal_desktop_login">
				<a href="/logout.login?target=index" class="icon-link"> <b><xsl:value-of select="page/user/@name"/>:</b>
					<div class="icon"><img src="img/icon-exit.svg" alt="" /></div>
					<span class="icon-link__item">Выход</span>
				</a>
				<script>var REGISTERED = true;</script>
			</div>
			<div class="result" id="personal_desktop_extra">
				<div style="margin-left: 10px; margin-top: 10px">
					<a href="{page/personal_link}" class="icon-link">
						<div class="icon"><img src="img/icon-history.svg" alt="" /></div>
						<span class="icon-link__item">Профиль</span>
					</a>
				</div>
				<div style="margin-left: 10px;">
					<a href="{page/purchase_history_link}" class="icon-link">
						<div class="icon"><img src="img/icon-bill.svg" alt="" /></div>
						<span class="icon-link__item">Заказы</span>
					</a>
				</div>
			</div>
		</xsl:if>
	</xsl:template>



</xsl:stylesheet>