<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>


	<xsl:variable name="view" select="page/variables/view"/>
	<xsl:variable name="products" select="page/product"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a>
			</div>
			<span><i class="fas fa-print"></i> <a href="">Распечатать</a></span>
		</div>
		<h1>Поиск по запросу "<xsl:value-of select="page/variables/q"/>"</h1>

		<div class="page-content m-t">

			<xsl:if test="$products">
				<div class="view-container desktop">
					<div class="view">
						<span>Показывать:</span>
						<span><i class="fas fa-th-large"></i> <a href="{page/set_view_table}">Плиткой</a></span>
						<span><i class="fas fa-th-list"></i> <a href="{page/set_view_list}">Строками</a></span>
						<div class="checkbox">
							<label>
								<input type="checkbox"/> в наличии
							</label>
						</div>
					</div>
				</div>
			</xsl:if>

			<div class="catalog-items{' lines'[$view = 'list']}">
				<xsl:apply-templates select="$products"/>
				<xsl:if test="not($products)">
					<h4>По заданным критериям товары не найдены</h4>
				</xsl:if>
			</div>

		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
	</xsl:template>

</xsl:stylesheet>