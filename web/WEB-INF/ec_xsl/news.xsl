<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="inner_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="pagination" select="page/news/news_item_pages"/>

	<!-- ****************************    НОМЕРА СТРАНИЦ    ******************************** -->


	<xsl:template name="INNER_CONTENT">
	<div class="col-xs-12">
		<div class="path hidden-xs">
			<a href="{page/index_link}">Главная страница</a> →
		</div>
		<h2 class="m-t-zero">Новости</h2>
		<div class="row section-items">
			<xsl:for-each select="page/news/news_item">
				<div class="col-md-3 col-sm-6 col-xs-12">
					<a href="{show_news_item}" class="section-thumbnail">
						<xsl:if test="header_pic and not(header_pic = '')">
							<xsl:attribute name="style" select="concat('background-image: url(', @path, header_pic, ');')"/>
						</xsl:if>
					</a>
					<a href="{show_news_item}">
						<h4><xsl:value-of select="header"/></h4>
					</a>
					<xsl:value-of select="short" disable-output-escaping="yes"/>
					<span class="date"><xsl:value-of select="date"/></span>
				</div>
				<xsl:if test="position() mod 4 = 0">
					<div class="clearfix"></div>
				</xsl:if>
			</xsl:for-each>
		</div>
		<xsl:if test="$pagination">
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">
					<xsl:apply-templates select="$pagination" mode="pagination"/>
				</div>
			</div>
		</xsl:if>
	</div>
	</xsl:template>


</xsl:stylesheet>