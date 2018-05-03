<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>

	<xsl:variable name="title" select="$sel_sec/name"/>
	<xsl:variable name="active_menu_item" select="'catalog'"/>

	<xsl:variable name="view" select="page/variables/view"/>
	<xsl:variable name="tag" select="page/variables/tag"/>
	<xsl:variable name="tag1" select="page/variables/tag1"/>
	<xsl:variable name="tag2" select="page/variables/*[starts-with(name(), 'tag2')]"/>
	<xsl:variable name="not_found" select="$tag1 and not($sel_sec/product)"/>
	<xsl:variable name="products" select="$sel_sec/product or $not_found"/>
	<xsl:variable name="only_available" select="page/variables/minqty = '0'"/>

	<xsl:variable name="user_filter" select="page/variables/fil[input]"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->

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
			"offerCount": <xsl:value-of select="concat($quote, $sel_sec/product_count, $quote)"/>
			}
			}
		</script>


		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a>
				<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id]">
					<xsl:text disable-output-escaping="yes"> &gt; </xsl:text>
					<a href="{if(section) then show_section else show_products}"><xsl:value-of select="name"/></a>
				</xsl:for-each>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1><xsl:value-of select="$sel_sec/name"/></h1>
		<div class="page-content m-t">

			<div class="tags">
				<form method="GET" action="{page/source_link}">
					<xsl:apply-templates select="$sel_sec/tag"/>
				</form>
			</div>

			<xsl:if test="$sel_sec/params_filter/filter">
				<div class="toggle-filters">
					<i class="fas fa-cog"></i> <a onclick="$('#filters_container').toggle('blind', 200);">Подбор по параметрам</a>
				</div>
			</xsl:if>

			<xsl:if test="$sel_sec/params_filter/filter">
				<form method="post" action="{$sel_sec/filter_base_link}">
					<div class="filters" style="{'display: none'[not($user_filter)]}" id="filters_container">
						<xsl:for-each select="$sel_sec/params_filter/filter/input">
							<xsl:variable name="name" select="@id"/>
							<div class="active checkgroup">
								<strong><xsl:value-of select="@caption"/></strong>
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
				<div class="view-container desktop">
					<div class="view">
						<span>Показывать:</span>
						<span><i class="fas fa-th-large"></i> <a href="{page/set_view_table}">Плиткой</a></span>
						<span><i class="fas fa-th-list"></i> <a href="{page/set_view_list}">Строками</a></span>
						<!--<div class="checkbox">-->
							<!--<label>-->
								<!--<xsl:if test="not($only_available)">-->
									<!--<input type="checkbox" onclick="window.location.href = '{page/show_only_available}'"/>-->
								<!--</xsl:if>-->
								<!--<xsl:if test="$only_available">-->
									<!--<input type="checkbox" checked="checked" onclick="window.location.href = '{page/show_all}'"/>-->
								<!--</xsl:if>-->
								<!--в наличии-->
							<!--</label>-->
						<!--</div>-->
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
					<div class="quantity">
						<span>Кол-во на странице:</span>
						<span>
							<select class="form-control" value="{page/variables/limit}"
									onchange="window.location.href = $(this).find(':selected').attr('link')">
								<option value="24" link="{page/set_limit_24}">24</option>
								<option value="48" link="{page/set_limit_48}">48</option>
								<option value="10000" link="{page/set_limit_all}">все</option>
							</select>
						</span>
					</div>
				</div>
			</xsl:if>

			<div class="catalog-items{' lines'[$view = 'list']}">
				<xsl:apply-templates select="$sel_sec/product"/>
				<xsl:if test="$not_found">
					<h4>По заданным критериям товары не найдены</h4>
				</xsl:if>
			</div>

		</div>

		<xsl:if test="$sel_sec/product_pages">
		<div class="pagination">
			<span>Странциы:</span>
			<div class="pagination-container">
				<xsl:for-each select="$sel_sec/product_pages/page">
					<a href="{link}" class="{'active'[current()/@current]}"><xsl:value-of select="number"/></a>
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
				<input type="radio" checked="checked" value="{tag}" name="tag" onclick="document.location.replace('{//reset_filter_link}')"/>
			</xsl:if>
			<xsl:if test="not(current()/tag = $tag)">
				<input type="radio" value="{tag}"  name="tag" onchange="$(this).closest('form').submit();"/>
			</xsl:if>
			<xsl:value-of select="tag"/>
		</label>
	</xsl:template>

</xsl:stylesheet>