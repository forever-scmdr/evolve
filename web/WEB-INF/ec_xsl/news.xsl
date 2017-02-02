<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	version="1.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<!-- ****************************    НОМЕРА СТРАНИЦ    ******************************** -->

	<xsl:template match="pages">
		<xsl:if test="news_item_page">
			<div class="page-count">
				<b>Страница:<xsl:call-template name="NBSP"/></b>
				<xsl:apply-templates/>
			</div>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="pages/news_item_current_page">
		<a href="{link}" class="open"><xsl:value-of select="number"/></a>
	</xsl:template>
	
	<xsl:template match="pages/news_item_page">
		<a href="{link}"><xsl:value-of select="number"/></a>
	</xsl:template>
    
 	<xsl:template match="pages/news_item_next"></xsl:template>
    
 	<xsl:template match="pages/news_item_previous"></xsl:template>

	<xsl:variable name="current_page_class" select="'news'"/>

	<xsl:template name="CONTENT">
	<div class="common">
		<div class="path"><a href="{/page/index_link}">Главная страница</a><xsl:call-template name="arrow"/></div>
		<xsl:call-template name="PAGE_TITLE"><xsl:with-param name="page" select="/page/news"/></xsl:call-template>
		<xsl:for-each select="/page/news/news_item">
			<div class="news-item">
				<p class="date"><xsl:value-of select="date"/></p>
				<h2><a href="{show_news_item}"><xsl:value-of select="header"/></a></h2>
				<xsl:value-of select="short" disable-output-escaping="yes"/>
			</div>
		</xsl:for-each>
		<xsl:apply-templates select="/page//pages"/>
	</div>
	</xsl:template>


</xsl:stylesheet>