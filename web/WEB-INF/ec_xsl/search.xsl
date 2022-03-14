<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="LEFT_COLUMN">
		<xsl:call-template name="CATALOG_LEFT_COLUMN"/>
	</xsl:template>

	<xsl:variable name="title">Поиск по запросу "<xsl:value-of select="page/variables/q"/>"</xsl:variable>
	<xsl:variable name="active_menu_item" select="'catalog'"/>

	<xsl:variable name="view" select="page/variables/view"/>
	<xsl:variable name="products" select="page/product"/>
	<xsl:variable name="only_available" select="page/variables/minqty = '0'"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="{$main_host}">Главная страница</a> <i class="fas fa-angle-right"></i>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title">Поиск по запросу "<xsl:value-of select="page/variables/q"/>"</h1>

		<div class="page-content m-t">
				<div class="view-container desktop">
					<div class="view">
						<span class="{'active'[not($view = 'list')]}">
							<i class="fas fa-th-large"></i>
							<a href="{page/set_view_table}">Плиткой</a>
						</span>
						<span class="{'active'[$view = 'list']}">
							<i class="fas fa-th-list"></i>
							<a href="{page/set_view_list}">Строками</a>
						</span>
					</div>
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
					<div class="quantity currency">
						Валюта:
						<a href="{page/set_currency_byn}" title="Показать цены в белорусских рублях" class="{'active'[$curr = 'byn']}">BYN</a>
						<a href="{page/set_currency_rub}" title="Показать цены в российских рублях" class="{'active'[$curr = 'rub']}">RUB</a>
						<a href="{page/set_currency_usd}" title="Показать цены в долларах США" class="{'active'[$curr = 'usd']}">USD</a>
					</div>
				</div>

			<div class="catalog-items{' lines'[$view = 'list']}">
					<xsl:if test="$view = 'table'">
						<xsl:apply-templates select="$products[not(tag = 'external_shop')]" mode="product-table"/>
					</xsl:if>
					<xsl:if test="$view = 'list'">
						<xsl:apply-templates select="$products[not(tag = 'external_shop')]" mode="product-lines"/>
					</xsl:if>
			</div>

			<xsl:if test="$products[tag = 'external_shop']">
				<div id="external_shops_search">
					<xsl:for-each-group select="$products[tag = 'external_shop']" group-by="tag[. = $shop/name]">
						<h2><xsl:value-of select="concat('Результат поиска по ', current-grouping-key())"/></h2>
						<div class="catalog-items{' lines'[$view = 'list']}">
							<xsl:if test="$view = 'table'">
								<xsl:apply-templates select="current-group()" mode="product-table"/>
							</xsl:if>
							<xsl:if test="$view = 'list'">
								<xsl:apply-templates select="current-group()" mode="product-lines"/>
							</xsl:if>
						</div>
					</xsl:for-each-group>
				</div>
			</xsl:if>

			<xsl:if test="not($products)">
				<h4>По заданным критериям товары не найдены</h4>
			</xsl:if>
			<div id="tme_search">
				Идет поиск по дополнительным каталогам...
			</div>
			<!-- <div id="farnell_search"></div> -->
			<div id="promelec_search"></div>
			<!-- <div id="digikey_search"></div> -->
			<!-- <div id="arrow_search"></div> -->
			<div id="gkel_search"></div>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
		<script type="text/javascript">
			$(document).ready(function(){
			<!-- insertAjax('<xsl:value-of select="page/arrow_search_link"/>'); -->
			<!-- insertAjax('<xsl:value-of select="page/digikey_search_link"/>'); -->
			<!-- insertAjax('<xsl:value-of select="page/farnell_search_link"/>'); -->
			insertAjax('<xsl:value-of select="page/platan_search_link"/>');
			insertAjax('<xsl:value-of select="page/promelec_search_link"/>');
			<!-- insertAjax('<xsl:value-of select="page/tme_search_link"/>'); -->
			insertAjax('<xsl:value-of select="page/gkel_search_link"/>');
			});
		</script>
	</xsl:template>

</xsl:stylesheet>