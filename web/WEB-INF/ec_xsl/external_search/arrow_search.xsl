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


	<xsl:template match="product">
		<div class="device items-catalog__device">
			<xsl:if test="main_pic != ''">
				<a class="device__image" style="background-image: url('{main_pic}');"></a>
			</xsl:if>
			<xsl:if test="not(main_pic != '')">
				<a class="device__image" style="background-image: url('img/no_image.png');"></a>
			</xsl:if>
			<a class="device__title" >
				<xsl:value-of select="name"/>
			</a>
			<div class="device__article-number">
				<xsl:value-of select="code"/>
			</div>
			<xsl:if test="doc != ''">
				<div class="device__article-number">
					<a href="{doc}" target="_blank">Документация PDF</a>
				</div>
			</xsl:if>
			<div class="device__price">
				<div class="price_normal">
					<xsl:variable name="price" select="string(min(current()//price))"/>
					<xsl:if test="f:num($price) &gt; 0">
						<xsl:variable name="output_price" select="if(count(offer) = 1) then f:price_output($price, $shop) else concat('от ', f:price_output($price, $shop))"/>
						<xsl:value-of select="concat($output_price, ' ', upper-case($curr), '/шт.')" />
					</xsl:if>
					<xsl:if test="f:num($price) = 0">
						Цена по запросу
					</xsl:if>
				</div>
			</div>
			<xsl:if test="count(offer) = 1">
				<xsl:call-template name="CART_BUTTON">
					<xsl:with-param name="offer" select="offer"/>
					<xsl:with-param name="product" select="current()"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="count(offer) &gt; 1">
				<input type="submit" onclick="$('#price-popup-{@id}').show()" class="button" value="Подробнее"/>
			</xsl:if>
			<xsl:if test="not(offer)">
				<xsl:call-template name="CART_BUTTON_ZERO" >
					<xsl:with-param name="product" select="current()"/>
				</xsl:call-template>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="product" mode="lines">
		<div class="device device_row">
			<xsl:if test="main_pic != ''">
				<a class="device__image device_row__image" style="background-image: url('{main_pic}');">&nbsp;</a>
			</xsl:if>
			<xsl:if test="not(main_pic != '')">
				<a class="device__image device_row__image" style="background-image: url('img/no_image.png');">&nbsp;</a>
			</xsl:if>
			<div class="device__info">
				<a class="device__title">
					<xsl:value-of select="name"/>
				</a>
				<div class="device__description">
					<p class="basics">
						<span><b>Код:</b>&#160;<xsl:value-of select="code"/></span><br/>
						<span><b>Производитель:</b>&#160;<xsl:value-of select="vendor" /></span><br/>
						<span><b>Страна производства:</b>&#160;<xsl:value-of select="offer[1]/country" /></span>
						<xsl:if test="doc != ''">
							<br/><span><b>Документация:</b>&#160;<a href="{doc}" target="_blank">файл PDF</a></span>
						</xsl:if>
					</p>
				</div>
			</div>
			<div class="device__actions device_row__actions">
				<div id="compare_list_{@id}" style="visibility: hidden;" class="icon-link device__action-link"><i class="fas fa-balance-scale"></i>&#160;сравнить</div>
				<div id="fav_list_{@id}" style="visibility: hidden;" class="icon-link device__action-link"><i class="fas fa-star"></i>сравнить</div>
			</div>
			<div class="device__price device_row__price">
				<div class="price_normal">
					<xsl:variable name="price" select="string(min(current()//price))"/>
					<xsl:if test="f:num($price) &gt; 0">
						<xsl:variable name="output_price" select="if(count(offer) = 1) then f:price_output($price, $shop) else concat('от ', f:price_output($price, $shop))"/>
						<xsl:value-of select="concat($output_price, ' ', upper-case($curr), '/шт.')" />
					</xsl:if>
					<xsl:if test="f:num($price) = 0">
						Цена по запросу
					</xsl:if>
				</div>
				<div class="nds">*цена c НДС</div>
				<xsl:if test="count(offer) = 1 and count(offer/price) &gt; 1">
					<xsl:variable name="prc" select="offer/price" />
					<xsl:variable name="p" select="position()"/>
					<div class="manyPrice">
						<xsl:for-each select="offer/min_qty">
							<xsl:variable name="p" select="position()"/>
							<div class="manyPrice__item">
								<div class="manyPrice__qty"><xsl:value-of select="." />+</div>
								<div class="manyPrice__price"><xsl:value-of select="concat(f:price_output($prc[$p], $shop), ' ', upper-case($curr))" /></div>
							</div>
						</xsl:for-each>
					</div>
				</xsl:if>
			</div>
			<div class="device__order device_row__order">
				<xsl:if test="count(offer) = 1">
					<xsl:call-template name="CART_BUTTON">
						<xsl:with-param name="offer" select="offer"/>
						<xsl:with-param name="product" select="current()"/>
					</xsl:call-template>
				</xsl:if>
				<xsl:if test="count(offer) &gt; 1">
					<input type="submit" onclick="$('#price-popup-{@id}').show()" class="button" value="Подробнее"/>
				</xsl:if>
				<xsl:if test="not(offer)">
					<xsl:call-template name="CART_BUTTON_ZERO" >
						<xsl:with-param name="product" select="current()"/>
					</xsl:call-template>
				</xsl:if>
			</div>
		</div>
	</xsl:template>


	<xsl:template match="product" mode="offers_popup">
		<div class="pop" id="price-popup-{@id}" style="display: none;">
			<div class="pop__body">
				<div class="pop__title">
					<a class="pop__close"><img src="{//page/base}/img/icon-close.png" alt=""/></a>
					<xsl:value-of select="name"/><br/>
					<span style="color: gray; font-size: 20px;">
						<xsl:value-of select="concat('Код: ', code)"/>
					</span>
				</div>
				<div style="margin-bottom:15px;"></div>
				<div class="pop-prices">
					<xsl:apply-templates select="offer"/>
				</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="offer">
		<div class="pop-prices__item pop-price">
			<div class="pop-price__price" style="font-size: 16px;">
				<xsl:value-of select="concat('Доступно: ', format-number(qty, '# ###', 'r'), 'шт')" />
			</div>
			<div class="pop-price__variants">
				<xsl:variable name="min_qty" select="min_qty"/>
				<xsl:for-each select="price">
					<xsl:variable name="p" select="position()"/>
					<div class="pop-price__variant">
						<div><xsl:value-of select="$min_qty[$p]"/>+</div>
						<div><xsl:value-of select="concat(f:price_output(., $shop), ' ', upper-case($curr))"/></div>
					</div>
				</xsl:for-each>
				<div class="pop-price__order" style="max-width: 150px; margin-top: 1rem;">
					<xsl:variable name="product" select="current()/.."/>
					<xsl:call-template name="CART_BUTTON">
						<xsl:with-param name="offer" select="current()"/>
						<xsl:with-param name="product" select="$product"/>
					</xsl:call-template>
				</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="CART_BUTTON_ZERO">
		<xsl:param name="product" />
		<xsl:variable name="id" select="concat('v',$product/@id)"/>

		<div class="device__order">
			<div id="cart_list_{$id}">
				<form action="cart_action/?action=addExternalToCart&amp;code={$id}" method="post" ajax="true" ajax-loader-id="cart_list_{$id}">
					<input type="hidden" value="{$product/code}" name="vendor_code"/>
					<input type="hidden" value="{$id}" name="id"/>
					<input type="hidden" value="1" name="not_available"/>
					<input type="hidden" value="{$shop/name}" name="aux"/>
					<input type="hidden" value="{$product/vendor}" name="vendor"/>

					<input type="hidden" value="{$product/name}" name="name"/>
					<input type="hidden" value="шт" name="unit"/>
					<input type="hidden" value="1" name="upack"/>

					<input type="hidden" value="0" name="max"/>
					<input type="hidden" value="min_qty" name="min(min_qty/f:num(.))"/>
					<input type="number" class="text-input" name="qty" value="{min(min_qty/f:num(.))}" min="{min(min_qty/f:num(.))}"/>

					<input type="hidden" name="img" value="{$product/main_pic}"/>
					<input type="submit" class="button not_available" value="Под заказ"/>
				</form>
			</div>
		</div>
		<div class="device__in-stock device__in-stock_no">
			<i class="far fa-clock"></i>под заказ
		</div>
	</xsl:template>

	<xsl:template name="CART_BUTTON">
		<xsl:param name="offer" />
		<xsl:param name="product" />

		<xsl:variable name="code" select="replace($offer/code, ':', '-')"/>
		<xsl:variable name="map" select="f:map($offer)"/>

		<div class="device__order">
			<div id="cart_list_{$code}">
				<form action="cart_action/?action=addExternalToCart&amp;code={$code}" method="post" ajax="true" ajax-loader-id="cart_list_{$code}">
					<input type="hidden" value="{$product/code}" name="vendor_code"/>
					<input type="hidden" value="{if(f:num($offer) = 0) then 1 else  0}" name="not_available"/>
					<input type="hidden" value="{$shop/name}" name="aux"/>
					<input type="hidden" value="{$code}" name="id"/>

					<input type="hidden" value="{$product/name}" name="name"/>
					<input type="hidden" value="{$product/vendor}" name="vendor"/>
					<input type="hidden" value="шт" name="unit"/>
					<input type="hidden" value="{$offer/step}" name="upack"/>

					<input type="hidden" value="{$offer/qty}" name="max"/>
					<input type="hidden" value="{f:num($offer/min_qty[1])}" name="min_qty"/>
					<input type="number" class="text-input" name="qty" value="{$offer/min_qty[1]}" min="{$offer/min_qty[1]}"/>

					<xsl:if test="f:num($offer/qty) != 0">
						<input type="hidden" name="price_map" value="{$map}"/>
					</xsl:if>

					<input type="hidden" name="img" value="{$product/main_pic}"/>
					<input type="hidden" name="delivery_time" value="{$shop/delivery_string}"/>
					<input type="submit" class="button{' not_available'[f:num($offer/qty) = 0]}" value="{if(f:num($offer/qty) != 0) then 'В корзину' else 'Под заказ'}"/>
				</form>
			</div>
		</div>
		<xsl:if test="f:num($offer/qty) != 0">
			<div class="device__in-stock">
				<i class="fas fa-check"></i>поставка <xsl:value-of select="concat(format-number($offer/qty, '# ###', 'r'), 'шт.')"/><br/>в течение <xsl:value-of select="$shop/delivery_string"/>
			</div>
		</xsl:if>
		<xsl:if test="f:num($offer/qty) = 0">
			<div class="device__in-stock device__in-stock_no">
				<i class="far fa-clock"></i>под заказ
			</div>
		</xsl:if>
	</xsl:template>


	<xsl:template match="/">

		<xsl:variable name="products" select="if(page/variables/minqty = '0') then $result/products/product[offer/f:num(qty) != 0] else $result/products/product"/>

		<div class="result" id="arrow_search">
			<xsl:if test="$result/summery/response/success = 'true'">
				<h2 class="search-header">Результат поиска по Arrow</h2>

				<xsl:if test="page/variables/minqty = '0' and count($products) = 0">
					<p>Найдены только товары под заказ.</p>
				</xsl:if>

				<div class="catalog-items{' lines'[$view = 'list']}">
					<xsl:if test="$view = 'list'">
						<xsl:apply-templates select="$products"  mode="lines" />
					</xsl:if>
					<xsl:if test="not($view = 'list')">
						<xsl:apply-templates select="$products" />
					</xsl:if>
				</div>
				<div>
					<xsl:apply-templates select="$result/products/product" mode="offers_popup"/>
				</div>
			</xsl:if>
			<xsl:if test="not($result/summery/response/success = 'true')">
				<h2 class="search-header">Результат поиска по Arrow</h2>
				<p>Товары не найдены</p>
				<xsl:if test="$result/summery/response/error">
					<p>
						<xsl:value-of select="page/summery/response/error"/>
					</p>
				</xsl:if>
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
	</xsl:template>

</xsl:stylesheet>