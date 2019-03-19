<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]><xsl:stylesheet		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"		xmlns="http://www.w3.org/1999/xhtml"		xmlns:f="f:f"		version="2.0">	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>	<xsl:import href="utils/price_conversions.xsl"/>	<xsl:variable name="cart" select="page/cart"/>	<xsl:variable name="window_closed" select="page/variables/window_closed = 'yes'"/>	<xsl:variable name="discount_value" select="100 * f:num(page/common/discount)"/>	<xsl:template match="/">		<div class="result" id="cart_ajax">			<xsl:if test="f:num(page/variables/discount_used) = 0 and f:num(page/variables/current_time) &lt; f:num(page/variables/discount_expires)">				<div id="dsc-data" data-now="{page/variables/current_time}" data-start="{page/variables/site_visit}" data-expires="{page/variables/discount_expires}" data-show="{page/variables/show_window}">					<script type="text/javascript" src="js/metabo_discount.js"></script>					<div style="background: #fff; position: fixed; border: 1px solid #000; top:50%; left:50%; width: 200px; z-index: 4;					 margin-left: -101px; color: #000; padding: 10px 20px; display: none;" id="discount-popup" title="скндка не распространяется на акционные товары">						Вы получите скидку <b style="color: darkred;"><xsl:value-of select="$discount_value"/>%</b><sup>*</sup>, если закажете что-нибудь в течение:						<p id="dsc-timer-1" style="color: red;"></p>						<a onclick="closeDiscountWindow();" style="color: #00afea; cursor: pointer;">Ураа!!!</a>					</div>					<div title="скндка не распространяется на акционные товары" id="discount-popup-2" style="position: fixed; color: #000; top: 0; left: 0; background: #fff; padding: 5px;{' display: none;'[not($window_closed)]}">						Осталось до окончания дейтсвия скидки <xsl:value-of select="$discount_value"/>%<sup>*</sup>:						<p id="dsc-timer-2" style="color: red; font-weight: bold;"></p>					</div>				</div>			</xsl:if>			<xsl:if test="$cart and (not($cart/processed) or $cart/processed != 1) and $cart/bought">					<p><i class="fas fa-shopping-cart"></i> <strong>Ваш заказ</strong></p>					<p>Сумма: <xsl:value-of select="$cart/sum"/> р.</p>					<!-- <p>Наименований: <xsl:value-of select="count($cart/bought)"/></p> -->					<p><a href="{page/show_cart}">Оформить заказ</a></p>			</xsl:if>			<xsl:if test="not($cart) or $cart/processed = 1 or not($cart/bought)">				<p><i class="fas fa-shopping-cart"/> <strong>Корзина пуста</strong></p>			</xsl:if>		</div>		<xsl:for-each select="$cart/bought">			<div class="result" id="cart_list_{product/code}">				<form>					<input type="submit" value="Оформить заказ" class="to_cart" onclick="location.replace('{//page/show_cart}'); return false"/>				</form>			</div>		</xsl:for-each>	</xsl:template></xsl:stylesheet>