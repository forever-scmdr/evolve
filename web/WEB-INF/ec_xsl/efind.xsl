<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="no"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="/">
		<data version="2.0">
			<xsl:for-each select="page/product">
				<item>
					<mfg><xsl:value-of select="vendor"/></mfg>
					<part><xsl:value-of select="name"/></part>
					<note><xsl:value-of select="type"/><xsl:text> </xsl:text><xsl:value-of select="name_extra"/></note>
					<img>http://alfacomponent.must.by/<xsl:value-of select="concat('http://alfacomponent.must.by/', @path, main_img)"/></img>
					<url>http://alfacomponent.must.by/<xsl:value-of select="show_product"/></url>
					<sku><xsl:value-of select="code"/></sku>
					<cur>RUR</cur>
					<stock>0</stock>
					<dlv>10-12 дней</dlv>
					<bid>0</bid>
				</item>
			</xsl:for-each>
		</data>
	</xsl:template>


</xsl:stylesheet>