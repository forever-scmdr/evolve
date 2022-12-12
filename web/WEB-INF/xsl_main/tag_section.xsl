<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>



	<xsl:variable name="title"><xsl:value-of select="page/variables/tag"/></xsl:variable>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>

	<xsl:variable name="catalog_hide_side_menu" select="$catalog/hide_side_menu"/>

	<xsl:variable name="hide_side_menu" select="$catalog_hide_side_menu = '1'"/>



	<xsl:variable name="active_menu_item" select="'catalog'"/>

	<xsl:variable name="products" select="page/product"/>
	<xsl:variable name="only_available" select="page/variables/minqty = '0'"/>


	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a class="path__link" href="{$main_host}">Главная страница</a>
				<div class="path__arrow"></div>
				<a class="path__link" href="{page/catalog_link}">Каталог</a>
				<div class="path__arrow"></div>
				<a class="path__link"><xsl:value-of select="page/variables/tag"/></a>
			</div>
		</div>

	</xsl:template>


	<xsl:template name="CONTENT">

		<div class="view view_section">
			<div class="view__column">
				<xsl:if test="not($view_disabled = 'плитка')">
					<a href="{page/set_view_table}"  class="icon-link{' active'[$view = 'table']}">
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
			<div class="view__column">
				<select value="{page/variables/sort}{page/variables/direction}" onchange="window.location.href = $(this).find(':selected').attr('link')">
					<option value="ASC" link="{page/set_sort_default}">Без сортировки</option>
					<option value="priceASC" link="{page/set_sort_price_asc}">Сначала дешевые</option>
					<option value="priceDESC" link="{page/set_sort_price_desc}">Сначала дорогие</option>
					<option value="nameASC" link="{page/set_sort_name_asc}">По алфавиту А→Я</option>
					<option value="nameDESC" link="{page/set_sort_name_desc}">По алфавиту Я→А</option>
				</select>
			</div>
			<div class="view__column">
				Кол-во на странице:
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


		<div class="devices devices_section{' lines'[$view = 'list']}">
			<xsl:if test="$view = 'table'">
				<div class="devices__wrap">
					<xsl:apply-templates select="$products" mode="product-table"/>
				</div>
			</xsl:if>
			<xsl:if test="$view = 'list'">
				<div class="devices__wrap devices__wrap_rows">
					<xsl:apply-templates select="$products" mode="product-list"/>
				</div>
			</xsl:if>
			<xsl:if test="$view = 'lines'">
				<xsl:if test="$products">
					<xsl:call-template name="LINES_TABLE">
						<xsl:with-param name="products" select="$products"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:if>
			<xsl:if test="not($products)">
				<h4>По заданным критериям товары не найдены</h4>
			</xsl:if>
		</div>

		<xsl:if test="//product_pages">
			<div class="pagination">
				<div class="pagination__label">Страницы:</div>
				<div class="pagination__wrap">
					<xsl:for-each select="//product_pages/page">
						<a href="{link}" class="pagination__item{' pagination__item_active'[current()/@current]}">
							<xsl:value-of select="number"/>
						</a>
					</xsl:for-each>
				</div>
			</div>
		</xsl:if>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
	</xsl:template>

</xsl:stylesheet>