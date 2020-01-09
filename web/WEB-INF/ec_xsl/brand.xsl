<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="active_menu_item" select="'news'"/>


	<xsl:variable name="ni" select="page/news_item"/>
	<xsl:variable name="brand_mask" select="page/variables/brand"/>
	<xsl:variable name="brand" select="page/brands/brand[mask = $brand_mask]"/>
	<xsl:variable name="sel_sec" select="page/sel_sec"/>

	<xsl:variable name="leaf_sections" select="page//section[not(section)]"/>

	<xsl:variable name="view" select="if (page/variables/view) then page/variables/view else 'list'"/>
	<xsl:variable name="only_available" select="page/variables/minqty = '1'"/>


	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a> &gt;
				<xsl:value-of select="$brand/name"/>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1><xsl:value-of select="$brand/name"/></h1>
		<div class="brand-info">
			<div class="brand-logo">
				<img src="{$brand/@path}{$brand/pic}" alt="" />
			</div>
			<!-- <div class="brand-paper">
				<img src="http://placehold.it/50x100" alt=""/>
			</div> -->
		</div>
		<xsl:value-of select="$brand/text" disable-output-escaping="yes"/>
		<h3><xsl:value-of select="if ($sel_sec) then $sel_sec/name else 'Продукция'"/>&#160;<xsl:value-of select="$brand/name" /></h3>
		<div class="catalog-links">
			<!-- <xsl:for-each select="page/brand_section[@id = $leaf_sections/@id]"> -->
			<xsl:for-each select="page/brand_section">
				<a href="{set_section}" class="catalog-links__link"><xsl:value-of select="name" /></a>
			</xsl:for-each>
		</div>
		<div class="page-content m-t">
			<xsl:call-template name="DISPLAY_CONTROL"/>
			<div class="catalog-items">
				<xsl:apply-templates select="page/product"/>
			</div>
		</div>

		<xsl:if test="page/product_pages">
			<div class="pagination">
				<span>Страницы:</span>
				<div class="pagination-container">
					<xsl:for-each select="page/product_pages/page">
						<a href="{replace(link, 'section/', '')}" class="{'active'[current()/@current]}"><xsl:value-of select="number"/></a>
					</xsl:for-each>
				</div>
			</div>
		</xsl:if>


		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="MAIN_CONTENT">
		<!-- MAIN COLOUMNS BEGIN -->
		<div class="container-fluid inner-content">
			<div class="container">
				<div class="row">

					<!-- RIGHT COLOUMN BEGIN -->
					<div class="col-xs-12 main-content">
						<div class="mc-container">
							<xsl:call-template name="INC_MOBILE_HEADER"/>
							<xsl:call-template name="CONTENT"/>
						</div>
					</div>
					<!-- RIGHT COLOUMN END -->
				</div>
			</div>
		</div>
		<!-- MAIN COLOUMNS END -->
	</xsl:template>

    <xsl:template name="EXTRA_SCRIPTS">
        <xsl:call-template name="CART_SCRIPT"/>
    </xsl:template>



	<xsl:template name="DISPLAY_CONTROL">
		<div class="view-container">
			<div class="view">
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
						<option value="500" link="{page/set_limit_all}">все</option>
					</select>
				</span>
			</div>
		</div>
	</xsl:template>


</xsl:stylesheet>