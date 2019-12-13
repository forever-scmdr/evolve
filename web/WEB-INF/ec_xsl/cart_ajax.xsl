<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:f="f:f"
		version="2.0">
<!--	<xsl:import href="utils/price_conversions.xsl"/>-->
	<xsl:import href="common_page_base.xsl"/>

	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="cart" select="page/cart"/>
	<xsl:variable name="ass" select="page/product[not(code = $cart/bought/code)]"/>

	<xsl:template match="/">
		<xsl:if test="$cart and (not($cart/processed) or $cart/processed != 1) and $cart/bought">
			<div class="result" id="cart_ajax">
				<xsl:if test="f:num($cart/sum) &gt; 0">
					<xsl:if test="not($ass)">
						<a href="{page/show_cart}"><i class="fas fa-shopping-cart"></i>Заявка</a>
					</xsl:if>
					<xsl:if test="$ass">
						<a data-toggle="modal" data-target="#assoc-products-modal"><i class="fas fa-shopping-cart"></i>Заявка</a>
					</xsl:if>
					<div>Наименований: <strong><xsl:value-of select="count($cart/bought)"/></strong></div>
					<div>Сумма: <strong><xsl:value-of select="f:currency_decimal($cart/sum)"/> руб.</strong></div>
				</xsl:if>
				<xsl:if test="f:num($cart/sum) = 0">
					<xsl:if test="not($ass)">
						<a href="{page/show_cart}"><i class="fas fa-shopping-cart"></i>Заявка</a>
					</xsl:if>
					<xsl:if test="$ass">
						<a data-toggle="modal" data-target="#assoc-products-modal"><i class="fas fa-shopping-cart"></i>Заявка</a>
					</xsl:if>
					<div>Наименований: <strong><xsl:value-of select="count($cart/bought)"/></strong></div>
					<div>Сумма по запросу</div>
				</xsl:if>
			</div>
			<xsl:for-each select="$cart/bought">
				<xsl:if test="not($ass[code = current()/code])">
					<div class="result" id="cart_list_{product/@id}">
						<form>
							<input type="submit" value="Оформить заявку" class="button to_cart" onclick="location.replace('{//page/show_cart}'); return false"/>
						</form>
					</div>
				</xsl:if>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="not($cart) or $cart/processed = 1 or not($cart/bought)">
			<div class="result" id="cart_ajax">
				<i class="fas fa-shopping-cart"/> <strong>Заявок нет</strong>
			</div>
		</xsl:if>
		<xsl:if test="$ass">
			<div class="result" id="assoc-products-modal">
				<div class="modal-dialog" role="document">
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" data-dismiss="modal" aria-label="Close">
								<span aria-hidden="true">×</span>
							</button>
							<div class="modal-title h4">Сопутствующие товары</div>
							<a href="page/cart_link">Оформить заказ</a>
						</div>
						<div class="modal-body" id="associated-products-list">
							<div class="catalog-items">
								<xsl:apply-templates select="$ass"/>
							</div>
						</div>
					</div>
				</div>
			</div>
		</xsl:if>
		<xsl:if test="not($ass)">
			<div class="modal-body result" id="associated-products-list">
				<p>Все сопутствующие товары уже в корзине.</p>
			</div>
		</xsl:if>
	</xsl:template>


	<xsl:template name="ASSOC_PRODUCTS">
		<div id="assoc-products-modal" class="modal fade" tabindex="-1" role="dialog" show-loader="yes">
			HUI
		</div>
	</xsl:template>

</xsl:stylesheet>