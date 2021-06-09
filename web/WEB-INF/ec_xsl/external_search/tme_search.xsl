<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="../utils/price_conversions.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="view" select="page/variables/view"/>
	<xsl:variable name="shop" select="page/shop"/>
	<xsl:variable name="result" select="/page/search/result"/>
	<xsl:variable name="products" select="$result/response/Data/ProductList/Product"/>

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
				<div id="tme_search" class="result">
					<h2>Результат поиска по TME</h2>
					<div class="catalog-items{' lines'[$view = 'list']}">
						<xsl:if test="$view = 'list'">
							<xsl:apply-templates select="$products" mode="lines"/>
						</xsl:if>
						<xsl:if test="not($view = 'list')">
							<xsl:apply-templates select="$products"/>
						</xsl:if>
					</div>
					<script type="text/javascript">
						$(".magnific_popup-image, a[rel=facebox]").magnificPopup({
						type: 'image',
						closeOnContentClick: true,
						mainClass: 'mfp-img-mobile',
						image: {
						verticalFit: true
						}
						});
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
			</body>
		</html>
	</xsl:template>

	<xsl:template match="Product">
		<xsl:variable name="pic" select="if(Photo != '') then concat('https:', normalize-space(Photo)) else 'img/no_image.png'" />
		<xsl:variable name="price" select="PriceList/Price"/>
		<xsl:variable name="min_price" select="min($price/f:num(PriceValue))"/>
		<xsl:variable name="prefix" select="if(count($price) &gt; 1) then ' от ' else ''"/>
		<xsl:variable name="unit" select="if(normalize-space(Unit) = 'pcs') then 'шт' else normalize-space(Unit)"/>

		<div class="device items-catalog__device">
			<a href="{$pic}" class="magnific_popup-image zoom-icon" title="{normalize-space(Symbol)}" rel="nofollow">
				<i class="fas fa-search-plus" ></i>
			</a>
			<span class="device__image" style="background-image: url('{$pic}');"></span>
			<a class="device__title">
				<xsl:value-of select="normalize-space(Symbol)"/>
			</a>
			<div class="device__article-number">
				<xsl:value-of select="normalize-space(OriginalSymbol)"/>
			</div>
			<div class="device__article-number"></div>
			<div class="device__price" style="display:block;">
				<div class="price_normal">
					<xsl:value-of select="concat($prefix, f:price_output($min_price, $shop), ' ', upper-case($curr), '/', $unit)" />
				</div>
				<xsl:variable name="prices">
					<xsl:for-each select="$price">
						<xsl:value-of select="concat(normalize-space(Amount), '+ ', '&lt;strong&gt;', concat(f:price_output(PriceValue, $shop), ' ', upper-case($curr)), '&lt;/strong&gt;', '&lt;br/&gt;')"/>
					</xsl:for-each>
				</xsl:variable>
				<a data-container="body"  data-html="true" data-toggle="popover" data-placement="top" data-content="{$prices}">Цена зависит от количества</a>
				<div class="nds">*цена включает НДС</div>
			</div>
			<div class="device__order">
				<xsl:call-template name="CART_BUTTON">
					<xsl:with-param name="product" select="current()" />
				</xsl:call-template>
			</div>
			<xsl:if test="f:num(Amount) != 0">
				<div class="device__in-stock device_row__in-stock" style="max-width: 140px;">
					<i class="fas fa-check" />поставка <xsl:value-of select="concat(f:num(Amount), ' ', $unit, '.')" /> в течение <xsl:value-of select="$shop/delivery_string"/>
				</div>
			</xsl:if>
			<xsl:if test="f:num(Amount) = 0">
				<div class="device__in-stock device_row__in-stock device__in-stock_no">
					<i class="far fa-clock"/>под заказ
				</div>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="Product" mode="lines">

		<xsl:variable name="pic" select="if(Photo != '') then concat('https:', normalize-space(Photo)) else 'img/no_image.png'" />
		<xsl:variable name="price" select="PriceList/Price"/>
		<xsl:variable name="min_price" select="min($price/f:num(PriceValue))"/>
		<xsl:variable name="prefix" select="if(count($price) &gt; 1) then ' от ' else ''"/>
		<xsl:variable name="unit" select="if(normalize-space(Unit) = 'pcs') then 'шт' else normalize-space(Unit)"/>

		<div class="device device_row">
			<a href="{$pic}" class="magnific_popup-image zoom-icon" title="{normalize-space(Symbol)}" rel="nofollow">
				<i class="fas fa-search-plus" ></i>
			</a>
			<span class="device__image device_row__image" style="background-image: url('{$pic}');"></span>
			<div class="device__info">
				<span class="device__title">
					<xsl:value-of select="Symbol" />
				</span>
				<div class="device__description">
					<p class="basics">
						<br/><span><b>Арт.:</b>&#160;<xsl:value-of select="Symbol" /></span>
						<br/><span><b>Производитель:</b>&#160;<xsl:value-of select="Producer" /></span>
						<br/><span><b>Арт. производителя:</b>&#160;<xsl:value-of select="OriginalSymbol" /></span>
						<xsl:if test="Description != ''">
							<br/><span><b>Описание:</b>&#160;<xsl:value-of select="Description" disable-output-escaping="yes" /></span>
						</xsl:if>
						<br/><span><b>Вес:</b>&#160;<xsl:value-of select="concat(normalize-space(Weight), normalize-space(WeightUnit))"/></span>
					</p>
				</div>
			</div>
<!--			<div class="device__article-number"><xsl:value-of select="Symbol"/></div>-->
			<div class="device__actions device_row__actions"></div>
			<div class="device__price device_row__price">
				<xsl:value-of select="concat($prefix, f:price_output($min_price, $shop), ' ', upper-case($curr), '/', $unit)" />
				<div class="nds">*цена c НДС</div>
				<xsl:if test="count($price) &gt; 1"></xsl:if>
				<div class="manyPrice">
					<xsl:for-each select="$price">
						<div class="manyPrice__item">
							<div class="manyPrice__qty"><xsl:value-of select="Amount" />+</div>
							<div class="manyPrice__price"><xsl:value-of select="concat(f:price_output(PriceValue, $shop), ' ', upper-case($curr))" /></div>
						</div>
					</xsl:for-each>
				</div>
			</div>
			<div class="device__order device_row__order">
				<xsl:call-template name="CART_BUTTON">
					<xsl:with-param name="product" select="current()" />
				</xsl:call-template>
				<xsl:if test="f:num(Amount) != 0">
					<div class="device__in-stock device_row__in-stock" style="max-width: 140px;">
						<i class="fas fa-check" />поставка <xsl:value-of select="concat(f:num(Amount), ' ', $unit, '.')" /> в течение <xsl:value-of select="$shop/delivery_string"/>
					</div>
				</xsl:if>
				<xsl:if test="f:num(Amount) = 0">
					<div class="device__in-stock device_row__in-stock device__in-stock_no">
						<i class="far fa-clock"/>под заказ
					</div>
				</xsl:if>
			</div>
		</div>

	</xsl:template>

	<xsl:template name="CART_BUTTON">
		<xsl:param name="product"/>

		<xsl:variable name="sbl" select="replace(replace(normalize-space($product/Symbol), '\.', '_dot_'), '/', '_sls_')"/>

		<div id="cart_list_{$sbl}">
			<form action="cart_action/?action=addExternalToCart" method="post" ajax="true" ajax-loader-id="cart_list_{$sbl}">
				<xsl:call-template name="CART_BUTTON_COMMON">
					<xsl:with-param name="product" select="$product" />
				</xsl:call-template>
				<xsl:if test="f:num($product/Amount) != 0">
					<xsl:call-template name="CART_BUTTON_AVAILABLE">
						<xsl:with-param name="product" select="$product" />
					</xsl:call-template>
				</xsl:if>
				<xsl:if test="f:num($product/Amount) = 0">
					<xsl:call-template name="CART_BUTTON_NOT_AVAILABLE"/>
				</xsl:if>
			</form>
		</div>

	</xsl:template>

	<xsl:template name="CART_BUTTON_COMMON">
		<xsl:param name="product"/>

		<xsl:variable name="sbl" select="replace(replace(normalize-space($product/Symbol), '\.', '_dot_'), '/', '_sls_')"/>
		<xsl:variable name="price" select="$product/PriceList/Price"/>
		<xsl:variable name="map" select="string-join($price/concat(normalize-space(Amount), ':', normalize-space(PriceValue)), ';')"/>
		<xsl:variable name="unit" select="if(normalize-space($product/Unit) = 'pcs') then 'шт' else normalize-space(Unit)"/>
		<xsl:variable name="pic" select="if(normalize-space(Photo) != '') then concat('https:', normalize-space(Photo)) else 'img/no_image.png'" />

		<input type="number" class="text-input" name="qty" value="{normalize-space($product/MinAmount)}" min="{normalize-space($product/MinAmount)}" step="{normalize-space($product/Multiples)}"/>
		<input type="hidden" name="code" value="{normalize-space($product/Symbol)}"/>
		<input type="hidden" value="{$product/OriginalSymbol}" name="vendor_code"/>
		<input type="hidden" name="id" value="{$sbl}"/>
		<input type="hidden" name="aux" value="{normalize-space($shop/name)}"/>
		<input type="hidden" value="{normalize-space($product/Symbol)}" name="name"/>
		<input type="hidden" value="{normalize-space($product/Producer)}" name="vendor"/>
		<input type="hidden" value="{$unit}" name="unit"/>
		<input type="hidden" value="{normalize-space($product/MinAmount)}" name="min_qty"/>
		<input type="hidden" value="{$map}" name="price_map"/>
		<input type="hidden" name="img" value="{$pic}"/>
		<input type="hidden" name="delivery_time" value="{$shop/delivery_string}"/>
		<textarea style="display: none;" name="description">
			<xsl:value-of select="normalize-space($product/Description)"/>
		</textarea>
	</xsl:template>

	<xsl:template name="CART_BUTTON_AVAILABLE">
		<xsl:param name="product"/>

		<input type="hidden" value="{normalize-space($product/Amount)}" name="max"/>
		<input type="hidden" name="delivery_time" value="{$shop/delivery_string}"/>
		<input type="hidden" value="0" name="not_available"/>
		<input type="submit" class="button" value="В корзину"/>
	</xsl:template>

	<xsl:template name="CART_BUTTON_NOT_AVAILABLE">
		<input type="hidden" value="1" name="not_available"/>
		<input type="submit" class="button not_available" value="Под заказ"/>
	</xsl:template>

</xsl:stylesheet>