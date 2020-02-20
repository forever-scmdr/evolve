<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:f="f:f"
		version="2.0">
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="no"/>
	<xsl:import href="utils/date_conversions.xsl"/>
	<xsl:import href="utils/price_conversions.xsl"/>

	<xsl:variable name="base" select="page/base"/>
	<xsl:variable name="news" select="page/news_item | page/small_news_item"/>
	<xsl:variable name="latest_date_ms" select="if($news) then format-number(max($news/date/number(@millis)),'###')else page/variables/yesterday"/>
<!--	<xsl:variable name="latest_date_time" select="f:millis_to_date_time($latest_date_ms)"/>-->

	<xsl:template match="/">
		<rss version="2.0" xmlns:content="http://purl.org/rss/1.0/modules/content/">
			<channel>
				<lastBuildDate><xsl:value-of select="f:millis_to_rss($latest_date_ms)"/></lastBuildDate>
				<title>Tempting.pro</title>
				<description>Новостной сайт для всех, кто любит чиать новости.</description>
				<xsl:apply-templates select="$news" />
			</channel>
		</rss>
	</xsl:template>

	<xsl:template match="small_news_item">
		<item>
			<guid isPermaLink="true"><xsl:value-of select="concat($base, show_page)"/></guid>
			<pubDate><xsl:value-of select="f:millis_to_rss(date/@millis)"/></pubDate>
			<title><xsl:value-of select="name"/></title>
			<content:encoded>
				<xsl:copy>
					<xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
					<xsl:value-of select="text" disable-output-escaping="yes"/>
					<xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
				</xsl:copy>
			</content:encoded>
			<link><xsl:value-of select="concat($base, show_page)"/></link>
			<author><xsl:value-of select="author"/></author>
		</item>
	</xsl:template>

	<xsl:template match="news_item"></xsl:template>

</xsl:stylesheet>