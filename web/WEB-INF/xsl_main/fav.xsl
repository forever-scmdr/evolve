<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="section.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Избранное'" />



	<xsl:template name="CONTENT">
		<!-- Отображние блоками/списком, товаров на страницу, сортировка, наличие -->
		<xsl:call-template name="DISPLAY_CONTROL"/>

		<div class="devices devices_section{' lines'[$view = 'list']}">
			<xsl:if test="$view = 'table'">
				<div class="devices__wrap">
					<xsl:apply-templates select="page/product" mode="product-table"/>
				</div>
			</xsl:if>
			<xsl:if test="$view = 'list'">
				<div class="devices__wrap devices__wrap_rows">
					<xsl:apply-templates select="page/product" mode="product-list"/>
				</div>
			</xsl:if>
			<xsl:if test="$not_found">
				<h4>Нет добавленных товаров</h4>
			</xsl:if>
		</div>
	</xsl:template>

</xsl:stylesheet>