<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="../utils/price_conversions.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="view" select="page/variables/view"/>
	<xsl:variable name="in_stock" select="if(page/variables/minqty != '') then f:num(page/variables/minqty) else -1"/>
	<xsl:variable name="shop" select="page/shop"/>
	<xsl:variable name="result" select="/page/search/result"/>
	<xsl:variable name="products" select="$result/item"/>
	<xsl:variable name="query" select="/page/variables/q"/>

	<xsl:template match="/">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
		<html lang="ru">
		<head>
			<base href="{page/base}"/>
			<meta charset="utf-8"/>
			<link rel="stylesheet" href="css/app.css"/>
			<script defer="defer" src="js/font_awesome_all.js"></script>
		</head>
			<body>
				<xsl:if test="$products">
					<div id="gkel_search" class="result">
						<h2>Резальтаты поиска по каталогу "Отечественные РЭК"</h2>
						<div class="catalog-items{' lines'[$view = 'list']}">
							<xsl:if test="$view = 'list'">
								<xsl:apply-templates select="$products" mode="lines"/>
							</xsl:if>
							<xsl:if test="not($view = 'list')">
								<xsl:apply-templates select="$products"/>
							</xsl:if>
						</div>
						<script type="text/javascript">
							$(document).ready(function(){
								//Инициализация всплывающей панели для
								//элементов веб-страницы, имеющих атрибут
								//data-toggle="popover"
								$('[data-toggle="popover"]').popover({
									//Установление направления отображения popover
									placement : 'top'
								});
							});
							//обновить корзину
							insertAjax("cart_ajax");
						</script>
					</div>
				</xsl:if>
			</body>
		</html>
	</xsl:template>

	<!-- TABLE view -->
	<xsl:template match="item">
		<xsl:variable name="has_price" select="pb[f:num(text()) &gt; 0]"/>
		<xsl:variable name="min_price" select="min(pb/f:num(text()))"/>
		<xsl:variable name="prefix" select="if(count(pb) &gt; 1) then ' от ' else ''"/>

		<div class="device items-catalog__device">
			<span class="device__image" style="background-image: url('img/no_image.png');"></span>
			<a class="device__title">
				<xsl:value-of select="part" disable-output-escaping="yes"/>
			</a>
			<div class="device__article-number">
				<xsl:value-of select="code"/>
			</div>
			<div class="device__article-number"></div>
			<div class="device__price" style="display:block;">
				<div class="price_normal">
					<xsl:if test="$has_price">
						<xsl:value-of select="concat($prefix, f:price_output($min_price, $shop), ' ', upper-case($curr), '/шт.')" />
						<div class="nds">*цена включает НДС</div>
					</xsl:if>
					<xsl:if test="not($has_price)">Цена неизвестна</xsl:if>
				</div>
				<xsl:if test="$has_price">
					<xsl:if test="count(pb) &gt; 1">
						<xsl:variable name="prices">
							<xsl:for-each select="pb">
								<xsl:value-of select="concat(f:num(@qty), '+ ', '&lt;strong&gt;', concat(f:price_output(current(), $shop), ' ', upper-case($curr)), '&lt;/strong&gt;', '&lt;br/&gt;')"/>
							</xsl:for-each>
						</xsl:variable>
						<a data-container="body"  data-html="true" data-toggle="popover" data-placement="top" data-content="{$prices}">Цена зависит от количества</a>
					</xsl:if>
				</xsl:if>
			</div>
			<div class="device__order">
				<xsl:call-template name="CART_BUTTON">
					<xsl:with-param name="product" select="current()" />
				</xsl:call-template>
			</div>
			<xsl:if test="f:num(stock) &gt; 0">
				<div class="device__in-stock device_row__in-stock" style="max-width: 140px;">
					<i class="fas fa-check" />поставка <xsl:value-of select="concat(f:num(stock), ' шт.')" /> в течение <xsl:value-of select="$shop/delivery_string"/>
				</div>
			</xsl:if>
			<xsl:if test="not(f:num(stock) &gt; 0)">
				<div class="device__in-stock device_row__in-stock device__in-stock_no">
					<i class="far fa-clock"/>под заказ
				</div>
			</xsl:if>
		</div>
	</xsl:template>

	<!-- LIST view -->
	<xsl:template match="item" mode="lines">
		<xsl:variable name="has_price" select="pb[f:num(text()) &gt; 0]"/>
		<xsl:variable name="min_price" select="min(pb/f:num(text()))"/>
		<xsl:variable name="prefix" select="if(count(pb) &gt; 1) then ' от ' else ''"/>

		<div class="device device_row">
			<span class="device__image device_row__image" style="background-image: url('img/no_image.png');"></span>
			<div class="device__info">
				<span class="device__title">
					<xsl:value-of select="part" disable-output-escaping="yes"/>
				</span>
				<div class="device__description">
					<p class="basics">
						<br/><span><b>Арт.:</b>&#160;<xsl:value-of select="code" /></span>
						<br/><span><b>Производитель:</b>&#160;<xsl:value-of select="mfg" disable-output-escaping="yes" /></span>
						<xsl:if test="note != ''">
							<br/><span><b>Описание:</b>&#160;<xsl:value-of select="note" disable-output-escaping="yes" /></span>
						</xsl:if>
					</p>
				</div>
			</div>
			<div class="device__actions device_row__actions"></div>
			<div class="device__price device_row__price">
				<xsl:if test="$has_price">
					<xsl:value-of select="concat($prefix, f:price_output($min_price, $shop), ' ', upper-case($curr), '/шт.')" />
					<div class="nds">*цена c НДС</div>
					<xsl:if test="count(pb) &gt; 1"></xsl:if>
					<div class="manyPrice">
						<xsl:for-each select="pb">
							<div class="manyPrice__item">
								<div class="manyPrice__qty"><xsl:value-of select="@qty" />+</div>
								<div class="manyPrice__price"><xsl:value-of select="concat(f:price_output(., $shop), ' ', upper-case($curr))" /></div>
							</div>
						</xsl:for-each>
					</div>
				</xsl:if>
				<xsl:if test="not($has_price)">Цена неизвестна</xsl:if>
			</div>
			<div class="device__order device_row__order">
				<xsl:call-template name="CART_BUTTON">
					<xsl:with-param name="product" select="current()" />
				</xsl:call-template>
				<xsl:if test="f:num(stock) &gt; 0">
					<div class="device__in-stock device_row__in-stock" style="max-width: 140px;">
						<i class="fas fa-check" />поставка <xsl:value-of select="concat(f:num(stock), ' шт.')" /> в течение <xsl:value-of select="$shop/delivery_string"/>
					</div>
				</xsl:if>
				<xsl:if test="not(f:num(stock) &gt; 0)" >
					<div class="device__in-stock device_row__in-stock device__in-stock_no" style="max-width: 140px;">
						<i class="far fa-clock"/>под заказ
					</div>
				</xsl:if>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="CART_BUTTON">
		<xsl:param name="product"/>
		<xsl:variable name="has_price" select="pb[f:num(text()) &gt; 0]"/>

		<div id="cart_list_{code}">
			<form action="cart_action/?action=addExternalToCart" method="post" ajax="true" ajax-loader-id="cart_list_{code}">
				<xsl:call-template name="CART_BUTTON_COMMON">
					<xsl:with-param name="product" select="$product" />
				</xsl:call-template>
				<xsl:if test="(f:num($product/stock) &gt; 0) and $has_price">
					<xsl:call-template name="CART_BUTTON_AVAILABLE">
						<xsl:with-param name="product" select="$product" />
					</xsl:call-template>
				</xsl:if>
				<xsl:if test="not(f:num($product/stock) &gt; 0) or not($has_price)">
					<xsl:call-template name="CART_BUTTON_NOT_AVAILABLE"/>
				</xsl:if>
			</form>
		</div>
	</xsl:template>

	<xsl:template name="CART_BUTTON_COMMON">
		<xsl:param name="product"/>
		<xsl:variable name="has_price" select="pb[f:num(text()) &gt; 0]"/>

		<xsl:variable name="map" select="string-join($product/pb/concat(@qty, ':', .), ';')"/>
		<input type="number" class="text-input" name="qty" value="1" min="1" step="1"/>
		<input type="hidden" name="code" value="{code}"/>
		<input type="hidden" value="{$product/part}" name="vendor_code"/>
		<input type="hidden" name="id" value="{$product/code}"/>
		<input type="hidden" name="aux" value="{normalize-space($shop/name)}"/>
		<input type="hidden" value="{$product/part}" name="name"/>
		<input type="hidden" value="{$product/mfg}" name="vendor"/>
		<input type="hidden" value="шт." name="unit"/>
		<input type="hidden" value="1" name="min_qty"/>
		<input type="hidden" value="{if($has_price) then $map else ''}" name="price_map"/>
		<input type="hidden" name="delivery_time" value="{$shop/delivery_string}"/>
		<textarea style="display: none;" name="description">
			<xsl:value-of select="normalize-space($product/note)"/>
		</textarea>
	</xsl:template>

	<xsl:template name="CART_BUTTON_AVAILABLE">
		<xsl:param name="product"/>

		<input type="hidden" value="{normalize-space($product/stock)}" name="max"/>
		<input type="hidden" name="delivery_time" value="{$shop/delivery_string}"/>
		<input type="hidden" value="0" name="not_available"/>
		<input type="submit" class="button" value="В корзину"/>
	</xsl:template>

	<xsl:template name="CART_BUTTON_NOT_AVAILABLE">
		<input type="hidden" value="1" name="not_available"/>
		<input type="submit" class="button not_available" value="Под заказ"/>
	</xsl:template>


</xsl:stylesheet>