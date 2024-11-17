<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="extra-header-class" select="' s-pageheader--home'"/>

	<xsl:template name="CONTENT">
	</xsl:template>


	<xsl:template name="EXTRA_HEADER_CONTENT"></xsl:template>


	<xsl:template name="WIDGET_CODE">

		<xsl:variable name="inf" select="page/main_page/informer_wrap"/>
		<div class="header__content row mobile-only">
			<xsl:for-each-group select="$inf" group-by="floor((position()-1) div 3)">

				<xsl:variable name="g" select="current-grouping-key()"/>

				<div class="tradingview-widget-container" style="{'display:none;'[$g != 0]}" id="tickers-{$g}">
					<div class="tradingview-widget-container__widget"></div>
						<script type="text/javascript" src="https://s3.tradingview.com/external-embedding/embed-widget-market-overview.js" async="async">
							{
								"colorTheme": "light",
								"dateRange": "12M",
								"showChart": false,
								"locale": "ru",
								"largeChartUrl": "",
								"isTransparent": false,
								"showSymbolLogo": true,
								"showFloatingTooltip": false,
								"width": "100%",
								"height": "375",
								"tabs": [
									<xsl:for-each select="current-group()">
										<xsl:variable name="sep" select="if(position() != 1) then ',' else ''"/>
										<xsl:value-of select="$sep"/>{
											 "title": "<xsl:value-of select="name" />" 
											,"symbols":[
												<xsl:for-each select="informer">
													<xsl:variable name="s" select="if(position() != 1) then ',' else ''"/>
													<xsl:value-of select="$s"/>{
														"s": "<xsl:value-of select="pro_name" />",
														"d": "<xsl:value-of select="name" />"
													}
												</xsl:for-each>
											]
										}
									</xsl:for-each>
								]
							}
						</script>
				</div>
			</xsl:for-each-group>

			<div class="ticker-pagination">
				Страница:&#160;
				<xsl:for-each-group select="$inf" group-by="floor((position()-1) div 3)">
					
					<xsl:variable name="g" select="current-grouping-key()"/>
					<xsl:variable name="f" select="concat('showGroup(',$g,', this)')"/>

					<a onclick="{$f}" class="{'active'[$g = 0]} lnl"><xsl:value-of select="$g + 1"/></a>&#160;&#160;&#160;&#160;

				</xsl:for-each-group>

				<script>
					function showGroup(g, a){
						var id = '#tickers-' + g;
						var $el = $(id);
						$(".tradingview-widget-container").not($el).hide();
						$(".lnl").removeClass("active");
						$(a).addClass("active");
						$el.show();
					}
				</script>
			</div>

		</div>
	</xsl:template>

</xsl:stylesheet>
