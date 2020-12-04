<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
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

		<p>
			<a href="oeder_email" target="_blank">Смотреть письмо покупателю</a><br/>
			<a href="shop_email" target="_blank">Смотреть письмо магазаину</a><br/>
		</p>

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
						<a href="{page/set_currency_rur}" title="Показать цены в российских рублях" class="{'active'[$curr = 'rur']}">RUR</a>
					</div>
				</div>
			<p>Поиск по основному каталогу отключен.</p>
			<div class="catalog-items{' lines'[$view = 'list']}"></div>
<!--			<xsl:if test="not($products)">-->
<!--				<h4>По заданным критериям товары не найдены</h4>-->
<!--			</xsl:if>-->
			<div id="extra_search_1">
				<p>Поиск по каталогу PLATAN отключен.</p>
			</div>
			<div id="extra_search_2">
				<p>Поиск по каталогу DIGIKEY отключен.</p>
			</div>
			<div id="extra_search_3">
				<h2>Идет поиск по FARNELL</h2>
			</div>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
		<script type="text/javascript">
			$(document).ready(function(){
				//insertAjax('<xsl:value-of select="concat('platan_search', '?query=', page/variables/q)"/>');
				//postForm('dgk-form');
				insertAjax('<xsl:value-of select="page/farnell_search_link"/>');
			})

		</script>
	</xsl:template>

	<xsl:template name="SEARCH_FORM">
		<form class="header__search header__column" action="search_test" method="get" style="flex-wrap: wrap">
			<input type="text" class="text-input header__field" name="q" value="{page/variables/q}" autocomplete="off" />
			<input type="submit" class="button header__button" value="Поиск" />
			<div style="color: #9f9e9e; display: block; flex-basis: 100%;">
				Поиск по нашему складу и складам партнеров
			</div>
		</form>
	</xsl:template>

</xsl:stylesheet>