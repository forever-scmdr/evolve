<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]><xsl:stylesheet		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"		xmlns="http://www.w3.org/1999/xhtml"		xmlns:f="f:f"		version="2.0">	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>	<xsl:import href="utils/price_conversions.xsl"/>	<xsl:variable name="cart" select="page/cart"/>	<xsl:variable name="window_closed" select="page/variables/window_closed = 'yes'"/>	<xsl:variable name="discount_value" select="100 * f:num(page/common/discount)"/>	<xsl:template match="/">		<!-- HEADER CART -->			<div class="result" id="cart_ajax">			<xsl:if test="$cart and (not($cart/processed) or $cart/processed != 1) and $cart/bought">				<p><i class="fas fa-shopping-cart"></i> <strong>Ваш заказ</strong></p>				<p>Сумма: <xsl:value-of select="$cart/sum"/> р.</p>				<!-- <p>Наименований: <xsl:value-of select="count($cart/bought)"/></p> -->				<p><a href="{page/show_cart}">Оформить заказ</a></p>			</xsl:if>			<xsl:if test="not($cart) or $cart/processed = 1 or not($cart/bought)">				<p><i class="fas fa-shopping-cart"/> <strong>Корзина пуста</strong></p>			</xsl:if>			</div>		<!-- DEVICE BUTTONS -->			<xsl:for-each select="$cart/bought">				<div class="result" id="cart_list_{product/code}">					<form>						<input type="submit" value="Оформить заказ" class="to_cart" onclick="location.replace('{//page/show_cart}'); return false"/>					</form>				</div>			</xsl:for-each>	</xsl:template></xsl:stylesheet>