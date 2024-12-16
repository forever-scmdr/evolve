<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="../snippets/product.xsl"/>

	<xsl:variable name="p" select="page/product"/>
	<xsl:variable name="currencies" select="page/currencies"/>
	<xsl:variable name="queries" select="page/command/product_list/result/query"/>

	<xsl:variable name="search_result_el" select="page/command/product_list/result"/>
	<xsl:variable name="result_queries" select="$search_result_el/query"/>
	<xsl:variable name="products" select="$result_queries/product"/>
	<xsl:variable name="query" select="$p/name"/>


	<xsl:template match="/">
		<div>
			<div class="popup result" id="product-ajax-popup">
				<div class="popup__body">
					<div class="popup__content">
						<a class="popup__close" onclick="clearProductAjax();">×</a>
						<div class="popup__title title title_2">
							Добавить к заказу
						</div>

						<xsl:call-template name="LINES_TABLE">
							<xsl:with-param name="results_api" select="$products"/>
							<xsl:with-param name="multiple" select="false()"/>
							<xsl:with-param name="queries" select="$query"/>
							<xsl:with-param name="exact" select="'true'"/>
						</xsl:call-template>

						<xsl:if test="not($queries/product)">
							<div class="view-table">
								<h3 style="text-align: center"><xsl:value-of select="page/variables/distr_title" /></h3>
								<table>
									<tr><th>Результаты не найдены</th></tr>
								</table>
							</div>
						</xsl:if>
					</div>
				</div>
				<script type="text/javascript">
					insertAjax('cart_ajax');
				</script>
			</div>
		</div>
	</xsl:template>



</xsl:stylesheet>