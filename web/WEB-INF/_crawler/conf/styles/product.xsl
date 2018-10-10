<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	version="2.0">
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:template match="/">
		<xsl:variable name="subname" select="//h2[1]/text()"/>
		<xsl:variable name="symbols_line" select="//div[contains(@class, 'symbols')]"/>
		<xsl:variable name="code" select="$symbols_line/td[starts-with(text(), 'Код')]/following-sibling::td[1]"/>
		<result>
			<product id="{$code}">
				<name><xsl:value-of select="//h1[1]/text()" /></name>
				<type><xsl:value-of select="substring-before($subname, ';')" /></type>
				<code><xsl:value-of select="$code" /></code>
				<name_extra><xsl:value-of select="$symbols_line" /></name_extra>
				<vendor_code><xsl:value-of select="$symbols_line/td[starts-with(text(), 'Обозначение')]/following-sibling::td[1]" /></vendor_code>
				<vendor><xsl:value-of select="$symbols_line/td[starts-with(text(), 'Производитель')]/following-sibling::td[1]" /></vendor>
				<short><xsl:value-of select="//div[@class = 'tabs']/div[1]/p[1]" /></short>
				<gallery>
					<xsl:for-each select="//meta[@name='twitter:image']">
						<pic download="{@content}" link="{@content}"/>
					</xsl:for-each>
				</gallery>
				<tech>
					<xsl:copy-of select="//div[@class='tabbody'][1]//table[@class='technical_data']"/>
					<xsl:copy-of select="//div[@class='tabbody'][1]//div[@class='table_legend'][1]"/>
				</tech>
				<elements type="html">
					<xsl:copy-of select="//div[@class='tabbody'][2]"/>
				</elements>
				<extra_parts type="html">
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

				<params_xml>
					<xsl:variable name="table" select="//div[@id='specification']/table"/>
					<xsl:for-each select="$table//tr">
						<parameter>
							<name><xsl:value-of select="td[1]/label" /></name>
							<value><xsl:value-of select="td[2]" /></value>
						</parameter>
					</xsl:for-each>
				</params_xml>
			</product>
		</result>
	</xsl:template>

</xsl:stylesheet>