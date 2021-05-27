<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<!-- ****************************    УПРАВЛЯЮЩИЕ КОНСТАНТЫ    ******************************** -->

	<xsl:variable name="MIN_PAGE_COUNT" select="6"/>

	<!-- ****************************    ПЕРЕМЕННЫЕ    ******************************** -->

	<xsl:variable name="title" select="if($tag[1] != '') then concat($sel_sec/name, ' - ', $tag[1]) else $sel_sec/name"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>

	<xsl:variable name="main_menu_section" select="page/catalog//section[@id = $sel_sec_id]"/>
	<xsl:variable name="subs" select="$main_menu_section/section"/>
	<xsl:variable name="show_devices" select="$sel_sec/show_devices = '1' or not($subs)"/>

	<xsl:variable name="default_sub_view" select="if($show_devices) then 'tags' else 'pics'"/>

	<xsl:variable name="sub_view" select="if($sel_sec/sub_view != '') then $sel_sec/sub_view else $default_sub_view"/>
	<xsl:variable name="is_manual_filter_on" select="page/optional_modules/display_settings/manual_filter_params = 'on'"/>
	<xsl:variable name="page_num" select="number(page/variables/page)"/>

	<xsl:variable name="pv" select="page/variables"/>

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
	<xsl:variable name="view" select="page/variables/view"/>
	<xsl:variable name="tag" select="page/variables/tag"/>
	<xsl:variable name="tag1" select="page/variables/tag1"/>
	<xsl:variable name="tag2" select="page/variables/*[starts-with(name(), 'tag2')]"/>
	<xsl:variable name="not_found" select="$tag1 and not($sel_sec/product)"/>
	<xsl:variable name="products" select="$sel_sec/product or $not_found"/>
	<xsl:variable name="only_available" select="page/variables/minqty = '0'"/>
	<xsl:variable name="canonical" select="$sel_sec/canonical_link"/>

	<xsl:variable name="user_filter" select="page/variables/fil[input]"/>
	<xsl:variable name="show_filter" select="$user_filter or $tag"/>

	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a class="path__link" href="{$main_host}">Главная страница</a>
				<div class="path__arrow"></div>
				<a class="path__link" href="{page/catalog_link}">Каталог</a>
				<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id and @id != $sel_sec_id]">
					<div class="path__arrow"></div>
					<a class="path__link" href="{show_products}">
						<xsl:value-of select="name"/>
					</a>
				</xsl:for-each>
			</div>
		</div>
	</xsl:template>


	<xsl:template match="page" mode="pagination">
		<div class="pagination__item">
			<a href="{link}"><xsl:value-of select="number"/></a>
		</div>
	</xsl:template>


	<xsl:template match="page[@current]" mode="pagination">
		<div class="pagination__item pagination__item_active">
			<a href="{link}"><xsl:value-of select="number"/></a>
			<div class="pagination__dd pag-dd">
				<div class="pag-dd__container">
					<xsl:for-each select="$sel_sec/product_pages/page">
						<a class="pag-dd__item" href="{link}"><xsl:value-of select="number"/></a>
					</xsl:for-each>
				</div>
			</div>
		</div>
	</xsl:template>


	<xsl:template name="PAGINATION">
		<xsl:if test="$sel_sec/product_pages and $show_devices">
			<xsl:variable name="total_pages" select="count($sel_sec/product_pages/page)"/>
			<div class="pagination{' pagination_short'[$total_pages &lt;= $MIN_PAGE_COUNT]}">
				<div class="pagination__label">Страницы:</div>
				<div class="pagination__wrap">
					<xsl:choose>
						<xsl:when test="$total_pages &gt; $MIN_PAGE_COUNT">
							<xsl:apply-templates select="$sel_sec/product_pages/page[1]" mode="pagination"/>
							<xsl:if test="$page_num = (1, 2, 3)">
								<xsl:apply-templates select="$sel_sec/product_pages/page[position() = (2, 3)]" mode="pagination"/>
							</xsl:if>
							<xsl:if test="$page_num &gt; 3">
								<div class="pagination__dots">...</div>
							</xsl:if>
							<xsl:if test="$page_num &gt; 3 and $page_num &lt; $total_pages">
								<xsl:apply-templates select="$sel_sec/product_pages/page[$page_num]" mode="pagination"/>
							</xsl:if>
							<xsl:if test="$page_num &lt; $total_pages - 1">
								<div class="pagination__dots">...</div>
							</xsl:if>
							<xsl:apply-templates select="$sel_sec/product_pages/page[last()]" mode="pagination"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="$sel_sec/product_pages/page" mode="pagination"/>
						</xsl:otherwise>
					</xsl:choose>
				</div>
			</div>
		</xsl:if>
	</xsl:template>



	<xsl:template name="FILTER">
		<xsl:variable name="valid_inputs" select="$sel_sec/params_filter/filter/input[count(domain/value) &gt; 1]"/>
		<xsl:variable name="user_defined_params" select="tokenize($sel_sec/extra, '[\|;]\s*')"/>
		<xsl:variable name="is_user_defined" select="$sel_sec/extra and not($sel_sec/extra = '') and count($user_defined_params) &gt; 0"/>
		<xsl:variable name="captions" select="if ($is_user_defined) then $user_defined_params else $valid_inputs/@caption"/>
		<xsl:if test="not($subs) and $valid_inputs">
			<div class="filter filter_section">
<!--				<a href="#" onclick="$('#filters_container').slideToggle(200);return false;" class="icon-link filter__button button">-->
<!--					<div class="icon">-->
<!--						<img src="img/icon-gear.svg" alt="" />-->
<!--					</div>-->
<!--					<span class="icon-link__item">Подбор по параметрам</span>-->
<!--				</a>-->
				<form method="post" action="{$sel_sec/filter_base_link}">
					<div class="filter__wrap" style="{'display: none'[not($show_filter)]}" id="filters_container">
						<xsl:for-each select="$captions">
							<xsl:variable name="input" select="$valid_inputs[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()))]"/>
							<xsl:if test="$input">
								<xsl:variable name="name" select="$input/@id"/>
								<div class="filter__item{' active'[$user_filter/input[@id = $name]]}">
									<div class="filter__title"><xsl:value-of select="$input/@caption"/></div>
									<div class="filter__values">
										<xsl:for-each select="$input/domain/value">
											<div class="filter__value">
												<label>
													<input name="{$name}" type="checkbox" value="{.}" rel="inp{$name}{.}">
														<xsl:if test=". = $user_filter/input[@id = $name]">
															<xsl:attribute name="checked" select="'checked'"/>
														</xsl:if>
														&#160;<xsl:value-of select="."/>
													</input>
												</label>
											</div>
										</xsl:for-each>
									</div>
									<xsl:if test="$input/criteria/@type = ('double', 'integer')">
										<div class="filter__control ltgt">
											<label>
												&lt;<input class="lt" type="radio"/>
											</label>
											<label>
												&gt;<input class="gt" type="radio"/>
											</label>
											<label>×
												<input class="x" type="radio"/>
											</label>
										</div>
									</xsl:if>
								</div>
							</xsl:if>
						</xsl:for-each>
						<div class="filter__item{' active'[$tag]}">
							<div class="filter__title">Производитель</div>
							<div class="filter__values">
								<xsl:for-each select="$sel_sec/tag">
									<div class="filter__value">
										<label>
											<input name="tag" type="checkbox" value="{vendor}" rel="prod{vendor}">
												<xsl:if test="vendor = $tag">
													<xsl:attribute name="checked" select="'checked'"/>
												</xsl:if>
												&#160;<xsl:value-of select="vendor"/>
											</input>
										</label>
									</div>
								</xsl:for-each>
							</div>
						</div>
					</div>
					<!-- фильтры (ничего не выбрано)-->
					<div class="filter__actions filter_hidden" style="{'display: none'[$show_filter]}">
						<button class="button button_disabled" disabled="disabled" type="button">Применить фильтры</button>
						<button class="button button_disabled" disabled="disabled" type="button">Очистить фильтры</button>
						<button class="button button_secondary" type="button" onclick="showSectionFilter()">Показать фильтры</button>
					</div><!-- фильтры (выбраны, но не применены)-->
					<div class="filter__actions filter_visible" style="{'display: none'[not($show_filter)]}">
						<button class="button button_disabled apply_filter" type="submit" disabled="disabled">Применить фильтры</button>
						<button class="button{' button_disabled'[not($show_filter)]}" onclick="location.href = '{page/reset_filter_link}'; return false;" type="button">
							Очистить фильтры
						</button>
						<button class="button button_secondary" type="button" onclick="hideSectionFilter()">Скрыть фильтры</button>
					</div>
				</form>
			</div>
			<script>
				function showSectionFilter() {
					$('.filter_hidden').hide();
					$('.filter_visible').show();
					$('#filters_container').show('blind', 200);
				}

				function hideSectionFilter() {
					$('.filter_hidden').show();
					$('.filter_visible').hide();
					$('#filters_container').hide('blind', 200);
				}

				$(document).ready(function() {
					$('#filters_container').find('input').change(function() {
						$('.apply_filter').removeClass('button_disabled');
						$('.apply_filter').attr("disabled", false);
					});

					$('.ltgt').find('input').change(function() {
						$(this).closest('label').siblings('label').find('input').prop( "checked", false);
						if ($(this).hasClass('lt')) {
							$(this).closest('.filter__item').find('input:checked').closest('div').first().nextAll().find('input').prop("checked", false);
							$(this).closest('.filter__item').find('input:checked').closest('div').first().prevAll().find('input').prop("checked", true);
						} else if ($(this).hasClass('gt')) {
							$(this).closest('.filter__item').find('input:checked').closest('div').first().prevAll().find('input').prop("checked", false);
							$(this).closest('.filter__item').find('input:checked').closest('div').first().nextAll().find('input').prop("checked", true);
						} else {
							$(this).closest('.filter__item').find('input:checkbox').prop("checked", false);
						}
					});

					$('.filter__values').find('input').change(function() {
						$(this).closest('.filter__item').find('.ltgt').find('input').prop("checked", false);
					});


					$('.filtered').find('a').click(function(e){
						e.preventDefault();
						var rel = $(this).attr('rel');
						$('#filters_container').find('input[rel="' + rel + '"]').prop("checked", false);
						$('#filters_container').closest('form').submit();
					});
				});
			</script>
		</xsl:if>
	</xsl:template>




	<xsl:template name="CONTENT">
		<div class="content__main">
			<xsl:call-template name="PAGE_PATH"/>
			<xsl:call-template name="PAGE_HEADING"/>
			<xsl:if test="$seo[1]/text">
				<div class="section-text">
					<xsl:value-of select="$seo[1]/text" disable-output-escaping="yes"/>
				</div>
			</xsl:if>
			<xsl:call-template name="FILTER"/>
			<div class="view view_section">
				<div class="view__column toggle-view">
					<span>Вид:</span>
					<a href="{page/set_view_table}">
						<img src="img/icon-view-cards{'-active'[$view = 'table']}.jpg" alt=""/>
					</a>
					<a href="{page/set_view_list}">
						<img src="img/icon-view-rows{'-active'[$view = 'list']}.jpg" alt=""/>
					</a>
				</div>
				<xsl:if test="/page/@name != 'fav'">
					<div class="view__column">
						<span>Сортировка:</span>
						<select value="{$pv/sort1}{$pv/dir1}{$pv/sort2}{$pv/dir2}" onchange="window.location.href = $(this).find(':selected').attr('link')">
							<option value="availableDESCnameASC" link="{page/set_sort_available}">По наличию</option>
							<option value="availableDESCpriceASC" link="{page/set_sort_price_asc}">Сначала дешевые</option>
							<option value="availableDESCpriceDESC" link="{page/set_sort_price_desc}">Сначала дорогие</option>
							<option value="nameASCname_extraASC" link="{page/set_sort_name_asc}">По алфавиту А→Я</option>
							<option value="nameDESCname_extraASC" link="{page/set_sort_name_desc}">По алфавиту Я→А</option>
						</select>
					</div>
				</xsl:if>
				<div class="view__column">
					<xsl:call-template name="PAGINATION"/>
				</div>
				<div class="view__column">
					<span></span>
					<select value="{page/variables/limit}" onchange="window.location.href = $(this).find(':selected').attr('link')">
						<xsl:for-each select="/page/*[starts-with(name(), 'set_limit_')]">
							<xsl:variable name="nos" select="tokenize(name(), '_')[3]"/>
							<option value="{$nos}" link="{.}">
								<xsl:value-of select="$nos"/>
							</option>
						</xsl:for-each>
					</select>
				</div>
			</div>
			<div class="filtered">
				<div class="filtered__wrap">
					<xsl:for-each select="$tag">
						<div class="filtered__item filtered-item">
							<div class="filtered-item__label"><xsl:value-of select="."/></div>
							<a class="filtered-item__close" href="" rel="prod{.}">×</a>
						</div>
					</xsl:for-each>
					<xsl:variable name="fil" select="$sel_sec/params_filter/filter"/>
					<xsl:for-each select="$user_filter/input">
						<xsl:variable name="id" select="@id"/>
						<xsl:variable name="input" select="$fil/input[@id = $id]"/>
						<xsl:variable name="tokens" select="tokenize($input/@caption, ',')"/>
						<xsl:variable name="unit" select="if (count($tokens) &gt; 1) then normalize-space($tokens[2]) else ''"/>
						<div class="filtered__item filtered-item">
							<div class="filtered-item__label"><xsl:value-of select="."/>&#160;<xsl:value-of select="$unit"/></div>
							<a class="filtered-item__close" href="#" rel="inp{$id}{.}">×</a>
						</div>
					</xsl:for-each>
				</div>
			</div>
			<div class="devices">
				<xsl:if test="$view = 'table'">
					<div class="devices__wrap">
						<xsl:apply-templates select="$sel_sec/product"/>
					</div>
				</xsl:if>
				<xsl:if test="$view = 'list'">
					<table class="devices__wrap_rows">
						<tbody>
							<xsl:apply-templates select="$sel_sec/product" mode="lines"/>
						</tbody>
					</table>
				</xsl:if>

			</div>
			<xsl:call-template name="PAGINATION"/>
			<xsl:if test="$seo[1]/bottom_text">
				<div class="section-text" style="clear: both; margin-top: 30px;">
					<xsl:value-of select="$seo[1]/bottom_text" disable-output-escaping="yes"/>
				</div>
			</xsl:if>
		</div>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
	</xsl:template>

	<xsl:template match="tag">
		<xsl:variable name="active" select="current()/tag = $tag"/>
		<a href="{if ($active) then remove_tag else add_tag}" class="labels__item label{' labels__item_active'[$active]}"><xsl:value-of select="tag"/></a>
	</xsl:template>

	<xsl:template name="TAGS">
		<xsl:if test="$subs or $sel_sec/tag">
			<xsl:if test="$show_devices">
				<div class="labels labels_section">
					<xsl:apply-templates select="$sel_sec/tag"/>
				</div>
			</xsl:if>
			<xsl:if test="not($sel_sec/show_subs = '0')">
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


	<xsl:template match="section" mode="tag">
		<a href="{show_products}" class="labels__item label"><xsl:value-of select="name"/></a>
	</xsl:template>

	<xsl:template match="section" mode="pic">
		<div class="catalog-item">
			<xsl:variable name="sec_pic" select="if (main_pic != '') then concat(@path, main_pic) else ''"/>
			<xsl:variable name="product_pic" select="if (product/main_pic != '') then concat(product/@path, product/main_pic) else ''"/>
			<xsl:variable name="pic" select="if($sec_pic != '') then $sec_pic else if($product_pic != '') then $product_pic else 'img/no_image.png'"/>
			<div class="catalog-item__image img"><img src="{$pic}"  onerror="$(this).attr('src', 'img/no_image.png')" alt="{name}" /></div>
			<div class="catalog-item__info">
				<div class="catalog-item__title"><xsl:value-of select="name"/></div>
				<div class="catalog-item__text"><xsl:value-of select="short" disable-output-escaping="yes"/></div>
				<a href="{show_products}" class="catalog-item__link"></a>
			</div>
		</div>
	</xsl:template>

</xsl:stylesheet>