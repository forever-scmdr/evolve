<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="CONTENT">
		<section class="s-content s-content--narrow s-content--no-padding-bottom white">
			<article class="row format-standard" style="max-width: 100%">
				<div class="s-content__header col-full">
					<h1 class="s-content__header-title">
						Котировки
					</h1>
					<ul class="s-content__header-meta">
						<li class="cat">
							Источник: <a href="https://ru.tradingview.com" rel="noopener" target="_blank">TradingView</a>
						</li>
					</ul>
				</div>
				<div class="col-full s-content__main white" style="padding: 0 15px;">
					<div class="tradingview-widget-container" id="xxx">
						<script type="text/javascript" src="https://s3.tradingview.com/external-embedding/embed-widget-tickers.js" async="async">
							{
							"symbols": [
							{
							"title": "S&amp;P 500",
							"proName": "OANDA:SPX500USD"
							},
							{
							"title": "Shanghai Composite",
							"proName": "INDEX:XLY0"
							},
							{
							"title": "EUR/USD",
							"proName": "FX_IDC:EURUSD"
							},
							{
							"title": "BTC/USD",
							"proName": "BITSTAMP:BTCUSD"
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
					</div>
				</div>
			</article>
			<div style="height: 3rem;"></div>
		</section>
	</xsl:template>

</xsl:stylesheet>