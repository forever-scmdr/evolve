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
					<xsl:variable name="p" select="position()"/>{"title": <xsl:value-of select="concat('&#34;', name, '&#34;')" disable-output-escaping="yes"/>,"proName": <xsl:value-of select="concat('&#34;', pro_name, '&#34;')" disable-output-escaping="yes"/>}<xsl:if test="$p != last() and $p mod $ipp != 0">,</xsl:if><xsl:if test="$p != last() and $p mod $ipp = 0"><xsl:call-template name="V_END"/><xsl:call-template name="V_START" />
					</xsl:if>
				</xsl:for-each>
			<xsl:call-template name="V_END" />
			<xsl:if test="//informer_pages and number(page/variables/limit) &gt; 4">
				<div class="informer-pagination">
					<span>Страница: </span>
					<xsl:apply-templates select="//informer_pages/page"/>
				</div>
				<script type="text/javascript">
					function nextPage(link){
						insertAjax(link,'informers', function(){
							setTimeout(updateHeight,500);
						});
					}
				</script>
			</xsl:if>
			<xsl:if test="//informer_pages and number(page/variables/limit) &lt; 5">
				<div class="informer-pagination" style="text-align: center;">
					<a href="{//base_link}" class="informer-ajax-caller no-active">Подробнее</a>
				</div>
			</xsl:if>



		</div>
	</xsl:template>

	<xsl:template match="page">
		<a onclick="nextPage({concat('&#34;', link, '&#34;','')})" class="{if(@current = 'current') then 'active' else ''}">
			<xsl:value-of select="number"/>
		</a>
	</xsl:template>


	<xsl:template name="V_START">
		<xsl:text disable-output-escaping="yes">
			&lt;div class="tradingview-widget-container"&gt;
		</xsl:text>
		<div class="tradingview-widget-container__widget"></div>
		<xsl:text disable-output-escaping="yes">
			&lt;script type="text/javascript" src="https://s3.tradingview.com/external-embedding/embed-widget-tickers.js" async&gt;{"showSymbolLogo": true,	"symbols": [</xsl:text>
	</xsl:template><xsl:template name="V_END"><xsl:text disable-output-escaping="yes">],"colorTheme":"light","isTransparent": false,"locale": "ru"}&lt;/script&gt;
			&lt;/div&gt;
		</xsl:text>
	</xsl:template>

</xsl:stylesheet>