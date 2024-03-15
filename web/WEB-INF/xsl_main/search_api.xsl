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

	<div class="tabs tabs_product p5">
		<xsl:if test="$is_not_bom">
			<div class="tabs__nav">
				<a href="#tab_search_1" class="tab search_tab tab_active" rel="check_api">Склады</a>
				<a href="#tab_search_2" class="tab search_tab" rel="check_catalog">Каталог</a>
				<a href="#tab_search_3" class="tab search_tab" rel="check_docs">Документация</a>
			</div>
		</xsl:if>
		<div class="tabs__content">
			<div class="tab-container" id="tab_search_1">
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
						<xsl:if test="$has_results">
							<div class="devices devices_section{' lines'[$view = 'list']}">
								<xsl:call-template name="LINES_TABLE">
									<xsl:with-param name="results_api" select="$distributors"/>
									<xsl:with-param name="multiple" select="$is_bom"/>
									<xsl:with-param name="queries" select="$query"/>
								</xsl:call-template>
							</div>
						</xsl:if>
						<xsl:if test="not($has_results)">
							<h4>По заданным критериям товары не найдены</h4>
						</xsl:if>
					</xsl:if>

					<!-- ************************			МНОГО ЗАПРОСОВ (BOM)			*************************-->

					<xsl:if test="$is_bom">
						<div class="devices devices_section{' lines'[$view = 'list']}">
							<xsl:call-template name="LINES_TABLE">
								<xsl:with-param name="results_api" select="$distributors"/>
								<xsl:with-param name="multiple" select="true()"/>
								<xsl:with-param name="queries" select="$result_queries"/>
							</xsl:call-template>
						</div>
					</xsl:if>

				</div>
			</div>
			<div class="tab-container" id="tab_search_2" style="display: none; min-height: 200px; background: rgba(255, 255, 255, 0.5) url('admin/ajax/loader.gif') center no-repeat">

			</div>
			<div class="tab-container" id="tab_search_3" style="display: none; min-height: 200px; background: rgba(255, 255, 255, 0.5) url('admin/ajax/loader.gif') center no-repeat">

			</div>
		</div>
	</div>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
	</xsl:template>

</xsl:stylesheet>