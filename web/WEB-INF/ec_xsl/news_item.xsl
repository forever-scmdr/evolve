<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="ni" select="page/news_item"/>


	<xsl:template name="CONTENT">
		<xsl:call-template name="INC_MOBILE_HEADER"/>
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a> &gt;
				<xsl:choose>
					<xsl:when test="$ni/news"><a href="{page/news_link}">Новости</a> &gt;</xsl:when>
					<xsl:otherwise><a href="{page/articles_link}">Статьи</a> &gt;</xsl:otherwise>
				</xsl:choose>
			</div>
			<span><i class="fas fa-print"></i> <a href="">Распечатать</a></span>
		</div>
		<h1><xsl:value-of select="$ni/header"/></h1>

		<div class="page-content m-t">
			<xsl:value-of select="$ni/text" disable-output-escaping="yes"/>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

</xsl:stylesheet>