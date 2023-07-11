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
		<xsl:if test="parameter/value != ''">
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

		<!-- fix -->
		<p class="subtitle">Артикул: <xsl:value-of select="$p/code"/></p>
		<div class="device-basic">
			<div class="gallery device-basic__column">
				<div class="tags">
					<xsl:for-each select="$p/label">
						<div class="tag device__tag {f:translit(.)}">
							<xsl:value-of select="." />
						</div>
					</xsl:for-each>
				</div>
				<div class="fotorama" data-width="100%" data-nav="thumbs" data-thumbheight="75" data-thumbwidth="75" data-allowfullscreen="native">
					<xsl:for-each select="$p/gallery">
						<img src="{$p/@path}{.}" alt="{$p/name}"/>
					</xsl:for-each>
					<xsl:if test="not($p/gallery)">
						<img src="{concat($p/@path, $p/main_pic)}" alt="{$p/name}"/>
					</xsl:if>
				</div>
				<!-- <script>
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
					</script> -->
			</div>
			<div class="device-basic__column">
				<!-- <xsl:for-each select="$p/tag">
					<div class="device__tag device__tag_device-page"><xsl:value-of select="." /></div>
				</xsl:for-each> -->

				<xsl:variable name="has_price" select="$p/price and $p/price != '0'"/>

				<xsl:if test="not($has_lines)">
					<!-- цена -->
					<xsl:if test="$has_price">
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
					</xsl:if>

					<!-- заказ и ссылки добавления -->
					<div class="product-actions">
						<div id="cart_list_{$p/@id}" class="order order_product">
							<form action="{$p/to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{$p/@id}">
								<xsl:if test="$has_price">
									<input type="number" class="input input_size_lg input_type_number" name="qty"
										   value="{if ($p/min_qty) then $p/min_qty else 1}" min="{if ($p/min_qty) then $p/min_qty else 0}" step="{if ($p/step) then f:num($p/step) else 0.1}"/>
									<button class="button button_size_lg" type="submit"><xsl:value-of select="$to_cart_na_label"/></button>
								</xsl:if>
								<xsl:if test="not($has_price)">
									<input type="number" class="input input_size_lg input_type_number" name="qty"
										   value="{if ($p/min_qty) then $p/min_qty else 1}" min="{if ($p/min_qty) then $p/min_qty else 0}" step="{if ($p/step) then f:num($p/step) else 0.1}"/>
									<!-- кнопка запросить цену на стрранице товара -->
									<button class="button button_size_lg" type="submit"><xsl:value-of select="$to_cart_na_label"/></button>
								</xsl:if>
							</form>
						</div>
						<div class="add add_product">
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
						</div>
					</div>

					<!--
					<xsl:choose>
						<xsl:when test="$p/qty and $p/qty != '0'"><div class="device__in-stock"><i class="fas fa-check"></i> в наличии</div></xsl:when>
						<xsl:otherwise><div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i> под заказ</div></xsl:otherwise>
					</xsl:choose>
					-->
					<div class="extra-buttons product-actions mitaba">
						<xsl:if test="$is_my_price">
							<a href="{$p/my_price_link}" rel="nofollow" ajax="true" data-toggle="modal" class="button secondary button_size_lg"
							   data-target="#modal-my_price"><xsl:value-of select="$mp_link"/></a>
						</xsl:if>
						<xsl:if test="$is_one_click">
							<a href="{$p/one_click_link}" rel="nofollow" ajax="true" data-toggle="modal" class="button_size_lg"
							   data-target="#modal-one_click">Купить в 1 клик</a>
						</xsl:if>
					</div>
					<!-- один клик и своя цена не сверстаны -->
					<xsl:if test="$is_one_click or $is_my_price">
						<div class="extra-buttons">
<!--							<xsl:if test="$is_one_click">-->
<!--								<a href="{$p/one_click_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-one_click">Купить в 1 клик</a>-->
<!--							</xsl:if>-->
<!--							<xsl:if test="$is_my_price">-->
<!--								<a href="{$p/my_price_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-my_price"><xsl:value-of select="$mp_link"/></a>-->
<!--							</xsl:if>-->
							<xsl:if test="$is_subscribe">
								<a href="{$p/subscribe_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-subscribe">Сообщить о появлении</a>
							</xsl:if>
						</div>
					</xsl:if>

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
										<input type="number" class="text-input" name="qty"
											   value="{if (min_qty) then min_qty else 1}" min="{if (min_qty) then min_qty else 0}" step="{if (step) then f:num(step) else 0.1}" />
										<input type="submit" class="button" value="{$to_cart_available_label}" />
									</xsl:if>
									<xsl:if test="not($has_price)">
										<input type="number" class="text-input" name="qty"
											   value="{if (min_qty) then min_qty else 1}" min="{if (min_qty) then min_qty else 0}" step="{if (step) then f:num(step) else 0.1}" />
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

				<div class="product-icons">
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
				</div>

			</div>
		</div>

		<div class="device-basic complectations">
			<div class="complectation device-basic__column" style="flex: 0 0 50%;">
				<h3>Опции</h3>

					<table style="width:100%">
						<tr class="mandatory">
							<td><input type="radio" id="wheel1" name="wheels" /></td>
							<td>
								<label for="wheel1"><b>wl-0012</b></label>
							</td>
							<td>
								<label for="wheel1">Колеса тип 1</label>
							</td>
							<td>
								<b>10500</b><br/>
								12099
							</td>
						</tr>
						<tr class="mandatory">
							<td><input type="radio" id="wheel2" name="wheels" /></td>
							<td>
								<label for="wheel2"><b>wl-0013</b></label>
							</td>
							<td>
								<label for="wheel2">Колеса тип 2</label>
							</td>
							<td>
								<b>10550</b><br/>
								12200
							</td>
						</tr>
						
						<tr >
							<td><input type="checkbox" id="o1" name="wheels" /></td>
							<td>
								<label for="o1"><b>5150</b></label>
							</td>
							<td>
								<label for="o1">Фильтр для шланга гидросистемы (штука)</label>
							</td>
							<td>
								<b>500</b><br/>
							9999
							</td>
						</tr>
						<tr class="mandatory">
							<td><input type="checkbox" id="o2" name="wheels" /></td>
							<td>
								<label for="o2"><b>0013</b></label>
							</td>
							<td>
								<label for="o2">Главная рама</label>
							</td>
							<td>
								<b>3550</b><br/>
								4000
							</td>
						</tr>
						<tr >
							<td><input type="checkbox" id="o3" name="wheels" /></td>
							<td>
								<label for="o3"><b>5250</b></label>
							</td>
							<td>
								<label for="o3">Дполнительный насос шланга гидросистемы</label>
							</td>
							<td>
								<b>375</b><br/>
							862
							</td>
						</tr>
						<tr >
							<td><input type="checkbox" id="o4" name="wheels" /></td>
							<td>
								<label for="o4"><b>5350</b></label>
							</td>
							<td>
								<label for="o4">Гудок атовматический навесной</label>
							</td>
							<td>
								<b>250</b><br/>
							400
							</td>
						</tr>
					</table>

					<div class="legend">
						Обязательные опции
					</div>

			</div>
			<div class="complectation device-basic__column">
				<h3>Выбранная комплектация</h3>
				<!-- <div style="margin-bottom:40px"></div> -->
				<table>
					<tr>
						<td>
							<b>wl-0013</b>
							Колеса тип 2
						</td>
						<td>
							<b>10500</b><br/>
						</td>
					</tr>
					<tr>
						<td>
							<b>wl-0013</b>
							Главная рама
						</td>
						<td>
							<b>3550</b><br/>
						</td>
					</tr>
					<tr>
						<td>
							<b>5150</b>
							Фильтр для шланга гидросистемы (штука)
						</td>
						<td>
							<b>500</b><br/>
						</td>
					</tr>
					<tr>
						<td class="total">Сумма: <b>25673</b></td>
						<td>
							<button class="button button_size_lg" type="submit">Предзаказ</button>
						</td>
					</tr>
				</table>
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

			<hr/>
			<div class="extra-info extra-info_product">
				<xsl:value-of select="page/common/catalog_texts/payment" disable-output-escaping="yes"/>
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