<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="active_menu_item" select="'news'"/>


	<xsl:variable name="ni" select="page/news_item"/>
	<xsl:variable name="brand_mask" select="page/variables/brand"/>
	<xsl:variable name="brand" select="page/brands/brand[mask = $brand_mask]"/>
	<xsl:variable name="sel_sec" select="page/sel_sec"/>

	<xsl:variable name="leaf_sections" select="page//section[not(section)]"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a> &gt;
				<xsl:value-of select="$brand/name"/>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1><xsl:value-of select="$brand/name"/></h1>
		<div class="brand-info">
			<div class="brand-logo">
				<img src="{$brand/@path}{$brand/pic}" alt="" />
			</div>
			<!-- <div class="brand-paper">
				<img src="http://placehold.it/50x100" alt=""/>
			</div> -->
		</div>
		<xsl:value-of select="$brand/text" disable-output-escaping="yes"/>
		<h3><xsl:value-of select="if ($sel_sec) then $sel_sec/name else 'Продукция'"/>&#160;<xsl:value-of select="$brand/name" /></h3>
		<div class="catalog-links">
			<xsl:for-each select="page/brand_section[@id = $leaf_sections/@id]">
				<a href="{set_section}" class="catalog-links__link"><xsl:value-of select="name" /></a>
			</xsl:for-each>
		</div>
		<div class="page-content m-t">
			<div class="catalog-items">
				<xsl:apply-templates select="page/product"/>
			</div>
		</div>

		<xsl:if test="page/product_pages">
			<div class="pagination">
				<span>Страницы:</span>
				<div class="pagination-container">
					<xsl:for-each select="page/product_pages/page">
						<a href="{replace(link, 'section/', '')}" class="{'active'[current()/@current]}"><xsl:value-of select="number"/></a>
					</xsl:for-each>
				</div>
			</div>
		</xsl:if>


		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="MAIN_CONTENT">
		<!-- MAIN COLOUMNS BEGIN -->
		<div class="container-fluid inner-content">
			<div class="container">
				<div class="row">

					<!-- RIGHT COLOUMN BEGIN -->
					<div class="col-xs-12 main-content">
						<div class="mc-container">
							<xsl:call-template name="INC_MOBILE_HEADER"/>
							<xsl:call-template name="CONTENT"/>
						</div>
					</div>
					<!-- RIGHT COLOUMN END -->
				</div>
			</div>
		</div>
		<!-- MAIN COLOUMNS END -->
	</xsl:template>

</xsl:stylesheet>