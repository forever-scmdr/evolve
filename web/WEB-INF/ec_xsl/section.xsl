<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="tilte" select="if($tag != '') then concat($sel_sec/name, ' - ', $tag) else $sel_sec/name" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>

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
				"image": <xsl:value-of select="concat($quote, $main_host, '/', $sel_sec/@path, $sel_sec/main_pic, $quote)"/>,
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

	<xsl:variable name="view" select="page/variables/view"/>
	<xsl:variable name="tag" select="page/variables/tag"/>
	<xsl:variable name="title" select="if($tag != '') then concat($sel_sec/name, ' - ', $tag) else $sel_sec/name"/>
	<xsl:variable name="tag1" select="page/variables/tag1"/>
	<xsl:variable name="tag2" select="page/variables/*[starts-with(name(), 'tag2')]"/>
	<xsl:variable name="not_found" select="$tag1 and not($sel_sec/product)"/>
	<xsl:variable name="products" select="$sel_sec/product or $not_found"/>
	<xsl:variable name="only_available" select="page/variables/minqty = '0'"/>
	<xsl:variable name="canonical" select="if($tag != '') then concat('/', $sel_sec/@key, '/', //tag[tag = $tag]/canonical) else concat('/', $sel_sec/@key, '/')"/>

	<xsl:variable name="user_filter" select="page/variables/fil[input]"/>
	<xsl:variable name="main_menu_section" select="page/catalog//section[@id = $sel_sec_id]"/>
	
	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="{$main_host}">Главная страница</a>
				<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id and @id != $sel_sec_id]">
					<xsl:text disable-output-escaping="yes"> &gt; </xsl:text>
					<a href="{show_products}"><xsl:value-of select="name"/></a>
				</xsl:for-each>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1><xsl:value-of select="$h1"/></h1>
		<div class="page-content m-t">

			<div class="tags">
				<form method="GET" action="{page/source_link}">
					<xsl:apply-templates select="$sel_sec/tag"/>
				</form>
				<xsl:for-each select="$main_menu_section/section">
					<a href="{show_products}" class="tag" title="перейти в подраздел">
						<span>
							<xsl:value-of select="name"/>
						</span>
					</a>
				</xsl:for-each>
			</div>

		<xsl:if test="$sel_sec/params_filter/filter/input != ''">
			<xsl:if test="$sel_sec/params_filter/filter">
				<div class="toggle-filters">
					<i class="fas fa-cog"></i> <a onclick="$('#filters_container').slideToggle(200);" rel="nofollow">Подбор по параметрам</a>
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
		</xsl:if>

			<xsl:if test="not($not_found) and $sel_sec/product">

				<form id="view-control-form" method="post" action="{concat(page/base, '/', /page/reset_filter_link)}">
					<div class="view-container desktop">

						<!-- VIEW -->
						<input name="view" value="{page/variables/view}" type="hidden" id="view-inp"/>
						<div class="view">
							<span><i class="fas fa-th-large"></i> <a rel="nofollow" onclick="$('#view-inp').val('table'); $('#view-control-form').submit();">Плиткой</a></span>
							<span><i class="fas fa-th-list"></i> <a rel="nofollow" onclick="$('#view-inp').val('list'); $('#view-control-form').submit();">Строками</a></span>


							<!-- SORTING -->
							<input name="direction" value="{page/variables/direction}" type="hidden" id="direction"/>
							<input name="sort" value="{page/variables/sort}" type="hidden" id="sort"/>
							<span>
								<select class="form-control" onchange="$('#sort').val($(this).val()); $('#direction').val($(this).find(':selected').attr('dir')); $('#view-control-form').submit();">
									<option dir="" value="">Без сортировки</option>
									<option value="price" dir="ASC">Сначала дешевые</option>
									<option value="price" dir="DESC">Сначала дорогие</option>
									<option value="name" dir="ASC">По алфавиту А→Я</option>
									<option value="name" dir="DESC">По алфавиту Я→А</option>
								</select>
							</span>
						</div>

						<!-- LIMIT -->
						<div class="quantity">
							<span>Кол-во на странице:</span>
							<span>
								<select class="form-control" value="{page/variables/limit}" name="limit"
										onchange="$('#view-control-form').submit();">
									<option value="48">48</option>
									<option value="96" >96</option>
									<option value="144" >144</option>
								</select>
							</span>
						</div>
					</div>
				</form>

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
			<span>Страницы:</span>
			<div class="pagination-container">
				<xsl:for-each select="$sel_sec/product_pages/page">
					<a href="{link}" class="{'active'[current()/@current]}"><xsl:value-of select="number"/></a>
				</xsl:for-each>
			</div>
		</div>
		</xsl:if>
		<xsl:if test="$seo[1]/text and (not(page/variables/page) or page/variables/page = '1')">
			<div class="page-content m-t">
				<xsl:value-of select="$seo[1]/text" disable-output-escaping="yes"/>
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