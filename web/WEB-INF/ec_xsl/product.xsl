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
			<xsl:if test="f:num($price) &gt; 0">"price": <xsl:value-of select="concat($quote,f:pack($price, $p/pack_db, $p/pack), $quote)" /></xsl:if>
			<xsl:if test="f:num($price) = 0">"price":"15.00"</xsl:if>
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
		<!-- <p>арт. <xsl:value-of select="$p/code"/></p> -->
		<div class="catalog-item-container">
			<div class="gallery">
				<div class="fotorama" data-nav="thumbs" data-thumbheight="40" data-thumbwidth="40" data-allowfullscreen="native">
					<xsl:for-each select="$p/gallery">
						<img src="{$p/@path}{.}" alt="{$p/name}"/>
					</xsl:for-each>
					<xsl:if test="not($p/gallery)">
						<img src="{concat($p/@path, $p/main_pic)}" alt="{$p/name}"/>
					</xsl:if>
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
				<xsl:for-each select="$p/tag">
					<div class="device__tag device__tag_device-page"><xsl:value-of select="." /></div>
				</xsl:for-each>

				<xsl:variable name="has_price" select="$p/price and $p/price != '0'"/>

				<xsl:if test="not($has_lines)">
					<div class="device-page__actions">
						<xsl:if test="$has_price">
							<div class="device__price device__price_device-page">
								<xsl:if test="$p/price_old"><div class="price_old"><span><xsl:value-of select="f:pack($p/price_old, $p/pack_db, $p/pack)"/> руб.</span></div></xsl:if>
								<div class="price_normal"><xsl:value-of select="f:pack($p/price, $p/pack_db, $p/pack)"/> руб./<xsl:value-of select="f:unit($p/unit, $p/pack)"/></div>
							</div>
						</xsl:if>
						<div id="cart_list_{$p/@id}" class="device__order device__order_device-page product_purchase_container">
							<form action="{$p/to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{$p/@id}">
								<xsl:if test="$has_price">
									<input type="number" class="text-input" name="qty" value="1" max="{f:qty($p/qty, $p/pack_db, $p/pack)}" min="0" />
									<input type="submit" class="button" value="Заказать" />
								</xsl:if>
								<xsl:if test="not($has_price)">
									<input type="number" class="text-input" name="qty" value="1" min="0" />
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
							<!--<xsl:when test="$p/qty and $p/qty != '0'"><div class="device__in-stock"><i class="fas fa-check"></i> в наличии</div></xsl:when>-->
							<!--<xsl:otherwise><div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i> под заказ</div></xsl:otherwise>-->
							<xsl:when test="$p/available = '1'"><div class="device__in-stock"><i class="fas fa-check"></i> в наличии</div></xsl:when>
							<xsl:otherwise><div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i> под заказ</div></xsl:otherwise>
						</xsl:choose>
					</div>
				</xsl:if>

				<xsl:if test="$has_lines">
					<xsl:variable name="param_names" select="distinct-values($p/line_product/params/param/@name)"/>
					<xsl:variable name="param_captions" select="distinct-values($p/line_product/params/param/@caption)"/>
					<!-- <xsl:variable name="col_qty" select="count($param_names) + 5"/> -->
					<xsl:variable name="col_qty" select="5"/>
					<div style="height: 340px; overflow-y: scroll; margin-bottom: 32px; padding-right: 16px;">
					<div class="multi-device" style="grid-template-columns: repeat({$col_qty}, auto);">
							<!-- <div>Артикул</div> -->
							<div>Название</div>
						<!-- <xsl:for-each select="$param_captions">
							<div><xsl:value-of select="." /></div>
						</xsl:for-each> -->
							<div>Цена с НДС</div>
							<div>Ед. изм.</div>
							<div>Доступность</div>
							<div></div>

						<xsl:for-each select="$p/line_product">
							<xsl:variable name="lp" select="."/>
							<xsl:variable name="has_price" select="price and price != '0'"/>
								<div><xsl:value-of select="name" /></div>
								<!-- <div><xsl:value-of select="vendor_code" /></div> -->
							<!-- <xsl:for-each select="$param_names">
								<div><xsl:value-of select="$lp/params/param[@name = current()]" /></div>
							</xsl:for-each> -->
							<div class="multi-device__price">
								<xsl:if test="$has_price">
									<xsl:if test="price_old"><div class="multi-device__price_old"><xsl:value-of select="f:pack(price_old, pack_db, pack)"/> руб.</div></xsl:if>
									<div class="multi-device__price_new"><xsl:value-of select="if (price) then f:pack(price, pack_db, pack) else '0'"/></div>
								</xsl:if>
								<xsl:if test="not($has_price)">
									<div class="multi-device__price_new">по запросу</div>
								</xsl:if>
							</div>
							<div class="kk"><xsl:value-of select="if(unit != '') then unit else 'шт'" /></div>
							<div class="multi-device__we-have">
								<xsl:if test="available = '1'">
									<div class="device__in-stock"><i class="fas fa-check"></i> в наличии</div>
								</xsl:if>
								<xsl:if test="not(available = '1')">
									<div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i> под заказ</div>
								</xsl:if>
							</div>

								<div class="multi-device__actions" id="cart_list_{@id}">
									<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
										<xsl:if test="$has_price">
											<input type="number" class="text-input" name="qty" value="1" max="{f:qty(qty, pack_db, pack)}" min="0" />
											<input type="submit" class="button" value="Заказать" />
										</xsl:if>
										<xsl:if test="not($has_price)">
											<input type="number" class="text-input" name="qty" value="1" min="0" />
											<input type="submit" class="button" value="Запросить цену" />
										</xsl:if>
									</form>
								</div>
						</xsl:for-each>
					</div>
					</div>

					<!-- <div class="multi-device__links">
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
				</xsl:if>


				<!-- <div class="device-benefits">
					<div class="device-benefits__item">
						<i class="fas fa-shield-alt device-benefits__icon"></i>
						<div class="device-benefits__label">Официальная гарантия и сервис</div>
					</div>
					<div class="device-benefits__item">
						<i class="fas fa-trophy device-benefits__icon"></i>
						<div class="device-benefits__label">Официальные поставки</div>
					</div>
					<div class="device-benefits__item">
						<i class="far fa-thumbs-up device-benefits__icon"></i>
						<div class="device-benefits__label">Обучение и сопровождение</div>
					</div>
				</div> -->
				<!-- <div class="extra-contacts">
					<div class="extra-contacts__title">Звоните, чтобы получить помощь и консультацию</div>
					<div class="extra-contacts__items">
						<div class="extra-contacts__item">
							<div class="extra-contacts__number">(+375 17) 233-65-94</div>
							<div class="extra-contacts__text">офис г. Минск</div>
						</div>
						<div class="extra-contacts__item">
							<div class="extra-contacts__number">(+375 162) 54-54-40</div>
							<div class="extra-contacts__text">филиал г. Брест</div>
						</div>
						<div class="extra-contacts__item">
							<div class="extra-contacts__number">(+375 152) 77-29-52</div>
							<div class="extra-contacts__text">филиал г. Гродно</div>
						</div>
					</div>
				</div> -->
<!-- 				<div class="extra-info">
					<xsl:value-of select="$p/description" disable-output-escaping="yes"/>
				</div> -->
				<a class="button" data-toggle="modal" data-target="#modal-feedback">Консультация специалиста</a>
			</div>
			<div class="description">

					<ul class="nav nav-tabs" role="tablist">
						<!--<xsl:if test="string-length($p/text) &gt; 15">-->
							<li role="presentation" class="active">
								<a href="#tab0" role="tab" data-toggle="tab">Описание</a>
							</li>							
							<xsl:if test="$p/params/param !='' and not(p/line_product)">
								<li role="presentation">
									<a href="#tab1" role="tab" data-toggle="tab">Характеристики</a>
								</li>
							</xsl:if>
							<xsl:for-each select="$p/product_extra">
								<li role="presentation">
									<a href="#tab{@id}" role="tab" data-toggle="tab"><xsl:value-of select="name"/></a>
								</li>
							</xsl:for-each>
					</ul>
				<div class="tab-content">
					<div role="tabpanel" class="tab-pane active" id="tab0">
						<div>
							<xsl:value-of select="$p/description" disable-output-escaping="yes"/>
						</div>
					</div>
					<xsl:if test="$p/params/param != '' and not(p/line_product)">
						<div role="tabpanel" class="tab-pane" id="tab1">
							<!--<xsl:value-of select="$p/text" disable-output-escaping="yes"/>-->
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
											<p><xsl:value-of select="."/></p>
										</td>
									</tr>
								</xsl:for-each>
							</table>

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
		<xsl:if test="page/assoc">
			<h3>Вас также может заинтересовать</h3>
			<div class="catalog-items">
				<xsl:apply-templates select="page/assoc" mode="lines"/>
			</div>
		</xsl:if>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>



</xsl:stylesheet>
