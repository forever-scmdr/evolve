<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0"> 
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="concat($p/name, ' купить в Минске – магазин радиодеталей Чип Электроникс')"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $p/name"/>
	<xsl:variable name="active_menu_item" select="'catalog'"/>
	<xsl:variable name="has_price" select="f:num($p/qty) != 0"/>

	<xsl:variable name="meta_description" select="concat($p/name, ' в Минске от Чип Электроникс ✅. Оптом и в розницу! Звоните ☎ +375 (17) 269-92-36. Доступная цена! Доставка по Беларуси.')"/>
	<xsl:variable name="meta_keywords" select="$p/name"/>

	<xsl:template name="LEFT_COLUMN">
		<xsl:call-template name="CATALOG_LEFT_COLUMN"/>
	</xsl:template>


	<xsl:variable name="p" select="page/product"/>
	<xsl:variable name="has_lines" select="$p/has_lines = '1'"/>
	<xsl:variable name="p_big" select="if (index-of($p/text, 'img src') &gt; -1 or string-length($p/text) &gt; 500) then $p/text else ''"/>
	<xsl:variable name="is_big" select="$p_big and not($p_big = '')"/>

	<xsl:variable name="price" select="$p/price"/>
	<xsl:variable name="price_old" select="$p/price_old"/>
	<xsl:variable name="u" select="if($p/unit != '') then $p/unit else 'шт'"/>
	<xsl:variable name="unit" select="if(f:num($p/min_qty) &gt; 1) then concat($p/min_qty, $u) else $u"/>

	<xsl:variable name="display_price" select="f:price_ictrade($price)"/>
	<xsl:variable name="display_price_old" select="f:price_ictrade($price_old)"/>



	<xsl:template name="MARKUP">

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
				<a href="{$main_host}">Главная страница</a> <i class="fas fa-angle-right"></i> <a href="{page/catalog_link}">Электронные компоненты</a>
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
				<div class="fotorama" data-nav="thumbs" data-thumbheight="40" data-thumbwidth="40" data-allowfullscreen="native" data-maxwidth="300">
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
				<xsl:for-each select="$p/tag[text() != 'external_shop' and text() != 'compel.ru']">
					<div class="device__tag device__tag_device-page"><xsl:value-of select="." /></div>
				</xsl:for-each>			
 
				<xsl:if test="not($has_lines)">
					<div class="device-page__actions">
						<!-- <xsl:if test="f:num($p/price) &gt; 0"> -->
						<xsl:if test="$has_price">
							<div class="device__price device__price_device-page">
								<xsl:if test="$p/price_old"><div class="price_old"><span><xsl:value-of select="$display_price_old"/></span></div></xsl:if>
								<div class="price_normal">
									<xsl:value-of select="concat($display_price, ' ', upper-case($curr), '/', $unit)" />
									<div class="nds">*Цена включает НДС</div>
								</div>

							</div>
						</xsl:if>
						<!-- <xsl:if test="f:num($p/price) = 0"> -->
						<xsl:if test="not($has_price)">
							<div class="device__price device__price_device-page">
								Цена по запросу
							</div>
						</xsl:if>
						<div id="cart_list_{$p/@id}" class="device__order device__order_device-page product_purchase_container">
							<form action="{$p/to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{$p/@id}">
								<xsl:if test="$has_price">
									<input type="number" class="text-input" name="qty" value="{$p/min_qty}" step="{if(f:num($p/step)&gt; 0) then $p/step else $p/min_qty}" min="{$p/min_qty}" />
									<input type="submit" class="button" value="В корзину" />
								</xsl:if>
								<xsl:if test="not($has_price)">
									<input type="number" class="text-input" name="qty" value="{$p/min_qty}" step="{$p/min_qty}" min="{$p/min_qty}" />
									<input type="submit" class="button  not_available" value="Под заказ" />
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
							<xsl:when test="$has_price"><div class="device__in-stock"><i class="fas fa-check"></i> в наличии <xsl:value-of select="concat($p/qty, $p/u,'.')"/></div></xsl:when>
							<xsl:otherwise><div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i> под заказ</div></xsl:otherwise>
						</xsl:choose>
						<!-- <xsl:if test="$p/pdf != ''">
							<p>
								<a href="{concat('pdf/', $p/pdf)}" target="_blank">Документ PDF</a>
							</p>
						</xsl:if> -->
					</div>
				</xsl:if>

				<xsl:if test="$has_lines">
					<div class="multi-device">
						<div style="padding-left: 0;">Размер</div>
						<div>Цена</div>
						<div></div>

						<xsl:for-each select="$p/line_product">
							<xsl:variable name="has_price" select="f:num(price) &gt; 0 and f:num(qty) &gt; 0"/>

							<xsl:variable name="display_price" select="f:price_ictrade(price)"/>
							<xsl:variable name="display_price_old" select="f:price_ictrade(price_old)"/>

							<div class="multi-device__name"><xsl:value-of select="name" /></div>
							<div class="multi-device__price">
								<xsl:if test="f:num($p/price) &gt; 0">
									<xsl:if test="price_old"><div class="multi-device__price_old"><xsl:value-of select="concat($display_price_old, ' ', upper-case($curr), '/', $unit)"/></div></xsl:if>
									<div class="multi-device__price_new"><xsl:value-of select="concat($display_price, ' ', upper-case($curr), '/', $unit)"/></div>
								</xsl:if>
								<xsl:if test="not(f:num($p/price) = 0)">
									<div class="multi-device__price_new">по запросу</div>
								</xsl:if>
							</div>
							<div class="multi-device__actions" id="cart_list_{@id}">
								<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
									<xsl:if test="$has_price">
										<input type="number" class="text-input" name="qty" value="1" min="0" />
										<input type="submit" class="button" value="В корзину" />
									</xsl:if>
									<xsl:if test="not($has_price)">
										<input type="number" class="text-input" name="qty" value="1" min="0" />
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
				<div>
					<xsl:value-of select="page/common/catalog_texts/payment" disable-output-escaping="yes"/>
				</div>
				<div class="extra-info">
					<xsl:value-of select="$p/description" disable-output-escaping="yes"/>
				</div> <p>Не нашли нужный компонент? <br></br><a href="http://ictrade.by/page/individualnyi_zakaz/" rel="nofollow" class="MSI_ext_nofollow">Сделайте индивидуальный заказ!</a></p>
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
								<xsl:if test="$p/pdf != ''">
									<tr>
										<td>
											<p>
												<strong>Техническая документация</strong>
											</p>
										</td>
										<td>
											<xsl:for-each select="$p/pdf">
												<p>
													<a class="pdf" href="{concat('pdf/', .)}" target="_blank">
														<xsl:value-of select="concat('Документ-', position(), ' PDF')"/>
													</a>
												</p>
											</xsl:for-each>
										</td>
									</tr>									
								</xsl:if>
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
