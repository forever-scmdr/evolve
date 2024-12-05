<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<!-- ****************************    УПРАВЛЯЮЩИЕ КОНСТАНТЫ    ******************************** -->

	<xsl:variable name="MIN_PAGE_COUNT" select="6"/>

	<!-- ****************************    ПЕРЕМЕННЫЕ    ******************************** -->

	<xsl:variable name="query" select="$pv/q"/>
	<xsl:variable name="sec" select="$pv/sec"/>
	<xsl:variable name="query_sec" select="page/catalog//section[code = /page/variables/sec]"/>

	<xsl:variable name="title" select="'Поиск'"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>

	<xsl:variable name="main_menu_section" select="page/catalog//section[@id = $sel_sec_id]"/>
	<xsl:variable name="subs" select="$main_menu_section/section"/>
	<xsl:variable name="show_devices" select="$sel_sec/show_devices = '1' or not($subs)"/>

	<xsl:variable name="default_sub_view" select="if($show_devices) then 'tags' else 'pics'"/>

	<xsl:variable name="sub_view" select="if($sel_sec/sub_view != '') then $sel_sec/sub_view else $default_sub_view"/>
	<xsl:variable name="is_manual_filter_on" select="page/optional_modules/display_settings/manual_filter_params = 'on'"/>
	<xsl:variable name="page_num" select="number($pv/page)"/>

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
	<xsl:variable name="view" select="$pv/view"/>
	<xsl:variable name="tag" select="$pv/tag"/>
	<xsl:variable name="tag1" select="$pv/tag1"/>
	<xsl:variable name="tag2" select="$pv/*[starts-with(name(), 'tag2')]"/>
	<xsl:variable name="not_found" select="$tag1 and not($sel_sec/product)"/>
	<xsl:variable name="products" select="$sel_sec/product or $not_found"/>
	<xsl:variable name="canonical" select="$sel_sec/canonical_link"/>

	<xsl:variable name="user_filter" select="$pv/fil[input]"/>
	<xsl:variable name="show_filter" select="$user_filter or $tag"/>
	<xsl:variable name="is_search_strict" select="$pv/search = 'strict'"/>

	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a class="path__link" href="{$main_host}">Главная страница</a>
				<div class="path__arrow"></div>
				<a class="path__link" href="{page/catalog_link}">Каталог</a>
				<div class="path__arrow"></div>
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




	<xsl:template name="CONTENT">
		<div class="content__main">
			<xsl:call-template name="PAGE_PATH"/>
			<xsl:variable name="has_result" select="not(not(/page/product))"/>
			<xsl:if test="$is_search_strict and $has_result">
				<div class="title title_1">Найдено по запросу "<xsl:value-of select="$query"/>"</div>
			</xsl:if>
			<xsl:if test="not($is_search_strict) and $has_result">
				<div class="title title_1">Найдено по запросу "<xsl:value-of select="$query"/>". Включая похожие варианты</div>
			</xsl:if>
			<xsl:if test="not(/page/product)">
				<!--
				<xsl:if test="$is_search_strict and not(/page/@name = 'search_strict')"><xsl:value-of select="/page/@name"/>
					<script>
						window.location.replace("<xsl:value-of select="page/base"/>/" + "<xsl:value-of select="page/fuzzy_search_link"/>");
					</script>
				</xsl:if>
				-->
				<xsl:if test="not($is_search_strict) or (/page/@name = 'search_strict' and not(/page/product))">
					<div class="title title_1">По запросу "<xsl:value-of select="$query"/>" ничего не найдено</div>
				</xsl:if>
			</xsl:if>
			<xsl:if test="$seo[1]/text">
				<div class="section-text">
					<xsl:value-of select="$seo[1]/text" disable-output-escaping="yes"/>
				</div>
			</xsl:if>
			<xsl:if test="/page/product">
				<div class="search-links">
					<div class="search-links__item search-link{' search-link_active'[not($query_sec)]}" style="{'font-weight: bold'[$query_sec]}">
						<a href="{page/base_search_link}">Найдено товаров</a>
						<span>(<xsl:value-of select="count(page/product)"/>)</span>
					</div>
					<xsl:for-each-group select="/page/product/section" group-by="code">
						<xsl:sort select="count(current-group())" order="descending"/>
						<xsl:variable name="cur_sec" select="current-group()[1]"/>
						<xsl:variable name="selected" select="$query_sec/code = $cur_sec/code"/>
						<div class="search-links__item search-link{' search-link_active'[$selected]}">
							<a href="{$cur_sec/show_sec}"><xsl:value-of select="$cur_sec/name"/></a>
							<a href="{$cur_sec/show_products}" title="Перейти в указанный раздел">
								<img src="img/icon-goto.png" alt=""/>
							</a>
							<span>(<xsl:value-of select="count(current-group())"/>)</span>
						</div>
					</xsl:for-each-group>
				</div>
			</xsl:if>
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
							<option value="available2DESC" link="{page/set_sort_relevance}">Без сортировки</option>
							<option value="available2DESCnameASC" link="{page/set_sort_available2}">По наличию</option>
							<option value="availableDESCpriceASC" link="{page/set_sort_price_asc}">Сначала дешевые</option>
							<option value="availableDESCpriceDESC" link="{page/set_sort_price_desc}">Сначала дорогие</option>
							<option value="nameASCavailableDESC" link="{page/set_sort_name_asc}">По алфавиту А→Я</option>
							<option value="nameDESCavailableDESC" link="{page/set_sort_name_desc}">По алфавиту Я→А</option>
						</select>
					</div>
				</xsl:if>
				<div class="view__column">
					<xsl:call-template name="PAGINATION"/>
				</div>
<!--				<div class="view__column">-->
<!--					<span></span>-->
<!--					<select value="{page/variables/limit}" onchange="window.location.href = $(this).find(':selected').attr('link')">-->
<!--						<xsl:for-each select="/page/*[starts-with(name(), 'set_limit_')]">-->
<!--							<xsl:variable name="nos" select="tokenize(name(), '_')[3]"/>-->
<!--							<option value="{$nos}" link="{.}">-->
<!--								<xsl:value-of select="$nos"/>-->
<!--							</option>-->
<!--						</xsl:for-each>-->
<!--					</select>-->
<!--				</div>-->
			</div>
			<div class="devices">
				<xsl:if test="$view = 'table'">
					<div class="devices__wrap">
						<xsl:apply-templates select="if($query_sec) then /page/product[section/code = $sec] else /page/product"/>
					</div>
				</xsl:if>
				<xsl:if test="$view = 'list'">
					<table class="devices__wrap_rows">
						<tbody>
							<xsl:apply-templates select="if($query_sec) then /page/product[section/code = $sec] else /page/product" mode="lines"/>
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


</xsl:stylesheet>