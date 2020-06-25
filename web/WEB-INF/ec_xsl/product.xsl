<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="header" select="$p/name"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $header"/>
	<xsl:variable name="title" select="$h1"/>
	<xsl:variable name="active_menu_item" select="'catalog'"/>
	<xsl:variable name="default_keywords" select="string-join($p/tag, ', ')"/>


	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>


	<xsl:variable name="p" select="page/product"/>
	<xsl:variable name="has_lines" select="$p/has_lines = '1'"/>

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
				<a href="{$main_host}">Главная страница</a> &gt; <a href="{page/catalog_link}">Каталог</a>
				<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id]">
					<xsl:text disable-output-escaping="yes"> &gt; </xsl:text>
					<a href="{show_products}"><xsl:value-of select="name"/></a>
				</xsl:for-each>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title"><xsl:value-of select="$h1"/></h1>
		<p>
			<xsl:if test="$p/mizida_code != ''">
				арт. <xsl:value-of select="$p/mizida_code"/>
			</xsl:if>
		</p>
		<div class="catalog-item-container">
			<div class="gallery">
				<div class="fotorama" data-nav="thumbs" data-thumbheight="80" data-thumbwidth="80" data-allowfullscreen="native" data-width="100%">
					<xsl:for-each select="$p/gallery">
						<img src="{$p/@path}{.}" alt="{$p/name}"/>
					</xsl:for-each>
					<xsl:if test="not($p/gallery)">
						<img src="{concat($p/@path, $p/main_pic)}" alt="{$p/name}"/>
					</xsl:if>
				</div>

				<div class="extra-contacts">
					<xsl:value-of select="$common/product_side/text" disable-output-escaping="yes"/>
				</div>

				<script>
				$('.fotorama')
					.on('fotorama:fullscreenenter fotorama:fullscreenexit', function (e, fotorama) {
					if (e.type === 'fotorama:fullscreenenter') {
						// Options for the fullscreen
						fotorama.setOptions({
							fit: 'scaledown'
						});
					} else {
						// Back to normal settings
						fotorama.setOptions({
							fit: 'contain'
						});
					}
					})
					.fotorama();
				</script>
			</div>
			<div class="product-info">
				<!-- new html -->
				<!-- <xsl:for-each select="$p/tag">
					<div class="device__tag device__tag_device-page"><xsl:value-of select="." /></div>
				</xsl:for-each> -->

				<xsl:variable name="has_price" select="$p/price and $p/price != '0'"/>

				<xsl:if test="not($has_lines)">
					<div class="device-page__actions">
						<xsl:if test="$has_price">
							<div class="device__price device__price_device-page">
								<div class="price_normal">
									<xsl:value-of select="if ($p/price) then $p/price else '0'"/> руб.
									<xsl:if test="$p/unit != ''">/<xsl:value-of select="$p/unit"/></xsl:if>
								</div>
								<xsl:if test="$p/price_old">
									<div class="price_old">
										<span>
											<xsl:value-of select="$p/price_old"/> руб.
											<xsl:if test="$p/unit != ''">/<xsl:value-of select="$p/unit"/></xsl:if>
										</span>
									</div>
								</xsl:if>
							</div>
						</xsl:if>
						<div id="cart_list_{$p/@id}" class="device__order device__order_device-page product_purchase_container">
							<form action="{$p/to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{$p/@id}">
								<input type="hidden" name="code" value="{$p/code}"/>
								<xsl:if test="$has_price and f:num($p/qty) &gt; 0">
									<input type="number" class="text-input" name="qty" value="1" min="0" />
									<input type="submit" class="button" value="Заказать" />
								</xsl:if>
								<xsl:if test="not($has_price and f:num($p/qty) &gt; 0)">
									<input type="number" class="text-input" name="qty" value="1" min="0" />
									<input type="submit" class="button" value="Предзаказ" />
								</xsl:if>
							</form>
						</div>
						<div class="device__actions device__actions_device-page">
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
						<xsl:choose>
							<xsl:when test="$p/qty and $p/qty != '0'"><div class="device__in-stock"><i class="fas fa-check"></i> в наличии <span style="color:#8c8297; padding-left: .55rem;">Обновлено: <xsl:value-of select="f:utc_millis_to_bel_date(page/catalog[1]/date/@millis)"/></span></div></xsl:when>
							<xsl:otherwise><div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i> под заказ</div></xsl:otherwise>
						</xsl:choose>
					</div>
				</xsl:if>

				<xsl:if test="$has_lines">
					
					<xsl:variable name="param_names" select="distinct-values($p/line_product/params/param/@name)"/>
					<xsl:variable name="param_captions" select="distinct-values($p/line_product/params/param/@caption)"/>

					<xsl:variable name="cols" select="count($param_names) + 4"/>

					<div class="multi-device" style="grid-template-columns: repeat({$cols}, 1fr);">
						<div style="padding-left: 0;">Вариант</div>
						<xsl:for-each select="$param_captions">
							<div style="color: #a2a2a2; font-size: 13px;"><xsl:value-of select="."/></div>
						</xsl:for-each>
						<div>Цена</div>
						<div>Наличие</div>
						<div></div>

						<xsl:for-each select="$p/line_product">
							<xsl:variable name="lp" select="current()"/>
							<xsl:variable name="code" select="$lp/code"/>
							<xsl:variable name="has_price" select="price and price != '0'"/>
							<div class="multi-device__name"><xsl:value-of select="$lp/name" /></div>
							<xsl:for-each select="$param_names">
								<div class="multi-device__name">
									<xsl:value-of select="$lp/params/param[@name = current()]" />
									
								</div>
							</xsl:for-each>
							<div class="multi-device__price">
								<xsl:if test="$has_price">
									<xsl:if test="price_old">
										<div class="multi-device__price_old">
											<xsl:value-of select="price_old"/> руб.
											<xsl:if test="unit != ''">/<xsl:value-of select="unit"/></xsl:if>
										</div>
									</xsl:if>
									<div class="multi-device__price_new">
										<xsl:value-of select="if (price) then price else '0'"/> руб.
										<xsl:if test="unit != ''">/<xsl:value-of select="unit"/></xsl:if>
									</div>
								</xsl:if>
								<xsl:if test="not($has_price)">
									<div class="multi-device__price_new">по запросу</div>
								</xsl:if>
							</div>
							<div class="multi-device__price">
								<xsl:value-of select="if (f:num($p/qty) &gt; 0) then 'на складе' else 'под заказ'"/>
							</div>
							<div class="multi-device__actions" id="cart_list_{@id}">
								<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
									<input type="hidden" name="code" value="{$code}"/>
									<xsl:if test="$has_price">
										<input type="number" class="text-input" name="qty" value="1" min="0" />
										<input type="submit" class="button" value="Заказать" />
									</xsl:if>
									<xsl:if test="not($has_price)">
										<input type="number" class="text-input" name="qty" value="1" min="0" />
										<input type="submit" class="button" value="Предзаказ" />
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

				<div class="device-benefits">
					<div class="device-benefits__item">
						<i class="fas fa-shield-alt device-benefits__icon"></i>
						<div class="device-benefits__label">Надежность</div>
					</div>
					<div class="device-benefits__item">
						<i class="fas fa-trophy device-benefits__icon"></i>
						<div class="device-benefits__label">Гарантии</div>
					</div>
					<div class="device-benefits__item">
						<i class="far fa-thumbs-up device-benefits__icon"></i>
						<div class="device-benefits__label">Цены</div>
					</div>
				</div>
				
				<div class="extra-contacts">
					<div class="extra-contacts__title">Телефоны для связи</div>
					<div class="extra-contacts__items">
						<div class="extra-contacts__item">
							<div class="extra-contacts__number">(+375 17) 357-92-85</div>
							<div class="extra-contacts__text">ул. Притыцкого, 42</div>
						</div>
						<div class="extra-contacts__item">
							<div class="extra-contacts__number">(+375 162) 337-93-90</div>
							<div class="extra-contacts__text">ул. Я.Коласа, 30</div>
						</div>
						<div class="extra-contacts__item">
							<div class="extra-contacts__number">(+375 17) 354-47-84</div>
							<div class="extra-contacts__text">ул.Толбухина,12-А</div>
						</div>
					</div>
				</div>
				<div class="extra-info">
					<xsl:value-of select="$p/description" disable-output-escaping="yes"/>
				</div>
			</div>
			<div class="description">

					<ul class="nav nav-tabs" role="tablist">
						<!-- <xsl:if test="$p/params != ''"> -->
								<li role="presentation" class="{'active'[not($p/product_extra)]}">
									<a href="#tab1" role="tab" data-toggle="tab">Описание</a>
								</li>
							<!-- </xsl:if> -->
						<!--<xsl:if test="string-length($p/text) &gt; 15">-->
							<xsl:for-each select="$p/product_extra[. != '']">
								
								<xsl:variable name="first" select="position()=1" />
								<li role="presentation" class="{'active'[$first]}">
									<a href="#tab{@id}" role="tab" data-toggle="tab">
										<xsl:value-of select="current()/name"/>
									</a> 
								</li>
							</xsl:for-each>
							
					</ul>
				<div class="tab-content">
					<!-- <xsl:if test="$p/params != ''"> -->
						<div role="tabpanel" class="tab-pane {'active'[not($p/product_extra)]}" id="tab1">
							<xsl:value-of select="$p/text" disable-output-escaping="yes"/>
							<table>
								<colgroup>
									<col style="width: 40%"/>
								</colgroup>
								<xsl:for-each select="$p/params/param">
									<tr>
										<td>
											<p><strong><xsl:value-of select="@caption"/></strong></p>
										</td>
										<td>
											<p><xsl:value-of select="."/><xsl:text> </xsl:text><xsl:value-of select="@description"/></p>
										</td>
									</tr>
								</xsl:for-each>
							</table>

						</div>
					<!-- </xsl:if> -->
					<xsl:for-each select="$p/product_extra[. != '']">
						<xsl:variable name="first" select="position()=1" />
						<div role="tabpanel" class="tab-pane {'active'[$first]}" id="tab{@id}">
							<xsl:value-of select="text" disable-output-escaping="yes"/>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</div>
		<xsl:if test="page/assoc">
			<h3>Вас также может заинтересовать</h3>
			<div class="catalog-items">
				<xsl:apply-templates select="page/assoc"/>
			</div>
		</xsl:if>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>



</xsl:stylesheet>
