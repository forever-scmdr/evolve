<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="$p/name"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'catalog'"/>


	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>


	<xsl:variable name="p" select="page/product"/>
	<xsl:variable name="has_lines" select="$p/has_lines = '1'"/>
	<xsl:variable name="p_big" select="if (index-of($p/description, 'img src') &gt; -1 or string-length($p/description) &gt; 500) then $p/description else ''"/>
	<xsl:variable name="is_big" select="$p_big and not($p_big = '')"/>

	<xsl:template name="MARKUP">
		<xsl:variable name="price" select="$p/price"/>
		<script type="application/ld+json">
			<xsl:variable name="quote">"</xsl:variable>
			{
			"@context": "http://schema.org/",
			"@type": "Product",
			"name": <xsl:value-of select="concat($quote, replace($p/name, $quote, ''), $quote)" />,
			"image": <xsl:value-of select="concat($quote, $main_host, '/', $p/@path, $p/gallery[1], $quote)" />,
			"brand": <xsl:value-of select="concat($quote, $p/tag[1], $quote)" />,
			"offers": {
			"@type": "Offer",
			"priceCurrency": "BYN",
			<xsl:if test="f:num($price) &gt; 0">"price": <xsl:value-of select="concat($quote,f:currency_decimal($price), $quote)" /></xsl:if>
			<xsl:if test="f:num($price) = 0">"price":"15000.00"</xsl:if>
			}, "aggregateRating": {
			"@type": "AggregateRating",
			"ratingValue": "4.9",
			"ratingCount": "53",
			"bestRating": "5",
			"worstRating": "1",
			"name": <xsl:value-of select="concat($quote, translate($p/name, $quote, ''), $quote)" />
			}
			}
		</script>
	</xsl:template>

	<xsl:template name="CONTENT">





		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="{$main_host}">Главная страница</a> <i class="fas fa-angle-right"></i> <a href="{page/catalog_link}">Каталог</a>
				<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id]">
					<i class="fas fa-angle-right"></i>
					<a href="{show_products}"><xsl:value-of select="name"/></a>
				</xsl:for-each>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title"><xsl:value-of select="$h1"/></h1>
		<p class="subtitle">арт. <xsl:value-of select="$p/code"/></p>
		<div class="catalog-item-container">
			<div class="gallery">
				<div class="fotorama" data-nav="thumbs" data-thumbheight="40" data-thumbwidth="40" data-allowfullscreen="native">
					<xsl:if test="not($p/pic_ref)">
						<img src="{$p/@path}{$p/main_pic}" alt="{$p/name}"/>
						<xsl:for-each select="$p/gallery">
							<img src="{$p/@path}{.}" alt="{$p/name}"/>
						</xsl:for-each>
					</xsl:if>
					<xsl:if test="$p/pic_ref != ''">
						<img src="{$p/pic_ref[1]}" alt="{$p/name}"/>
						<xsl:for-each select="$p/pic_ref[position() &gt; 1 and not(starts-with(., 'device_pics/small_'))]">
							<img src="{.}" alt="{$p/name}"/>
						</xsl:for-each>
					</xsl:if>
				</div>
			</div>
			<div class="product-info">
				<xsl:variable name="has_price" select="if ($is_reg_jur) then ($p/price_opt and $p/price_opt != '0') else ($p/price and $p/price != '0')"/>
				<xsl:variable name="price" select="if ($is_reg_jur and $has_price) then f:number_decimal(f:num($p/price_opt) div 100 * (100 - $discount)) else $p/price"/>
				<xsl:variable name="price_old" select="if ($is_reg_jur) then $p/price_opt_old else $p/price_old"/>
				<xsl:variable name="discount_percent" select="f:discount($price, $price_old)"/><xsl:variable name="discount_percent" select="f:discount($price, $price_old)"/>
                <xsl:variable name="qty" select="if ($is_reg_jur) then $p/qty_opt else $p/qty"/>
                <xsl:variable name="available_qty" select="if ($qty and f:num($qty) &gt; 0) then f:num($qty) else 0"/>
				<xsl:variable name="max" select="if ($available_qty &gt; 0) then $available_qty else 1000000"/>
				<!-- new html -->
				<xsl:for-each select="$p/tag">
					<div class="device__tag device__tag_device-page"><xsl:value-of select="." /></div>
				</xsl:for-each>
				<!-- UPDATE 10.06/2019 discount label -->
				<xsl:if test="$discount_percent != ''">
					<div class="device__tag device__tag_device-page device__tag_discount">Скидка: <xsl:value-of select="$discount_percent" />%</div>
				</xsl:if>
				<!-- END_UPDATE 10.06/2019 discount label -->



				<xsl:if test="not($has_lines)">
					<div class="device-page__actions">
						<xsl:if test="$has_price">
							<div class="device__price device__price_device-page">
								<xsl:if test="$price_old"><div class="price_old"><span><xsl:value-of select="$price_old"/> руб.</span></div></xsl:if>
								<div class="price_normal"><xsl:value-of select="if ($price) then $price else '0'"/> руб.</div>


							</div>
						</xsl:if>
						<div id="cart_list_{$p/@id}" class="device__order device__order_device-page product_purchase_container">
							<form action="{$p/to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{$p/@id}">
								<xsl:if test="$has_price">
									<input type="number" class="text-input" name="qty" value="1" min="0" max="{$max}"/>
									<input type="submit" class="button" value="Заказать" />
								</xsl:if>
								<xsl:if test="not($has_price)">
									<input type="number" class="text-input" name="qty" value="1" min="0" max="{$max}"/>
									<input type="submit" class="button" value="Запросить цену" />
								</xsl:if>
							</form>
						</div>
						<!-- <div class="device__actions device__actions_device-page">
							<div id="compare_list_{$p/@id}">
								<a href="{$p/to_compare}" class="device__action-link icon-link" ajax="true" ajax-loader-id="compare_list_{$p/@id}">
									<i class="fas fa-balance-scale"></i>сравнить
								</a>
							</div>
							<div id="fav_list_{$p/@id}">
								<a href="{$p/to_fav}" class="device__action-link icon-link" ajax="true" ajax-loader-id="fav_list_{$p/@id}">
									<i class="fas fa-star"></i>отложить
								</a>
							</div>
						</div> -->
						<xsl:choose>
							<xsl:when test="$available_qty &gt; 10">
								<div class="device__in-stock"><i class="fas fa-signal"></i> есть на складе</div>
							</xsl:when>
							<xsl:when test="$available_qty &gt; 0">
								<div class="device__in-stock device__in-stock_maybe"><i class="fas fa-signal"></i><xsl:text>на складе: </xsl:text><xsl:value-of select="$qty"/> шт.</div>
							</xsl:when>
							<xsl:otherwise>
								<div class="device__in-stock device__in-stock_no"><i class="fas fa-truck"></i> Поставка: <xsl:value-of select="substring(//catalog/ship_date, 1,10)"/></div>
							</xsl:otherwise>
						</xsl:choose>
					</div>
				</xsl:if>

				<xsl:if test="$has_lines">
					<xsl:variable name="param_names" select="distinct-values($p/line_product/params/param/@name)"/>
					<xsl:variable name="param_captions" select="distinct-values($p/line_product/params/param/@caption)"/>
					<xsl:variable name="col_qty" select="count($param_names) + 4"/>

					<div class="multi-device" style="grid-template-columns: repeat({$col_qty}, auto);">
						<div style="padding-left: 0;">Название</div>
						<div>Артикул</div>
						<xsl:for-each select="$param_captions">
							<div><xsl:value-of select="." /></div>
						</xsl:for-each>
						<div>Цена</div>
						<div></div>

						<xsl:for-each select="$p/line_product">
							<xsl:variable name="lp" select="."/>

							<xsl:variable name="has_price" select="if($is_reg_jur) then f:num(price_opt) != 0 else f:num(price) != 0"/>
							<xsl:variable name="price" select="if($is_reg_jur) then price_opt else price"/>
							<xsl:variable name="price_old" select="if($is_reg_jur) then price_opt_old else price_old"/>
							<xsl:variable name="discount_percent" select="f:discount($price, $price_old)"/>
                            <xsl:variable name="qty" select="if ($is_reg_jur) then $p/qty_opt else $p/qty"/>
                            <xsl:variable name="available_qty" select="if ($qty and f:num($qty) &gt; 0) then f:num($qty) else 0"/>
							<xsl:variable name="max" select="if ($available_qty &gt; 0) then $available_qty else 1000000"/>

							<div class="multi-device__name"><xsl:value-of select="name" /></div>
							<div class="multi-device__name"><xsl:value-of select="vendor_code" /></div>
							<xsl:for-each select="$param_names">
								<div><xsl:value-of select="$lp/params/param[@name = current()]" /></div>
							</xsl:for-each>
							<div class="multi-device__price">
								<xsl:if test="$has_price">
									<xsl:if test="$price_old"><div class="multi-device__price_old"><xsl:value-of select="$price_old"/> руб.</div></xsl:if>
									<div class="multi-device__price_new"><xsl:value-of select="if ($price) then $price else '0'"/></div>
<!-- UPDATE 10.06/2019 discount label -->
									<xsl:if test="$discount_percent != ''">
										<span class="discount" style="color: red; font-weight: bold;">Скидка: <xsl:value-of select="$discount_percent" />%</span>
									</xsl:if>
<!-- END_UPDATE 10.06/2019 discount label -->
								</xsl:if>
								<xsl:if test="not($has_price)">
									<div class="multi-device__price_new">по запросу</div>
								</xsl:if>
							</div>
							<div class="multi-device__actions" id="cart_list_{@id}">
								<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
									<xsl:if test="$has_price">
										<input type="number" class="text-input" name="qty" value="1" min="0" max="{$max}"/>
										<input type="submit" class="button" value="Заказать" />
									</xsl:if>
									<xsl:if test="not($has_price)">
										<input type="number" class="text-input" name="qty" value="1" min="0" max="{$max}"/>
										<input type="submit" class="button" value="Запросить цену" />
									</xsl:if>
								</form>
							</div>
						</xsl:for-each>

					</div>
					<div class="multi-device__links">
						<div id="compare_list_{$p/@id}">
							<a href="{$p/to_compare}" class="device__action-link icon-link" ajax="true" ajax-loader-id="compare_list_{$p/@id}">
								<i class="fas fa-balance-scale"></i>сравнить
							</a>
						</div>
						<div id="fav_list_{$p/@id}">
							<a href="{$p/to_fav}" class="device__action-link icon-link" ajax="true" ajax-loader-id="fav_list_{$p/@id}">
								<i class="fas fa-star"></i>отложить
							</a>
						</div>
					</div>
				</xsl:if>
				<xsl:value-of select="page/common/catalog_texts/payment" disable-output-escaping="yes"/>
				<div class="extra-info">
					<xsl:if test="not($is_big)">
						<xsl:value-of select="$p/description" disable-output-escaping="yes"/>
					</xsl:if>
				</div>
			</div>
			<div class="description">

					<ul class="nav nav-tabs" role="tablist">
						<!--<xsl:if test="string-length($p/text) &gt; 15">-->
						<xsl:if test="$p/params">
						<li role="presentation" class="active">
							<a href="#tab1" role="tab" data-toggle="tab">Характеристики</a>
						</li>
						</xsl:if>
						<xsl:if test="$is_big">
							<li role="presentation" class="{'active'[not($p/params)]}">
								<a href="#tab2" role="tab" data-toggle="tab">Описание</a>
							</li>
						</xsl:if>
						<xsl:for-each select="$p/product_extra">
							<li role="presentation">
								<a href="#tab{@id}" role="tab" data-toggle="tab"><xsl:value-of select="name"/></a>
							</li>
						</xsl:for-each>
					</ul>
				<div class="tab-content">
					<xsl:if test="$p/params">
						<div role="tabpanel" class="tab-pane active" id="tab1">
							<!--<xsl:value-of select="$p/text" disable-output-escaping="yes"/>-->
							<table class="parameters">
								<colgroup>
									<col style="width: 40%"/>
								</colgroup>
								<xsl:for-each select="$p/params/param">
									<tr>
										<td>
											<p><xsl:value-of select="@caption"/></p>
										</td>
										<td>
											<p><xsl:value-of select="."/><xsl:text> </xsl:text><xsl:value-of select="@description"/></p>
										</td>
									</tr>
								</xsl:for-each>
							</table>

						</div>
					</xsl:if>
					<xsl:if test="$is_big">
						<div role="tabpanel" class="tab-pane {'active'[not($p/params)]}" id="tab2">
							<xsl:value-of select="$p_big" disable-output-escaping="yes"/>
						</div>
					</xsl:if>
					<xsl:for-each select="$p/product_extra">
						<div role="tabpanel" class="tab-pane" id="tab{@id}">
							<h4><xsl:value-of select="name"/></h4>
							<div class="catalog-items">
								<xsl:value-of select="text" disable-output-escaping="yes"/>
							</div>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</div>
		<xsl:if test="page/analog">
			<h3>Аналоги</h3>
			<div class="catalog-items">
				<xsl:apply-templates select="page/analog"/>
			</div>
		</xsl:if>
		<xsl:if test="page/similar">
			<h3>Похожие товары</h3>
			<div class="catalog-items">
				<xsl:apply-templates select="page/similar"/>
			</div>
		</xsl:if>
		<xsl:if test="page/support">
			<h3>Сопутствующие товары</h3>
			<div class="catalog-items">
				<xsl:apply-templates select="page/support"/>
			</div>
		</xsl:if>
		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>



</xsl:stylesheet>
