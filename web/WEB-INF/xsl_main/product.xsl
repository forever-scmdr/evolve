<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="classvl" select="1"/>
	<xsl:variable name="title" select="$p/name"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'catalog'"/>

	<xsl:variable name="has_plain" select="$mods/plain_in_product = 'on'"/><!-- + -->

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>


	<xsl:variable name="hide_side_menu" select="true()"/>
	<xsl:variable name="p" select="page/product"/>
	<xsl:variable name="has_lines" select="$p/has_lines = '1'"/>
	<xsl:variable name="p_big" select="if (index-of($p/text, 'img src') &gt; -1 or string-length($p/text) &gt; 500) then $p/text else ''"/>
	<xsl:variable name="is_big" select="$p_big and not($p_big = '')"/>
	<xsl:variable name="is_not_plain" select="$p/product_section"/>
	<xsl:variable name="multiple_prices" select="not($is_not_plain) or (section_name and not(section_name = ''))"/>
	<xsl:variable name="step_default" select="if (page/catalog/default_step) then f:num(page/catalog/default_step) else 1"/>

	<xsl:variable name="docs" select="if ($p/documents_xml) then parse-xml($p/documents_xml)/value else none"/>
	<xsl:variable name="env" select="if ($p/environmental_xml) then parse-xml($p/environmental_xml)/value else none"/>
	<xsl:variable name="names" select="if ($p/additional_xml) then parse-xml($p/additional_xml)/value else none"/>
	<xsl:variable name="other_names" select="$names/param[lower-case(normalize-space(name)) = 'other names']"/>
	<xsl:variable name="package" select="$names/param[lower-case(normalize-space(name)) = 'standard package']"/>
	<xsl:variable name="main_ds" select="$docs/param[1]/value[1]"/>

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
			<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id and f:num(hide) = 0]">
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
				<th colspan="2"><b><xsl:value-of select="name"/></b></th>
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

		<div class="device-full">
			<xsl:variable name="has_text" select="string-length($p/text) &gt; 15"/>
			<div class="tabs tabs_product">
				<div class="tabs__nav">
					<a href="#tab_main" class="tab tab_active">Описание</a>
					<xsl:if test="$docs/param">
						<a href="#tab_docs" class="tab">Документация</a>
					</xsl:if>
					<xsl:if test="$env/param">
						<a href="#tab_env" class="tab">Дополнительная информация</a>
					</xsl:if>
					<xsl:if test="$other_names">
						<a href="#tab_names" class="tab">Альтернативные названия</a>
					</xsl:if>
					<xsl:if test="$docs/param">
						<a href="#tab_ds_online" class="tab">Даташит online</a>
					</xsl:if>
				</div>
				<div class="tabs__content">
					<div class="tab-container" id="tab_main">
						<div class="device-basic">
							<div class="gallery device-basic__column">
								<p class="subtitle">Артикул: <xsl:value-of select="$p/code"/></p>
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
									<xsl:if test="not($p/gallery) and not($p/main_pic)">
										<img src="img/no_image.png" alt="{$p/name}"/>
									</xsl:if>
								</div>
							</div>
							<div class="device-basic__column">
								<!-- <xsl:for-each select="$p/tag">
									<div class="device__tag device__tag_device-page"><xsl:value-of select="." /></div>
								</xsl:for-each> -->

								<xsl:variable name="has_price" select="$p/price and $p/price != '0'"/>

								<xsl:if test="not($has_lines)">
									<!-- цена -->

									<!-- Множественная цена с промежутками -->
									<xsl:if test="section_name and not(section_name = '')">
										<div class="price price_product">
											<xsl:call-template name="ALL_PRICES">
												<xsl:with-param name="need_sum" select="false()"/>
												<xsl:with-param name="price_in_currency" select="f:exchange($p, 'price', 0)"/>
												<xsl:with-param name="product" select="$p"/>
												<xsl:with-param name="section_name" select="section_name"/>
											</xsl:call-template>
										</div>
									</xsl:if>

									<!-- Простая цена -->
									<xsl:if test="$has_price and not($multiple_prices)">
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
									<div class="product-actions mb-3">
										<!--
										<xsl:if test="$has_cart and $is_not_plain">
											<div id="cart_list_{$p/@id}" class="order order_product">
												<form action="{$p/to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{$p/@id}">
													<xsl:if test="$has_price">
														<input type="number" class="input input_size_lg input_type_number" name="qty"
															   value="{if ($p/min_qty) then $p/min_qty else 1}" min="{if ($p/min_qty) then $p/min_qty else 0}" step="{if ($p/step) then f:num($p/step) else $step_default}"/>
														<button class="button button_size_lg" type="submit"><xsl:value-of select="$to_cart_na_label"/></button>
													</xsl:if>
													<xsl:if test="not($has_price)">
														<input type="number" class="input input_size_lg input_type_number" name="qty"
															   value="{if ($p/min_qty) then $p/min_qty else 1}" min="{if ($p/min_qty) then $p/min_qty else 0}" step="{if ($p/step) then f:num($p/step) else $step_default}"/>
														- кнопка запросить цену на стрранице товара -
														<button class="button button_size_lg" type="submit"><xsl:value-of select="$to_cart_na_label"/></button>
													</xsl:if>
												</form>
											</div>
										</xsl:if>
										-->
										<div class="add add_product">
											<xsl:if test="$has_fav">
												<div id="fav_list_{$p/@id}">
													<a href="{$p/to_fav}" class="add__item icon-link" ajax="true" ajax-loader-id="fav_list_{$p/@id}">
														<div class="icon"><img src="img/icon-star.svg" alt="" /></div>
														<span><xsl:value-of select="$compare_add_label"/></span>
													</a>
												</div>
											</xsl:if>
											<xsl:if test="$has_compare">
												<div id="compare_list_{$p/@id}">
													<a href="{$p/to_compare}" class="add__item icon-link" ajax="true" ajax-loader-id="compare_list_{$p/@id}">
														<div class="icon"><img src="img/icon-balance.svg" alt="" /></div>
														<span><xsl:value-of select="$go_to_compare_label"/></span>
													</a>
												</div>
											</xsl:if>
										</div>
									</div>
									<div class="product-actions mb-3">
										<div class="add add_product">
											<div id="spec_list_{$p/@id}">
												<a href="{$p/to_spec}" class="add__item icon-link" ajax="true" ajax-loader-id="spec_list_{$p/@id}">
													<div class="icon"><img src="img/icon-spec.svg" alt=""/></div>
													<span><xsl:value-of select="$go_to_spec_label"/></span>
												</a>
											</div>
										</div>
									</div>
									<div class="product-actions">
										<div class="add add_product">
											<xsl:if test="$p/files">
												<a href="{$p/@path}{$p/files[1]}" class="icon-link product-icons__item" download="{$p/files[1]}">
													<div class="icon icon_size_lg">
														<img src="img/pdf.png" alt="" />
													</div>
													<span class="icon-link__item">Документация</span>
												</a><br/><br/>
											</xsl:if>
										</div>
									</div>
									<!--
									<xsl:choose>
										<xsl:when test="$p/qty and $p/qty != '0'"><div class="device__in-stock"><i class="fas fa-check"></i> в наличии</div></xsl:when>
										<xsl:otherwise><div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i> под заказ</div></xsl:otherwise>
									</xsl:choose>
									-->
									<div class="extra-buttons product-actions mitaba">
										<xsl:if test="$has_my_price">
											<a href="{$p/my_price_link}" rel="nofollow" ajax="true" data-toggle="modal" class="button secondary button_size_lg"
											   data-target="#modal-my_price"><xsl:value-of select="$mp_link"/></a>
										</xsl:if>
										<xsl:if test="$has_one_click">
											<a href="{$p/one_click_link}" rel="nofollow" ajax="true" data-toggle="modal" class="button_size_lg"
											   data-target="#modal-one_click">Купить в 1 клик</a>
										</xsl:if>
									</div>
									<!-- один клик и своя цена не сверстаны -->
									<xsl:if test="$has_one_click or $has_my_price">
										<div class="extra-buttons">
											<!--							<xsl:if test="$is_one_click">-->
											<!--								<a href="{$p/one_click_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-one_click">Купить в 1 клик</a>-->
											<!--							</xsl:if>-->
											<!--							<xsl:if test="$is_my_price">-->
											<!--								<a href="{$p/my_price_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-my_price"><xsl:value-of select="$mp_link"/></a>-->
											<!--							</xsl:if>-->
											<xsl:if test="$has_subscribe">
												<a href="{$p/subscribe_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-subscribe">Сообщить о появлении</a>
											</xsl:if>
										</div>
									</xsl:if>

									<!-- параметры -->
									<table class="params">
										<xsl:variable name="user_defined_params" select="tokenize($sel_sec/params_short, '[\|;]\s*')"/>
										<xsl:variable name="is_user_defined" select="$sel_sec/params_short and not($sel_sec/params_short = '') and count($user_defined_params) &gt; 0"/>
										<xsl:variable name="captions" select="if ($is_user_defined) then $user_defined_params else $p/param_vals/@key"/>
										<xsl:for-each select="$captions">
											<xsl:variable name="param" select="$p/param_vals[lower-case(normalize-space(@key)) = lower-case(normalize-space(current()))]"/>
											<tr>
												<td><xsl:value-of select="$param/@key"/></td>
												<td><xsl:value-of select="$param/@value"/></td>
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
															   value="{if (min_qty) then min_qty else 1}" min="{if (min_qty) then min_qty else 0}" step="{if (step) then f:num(step) else $step_default}" />
														<input type="submit" class="button" value="{$to_cart_available_label}" />
													</xsl:if>
													<xsl:if test="not($has_price)">
														<input type="number" class="text-input" name="qty"
															   value="{if (min_qty) then min_qty else 1}" min="{if (min_qty) then min_qty else 0}" step="{if (step) then f:num(step) else $step_default}" />
														<input type="submit" class="button" value="{$to_cart_na_label}" />
													</xsl:if>
												</form>
											</div>
										</xsl:for-each>
									</div>
									<div class="multi-device__links">
										<xsl:if test="$has_compare">
											<div id="compare_list_{$p/@id}">
												<a href="{$p/to_compare}" class="device__action-link icon-link" ajax="true" ajax-loader-id="compare_list_{$p/@id}">
													<i class="fas fa-balance-scale"></i><xsl:value-of select="$go_to_compare_label"/>
												</a>
											</div>
										</xsl:if>
										<xsl:if test="$has_fav">
											<div id="fav_list_{$p/@id}">
												<a href="{$p/to_fav}" class="device__action-link icon-link" ajax="true" ajax-loader-id="fav_list_{$p/@id}">
													<i class="fas fa-star"></i><xsl:value-of select="$compare_add_label"/>
												</a>
											</div>
										</xsl:if>
									</div>
								</xsl:if>

								<div class="product-lables">
									<xsl:value-of select="$p/text" disable-output-escaping="yes"/>
								</div>

								<div class="product-icons">
									<!--<a href="" class="icon-link product-icons__item">
										<div class="icon icon_size_lg">
											<img src="img/share.png" alt="" />
										</div>
										<span class="icon-link__item">поделиться</span>
									</a>
									<a href="{$p/@path}{$p/files[1]}" class="icon-link product-icons__item" target="_blank">
										<div class="icon icon_size_lg">
											<img src="img/print.png" alt="" />
										</div>
										<span class="icon-link__item">распечатать</span>
									</a>-->
								</div>

							</div>
						</div>
					</div>

					<div class="tab-container" id="tab_docs" style="display: none">
						<table>
							<tr>
								<th>ТИП РЕСУРСА</th>
								<th>ССЫЛКА</th>
							</tr>
							<xsl:for-each select="$docs/param">
								<xsl:variable name="digikey_link" select="value/a[contains(@href, 'digikey')]"/>
								<xsl:if test="not($digikey_link)">
									<tr>
										<td><xsl:value-of select="name"/></td>
										<td>
											<xsl:for-each select="value">
												<xsl:variable name="not_first_value" select="position() != 1"/>
												<xsl:for-each select="a">
													<xsl:variable name="not_first_a" select="position() != 1"/>
													<xsl:if test="$not_first_a or $not_first_value"><br/></xsl:if>
													<a href="{@href}"><xsl:value-of select="."/></a>
												</xsl:for-each>
											</xsl:for-each>
										</td>
									</tr>
								</xsl:if>
							</xsl:for-each>
						</table>
					</div>

					<div class="tab-container" id="tab_env" style="display: none">
						<table>
							<xsl:for-each select="$env/param">
								<tr>
									<td><xsl:value-of select="name"/></td>
									<td>
										<xsl:for-each select="value">
											<xsl:if test="position() != 1"><br/></xsl:if>
											<xsl:value-of select="."/>
										</xsl:for-each>
									</td>
								</tr>
							</xsl:for-each>
						</table>
					</div>

					<xsl:if test="$other_names">
						<div class="tab-container" id="tab_names" style="display: none">
							<table>
								<xsl:for-each select="$other_names/value">
									<tr>
										<td><xsl:value-of select="."/></td>
									</tr>
								</xsl:for-each>
							</table>
						</div>
					</xsl:if>

					<div class="tab-container" id="tab_ds_online" style="display: none">
						<p>
							<object data="{$main_ds/a/@href}" type="application/pdf" width="80%" height="720">
								не удалось показать документ
							</object>
						</p>
					</div>
				</div>
			</div>

		</div>


		<div style="padding-left: 5px; padding-bottom: 10px" id="cur_div">
			<ul class="currency-options">
				<xsl:variable name="currency_link" select="page/set_currency"/>
				<xsl:for-each select="$currencies/*[ends-with(name(), '_rate')]">
					<xsl:variable name="cur" select="substring-before(name(), '_rate')"/>
					<xsl:variable name="active" select="$currency = $cur"/>
					<li class="{'active'[$active]}">
						<xsl:if test="not($active)"><a href="{concat($currency_link, $cur)}#cur_div"><xsl:value-of select="$cur"/></a></xsl:if>
						<xsl:if test="$active"><xsl:value-of select="$cur"/></xsl:if>
					</li>
				</xsl:for-each>
				<li><i class="far fa-money-bill-alt"/>&#160;<strong>Валюта</strong></li>
			</ul>
			<div id="api_ajax_1" ajax-href="search_prices_mouser?q={$p/name}"></div>
			<div id="api_ajax_2" ajax-href="search_prices_digikey?q={$p/name}"></div>
			<div id="api_ajax_3" ajax-href="search_prices_farnell?q={$p/name}"></div>
		</div>


		<xsl:if test="page/plain_catalog/product and $has_plain">
			<xsl:call-template name="LINES_TABLE">
				<xsl:with-param name="products" select="page/plain_catalog/product"/>
			</xsl:call-template>
		</xsl:if>

		<xsl:if test="$p/api_ajax_link">
			<div id="products_api_ajax" ajax-href="{$p/api_ajax_link}" ajax-show-loader="yes"></div>
		</xsl:if>

		<div class="extra-info extra-info_product">
			<xsl:value-of select="page/common/catalog_texts/payment" disable-output-escaping="yes"/>
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
							<xsl:apply-templates select="." mode="product-table"/>
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
