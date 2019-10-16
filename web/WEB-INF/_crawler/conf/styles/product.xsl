<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:f="f:f"
	version="2.0"
	exclude-result-prefixes="xsl f">
	<xsl:import href="../styles_fwk/utils.xsl"/>
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="sec_top" select="//section[1]"/>
	<xsl:variable name="sec_bottom" select="//section[2]"/>
	<xsl:variable name="tab_panes" select="$sec_bottom//div[contains(@class, 'tab-pane')]"/>
	<xsl:variable name="desc" select="$tab_panes[1]"/>
	<xsl:variable name="parts" select="$sec_bottom/div/div[contains(@class, 'row')]"/>

	<xsl:template match="/">
		<result>
			<xsl:variable name="crumbs" select="//html//div[@class='breadcrumb']/div"/>
			<xsl:for-each select="$crumbs[position() &lt; (last() - 1)]">
				<xsl:variable name="pos" select="position()"/>
				<section id="{.//a/@href}">
					<xsl:if test="$pos &gt; 1">
						<h_parent parent="{$crumbs[$pos - 1]//a/@href}" element="section"/>
					</xsl:if>
					<name><xsl:value-of select=".//span" /></name>
				</section>
			</xsl:for-each>
			<xsl:variable name="header" select="//h1[1]"/>
			<xsl:variable name="spans" select="$crumbs[position() = last() - 1]//span"/>
			<xsl:variable name="name" select="$spans[1]"/>
			<xsl:variable name="type" select="normalize-space(substring-before($header, $name))"/>
			<xsl:variable name="b" select="$sec_top//b"/>
			<xsl:variable name="code" select="substring($b[1], 2)"/>
			<product id="{$code}">
				<h_parent parent="{$crumbs[last() - 2]//a/@href}" element="section"/>
				<name><xsl:value-of select="$name"/></name>
				<code><xsl:value-of select="$code"/></code>
				<type><xsl:value-of select="$type"/></type>
				<price><xsl:value-of select="normalize-space($sec_top//span[@itemprop = 'price'])"/></price>
				<description>
					<xsl:copy-of select="$desc/ul"/>
				</description>
				<tech>
					<xsl:copy-of select="$desc/div[contains(@class, 'row')]/div[1]"/>
				</tech>
				<package>
					<xsl:copy-of select="$parts//ul"/>
				</package>
				<extra>
					<xsl:copy-of select="$parts//div[contains(@class, 'hint')]"/>
				</extra>
				<xsl:variable name="links" select="$desc/div[contains(@class, 'row')]/div[2]//a"/>
				<vendor><xsl:value-of select="tokenize($links[1]/@href, '/')[last() - 1]"/></vendor>
				<gallery>
					<xsl:for-each select="$sec_top//div[@class='preview-image']//a">
						<pic download="https://voltra.by/{@href}" link="https://voltra.by/{@href}"/>
					</xsl:for-each>
				</gallery>
				<assoc>
					<xsl:for-each select="//div[@class='supplies'][last()]//div[contains(@class, 'item-top')]//a[not(img)]">
						<name><xsl:value-of select="normalize-space(.)"/></name>
					</xsl:for-each>
				</assoc>
			</product>
		</result>
	</xsl:template>

</xsl:stylesheet>