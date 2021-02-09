<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0"
				xmlns:xs="http://www.w3.org/1999/XSL/Transform">
	<xsl:import href="utils/price_conversions.xsl"/>

	<xsl:variable name="curr" select="page/variables/currency"/>
	<xsl:variable name="in_stock" select="if(page/variables/minqty != '') then f:num(page/variables/minqty) else -1"/>
	<xsl:variable name="view" select="page/variables/view"/>

	<xsl:template match="/">
		<xsl:if test="page/result/row">
			<div id="extra_search_4" class="result">
				<h2>Результат поиска по дополнительному каталогу №4</h2>
				<div class="catalog-items{' lines'[$view = 'list']}">
					<xsl:if test="$view = 'list'">
						<xsl:apply-templates select="page/result/row" mode="lines"/>
					</xsl:if>
					<xsl:if test="not($view = 'list')">
						<xsl:apply-templates select="page/result/row"/>
					</xsl:if>
				</div>
			</div>
		</xsl:if>
		<xsl:if test="not(page/result/row)">
			<div id="extra_search_2" class="result"></div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="row">
		<xsl:variable name="pricebreaks" select="if(pricebreaks != '') then pricebreaks else vendor/pricebreaks" />
		<xsl:variable name="moq" select="@moq" />
		<xsl:variable name="min_price" select="min($pricebreaks/break/f:num(@price))"/>

		<div class="device items-catalog__device">
			<xsl:if test="@photo_url != ''">
				<a href="{@photo_url}" class="magnific_popup-image zoom-icon" title="{@name}" rel="nofollow">
					<i class="fas fa-search-plus"></i>
				</a>
				<a class="device__image" style="background-image: url('{@photo_url}');"></a>
			</xsl:if>
			<xsl:if test="not(@photo_url != '')">
				<a class="device__image" style="background-image: url('img/no_image.png');"></a>
			</xsl:if>
			<a class="device__title" >
				<xsl:value-of select="@name"/>
			</a>
			<div class="device__article-number">
				<xsl:value-of select="@id"/>
			</div>
			<div class="device__article-number">
				Артикул производителя:<br/>
				<xsl:value-of select="@name"/>
			</div>
			<div class="device__price">
				<div class="price_normal">
					от <xsl:value-of select="f:price_promelec(string($min_price))"/>/шт.
				</div>
			</div>
			<xsl:if test="not(vendors/vendor)">
				<xsl:variable name="prices">
					<xsl:for-each select="$pricebreaks/break">
						<xsl:value-of select="concat(@quant, '+ ', '&lt;strong&gt;', f:price_promelec(@price), '&lt;/strong&gt;', '&lt;br/&gt;')"/>
					</xsl:for-each>
				</xsl:variable>
				<a data-container="body"  data-html="true" data-toggle="popover" data-placement="top" data-content="{$prices}">Цена зависит от количества</a>
				<div class="nds" style="margin-top: -8px; margin-bottom: 10px;">*цена включает НДС</div>
				<xsl:call-template name="CART_CORE">
					<xsl:with-param name="vendor" select="." />
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="vendors/vendor">
				<input type="submit" class="button" value="Подробнее"/>
				<div id="price-popup-{@id}">
					<xsl:variable name="id" select="@id"/>
					<xsl:variable name="name" select="@name"/>
					<xsl:call-template name="TO_CART">
						<xsl:with-param name="vendor" select="."/>
					</xsl:call-template>
					<xsl:for-each select="vendors/vendor">
						<xsl:call-template name="TO_CART">
							<xsl:with-param name="vendor" select="."/>
							<xsl:with-param name="name" select="$name"/>
							<xsl:with-param name="id" select="$id"/>
							<xsl:with-param name="pic" select="@photo_url" />
						</xsl:call-template>
					</xsl:for-each>
				</div>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="row" mode="lines">

	</xsl:template>

	<xsl:template name="TO_CART">
		<xs:param name="vendor"/>
		<xs:param name="name" select="$vendor/@name"/>
		<xs:param name="id" select="$vendor/@id"/>
		<xsl:param name="pic" select="$vendor/@photo_url"/>


		<div>
			<xsl:for-each select="$vendor/pricebreaks/break">
				<xsl:value-of select="concat(@quant, '+ ')"/>
				<strong>
					<xsl:value-of select="@price"/>
				</strong><br/>
			</xsl:for-each>
			<xsl:call-template name="CART_CORE">
				<xsl:with-param name="name" select="$name"/>
				<xsl:with-param name="vendor" select="."/>
				<xsl:with-param name="id" select="$id"/>
				<xsl:with-param name="pic" select="$pic"/>
			</xsl:call-template>
		</div>

	</xsl:template>


	<xsl:template name="CART_CORE">
		<xs:param name="vendor" />
		<xs:param name="name"  select="$vendor/@name"/>
		<xs:param name="id" select="$vendor/@id" />
		<xsl:param name="pic" select="$vendor/@photo_url"/>

		<xsl:variable name="code" select="if($id != $vendor/@id) then concat($id, 'v',$vendor/@id) else $id"/>
		<xsl:variable name="map" select="string-join(pricebreaks/break/concat(@quant, ':', f:rur_to_byn_promelec(@price)), ';')"/>

		<div class="device__order">
			<div id="cart_list_{concat($id, $code)}">
				<form action="cart_action/?action=addPromelecToCart&amp;code={$code}" method="post" ajax="true" ajax-loader-id="cart_list_{$code}">
					<input type="hidden" value="{$name}" name="vendor_code"/>
					<input type="hidden" value="{if(f:num(@qant) != 0) then 0 else 1}" name="not_available"/>
					<input type="hidden" value="promelec" name="aux"/>

					<input type="hidden" value="{$name}" name="name"/>
					<input type="hidden" value="шт" name="unit"/>
					<input type="hidden" value="{@moq}" name="upack"/>

					<input type="hidden" value="{@quant}" name="max"/>
					<input type="number" class="text-input" name="qty" value="{@moq}" min="{@moq}"/>

					<xsl:if test="f:num(qty) != 0">
						<input type="hidden" name="price_map" value="{$map}"/>
					</xsl:if>
					<input type="hidden" name="img" value="{$pic}"/>
					<input type="submit" class="button" value="В корзину"/>
				</form>
			</div>
		</div>
		<xsl:if test="f:num(@qty) != 0">
			<xsl:variable name="days" select="concat(f:num(@delivery)+7 ,'-', f:num(@delivery)+14)"/>
			<div class="device__in-stock"><i class="fas fa-check"></i>поставка<xsl:value-of select="if(f:num(@qty) &lt; 500000) then concat(' ',f:num(@qty), ' шт.') else ''" /> в течение <xsl:value-of select="$days"/> дней</div>
		</xsl:if>
		<xsl:if test="f:num(@qty) = 0">
			<div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i>под заказ</div>
		</xsl:if>

	</xsl:template>
</xsl:stylesheet>