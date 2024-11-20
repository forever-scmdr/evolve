<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="iframe_page_base.xsl" />
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="ni" select="page/news_item"/>
	<xsl:variable name="sec" select="$ni/news_section"/>

	<xsl:variable name="title" select="$ni/header"/>
	<xsl:variable name="canonical" select="/page/canonical"/>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:variable name="active_mmi" select="'news'"/>

	<xsl:template name="CONTENT">
	<div class="container main-content" style="width: 100%;">
		<div class="row">
			<div class="col-xs-12 content-block">
				<h1>
					<xsl:value-of select="$ni/header"/>
				</h1>
				<xsl:value-of select="$ni/text" disable-output-escaping="yes"/>
				<p>
					<a href="{concat(page/base,'/', 'termobrest_news')}">К списку новостей →</a>
				</p>
			</div>
		</div>
	</div>
	</xsl:template>

</xsl:stylesheet>