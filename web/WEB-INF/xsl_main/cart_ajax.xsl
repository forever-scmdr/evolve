<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]><xsl:stylesheet		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"		xmlns:f="f:f"		version="2.0">	<xsl:import href="utils/utils.xsl"/>	<xsl:import href="snippets/constants.xsl"/>	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>	<xsl:variable name="cart" select="page/cart"/>	<xsl:template match="/">		<xsl:if test="$cart and (not($cart/processed) or $cart/processed != 1) and $cart/bought">			<div class="result" id="cart_ajax">				<a href="{page/show_cart}" class="icon-link">					<div class="icon"><img src="img/icon-cart.svg" alt="" /></div>					<span class="icon-link__item">В корзине (<span><xsl:value-of select="count($cart/bought)"/></span>)</span>				</a>				<!--				<xsl:if test="f:num($cart/sum) &gt; 0">					<div class="cart__item">Сумма: <span><xsl:value-of select="f:exchange_cur($cart, 'sum', 0)"/></span></div>				</xsl:if>				<xsl:if test="f:num($cart/sum) = 0">					<div>Сумма по запросу</div>				</xsl:if>				-->			</div>			<xsl:for-each select="$cart/bought">				<xsl:variable name="is_api" select="f:num(product/@id) &lt; 0"/>				<xsl:variable name="ajax_suffix" select="if ($is_api) then product/@key else product/@id"/>				<div class="result" id="cart_list_{$ajax_suffix}" data-extra-selector=".cart_list_{$ajax_suffix}">					<form>						<button class="button button_checkout" type="submit" onclick="location.replace('{//page/show_cart}'); return false"><xsl:value-of select="$go_to_cart_label"/></button><!--						<input type="submit" value="{$go_to_cart_label}" class="button to_cart" onclick="location.replace('{//page/show_cart}'); return false"/>-->					</form>				</div>				<span id="sum-{$ajax_suffix}" class="result">					<xsl:value-of select="if(f:num(product/price) != 0) then f:exchange_cur(., 'sum', 0) else ''"/>				</span>			</xsl:for-each>		</xsl:if>		<xsl:if test="not($cart) or $cart/processed = 1 or not($cart/bought)">			<div class="result" id="cart_ajax">				<a class="icon-link">					<div class="icon"><img src="img/icon-cart.svg" alt="" /></div>					<span class="icon-link__item empty-cart">Корзина пуста</span>				</a>			</div>		</xsl:if><!--		<xsl:if test="f:num($cart/sum) != 0">-->			<div class="cart-total__text result" id="cart-total">				<xsl:value-of select="concat('Итого: ', f:exchange_cur($cart, 'sum', 0))" />			</div><!--		</xsl:if>-->	</xsl:template></xsl:stylesheet>