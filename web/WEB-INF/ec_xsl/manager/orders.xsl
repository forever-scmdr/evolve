<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="managers" select="page/manager"/>

	<xsl:template name="CONTENT">
		<h1 class="adm-page-title">История заказов</h1>
		<div class="adm-table">
			<div class="adm-table-row adm-table-head">
				<div class="adm-table-cell adm-table-head-cell">Дата</div>
				<div class="adm-table-cell adm-table-head-cell">Номер заказа</div>
				<div class="adm-table-cell adm-table-head-cell">Пользователь</div>
				<div class="adm-table-cell adm-table-head-cell">Статус</div>
				<div class="adm-table-cell adm-table-head-cell">Менеджер</div>
				<div class="adm-table-cell adm-table-head-cell">Скачать .xlsx</div>
				<div class="adm-table-cell adm-table-head-cell">Сумма, руб.</div>
			</div>
			<xsl:for-each select="page/purchase">
				<xsl:variable name="status" select="if (status = '0') then 'Новый' else if (status = '1') then 'В обработке' else 'Закрыт'"/>
				<div class="adm-table-row">
					<div class="adm-table-cell"><xsl:value-of select="date" /></div>
					<div class="adm-table-cell"><a href="{show_purchase}"><xsl:value-of select="num" /></a></div>
					<div class="adm-table-cell"><xsl:value-of select="user/name" /></div>
					<div class="adm-table-cell"><xsl:value-of select="$status" /></div>
					<div class="adm-table-cell">
						<select name="" id="">
							<xsl:for-each select="$managers">
								<option value="{email}"><xsl:value-of select="name" /></option>
							</xsl:for-each>
						</select>
						<button class="adm-button adm-button_small">Ок</button>
					</div>
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