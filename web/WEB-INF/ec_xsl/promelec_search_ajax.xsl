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
				<div>
					<xsl:for-each select="page/result/row[vendors/vendor/pricebreaks/break]">
						<xsl:call-template name="CART_POPUP">
							<xsl:with-param name="row" select="." />
						</xsl:call-template>
					</xsl:for-each>
				</div>
			</div>
		</xsl:if>
		<xsl:if test="not(page/result/row)">
			<div id="extra_search_4" class="result"></div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="row">
		<xsl:variable name="pricebreaks" select="if(pricebreaks != '') then pricebreaks else vendors/vendor/pricebreaks" />
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
				<input type="submit" onclick="$('#price-popup-{./@item_id}').show()" class="button" value="Подробнее"/>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="row" mode="lines">
		<xsl:variable name="pricebreaks" select="if(pricebreaks != '') then pricebreaks else vendors/vendor/pricebreaks" />
		<xsl:variable name="min_price" select="min($pricebreaks/break/f:num(@price))"/>

		<div class="device device_row">
			<a class="device__image device_row__image"
			   style="background-image: url('{if(@photo_url != '') then @photo_url else 'img/no_image.png'}');">&nbsp;
			</a>
			<div class="device__info">
				<a class="device__title">
					<xsl:value-of select="@name"/>
				</a>
				<div class="device__description">
					<p class="basics">
						<span><b>Код:</b>&#160;<xsl:value-of select="@item_id"/></span><br/>
						<span><b>Производитель:</b>&#160;<xsl:value-of select="@producer_name" /></span>
						<xsl:if test="@description != ''">
							<br/><span><b>Описание:</b>&#160;<xsl:value-of select="@description" disable-output-escaping="yes" /></span>
						</xsl:if>
					</p>
				</div>
			</div>
			<div class="device__article-number"><xsl:value-of select="@item_id"/></div>
			<div class="device__actions device_row__actions"></div>
			<div class="device__price device_row__price">
				<div class="price_normal">
					от <xsl:value-of select="f:price_platan(string($min_price))"/> за шт.
				</div>
				<div class="nds">*цена c НДС</div>
				<xsl:if test="not(vendors/vendor)">
					<div class="manyPrice__item">
						<xsl:for-each select="$pricebreaks/break">
							<div class="manyPrice__qty"><xsl:value-of select="@qty" />+</div>
							<div class="manyPrice__price"><xsl:value-of select="f:price_promelec(@price)" /></div>
						</xsl:for-each>
					</div>
				</xsl:if>
			</div>
			<div class="device__order device_row__order">
				<xsl:if test="not(vendors/vendor)">
					<xsl:call-template name="CART_CORE">
						<xsl:with-param name="vendor" select="." />
					</xsl:call-template>
				</xsl:if>
				<xsl:if test="vendors/vendor">
					<input type="submit" onclick="$('#price-popup-{./@item_id}').show()" class="button" value="Подробнее"/>
				</xsl:if>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="CART_POPUP">
		<xsl:param name="row" />

		<div class="pop" id="price-popup-{$row/@item_id}" style="display: none;">
			<div class="pop__body">
				<div class="pop__title">
					<xsl:value-of select="string-join(($row/@name, $row/@class2name), ' ')"/>
					<a class="pop__close"><img src="{//page/base}/img/icon-close.png" alt=""/></a>
					<p style="font-size: 16px; font-weight: normal;">
						<xsl:value-of select="$row/@description" disable-output-escaping="yes" />
					</p>
				</div>
				<div class="pop-prices">
					<xsl:call-template name="POPUP_PRICE_VARIANT">
						<xsl:with-param name="vendor" select="$row"/>
					</xsl:call-template>
					<xsl:for-each select="$row/vendors/vendor">
						<xsl:call-template name="POPUP_PRICE_VARIANT">
							<xsl:with-param name="vendor" select="."/>
							<xsl:with-param name="name" select="$row/@name"/>
							<xsl:with-param name="id" select="$row/@item_id"/>
							<xsl:with-param name="pic" select="$row/@photo_url" />
						</xsl:call-template>
					</xsl:for-each>
				</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="POPUP_PRICE_VARIANT">
		<xsl:param name="vendor"/>
		<xsl:param name="name" select="$vendor/@name"/>
		<xsl:param name="id" select="$vendor/@item_id"/>
		<xsl:param name="pic" select="$vendor/@photo_url"/>

		<xsl:if test="$vendor/pricebreaks/break">
			<div class="pop-prices__item pop-price">
				<div class="pop-price__price" style="font-size: 16px;">
					<xsl:value-of select="concat('Доступно: ', @quant, 'шт.')" />
				</div>
				<div class="pop-price__variants">
					<xsl:for-each select="$vendor/pricebreaks/break">
						<div class="pop-price__variant">
							<div><xsl:value-of select="@quant"/>+</div>
							<div><xsl:value-of select="f:price_promelec(@price)"/></div>
						</div>
					</xsl:for-each>
					<div class="pop-price__order" style="max-width: 150px; margin-top: 1rem;">
						<xsl:call-template name="CART_CORE">
							<xsl:with-param name="vendor" select="$vendor" />
							<xsl:with-param name="name" select="$name"/>
							<xsl:with-param name="id" select="$id"/>
							<xsl:with-param name="pic" select="$pic"/>
						</xsl:call-template>
					</div>
				</div>
			</div>
		</xsl:if>
	</xsl:template>


	<xsl:template name="CART_CORE">
		<xs:param name="vendor" />
		<xs:param name="name" select="$vendor/@name"/>
		<xs:param name="id" select="$vendor/@item_id" />
		<xsl:param name="pic" select="$vendor/@photo_url"/>

		<xsl:if test="$vendor/pricebreaks/break">

			<xsl:variable name="code" select="if($vendor/@id) then concat($id, 'v',$vendor/@id) else $id"/>
			<xsl:variable name="map" select="string-join($vendor/pricebreaks/break/concat(@quant, ':', f:rur_to_byn_promelec(@price)), ';')"/>

			<div class="device__order">
				<div id="cart_list_{$code}">
					<form action="cart_action/?action=addPromelecToCart&amp;code={$code}" method="post" ajax="true" ajax-loader-id="cart_list_{$code}">
						<input type="hidden" value="{$name}" name="vendor_code"/>
						<input type="hidden" value="{if(f:num(@qant) != 0) then 0 else 1}" name="not_available"/>
						<input type="hidden" value="promelec" name="aux"/>

						<input type="hidden" value="{$name}" name="name"/>
						<input type="hidden" value="шт" name="unit"/>
						<input type="hidden" value="{@moq}" name="upack"/>

						<input type="hidden" value="{@quant}" name="max"/>
						<input type="number" class="text-input" name="qty" value="{@moq}" min="{@moq}"/>

						<xsl:if test="f:num(@quant) != 0">
							<input type="hidden" name="price_map" value="{$map}"/>
						</xsl:if>
						<input type="hidden" name="img" value="{$pic}"/>
						<input type="submit" class="button" value="В корзину"/>
					</form>
				</div>
			</div>
			<xsl:if test="f:num(@quant) != 0">
				<xsl:variable name="days" select="concat(f:num(@delivery)+7 ,'-', f:num(@delivery)+14)"/>
				<div class="device__in-stock"><i class="fas fa-check"></i>поставка<xsl:value-of select="if(f:num(@quant) &lt; 500000) then concat(' ',f:num(@quant), ' шт.') else ''" /> в течение <xsl:value-of select="$days"/> дней</div>
			</xsl:if>
			<xsl:if test="f:num(@quant) = 0">
				<div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i>под заказ</div>
			</xsl:if>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>