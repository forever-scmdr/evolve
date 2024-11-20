<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="dealer_menu.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="concat($dsec/name, ' - Дилерам')"/>



	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:variable name="active_mmi" select="'dealers'"/>

	<xsl:template name="CONTENT">
	<div class="w-section top-bottom-padding">
		<div class="w-container bg">
			<div class="w-row">
				<div class="w-col w-col-3">
					<h3 class="first-heading">Дилерам</h3>
					<div class="side-menu">
						<ul class="w-list-unstyled side-menu-list">
							<xsl:for-each select="page/dealer_info/news_section">
								<li class="side-menu-item">
									<a href="{show_section}" class="side-link"><xsl:value-of select="name"/></a>
								</li>
							</xsl:for-each>
							<xsl:call-template name="MENU_EXTRA"/>
						</ul>
					</div>
				</div>
				<div class="w-col w-col-9">
					<div class="path-line">
						<a class="path-link" href="{page/index_link}">Главная страница</a>
						→
						<xsl:variable name="prev_sec" select="page/dealer_info/dealer_section[dealer_section/@id = $dsec/@id]"/>
						<xsl:if test="$prev_sec">
							<a class="path-link" href="{$prev_sec/show_section}"><xsl:value-of select="$prev_sec/name"/></a>
							→
						</xsl:if>
					</div>
					<h2 class="first-heading page-heading"><xsl:value-of select="$dsec/name"/></h2>
					<div class="w-row about">
						<xsl:apply-templates select="$dsec" mode="content"/>
					</div>
				</div>
			</div>
		</div>
	</div>
	</xsl:template>

</xsl:stylesheet>