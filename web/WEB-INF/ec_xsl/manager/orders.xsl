<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="managers" select="page/manager"/>
	<xsl:variable name="user" select="page/manager/user_jur | page/manager/user_phys | page/user_jur | page/user_phys"/>

	<xsl:template name="CONTENT">
		<h1 class="adm-page-title">
			История заказов
			<xsl:if test="$user">
				пользователя
				<xsl:value-of select="if ($user/organization) then $user/organization else $user/name"/>
			</xsl:if>
		</h1>
		<div class="adm-table">
			<div class="adm-table-row adm-table-head">
				<div class="adm-table-cell adm-table-head-cell">Дата</div>
				<div class="adm-table-cell adm-table-head-cell">Номер заказа</div>
				<div class="adm-table-cell adm-table-head-cell">Пользователь</div>
				<div class="adm-table-cell adm-table-head-cell">Статус</div>
				<xsl:if test="$is_all"><div class="adm-table-cell adm-table-head-cell">Менеджер</div></xsl:if>
				<div class="adm-table-cell adm-table-head-cell">Скачать .xlsx</div>
				<div class="adm-table-cell adm-table-head-cell">Сумма, руб.</div>
			</div>
			<xsl:for-each select="page//purchase">
				<xsl:variable name="status" select="$all_status[number(current()/status) + 1]"/>
				<xsl:variable name="manager" select="manager/email"/>
				<div class="adm-table-row">
					<div class="adm-table-cell"><xsl:value-of select="date" /></div>
					<div class="adm-table-cell"><a href="{show_purchase}"><xsl:value-of select="num" /></a></div>
					<div class="adm-table-cell"><xsl:value-of select="user/name" /></div>
					<div class="adm-table-cell"><xsl:value-of select="$status" /></div>
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
					<div class="adm-table-cell"><a href=""><i class="fas fa-file-excel adm-download-xlsx"></i></a></div>
					<div class="adm-table-cell"><xsl:value-of select="sum" /></div>
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