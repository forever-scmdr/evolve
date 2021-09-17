<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="../utils/price_conversions.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="view" select="page/variables/view"/>
	<xsl:variable name="shop" select="page/shop"/>
	<xsl:variable name="result" select="page/search/result"/>
	<xsl:variable name="found" select="if(page/variables/minqty = '0') then $result/product[f:num(qty) &gt; 0] else $result/product"/>

	<xsl:template match="/">
		<div>
			<xsl:if test="$found">
				<div id="digikey_search" class="result">
					<h2>Результат поиска по Digikey</h2>			
					
						<div class="catalog-items{' lines'[$view = 'list']}">
							<xsl:if test="$view = 'list'">
								<xsl:apply-templates select="$found" mode="lines"/>
							</xsl:if>
							<xsl:if test="not($view = 'list')">
								<xsl:apply-templates select="$found"/>
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
						</script>
					
				</div>
			</xsl:if>
			<xsl:if test="not($found)">
				<div id="extra_search_2" class="result">
					<h2>Результат поиска по Digikey</h2>
					<p>Товары не найдены</p>
				</div>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template name="CART_BUTTON">
		<xsl:param name="product" />

		<xsl:variable name="id" select="$product/code"/>

		<div id="cart_list_{code}">
			<form action="cart_action/?action=addExternalToCart" method="post" ajax="true" ajax-loader-id="cart_list_{code}">
				<input type="hidden" name="id" value="{$id}"/>
				<input type="hidden" name="aux" value="{$shop/name}"/>
				<input type="hidden" name="code" value="{$id}"/>
				<input type="hidden" name="name" value="{$product/name}"/>
				<input type="hidden" value="{$product/producer_code}" name="vendor_code"/>
				<input type="hidden" value="{$product/producer}" name="vendor"/>
				<textarea style="display:none;" name="description">
					<xsl:value-of select="description"/>
				</textarea>
				<input type="hidden" value="{if(f:num($product/qty) &gt; 0) then 0 else 1}" name="not_available"/>
				<input type="hidden" value="{$product/qty}" name="max"/>
				<input type="number" class="text-input" name="qty" value="{if(min_qty != '') then min_qty else 1}" min="{if(min_qty != '') then min_qty else 1}"/>
				<input type="hidden" name="price_map" value="{spec_price}"/>
				<input type="hidden" name="img" value="{main_pic}"/>
				<input type="hidden" name="delivery_time" value="{if(f:num($product/qty)&gt; 0) then $shop/delivery_string else ''}"/>
				<input type="submit" class="button{if(f:num($product/qty) &gt; 0) then '' else ' not_available'}" value="{if(f:num($product/qty) &gt; 0) then 'В корзину' else 'Под заказ'}"/>
			</form>
		</div>
	</xsl:template>

	<xsl:template match="product">

		<xsl:variable name="price" select="f:price_output(price, $shop)"/>
		<xsl:variable name="price_pack" select="f:price_output(f:num(price)*f:num(min_qty), $shop)"/>

		<div class="device items-catalog__device">
			<xsl:if test="main_pic != ''">
				<a href="{main_pic}" class="magnific_popup-image zoom-icon" title="{name}" rel="nofollow">
					<i class="fas fa-search-plus"></i>
				</a>
				<a class="device__image" style="background-image: url('{main_pic}');"></a>
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
			<div class="device__article-number">
				Артикул производителя:<br/>
				<xsl:value-of select="producer_code"/>
			</div>
			<xsl:if test="doc_ref">
				<div class="device__article-number">
					<a href="{doc_ref}" target="_blank">Документация PDF</a>
				</div>
			</xsl:if>
			<div class="device__price">
				<div class="price_normal">
					<xsl:value-of select="concat($price, ' ', upper-case($curr), '/', 'шт')"/>
				</div>
				
				<xsl:variable name="x">
					<xsl:for-each select="spec_price_map">
						<xsl:value-of select="concat(@qty, '+ ', '&lt;strong&gt;', f:price_output(@price, $shop), upper-case($curr), '&lt;/strong&gt;', '&lt;br/&gt;')"/>
					</xsl:for-each>
				</xsl:variable>
				<a data-container="body"  data-html="true" data-toggle="popover" data-placement="top" data-content="{$x}">Цена зависит от количества</a>
			</div>
			<div class="nds" style="margin-top: -8px; margin-bottom: 10px;">*цена включает НДС</div>
			<div class="device__order">
				<xsl:call-template name="CART_BUTTON">
					<xsl:with-param name="product" select="current()" />
				</xsl:call-template>
			</div>
			<xsl:if test="f:num(qty) != 0">
				<div class="device__in-stock"><i class="fas fa-check"></i>поставка<xsl:value-of select="if(f:num(qty) &lt; 500000) then concat(' ',f:num(qty), ' шт.') else ''" /> в течение <xsl:value-of select="$shop/delivery_string"/></div>
			</xsl:if>
			<xsl:if test="f:num(qty) = 0">
				<div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i>под заказ</div>
			</xsl:if>
		</div>

	</xsl:template>
	<xsl:template match="product" mode="lines">
		<xsl:variable name="price" select="f:price_output(price, $shop)"/>
		<xsl:variable name="price_pack" select="f:price_output(f:num(price)*f:num(min_qty), $shop)"/>

		<div class="device device_row" >
			<a class="device__image device_row__image"
			   style="background-image: url('{if(main_pic != '') then main_pic else 'img/no_image.png'}');">&nbsp;
			</a>
			<div class="device__info">
				<a class="device__title">
					<xsl:value-of select="name"/>
				</a>
				<div class="device__description">

					<p class="basics">
						<span><b>Код:</b>&#160;<xsl:value-of select="code"/></span><br/>
						<span><b>Ариткул производителя:</b>&#160;<xsl:value-of select="producer_code"/></span><br/>
						<span><b>Производитель:</b>&#160;<xsl:value-of select="producer" /></span>
						<xsl:if test="description != ''">
							<br/><span><b>Описание:</b>&#160;<xsl:value-of select="description" /></span>
						</xsl:if>
						<xsl:if test="f:num(min_qty) &gt; 1">
							<br/><span><b>Минимальный заказ:</b>&#160;<xsl:value-of select="min_qty"/></span>
						<br/><span><b>Цена за мин. заказ:</b>&#160;<xsl:value-of select="$price_pack"/></span>
						</xsl:if>
					</p>

					<xsl:if test="doc_ref">
						<p>
							<a href="{doc_ref}" target="_blank">Документация PDF</a>
						</p>
					</xsl:if>

					<a style="color: #707070; text-decoration: underline;" class="javascript" onclick="$('#tech-{@id}').toggle();">Показать технические характеристики</a>
					<table class="features table-bordered" id="tech-{@id}" style="display:none;">
					<xsl:for-each select="parameter">
						<tr>
							<td style="color: #616161; "><xsl:value-of select="@name"/></td>
							<td><xsl:value-of select="."/></td>
						</tr>
					</xsl:for-each>
					</table>
				</div>
			</div>
			<div class="device__article-number"><xsl:value-of select="code"/></div>
			<div class="device__actions device_row__actions">

			</div>
			<div class="device__price device_row__price">
				<div class="price_normal">
					<xsl:value-of select="concat($price, ' ', upper-case($curr), '/', 'шт')"/>
				</div>
				<div class="nds">*цена c НДС</div>
				<xsl:variable name="x">
					<xsl:for-each select="spec_price_map">
						<xsl:value-of select="concat(f:price_output(@price, $shop), ' от ', upper-case($curr), ' ', @qty, '&lt;br/&gt;')"/>
					</xsl:for-each>
				</xsl:variable>
				<div class="manyPrice">
					<xsl:for-each select="spec_price_map">
						<div class="manyPrice__item">
							<div class="manyPrice__qty"><xsl:value-of select="@qty" />+</div>
							<div class="manyPrice__price"><xsl:value-of select="concat(f:price_output(@price, $shop), upper-case($curr))" /></div>
						</div>
					</xsl:for-each>
				</div>
			</div>
			<div class="device__order device_row__order">
				<xsl:call-template name="CART_BUTTON">
					<xsl:with-param name="product" select="." />
				</xsl:call-template>
				<xsl:if test="f:num(qty) != 0">
					<div class="device__in-stock device_row__in-stock" style="max-width: 140px;">
						<i class="fas fa-check" />поставка<xsl:value-of select="if(f:num(qty) &lt; 500000) then concat(' ',f:num(qty), ' шт.') else ''" /> в течение <xsl:value-of select="$shop/delivery_string"/>
					</div>
				</xsl:if>
				<xsl:if test="f:num(qty) = 0">
					<div class="device__in-stock device_row__in-stock device__in-stock_no">
						<i class="far fa-clock"/>нет в наличии
					</div>
				</xsl:if>
			</div>
		</div>
	</xsl:template>

</xsl:stylesheet>