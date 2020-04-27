<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="utils/price_conversions.xsl"/>

	<xsl:variable name="curr" select="page/variables/currency"/>
	<xsl:variable name="ratio_rur" select="f:num(page/variables/rur_ratio)"/>
	<xsl:variable name="ratio_usd" select="f:num(page/variables/ratio)"/>
	<xsl:variable name="q1_usd" select="f:num(page/variables/q1)"/>
	<xsl:variable name="q2_usd" select="f:num(page/variables/q2)"/>
	<xsl:variable name="view" select="items/view"/>

	<xsl:template match="/">
		<div>
			<xsl:if test="page/product">
				<div id="extra_search_2" class="result">
					<h2>Результат поиска по дополнительному каталогу №2</h2>
					<div class="catalog-items{' lines'[$view = 'list']}">
						<xsl:if test="$view = 'list'">
							<xsl:apply-templates select="page/product" mode="lines"/>
						</xsl:if>
						<xsl:if test="not($view = 'list')">
							<xsl:apply-templates select="page/product"/>
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
					</script>
				</div>
			</xsl:if>
			<xsl:if test="not(page/product)">
				<div id="extra_search_2" class="result"></div>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="product">

		<xsl:variable name="price" select="f:price_digikey(price)"/>
		<xsl:variable name="price_pack" select="f:price_digikey(string(f:num(price)*f:num(min_qty)))"/>
		<xsl:variable name="delivery" select="POSTAV"/>
		<div class="device items-catalog__device">
			<xsl:if test="main_pic != ''">
				<a href="{main_pic}" class="magnific_popup-image zoom-icon" title="{name}" rel="nofollow">
					<i class="fas fa-search-plus"></i>
				</a>
				<a class="device__image" style="background-image: {concat('url(',main_pic,');')}"></a>
			</xsl:if>
			<xsl:if test="main_pic = ''">
				<a class="device__image" style="background-image: url('img/no_image.png');"></a>
			</xsl:if>
			<a class="device__title" >
				<xsl:value-of select="name"/>
			</a>
			<div class="device__article-number">
				<xsl:value-of select="code"/>
			</div>
			<div class="device__price">
				<div class="price_normal">
					<xsl:value-of select="concat($price, '/', 'шт')"/>
				</div>
			</div>
			<div class="device__order">
				<div id="cart_list_{code}">
					<form action="cart_action/?action=addPltToCart&amp;code={code}" method="post" ajax="true" ajax-loader-id="cart_list_{code}">
						<xsl:if test="f:num(qty) != 0">
							<input type="hidden" value="0" name="not_available"/>
							<input type="hidden" value="digikey" name="aux"/>
							<input type="hidden" value="{name}" name="name"/>
							<input type="hidden" value="шт" name="unit"/>
							<input type="hidden" value="{qty}" name="upack"/>
							<input type="hidden" value="{f:usd_to_byn(price)}" name="price"/>
							<input type="hidden" value="{f:usd_to_byn(price)}" name="price_spec"/>
							<input type="hidden" value="{qty}" name="max"/>
							<input type="number" class="text-input" name="qty" value="1" min="1"/>
							<input type="submit" class="button" value="В корзину"/>
						</xsl:if>
						<xsl:if test="f:num(qty) = 0">
							<input type="hidden" value="digikey" name="aux"/>
							<input type="hidden" value="{name}" name="name"/>
							<input type="hidden" value="1" name="not_available"/>
							<input type="hidden" value="шт" name="unit"/>
							<input type="hidden" value="0" name="upack"/>
							<input type="hidden" value="{f:usd_to_byn(price)}" name="price"/>
							<input type="hidden" value="{f:usd_to_byn(price)}" name="price_spec"/>
							<input type="number" class="text-input" name="qty" value="1" min="0"/>
							<input type="submit" class="button not_available" value="Под заказ"/>
						</xsl:if>
					</form>
				</div>
			</div>
			<xsl:if test="f:num(qty) != 0">
				<div class="device__in-stock"><i class="fas fa-check"></i>поставка <xsl:value-of select="concat(f:num(qty), ' ', шт, '.')" /> в течение 7-10 дней</div>
			</xsl:if>
			<xsl:if test="f:num(qty) = 0">
				<div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i>под заказ</div>
			</xsl:if>
		</div>

	</xsl:template>
	<xsl:template match="product" mode="lines">
		<xsl:variable name="price" select="f:price_platan(CENA_ROZ)"/>
		<xsl:variable name="price_pack" select="f:price_platan(CENA_PACK)"/>
		<xsl:variable name="delivery" select="POSTAV"/>

		<div class="device device_row">
			<a class="device__image device_row__image"
			   style="background-image: url(img/no_image.png);">&nbsp;
			</a>
			<div class="device__info">
				<a class="device__title">
					<xsl:value-of select="NAME"/>
				</a>
				<div class="device__description"></div>
			</div>
			<div class="device__article-number"><xsl:value-of select="NOM_N"/></div>
			<div class="device__actions device_row__actions">

			</div>
			<div class="device__price device_row__price">
				<div class="price_normal">
					<xsl:value-of select="concat($price, '/', EI_NAME)"/>
				</div>
				<xsl:if test="f:num(CENA_PACK) != 0">
					<div class="price_special">
						Спец цена:
						<br/>
						<span>
							<xsl:value-of select="$price_pack"/>
						</span>
						от
						<span>
							<xsl:value-of select="concat(UPACK, ' ', EI_NAME)"/>
						</span>
					</div>
				</xsl:if>
			</div>
			<div class="device__order device_row__order">
				<div id="cart_list_{NOM_N}">
					<form action="cart_action/?action=addPltToCart&amp;code={NOM_N}" method="post" ajax="true" ajax-loader-id="cart_list_{NOM_N}">
						<xsl:if test="f:num(QUANTY) != 0">
							<input type="hidden" value="0" name="not_available"/>
							<input type="hidden" value="platan" name="aux"/>
							<input type="hidden" value="{NAME}" name="name"/>
							<input type="hidden" value="{EI_NAME}" name="unit"/>
							<input type="hidden" value="{UPACK}" name="upack"/>
							<input type="hidden" value="{f:rur_to_byn(CENA_ROZ)}" name="price"/>
							<input type="hidden" value="{f:rur_to_byn(CENA_PACK)}" name="price_spec"/>
							<input type="hidden" value="{QUANTY}" name="max"/>
							<input type="number" class="text-input" name="qty" value="1" min="0"/>
							<input type="submit" class="button" value="В корзину"/>
						</xsl:if>
						<xsl:if test="f:num(QUANTY) = 0">
							<input type="hidden" value="platan" name="aux"/>
							<input type="hidden" value="{NAME}" name="name"/>
							<input type="hidden" value="1" name="not_available"/>
							<input type="hidden" value="{EI_NAME}" name="unit"/>
							<input type="hidden" value="{UPACK}" name="upack"/>
							<input type="hidden" value="{f:rur_to_byn(CENA_ROZ)}" name="price"/>
							<input type="hidden" value="{f:rur_to_byn(CENA_PACK)}" name="price_spec"/>
							<input type="number" class="text-input" name="qty" value="1" min="0"/>
							<input type="submit" class="button not_available" value="Под заказ"/>
						</xsl:if>
					</form>
				</div>
				<xsl:if test="f:num(QUANTY) != 0">
					<div class="device__in-stock device_row__in-stock" style="max-width: 140px;">
						<i class="fas fa-check" />поставка <xsl:value-of select="concat(f:num(QUANTY), ' ', EI_NAME, '.')" /> в течение 7-10 дней
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

</xsl:stylesheet>