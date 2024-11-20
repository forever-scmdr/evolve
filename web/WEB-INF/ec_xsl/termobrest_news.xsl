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

	<xsl:variable name="sec" select="page/news_section"/>

	<xsl:variable name="title" select="'Новости'"/>

	<xsl:template match="news_item_pages">
	<div class="btn-group pagination" role="group">
		<xsl:apply-templates select="page | current_page"/>
	</div>
	</xsl:template>

	<xsl:template match="current_page">
		<button type="button" class="btn btn-info active-link" onclick="location.href='{link}'"><xsl:value-of select="number"/></button>
	</xsl:template>

	<xsl:template match="page">
		<button type="button" class="btn btn-info" onclick="location.href='{link}'"><xsl:value-of select="number"/></button>
	</xsl:template>


	<xsl:variable name="news_src" select="page/news"/>
	<xsl:variable name="heading" select="if($sec/name != '') then $sec/name else 'Новости'"/>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:variable name="active_mmi" select="'news'"/>

	<xsl:template name="CONTENT">
	<div class="container main-content" style="width: 100%;" id="wrapper">
				<div class="row section-items">
					<xsl:for-each select="/page/news_item">
						<div class="col-xs-6 col-sm-4 col-md-4">
							<a href="{show_news_item}">
								<img src="{@path}{main_pic}" alt="{header}" style="max-width: 100%;"/>
							</a>
							<a href="{show_news_item}"><xsl:value-of select="header"/></a>
						</div>
						<xsl:if test="position() mod 3 = 0 and position() &gt; 0">
							<div class="clearfix"></div>
							<xsl:text disable-output-escaping="yes">
								&lt;/div&gt;&lt;div class="row section-items"&gt;
							</xsl:text>
						</xsl:if>
						<!-- <xsl:if test="position() mod 2 = 0">
							<div class="clearfix hidden-md hidden-lg"></div>
						</xsl:if>
						<xsl:if test="position() mod 3 = 0">
							<div class="clearfix hidden-xs hidden-sm"></div>
						</xsl:if> -->
					</xsl:for-each>
			</div>
			<div class="row">
				<xsl:apply-templates select="/page/news_item_pages"/>
			</div>
	</div>
	</xsl:template>

</xsl:stylesheet>