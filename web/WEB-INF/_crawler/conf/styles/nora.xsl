<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="code_prefix" select="n_"/>
	<xsl:variable name="prod" select="//div[@class = 'product_card_frame'][1]"/>

	<xsl:variable name="name" select="//h1[1]"/>
	<xsl:variable name="text" select="$prod//div[@class = 'description-product']//div[@class = 'entry'][1]"/>
	<xsl:variable name="pic_links" select="$prod//div[@id = 'carousel-custom']//div[@class = 'carousel-inner']//a"/>
	<xsl:variable name="tech_table" select="$prod//div[contains(@class, 'holder_table')]//table"/>
	<xsl:variable name="selects" select="$prod//form[1]//select"/>

	<xsl:template match="/">
		<result>
			<xsl:call-template name="PRODUCT">
				<xsl:with-param name="select_index" select="1"/>
				<xsl:with-param name="value_path" select="()"/>
			</xsl:call-template>
		</result>
	</xsl:template>


	<xsl:template name="PRODUCT">
		<xsl:param name="select_index"/>
		<xsl:param name="value_path"/>
		<xsl:for-each select="$selects[$select_index]/option">
			<xsl:choose>
				<xsl:when test="$select_index = count($selects)">
					<xsl:variable name="code" select="tokenize(normalize-space(@class), ' ')[1]"/>
					<product id="{$code}">
						<name>
							<xsl:value-of select="normalize-space($name)" />
							<xsl:for-each select="$value_path"><xsl:text> </xsl:text><xsl:value-of select="normalize-space(.)" /></xsl:for-each>
							<xsl:text> </xsl:text><xsl:value-of select="normalize-space(.)" />
						</name>
						<vendor_code><xsl:value-of select="$code" /></vendor_code>
						<code><xsl:value-of select="concat($code_prefix, $code)" /></code>
						<!--<name_extra><xsl:value-of select="$colour" /></name_extra>-->
						<gallery>
							<xsl:for-each select="$pic_links">
								<pic download="http://nora-m.ru{@href}" link="http://nora-m.ru{@href}"/>
							</xsl:for-each>
						</gallery>
						<text>
							<xsl:copy-of select="$text"/>
						</text>
						<params_xml>
							<xsl:for-each select="$tech_table//tr[position() &gt; 1]">
								<parameter>
									<name><xsl:value-of select="normalize-space(td[1])" /></name>
									<value><xsl:value-of select="normalize-space(td[2])" /></value>
								</parameter>
							</xsl:for-each>
						</params_xml>
					</product>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="PRODUCT">
						<xsl:with-param name="select_index" select="$select_index + 1"/>
						<xsl:with-param name="value_path" select="($value_path, .)"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>


</xsl:stylesheet>