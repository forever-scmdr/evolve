<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f"
		version="2.0">
	<xsl:import href="section.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Сравнение товаров'" />


	<xsl:template name="CONTENT">
		<div class="compare-items">
			<xsl:for-each select="page/product">
				<div class="compare-items__item">
					<xsl:apply-templates select="." />
					<ul class="compare-items__params">
						<xsl:for-each select="params/param">
							<li>
								<span><xsl:value-of select="@caption"/></span>
								<span><xsl:value-of select="."/></span>
							</li>
						</xsl:for-each>
					</ul>
				</div>
			</xsl:for-each>
		</div>
	</xsl:template>

</xsl:stylesheet>