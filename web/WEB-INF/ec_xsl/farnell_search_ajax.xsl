<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="utils/price_conversions.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="page" select="keywordSearchReturn"/>
	<xsl:variable name="curr" select="normalize-space($page/variables/currency)"/>
	<xsl:variable name="ratio_rur" select="f:num($page/variables/rur_ratio)"/>
	<xsl:variable name="ratio_eur" select="f:num($page/variables/eur_ratio)"/>
	<xsl:variable name="q1_eur" select="f:num($page/variables/q1_eur)"/>
	<xsl:variable name="q2_eur" select="f:num($page/variables/q2_eur)"/>
	<xsl:variable name="view" select="normalize-space($page/variables/view)"/>
	<xsl:variable name="apos">'</xsl:variable>

	<xsl:variable name="found" select="if(f:num($page/numberOfResults) &gt; 500) then 'более 500' else f:num($page/numberOfResults)"/>

	<xsl:template match="/">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
		<html lang="ru">
			<head>
				<base href="{page/base}"/>
				<meta charset="utf-8"/>
				<link rel="stylesheet" href="css/app.css"/>
			</head>
			<body>
				<div id="extra_search_3" class="result">
					<h2>Результат поиска по Farnell</h2>
					<xsl:if test="not($page/products)">
						<p>Товары не найдены</p>
					</xsl:if>
					<xsl:if test="$page/products">
						<div class="catalog-items{' lines'[$view = 'list']}">
							<xsl:if test="$view = 'list'">
								<xsl:apply-templates select="$page/products" mode="lines"/>
							</xsl:if>
							<xsl:if test="not($view = 'list')">
								<xsl:apply-templates select="$page/products"/>
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
		<xsl:variable name="available" select="f:num(stock/status) = 1"/>
		<xsl:variable name="vendor_code" select="normalize-space(translatedManufacturerPartNumber)" />

		<div class="device device_row">
			<a class="device__image device_row__image"
			   style="background-image: url({concat($apos,'https://ru.farnell.com/productimages/standard/ru_RU',normalize-space(image/baseName) , $apos)});"> </a>
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
									<xsl:if test="position() &gt; 1"></xsl:if>
									<a href="{normalize-space(url)}" target="_blank">
										<xsl:value-of select="normalize-space(if(description) then description else 'документ PDF')" />
									</a>
								</xsl:for-each>
							</span>
						</xsl:if>
						<xsl:if test="$min &gt; 1">
							<br/><span><b>Минимальный заказ:</b>&#160;<xsl:value-of select="$min"/></span>
							<br/><span><b>Цена за мин. заказ:</b>&#160;<xsl:value-of select="f:price_farnell(string($price_pack))"/></span>
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
			<div class="device__article-number"><xsl:value-of select="normalize-space(sku)"/></div>
			<div class="device__actions device_row__actions"></div>
			<div class="device__price device_row__price">
				<xsl:if test="prices[f:num(from) = 1]">
					<div class="price_normal">
						<xsl:if test="$min &gt; 1">
							<xsl:value-of select="concat(f:price_farnell(string($price_pack)), ' за ', $min, 'шт')"/>
						</xsl:if>
						<xsl:if test="$min &lt; 2">
							<xsl:value-of select="concat(f:price_farnell(string($price_pack)), '/шт')"/>
						</xsl:if>
					</div>
					<div class="nds">*цена c НДС</div>
				</xsl:if>
				<div class="manyPrice">
					<xsl:for-each select="prices">
						<div class="manyPrice__item">
							<div class="manyPrice__qty"><xsl:value-of select="f:num(from)" />+</div>
							<div class="manyPrice__price"><xsl:value-of select="f:price_farnell(cost)" /></div>
						</div>
					</xsl:for-each>
				</div>
			</div>
			<div class="device__order device_row__order">
				<div id="cart_list_{normalize-space(sku)}">
					<form action="cart_action/?action=addFarnellToCart&amp;code={normalize-space(sku)}" method="post" ajax="true" ajax-loader-id="cart_list_{normalize-space(sku)}">
						<input type="hidden" value="{if($available) then 0 else 1}" name="not_available"/>
						<input type="hidden" value="farnell" name="aux"/>
						<input type="hidden" value="{displayName}" name="name"/>
						<input type="hidden" value="{normalize-space(vendorName)}" name="vendor"/>
						<input type="hidden" value="{$vendor_code}" name="vendor_code"/>
						<input type="hidden" value="{f:num(stock/level)}" name="max"/>
						<input type="hidden" value="{$min}" name="min_qty"/>
						<input type="hidden" name="img" value="{concat('https://ru.farnell.com/productimages/standard/ru_RU',normalize-space(image/baseName))}"/>
						<input type="number" class="text-input" name="qty" value="{$min}" min="{$min}"/>
						<xsl:for-each select="prices">
							<input type="hidden" name="price" value="{concat(f:num(from), ':', f:num(cost))}"/>
						</xsl:for-each>
						<input type="hidden" name="delivery_time" value="{if($available) then '7-14 дней' else ' '}"/>
						<input type="submit" class="button{' not_available'[not($available)]}" value="{if($available) then 'В корзину' else 'Под заказ'}"/>
					</form>
					<xsl:if test="$available">
<div class="device__in-stock" style="max-width:140px;"><i class="fas fa-check"></i>поставка<xsl:value-of select="if(f:num(stock/level) &lt; 500000) then concat(' ',f:num(stock/level), ' шт.') else ''" /> в течение 7-14 дней</div>
					</xsl:if>
					<xsl:if test="not($available)">
						<div class="device__in-stock device__in-stock_no" style="max-width:140px;"><i class="far fa-clock"></i>под заказ</div>
					</xsl:if>
				</div>
			</div>
		</div>
	</xsl:template>
	<xsl:template match="products">
		<xsl:variable name="pic" select="concat('https://ru.farnell.com/productimages/standard/ru_RU',normalize-space(image/baseName))"/>
		<xsl:variable name="min" select="f:num(translatedMinimumOrderQuality)"/>
		<xsl:variable name="price_pack" select="$min * f:num(prices[f:num(from) = $min]/cost)"/>
		<xsl:variable name="available" select="f:num(stock/status) = 1"/>
		<xsl:variable name="vendor_code" select="normalize-space(translatedManufacturerPartNumber)" />

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
					<xsl:if test="$min &gt; 1">
						<xsl:value-of select="concat(f:price_farnell(string($price_pack)), ' за ', $min, 'шт')"/>
					</xsl:if>
					<xsl:if test="$min &lt; 2">
						<xsl:value-of select="concat(f:price_farnell(string($price_pack)), '/шт')"/>
					</xsl:if>
				</div>
				<div class="nds">*цена включает НДС</div>
				<xsl:if test="count(prices) &gt; 1">
					<xsl:variable name="x">
						<xsl:for-each select="prices">
							<xsl:value-of select="concat(f:num(from), '+ ', '&lt;strong&gt;', f:price_farnell(cost), '&lt;/strong&gt;', '&lt;br/&gt;')"/>
						</xsl:for-each>
					</xsl:variable>
					<a data-container="body" style="display:block;" data-html="true" data-toggle="popover" data-placement="top" data-content="{$x}">Цена зависит от количества</a>
				</xsl:if>
			</div>
			<div class="device__order">
				<div id="cart_list_{normalize-space(sku)}">
					<form action="cart_action/?action=addFarnellToCart&amp;code={normalize-space(sku)}" method="post" ajax="true" ajax-loader-id="cart_list_{normalize-space(sku)}">

						<input type="hidden" value="{if($available) then 0 else 1}" name="not_available"/>
						<input type="hidden" value="farnell" name="aux"/>
						<input type="hidden" value="{displayName}" name="name"/>
						<input type="hidden" value="{normalize-space(vendorName)}" name="vendor"/>
						<input type="hidden" value="{$vendor_code}" name="vendor_code"/>
						<input type="hidden" value="{f:num(stock/level)}" name="max"/>
						<input type="hidden" value="{$min}" name="min_qty"/>
						<input type="hidden" name="img" value="{concat('https://ru.farnell.com/productimages/standard/ru_RU',normalize-space(image/baseName))}"/>
						<input type="number" class="text-input" name="qty" value="{$min}" min="{$min}"/>
						<xsl:for-each select="prices">
							<input type="hidden" name="price" value="{concat(f:num(from), ':', f:num(cost))}"/>
						</xsl:for-each>
						<input type="hidden" name="delivery_time" value="{if($available) then '7-14 дней' else ' '}"/>
						<input type="submit" class="button{' not_available'[not($available)]}" value="{if($available) then 'В корзину' else 'Под заказ'}"/>
					</form>
					<xsl:if test="$available">
						<div class="device__in-stock"><i class="fas fa-check"></i>поставка<xsl:value-of select="if(f:num(stock/level) &lt; 500000) then concat(' ',f:num(stock/level), ' шт.') else ''" /> в течение 7-14 дней</div>
					</xsl:if>
					<xsl:if test="not($available)">
						<div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i>под заказ</div>
					</xsl:if>
				</div>
			</div>
		</div>
	</xsl:template>
</xsl:stylesheet>