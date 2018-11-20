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
				<a href="{$main_host}">Главная страница</a>
				<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id]">
					<xsl:text disable-output-escaping="yes"> &gt; </xsl:text>
					<a href="{show_products}"><xsl:value-of select="name"/></a>
				</xsl:for-each>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title"><xsl:value-of select="$h1"/></h1>
		<p>арт. <xsl:value-of select="$p/code"/></p>
		<div class="catalog-item-container">
			<div class="gallery">
				<div class="fotorama" data-nav="thumbs" data-thumbheight="40" data-thumbwidth="40" data-allowfullscreen="true">
					<xsl:for-each select="$p/gallery">
						<img src="{$p/@path}{.}" alt="{$p/name}"/>
					</xsl:for-each>
					<xsl:if test="not($p/gallery)">
						<img src="{concat($p/@path, $p/main_pic)}" alt="{$p/name}"/>
					</xsl:if>
				</div>
			</div>
			<div class="product-info">
				<!-- new html -->
				<xsl:for-each select="$p/tag">
					<div class="device__tag device__tag_device-page"><xsl:value-of select="." /></div>
				</xsl:for-each>

				<xsl:variable name="has_price" select="$p/price and $p/price != '0'"/>
				<div class="device-page__actions">
					<xsl:if test="$has_price">
						<div class="device__price device__price_device-page">
							<div class="price_old"><span>100 руб.</span></div>
							<div class="price_normal"><xsl:value-of select="if ($p/price) then $p/price else '0'"/> р.</div>
						</div>
					</xsl:if>

					<xsl:variable name="has_price" select="$p/price and $p/price != '0'"/>
					<div id="cart_list_{replace($p/code, '[)()]', '-')}" class="device__order device__order_device-page product_purchase_container">
						<form action="{$p/to_cart}" method="post" ajax="true">
							<xsl:if test="$has_price">
								<input type="number" class="text-input" name="qty" value="1" min="0" />
								<input type="submit" class="button" value="Заказать" />
							</xsl:if>
							<xsl:if test="not($has_price)">
								<input type="number" class="text-input" name="qty" value="1" min="0" />
								<input type="submit" class="button" value="Запросить цену" />
							</xsl:if>
						</form>
					</div>
					<div class="device__actions device__actions_device-page">
						<a href="" class="device__action-link icon-link"><i class="fas fa-balance-scale"></i>сравнить</a>
						<a href="" class="device__action-link icon-link"><i class="fas fa-star"></i>отложить</a>
					</div>
					<xsl:choose>
						<xsl:when test="$p/qty and $p/qty != '0'"><div class="device__in-stock"><i class="fas fa-check"></i> в наличии</div></xsl:when>
						<xsl:otherwise><div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i> под заказ</div></xsl:otherwise>
					</xsl:choose>
				</div>
				<div class="multi-device">
					<div style="padding-left: 0;">Размер</div>
					<div>Цена</div>
					<div></div>

					<div class="multi-device__name">P318L1/8 - 3,2 мм</div>
					<div class="multi-device__price">
						<div class="multi-device__price_old">1 р. 50 коп.</div>
						<div class="multi-device__price_new">1 р. 20 коп.</div>
					</div>
					<div class="multi-device__actions">
						<input type="number" class="text-input" name="qty" value="1" min="0" />
						<input type="submit" class="button" value="Заказать" />
					</div>

					<div class="multi-device__name">P318L1/8 - 3,2 мм</div>
					<div class="multi-device__price">
						<div class="multi-device__price_old">1 р. 50 коп.</div>
						<div class="multi-device__price_new">1 р. 20 коп.</div>
					</div>
					<div class="multi-device__actions">
						<input type="number" class="text-input" name="qty" value="1" min="0" />
						<input type="submit" class="button" value="Заказать" />
					</div>

					<div class="multi-device__name">P318L1/8 - 3,2 мм</div>
					<div class="multi-device__price">
						<div class="multi-device__price_old">1 р. 50 коп.</div>
						<div class="multi-device__price_new">1 р. 20 коп.</div>
					</div>
					<div class="multi-device__actions">
						<input type="number" class="text-input" name="qty" value="1" min="0" />
						<input type="submit" class="button" value="Заказать" />
					</div>

					<div class="multi-device__name">P318L1/8 - 3,2 мм</div>
					<div class="multi-device__price">
						<div class="multi-device__price_old">1 р. 50 коп.</div>
						<div class="multi-device__price_new">1 р. 20 коп.</div>
					</div>
					<div class="multi-device__actions">
						<input type="number" class="text-input" name="qty" value="1" min="0" />
						<input type="submit" class="button" value="Заказать" />
					</div>
				</div>
				<div class="multi-device__links">
					<a href="" class="device__action-link icon-link"><i class="fas fa-balance-scale"></i>сравнить</a>
					<a href="" class="device__action-link icon-link"><i class="fas fa-star"></i>отложить</a>
				</div>
				<div class="device-benefits">
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
				</div>
				<div class="extra-contacts">
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
				</div>
				<div class="extra-info">
					<xsl:value-of select="$p/description" disable-output-escaping="yes"/>
				</div>
								
				<!-- new html end -->
				

				<!-- <xsl:for-each select="$p/tag">
					<div class="tag-container">
						<div class="device__tag"><xsl:value-of select="." /></div>
					</div>
				</xsl:for-each>
				<xsl:variable name="has_price" select="$p/price and $p/price != '0'"/>
				<div class="price-etc">
					<xsl:if test="$has_price">
						<div class="price">
							<div class="price_old">
								<span class="marked">Старая цена</span>
								<span class="lthrough"><span>100 руб.</span></span>
							</div>
							<div class="price_normal">
								<span class="marked">Цена</span>
								<p>
									<xsl:value-of select="if ($p/price) then $p/price else '0'"/> р.
								</p>
							</div>

						</div>
					</xsl:if>
					<div class="order">
						<xsl:variable name="has_price" select="$p/price and $p/price != '0'"/>
						<div id="cart_list_{replace($p/code, '[)()]', '-')}" class="product_purchase_container">
							<form action="{$p/to_cart}" method="post" ajax="true">
								<xsl:if test="$has_price">
									<input type="number" name="qty" value="1" min="0"/>
									<input type="submit" value="Заказать"/>
								</xsl:if>
								<xsl:if test="not($has_price)">
									<input type="number" name="qty" value="1" min="0"/>
									<input type="submit" class="button not_available" value="Запросить цену"/>
								</xsl:if>
							</form>
						</div>
					</div>
					<div class="links">
						<div id="compare_list_{$p/code}">
							<span><i class="fas fa-balance-scale"></i> <a href="{$p/to_compare}" ajax="true" ajax-loader-id="compare_list_{$p/code}">сравнить</a></span>
						</div>
						<div id="fav_list_{$p/code}">
							<span><i class="fas fa-star"></i> <a href="{$p/to_fav}" ajax="true" ajax-loader-id="fav_list_{$p/code}">отложить</a></span>
						</div>
					</div>
					<xsl:choose>
						<xsl:when test="$p/qty and $p/qty != '0'"><div class="device__in-stock"><i class="fas fa-check"></i> в наличии</div></xsl:when>
						<xsl:otherwise><div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i> под заказ</div></xsl:otherwise>
					</xsl:choose>
				</div>
				<div class="info-blocks">
					<div class="info-block">
						<xsl:value-of select="$p/description" disable-output-escaping="yes"/>
					</div>

					<div class="info-block">
						<h4>Рассрочка от 3 до 12 месяцев</h4>
						<p><a href="">Условия рассрочки</a></p>
					</div>
					<div class="info-block">
						<h4>Бесплатная доставка по Минску</h4>
						<p>При сумме заказа до 100 рублей, доставка — 5 рублей.</p>
					</div>

				</div> -->
			</div>
			<div class="description">

					<ul class="nav nav-tabs" role="tablist">
						<!--<xsl:if test="string-length($p/text) &gt; 15">-->
							<xsl:if test="$p/params">
							<li role="presentation" class="active">
								<a href="#tab1" role="tab" data-toggle="tab">Характеристики</a>
							</li>
							</xsl:if>
							<xsl:if test="$p/product">
								<li role="presentation" class="{'active'[not($p/params)]}">
									<a href="#tab2" role="tab" data-toggle="tab">
										Другие расцветки
									</a>
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
