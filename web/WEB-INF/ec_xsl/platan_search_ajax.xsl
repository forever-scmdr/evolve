<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="utils/price_conversions.xsl"/>

	<xsl:variable name="curr" select="items/currency"/>
	<xsl:variable name="ratio_rur" select="f:num(items/currency_ratio)"/>
	<xsl:variable name="q1_rur" select="f:num(items/q1)"/>
	<xsl:variable name="q2_rur" select="f:num(items/q2)"/>
	<xsl:variable name="view" select="items/view"/>

	<xsl:template match="/">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
		<html lang="ru">
			<head>
				<base href="{items/base}"/>
				<link href="https://fonts.googleapis.com/css?family=Roboto:100,300,400,700,900&amp;subset=cyrillic,cyrillic-ext"
					  rel="stylesheet"/>
				<link href="https://fonts.googleapis.com/css?family=Roboto+Condensed:100,300,400,700&amp;subset=cyrillic,cyrillic-ext"
					  rel="stylesheet"/>
				<link href="https://fonts.googleapis.com/css?family=Roboto+Slab:100,300,400,700&amp;subset=cyrillic,cyrillic-ext"
					  rel="stylesheet"/>
				<link rel="stylesheet" href="css/app.css"/>
			</head>
			<body>
				<div class="content-container">
					<div class="container columns">
						<div class="column-left desktop">zzz</div>
						<div class="column-right main-content">
							<div class="mc-container">
								<div class="page-content m-t">
									<!-- INFO -->
									<div id="extra_search_1" class="result catalog-items{' lines'[$view = 'list']}">
										<xsl:if test="$view = 'list'">
											<xsl:apply-templates select="items/item"/>
										</xsl:if>
										<xsl:if test="not($view = 'list')">
											<xsl:apply-templates select="items/item"/>
										</xsl:if>
									</div>
									<!-- END_INFO -->
								</div>
							</div>
						</div>
					</div>
				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="item">

		<xsl:variable name="price" select="f:price_platan(CENA_ROZ)"/>
		<xsl:variable name="price_pack" select="f:price_platan(CENA_PACK)"/>
		<xsl:variable name="delivery" select="POSTAV"/>
		<div class="device items-catalog__device">
			<a class="device__image" style="background-image: url('img/no_image.png');"></a>
			<a class="device__title" title="{name}">
				<xsl:value-of select="NAME"/>
			</a>
			<div class="device__article-number">
				<xsl:value-of select="NOM_N"/>
			</div>
			<xsl:if test="f:num(CENA_ROZ) != 0">
				<div class="device__price">
					<div class="price_normal">
						<xsl:value-of select="concat($price, '/', EI_NAME)"/>
					</div>
					<xsl:if test="f:num(CENA_PACK) != 0">
						<div class="price_special">
							Спец цена:
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
			</xsl:if>
			<xsl:if test="f:num(CENA_ROZ) = 0">
				<div class="device__price"></div>
			</xsl:if>
			<div class="device__order">
				<div id="cart_list_{NOM_N}">
					<form action="cart_action/?action=addPltToCart&amp;code={NOM_N}" method="post" ajax="true" ajax-loader-id="cart_list_{NOM_N}">
						<xsl:if test="f:num(CENA_ROZ) != 0">
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
						<xsl:if test="f:num(CENA_ROZ) = 0">
							<input type="hidden" value="platan" name="aux"/>
							<input type="hidden" value="{NAME}" name="name"/>
							<input type="hidden" value="1" name="not_available"/>
							<input type="hidden" value="{EI_NAME}" name="unit"/>
							<input type="hidden" value="{UPACK}" name="upack"/>
							<input type="hidden" value="{f:rur_to_byn(CENA_ROZ)}" name="price"/>
							<input type="hidden" value="{f:rur_to_byn(CENA_PACK)}" name="price_spec"/>
							<input type="number" class="text-input" name="qty" value="1" min="0"/>
							<input type="submit" class="button" value="Запросить цену"/>
						</xsl:if>
					</form>
				</div>
			</div>
			<xsl:if test="f:num(QUANTY) != 0">
				<div class="device__in-stock"><i class="fas fa-check"></i>поставка <xsl:value-of select="concat(f:num(QUANTY), ' ', EI_NAME, '.')" /> в течение 7-10 дней</div>
			</xsl:if>
			<xsl:if test="f:num(QUANTY) = 0">
			<div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i>под заказ</div>
			</xsl:if>
		</div>

	</xsl:template>
	<xsl:template match="item" mode="lines">
		<xsl:variable name="price" select="f:price_platan(CENA_ROZ)"/>
		<xsl:variable name="price_pack" select="f:price_platan(CENA_PACK)"/>
		<xsl:variable name="delivery" select="POSTAV"/>



	</xsl:template>

</xsl:stylesheet>