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
	<xsl:variable name="products" select="$result/product"/>

	<xsl:template match="/">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
		<html lang="ru">
			<head>
				<base href="{page/base}"/>
				<meta charset="utf-8"/>
				<link rel="stylesheet" href="css/app.css"/>
			</head>
			<body>
				<div id="mouser_search" class="result">
					<h2 class="search-header">Результат поиска по Mouser</h2>
					<xsl:if test="not($products)">
						<p>Товары не найдены</p>
					</xsl:if>
					<xsl:if test="$result/product">
						<div class="catalog-items{' lines'[$view = 'list']}">
							<xsl:if test="$view = 'list'">
								<xsl:apply-templates select="$products" mode="lines"/>
							</xsl:if>
							<xsl:if test="not($view = 'list')">
								<xsl:apply-templates select="$products"/>
							</xsl:if>
						</div>
					</xsl:if>
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
						insertAjax('cart_ajax');
					</script>
				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="product">

		<xsl:variable name="pic" select="if(main_pic/@path != '') then main_pic/@path else 'img/no_image.png'"/>
		<xsl:variable name="min" select="price_break[1]/qty"/>
		<xsl:variable name="price" select="price_break[1]/price"/>

		<div class="device items-catalog__device">
			<a class="device__image" style="background-image: url('{$pic}');"></a>
			<a class="device__title">
				<xsl:value-of select="name"/>
			</a>
			<div class="device__article-number">
				<xsl:value-of select="code"/>
			</div>
			<div class="device__article-number">
				Артикул производителя:
				<br/>
				<xsl:value-of select="vendor_code"/>
			</div>
			<xsl:if test="link != ''">
				<div class="device__article-number">
					<a href="{link}" target="_blank">Документация PDF</a>
				</div>
			</xsl:if>
			<div class="device__price" style="display:block;">
				<div class="price_normal" style="{'visibility: hidden;'[f:num(price) = 0]}">
					<xsl:value-of
							select="concat(f:price_output($price, $shop), ' ', upper-case($curr), ' за ', $min, 'шт')"/>
				</div>
				<div class="nds">*цена включает НДС</div>
				<xsl:if test="count(price_break) &gt; 1">
					<xsl:variable name="x">
						<xsl:for-each select="price_break">
							<xsl:value-of
									select="concat(f:num(qty), '+ ', '&lt;strong&gt;', f:price_output(price, $shop), ' ', upper-case($curr), '&lt;/strong&gt;', '&lt;br/&gt;')"/>
						</xsl:for-each>
					</xsl:variable>
					<a data-container="body" style="display:block;" data-html="true" data-toggle="popover"
					   data-placement="top" data-content="{$x}">Цена зависит от количества
					</a>
				</xsl:if>
			</div>
			<div class="device__order">
				<xsl:call-template name="CART_BUTTON">
					<xsl:with-param name="product" select="current()"/>
				</xsl:call-template>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="product" mode="lines">
		<xsl:variable name="pic" select="if(main_pic/@path != '') then main_pic/@path else 'img/no_image.png'"/>
		<xsl:variable name="min" select="price_break[1]/qty"/>
		<xsl:variable name="price" select="price_break[1]/price"/>

		<div class="device device_row">
			<a class="device__image device_row__image" style="background-image: url('{$pic}');"></a>
			<div class="device__info">
				<a class="device__title">
					<xsl:value-of select="name"/>
				</a>
				<div class="device__description">
					<p class="basics">
						<span><b>Код:</b>&#160;<xsl:value-of select="code"/>
						</span>
						<br/>
						<span><b>Ариткул производителя:</b>&#160;<xsl:value-of select="vendor_code"/>
						</span>
						<br/>
						<span><b>Производитель:</b>&#160;<xsl:value-of select="vendor"/>
						</span>
						<xsl:if test="link != ''">
							<br/>
							<span><b>Документация:</b>&#160;<a href="{link}" target="_blank">PDF</a>
							</span>
						</xsl:if>
						<xsl:if test="$min &gt; 1">
							<br/>
							<span><b>Минимальный заказ:</b>&#160;<xsl:value-of select="$min"/>
							</span>
							<br/>
							<span><b>Цена за мин. заказ:</b>&#160;<xsl:value-of
									select="concat(f:price_output($price, $shop), ' ', upper-case($curr))"/>
							</span>
						</xsl:if>
					</p>
					<xsl:if test="params/parameter != '' or f:num(leadtime) &gt; 0">
						<a style="color: #707070; text-decoration: underline;" class="javascript"
						   onclick="$('#tech-{code}').toggle();">Показать технические характеристики
						</a>
						<table class="features table-bordered" id="tech-{code}" style="display:none;">
							<xsl:for-each select="params/parameter">
								<tr>
									<td style="color: #616161; ">
										<xsl:value-of select="name"/>
									</td>
									<td>
										<xsl:value-of select="value"/>
									</td>
								</tr>
							</xsl:for-each>
						</table>
					</xsl:if>
				</div>
			</div>
			<div class="device__actions device_row__actions">
				<div style="visibility: hidden;" class="icon-link device__action-link"><i
						class="fas fa-balance-scale"></i>&#160;сравнить
				</div>
				<div style="visibility: hidden;" class="icon-link device__action-link">
					<i class="fas fa-star"></i>
					сравнить
				</div>
			</div>
			<div class="device__price device_row__price">

				<div class="price_normal">
					<xsl:if test="$min &gt; 1">
						<xsl:value-of
								select="concat(f:price_output($price, $shop), ' ', upper-case($curr),' за ', $min, 'шт')"/>
					</xsl:if>
					<xsl:if test="$min &lt; 2">
						<xsl:value-of
								select="concat(f:price_output($price, $shop), ' ', upper-case($curr), '/шт')"/>
					</xsl:if>
				</div>
				<div class="nds">*цена c НДС</div>

				<xsl:if test="count(price_break) &gt; 1">
					<div class="manyPrice">
						<xsl:for-each select="price_break">
							<div class="manyPrice__item">
								<div class="manyPrice__qty"><xsl:value-of select="f:num(qty)"/>+
								</div>
								<div class="manyPrice__price">
									<xsl:value-of select="f:price_output(price, $shop)"/>
								</div>
							</div>
						</xsl:for-each>
					</div>
				</xsl:if>
			</div>
			<div class="device__order device_row__order">
				<xsl:call-template name="CART_BUTTON">
					<xsl:with-param name="product" select="current()"/>
				</xsl:call-template>
			</div>
		</div>
	</xsl:template>


	<xsl:template name="CART_BUTTON">
		<xsl:param name="product"/>

		<xsl:variable name="pic" select="$product/main_pic/@path"/>
		<xsl:variable name="min" select="$product/min_qty"/>
		<xsl:variable name="step" select="$product/step"/>
		<xsl:variable name="vendor_code" select="$product/vendor_code"/>
		<xsl:variable name="qty" select="f:num($product/qty)"/>
		<xsl:variable name="available" select="$qty &gt; 0"/>
		<xsl:variable name="c" select="translate($product/code, ' ', '')"/>

		<div id="cart_list_{$c}">
			<form action="cart_action/?action=addExternalToCart&amp;code={$c}" method="post" ajax="true"
				  ajax-loader-id="cart_list_{$c}">
				<input type="hidden" value="{if($available) then 0 else 1}" name="not_available"/>
				<input type="hidden" value="{$c}" name="id"/>
				<input type="hidden" value="{$shop/name}" name="aux"/>
				<input type="hidden" value="{$product/name}" name="name"/>
				<input type="hidden" value="{$product/vendor}" name="vendor"/>
				<input type="hidden" value="{$vendor_code}" name="vendor_code"/>
				<input type="hidden" value="{$qty}" name="max"/>
				<input type="hidden" value="{$min}" name="min_qty"/>
				<input type="hidden" name="img" value="{$pic}"/>
				<input type="number" class="text-input" name="qty" value="{$min}" min="{$min}"
					   step="if(f:num($step) = 0) then 1 else $step"/>
				<input type="hidden" name="price_map"
					   value="{string-join(($product/price_break/concat(f:num(qty), ':', f:num(price))), ';')}"/>
				<input type="hidden" name="delivery_time" value="{if($available) then $shop/delivery_string else ''}"/>
				<input type="submit" class="button{' not_available'[not($available)]}"
					   value="{if($available) then 'В корзину' else 'Под заказ'}"/>
			</form>
		</div>
		<xsl:if test="$available">
			<div class="device__in-stock" style="max-width:140px;"><i class="fas fa-check"></i>поставка
				<xsl:value-of
						select="if($qty &lt; 500000) then concat(' ',$qty, ' шт.') else ''"/>
				в течение
				<xsl:value-of select="$shop/delivery_string"/>
			</div>
		</xsl:if>
		<xsl:if test="not($available)">
			<div class="device__in-stock device__in-stock_no" style="max-width:140px;"><i class="far fa-clock"></i>под
				заказ
			</div>
		</xsl:if>
	</xsl:template>


</xsl:stylesheet>