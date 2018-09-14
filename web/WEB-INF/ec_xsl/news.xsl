<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="active_menu_item" select="'news'"/>
	<xsl:variable name="title" select="'Новости'" />

	<xsl:variable name="p" select="page/product"/>


	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a> &gt;
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1><xsl:value-of select="if (page/news) then 'Новости' else 'Статьи'"/></h1>

		<div class="page-content m-t">
			<div class="catalog-items info">
				<xsl:for-each select="page//news_item">
					<div class="catalog-item">
						<a href="{show_news_item}" class="image-container" style="background-image: url('{@path}{main_pic}');"><!-- <img src="{@path}{main_pic}" alt=""/> --></a>
						<div class="text">
							<div class="date"><xsl:value-of select="date"/></div>
							<a href="{show_news_item}"><xsl:value-of select="header"/></a>
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