<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>

	<xsl:variable name="title">Поиск по запросу "<xsl:value-of select="page/variables/q"/>"</xsl:variable>
	<xsl:variable name="active_menu_item" select="'catalog'"/>

	<xsl:variable name="view" select="page/variables/view"/>
	<xsl:variable name="products" select="page/product"/>
	<xsl:variable name="only_available" select="page/variables/minqty = '0'"/>

	<xsl:variable name="h1" select="if(not($seo/h1 != '')) then concat('Поиск по запросу ', page/variables/q) else $seo/h1"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="{$main_host}">Главная страница</a> &gt; <a href="{page/catalog_link}">Каталог</a>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1><xsl:value-of select="$h1"/></h1>

		<xsl:if test="$seo[1]/text">
			<div class="page-content m-t ">
				<xsl:value-of select="$seo[1]/text" disable-output-escaping="yes"/>
			</div>
		</xsl:if>

		<div class="page-content m-t">

			<xsl:if test="$products">
				<div class="view-container desktop">
					<div class="view">
						<span>Показывать:</span>
						<span><i class="fas fa-th-large"></i> <a href="{page/set_view_table}">Плиткой</a></span>
						<span><i class="fas fa-th-list"></i> <a href="{page/set_view_list}">Строками</a></span>
						<!-- <div class="checkbox">
							<label>
								<xsl:if test="not($only_available)">
									<input type="checkbox" onclick="window.location.href = '{page/show_only_available}'"/>
								</xsl:if>
								<xsl:if test="$only_available">
									<input type="checkbox" checked="checked" onclick="window.location.href = '{page/show_all}'"/>
								</xsl:if>
								в наличии
							</label>
						</div> -->
					</div>
				</div>
			</xsl:if>

			<div class="catalog-items{' lines'[$view = 'list']}">
				<xsl:apply-templates select="$products"/>
				<xsl:if test="not($products)">
					<h4>По заданным критериям товары не найдены</h4>
				</xsl:if>
			</div>

			<xsl:if test="$seo[1]/bottom_text">
				<div style="margin-top:20px;"></div>
				<xsl:value-of select="$seo/bottom_text" disable-output-escaping="yes"/>
			</xsl:if>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
	</xsl:template>

</xsl:stylesheet>