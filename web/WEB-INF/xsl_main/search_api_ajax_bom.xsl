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
	<xsl:variable name="products" select="$result_queries/product"/>
	<xsl:variable name="has_results" select="if ($is_not_bom) then $products else true()"/>
	<xsl:variable name="vars" select="page/variables"/>





	<xsl:template match="/">
		<xsl:for-each select="$result_queries">
			<xsl:variable name="visible_prods" select="product[position() = 1 or f:num(@request_qty) &gt; 0]"/>
			<xsl:variable name="hidden_prods" select="product[position() &gt; 1 and not(@request_qty)]"/>
			<div class="result" id="search_bom_ajax" mode="prepend" data-extra-selector="#new_{@id}">
				<div class="green" qty="{@qty}" query_id="{@id}" id="new_{@id}">
					<xsl:if test="$visible_prods">
						<div class="div-row blue blue_visible">
							<div class="w-1">
								<xsl:apply-templates select="$visible_prods" mode="product-lines-api">
									<xsl:with-param name="multiple" select="true()"/>
									<xsl:with-param name="query" select="@q"/>
									<xsl:with-param name="req_qty" select="@qty"/>
								</xsl:apply-templates>
							</div>
						</div>
					</xsl:if>
					<xsl:if test="$hidden_prods">
						<div class="div-row red">
							<div class="w-1 text-center fw-bold"><a href="#" popup=".prod_{@id}">Показать все предложения по данной строке</a></div>
						</div>
						<div class="div-row blue" line_hidden="true">
							<div class="w-1 prod_{@id}" style="display:none">
								<xsl:apply-templates select="$hidden_prods" mode="product-lines-api">
									<xsl:with-param name="multiple" select="true()"/>
									<xsl:with-param name="query" select="@q"/>
									<xsl:with-param name="req_qty" select="@qty"/>
								</xsl:apply-templates>
							</div>
							<div class="w-2"><!-- empty --></div>
						</div>
					</xsl:if>

					<xsl:if test="not($visible_prods)">
						<div class="div-row red">
							<div class="div-tr">
								<div class="div-td td-check">❌</div>
								<div class="div-td td-query"><b><xsl:value-of select="@q"/></b></div>
								<div class="div-td" style="max-width: fit-content; border: none; color: red; font-size: larger">По запросу товары не найдены</div>
							</div>
						</div>
					</xsl:if>
				</div>
			</div>
		</xsl:for-each>
	</xsl:template>



</xsl:stylesheet>