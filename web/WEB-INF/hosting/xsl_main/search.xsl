<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<!-- Поиск BOM -->
	<xsl:variable name="queries" select="page/variables/q"/>
	<xsl:variable name="numbers" select="page/variables/n"/>
	<xsl:variable name="is_search_multiple" select="count($queries) &gt; 1"/>


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
	<xsl:variable name="view" select="if ($is_search_multiple) then 'lines' else if ($view_var) then $view_var else $view_search"/>
	<xsl:variable name="hide_side_menu" select="$search/hide_side_menu = '1'"/>
	<!-- Список отключенных элементов (плитка список таблица поиск) -->
	<xsl:variable name="view_disabled" select="$search/disable"/>



	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>




	<xsl:variable name="title">Поиск по запросу "<xsl:value-of select="if ($is_search_multiple) then 'BOM' else page/variables/q"/>"</xsl:variable>
	<xsl:variable name="h1">Поиск по запросу "<xsl:value-of select="if ($is_search_multiple) then 'BOM' else page/variables/q"/>"</xsl:variable>
	<xsl:variable name="active_menu_item" select="'catalog'"/>

	<xsl:variable name="products" select="page/product | page/plain_catalog/product | page/catalog/product"/>
	<xsl:variable name="results_api" select="page/api_search/product_list/results"/>
	<xsl:variable name="has_results" select="$products or $results_api"/>
	<xsl:variable name="only_available" select="page/variables/minqty = '0'"/>



	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a class="path__link" href="{$main_host}">Главная страница</a>
				<div class="path__arrow"></div>
				<a class="path__link" href="{page/catalog_link}">Поиск</a>
				<xsl:if test="$has_excel_search_results and $has_results">
					<a style="position: absolute; right: 0px; font-weight: bold;" href="{page/save_excel_file}">
						<img src="img/excel2.png" /> Сохранить результаты
					</a>
				</xsl:if>
			</div>
		</div>

	</xsl:template>


	<xsl:template name="CONTENT">

		<xsl:if test="$has_results and not($is_search_multiple)">
			<div class="view view_section">
				<div class="view__column">
					<xsl:if test="not($view_disabled = 'плитка')">
						<a href="{page/set_view_table}" class="icon-link{' active'[$view = 'table']}">
							<div class="icon">
								<img src="img/icon-grid.svg" alt="" />
							</div>
							<span class="icon-link__item">Плиткой</span>
						</a>
					</xsl:if>
					<xsl:if test="not($view_disabled = 'список')">
						<a href="{page/set_view_list}" class="icon-link{' active'[$view = 'list']}">
							<div class="icon">
								<img src="img/icon-line.svg" alt="" />
							</div>
							<span class="icon-link__item">Строками</span>
						</a>
					</xsl:if>
					<xsl:if test="not($view_disabled = 'таблица')">
						<a href="{page/set_view_lines}" class="icon-link{' active'[$view = 'lines']}">
							<div class="icon">
								<img src="img/icon-lines.svg" alt="" />
							</div>
							<span class="icon-link__item">Таблица</span>
						</a>
					</xsl:if>
				</div>
			</div>
		</xsl:if>


		<xsl:if test="$has_results">
			<div class="devices devices_section{' lines'[$view = 'list']}">
				<xsl:if test="$view = 'table'">
					<div class="devices__wrap">
						<xsl:apply-templates select="$products" mode="product-table"/>
						<xsl:apply-templates select="$results_api/product" mode="product-table-api"/>
					</div>
				</xsl:if>
				<xsl:if test="$view = 'list'">
					<div class="devices__wrap devices__wrap_rows">
						<xsl:apply-templates select="$products" mode="product-list"/>
						<xsl:apply-templates select="$results_api/product" mode="product-list-api"/>
					</div>
				</xsl:if>
				<xsl:if test="$view = 'lines'">
					<xsl:if test="$has_results">
						<xsl:call-template name="LINES_TABLE">
							<xsl:with-param name="products" select="$products"/>
							<xsl:with-param name="results_api" select="$results_api"/>
							<xsl:with-param name="multiple" select="$is_search_multiple"/>
							<xsl:with-param name="queries" select="$queries"/>
							<xsl:with-param name="numbers" select="$numbers"/>
						</xsl:call-template>
					</xsl:if>
				</xsl:if>
			</div>
		</xsl:if>
		<xsl:if test="not($has_results)">
			<h4>По заданным критериям товары не найдены</h4>
		</xsl:if>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
	</xsl:template>

</xsl:stylesheet>