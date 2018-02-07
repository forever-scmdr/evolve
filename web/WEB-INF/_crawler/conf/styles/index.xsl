<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0"
	exclude-result-prefixes="xsl">
	<xsl:import href="../styles_fwk/utils.xsl"/>
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="/">
	<result>
		<xsl:for-each select="//ul[contains(@class, 'two-level menu_vert')]">
			<catalog id="catalog">
				<xsl:for-each select="li">
					<section type="main" id="{f:url_id(a/@href)}" name="{a/span}">
						<xsl:for-each select="ul/li">
							<section type="second" id="{f:url_id(a/@href)}" name="{a/span}"/>
						</xsl:for-each>
					</section>
					<!--<small action="ignore" download="{//base/@href}{a/img/@src}">small.jpg</small>-->
				</xsl:for-each>
			</catalog>
		</xsl:for-each>
	</result>
	</xsl:template>

</xsl:stylesheet>