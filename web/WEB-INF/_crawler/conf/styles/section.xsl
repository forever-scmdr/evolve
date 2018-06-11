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
		<xsl:variable name="current_id" select="f:url_id(//body/@source)"/>
		<xsl:variable name="parent_link" select="//div[contains(@class, 'breadcrumbs')]//li[position() &gt; 1 and position() &lt; last()]/a"/>
		<xsl:variable name="has_filter" select="//div[@id='narrow-by-list']//dt[not(contains(., 'Функционал')) and not(contains(., 'Новинка')) and not(contains(., 'Производитель')) and not(contains(., 'Лидер продаж'))]"/>
		<result>
			<xsl:if test="$parent_link and count($parent_link) &gt; 0">
				<section id="{$current_id}">
					<h_parent parent="catalog" element="catalog"/>
					<xsl:for-each select="$parent_link">
						<h_parent parent="{f:url_id(@href)}" element="section"/>
					</xsl:for-each>
				</section>
			</xsl:if>
			<xsl:variable name="sections" select="//div[contains(@class, 'categories')]//a"/>
			<xsl:for-each select="$sections">
				<section id="{f:url_id(@href)}" name="{.}">
					<h_parent parent="{$current_id}" element="section"/>
				</section>
			</xsl:for-each>
			<section id="{$current_id}" filter="{boolean($has_filter)}"/>
			<xsl:if test="not($sections) or count($sections) = 0">
				<xsl:for-each select="//ul[contains(@class, 'products-list')]/li">
					<product id="{f:url_id(div/a[contains(@class, 'product-image')]/@href)}">
						<xsl:variable name="pic" select="div/a[contains(@class, 'product-image')]/img"/>
						<small_pic download="{$pic/@src}">small_<xsl:value-of select="tokenize($pic/@src, '/')[last()]"/></small_pic>
						<h_parent parent="{$current_id}" element="section"/>
					</product>
				</xsl:for-each>
				<section type="last" id="{$current_id}"/>
			</xsl:if>
		</result>
	</xsl:template>

</xsl:stylesheet>