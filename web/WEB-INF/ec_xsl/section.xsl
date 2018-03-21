<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>


	<xsl:variable name="view" select="page/variables/view"/>
	<xsl:variable name="tag1" select="page/variables/tag1"/>
	<xsl:variable name="tag2" select="page/variables/*[starts-with(name(), 'tag2')]"/>
	<xsl:variable name="not_found" select="$tag1 and not($sel_sec/product)"/>
	<xsl:variable name="products" select="$sel_sec/product or $not_found"/>
	<xsl:variable name="only_available" select="page/variables/minqty = '0'"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a>
				<xsl:for-each select="page/catalog//section[.//@id = $sel_sec_id]">
					<xsl:text disable-output-escaping="yes"> &gt; </xsl:text>
					<a href="{show_section}"><xsl:value-of select="name"/></a>
				</xsl:for-each>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1><xsl:value-of select="$sel_sec/name"/></h1>
		<div class="page-content m-t">
			<xsl:if test="$sel_sec/tag_first">
				<div class="tags">
					<strong>Выберите тэг:</strong>
					<xsl:for-each select="$sel_sec/tag_first">
						<a href="{if (tag = $tag1) then //page/remove_tag_link else set_tag_1}">
							<span class="label label-{if (tag = $tag1) then 'primary' else 'success'}"><xsl:value-of select="tag"/></span>
						</a>
					</xsl:for-each>
				</div>
			</xsl:if>

			<xsl:if test="$sel_sec/tag_second">
				<form method="post" action="{page/filter_base_link}">
					<div class="filters">
						<div class="toggle-filters">
							<i class="fas fa-cog"></i> <a href="">Подбор по параметрам</a>
						</div>
						<xsl:for-each-group select="$sel_sec/tag_second" group-by="name">
							<xsl:sort select="name"/>
							<div class="active checkgroup">
								<strong><xsl:value-of select="current-grouping-key()"/></strong>
								<div class="values">
									<xsl:for-each-group select="current-group()" group-by="value">
										<xsl:sort select="value"/>
										<div class="checkbox">
											<label>
												<input type="checkbox" value="{name_value}">
													<xsl:if test="name_value = $tag2">
														<xsl:attribute name="checked" select="'checked'"/>
													</xsl:if>
												</input>&#160;<xsl:value-of select="current-grouping-key()"/>
											</label>
										</div>
									</xsl:for-each-group>
								</div>
							</div>
						</xsl:for-each-group>
						<div class="buttons">
							<input type="submit" value="Показать найденное" onclick="prepareFilterForm()"/>
							<input type="submit" value="Сбросить" onclick="$('.checkgroup input').attr('name', '')"/>
						</div>
					</div>
				</form>
				<script>
					<xsl:text disable-output-escaping="yes">
					var _finlterInputIndex = 0;
					function prepareFilterForm() {
						_finlterInputIndex = 0;
						$('.checkgroup').each(function(index, element) {
							var checkedInputs = $(element).find('input:checked');
							if (checkedInputs.length &gt; 0) {
								_finlterInputIndex++;
								checkedInputs.attr('name', 'tag2_' + _finlterInputIndex);
							}
						});
					}
					</xsl:text>
				</script>
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
								<option value="12" link="{page/set_limit_12}">12</option>
								<option value="24" link="{page/set_limit_24}">24</option>
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

</xsl:stylesheet>