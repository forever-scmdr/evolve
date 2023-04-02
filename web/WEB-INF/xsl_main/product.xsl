<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="concat($p/name, '')"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'catalog'"/>


	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>


	<xsl:variable name="p" select="page/product"/>
	<xsl:variable name="has_lines" select="$p/has_lines = '1'"/>
	<xsl:variable name="p_big"
				  select="if (index-of($p/text, 'img src') &gt; -1 or string-length($p/text) &gt; 500) then $p/text else ''"/>
	<xsl:variable name="is_big" select="$p_big and not($p_big = '')"/>

	<xsl:template name="MARKUP">
		<!-- <xsl:variable name="price" select="$p/price"/>
		<script type="application/ld+json">
			<xsl:variable name="quote">"</xsl:variable>
			{
			"@context": "http://schema.org/",
			"@type": "Product",
			"name": <xsl:value-of select="concat($quote, replace($p/name, $quote, ''), $quote)"/>,
			"image": <xsl:value-of select="concat($quote, $main_host, '/', $p/@path, $p/gallery[1], $quote)"/>,
			"brand": <xsl:value-of select="concat($quote, $p/tag[1], $quote)"/>,
			"offers": {
			"@type": "Offer",
			"priceCurrency": "BYN",
			<xsl:if test="f:num($price) &gt; 0">"price":
				<xsl:value-of select="concat($quote,f:currency_decimal($price), $quote)"/>
			</xsl:if>
			<xsl:if test="f:num($price) = 0">"price":"15000.00"</xsl:if>
			}, "aggregateRating": {
			"@type": "AggregateRating",
			"ratingValue": "4.9",
			"ratingCount": "53",
			"bestRating": "5",
			"worstRating": "1",
			"name":
			<xsl:value-of select="concat($quote, translate($p/name, $quote, ''), $quote)"/>
			}
			}
		</script> -->
	</xsl:template>

	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a href="{$main_host}" class="path__link">Главная страница</a>
				<div class="path__arrow"></div>
			</div>
			<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id and f:num(hide) = 0]">
				<div class="path__item">
					<a href="{show_products}" class="path__link">
						<xsl:value-of select="name"/>
					</a>
					<div class="path__arrow"></div>
				</div>
			</xsl:for-each>
		</div>
	</xsl:template>

	<xsl:template match="group">
		<xsl:if test="parameter/value != ''">
			<tr>
				<th colspan="2">
					<b>
						<xsl:value-of select="name"/>
					</b>
				</th>
			</tr>
			<xsl:apply-templates select="parameter"/>
		</xsl:if>
	</xsl:template>

	<xsl:template match="parameter">
		<xsl:variable name="param"
					  select="$p/params/param[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()/name))]"/>
		<xsl:if test="$param">
			<tr>
				<td>
					<xsl:value-of select="$param/@caption"/>
				</td>
				<td>
					<xsl:value-of select="$param"/>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template name="CONTENT">

		<!-- fix -->
		<!-- <p class="subtitle">Артикул:
			<xsl:value-of select="$p/code"/>
		</p> -->
		<div class="device-basic">
			<div class="gallery device-basic__column">
				<div class="tags">
					<xsl:for-each select="$p/label">
						<div class="tag device__tag {f:translit(.)}">
							<xsl:value-of select="."/>
						</div>
					</xsl:for-each>
				</div>
				<div class="fotorama" data-width="100%" data-nav="thumbs" data-thumbheight="75" data-thumbwidth="75"
					 data-allowfullscreen="native">
					<xsl:for-each select="$p/pic_link">
						<img src="{.}" alt="{$p/name}"/>
					</xsl:for-each>
					<!-- <xsl:if test="not($p/gallery_path)">
						<img src="{concat($pic_server, $p/main_pic_path)}" alt="{$p/name}"/>
					</xsl:if> -->
					<xsl:if test="not($p/gallery_path) and not($p/pic_link)">
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
					<xsl:if test="$has_price">
						<div class="price price_product">
							<xsl:if test="$p/price_old">
								<div class="price__item price__item_old">
									<span class="price__label">Цена</span>
									<span class="price__value">
										<xsl:value-of select="f:exchange_cur($p, $price_old_param_name, 0)"/>
									</span>
								</div>
							</xsl:if>
							<div class="price__item price__item_new">
								<span class="price__label">Цена
									<xsl:if test="$p/price_old">со скидкой</xsl:if>
								</span>
								<span class="price__value">
									<xsl:value-of select="f:exchange_cur($p, $price_param_name, 0)"/>
								</span>
							</div>
						</div>
					</xsl:if>

					<!-- заказ и ссылки добавления -->
					<div class="product-actions">
						<xsl:if test="$p/@type != 'complex_product'">
							<div id="cart_list_{$p/@id}" class="order order_product">

								<form action="{$p/to_cart}" method="post" ajax="true"
									  ajax-loader-id="cart_list_{$p/@id}">
									<xsl:if test="$has_price">
										<input type="number" class="input input_size_lg input_type_number" name="qty"
											   value="{if ($p/min_qty) then $p/min_qty else 1}"
											   min="{if ($p/min_qty) then $p/min_qty else 0}"
											   step="{if ($p/step) then f:num($p/step) else 1}"/>
										<button class="button button_size_lg" type="submit">
											<xsl:value-of select="$to_cart_na_label"/>
										</button>
									</xsl:if>
									<xsl:if test="not($has_price)">
										<input type="number" class="input input_size_lg input_type_number" name="qty"
											   value="{if ($p/min_qty) then $p/min_qty else 1}"
											   min="{if ($p/min_qty) then $p/min_qty else 0}"
											   step="{if ($p/step) then f:num($p/step) else 1}"/>
										<!-- кнопка запросить цену на стрранице товара -->
										<button class="button button_size_lg" type="submit">
											<xsl:value-of select="$to_cart_na_label"/>
										</button>
									</xsl:if>
								</form>
							</div>
						</xsl:if>
						<div class="add add_product">
							<div id="fav_list_{$p/@id}">
								<a href="{$p/to_fav}" class="add__item icon-link" ajax="true"
								   ajax-loader-id="fav_list_{$p/@id}">
									<div class="icon">
										<img src="img/icon-star.svg" alt=""/>
									</div>
									<span>
										<xsl:value-of select="$compare_add_label"/>
									</span>
								</a>
							</div>
							<div id="compare_list_{$p/@id}">
								<a href="{$p/to_compare}" class="add__item icon-link" ajax="true"
								   ajax-loader-id="compare_list_{$p/@id}">
									<div class="icon">
										<img src="img/icon-balance.svg" alt=""/>
									</div>
									<span>
										<xsl:value-of select="$go_to_compare_label"/>
									</span>
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
							<a href="{$p/my_price_link}" rel="nofollow" ajax="true" data-toggle="modal"
							   class="button secondary button_size_lg"
							   data-target="#modal-my_price">
								<xsl:value-of select="$mp_link"/>
							</a>
						</xsl:if>
						<xsl:if test="$is_one_click">
							<a href="{$p/one_click_link}" rel="nofollow" ajax="true" data-toggle="modal"
							   class="button_size_lg"
							   data-target="#modal-one_click">Купить в 1 клик
							</a>
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
								<a href="{$p/subscribe_link}" rel="nofollow" ajax="true" data-toggle="modal"
								   data-target="#modal-subscribe">Сообщить о появлении
								</a>
							</xsl:if>
						</div>
					</xsl:if>

					<!-- параметры -->
					<!--					<table class="params">-->
					<!--						<xsl:variable name="user_defined_params" select="tokenize($sel_sec/params_short, '[\|;]\s*')"/>-->
					<!--						<xsl:variable name="is_user_defined" select="$sel_sec/params_short and not($sel_sec/params_short = '') and count($user_defined_params) &gt; 0"/>-->
					<!--						<xsl:variable name="captions" select="if ($is_user_defined) then $user_defined_params else $p/params/param/@caption"/>-->
					<!--						<xsl:for-each select="$captions">-->
					<!--							<xsl:variable name="param" select="$p/params/param[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()))]"/>-->
					<!--							<tr>-->
					<!--								<td><xsl:value-of select="$param/@caption"/></td>-->
					<!--								<td><xsl:value-of select="$param"/></td>-->
					<!--							</tr>-->
					<!--						</xsl:for-each>-->
					<!--					</table>-->
				</xsl:if>


				<div class="product-lables">
					<xsl:value-of select="$p/text" disable-output-escaping="yes"/>
				</div>

				<div class="product-icons">
					<xsl:if test="$p/files">
						<a href="{$p/@path}{$p/files[1]}" class="icon-link product-icons__item"
						   download="{$p/files[1]}">
							<div class="icon icon_size_lg">
								<img src="img/product-icon-02.png" alt=""/>
							</div>
							<span class="icon-link__item">скачать</span>
						</a>
					</xsl:if>
					<!--					<a href="" class="icon-link product-icons__item">-->
					<!--						<div class="icon icon_size_lg">-->
					<!--							<img src="img/product-icon-02.png" alt="" />-->
					<!--						</div>-->
					<!--						<span class="icon-link__item">поделиться</span>-->
					<!--					</a>-->
					<!--					<a href="{$p/@path}{$p/files[1]}" class="icon-link product-icons__item" target="_blank">-->
					<!--						<div class="icon icon_size_lg">-->
					<!--							<img src="img/product-icon-02.png" alt="" />-->
					<!--						</div>-->
					<!--						<span class="icon-link__item">распечатать</span>-->
					<!--					</a>-->
				</div>

			</div>
		</div>


		<div class="device-basic complectations">
			<xsl:call-template name="OPTIONS"/>
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
					<xsl:if test="$p/complectation">
						<a href="#tab_compl" class="tab{' tab_active'[not($has_text)]}">Отчет по складам</a>
					</xsl:if>
					<xsl:for-each select="$p/product_extra">
						<a href="#tab_{@id}" class="tab">
							<xsl:value-of select="name"/>
						</a>
					</xsl:for-each>
					<xsl:if test="$p/product_extra_file">
						<a href="#tab_files" class="tab">Файлы</a>
					</xsl:if>
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
								<xsl:variable name="params_xml_item"
											  select="if($sel_sec/params_xml != '') then $sel_sec/params_xml else $p/params_xml"/>
								<xsl:variable name="params_xml"
											  select="parse-xml(concat('&lt;params&gt;', $params_xml_item/xml, '&lt;/params&gt;'))"/>
								<xsl:apply-templates select="$params_xml/params/parameter"/>
								<xsl:apply-templates select="$params_xml/params/group"/>
							</table>
						</div>
					</xsl:if>
					<xsl:if test="$p/complectation">
						<div class="tab-container" id="tab_compl" style="{'display: none'[$has_text]}">
							<h3>Список доступных вариантов сборки</h3>
							<xsl:apply-templates select="$p/complectation"/>
						</div>
					</xsl:if>
					<xsl:for-each select="$p/product_extra">
						<div class="tab-container" id="tab_{@id}" style="display: none">
							<xsl:value-of select="text" disable-output-escaping="yes"/>
						</div>
					</xsl:for-each>
					<xsl:if test="$p/product_extra_file">
						<div class="tab-container" id="tab_files" style="display: none">
							<table class="full-params">
								<xsl:for-each select="$p/product_extra_file">
									<tr>
										<td>
											<xsl:value-of select="desc"/>
										</td>
										<td>
											<xsl:value-of select="size"/>
										</td>
										<td>
											<a href="{@path}{file}" download="{name}">
												<xsl:value-of select="name"/>
											</a>
										</td>
									</tr>
								</xsl:for-each>
							</table>
						</div>
					</xsl:if>
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
								<xsl:variable name="main_pic" select="gallery[2]"/>
								<xsl:variable name="pic_path"
											  select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>
								<a href="{show_product}" class="device__image img">
									<img src="{$pic_path}" alt=""/>
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

	<xsl:template match="complectation">
		<div class="complectation-summary">
			<table class="with-borders">
				<thead>
					<tr>
						<th rowspan="2">Описание</th>
						<th colspan="5">Наличие</th>
					</tr>
					<tr>
						<th>Заказано на заводе</th>
						<th>Находится в Смоленске</th>
						<th>Находится на хранении</th>
						<th>Резерв</th>
						<th>Свободно к продаже</th>
					</tr>
				</thead>
				<tr>
					<td>
						<p>
							<b>
								<xsl:value-of select="$p/name"/>
							</b>
						</p>
						<xsl:if test="f:num(price) &gt; 0">
							<p>
								Цена:
								<b>
									<xsl:value-of select="price"/>
								</b>
							</p>
						</xsl:if>
						<xsl:if test="option">
							<p>
								Дополнительные опции:
							</p>
							<ul>
								<xsl:for-each select="option">
									<li>
										<xsl:value-of select="name"/>
									</li>
								</xsl:for-each>
							</ul>
						</xsl:if>
					</td>
					<td>
						<xsl:value-of select="qty_factory"/>
					</td>
					<td>
						<xsl:value-of select="qty_smolensk"/>
					</td>
					<td>
						<xsl:value-of select="qty_store"/>
					</td>
					<td>
						<xsl:value-of select="qty_reserve"/>
					</td>
					<td>
						<xsl:value-of select="qty"/>
					</td>
				</tr>
				<tr>
					<td colspan="6">
						<span class="toggle" onclick="$('#cmpl-{@id}').toggle()">Подробный список</span>
					</td>
				</tr>
				<tbody id="cmpl-{@id}" style="display: none;">
					<xsl:for-each select="base_complectation_product">
						<tr>
							<td>
								Серийный номер:
								<b>
									<xsl:value-of select="if(serial != '') then serial else 'N/A'"/>
								</b>
								<xsl:if test="reserve_time != ''">
									<br/>Зарезервирован:
									<b>
										<xsl:value-of select="reserve_time"/>
									</b>
								</xsl:if>
								<xsl:if test="stored_time != ''">
									<br/>На складе:
									<b>
										<xsl:value-of select="stored_time"/>
									</b>
								</xsl:if>
							</td>
							<td>
								<xsl:value-of select="qty_factory"/>
							</td>
							<td>
								<xsl:value-of select="qty_smolensk"/>
							</td>
							<td>
								<xsl:value-of select="qty_store"/>
							</td>
							<td>
								<xsl:value-of select="qty_reserve"/>
							</td>
							<td>
								<xsl:value-of select="qty"/>
							</td>
						</tr>
					</xsl:for-each>
				</tbody>
			</table>
		</div>
	</xsl:template>


	<xsl:template name="OPTIONS">
		<xsl:if test="$is_jur">
			<xsl:call-template name="OPTIONS_REGISTERED"/>
			<div id="on-update" ></div>
		</xsl:if>
		<xsl:if test="not($is_jur)">
			<xsl:call-template name="OPTIONS_GUEST"/>
		</xsl:if>
	</xsl:template>

	<xsl:template name="OPTIONS_GUEST">
		<div class="device-basic complectations" id="cmpl-form" data-price="{f:num($p/price)}">
			<div class="complectation device-basic__column" style="flex: 0 0 50%;">
				<xsl:if test="$p/option">
					<h3>Опции</h3>
					<table style="width:100%" class="options-table">
						<tr>
							<th></th>
							<th style="text-align: left"></th>
							<th style="text-align: left">Цена</th>
						</tr>
						<xsl:for-each select="$p/option">
							<tr>
								<td>
									<input name="option" class="option-cb" type="checkbox" data-name="{name}"
										   data-price="{f:num(price)}" id="cb-{@id}"/>
								</td>
								<td style="padding:0">
									<label for="cb-{@id}">
										<xsl:value-of select="name"/>
									</label>
								</td>
								<td style="padding:0; border-left: 0.5px solid #f0f0f0;">
									<label for="cb-{@id}">
										<xsl:value-of select="price"/>
									</label>
								</td>
							</tr>
						</xsl:for-each>
					</table>
					<xsl:call-template name="OPTIONS_SCRIPT"/>
				</xsl:if>
			</div>
			<xsl:if test="$p/@type = 'complex_product'">
				<div class="complectation device-basic__column" style="flex: 0 0 50%;">
					<xsl:if test="$p/option">
						<div class="wide-only">
							<h3 class="desktop-only" style="margin-bottom:12px">Выбранная комплектация</h3>
							<div>
								<table style="width:100%" class="options-table" id="selected_options"></table>
							</div>
						</div>
					</xsl:if>
					<table>
						<tr>
							<td class="total">Сумма:
								<b id="sum">
									<xsl:value-of select="$p/price"/>
								</b>
							</td>
							<td style="text-align:right;">
								<button class="button button_size_lg"
										style="background: #ccc; color: #363636; border-color: #ccc;">Заказ доступен после
									регистрации
								</button>
							</td>
						</tr>
					</table>
				</div>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template name="OPTIONS_REGISTERED">
		<form method="POST" ajax="true" action="{$p/to_cart_pre_order}" data-price="{f:num($p/price)}" id="cmpl-form">
			<div class="device-basic complectations">
				<div class="complectation device-basic__column" style="flex: 0 0 50%;">
					<xsl:if test="$p/option">
						<h3>Опции</h3>
						<table style="width:100%" class="options-table">
							<tr>
								<th></th>
								<th style="text-align: left"></th>
								<th style="text-align: left">Цена</th>
							</tr>
							<xsl:for-each select="$p/option">
								<tr>
									<td>
										<input name="option" class="option-cb" type="checkbox" value="{@id}"
											   data-name="{name}" data-price="{f:num(price)}" id="cb-{code}"/>
									</td>
									<td style="padding:0">
										<label for="cb-{code}">
											<xsl:value-of select="name"/>
										</label>
									</td>
									<td style="padding:0; border-left: 0.5px solid #f0f0f0;">
										<label for="cb-{code}">
											<xsl:value-of select="price"/>
										</label>
									</td>
								</tr>
							</xsl:for-each>
						</table>
						<xsl:call-template name="OPTIONS_SCRIPT"/>
					</xsl:if>
				</div>
				<div class="complectation device-basic__column">
					<xsl:if test="$p/option">
						<div class="wide-only">
							<h3 class="desktop-only" style="margin-bottom:12px">Выбранная комплектация</h3>
							<div>
								<table style="width:100%" class="options-table" id="selected_options"></table>
							</div>
						</div>
					</xsl:if>
					<table>
						<tr>
							<td colspan="2">
								Название комплектации
								<!-- ID of selected complectation for editing it's name -->
								<input type="hidden" name="complectation_id" id="cmp-id-{$p/code}"/>
								<div class="combobox">
									<select onchange="selectExistingComplectation(this)"
											id="existing-complectations-{$p/code}">
										<!-- Existing complectation names loaded by ajax -->
										<option value=""></option>
									</select>
									<input class="field" type="text" name="сomplectation_name"
										   placeholder="Новая комплектация"/>
								</div>
							</td>
						</tr>
						<tr>
							<td class="total">Сумма:
								<b id="sum">
									<xsl:value-of select="$p/price"/>
								</b>
							</td>
							<td style="text-align:right;">
								<button class="button button_size_lg" type="submit" id="sbmt">Предзаказ</button>
							</td>
						</tr>
					</table>
					<div id="scripts"></div>
				</div>
			</div>
		</form>
	</xsl:template>

	<xsl:template name="OPTIONS_SCRIPT">
		<script type="text/javascript">
			$(document).ready(function(){

			insertAjax('<xsl:value-of select="$p/complect_ajax_link"/>');

			$optionCb = $("#cmpl-form").find(".option-cb");
			$sum = $("#sum");
			update();
			$optionCb.change(function(e){update();});

			});

			function selectExistingComplectation(el){
				$textInp = el.nextElementSibling;
				txt = el.value == ""? "" : $(el).find('option:selected').text();
				$textInp.value = txt;
				$('#cmp-id-<xsl:value-of select="$p/code"/>').val(el.value); selectOptions(el.value);
				selectOptions(el.value);
			}

			function update(){
			var sum = 0;
			var updatedSelection = $("&lt;table&gt;");

			for(i = 0; i &lt; $optionCb.length; i++){
			var $current = $($optionCb.get(i));
			if($current.is(":checked")){
			sum += $current.attr("data-price") * 1;

			$tr = $("&lt;tr&gt;");
			$tr.append($("&lt;td&gt;", {"html" : $current.attr("data-name")}));
			$tr.append($("&lt;td&gt;", {"html" : $current.attr("data-price").toLocaleString('ru-RU'), "style" :
			"font-weight: bold"}));
			updatedSelection.append($tr);
			}
			}
			$("#selected_options").html(updatedSelection.html());
			sum += $("#cmpl-form").attr("data-price") * 1;
			sum = sum.toLocaleString('ru-RU');
			$sum.html(sum);
			}

			function selectOptions(value){
			$(".options-table").find(":checked").each(function(i){
			this.checked = false;
			});
			if(typeof value != undefined &amp;&amp; value.length &gt; 0){
				c = copmlectationOptions[value];
				for(i = 0; i &lt; c.length; i++ ){
					document.getElementById(c[i]).checked = true;
				}
				$("#sbmt").text("Сохранить изменения");
			}else{
				$("#sbmt").text("Предзаказ");
			}
			update();
			}

		</script>
	</xsl:template>


</xsl:stylesheet>
