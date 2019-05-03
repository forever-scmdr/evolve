<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="3.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="path" select="page/catalog//section[.//@id = $sel_sec_id]"/>
	<xsl:variable name="is_video" select="$path/name = 'Видео'"/>
	<xsl:variable name="is_music" select="$path/name = 'Музыка'"/>
	<xsl:variable name="is_book" select="$path/name = 'Книги'"/>

	<xsl:variable name="video_prefix"><xsl:choose>
		<xsl:when test="$path/name = 'Художественные фильмы'">Фильм</xsl:when>
		<xsl:when test="$path/name = 'Сериалы'">Сериал</xsl:when>
		<xsl:when test="$path/name = 'Мультфильмы'">Мультфильм</xsl:when>
		<xsl:when test="$path/name = 'Документальные фильмы'">Документальный фильм</xsl:when>
		<xsl:otherwise>Видео</xsl:otherwise>
	</xsl:choose></xsl:variable>

	<xsl:variable name="video_title" select="string-join(('Купить', lower-case($video_prefix) ,$p/name,'в Минске - интернет магазин Mystery.by, Беларусь'), ' ')"/>
	<xsl:variable name="video_description" select="string-join(($video_prefix, $p/name, 'от Мистери недорого. Доставка по Беларуси. ЗАКАЗЫВАЙТЕ по ☎☎☎ +375 29 257 08 03!'), ' ')"/>

	<xsl:variable name="music_title" select="string-join(('Купить музыку', $p/name,',', $p/artist,'в Минске - интернет магазин Mystery.by, Беларусь'), ' ')"/>
	<xsl:variable name="music_description" select="string-join(('Музыка: альбом', $p/name,',', $p/artist, 'в магазине Мистери недорого. Доставка по Беларуси. ЗАКАЗЫВАЙТЕ по ☎☎☎ +375 29 257 08 03!'), ' ')"/>

	<xsl:variable name="book_title" select="string-join(('Купить книгу', $p/name,'', $p/author,': цена ', $p/price,'byn в Минске - интернет магазин Mystery.by, Беларусь'),' ')"/>
	<xsl:variable name="book_description" select="string-join(('Книга', $p/name,',', $p/author, 'в магазине Мистери недорого. Доставка по Беларуси. ЗАКАЗЫВАЙТЕ по ☎☎☎ +375 29 257 08 03!'),' ')"/>


	<xsl:variable name="title" select="if($is_video) then $video_title else if($is_book) then $book_title else $music_title"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $p/name"/>
	<xsl:variable name="meta_description" select="if($is_video) then $video_description else if($is_book) then $book_description else $music_description"/>
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
				<xsl:for-each select="$path">
					<xsl:text disable-output-escaping="yes"> &gt; </xsl:text>
					<a href="{show_products}"><xsl:value-of select="name"/></a>
				</xsl:for-each>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title"><xsl:value-of select="$h1"/></h1>
		<p>арт. <!-- <a target="_blank" rel="nofollow" href="{concat('https://www.ozon.ru/context/detail/id/', $p/code, '/?partner=mysteryby')}"> --><xsl:value-of select="$p/code"/><!-- </a> --></p>
		<div class="catalog-item-container">
			<div class="gallery">
				<div class="fotorama" data-max-width="350" data-nav="thumbs" data-thumbheight="40" data-thumbwidth="40" data-allowfullscreen="true">
					<xsl:for-each select="$p/picture">
						<img src="{.}" alt="{string-join(($p/name, $p/author), '. ')}"/>
					</xsl:for-each>
				</div>
			</div>
			<div class="product-info">
				<div class="delivery-date extra-info">
					<p>Товар поступит на пункт самовывоза <b><xsl:value-of select="$delivery_date" /></b></p>
				</div>
				<!-- new html -->
				<xsl:for-each select="$p/tag">
					<div class="device__tag device__tag_device-page"><xsl:value-of select="." /></div>
				</xsl:for-each>

				<xsl:variable name="has_price" select="$p/price and $p/price != '0'"/>

				<xsl:if test="not($has_lines)">
					<div class="device-page__actions">
						<xsl:if test="$has_price">
							<div class="device__price device__price_device-page">
								<xsl:if test="$p/price_old"><div class="price_old"><span><xsl:value-of select="$p/price_old"/> руб.</span></div></xsl:if>
								<div class="price_normal"><xsl:value-of select="if ($p/price) then $p/price else '0'"/> р.</div>
							</div>
						</xsl:if>
						<div id="cart_list_{$p/@id}" class="device__order device__order_device-page product_purchase_container">
							<form action="{$p/to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{$p/@id}">
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
						<div class="device__actions device__actions_device-page">
							<div id="compare_list_{$p/@id}">
								<a href="{$p/to_compare}" rel="nofollow" class="device__action-link icon-link" ajax="true" ajax-loader-id="compare_list_{$p/@id}">
									<i class="fas fa-balance-scale"></i>сравнить
								</a>
							</div>
							<div id="fav_list_{$p/@id}">
								<a href="{$p/to_fav}" rel="nofollow" class="device__action-link icon-link" ajax="true" ajax-loader-id="fav_list_{$p/@id}">
									<i class="fas fa-star"></i>отложить
								</a>
							</div>
						</div>
						<div class="extra-links">
							<a href="{$p/one_click_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-one_click">Купить в 1 клик</a>
						</div>
						<xsl:choose>
							<xsl:when test="$p/qty and $p/qty != '0'"><div class="device__in-stock"><i class="fas fa-check"></i> в наличии</div></xsl:when>
							<xsl:otherwise><div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i> под заказ</div></xsl:otherwise>
						</xsl:choose>
					</div>
				</xsl:if>

				<xsl:if test="$has_lines">
					<div class="multi-device">
						<div style="padding-left: 0;">Размер</div>
						<div>Цена</div>
						<div></div>

						<xsl:for-each select="$p/line_product">
							<xsl:variable name="has_price" select="price and price != '0'"/>
							<div class="multi-device__name"><xsl:value-of select="name" /></div>
							<div class="multi-device__price">
								<xsl:if test="$has_price">
									<xsl:if test="price_old"><div class="multi-device__price_old"><xsl:value-of select="price_old"/> руб.</div></xsl:if>
									<div class="multi-device__price_new"><xsl:value-of select="if (price) then price else '0'"/></div>
								</xsl:if>
								<xsl:if test="not($has_price)">
									<div class="multi-device__price_new">по запросу</div>
								</xsl:if>
							</div>
							<div class="multi-device__actions" id="cart_list_{@id}">
								<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
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
						<i class="fas fa-truck device-benefits__icon"></i>
						<div class="device-benefits__label">Курьерская доставка по Минску 5 рублей</div>
					</div>
					<div class="device-benefits__item">
						<i class="fas fa-envelope device-benefits__icon"></i>
						<div class="device-benefits__label">Почтой по Беларуси 5 рублей</div>
					</div>
					<div class="device-benefits__item">
						<i class="fas fa-shopping-cart device-benefits__icon"></i>
						<div class="device-benefits__label">Самовывоз - бесплатно</div>
					</div>
				</div>
				<div class="extra-contacts">
					<div class="extra-contacts__title">Заказать или получить помощь и консультацию:</div>
					<div class="extra-contacts__items">
						<div class="extra-contacts__item">
							<div class="extra-contacts__number">(+375 29) 381-08-03</div>
							<div class="extra-contacts__text">Velcom</div>
						</div>
						<div class="extra-contacts__item">
							<div class="extra-contacts__number">(+375 29) 257-08-03</div>
							<div class="extra-contacts__text">МТС</div>
						</div>
						<div class="extra-contacts__item">
							<div class="extra-contacts__number">(+375 25) 755-08-03</div>
							<div class="extra-contacts__text">Life</div>
						</div>
					</div>
				</div>
				<div class="extra-info">
					<xsl:value-of select="$p/description" disable-output-escaping="yes"/>
					<p></p>
					<p><b>ОПИСАНИЕ</b></p>
					<ul>
						<xsl:if test="$p/artist"><li>Исполнитель: <xsl:value-of select="$p/artist"/></li></xsl:if>
						<xsl:if test="$p/starring"><li>Исполнители ролей: <xsl:value-of select="$p/starring"/></li></xsl:if>
						<xsl:if test="$p/director"><li>Режиссер: <xsl:value-of select="$p/director"/></li></xsl:if>
						<xsl:if test="$p/author"><li>Автор: <xsl:value-of select="$p/author"/></li></xsl:if>
						<xsl:if test="$p/series"><li>Cерия: <xsl:value-of select="$p/series"/></li></xsl:if>
						<xsl:if test="$p/media"><li>Тип медиа-носителя: <xsl:value-of select="$p/media"/></li></xsl:if>
						<xsl:if test="$p/year"><li>Год: <xsl:value-of select="$p/year"/></li></xsl:if>
						<xsl:if test="$p/originalName"><li>Оригинальное название: <xsl:value-of select="$p/originalName"/></li></xsl:if>
						<xsl:if test="$p/country_of_origin"><li>Страна производства товара: <xsl:value-of select="$p/country_of_origin"/></li></xsl:if>
						<xsl:if test="$p/country"><li>Страна производства произведения: <xsl:value-of select="$p/country"/></li></xsl:if>
						<xsl:if test="$p/publisher"><li>Издатель: <xsl:value-of select="$p/publisher"/></li></xsl:if>
						<xsl:if test="$p/page_extent"><li>Количество страниц: <xsl:value-of select="$p/page_extent"/></li></xsl:if>
						<xsl:if test="$p/language"><li>Язык: <xsl:value-of select="$p/language"/></li></xsl:if>
						<xsl:if test="$p/ISBN"><li>ISBN: <xsl:value-of select="$p/ISBN"/></li></xsl:if>
					</ul>
				</div>
				<p>Поделиться с друзьями в: <script src="//yastatic.net/es5-shims/0.0.2/es5-shims.min.js"></script>
				<script src="//yastatic.net/share2/share.js"></script>
				<div class="ya-share2" data-services="vkontakte,facebook,pinterest,viber,whatsapp,skype,telegram" data-image="https://www.mystery.by/img/logo-big.svg" data-counter=""></div>
			</p>
			</div>
			<div class="description">

					<ul class="nav nav-tabs" role="tablist">
						<!--<xsl:if test="string-length($p/text) &gt; 15">-->
						<xsl:if test="$p/params_xml">
						<li role="presentation" class="active">
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
					<xsl:if test="$p/params_xml">
						<xsl:variable name="params" select="parse-xml(concat('&lt;xml&gt;', $p/params_xml/xml, '&lt;/xml&gt;'))"/>
						<div role="tabpanel" class="tab-pane active" id="tab1">
							<!--<xsl:value-of select="$p/text" disable-output-escaping="yes"/>-->
							<table>
								<colgroup>
									<col style="width: 40%"/>
								</colgroup>
								<xsl:for-each select="$params/xml/parameter">
									<tr>
										<td>
											<p><strong><xsl:value-of select="name"/></strong></p>
										</td>
										<td>
											<p><xsl:value-of select="value"/></p>
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
			<xsl:if test="page/also">
				<div class="block-title">Смотрите также</div>
				<div class="page-content" style="width: 100%;">
				<div class="catalog-items">
					<xsl:apply-templates select="page/also"/>
				</div>
				
				</div>	
			</xsl:if>

		</div>

		<xsl:if test="page/assoc">
			<h3>Вас также может заинтересовать</h3>
			<div class="catalog-items">
				<xsl:apply-templates select="page/assoc" mode="lines"/>
			</div>
		</xsl:if>

		<xsl:if test="$common/soc">
			<section class="s-info pt-4">
				<div class="container">
					<xsl:value-of select="$common/soc/code" disable-output-escaping="yes"/>
				</div>
			</section>
		</xsl:if>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>



</xsl:stylesheet>
