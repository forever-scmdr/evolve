<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]><xsl:stylesheet		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"		xmlns:f="f:f"		version="2.0">	<xsl:import href="utils/price_conversions.xsl"/>	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>	<xsl:variable name="cart" select="page/cart"/>	<xsl:template match="/">		<xsl:if test="$cart and (not($cart/processed) or $cart/processed != 1) and $cart/bought">			<div class="result" id="cart_ajax">				<xsl:if test="f:num($cart/sum) &gt; 0">					<a href="{page/show_cart}"><i class="fas fa-shopping-cart"></i>Корзина</a>					<div>Наименований: <strong><xsl:value-of select="count($cart/bought)"/></strong></div>					<div>Сумма: <strong><xsl:value-of select="f:currency_decimal($cart/sum)"/> руб.</strong></div>				</xsl:if>				<xsl:if test="f:num($cart/sum) = 0">					<a href="{page/show_cart}"><i class="fas fa-shopping-cart"></i>Товары по запросу</a>					<div>Наименований: <strong><xsl:value-of select="count($cart/bought)"/></strong></div>					<div>Сумма по запросу</div>				</xsl:if>			</div>			<xsl:for-each select="$cart/bought">				<div class="result" id="cart_list_{product/@id}">					<form>						<input type="submit" value="Оформить заказ" class="button to_cart" onclick="location.replace('{//page/show_cart}'); return false"/>					</form>				</div>			</xsl:for-each>		</xsl:if>		<xsl:if test="not($cart) or $cart/processed = 1 or not($cart/bought)">			<div class="result" id="cart_ajax">				<i class="fas fa-shopping-cart"/> <strong>Нет заказов</strong>			</div>		</xsl:if>	</xsl:template></xsl:stylesheet>