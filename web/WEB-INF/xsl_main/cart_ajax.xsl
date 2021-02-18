<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:f="f:f"
		version="2.0">
	<xsl:import href="utils/utils.xsl"/>
	<xsl:import href="snippets/constants.xsl"/>

	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="cart" select="page/cart"/>

	<xsl:template match="/">
		<xsl:if test="$cart and (not($cart/processed) or $cart/processed != 1) and $cart/bought">
			<div class="result" id="cart_ajax">

        <div class="header-icon__icon">
          <img src="img/icon-cart.png" alt="" />
          <div class="header-icon__label"><xsl:value-of select="count($cart/bought)"/></div>
        </div>
        <xsl:if test="f:num($cart/sum) &gt; 0">
          <div class="header-icon__text"><xsl:value-of select="f:currency_decimal($cart/sum)"/> руб.</div>
        </xsl:if>
        <xsl:if test="f:num($cart/sum) = 0">
					<div>По запросу</div>
				</xsl:if>
        <a class="header-icon__link" href="{page/show_cart}"></a>
			</div>
			<xsl:for-each select="$cart/bought">
				<div class="result" id="cart_list_{product/@id}">
					<form>
						<button class="button button_checkout" type="submit" onclick="location.replace('{//page/show_cart}'); return false"><xsl:value-of select="$go_to_cart_label"/></button>
<!--						<input type="submit" value="{$go_to_cart_label}" class="button to_cart" onclick="location.replace('{//page/show_cart}'); return false"/>-->
					</form>
				</div>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="not($cart) or $cart/processed = 1 or not($cart/bought)">
			<div class="result" id="cart_ajax">
          <div class="header-icon__icon">
            <img src="img/icon-cart.png" alt="" />
          </div>
          <div class="header-icon__text">0,00 руб.</div>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
