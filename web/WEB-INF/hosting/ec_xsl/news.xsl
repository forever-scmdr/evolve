<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="page/selected_news/name" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'news'"/>

	<xsl:variable name="p" select="page/product"/>


	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a> &gt;
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1><xsl:value-of select="$h1"/></h1>

		<div class="page-content m-t">
			<div class="catalog-items info">
				<xsl:for-each select="page/selected_news/news_item">
					<div class="catalog-item">
						<a href="{show_news_item}" class="image-container" style="background-image: url('http://alfacomponent.com/{@path}{main_pic}');"><!-- <img src="http://alfacomponent.com/{@path}{main_pic}" alt=""/> --></a>
						<div class="text">
							<div class="date"><xsl:value-of select="date"/></div>
							<a href="{show_news_item}" style="height: 9.7rem"><xsl:value-of select="header"/></a>
							<xsl:value-of select="short" disable-output-escaping="yes"/>
						</div>
					</div>
				</xsl:for-each>
			</div>
		</div>

		<xsl:if test="page//news_item_pages">
			<div class="pagination">
				<span>Странциы:</span>
				<div class="pagination-container">
					<xsl:for-each select="page//news_item_pages/page">
						<a href="{link}" class="{'active'[current()/@current]}"><xsl:value-of select="number"/></a>
					</xsl:for-each>
				</div>
			</div>
		</xsl:if>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

</xsl:stylesheet>