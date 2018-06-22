<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="managers" select="page/manager"/>

	<xsl:template name="CONTENT">
		<h1 class="adm-page-title">Пользователи</h1>
		<div class="adm-table">
			<div class="adm-table-row adm-table-head">
				<div class="adm-table-cell adm-table-head-cell"></div>
				<div class="adm-table-cell adm-table-head-cell">Пользователь</div>
				<xsl:if test="$is_all"><div class="adm-table-cell adm-table-head-cell">Менеджер</div></xsl:if>
				<div class="adm-table-cell adm-table-head-cell"></div>
			</div>
			<xsl:for-each select="page//user_phys | page//user_jur">
				<xsl:variable name="manager" select="manager/email"/>
				<div class="adm-table-row">
					<div class="adm-table-cell"><xsl:value-of select="position()" /></div>
					<div class="adm-table-cell"><xsl:value-of select="if (organization) then organization else name" /></div>
					<xsl:if test="$is_all">
						<div class="adm-table-cell">
							<form method="post" action="{set_manager}">
								<select name="manager" value="{$manager}">
									<option value=""></option>
									<xsl:for-each select="$managers">
										<option value="{email}"><xsl:value-of select="name" /></option>
									</xsl:for-each>
								</select>
								<button class="adm-button adm-button_small" type="submit">Ок</button>
							</form>
						</div>
					</xsl:if>
					<div class="adm-table-cell"><a href="{show_orders}">Заказы</a></div>
				</div>
			</xsl:for-each>
		</div>
		<!--
		<div class="adm-pages">
			Страницы:
			<a href="" class="adm-pages__link adm-pages__link_active">1</a>
			<a href="" class="adm-pages__link">2</a>
			<a href="" class="adm-pages__link">3</a>
			<a href="" class="adm-pages__link">4</a>
		</div>
		-->
	</xsl:template>

</xsl:stylesheet>