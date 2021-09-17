<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<!-- ****************************    УПРАВЛЯЮЩИЕ КОНСТАНТЫ    ******************************** -->

	<xsl:variable name="MIN_PAGE_COUNT" select="6"/>

	<!-- ****************************    ПЕРЕМЕННЫЕ    ******************************** -->

	<xsl:variable name="title" select="'Лучшие цены'"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>

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
	<xsl:variable name="view" select="$pv/view"/>
	<xsl:variable name="tag" select="$pv/tag"/>
	<xsl:variable name="tag1" select="$pv/tag1"/>
	<xsl:variable name="tag2" select="$pv/*[starts-with(name(), 'tag2')]"/>
	<xsl:variable name="not_found" select="$tag1 and not($sel_sec/product)"/>
	<xsl:variable name="products" select="page/product"/>
	<xsl:variable name="only_available" select="$pv/minqty = '0'"/>

	<xsl:variable name="user_filter" select="$pv/fil[input]"/>
	<xsl:variable name="show_filter" select="$pv/show_filter = 'yes'"/><!--$user_filter or $tag-->

	<xsl:template name="PAGE_PATH">
		<div class="path path_common" style="{if(page/@name = 'catalog') then 'display: none;' else ''}">
			<div class="path__item">
				<a class="path__link" href="{$main_host}">Главная страница</a>
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
		<xsl:if test="page/product_pages">
			<xsl:variable name="total_pages" select="count(page/product_pages/page)"/>
			<div class="pagination{' pagination_short'[$total_pages &lt;= $MIN_PAGE_COUNT]}">
				<div class="pagination__label">Страницы:</div>
				<div class="pagination__wrap">
					<xsl:if test="$products/product_pages/previous">
						<div class="pagination__item">
							<a href="{$products/product_pages/previous[1]/link}">&lt;</a>
						</div>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="$total_pages &gt; $MIN_PAGE_COUNT">
							<xsl:apply-templates select="$products/product_pages/page[1]" mode="pagination"/>
							<xsl:if test="$page_num = (1, 2, 3)">
								<xsl:apply-templates select="$products/product_pages/page[position() = (2, 3)]" mode="pagination"/>
							</xsl:if>
							<xsl:if test="$page_num &gt; 3">
								<div class="pagination__dots">...</div>
							</xsl:if>
							<xsl:if test="$page_num &gt; 3 and $page_num &lt; $total_pages">
								<xsl:apply-templates select="$products/product_pages/page[$page_num]" mode="pagination"/>
							</xsl:if>
							<xsl:if test="$page_num &lt; $total_pages - 1">
								<div class="pagination__dots">...</div>
							</xsl:if>
							<xsl:apply-templates select="$products/product_pages/page[last()]" mode="pagination"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates select="$products/product_pages/page" mode="pagination"/>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:if test="$sel_sec/product_pages/next">
						<div class="pagination__item">
							<a href="{$products/product_pages/next[1]/link}">&gt;</a>
						</div>
					</xsl:if>
				</div>
			</div>
		</xsl:if>
	</xsl:template>



	<xsl:template name="CONTENT_INNER">
		<xsl:call-template name="TAGS"/>
		
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
					<xsl:for-each select="$fil/input">
						<xsl:variable name="input" select="."/>
						<xsl:variable name="filled" select="$user_filter/input[@id = $input/@id]"/>
						<xsl:if test="$filled">
							<xsl:variable name="tokens" select="tokenize($input/@caption, ',')"/>
							<xsl:variable name="unit" select="if (count($tokens) &gt; 1) then normalize-space($tokens[2]) else ''"/>
							<span style="font-size: 13px; padding-top: 5px; padding-right: 3px; padding-left: 5px; color: #438539;">
								<xsl:value-of select="if ($unit) then substring-before(@caption, concat(', ', $unit)) else @caption" />:
							</span>
							<xsl:for-each select="$filled">
								<div class="filtered__item filtered-item">
									<div class="filtered-item__label"><xsl:value-of select="."/>&#160;<xsl:value-of select="$unit"/></div>
									<a class="filtered-item__close" href="#" rel="inp{$input/@id}{.}">×</a>
								</div>
							</xsl:for-each>
						</xsl:if>
					</xsl:for-each>
<!--					<xsl:for-each select="$user_filter/input">-->
<!--						<xsl:variable name="id" select="@id"/>-->
<!--						<xsl:variable name="input" select="$fil/input[@id = $id]"/>-->
<!--						<xsl:variable name="tokens" select="tokenize($input/@caption, ',')"/>-->
<!--						<xsl:variable name="unit" select="if (count($tokens) &gt; 1) then normalize-space($tokens[2]) else ''"/>-->
<!--						<div class="filtered__item filtered-item">-->
<!--							<div class="filtered-item__label"><xsl:value-of select="."/>&#160;<xsl:value-of select="$unit"/></div>-->
<!--							<a class="filtered-item__close" href="#" rel="inp{$id}{.}">×</a>-->
<!--						</div>-->
<!--					</xsl:for-each>-->
				</div>
			</div>
			<div class="devices">
				<xsl:if test="$view = 'table'">
					<div class="devices__wrap">
						<xsl:apply-templates select="$products"/>
					</div>
				</xsl:if>
				<xsl:if test="$view = 'list'">
					<table class="devices__wrap_rows">
						<tbody>
							<xsl:apply-templates select="$products" mode="lines"/>
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
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
	</xsl:template>

	<xsl:template match="tag">
		<xsl:if test="current() != $pv/tag">
			<xsl:variable name="active" select="current()/tag = $tag"/>
			<a href="{if ($active) then remove_tag else add_tag}" class="labels__item label{' labels__item_active'[$active]}"><xsl:value-of select="tag"/></a>
		</xsl:if>
	</xsl:template>

	<xsl:template name="TAGS"></xsl:template>
	

</xsl:stylesheet>