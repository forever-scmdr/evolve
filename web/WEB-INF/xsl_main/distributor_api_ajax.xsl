<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="snippets/product.xsl"/>

	<xsl:variable name="distr" select="page/command/product_list/result/query/distributor"/>
	<xsl:variable name="queries" select="page/command/product_list/result/query"/>
	<xsl:variable name="currencies" select="page/currencies"/>
	<xsl:variable name="position" select="page/variables/position"/>

	<xsl:template match="/">
		<div class="result" id="api_ajax_{$position}">
			<xsl:if test="$distr/product">
				<xsl:call-template name="LINES_TABLE">
					<xsl:with-param name="results_api" select="$distr"/>
					<xsl:with-param name="header" select="page/variables/distr_title"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="not($distr/product)">
				<div class="view-table">
					<h3 style="text-align: center"><xsl:value-of select="page/variables/distr_title" /></h3>
					<table>
						<tr><th>Результаты не найдены</th></tr>
					</table>
				</div>
			</xsl:if>
			<script type="text/javascript">
				insertAjax('cart_ajax');
			</script>
		</div>
	</xsl:template>



</xsl:stylesheet>