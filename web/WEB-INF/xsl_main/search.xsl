<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>

	<xsl:variable name="title">Поиск по запросу "<xsl:value-of select="page/variables/q"/>"</xsl:variable>
	<xsl:variable name="h1">Поиск по запросу "<xsl:value-of select="page/variables/q"/>"</xsl:variable>
	<xsl:variable name="active_menu_item" select="'catalog'"/>

	<xsl:variable name="view" select="page/variables/view"/>
	<xsl:variable name="products" select="page/product"/>
	<xsl:variable name="only_available" select="page/variables/minqty = '0'"/>


	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a class="path__link" href="{$main_host}">Главная страница</a>
				<div class="path__arrow"></div>
			</div>
			<div class="path__item">
				<a class="path__link" href="{page/catalog_link}">Каталог</a>
				<div class="path__arrow"></div>
			</div>
		</div>

	</xsl:template>


	<xsl:template name="CONTENT">

		<xsl:if test="$products">
			<div class="view view_section">
				<div class="view__column">
					<a href="{page/set_view_table}" class="icon-link">
						<div class="icon">
							<img src="img/icon-grid.svg" alt="" />
						</div>
						<span class="icon-link__item">Плиткой</span>
					</a>
					<a href="{page/set_view_list}" class="icon-link">
						<div class="icon">
							<img src="img/icon-lines.svg" alt="" />
						</div>
						<span class="icon-link__item">Строками</span>
					</a>
				</div>
			</div>
		</xsl:if>


		<xsl:if test="$products">
			<div class="devices devices_section{' lines'[$view = 'list']}">
				<xsl:if test="$view = 'table'">
					<div class="devices__wrap">
						<xsl:apply-templates select="$products"/>
					</div>
				</xsl:if>
				<xsl:if test="$view = 'list'">
					<div class="devices__wrap devices__wrap_rows">
						<xsl:apply-templates select="$products" mode="lines"/>
					</div>
				</xsl:if>
				<xsl:if test="not($products)">
					<h4>По заданным критериям товары не найдены</h4>
				</xsl:if>
			</div>
		</xsl:if>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
	</xsl:template>

</xsl:stylesheet>
