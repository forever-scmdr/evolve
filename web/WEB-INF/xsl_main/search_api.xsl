<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<!--Отображение товаров в каталоге -->
	<xsl:variable name="search" select="page/search"/>
	<xsl:variable name="view_var" select="page/variables/sview"/>
	<xsl:variable name="view_search">
		<xsl:choose>
			<xsl:when test="$search/default_view = 'список'">list</xsl:when>
			<xsl:when test="$search/default_view = 'таблица'">lines</xsl:when>
			<xsl:otherwise>table</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:variable name="view" select="'lines'"/>
	<xsl:variable name="hide_side_menu" select="$search/hide_side_menu = '1'"/>
	<!-- Список отключенных элементов (плитка список таблица поиск) -->
	<xsl:variable name="view_disabled" select="$search/disable"/>



	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>



<!--	<xsl:variable name="products" select="page/product | page/plain_catalog/product | page/catalog/product"/>-->
<!--	<xsl:variable name="results_api" select="page/api_search/product_list/results"/>-->
	<xsl:variable name="only_available" select="page/variables/minqty = '0'"/>


	<!-- Результаты поиска -->
	<xsl:variable name="search_result_el" select="page/command/product_list/result"/>
	<xsl:variable name="result_queries" select="$search_result_el/query"/>
	<xsl:variable name="is_search_multiple" select="count($result_queries) &gt; 1"/>
	<xsl:variable name="is_bom" select="$is_search_multiple"/>
	<xsl:variable name="is_not_bom" select="not($is_search_multiple)"/>
	<xsl:variable name="distributors" select="if ($is_not_bom) then $result_queries[1]/distributor else empty"/><!-- Только для варианта без BOM -->
	<xsl:variable name="products" select="$distributors/product"/>
	<xsl:variable name="has_results" select="if ($is_not_bom) then $products else true()"/>
	<xsl:variable name="vars" select="page/variables"/>

	<!-- Фильтр -->
	<xsl:variable name="input_from" select="$vars/from"/>
	<xsl:variable name="input_to" select="$vars/to"/>
	<xsl:variable name="input_ship_date" select="$vars/ship_date"/>
	<xsl:variable name="input_vendor" select="$vars/vendor"/>
	<xsl:variable name="input_distributor" select="$vars/distributor"/>


	<xsl:variable name="classvl" select="1"/>
	<xsl:variable name="title">Поиск по запросу "<xsl:value-of select="if ($is_bom) then 'BOM' else page/variables/q"/>"</xsl:variable>
	<xsl:variable name="h1">Поиск по запросу "<xsl:value-of select="if ($is_bom) then 'BOM' else page/variables/q"/>"</xsl:variable>
	<xsl:variable name="active_menu_item" select="'catalog'"/>


	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a class="path__link" href="{$main_host}">Главная страница</a>
				<div class="path__arrow"></div>
				<xsl:if test="$is_not_bom">
					<a class="path__link" href="{page/catalog_link}">Поиск</a>
				</xsl:if>
				<xsl:if test="$is_bom">
					<a class="path__link" href="#" onclick="$('#hidden_query').submit(); return false;">Поиск BOM</a>
					<form id="hidden_query" method="post" action="{page/input_bom_link}" style="display: none">
						<textarea name="q"><xsl:value-of select="$query" /></textarea>
					</form>
				</xsl:if>
			</div>
		</div>
	</xsl:template>




	<xsl:template name="CONTENT">
		<div class="devices devices_section">
			<div class="devices__wrap devices__wrap_rows">

				<xsl:apply-templates select="page/catalog/product[1]" mode="single_product"/>

				<xsl:if test="$is_not_bom">
					<form action="search_api_ajax" method="post" ajax-loader-id="api_results" id="api_ajax_form" ajax="true">
						<input type="hidden" name="q" value="{$query}"/>
						<div class="search_filter">
							<div class="item">
								<xsl:if test="$has_excel_search_results and $has_results">
									<a href="{page/save_excel_file}" style="text-align:center">
										&#160;&#160;&#160;&#160;&#160;&#160;<img src="img/excel2.png" /><br/>Сохранить<br/>результаты
									</a>
								</xsl:if>
							</div>
							<div class="item">
								<div class="box">
									<div class="title">Стоимость от</div>
									<input type="text" name="from" value="{$input_from}" placeholder="от {$search_result_el/min_price}"/>
								</div>
							</div>
							<div class="item">
								<div class="box">
									<div class="title">Стоимость до</div>
									<input type="text" name="to" value="{$input_to}" placeholder="до {$search_result_el/max_price}"/>
								</div>
							</div>
							<div class="item">
								<div class="box">
									<div class="title">Срок поставки</div>
									<div class="chbox">
										<div class="value">Любой</div>
										<div class="options">
											<xsl:for-each-group select="$products" group-by="next_delivery">
												<xsl:sort select="next_delivery"/>
												<xsl:variable name="value" select="current-grouping-key()"/>
												<xsl:variable name="is_empty" select="normalize-space($value) = ''"/>
												<label class="option">
													<input type="checkbox" name="ship_date" value="{$value}">
														<xsl:if test="$value = $input_ship_date"><xsl:attribute name="checked" select="checked" /></xsl:if>
													</input>
													<xsl:value-of select="$value"/><xsl:if test="$is_empty">(не задано)</xsl:if>
												</label>
											</xsl:for-each-group>
										</div>
									</div>
								</div>
							</div>
							<div class="item">
								<div class="box">
									<div class="title">Бренд</div>
									<div class="chbox">
										<div class="value">Любой</div>
										<div class="options">
											<xsl:for-each-group select="$products" group-by="vendor">
												<xsl:sort select="vendor"/>
												<xsl:variable name="value" select="current-grouping-key()"/>
												<label class="option">
													<input type="checkbox" name="vendor" value="{$value}">
														<xsl:if test="$value = $input_vendor"><xsl:attribute name="checked" select="checked" /></xsl:if>
													</input>
													<xsl:value-of select="$value"/>
												</label>
											</xsl:for-each-group>
										</div>
									</div>
								</div>
							</div>
							<div class="item">
								<div class="box">
									<div class="title">Поставщик</div>
									<div class="chbox">
										<div class="value">Любой</div>
										<div class="options">
											<xsl:for-each-group select="$distributors" group-by="@name">
												<xsl:sort select="@name"/>
												<xsl:variable name="value" select="current-grouping-key()"/>
												<label class="option">
													<input type="checkbox" name="distributor" value="{$value}">
														<xsl:if test="$value = $input_distributor"><xsl:attribute name="checked" select="checked" /></xsl:if>
													</input>
													<xsl:value-of select="$value"/>
												</label>
											</xsl:for-each-group>
										</div>
									</div>
								</div>
							</div>
							<div class="item">
								<a href="#" class="button button_request clear_filter_button">Снять фильтры</a>
							</div>
						</div>
					</form>
				</xsl:if>

				<div id="api_results">

					<!-- ************************			ОДИН ЗАПРОС (БЕЗ BOM)			*************************-->

					<xsl:if test="$is_not_bom">
						<xsl:variable name="has_exact_matches" select="$distributors/product[@query_exact_match = 'true']"/>
						<xsl:variable name="has_extra_matches" select="$distributors/product[@query_exact_match = 'false']"/>
						<xsl:if test="$has_exact_matches">
							<xsl:call-template name="LINES_TABLE">
								<xsl:with-param name="results_api" select="$distributors"/>
								<xsl:with-param name="multiple" select="$is_bom"/>
								<xsl:with-param name="queries" select="$query"/>
								<xsl:with-param name="exact" select="'true'"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="$has_extra_matches">
							<xsl:if test="not($has_exact_matches)">
								<h2>Полных совпадений по запросу не найдено, похожие результаты</h2>
							</xsl:if>
							<xsl:if test="$has_exact_matches">
								<h2>Дополнительно найдено (смежные запросы)</h2>
							</xsl:if>
							<xsl:call-template name="LINES_TABLE">
								<xsl:with-param name="results_api" select="$distributors"/>
								<xsl:with-param name="multiple" select="$is_bom"/>
								<xsl:with-param name="queries" select="$query"/>
								<xsl:with-param name="exact" select="'false'"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="not($has_results)">
							<h2>По заданным критериям товары не найдены</h2>
						</xsl:if>
					</xsl:if>

					<!-- ************************			МНОГО ЗАПРОСОВ (BOM)			*************************-->

					<xsl:if test="$is_bom">
						<xsl:call-template name="LINES_TABLE">
							<xsl:with-param name="results_api" select="$distributors"/>
							<xsl:with-param name="multiple" select="true()"/>
							<xsl:with-param name="queries" select="$result_queries"/>
						</xsl:call-template>
					</xsl:if>

				</div>
			</div>
		</div>
	</xsl:template>





	<xsl:template match="product" mode="single_product">
		<xsl:variable name="docs" select="if (documents_xml) then parse-xml(documents_xml)/value else none"/>
		<xsl:variable name="main_ds" select="$docs/param[1]/value[1]"/>
		<div class="device device_row" style="margin-bottom: 5px; z-index: 100">
			<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
			<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>

			<div class="device__column">

				<!-- zoom icon (not displayed, delete <div> with display: none to show) -->
				<div style="display: none">
					<xsl:if test="main_pic and number(main_pic/@width) &gt; 200">
						<a href="{concat(@path, main_pic)}" class="magnific_popup-image zoom-icon" title="{name}">
							<i class="fas fa-search-plus"></i>
						</a>
					</xsl:if>
				</div>

				<!-- device image -->
				<div class="device__image img">
					<a href="{show_product}">
						<img src="{$pic_path}" alt="" />
					</a>
				</div>

			</div>

			<div class="device__column">

				<!-- device title -->
				<a href="{show_product}" class="device__name"><span><xsl:value-of select="name"/></span></a>

				<!-- device description parameters -->
				<div class="device__info">

					<table class="params">
						<xsl:variable name="p" select="current()"/>
						<xsl:variable name="user_defined_params" select="tokenize($sel_sec/params_list, '[\|;]\s*')"/>
						<xsl:variable name="is_user_defined" select="$sel_sec/params_list and not($sel_sec/params_list = '') and count($user_defined_params) &gt; 0"/>
						<!--<xsl:variable name="captions" select="if ($is_user_defined) then $user_defined_params else params/param/@caption"/>-->
						<xsl:variable name="captions" select="if ($is_user_defined) then $user_defined_params else $p/param_vals/@key"/>

						<xsl:if test="//page/@name != 'fav'">
							<tbody>
								<xsl:for-each select="$captions[position() &lt;= $product_params_limit]">
									<!--<xsl:variable name="param" select="$p/params/param[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()))]"/>-->
									<xsl:variable name="param" select="$p/param_vals[lower-case(normalize-space(@key)) = lower-case(normalize-space(current()))]"/>
									<tr class="tr">
										<td><xsl:value-of select="$param/@key"/></td>
										<td><xsl:value-of select="$param/@value"/></td>
									</tr>
								</xsl:for-each>
								<xsl:if test="count($captions) &gt; $product_params_limit">
									<tr>
										<td colspan="2">
											<b><a class="toggle" href="#params-{@id}" rel="&lt;&lt;&lt;Скрыть параметры" style="text-decoration: underline;">Показать все параметры>>></a></b>
										</td>
									</tr>
								</xsl:if>
							</tbody>
							<xsl:if test="count($captions) &gt; $product_params_limit">
								<tbody id="params-{@id}" style="display:none;">
									<xsl:for-each select="$captions[position() &gt; $product_params_limit]">
										<!--<xsl:variable name="param" select="$p/params/param[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()))]"/>-->
										<xsl:variable name="param" select="$p/param_vals[lower-case(normalize-space(@key)) = lower-case(normalize-space(current()))]"/>
										<tr class="tr">
											<td><xsl:value-of select="$param/@key"/></td>
											<td><xsl:value-of select="$param/@value"/></td>
										</tr>
									</xsl:for-each>
								</tbody>
							</xsl:if>
						</xsl:if>
					</table>

				</div>
			</div>


			<div class="device__column">

				<!-- device actions (compare and favourites) -->
				<div class="add">
					<xsl:call-template name="FAV_AND_COMPARE">
						<xsl:with-param name="p" select="current()"/>
					</xsl:call-template>
				</div>
				<div class="add">
					<div id="spec_list_{@id}">
						<a href="{to_spec}" class="add__item icon-link" ajax="true" ajax-loader-id="spec_list_{@id}">
							<div class="icon"><img src="img/icon-spec.svg" alt=""/></div>
							<span><xsl:value-of select="$go_to_spec_label"/></span>
						</a>
					</div>
				</div>
				<div class="add">
					<a href="{$main_ds/a/@href}" class="icon-link product-icons__item" download="{$main_ds/a/@href}">
						<div class="icon icon_size_lg">
							<img src="img/pdf.png" alt="" width="18"/>
						</div>
						<span class="add__item">Скачать документацию</span>
					</a>
				</div>
			</div>
			<div class="price_box" id="price_{@id}">

			</div>
		</div>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
	</xsl:template>

</xsl:stylesheet>