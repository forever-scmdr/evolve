<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:f="f:f"
	version="2.0"
	exclude-result-prefixes="xsl f">
	<xsl:import href="../styles_fwk/utils.xsl"/>
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="caps" select="'[ABCDEFGHIJKLMNOPRSTUVWXYZ1234567890]'"/>


	<xsl:template match="/">
		<result>
			<xsl:variable name="crumbs" select="html//ul[@class='breadcrumbs'][1]//li[position() &gt; 1]/a"/>
			<xsl:for-each select="$crumbs">
				<xsl:variable name="pos" select="position()"/>
				<section id="{string-join($crumbs[position() &lt; $pos] | ., '_')}">
					<xsl:if test="$crumbs[position() &lt; $pos]">
						<h_parent element="section" parent="{string-join($crumbs[position() &lt; $pos], '_')}"/>
					</xsl:if>
					<name><xsl:value-of select="." /></name>
				</section>
			</xsl:for-each>
			<xsl:variable name="header" select="//h1"/>
			<xsl:variable name="code" select="normalize-space(substring-after(html//div[@class='content-box']/p[1], ':'))"/>
			<xsl:variable name="type" select="normalize-space(tokenize($header, $caps)[1])"/>
			<xsl:variable name="name" select="normalize-space(substring-after($header, $type))"/>
			<product id="{$code}">
				<h_parent element="section" parent="{string-join($crumbs, '_')}"/>
				<name><xsl:value-of select="$name"/></name>
				<url><xsl:value-of select="//body/@source"/></url>
				<code><xsl:value-of select="$code"/></code>
				<type><xsl:value-of select="$type"/></type>
				<name_extra></name_extra>
				<short></short>
				<extra></extra>
				<description>
					<xsl:copy-of select="//div[@class='description-box']/*"/>
				</description>
				<tech>
					<xsl:copy-of select="//div[@class='tabset']/div[@class='tab'][1]/table/tbody"/>
				</tech>
				<package>
					<xsl:copy-of select="//div[@class='tabset']/div[@class='tab'][2]/table"/>
				</package>
				<symbols></symbols>
				<xsl:for-each select="//div[@class='tabset']//a[@class='shop_download']">
					<manual>https://laserlevel.ru<xsl:value-of select="@href"/></manual>
				</xsl:for-each>
				<parts></parts>
				<xsl:variable name="spins" select="//ul[@class='thumb-list']//a[@class='ifancy']"/>
				<xsl:if test="$spins">
					<spin link="{$spins[1]/@href}" img="https://laserlevel.ru{$spins[1]/img/@src}"/>
				</xsl:if>
				<xsl:for-each select="//div[@class='sidebar']//div[@class='video-box']/div[@class='video']">
					<video link="https://www.youtube.com/watch?v={substring-after(a/@href, '=')}"/>
				</xsl:for-each>
				<gallery>
					<xsl:for-each select="//div[@class='item-gallery']//ul[@class='thumb-list']//a[@class='fancybox']">
						<pic download="https://laserlevel.ru{@href}" link="https://laserlevel.ru{@href}"/>
					</xsl:for-each>
				</gallery>
				<assoc>
					<xsl:for-each select="//ul[@class='accessories-list']/li">
						<url>https://laserlevel.ru<xsl:value-of select="div[1]/a/@href"/></url>
					</xsl:for-each>
				</assoc>
			</product>
		</result>
	</xsl:template>

</xsl:stylesheet>