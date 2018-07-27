<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="managers" select="page/manager"/>
	<xsl:variable name="p" select="page/purchase"/>
	<xsl:variable name="u" select="$p/user"/>
	<xsl:variable name="prods" select="page/product"/>

	<xsl:template name="CONTENT">
		<h1 class="adm-page-title">№ <xsl:value-of select="$p/num"/>. <xsl:value-of select="$u/name"/><xsl:value-of select="$u/organization"/> </h1>
		<div class="adm-order-status-line">
			<div class="adm-order-details">
				<div class="adm-order-details__item">Дата: <strong><xsl:value-of select="$p/date" /></strong></div>
				<div class="adm-order-details__item">Сумма: <strong><xsl:value-of select="$p/sum" /> руб.</strong></div>
				<div class="adm-order-details__item">Доставка: <strong><xsl:value-of select="$u/ship_type" /></strong></div>
				<div class="adm-order-details__item">
					Статус заказа:
					<form method="post" action="{page/set_status}" style="display: inline">
						<select name="status" value="{$p/status}">
							<xsl:for-each select="$all_status">
								<option value="{position() - 1}"><xsl:value-of select="." /></option>
							</xsl:for-each>
						</select>
						<button class="adm-button adm-button_small" type="submit">Сохранить</button>
					</form>
				</div>
			</div>
			<!--<a href=""><i class="fas fa-file-excel adm-download-xlsx"></i> Скачать .xlsx</a>-->
		</div>
		<div class="adm-table">
			<div class="adm-table-row adm-table-head">
				<div class="adm-table-cell adm-table-head-cell">№</div>
				<div class="adm-table-cell adm-table-head-cell">Название</div>
				<div class="adm-table-cell adm-table-head-cell">Категория</div>
				<div class="adm-table-cell adm-table-head-cell">Количество</div>
				<div class="adm-table-cell adm-table-head-cell">Цена, руб.</div>
				<div class="adm-table-cell adm-table-head-cell">Сумма, руб</div>
			</div>
			<xsl:for-each select="$p/bought">
				<xsl:variable name="prod" select="$prods[code = current()/code]"/>
				<div class="adm-table-row">
					<div class="adm-table-cell"><xsl:value-of select="position()" /></div>
					<div class="adm-table-cell">
						<a href="{$prod/section/show_product}" target="_blank">
							<xsl:value-of select="concat(substring(code, 1, 4), ' ', substring(code, 4))"/>&#160;<xsl:value-of select="$prod/name"/>
						</a>
					</div>
					<div class="adm-table-cell"><xsl:value-of select="$prod/section[1]/name"/></div>
					<div class="adm-table-cell"><xsl:value-of select="qty"/></div>
					<div class="adm-table-cell"><xsl:value-of select="price"/></div>
					<div class="adm-table-cell"><xsl:value-of select="sum"/></div>
				</div>
			</xsl:for-each>
		</div>
	</xsl:template>

</xsl:stylesheet>