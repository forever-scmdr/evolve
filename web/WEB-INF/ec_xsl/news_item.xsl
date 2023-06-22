<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="inner_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="INNER_CONTENT">
	<div class="col-xs-12">
		<div class="path hidden-xs">
			<a href="{page/index_link}">Главная страница</a> →
			<a href="{page/news_link}">Новости</a> →
		</div>
		<h2 class="m-t-zero"><xsl:value-of select="/page/sn/header"/></h2>
		<div class="row">
			<div class="col-xs-12">
				<span class="date"><xsl:value-of select="/page/sn/date"/></span>
				<xsl:apply-templates select="page/sn" mode="content"/>
			</div>
		</div>
	</div>
	</xsl:template>


</xsl:stylesheet>