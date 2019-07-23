<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="CONTENT">
		<div class="tradingview-widget-container__widget"></div>
		<div class="tradingview-widget-copyright">
			<a href="https://ru.tradingview.com" rel="noopener" target="_blank">
				<span class="blue-text">Котировки</span>
			</a>
			предоставлены TradingView
		</div>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript" src="https://s3.tradingview.com/external-embedding/embed-widget-tickers.js"
				async="async">
			{
			"symbols": [
			{
			"title": "S&P 500",
			"proName": "OANDA:SPX500USD"
			},
			{
			"title": "ETH/USD",
			"proName": "BITSTAMP:ETHUSD"
			}
			],
			"colorTheme": "light",
			"isTransparent": false,
			"locale": "ru"
			}
		</script>
	</xsl:template>


</xsl:stylesheet>