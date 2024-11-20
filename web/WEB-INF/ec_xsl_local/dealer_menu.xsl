<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="heading" select="'Дилерам'"/>

	<!-- ****************************          МЕНЮ            **************************** -->

	<xsl:variable name="dsec" select="page/dealer_section"/>

	<xsl:template match="dealer_section[.//@id = $dsec/@id]" mode="first">
		<xsl:variable name="active" select="@id = $dsec/@id"/>
		<li class="w-clearfix side-menu-item open">
			<a href="{show_section}" class="side-link" style="{'text-decoration: none; color: #bf0000'[$active]}"><xsl:value-of select="name"/></a>
			<xsl:apply-templates select="dealer_section" mode="second"/>
		</li>
	</xsl:template>
	
	<xsl:template match="dealer_section" mode="first">
		<li class="side-menu-item">
			<a href="{show_section}" class="side-link"><xsl:value-of select="name"/></a>
		</li>
	</xsl:template>
	
	<xsl:template match="dealer_section" mode="second">
		<xsl:variable name="active" select="@id = $dsec/@id"/>
		<div class="side_sublink _2{' active'[$active]}">
			<div>
				<a href="{show_section}" style="{'text-decoration: none; color: #bf0000'[$active]}"><xsl:value-of select="name"/></a>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="MENU_EXTRA">
		<xsl:apply-templates select="page/dealer_info/dealer_section" mode="first"/>
	</xsl:template>

</xsl:stylesheet>