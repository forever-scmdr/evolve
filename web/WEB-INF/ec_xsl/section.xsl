<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="if(not(section)) then concat($sel_sec/name, ' Метабо купить в Минске: цена') else $sel_sec/name" />
	<xsl:variable name="meta_description" select="if(not(section)) then concat($sel_sec/name, ' Метабо от официального дилера №1 ✅ Доставка по РБ ☎☎☎ +375 29 566 61 16 Отличная цена, гарантия 3 года! Рассрочка по Халве') else ''"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else concat($sel_sec/name,' Metabo')"/>
	<xsl:variable name="canonical" select="concat('/', $sel_sec/@key, '/')"/>

	<xsl:variable name="main_menu_section" select="page/catalog//section[@id = $sel_sec_id]"/>
	<xsl:variable name="subs" select="$main_menu_section/section"/>
	<xsl:variable name="show_devices" select="not($sel_sec/show_devices = '0') or not($subs)"/>

	<xsl:variable name="default_sub_view" select="'pics'"/>

	<xsl:variable name="sub_view" select="if($sel_sec/sub_view != '') then $sel_sec/sub_view else $default_sub_view"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>

	<xsl:template name="MARKUP">
		<script type="application/ld+json">
			<xsl:variable name="quote">"</xsl:variable>
			<xsl:variable name="min" select="f:currency_decimal(//min/price)"/>
			<xsl:variable name="max" select="f:currency_decimal(//max/price)"/>

			{
			"@context": "http://schema.org/",
			"@type": "Product",
			"name": <xsl:value-of select="concat($quote, replace($sel_sec/name, $quote, ''), $quote)" />,
			<xsl:if test="$sel_sec/main_pic != ''">
				"image": <xsl:value-of select="concat($quote, $base, '/', $sel_sec/@path, $sel_sec/main_pic, $quote)"/>,
			</xsl:if>
			"offers": {
			"@type": "AggregateOffer",
			"priceCurrency": "BYN",
			"lowPrice": <xsl:value-of select="concat($quote,$min, $quote)"/>,
			"highPrice": <xsl:value-of select="concat($quote, $max, $quote)"/>,
			"offerCount": <xsl:value-of select="concat($quote, count($sel_sec/product), $quote)"/>
			}, "aggregateRating": {
			"@type": "AggregateRating",
			"ratingValue": "4.9",
			"ratingCount": "53",
			"bestRating": "5",
			"worstRating": "1",
			"name": <xsl:value-of select="concat($quote, translate($sel_sec/name, $quote, ''), $quote)" />
			}
			}

		</script>
	</xsl:template>

	<xsl:variable name="active_menu_item" select="'catalog'"/>

	<xsl:variable name="view" select="if (page/variables/view) then page/variables/view else 'list'"/>
	<xsl:variable name="tag1" select="page/variables/tag1"/>
	<xsl:variable name="tag2" select="page/variables/*[starts-with(name(), 'tag2')]"/>
	<xsl:variable name="not_found" select="$tag1 and not($sel_sec/product)"/>
	<xsl:variable name="products" select="$sel_sec/product or $not_found"/>
	<xsl:variable name="only_available" select="page/variables/minqty = '1'"/>

	<xsl:variable name="user_filter" select="page/variables/fil[input]"/>

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
		<h1><xsl:value-of select="$h1"/></h1>
		<xsl:if test="$seo/text">
			<div class="page-content m-t">
				<xsl:value-of select="$seo/text" disable-output-escaping="yes"/>
			</div>
		</xsl:if>
		<div class="page-content m-t">

			<xsl:call-template name="TAGS"/>
			<xsl:if test="$show_devices">
				<xsl:if test="$subs and $sub_view = 'pics' and $show_devices and not($sel_sec/show_subs = '0')">
					<div class="h3">Товары</div>
				</xsl:if>
				<xsl:call-template name="DISPLAY_CONTROL"/>
				<div class="catalog-items{' lines'[$view = 'list']}">
					<xsl:apply-templates select="$sel_sec/product"/>
					<xsl:if test="$not_found">
						<h4>По заданным критериям товары не найдены</h4>
					</xsl:if>
				</div>
			</xsl:if>
		</div>

		<xsl:if test="$sel_sec/product_pages">
		<div class="pagination">
			<span>Страницы:</span>
			<div class="pagination-container">
				<xsl:for-each select="$sel_sec/product_pages/page">
					<a href="{replace(link, 'section/', '')}" class="{'active'[current()/@current]}"><xsl:value-of select="number"/></a>
				</xsl:for-each>
			</div>
		</div>
		</xsl:if>

		<xsl:if test="$seo/bottom">
			<div class="page-content">
				<xsl:value-of select="$seo/bottom" disable-output-escaping="yes"/>
			</div>
		</xsl:if>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
	</xsl:template>

	<xsl:template name="DISPLAY_CONTROL">
		<xsl:if test="$sel_sec/params_filter/filter">
				<div class="toggle-filters">
					<i class="fas fa-cog"></i> <a href="#" rel="nofollow" onclick="$('#filters_container').toggle('blind', 200); return false;">Подбор по параметрам</a>
				</div>
			</xsl:if>

			<xsl:if test="$sel_sec/params_filter/filter">
				<form method="get" action="{$sel_sec/filter_base_link}">
					<input type="hidden" name="var" value="fil"/>
					<div class="filters" style="{'display: none'}" id="filters_container"><!--[not($user_filter)]-->
						<xsl:for-each select="$sel_sec/params_filter/filter/input">
							<xsl:variable name="name" select="@id"/>
							<div class="active checkgroup">
								<strong>
									<xsl:value-of select="@caption"/><xsl:text> </xsl:text>
									<xsl:if test="@description and not(@description = '')">[<xsl:value-of select="@description"/>]</xsl:if>
								</strong>
								<div class="values">
									<xsl:for-each select="domain/value">
										<div class="checkbox">
											<label>
												<input name="{$name}" type="checkbox" value="{.}">
													<xsl:if test=". = $user_filter/input[@id = $name]">
														<xsl:attribute name="checked" select="'checked'"/>
													</xsl:if>
												</input>&#160;<xsl:value-of select="."/>
											</label>
										</div>
									</xsl:for-each>
								</div>
							</div>
						</xsl:for-each>
						<div class="buttons">
							<input type="submit" value="Показать найденное"/>
							<input type="submit" value="Сбросить" onclick="location.href = '{page/reset_filter_link}'; return false;"/>
						</div>
					</div>
				</form>
			</xsl:if>

			<xsl:if test="not($not_found)">
				<div class="view-container">
					<div class="view">
						<div class="desktop">
							<span>Показывать:</span>
							<span><i class="fas fa-th-large"></i> <a href="{page/set_view_table}"  rel="nofollow">Плиткой</a></span>
							<span><i class="fas fa-th-list"></i> <a href="{page/set_view_list}"  rel="nofollow">Строками</a></span>
						</div>
						<div class="checkbox">
							<label>
								<xsl:if test="not($only_available)">
									<input type="checkbox" onclick="window.location.href = '{page/show_only_available}'"/>
								</xsl:if>
								<xsl:if test="$only_available">
									<input type="checkbox" checked="checked" onclick="window.location.href = '{page/show_all}'"/>
								</xsl:if>
								в наличии
							</label>
						</div>
						<span>
							<select class="form-control" value="{page/variables/sort}{page/variables/direction}"
							        onchange="window.location.href = $(this).find(':selected').attr('link')">
								<option value="ASC" link="{page/set_sort_default}">Без сортировки</option>
								<option value="priceASC" link="{page/set_sort_price_asc}">Сначала дешевые</option>
								<option value="priceDESC" link="{page/set_sort_price_desc}">Сначала дорогие</option>
								<option value="nameASC" link="{page/set_sort_name_asc}">По алфавиту А→Я</option>
								<option value="nameDESC" link="{page/set_sort_name_desc}">По алфавиту Я→А</option>
							</select>
						</span>
					</div>
					<div class="quantity desktop">
						<span>Кол-во на странице:</span>
						<span>
							<select class="form-control" value="{page/variables/limit}"
							        onchange="window.location.href = $(this).find(':selected').attr('link')">
								<option value="12" link="{page/set_limit_12}">12</option>
								<option value="24" link="{page/set_limit_24}">24</option>
								<option value="10000" link="{page/set_limit_all}">все</option>
							</select>
						</span>
					</div>
				</div>
			</xsl:if>
	</xsl:template>

	<xsl:template name="TAGS">
	   <xsl:if test="$subs or $sel_sec/tag">
				<xsl:if test="not($subs)">
					<div class="tags">
						<form method="GET" action="{page/source_link}">
							<xsl:apply-templates select="$sel_sec/tag"/>
						</form>
					</div>
				</xsl:if>
				<xsl:if test="not($sel_sec/show_subs = '0')">
					<xsl:if test="$subs and $sub_view = 'tags'">
						<div class="tags">
							<xsl:apply-templates select="$subs" mode="tag"/>
						</div>
					</xsl:if>
					<xsl:if test="$subs and $sub_view = 'pics'">
						<div class="catalog-items">
							<xsl:apply-templates select="$subs" mode="pic"/>
						</div>
					</xsl:if>
				</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="section" mode="tag">
		<a href="{show_products}" class="tag" title="перейти в подраздел">
			<span>
				<xsl:value-of select="name"/>
			</span>
		</a>
	</xsl:template>

	<xsl:template match="section" mode="pic">
		<xsl:variable name="main_pic" select="product[1]/gallery[1]"/>
		<xsl:variable name="sec_id" select="@id"/>
		<xsl:variable name="has_sub" select="//page/catalog//section[@id = $sec_id and section]"/>
		<xsl:variable name="pic_path" select="if ($main_pic) then concat(product[1]/@path, $main_pic) else 'img/no_image.png'"/>
		
		<div class="catalog-item">
			<a href="{show_products}" class="image-container" style="background-image: url({$pic_path})"></a>
		<div>
			<a href="{show_products}" style="height: unset;"><xsl:value-of select="name"/></a>
			<xsl:value-of select="short" disable-output-escaping="yes"/>
		</div>
		</div>
	</xsl:template>

</xsl:stylesheet>