<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="../utils/price_conversions.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="view" select="page/variables/view"/>
	<xsl:variable name="in_stock_only" select="page/variables/minqty = '0'"/>
	<xsl:variable name="mq" select="if(page/variables/minqty != '') then f:num(page/variables/minqty) else -1"/>
	<xsl:variable name="shop" select="page/shop"/>
	<xsl:variable name="result" select="/page/search/result"/>

	<xsl:template match="/">

<!--	<xsl:call-template name="TEST_MATH" />-->

		<xsl:if test="$result/items/item">
			<div id="platan_search" class="result">
				<h2 class="search-header">Результаты поиска по Platan</h2>
				<div class="catalog-items{' lines'[$view = 'list']}">
					<xsl:if test="$view = 'list'">
						<xsl:apply-templates select="$result/items/item[f:num(QUANTY) &gt; $mq]" mode="product-lines"/>
					</xsl:if>
					<xsl:if test="not($view = 'list')">
						<xsl:apply-templates select="$result/items/item[f:num(QUANTY) &gt; $mq]" mode="product-table"/>
					</xsl:if>
				</div>
			</div>
		</xsl:if>
		<xsl:if test="not($result/items/item)">
			<div id="extra_search_1" class="result">
				<h2 class="search-header">Результат поиска по Platan</h2>
				<p>Товары не найдены</p>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="item" mode="product-lines">
		<div class="device device_row">
			<a class="device__image device_row__image"
			   style="background-image: url(img/no_image.png);">&nbsp;
			</a>
			<div class="device__info">
				<a class="device__title">
					<xsl:value-of select="NAME"/>
				</a>
				<div class="device__description">
					<p class="basics">
						<span><b>Код:</b>&#160;<xsl:value-of select="NOM_N" /></span>
					</p>
				</div>
			</div>
			<div class="device__actions device_row__actions">
				<div id="compare_list_{@id}" style="visibility: hidden;" class="icon-link device__action-link"><i class="fas fa-balance-scale"></i>&#160;сравнить</div>
				<div id="fav_list_{@id}" style="visibility: hidden;" class="icon-link device__action-link"><i class="fas fa-star"></i>сравнить</div>
			</div>
			<div class="device__price device_row__price">
				<div class="price_normal">
					<xsl:value-of select="concat(f:price_output(CENA_ROZ, $shop), ' ', upper-case($curr), '/', EI_NAME)"/>
				</div>
				<div class="nds">
					*цена c НДС
				</div>
				<xsl:if test="f:num(CENA_PACK) != 0">
					<div class="price_special">
						Спец цена:
						<br/>
						<span>
							<xsl:value-of select="concat(f:price_output(CENA_PACK, $shop), ' ', upper-case($curr))"/>
						</span>
						от
						<span>
							<xsl:value-of select="concat(UPACK, ' ', EI_NAME)"/>
						</span>
					</div>
				</xsl:if>
			</div>
			<div class="device__order device_row__order">
				<xsl:call-template name="CART_BUTTON">
					<xsl:with-param name="product" select="current()" />
				</xsl:call-template>
				<xsl:if test="f:num(QUANTY) != 0">
					<div class="device__in-stock device_row__in-stock" style="max-width: 140px;">
						<i class="fas fa-check" />поставка <xsl:value-of select="concat(f:num(QUANTY), ' ', EI_NAME, '.')" /> в течение <xsl:value-of select="$shop/delivery_string"/>
					</div>
				</xsl:if>
				<xsl:if test="f:num(QUANTY) = 0">
					<div class="device__in-stock device_row__in-stock device__in-stock_no">
						<i class="far fa-clock"/>под заказ
					</div>
				</xsl:if>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="item" mode="product-table">
		<div class="device items-catalog__device">
			<a class="device__image" style="background-image: url('img/no_image.png');"></a>
			<a class="device__title" title="{name}">
				<xsl:value-of select="NAME"/>
			</a>
			<div class="device__article-number">
				<xsl:value-of select="NOM_N"/>
			</div>
			<xsl:if test="f:num(CENA_ROZ) != 0">
				<div class="device__price" style="flex-direction: column">
					<div class="price_normal">
						<xsl:value-of select="concat(f:price_output(CENA_ROZ, $shop), ' ', upper-case($curr), '/', EI_NAME)"/>
					</div>
					<div class="nds">
						*цена включает НДС
					</div>
					<xsl:if test="f:num(CENA_PACK) != 0">
						<div class="price_special">
							Спец цена:
							<span>
								<xsl:value-of select="concat(f:price_output(CENA_PACK, $shop), ' ', upper-case($curr))"/>
							</span>
							от
							<span>
								<xsl:value-of select="concat(UPACK, ' ', EI_NAME)"/>
							</span>
						</div>
					</xsl:if>
				</div>
			</xsl:if>
			<xsl:if test="f:num(CENA_ROZ) = 0">
				<div class="device__price"></div>
			</xsl:if>
			<div class="device__order">
				<xsl:call-template name="CART_BUTTON">
					<xsl:with-param name="product" select="current()" />
				</xsl:call-template>
			</div>
			<xsl:if test="f:num(QUANTY) != 0">
				<div class="device__in-stock"><i class="fas fa-check"></i>поставка <xsl:value-of select="concat(f:num(QUANTY), ' ', EI_NAME, '.')" /> в течение <xsl:value-of select="$shop/delivery_string"/></div>
			</xsl:if>
			<xsl:if test="f:num(QUANTY) = 0">
				<div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i>под заказ</div>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template name="CART_BUTTON">
		<xsl:param name="product"/>

		<div id="cart_list_{$product/NOM_N}">
			<form action="cart_action/?action=addExternalToCart&amp;code={$product/NOM_N}" method="post" ajax="true" ajax-loader-id="cart_list_{$product/NOM_N}">
				<xsl:call-template name="CART_BUTTON_COMMON">
					<xsl:with-param name="product" select="$product" />
				</xsl:call-template>
				<xsl:if test="f:num($product/QUANTY) != 0">
					<xsl:call-template name="CART_BUTTON_AVAILABLE">
						<xsl:with-param name="product" select="$product" />
					</xsl:call-template>
				</xsl:if>
				<xsl:if test="f:num($product/QUANTY) = 0">
					<xsl:call-template name="CART_BUTTON_NOT_AVAILABLE"/>
				</xsl:if>
			</form>
		</div>
	</xsl:template>
	<xsl:template name="CART_BUTTON_COMMON">
		<xsl:param name="product"/>

		<xsl:variable name="price_roz" select="f:num($product/CENA_ROZ)"/>
		<xsl:variable name="price_pack" select="f:num($product/CENA_PACK)"/>
		<xsl:variable name="m1" select="if($price_roz &gt; 0) then concat($product/MINZAKAZ, ':', $price_roz) else $nothing"/>
		<xsl:variable name="m2" select="if($price_pack &gt; 0) then concat($product/UPACK, ':', $price_pack) else $nothing"/>
		<xsl:variable name="min" select="if(f:num($product/MINZAKAZ) &gt; 0) then $product/MINZAKAZ else 1"/>

		<input type="hidden" value="{$product/NOM_N}" name="id"/>
		<input type="hidden" value="{$shop/name}" name="aux"/>
		<input type="hidden" value="{$product/NAME}" name="name"/>
		<input type="hidden" value="{$product/MANUFAC}" name="vendor"/>
		<input type="hidden" value="{$product/EI_NAME}" name="unit"/>
		<input type="hidden" value="{$min}" name="min_qty"/>
		<input type="hidden" value="{string-join(($m1, $m2), ';')}" name="price_map"/>
		<input type="number" class="text-input" name="qty" value="{$min}" min="{$min}"/>
	</xsl:template>
	<xsl:template name="CART_BUTTON_AVAILABLE">
		<xsl:param name="product"/>
		<input type="hidden" value="{$product/QUANTY}" name="max"/>
		<input type="hidden" name="delivery_time" value="{$shop/delivery_string}"/>
		<input type="hidden" value="0" name="not_available"/>
		<input type="submit" class="button" value="В корзину"/>
	</xsl:template>
	<xsl:template name="CART_BUTTON_NOT_AVAILABLE">
		<input type="hidden" value="1" name="not_available"/>
		<input type="submit" class="button not_available" value="Под заказ"/>
	</xsl:template>

	<xsl:template name="TEST_MATH">
		<xsl:variable name="sum" select="100"/>

		<p>test sum: <xsl:value-of select="$sum"/> RUB</p>
		<p>to BYN: <xsl:value-of select="f:currency_to_byn($sum, $shop/currency)"/> BYN</p>
		<p>to RUB: <xsl:value-of select="f:byn_to_currency(f:currency_to_byn($sum, $shop/currency), $sel_cur)"/> RUB</p>
		<p>
			q RUB: <xsl:value-of select="1 + f:num($shop/currency/q)"/><br/>
			q <xsl:value-of select="$shop/name"/>: <xsl:value-of select="1+ f:num($shop/q)"/><br/>
			Output: <xsl:value-of select="concat(f:price_output($sum, $shop), upper-case($curr))" />
		</p>
	</xsl:template>
</xsl:stylesheet>