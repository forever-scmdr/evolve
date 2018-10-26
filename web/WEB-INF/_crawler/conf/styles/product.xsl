<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:template match="/">
		<xsl:variable name="name" select="//h1[1]"/>
		<xsl:variable name="subname" select="//h2[1]"/>
		<xsl:variable name="symbols_line" select="//div[contains(@class, 'symbols')]"/>
		<xsl:variable name="code" select="$symbols_line//td[starts-with(text(), 'Код')]/following-sibling::td[1]"/>
		<xsl:variable name="paths" select="//span[@class = 'category-path']"/>
		<result>
			<xsl:for-each select="$paths">
				<xsl:variable name="path" select=".//a[@href]"/>
				<xsl:for-each select="$path">
					<xsl:variable name="pos" select="position()"/>
					<section id="{@data-categoryid}">
						<xsl:if test="$pos != 1">
							<h_parent parent="{$path[$pos - 1]/@data-categoryid}" element="section"/>
						</xsl:if>
						<name>
							<xsl:value-of select="text()" />
						</name>
					</section>
				</xsl:for-each>
			</xsl:for-each>
			<product id="{$code}">
				<name><xsl:value-of select="normalize-space($name[1])" /></name>
				<type><xsl:value-of select="substring-before($subname[1], ';')" /></type>
				<code><xsl:value-of select="$code" /></code>
				<name_extra><xsl:value-of select="$subname[1]/text()" /></name_extra>
				<vendor_code><xsl:value-of select="$symbols_line//td[starts-with(text(), 'Обозначение')]/following-sibling::td[1]" /></vendor_code>
				<vendor><xsl:value-of select="$symbols_line//td[starts-with(text(), 'Производитель')]/following-sibling::td[1]" /></vendor>
				<!--<short><xsl:value-of select="//div[contains(@class, 'tabs')]/div[1]/p[1]" /></short>-->
				<gallery>
					<xsl:for-each select="//meta[@name='twitter:image']">
						<pic download="{@content}" link="{@content}"/>
					</xsl:for-each>
				</gallery>
				<text>
					<xsl:copy-of select="//div[@id='specification']/*[not(name() = 'table')]"/>
				</text>
				<params_xml>
					<xsl:variable name="table" select="//div[@id='specification']/table"/>
					<xsl:for-each select="$table//tr">
						<parameter>
							<name><xsl:value-of select="td[1]/label" /></name>
							<value><xsl:value-of select="td[2]" /></value>
						</parameter>
					</xsl:for-each>
				</params_xml>
				<manuals>
					<xsl:for-each select="//div[@id = 'downloads']//td[@class='filename']/a">
						<manual>
							<name><xsl:value-of select="span[@class='filelink']" /></name>
							<file><xsl:value-of select="@href" /></file>
						</manual>
					</xsl:for-each>
				</manuals>
				<assoc>
					<xsl:for-each select="//div[@id='similar-box']//a[@data-product-symbol]">
						<assoc_code><xsl:value-of select="@data-product-symbol" /></assoc_code>
					</xsl:for-each>
				</assoc>
				<xsl:for-each select="$paths">
					<xsl:variable name="path" select=".//a[@href]"/>
					<h_parent parent="{$path[position() = last()]/@data-categoryid}" element="section"/>
				</xsl:for-each>
			</product>
		</result>
	</xsl:template>

</xsl:stylesheet>