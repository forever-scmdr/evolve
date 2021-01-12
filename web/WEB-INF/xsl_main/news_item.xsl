<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="$ni/header" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'news'"/>
	<xsl:variable name="ns" select="page/news[@id = $ni/news/@id]"/>
	<xsl:variable name="sel_news_id" select="$ns/@id"/>


	<xsl:variable name="ni" select="page/news_item"/>
	<xsl:variable name="canonical" select="concat('/', $ni/@key, '/')"/>

	<xsl:template name="PAGE_PATH">
		<div class="path path_common">

			<div class="path__item">
				<a href="{$main_host}" class="path__link">Главная страница</a>
				<div class="path__arrow"></div>
			</div>
			<div class="path__item">
				<a href="{$ns/show_page}" class="path__link"><xsl:value-of select="$ns/name"/></a>
				<div class="path__arrow"></div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="INC_SIDE_MENU_INTERNAL_NEWS"/>
		<xsl:call-template name="COMMON_LEFT_COLOUMN"/>
	</xsl:template>


	<xsl:template name="CONTENT">
		<div class="text">
			<xsl:value-of select="$ni/text" disable-output-escaping="yes"/>
		</div>
		<xsl:if test="$seo/bottom_text !=''">
			<div class="text seo">
				<xsl:value-of select="$seo/bottom_text" disable-output-escaping="yes"/>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>