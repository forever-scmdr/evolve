<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]><xsl:stylesheet		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"		xmlns:f="f:f"		version="2.0">	<xsl:import href="utils/price_conversions.xsl"/>	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>	<xsl:variable name="cart" select="page/cart"/>	<xsl:variable name="window_closed" select="page/variables/window_closed = 'yes'"/>	<xsl:variable name="discount_value" select="100 * f:num(page/common/discount)"/>	<xsl:template match="/">		<xsl:if test="$cart and (not($cart/processed) or $cart/processed != 1) and $cart/bought">			<div class="result" id="cart_ajax">				<xsl:if test="f:num($cart/sum) &gt; 0">					<a href="{page/show_cart}" rel="nofollow"><i class="fas fa-shopping-cart"></i>Корзина</a>					<div>Наименований: <strong><xsl:value-of select="count($cart/bought)"/></strong></div>					<div>Сумма: <strong><xsl:value-of select="f:currency_decimal($cart/sum)"/> руб.</strong></div>				</xsl:if>				<xsl:if test="f:num($cart/sum) = 0">					<a href="{page/show_cart}" rel="nofollow"><i class="fas fa-shopping-cart"></i>Корзина</a>					<div>Наименований: <strong><xsl:value-of select="count($cart/bought)"/></strong></div>					<div>Сумма по запросу</div>				</xsl:if>			</div>			<xsl:for-each select="$cart/bought">				<div class="result" id="cart_list_{product/@id}">					<form>						<input type="submit" value="Товар в корзине" class="button to_cart" onclick="location.replace('{//page/show_cart}'); return false"/>					</form>				</div>				<span id="cart_list_a_{product/@id}" class="result">					<form style="display: inline;">							<input type="number" style="width: 50px; margin-right: 7px;" class="text-input" disabled="disabled" name="qty" value="{../qty}" />							<input value="Товар в корзине" type="submit" class="button button_primary" onclick="location.replace('{//page/show_cart}'); return false" />					</form>				</span>			</xsl:for-each>		</xsl:if>		<xsl:if test="not($cart) or $cart/processed = 1 or not($cart/bought)">			<div class="result" id="cart_ajax">				<i class="fas fa-shopping-cart"/> <strong>Корзина пуста</strong>			</div>		</xsl:if>		<!-- DISCOUNT ALERT -->		<xsl:if test="f:num(page/variables/discount_used) = 0 and f:num(page/variables/current_time) &lt; f:num(page/variables/discount_expires)">			<div class="message result" id="discount-popup-2" style="display: none;">				<div id="dsc-data" data-now="{page/variables/current_time}" data-start="{page/variables/site_visit}" data-expires="{page/variables/discount_expires}" data-show="{page/variables/show_window}">					<script type="text/javascript" src="js/metabo_discount.js"></script>				</div>				<div class="container">					До окончания действия скидки <xsl:value-of select="$discount_value"/>% осталось <strong id="dsc-timer-2"></strong>				</div>			</div>			<xsl:if test="not($window_closed)">				<div class="discount-alert result" id="discount-popup" style="display: none;">					<div>						<xsl:value-of select="page/common/discount_text" disable-output-escaping="yes"/>						<!-- <h2>Скидка <xsl:value-of select="$discount_value"/>% на заказ</h2>						<p>Если вы сделаете заказ в течение <span id="dsc-timer-1"></span>, то получите скидку.</p> -->						<span onclick="closeDiscountWindow(); return false;" class="button">Понятно</span>					</div>				</div>			</xsl:if>		</xsl:if>	</xsl:template></xsl:stylesheet>