<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="snippets/product.xsl"/>

	<xsl:variable name="p" select="page/product"/>
	<xsl:variable name="currencies" select="page/currencies"/>

	<xsl:template match="/">
		<div>
			<div class="popup result" id="product-ajax-popup">
				<div class="popup__body">
					<div class="popup__content">
						<a class="popup__close" onclick="clearProductAjax();">×</a>
						<div class="popup__title title title_2">
							<xsl:value-of select="$p/name" />
							<ul class="currency-options">
								<xsl:variable name="currency_link" select="page/set_currency"/>
								<xsl:for-each select="$currencies/*[ends-with(name(), '_rate')]">
									<xsl:variable name="cur" select="substring-before(name(), '_rate')"/>
									<xsl:variable name="active" select="$currency = $cur"/>
									<li class="{'active'[$active]}">
										<xsl:if test="not($active)"><a href="{concat($currency_link, $cur)}" ajax="true"><xsl:value-of select="$cur"/></a></xsl:if>
										<xsl:if test="$active"><xsl:value-of select="$cur"/></xsl:if>
									</li>
								</xsl:for-each>
								<li><i class="far fa-money-bill-alt"/>&#160;<strong>Валюта</strong></li>
							</ul>
						</div>
						<div id="api_ajax_1" ajax-href="search_prices_mouser?q={$p/name}"></div>
						<div id="api_ajax_2" ajax-href="search_prices_digikey?q={$p/name}"></div>
						<div id="api_ajax_3" ajax-href="search_prices_farnell?q={$p/name}"></div>
						<div id="api_ajax_4" ajax-href="search_prices_onlinecomponents?q={$p/name}"></div>
						<div id="api_ajax_5" ajax-href="search_prices_newark?q={$p/name}"></div>
						<div id="api_ajax_6" ajax-href="search_prices_arrowelectronics?q={$p/name}"></div>
					</div>
				</div>
			</div>
		</div>
	</xsl:template>



</xsl:stylesheet>