<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	version="2.0">
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:template match="/">
		<xsl:variable name="img" select="//img[@class='detail_image']"/>
		<xsl:variable name="code" select="$img/@title"/>
		<xsl:variable name="header" select="//h1"/>
		<xsl:variable name="model" select="$code"/>
		<xsl:variable name="type" select="substring-before($header, $code)"/>
		<result>
			<product id="{$code}">
				<name><xsl:value-of select="$model" /></name>
				<type><xsl:value-of select="$type" /></type>
				<code><xsl:value-of select="$code" /></code>
				<name_extra><xsl:value-of select="h2" /></name_extra>
				<venodor_code></venodor_code>
				<offer_id></offer_id>
				<short><xsl:value-of select="//div[@class = 'tabs']/div[1]/p[1]" /></short>
				<available></available>
				<group_id></group_id>
				<url></url>
				<category_id></category_id>
				<currency_id></currency_id>
				<price></price>
				<country></country>
				<main_pic></main_pic>
				<tech>
					<xsl:copy-of select="//div[@class='tabbody'][1]//table[@class='technical_data']"/>
					<xsl:copy-of select="//div[@class='tabbody'][1]//div[@class='table_legend'][1]"/>
				</tech>
				<elements>
					<xsl:copy-of select="//div[@class='tabbody'][2]"/>
				</elements>
				<extra_parts>
					<xsl:copy-of select="//div[@class='tabbody'][3]"/>
				</extra_parts>
				<manuals>
					<xsl:for-each select="//div[@id = 'manualtable']//tr">
						<manual>
							<name><xsl:value-of select=".//td[1]" /></name>
							<file><xsl:value-of select=".//td[2]/a/@href" /></file>
						</manual>
					</xsl:for-each>
				</manuals>
				<assoc_code></assoc_code>
				<gallery>
					<xsl:for-each select="//div[@class='detail_text']//a[@class='image_link']">
						<pic download="{@href}" link="{@href}"/>
					</xsl:for-each>
				</gallery>
				<params_xml>
					<xsl:variable name="table" select="//div[@class='tabbody'][1]//table[@class='technical_data']"/>
					<xsl:for-each select="$table//tr">
						<xsl:variable name="name" select="td[@class = 'col1']"/>
						<xsl:variable name="value" select="td[@class = 'col2']"/>
						<xsl:variable name="sup" select="$name/sup"/>
						<xsl:if test="$value">
							<parameter>
								<name><xsl:value-of select="if ($sup) then substring-before($name, $sup) else $name" /></name>
								<value><xsl:value-of select="$value" /></value>
							</parameter>
						</xsl:if>
					</xsl:for-each>
				</params_xml>
			</product>
		</result>
	</xsl:template>

</xsl:stylesheet>