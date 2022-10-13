<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="../utils/price_conversions.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="view" select="page/variables/view"/>
	<xsl:variable name="shop" select="page/shop"/>
	<xsl:variable name="result" select="/page/search/result/keywordSearchReturn"/>
	<xsl:variable name="apos">'</xsl:variable>

	<xsl:variable name="found" select="if(f:num($result/numberOfResults) &gt; 500) then 'более 500' else f:num($result/numberOfResults)"/>

	<xsl:template match="/">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
		<html lang="ru">
			<head>
				<base href="{page/base}"/>
				<meta charset="utf-8"/>
				<link rel="stylesheet" href="css/app.css"/>
			</head>
			<body>
				<div id="farnell_search" class="result">
					<h2 class="search-header">Результат поиска по Farnell</h2>
					<xsl:if test="not($result/products)">
						<p>Товары не найдены</p>
					</xsl:if>
					<xsl:if test="$result/products">
						<div class="catalog-items{' lines'[$view = 'list']}">
							<xsl:if test="$view = 'list'">
								<xsl:apply-templates select="$result/products" mode="lines"/>
							</xsl:if>
							<xsl:if test="not($view = 'list')">
								<xsl:apply-templates select="$result/products"/>
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
					</xsl:if>
				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="products" mode="lines">
		<xsl:variable name="pic" select="concat('https://ru.farnell.com/productimages/standard/ru_RU',normalize-space(image/baseName))"/>
		<xsl:variable name="min" select="f:num(translatedMinimumOrderQuality)"/>
		<xsl:variable name="price_pack" select="$min * f:num(prices[f:num(from) = $min]/cost)"/>
		<xsl:variable name="vendor_code" select="normalize-space(translatedManufacturerPartNumber)" />

		<div class="device device_row">
			<a class="device__image device_row__image"
			   style="background-image: url({concat($apos,$pic, $apos)});"> </a>
			<div class="device__info">
				<a class="device__title">
					<xsl:value-of select="normalize-space(displayName)"/>
				</a>
				<div class="device__description">
					<p class="basics">
						<span><b>Код:</b>&#160;<xsl:value-of select="normalize-space(sku)"/></span><br/>
						<span><b>Ариткул производителя:</b>&#160;<xsl:value-of select="$vendor_code"/></span><br/>
						<span><b>Производитель:</b>&#160;<xsl:value-of select="normalize-space(vendorName)" /></span>
						<xsl:if test="datasheets/url != ''">
							<br/><span><b>Документация:</b>&#160;
								<xsl:for-each select="datasheets">
									<a href="{normalize-space(url)}" target="_blank">
										<xsl:value-of select="normalize-space(if(description) then description else 'документ PDF')" />
									</a>
								</xsl:for-each>
							</span>
						</xsl:if>
						<xsl:if test="$min &gt; 1">
							<br/><span><b>Минимальный заказ:</b>&#160;<xsl:value-of select="$min"/></span>
							<br/><span><b>Цена за мин. заказ:</b>&#160;<xsl:value-of select="concat(f:price_output($price_pack, $shop), ' ', upper-case($curr))"/></span>
						</xsl:if>
					</p>
					<xsl:if test="attributes/normalize-space(attributeValue) != ''">
						<a style="color: #707070; text-decoration: underline;" class="javascript" onclick="$('#tech-{normalize-space(sku)}').toggle();">Показать технические характеристики</a>
						<table class="features table-bordered" id="tech-{normalize-space(sku)}" style="display:none;">
							<xsl:for-each select="attributes">
								<tr>
									<td style="color: #616161; "><xsl:value-of select="string-join(attributeLabel | attributeUnit, ', ')"/></td>
									<td><xsl:value-of select="attributeValue"/></td>
								</tr>
							</xsl:for-each>
						</table>
					</xsl:if>
				</div>
			</div>
			<div class="device__actions device_row__actions">
				<div id="compare_list_{@id}" style="visibility: hidden;" class="icon-link device__action-link"><i class="fas fa-balance-scale"></i>&#160;сравнить</div>
				<div id="fav_list_{@id}" style="visibility: hidden;" class="icon-link device__action-link"><i class="fas fa-star"></i>сравнить</div>
			</div>
			<div class="device__price device_row__price">
				<xsl:if test="prices[f:num(from) = 1]">
					<div class="price_normal"> по запросу
<!--						<xsl:if test="$min &gt; 1">-->
<!--							<xsl:value-of select="concat(f:price_output($price_pack, $shop), ' ', upper-case($curr),' за ', $min, 'шт')"/>-->
<!--						</xsl:if>-->
<!--						<xsl:if test="$min &lt; 2">-->
<!--							<xsl:value-of select="concat(f:price_output($price_pack, $shop), ' ', upper-case($curr), '/шт')"/>-->
<!--						</xsl:if>-->
					</div>
<!--					<div class="nds">*цена c НДС</div>-->
				</xsl:if>
<!--				<div class="manyPrice">-->
<!--					<xsl:for-each select="prices">-->
<!--						<div class="manyPrice__item">-->
<!--							<div class="manyPrice__qty"><xsl:value-of select="f:num(from)" />+</div>-->
<!--							<div class="manyPrice__price"><xsl:value-of select="f:price_output(cost, $shop)" /></div>-->
<!--						</div>-->
<!--					</xsl:for-each>-->
<!--				</div>-->
			</div>
			<div class="device__order device_row__order">
				<xsl:call-template name="CART_BUTTON">
					<xsl:with-param name="product" select="current()"/>
				</xsl:call-template>
			</div>
		</div>
	</xsl:template>
	<xsl:template match="products">
		<xsl:variable name="pic" select="concat('https://ru.farnell.com/productimages/standard/ru_RU',normalize-space(image/baseName))"/>
		<xsl:variable name="min" select="f:num(translatedMinimumOrderQuality)"/>
		<xsl:variable name="price_pack" select="$min * f:num(prices[f:num(from) = $min]/cost)"/>

		<div class="device items-catalog__device">
			<a href="{$pic}" class="magnific_popup-image zoom-icon" title="{displayName}" rel="nofollow">
				<i class="fas fa-search-plus"></i>
			</a>
			<a class="device__image" style="background-image: url('{$pic}');"></a>

			<a class="device__title">
				<xsl:value-of select="normalize-space(displayName)"/>
			</a>

			<div class="device__article-number">
				<xsl:value-of select="sku"/>
			</div>
			<div class="device__article-number">
				Артикул производителя:<br/>
				<xsl:value-of select="normalize-space(translatedManufacturerPartNumber)"/>
			</div>
			<div class="device__price" style="display:block;">
				<div class="price_normal">
<!--					<xsl:if test="$min &gt; 1">-->
<!--						<xsl:value-of select="concat(f:price_output($price_pack, $shop), ' ', upper-case($curr), ' за ', $min, 'шт')"/>-->
<!--					</xsl:if>-->
<!--					<xsl:if test="$min &lt; 2">-->
<!--						<xsl:value-of select="concat(f:price_output($price_pack, $shop), ' ', upper-case($curr), '/шт')"/>-->
<!--					</xsl:if>-->
				</div>
<!--				<div class="nds">*цена включает НДС</div>-->
<!--				<xsl:if test="count(prices) &gt; 1">-->
<!--					<xsl:variable name="x">-->
<!--						<xsl:for-each select="prices">-->
<!--							<xsl:value-of select="concat(f:num(from), '+ ', '&lt;strong&gt;', f:price_output(cost, $shop), ' ', upper-case($curr), '&lt;/strong&gt;', '&lt;br/&gt;')"/>-->
<!--						</xsl:for-each>-->
<!--					</xsl:variable>-->
<!--					<a data-container="body" style="display:block;" data-html="true" data-toggle="popover" data-placement="top" data-content="{$x}">Цена зависит от количества</a>-->
<!--				</xsl:if>-->
			</div>
			<div class="device__order">
				<xsl:call-template name="CART_BUTTON">
					<xsl:with-param name="product" select="current()"/>
				</xsl:call-template>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="CART_BUTTON">
		<xsl:param name="product"/>

		<xsl:variable name="pic" select="concat('https://ru.farnell.com/productimages/standard/ru_RU',normalize-space($product/image/baseName))"/>
		<xsl:variable name="min" select="f:num($product/translatedMinimumOrderQuality)"/>
		<xsl:variable name="available" select="f:num($product/stock/status) = 1"/>
		<xsl:variable name="vendor_code" select="normalize-space($product/translatedManufacturerPartNumber)" />

		<div id="cart_list_{normalize-space($product/sku)}">
			<form action="cart_action/?action=addExternalToCart&amp;code={normalize-space($product/sku)}" method="post" ajax="true" ajax-loader-id="cart_list_{normalize-space($product/sku)}">
				<input type="hidden" value="{if($available) then 0 else 1}" name="not_available"/>
				<input type="hidden" value="{normalize-space($product/sku)}" name="id"/>
				<input type="hidden" value="{$shop/name}" name="aux"/>
				<input type="hidden" value="{displayName}" name="name"/>
				<input type="hidden" value="{normalize-space($product/vendorName)}" name="vendor"/>
				<input type="hidden" value="{$vendor_code}" name="vendor_code"/>
				<input type="hidden" value="{f:num($product/stock/level)}" name="max"/>
				<input type="hidden" value="{$min}" name="min_qty"/>
				<input type="hidden" name="img" value="{$pic}"/>
				<input type="number" class="text-input" name="qty" value="{$min}" min="{$min}"/>
				<input type="hidden" name="price_map" value="{string-join(($product/prices/concat(f:num(from), ':', f:num(cost))), ';')}"/>
				<input type="hidden" name="delivery_time" value="{if($available) then $shop/delivery_string else ''}"/>
				<input type="submit" class="button{' not_available'[not($available)]}" value="{if($available) then 'В корзину' else 'Под заказ'}"/>
			</form>
		</div>
		<xsl:if test="$available">
			<div class="device__in-stock" style="max-width:140px;"><i class="fas fa-check"></i>поставка<xsl:value-of select="if(f:num($product/stock/level) &lt; 500000) then concat(' ',f:num($product/stock/level), ' шт.') else ''" /> в течение <xsl:value-of select="$shop/delivery_string"/></div>
		</xsl:if>
		<xsl:if test="not($available)">
			<div class="device__in-stock device__in-stock_no" style="max-width:140px;"><i class="far fa-clock"></i>под заказ</div>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>