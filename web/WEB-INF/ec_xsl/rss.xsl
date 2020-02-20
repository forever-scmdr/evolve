<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:content="http://purl.org/rss/1.0/modules/content/"
		xmlns:media="http://search.yahoo.com/mrss/"
		xmlns:f="f:f"
		version="2.0">
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="no"/>
	<xsl:import href="utils/date_conversions.xsl"/>
	<xsl:import href="utils/price_conversions.xsl"/>

	<xsl:variable name="base" select="page/base"/>
	<xsl:variable name="rss" select="page/rss"/>
	<xsl:variable name="news" select="page/news_item | page/small_news_item"/>
	<xsl:variable name="src">src="files</xsl:variable>
	<xsl:variable name="replacement">src="<xsl:value-of select="$base"/>/files</xsl:variable>
	<xsl:variable name="latest_date_ms" select="if($news) then format-number(max($news/date/number(@millis)),'###')else page/variables/yesterday"/>
<!--	<xsl:variable name="latest_date_time" select="f:millis_to_date_time($latest_date_ms)"/>-->

	<xsl:template match="/">
		<rss version="2.0" xmlns:content="http://purl.org/rss/1.0/modules/content/" xmlns:media="http://search.yahoo.com/mrss/">
			<channel>
				<lastBuildDate><xsl:value-of select="f:millis_to_rss($latest_date_ms)"/></lastBuildDate>
				<title><xsl:value-of select="$rss/title"/></title>
				<description><xsl:value-of select="$rss/description"/></description>
				<link><xsl:value-of select="$rss/link"/></link>
				<xsl:apply-templates select="$news" />
			</channel>
		</rss>
	</xsl:template>

	<xsl:template match="small_news_item">
		<item>
			<guid isPermaLink="true"><xsl:value-of select="concat($base, show_page)"/></guid>
			<pubDate><xsl:value-of select="f:millis_to_rss(date/@millis)"/></pubDate>
			<title><xsl:value-of select="name"/></title>
			<description><xsl:value-of select="twitter_description"/></description>
			<content:encoded>
<!--				<xsl:copy>-->
					<xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
<!--					<xsl:value-of select="replace(text, $src, $replacement)" disable-output-escaping="yes"/>-->
					<xsl:value-of select="text" disable-output-escaping="yes"/>
					<xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
<!--				</xsl:copy>-->
			</content:encoded>
			<link><xsl:value-of select="concat($base, show_page)"/></link>
			<author><xsl:value-of select="author"/></author>
			<xsl:if test="main_pic">
				<media:content url="{concat($base, @path, main_pic)}" type="image/{f:substring-after-last(main_pic,'.')}" expression="full" width="{@width}" height="{@height}"/>
			</xsl:if>
		</item>
	</xsl:template>

	<xsl:template match="news_item">
		<item>
			<guid isPermaLink="true"><xsl:value-of select="concat($base, show_page)"/></guid>
			<pubDate><xsl:value-of select="f:millis_to_rss(date/@millis)"/></pubDate>
			<title><xsl:value-of select="name"/></title>
			<description><xsl:value-of select="twitter_description"/></description>
			<content:encoded>
<!--				<xsl:copy>-->
					<xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
<!--					<xsl:value-of select="replace(text, $src, $replacement)" disable-output-escaping="yes"/>-->
					<xsl:value-of select="text" disable-output-escaping="yes"/>
					<xsl:apply-templates select="text_part | gal_part" mode="cdata"/>
					<xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
<!--				</xsl:copy>-->
			</content:encoded>
			<link><xsl:value-of select="concat($base, show_page)"/></link>
			<author><xsl:value-of select="author"/></author>
			<xsl:if test="main_pic">
				<media:content url="{concat($base, @path, main_pic)}" type="image/{f:substring-after-last(main_pic,'.')}" expression="full" width="{@width}" height="{@height}"/>
			</xsl:if>
			<xsl:if test="video_url != ''">
				<iframe width="560" height="315" src="{video_url}" frameborder="0"></iframe>
			</xsl:if>
			<xsl:if test="main_audio != ''">
				<media:content url="{concat($base,@path,main_audio)}" medium="audio"/>
			</xsl:if>
		</item>
	</xsl:template>

	<xsl:template match="text_part" mode="cdata" >
<!--		<xsl:value-of select="replace(text, $src, $replacement)" disable-output-escaping="yes"/>-->
		<xsl:value-of select="text" disable-output-escaping="yes"/>
	</xsl:template>




</xsl:stylesheet>