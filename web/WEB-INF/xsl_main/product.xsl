<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="concat($p/name, ' купить в Минске')"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'catalog'"/>


	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>


	<xsl:variable name="p" select="page/product"/>
	<xsl:variable name="has_lines" select="$p/has_lines = '1'"/>
	<xsl:variable name="p_big" select="if (index-of($p/text, 'img src') &gt; -1 or string-length($p/text) &gt; 500) then $p/text else ''"/>
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

	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a href="{$main_host}" class="path__link">Главная страница</a>
				<div class="path__arrow"></div>
			</div>
			<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id]">
				<div class="path__item">
					<a href="{show_products}" class="path__link"><xsl:value-of select="name"/></a>
					<div class="path__arrow"></div>
				</div>
			</xsl:for-each>
		</div>
	</xsl:template>

	<xsl:template match="group">
		<xsl:if test="parameter/value">
			<tr>
				<th colspan="2"><b><xsl:value-of select="@name"/></b></th>
			</tr>
			<xsl:apply-templates select="parameter"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="parameter">
		<xsl:variable name="param" select="$p/params/param[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()/name))]"/>
		<xsl:if test="$param">
			<tr>
				<td><xsl:value-of select="$param/@caption"/></td>
				<td><xsl:value-of select="$param"/></td>
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template name="CONTENT">

		<!-- <p class="subtitle">Артикул: <xsl:value-of select="$p/code"/></p> -->
		<div class="device-basic">
			<div class="gallery device-basic__column">

				<div class="fotorama" data-width="100%" data-nav="thumbs" data-thumbheight="75" data-thumbwidth="75" data-allowfullscreen="native">
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
			<div class="device-basic__column">
				<div class="tags device-basic__tags">
					<xsl:for-each select="$p/label">
						<div class="tag  {f:translit(.)}">
							<xsl:value-of select="." />
						</div>
					</xsl:for-each>
				</div>
				<!-- <xsl:for-each select="$p/tag">
					<div class="device__tag device__tag_device-page"><xsl:value-of select="." /></div>
				</xsl:for-each> -->

				<xsl:variable name="has_price" select="$p/price and $p/price != '0'"/>

				<xsl:if test="not($has_lines)">

					<div class="buy">
						<xsl:if test="$has_price">
	            <div class="price price_product">
	              <div class="price__item_new">
	                <span class="price__value"><xsl:value-of select="f:exchange_cur($p, $price_param_name, 0)"/></span>
	              </div>
								<xsl:if test="$p/price_old">
		              <div class="price__item_old">
		                <div class="price__tag">-10%</div>
		                <span class="price__value"><xsl:value-of select="f:exchange_cur($p, $price_old_param_name, 0)"/></span>
		              </div>
								</xsl:if>
	            </div>
	            <!-- <div class="order order_product" id="cart_list_386380">
	              <form action="" method="post" ajax="true" ajax-loader-id="">
	                <input class="input input_qty" type="number" name="qty" value="1" min="0" />
	                <button class="button button_device" type="submit">Заказать</button>
	              </form>
	            </div> -->
						</xsl:if>
						<div class="order order_product" id="cart_list_{$p/@id}">
              <form action="{$p/to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{$p/@id}">
								<xsl:if test="$has_price">
	                <input class="input input_qty" type="number" name="qty" value="{if ($p/min_qty) then $p/min_qty else 1}" min="{if ($p/min_qty) then $p/min_qty else 0}" step="1" />
	                <button class="button button_device" type="submit"><xsl:value-of select="$to_cart_na_label"/></button>
								</xsl:if>
								<xsl:if test="not($has_price)">
	                <input class="input input_qty" type="number" name="qty" value="{if ($p/min_qty) then $p/min_qty else 1}" min="{if ($p/min_qty) then $p/min_qty else 0}" step="1" />
	                <button class="button button_device" type="submit"><xsl:value-of select="$to_cart_na_label"/></button>
								</xsl:if>
              </form>
            </div>
          </div>

					<!-- цена -->
					<!-- <xsl:if test="$has_price">
						<div class="price price_product">
							<xsl:if test="$p/price_old">
								<div class="price__item price__item_old">
									<span class="price__label">Цена</span>
									<span class="price__value"><xsl:value-of select="f:exchange_cur($p, $price_old_param_name, 0)"/></span>
								</div>
							</xsl:if>
							<div class="price__item price__item_new">
								<span class="price__label">Цена<xsl:if test="$p/price_old"> со скидкой</xsl:if></span>
								<span class="price__value"><xsl:value-of select="f:exchange_cur($p, $price_param_name, 0)"/></span>
							</div>
						</div>
					</xsl:if> -->

					<!-- заказ и ссылки добавления -->
					<div class="product-actions">
						<div class="add add_product">
              <div id="fav_list_{$p/@id}">
                <a class="add__item" href="{$p/to_fav}" ajax="true" ajax-loader-id="fav_list_{$p/@id}">
                  <img src="img/icon-device-01.png" alt="" />
									<!-- <span><xsl:value-of select="$compare_add_label"/></span> -->
                </a>
              </div>
              <div id="compare_list_{$p/@id}">
                <a class="add__item" href="{$p/to_compare}" ajax="true" ajax-loader-id="compare_list_{$p/@id}">
                  <img src="img/icon-device-02.png" alt="" />
									<!-- <span><xsl:value-of select="$go_to_compare_label"/></span> -->
                </a>
              </div>
            </div>
						<a class="info-link" href="">
              <img src="img/icon-info.png" alt="" />
              <span>Оплата и доставка</span>
            </a>
						<!-- <div id="cart_list_{$p/@id}" class="order order_product">
							<form action="{$p/to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{$p/@id}">
								<xsl:if test="$has_price">
									<input type="number" class="input input_size_lg input_type_number" name="qty" value="{if ($p/min_qty) then $p/min_qty else 1}" min="{if ($p/min_qty) then $p/min_qty else 0}" step="0.1"/>
									<button class="button button_size_lg" type="submit"><xsl:value-of select="$to_cart_na_label"/></button>
								</xsl:if>
								<xsl:if test="not($has_price)">
									<input type="number" class="input input_size_lg input_type_number" name="qty"
										   value="{if ($p/min_qty) then $p/min_qty else 1}" min="{if ($p/min_qty) then $p/min_qty else 0}" step="0.1"/>
									<button class="button button_size_lg" type="submit"><xsl:value-of select="$to_cart_na_label"/></button>
								</xsl:if>
							</form>
						</div> -->
						<!-- <div class="add add_product">
							<div id="fav_list_{$p/@id}">
								<a href="{$p/to_fav}" class="add__item icon-link" ajax="true" ajax-loader-id="fav_list_{$p/@id}">
									<div class="icon"><img src="img/icon-star.svg" alt="" /></div>
									<span><xsl:value-of select="$compare_add_label"/></span>
								</a>
							</div>
							<div id="compare_list_{$p/@id}">
								<a href="{$p/to_compare}" class="add__item icon-link" ajax="true" ajax-loader-id="compare_list_{$p/@id}">
									<div class="icon"><img src="img/icon-balance.svg" alt="" /></div>
									<span><xsl:value-of select="$go_to_compare_label"/></span>
								</a>
							</div>
						</div> -->
					</div>
					<xsl:value-of select="page/common/catalog_texts/payment" disable-output-escaping="yes"/>

					<!--
					<xsl:choose>
						<xsl:when test="$p/qty and $p/qty != '0'"><div class="device__in-stock"><i class="fas fa-check"></i> в наличии</div></xsl:when>
						<xsl:otherwise><div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i> под заказ</div></xsl:otherwise>
					</xsl:choose>
					-->

					<!-- параметры -->
					<table class="params">
						<xsl:variable name="user_defined_params" select="tokenize($sel_sec/params_short, '[\|;]\s*')"/>
						<xsl:variable name="is_user_defined" select="$sel_sec/params_short and not($sel_sec/params_short = '') and count($user_defined_params) &gt; 0"/>
						<xsl:variable name="captions" select="if ($is_user_defined) then $user_defined_params else $p/params/param/@caption"/>
						<xsl:for-each select="$captions">
							<xsl:variable name="param" select="$p/params/param[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()))]"/>
							<tr>
								<td><xsl:value-of select="$param/@caption"/></td>
								<td><xsl:value-of select="$param"/></td>
							</tr>
						</xsl:for-each>
					</table>
				</xsl:if>

				<!-- один клик и своя цена не сверстаны -->
				<xsl:if test="$is_one_click or $is_my_price">
					<div class="extra-buttons">
						<xsl:if test="$is_one_click">
							<a href="{$p/one_click_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-one_click">Купить в 1 клик</a>
						</xsl:if>
						<xsl:if test="$is_my_price">
							<a href="{$p/my_price_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-my_price"><xsl:value-of select="$mp_link"/></a>
						</xsl:if>
						<a class="button secondary" data-toggle="modal" data-target="#warranty">XXL-гарантия</a>
						<xsl:if test="$is_subscribe">
							<a href="{$p/subscribe_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-subscribe">Сообщить о появлении</a>
						</xsl:if>
					</div>
				</xsl:if>

				<!-- вложенные товары не сверстаны -->
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
									<xsl:if test="price_old"><div class="multi-device__price_old"><xsl:value-of select="f:exchange(current(), $price_old_param_name, 0)"/></div></xsl:if>
									<div class="multi-device__price_new"><xsl:value-of select="f:exchange(current(), $price_param_name, 0)"/></div>
								</xsl:if>
								<xsl:if test="not($has_price)">
									<div class="multi-device__price_new">по запросу</div>
								</xsl:if>
							</div>
							<div class="multi-device__actions" id="cart_list_{@id}">
								<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
									<xsl:if test="$has_price">
										<input type="number" class="text-input" name="qty" value="1" min="0" />
										<input type="submit" class="button" value="{$to_cart_available_label}" />
									</xsl:if>
									<xsl:if test="not($has_price)">
										<input type="number" class="text-input" name="qty" value="1" min="0" />
										<input type="submit" class="button" value="{$to_cart_na_label}" />
									</xsl:if>
								</form>
							</div>
						</xsl:for-each>

					</div>
					<div class="multi-device__links">
						<div id="compare_list_{$p/@id}">
							<a href="{$p/to_compare}" class="device__action-link icon-link" ajax="true" ajax-loader-id="compare_list_{$p/@id}">
								<i class="fas fa-balance-scale"></i><xsl:value-of select="$go_to_compare_label"/>
							</a>
						</div>
						<div id="fav_list_{$p/@id}">
							<a href="{$p/to_fav}" class="device__action-link icon-link" ajax="true" ajax-loader-id="fav_list_{$p/@id}">
								<i class="fas fa-star"></i><xsl:value-of select="$compare_add_label"/>
							</a>
						</div>
					</div>
				</xsl:if>

				<div class="product-lables">
					<xsl:value-of select="$p/description" disable-output-escaping="yes"/>
				</div>

				<!-- <div class="product-icons">
					<xsl:if test="$p/files">
						<a href="{$p/@path}{$p/files[1]}" class="icon-link product-icons__item" download="{$p/files[1]}">
							<div class="icon icon_size_lg">
								<img src="img/product-icon-02.png" alt="" />
							</div>
							<span class="icon-link__item">скачать</span>
						</a>
					</xsl:if>
					<a href="" class="icon-link product-icons__item">
						<div class="icon icon_size_lg">
							<img src="img/product-icon-02.png" alt="" />
						</div>
						<span class="icon-link__item">поделиться</span>
					</a>
					<a href="{$p/@path}{$p/files[1]}" class="icon-link product-icons__item" target="_blank">
						<div class="icon icon_size_lg">
							<img src="img/product-icon-02.png" alt="" />
						</div>
						<span class="icon-link__item">распечатать</span>
					</a>
				</div> -->

			</div>
		</div>

		<div class="device-full">
			<xsl:variable name="has_text" select="string-length($p/text) &gt; 15"/>
			<div class="tabs tabs_product">
				<div class="tabs__nav">
					<xsl:if test="$has_text">
						<a href="#tab_text" class="tab tab_active">Описание</a>
					</xsl:if>
					<xsl:if test="$p/params">
						<a href="#tab_tech" class="tab{' tab_active'[not($has_text)]}">Характеристики</a>
					</xsl:if>
					<xsl:for-each select="$p/product_extra">
						<a href="#tab_{@id}" class="tab"><xsl:value-of select="name"/></a>
					</xsl:for-each>
				</div>
				<div class="tabs__content">
					<xsl:if test="$has_text">
						<div class="tab-container" id="tab_text">
							<xsl:value-of select="$p/text" disable-output-escaping="yes"/>
						</div>
					</xsl:if>
					<xsl:if test="$p/params">
						<div class="tab-container" id="tab_tech" style="{'display: none'[$has_text]}">
							<table class="full-params">
								<xsl:variable name="params_xml_item" select="if($sel_sec/params_xml != '') then $sel_sec/params_xml else $p/params_xml"/>
								<xsl:variable name="params_xml" select="parse-xml(concat('&lt;params&gt;', $params_xml_item/xml, '&lt;/params&gt;'))"/>
								<xsl:apply-templates select="$params_xml/params/group"/>
								<xsl:apply-templates select="$params_xml/params/parameter"/>
							</table>
						</div>
					</xsl:if>
					<xsl:for-each select="$p/product_extra">
						<div class="tab-container" id="tab_{@id}" style="display: none">
							<xsl:value-of select="text" disable-output-escaping="yes"/>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</div>

		<xsl:if test="page/grouped">
			<div class="block devices-block pt">
				<div class="title title_2">Другие цвета данной модели</div>
				<div class="devices-block__wrap device-carousel-colors">
					<xsl:for-each select="page/grouped[gallery[2] and not(code = $p/code)]">
						<div class="devices-block__column">
							<div class="card device">
								<xsl:variable  name="main_pic" select="gallery[2]"/>
								<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>
								<a href="{show_product}" class="device__image img">
									<img src="{$pic_path}" alt="" />
								</a>
								<div class="text_size_sm">
									<!-- <xsl:value-of select="code"/> -->
									<xsl:value-of select="name_extra"/>
								</div>
							</div>
						</div>
					</xsl:for-each>
				</div>
				<div class="device-nav-colors device-nav"></div>
			</div>
		</xsl:if>


		<xsl:if test="page/assoc">
			<div class="block devices-block pt">
				<div class="title title_2">Вас также могут заинтересовать</div>
				<div class="devices-block__wrap device-carousel-similar">
					<xsl:for-each select="page/assoc">
						<div class="devices-block__column">
							<!-- это обычный товар -->
							<xsl:apply-templates select="."/>
						</div>
					</xsl:for-each>
				</div>
				<div class="device-nav-similar device-nav"></div>
			</div>
		</xsl:if>


	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
		<script type="text/javascript" src="fotorama/fotorama.js"/>
	</xsl:template>



</xsl:stylesheet>
