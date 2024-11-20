<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="title" select="if(page/@name = 'all_docs') then 'Документы' else '3D модели'"/>

	<xsl:variable name="active_mmi" select="if(page/@name = 'all_docs') then 'docs' else '3d'"/>

	<xsl:variable name="info" select="if(page/@name = 'all_docs') then page/docs else page/three_dmodels"/>

	<xsl:variable name="critical_item" select="$info"/>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:template name="CONTENT">
	<div class="spacer"></div>
	<div class="container main-content">
		<div class="row">
			<div class="col-xs-12">
				<div class="row">
					<div class="col-xs-12">
						<div class="path hidden-xs">
							<a href="{$base}">Главная страница</a>
							→
						</div>
						<h2 class="no-top-margin">
							<xsl:value-of select="if(page/@name = 'all_docs') then 'Все документы' else 'Все 3D модели'"/>
						</h2>
					</div>
				</div>
				<div class="row section-links">
					<xsl:for-each select="$info/doc_section">
						<div class="col-xs-6 col-sm-4">
							<a href="{show_section}">
								<img src="{@path}{pic}" alt="" style="max-width: 100%;"/>
							</a>
							<a href="{show_section}"><xsl:value-of select="name" /></a>
						</div>
						<xsl:if test="position() mod 2 = 0">
							<div class="clearfix hidden-sm hidden-md hidden-lg"></div>
						</xsl:if>
						<xsl:if test="position() mod 3 = 0">
							<div class="clearfix hidden-xs"></div>
						</xsl:if>					
					</xsl:for-each>
				</div>
			</div>
		</div>
	</div>
	</xsl:template>

</xsl:stylesheet>