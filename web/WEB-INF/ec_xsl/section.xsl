<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="cities" as="element()*">
		<item>Минске</item>
		<item>Витебске</item>
		<item>Могилеве</item>
		<item>Гомеле</item>
		<item>Бресте</item>
		<item>Гродно</item>
	</xsl:variable>
	<xsl:variable name="x" select="f:num(page/variables/page) mod count($cities)" />


	<xsl:variable name="title" select="string-join((replace($sel_sec/name, 'Прочее', 'Радиокомпоненты, радиодетали и электронные компоненты'), 'купить в', $cities[$x],'в розницу и оптом – Чип Электроникс'), ' ')"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $sel_sec/name"/>

	<xsl:variable name="meta_description" select="concat(replace($sel_sec/name, 'Прочее', 'Радиокомпоненты'),' в ', $cities[$x],' от Чип Электроникс. Звоните ☎☎☎ +375 (17) 269-92-36. Доступная цена! Доставка по Беларуси. Оптом и в розницу)')"/>
	<xsl:variable name="meta_keywords" select="$sel_sec/name"/>

	<xsl:variable name="main_menu_section" select="page/catalog//section[@id = $sel_sec_id]"/>
	<xsl:variable name="subs" select="$main_menu_section/section"/>
	<xsl:variable name="show_devices" select="not($sel_sec/show_devices = '0') or not($subs)"/>

	<xsl:variable name="default_sub_view" select="if($show_devices) then 'tags' else 'pics'"/>

	<xsl:variable name="sub_view" select="if($sel_sec/sub_view != '') then $sel_sec/sub_view else $default_sub_view"/>

	<xsl:template name="LEFT_COLUMN">
		<xsl:call-template name="CATALOG_LEFT_COLUMN"/>
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

	<xsl:variable name="view" select="page/variables/view"/>
	<xsl:variable name="tag" select="page/variables/tag"/>
	
	<xsl:variable name="tag1" select="page/variables/tag1"/>
	<xsl:variable name="tag2" select="page/variables/*[starts-with(name(), 'tag2')]"/>
	<xsl:variable name="not_found" select="$tag1 and not($sel_sec/product)"/>
	<xsl:variable name="products" select="$sel_sec/product or $not_found"/>
	<xsl:variable name="only_available" select="page/variables/minqty = '0'"/>
	<!-- <xsl:variable name="canonical"
				  select="if($tag != '') then concat('section/', $sel_sec/@key, '/', //tag[tag = $tag]/canonical) else concat('section/', $sel_sec/@key, '/')"/> -->

	<xsl:variable name="user_filter" select="page/variables/fil[input]"/>


	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="{$main_host}">Главная страница</a> <i class="fas fa-angle-right"></i> <a href="{page/catalog_link}">Электронные компоненты</a>
				<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id and @id != $sel_sec_id]">
					<i class="fas fa-angle-right"></i>
					<a href="{show_products}">
						<xsl:value-of select="name"/>
					</a>
				</xsl:for-each>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title">
			<xsl:value-of select="$h1"/>
		</h1>
		<xsl:call-template name="TOP_SEO"/>
		<div class="page-content m-t">
			<!-- Теги, подразделы -->
			<xsl:call-template name="TAGS"/>
			<!-- Фильтр по параметрам -->
			<xsl:call-template name="FILTER"/>
			<!-- Отображние блоками/списком, товаров на страницу, сортировка, наличие -->

			<xsl:if test="$subs and $sub_view = 'pics' and $show_devices and not($sel_sec/show_subs = '0')">
				<div class="h3">Товары</div>
			</xsl:if>
			<xsl:call-template name="DISPLAY_CONTROL"/>

			<xsl:if test="$show_devices">
				<div class="catalog-items{' lines'[$view = 'list']}">
					<xsl:if test="$view = 'table'">
						<xsl:apply-templates select="$sel_sec/product" mode="product-table"/>
					</xsl:if>
					<xsl:if test="$view = 'list'">
						<xsl:apply-templates select="$sel_sec/product" mode="product-lines"/>
					</xsl:if>
					<xsl:if test="$not_found">
						<h4>По заданным критериям товары не найдены</h4>
					</xsl:if>
				</div>
			</xsl:if>
		</div>

		<xsl:if test="$sel_sec/product_pages and $show_devices">
			<div class="pagination">
				<span>Странциы:</span>
				<div class="pagination-container">
					<xsl:for-each select="$sel_sec/product_pages/page">
						<a href="{link}" class="{'active'[current()/@current]}">
							<xsl:value-of select="number"/>
						</a>
					</xsl:for-each>
				</div>
			</div>
		</xsl:if>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
	</xsl:template>

	<xsl:template match="tag">
		<label class="tag{if(current()/tag = $tag) then ' active' else ''}">
			<xsl:if test="current()/tag = $tag">
				<input type="radio" checked="checked" value="{tag}" name="tag"
					   onclick="document.location.replace('{//reset_filter_link}')"/>
			</xsl:if>
			<xsl:if test="not(current()/tag = $tag)">
				<input type="radio" value="{tag}" name="tag" onchange="$(this).closest('form').submit();"/>
			</xsl:if>
			<xsl:value-of select="tag"/>
		</label>
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

	<xsl:template name="FILTER">
		<xsl:variable name="valid_inputs" select="$sel_sec/params_filter/filter/input[count(domain/value) &gt; 0]"/>

		<xsl:if test="not($subs) and $valid_inputs">
			<div class="toggle-filters">
				<i class="fas fa-cog"></i>
				<a onclick="$('#filters_container').slideToggle(200);">Подбор по параметрам</a>
			</div>
			<form method="post" action="{$sel_sec/filter_base_link}">
				<div class="filters" style="{'display: none'[not($user_filter)]}" id="filters_container">
					   <xsl:for-each select="$valid_inputs">
						   <xsl:variable name="name" select="@id"/>
							<div class="active checkgroup">
								<strong>
									<xsl:value-of select="@caption"/>
								</strong>
								<div class="values">
									<xsl:for-each select="domain/value">
										<div class="checkbox">
											<label>
												<input name="{$name}" type="checkbox" value="{.}">
													<xsl:if test=". = $user_filter/input[@id = $name]">
														<xsl:attribute name="checked" select="'checked'"/>
													</xsl:if>
												</input>
												&#160;<xsl:value-of select="."/>
											</label>
										</div>
									</xsl:for-each>
								</div>
							</div>
					   </xsl:for-each>
					   <div class="buttons">
							<input class="button" type="submit" value="Показать найденное"/>
							<input class="button" type="submit" value="Сбросить" onclick="location.href = '{page/reset_filter_link}'; return false;"/>
					   </div>
				</div>
			</form>
		</xsl:if>
	</xsl:template>

	<xsl:template name="DISPLAY_CONTROL">
		<xsl:if test="$show_devices and not($not_found) and $sel_sec/product">
			<div class="view-container desktop">
				<div class="view">
					<span class="{'active'[not($view = 'list')]}">
						
						<a href="{page/set_view_table}"><i class="fas fa-th-large"></i></a>
					</span>
					<span class="{'active'[$view = 'list']}">
						
						<a href="{page/set_view_list}"><i class="fas fa-th-list"></i></a>
					</span>
				</div>

<!--				<div class="quantity">-->
					<label title="Показать только товары в наличии">
						<xsl:if test="not($only_available)">
							<input type="checkbox"
								   onclick="window.location.href = '{page/show_only_available}'"/>
						</xsl:if>
						<xsl:if test="$only_available">
							<input type="checkbox" checked="checked" onclick="window.location.href = '{page/show_all}'"/>
						</xsl:if>
						в наличии
					</label>
<!--				</div>-->
				<div class="quantity">
					Сортировка:
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
				<div class="quantity currency">
					<xsl:variable name="qot">'</xsl:variable>
					<xsl:variable name="currency_switch">
						<xsl:value-of select="concat('&lt;a href=',$qot, page/set_currency_byn, $qot, 'title=',$qot,'Показать цены в белорусских рублях',$qot,'&gt;BYN&lt;/a&gt;&lt;br/&gt;')" />
						<xsl:value-of select="concat('&lt;a href=',$qot, page/set_currency_rub, $qot, 'title=',$qot,'Показать цены в российских рублях',$qot,'&gt;RUB&lt;/a&gt;&lt;br/&gt;')" />
						<xsl:value-of select="concat('&lt;a href=',$qot, page/set_currency_usd, $qot, 'title=',$qot,'Показать цены в долларах США',$qot,'&gt;USD&lt;/a&gt;')" />
					</xsl:variable>

					Валюта: <a data-container="body"  data-html="true" data-toggle="popover" data-placement="bottom" data-content="{$currency_switch}">
								<xsl:value-of select="upper-case($curr)"/>
							</a>
				</div>

				<div class="quantity">
					<span>Кол-во на странице:</span>
					<span>
						<select class="form-control" value="{page/variables/limit}"
								onchange="window.location.href = $(this).find(':selected').attr('link')">
							<option value="48" link="{page/set_limit_48}">48</option>
							<option value="96" link="{page/set_limit_96}">96</option>
							<option value="144" link="{page/set_limit_144}">144</option>
						</select>
					</span>
				</div>
			</div>
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
		<xsl:variable name="sec_pic" select="if (main_pic != '') then concat(@path, main_pic) else ''"/>
		<xsl:variable name="product_pic" select="if (product/main_pic != '') then concat(product/@path, product/main_pic) else ''"/>
		<xsl:variable name="pic" select="if($sec_pic != '') then $sec_pic else if($product_pic != '') then $product_pic else 'img/no_image.png'"/>
		<div class="device items-catalog__section">
			<a href="{show_products}" class="device__image device_section__image" style="background-image: url({$pic});"></a>
			<a href="{show_products}" class="device__title"><xsl:value-of select="name"/></a>
		</div>
	</xsl:template>

</xsl:stylesheet>