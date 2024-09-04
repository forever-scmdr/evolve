<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="if($tag[1] != '') then concat($sel_sec/name, ' - ', $tag[1]) else $sel_sec/name"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="custom_canonical" select="concat($main_host, tokenize($source_link, '\?')[1])"/>

	<xsl:variable name="main_menu_section" select="$catalog//section[@id = $sel_sec_id]"/>
	<xsl:variable name="subs" select="$main_menu_section[1]/section[f:num(hide) = 0]"/>

	<!-- Настройки каталога по отображению товаров и разделов (по умолчанию) -->
	<xsl:variable name="catalog_show_devices" select="$catalog/show_devices"/>
	<xsl:variable name="catalog_sub_view" select="$catalog/sub_view"/>
	<xsl:variable name="catalog_show_subs" select="$catalog/show_subs"/>
	<xsl:variable name="catalog_hide_side_menu" select="$catalog/hide_side_menu"/>
	<xsl:variable name="catalog_catalog_show_filter_default" select="$catalog/show_filter = 'да'"/>

	<!-- Настройки выбранного раздела по отображению товаров и разделов -->
	<xsl:variable name="section_show_devices" select="if ($sel_sec/show_devices and not($sel_sec/show_devices = '')) then $sel_sec/show_devices else $catalog_show_devices"/>
	<xsl:variable name="section_sub_view" select="if ($sel_sec/sub_view and not($sel_sec/sub_view = '')) then $sel_sec/sub_view else $catalog_sub_view"/>
	<xsl:variable name="section_show_subs" select="if ($sel_sec/show_subs and not($sel_sec/show_subs = '')) then $sel_sec/show_subs else $catalog_show_subs"/>
	<xsl:variable name="section_hide_side_menu" select="if ($sel_sec/hide_side_menu and not($sel_sec/hide_side_menu = '')) then $sel_sec/hide_side_menu else $catalog_hide_side_menu"/>

	<!-- Фактическое отображение товаров и разделов (зависит от настроек и самих данных) -->
	<xsl:variable name="show_devices" select="($section_show_devices = '1') or not($subs)"/>

	<xsl:variable name="default_sub_view" select="if($show_devices) then 'tags' else 'pics'"/>

	<xsl:variable name="sub_view" select="if($section_sub_view != '') then $section_sub_view else $default_sub_view"/>
	<xsl:variable name="is_manual_filter_on" select="page/optional_modules/display_settings/manual_filter_params = 'on'"/>
	<xsl:variable name="hide_side_menu" select="$section_hide_side_menu = '1'"/>


	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>

	<xsl:template name="MARKUP">
		<xsl:variable name="quote">"</xsl:variable>
		<xsl:variable name="min" select="f:currency_decimal(//min/price)"/>
		<xsl:variable name="max" select="f:currency_decimal(//max/price)"/>
		<script type="application/ld+json">
		{
			"@context": "http://schema.org/",
			"@type": "Product",
			"name": <xsl:value-of select="concat($quote, replace($sel_sec/name, $quote, ''), $quote)"/>,
			<xsl:if test="$sel_sec/main_pic != ''">
				"image": <xsl:value-of select="concat($quote, $main_host, '/', $sel_sec/@path, $sel_sec/main_pic, $quote)"/>,
			</xsl:if>
			"offers": {
				"@type": "AggregateOffer",
				"priceCurrency": "BYN",
				"lowPrice": <xsl:value-of select="concat($quote,$min, $quote)"/>,
				"highPrice": <xsl:value-of select="concat($quote, $max, $quote)"/>,
				"offerCount": <xsl:value-of select="concat($quote, $sel_sec/product_count, $quote)"/>
			},
			"aggregateRating": {
				"@type": "AggregateRating",
				"ratingValue": "4.9",
				"ratingCount": "53",
				"bestRating": "5",
				"worstRating": "1",
				"name": <xsl:value-of select="concat($quote, translate($sel_sec/name, $quote, ''), $quote)"/>
			}
		}
		</script>
	</xsl:template>

	<xsl:variable name="active_menu_item" select="'catalog'"/>

	<xsl:variable name="tag" select="page/variables/tag"/>
	<xsl:variable name="tag1" select="page/variables/tag1"/>
	<xsl:variable name="tag2" select="page/variables/*[starts-with(name(), 'tag2')]"/>
	<xsl:variable name="not_found" select="$tag1 and not($sel_sec/product)"/>
	<xsl:variable name="products" select="$sel_sec/product or $not_found"/>
	<xsl:variable name="only_available" select="page/variables/minqty = '0'"/>

	<xsl:variable name="user_filter" select="page/variables/fil[input]"/>


	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a class="path__link" href="{$main_host}">Главная страница</a>
				<div class="path__arrow"></div>
				<a class="path__link" href="{page/catalog_link}">Каталог</a>
				<xsl:for-each select="$catalog//section[.//@id = $sel_sec_id and @id != $sel_sec_id]">
					<div class="path__arrow"></div>
					<a class="path__link" href="{show_products}">
						<xsl:value-of select="name"/>
					</a>
				</xsl:for-each>
			</div>
		</div>

	</xsl:template>


	<xsl:template name="CONTENT">
		
		<xsl:if test="$seo[1]/text">
			<div class="section-text">
				<xsl:value-of select="$seo[1]/text" disable-output-escaping="yes"/>
			</div>
		</xsl:if>
		<xsl:call-template name="TAGS"/>
		<xsl:call-template name="FILTER"/>

		<!-- Отображние блоками/списком, товаров на страницу, сортировка, наличие -->
		<xsl:if test="$subs and $sub_view = 'pics' and $show_devices and not($section_show_subs = '0')">
			<div class="h3">&#160;</div><!--Товары-->
		</xsl:if>
		<xsl:call-template name="DISPLAY_CONTROL"/>
		
		<xsl:if test="$show_devices">
			<div class="devices devices_section{' lines'[$view = 'list']}">
				<xsl:if test="$view = 'table'">
					<div class="devices__wrap">
						<xsl:apply-templates select="$sel_sec/product" mode="product-table"/>
					</div>
				</xsl:if>
				<xsl:if test="$view = 'list'">
					<div class="devices__wrap devices__wrap_rows">
						<xsl:apply-templates select="$sel_sec/product" mode="product-list"/>
					</div>
				</xsl:if>
				<xsl:if test="$view = 'lines'">
					<xsl:if test="$sel_sec/product">
						<xsl:call-template name="LINES_TABLE">
							<xsl:with-param name="products" select="$sel_sec/product"/>
						</xsl:call-template>
					</xsl:if>
				</xsl:if>
				<xsl:if test="$not_found">
					<h4>По заданным критериям товары не найдены</h4>
				</xsl:if>
			</div>
		</xsl:if>

		<xsl:if test="$sel_sec/product_pages and $show_devices">
			<div class="pagination">
				<div class="pagination__label">Страницы:</div>
				<div class="pagination__wrap">
					<xsl:for-each select="$sel_sec/product_pages/page">
						<a href="{link}" class="pagination__item{' pagination__item_active'[current()/@current]}">
							<xsl:value-of select="number"/>
						</a>
					</xsl:for-each>
				</div>
			</div>
		</xsl:if>

		<xsl:if test="$seo[1]/bottom_text">
			<div class="section-text" style="clear: both; margin-top: 30px;">
				<xsl:value-of select="$seo[1]/bottom_text" disable-output-escaping="yes"/>
			</div>
		</xsl:if>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
	</xsl:template>

	<xsl:template match="tag">
		<xsl:variable name="active" select="current()/tag = $tag"/>
		<a href="{if ($active) then remove_tag else add_tag}" class="labels__item label{' labels__item_active'[$active]}"><xsl:value-of select="tag"/></a>
	</xsl:template>

	<xsl:template name="TAGS">
		<!-- VIEW TABLE -->

		<xsl:if test="$subs or $sel_sec/tag">
			<xsl:if test="$show_devices">
				<div class="labels labels_section">
					<xsl:apply-templates select="$sel_sec/tag"/>
				</div>
			</xsl:if>
			<xsl:if test="not($section_show_subs = '0')">
				<xsl:if test="$subs and $sub_view = 'tags'">
					<div class="labels labels_section">
						<xsl:apply-templates select="$subs" mode="tag"/>
					</div>
				</xsl:if>
				<xsl:if test="$subs and $sub_view = 'pics'">
					<div class="catalog-items">
						<div class="catalog-items__wrap">
							<xsl:apply-templates select="$subs" mode="pic"/>
						</div>
					</div>
				</xsl:if>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="FILTER">
		<xsl:variable name="valid_inputs" select="$sel_sec/params_filter/filter/input[count(domain/value) &gt; 1]"/>
		<xsl:variable name="user_defined_params" select="tokenize($sel_sec/extra, '[\|;]\s*')"/>
		<xsl:variable name="is_user_defined" select="$sel_sec/extra and not($sel_sec/extra = '') and count($user_defined_params) &gt; 0"/>
		<xsl:variable name="captions" select="if ($is_user_defined and $is_manual_filter_on) then $user_defined_params else $valid_inputs/@caption"/>
		<xsl:variable name="filter_is_open" select="$user_filter or $catalog_catalog_show_filter_default or //page/variables/show_filter = 'yes'"/>
		<xsl:if test="not($subs) and $valid_inputs">
			<div class="filter filter_section">
				<a href="#" onclick="$('.filter_extra').toggle();$('#filters_container').slideToggle(200);return false;" class="icon-link filter__button button">
					<div class="icon">
						<img src="img/icon-gear.svg" alt="" />
					</div>
					<span class="icon-link__item filter_extra" style="{'display: none'[$filter_is_open]}">Показать подбор по параметрам</span>
					<span class="icon-link__item filter_extra" style="{'display: none'[not($filter_is_open)]}">Скрыть подбор по параметрам</span>
				</a>
				<form method="post" action="{$sel_sec/filter_base_link}">
					<div class="filter__wrap" style="{'display: none'[not($filter_is_open)]}" id="filters_container">
						<xsl:for-each select="$captions">
							<xsl:variable name="input" select="$valid_inputs[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()))]"/>
							<xsl:if test="$input">
								<xsl:variable name="name" select="$input/@id"/>
								<div class="filter__item active checkgroup">
									<div class="filter__title"><xsl:value-of select="$input/@caption"/></div>
									<div class="filter__values">
										<xsl:for-each select="$input/domain/value">
											<div class="filter__value">
												<label>
													<input name="{$name}" type="checkbox" value="{.}">
														<xsl:if test=". = $user_filter/input[@id = $name]">
															<xsl:attribute name="checked" select="'checked'"/>
														</xsl:if>
														&#160;<xsl:value-of select="."/>
													</input>
												</label>
											</div>
										</xsl:for-each>
									</div>
								</div>
							</xsl:if>
						</xsl:for-each>
						<div class="filter__actions">
							<button class="button button_2" type="submit">Показать результат</button>
							<button class="button button_2" onclick="location.href = '{page/reset_filter_link}'; return false;">Сбросить</button>
						</div>
					</div>
				</form>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="DISPLAY_CONTROL">
		<xsl:if test="($show_devices and not($not_found) and $sel_sec/product) or (/page/@name = 'fav' and page/product)">
			<div class="view view_section">
				<div class="view__column">
					<xsl:if test="not($view_disabled = 'плитка')">
						<a href="{page/set_view_table}"  class="icon-link{' active'[$view = 'table']}" rel="nofollow">
							<div class="icon">
								<img src="img/icon-grid.svg" alt="" />
							</div>
							<span class="icon-link__item">Плиткой</span>
						</a>
					</xsl:if>
					<xsl:if test="not($view_disabled = 'список')">
						<a href="{page/set_view_list}" class="icon-link{' active'[$view = 'list']}" rel="nofollow">
							<div class="icon">
								<img src="img/icon-line.svg" alt="" />
							</div>
							<span class="icon-link__item">Строками</span>
						</a>
					</xsl:if>
					<xsl:if test="not($view_disabled = 'таблица')">
						<a href="{page/set_view_lines}" class="icon-link{' active'[$view = 'lines']}" rel="nofollow">
							<div class="icon">
								<img src="img/icon-lines.svg" alt="" />
							</div>
							<span class="icon-link__item">Таблица</span>
						</a>
					</xsl:if>
				</div>
				<xsl:if test="/page/@name != 'fav'">
					<div class="view__column">
						<select value="{page/variables/sort}{page/variables/direction}" onchange="window.location.href = $(this).find(':selected').attr('link')">
							<option value="ASC" link="{page/set_sort_default}">Без сортировки</option>
							<option value="priceASC" link="{page/set_sort_price_asc}">Сначала дешевые</option>
							<option value="priceDESC" link="{page/set_sort_price_desc}">Сначала дорогие</option>
							<option value="nameASC" link="{page/set_sort_name_asc}">По алфавиту А→Я</option>
							<option value="nameDESC" link="{page/set_sort_name_desc}">По алфавиту Я→А</option>
						</select>
					</div>
					<xsl:if test="$sel_sec/mark">
						<div class="view__column">
							<select value="{page/variables/mark}" onchange="window.location.href = $(this).find(':selected').attr('link')">
								<option value="" link="{page/set_mark_default}">Все товары</option>
								<xsl:for-each select="$sel_sec/mark">
									<option value="{mark}" link="{set_mark}"><xsl:value-of select="mark"/></option>
								</xsl:for-each>
							</select>
						</div>
					</xsl:if>
					<div class="view__column">
						Кол-во на странице:
						<select value="{page/variables/limit}" onchange="window.location.href = $(this).find(':selected').attr('link')">					
								<xsl:for-each select="/page/*[starts-with(name(), 'set_limit_')]">
									<xsl:variable name="nos" select="tokenize(name(), '_')[3]"/>
									<option value="{$nos}" link="{.}">
										<xsl:value-of select="$nos"/>
									</option>
								</xsl:for-each>	
						</select>
					</div>
				</xsl:if>
				<xsl:if test="/page/@name = 'fav'">
					<div class="view__column" style=""></div>
					<div class="view__column" style=""></div>
				</xsl:if>
			</div>

				<!-- <div class="view">
					<span class="{'active'[not($view = 'list')]}">
						
						<a href="{page/set_view_table}"><i class="fas fa-th-large"></i></a>
					</span>
					<span class="{'active'[$view = 'list']}">
						
						<a href="{page/set_view_list}"><i class="fas fa-th-list"></i></a>
					</span>
				</div> -->


				<!-- <div class="checkbox">
					<label>
						<xsl:if test="not($only_available)">
							<input type="checkbox"
								   onclick="window.location.href = '{page/show_only_available}'"/>
						</xsl:if>
						<xsl:if test="$only_available">
							<input type="checkbox" checked="checked"
								   onclick="window.location.href = '{page/show_all}'"/>
						</xsl:if>
						в наличии на складе
					</label>
				</div> -->
				
		</xsl:if>
	</xsl:template>

	<xsl:template match="section" mode="tag">
		<a href="{show_products}" class="labels__item label"><xsl:value-of select="name"/></a>
	</xsl:template>

	<xsl:template match="section" mode="pic">
		<div class="catalog-item">
			<xsl:variable name="sec_pic" select="if (main_pic != '') then concat(@path, main_pic) else ''"/>
			<xsl:variable name="product_pic" select="if (product/main_pic != '') then concat(product/@path, product/main_pic) else ''"/>
			<xsl:variable name="pic" select="if($sec_pic != '') then $sec_pic else if($product_pic != '') then $product_pic else 'img/no_image.png'"/>
			<div class="catalog-item__image img"><img src="{$pic}"  onerror="{$onerror}" alt="{name}" /></div>
			<div class="catalog-item__info">
				<div class="catalog-item__title"><xsl:value-of select="name"/></div>
				<div class="catalog-item__text"><xsl:value-of select="short" disable-output-escaping="yes"/></div>
				<a href="{show_products}" class="catalog-item__link"></a>
			</div>
		</div>
	</xsl:template>

</xsl:stylesheet>