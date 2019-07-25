<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="utils/price_conversions.xsl"/>
	<xsl:import href="utils/date_conversions.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>

	<xsl:strip-space elements="*"/>
	
	<xsl:variable name="ipp" select="f:num(page/variables/tickers_per_request)"/>

	<xsl:template match="/">
		<div class="result" id="informers">
			<xsl:call-template name="V_START"/>
				<xsl:for-each select="page/informer_wrap/informer">
					<xsl:variable name="p" select="position()"/>
					{
					"title": <xsl:value-of select="concat('&#34;', name, '&#34;')" disable-output-escaping="yes"/>,
					"proName": <xsl:value-of select="concat('&#34;', pro_name, '&#34;')" disable-output-escaping="yes"/>
					}
					<xsl:if test="$p != last() and $p mod $ipp != 0">,</xsl:if>
					<xsl:if test="$p != last() and $p mod $ipp = 0">
						<xsl:call-template name="V_END"/>
						<xsl:call-template name="V_START" />
					</xsl:if>
				</xsl:for-each>
			<xsl:call-template name="V_END" />
		</div>
	</xsl:template>

	<xsl:template name="V_START">
		<xsl:text disable-output-escaping="yes">
			&lt;div class="tradingview-widget-container"&gt;
		</xsl:text>
		<div class="tradingview-widget-container__widget"></div>
		<xsl:text disable-output-escaping="yes">
			&lt;script type="text/javascript" src="https://s3.tradingview.com/external-embedding/embed-widget-tickers.js" async&gt;
			{
  				"symbols": [
		</xsl:text>
	</xsl:template>
	<xsl:template name="V_END">
		<xsl:text disable-output-escaping="yes">
			],
		  "colorTheme": "light",
		  "isTransparent": false,
		  "locale": "ru"
		}
		  &lt;/script&gt;
			&lt;/div&gt;
		</xsl:text>
	</xsl:template>

</xsl:stylesheet>