<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:f="f:f"
	version="2.0"
	exclude-result-prefixes="xsl f">
	<xsl:import href="../styles_fwk/utils.xsl"/>
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="/">
		<result>
			<xsl:variable name="crumbs" select="html//div[@id='breadcramps__top'][1]//li/a"/>
			<xsl:variable name="code" select="//h3"/>
			<xsl:for-each select="$crumbs[position() &gt; 2]">
				<xsl:variable name="cp" select="position()"/>
				<xsl:variable name="pos" select="$cp + 2"/>
				<section id="{@href}">
					<xsl:if test="$cp &gt; 1">
						<h_parent parent="{$crumbs[$pos - 1]/@href}" element="section"/>
						</xsl:if>
					<name><xsl:value-of select="span" /></name>
				</section>
			</xsl:for-each>
			<xsl:variable name="code" select="$code"/>
			<product id="{$code}">
				<h_parent parent="{$crumbs[position() = last()]/@href}" element="section"/>
				<name><xsl:value-of select="$code"/></name>
				<code><xsl:value-of select="$code" /></code>
				<type><xsl:value-of select="$crumbs[position() = last()]/span"/></type>
				<name_extra></name_extra>
				<short>
					<xsl:copy-of select="//div[@class='detail__info-text']/ul/li[position() &lt;= 3]"/>
				</short>
				<extra></extra>
				<description>
					<xsl:copy-of select="//div[@class='detail__info-text']/ul"/>
				</description>
				<tech>
					<xsl:copy-of select="//div[@class='detail__characteristic-table']"/>
				</tech>
				<package></package>
				<symbols></symbols>
				<xsl:variable name="manual" select="//a[contains(@class, 'detail__download')][1]/@href"/>
				<manual><xsl:if test="$manual" >https://hikoki-powertools.ru<xsl:value-of select="$manual" /></xsl:if></manual>
				<parts></parts>
				<gallery>
					<xsl:for-each select="//div[contains(@class, 'detail__slider-big')]//img">
						<pic download="https://hikoki-powertools.ru{@src}" link="https://hikoki-powertools.ru{@src}"/>
					</xsl:for-each>
				</gallery>
				<assoc></assoc>
			</product>
		</result>
	</xsl:template>

</xsl:stylesheet>