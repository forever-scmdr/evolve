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
						<xsl:if test="page/plain_catalog/product">
							<xsl:call-template name="LINES_TABLE">
								<xsl:with-param name="products" select="page/plain_catalog/product"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="not(page/plain_catalog/product)">
							<h3>На складах не найден</h3>
						</xsl:if>
						<script type="text/javascript">
							insertAjax('cart_ajax');
						</script>
					</div>
				</div>
			</div>
		</div>
	</xsl:template>



</xsl:stylesheet>