<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f"
		version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Сравнение товаров'" />

	<xsl:template name="CONTENT">
		<style>
			table.compare td {
			vertical-align: top;
			}
			table.compare td > strong {
			display: block;
			font-size: .8em;
			margin-bottom: 1rem;
			background-color: #eee;
			padding: .5rem;
			}
			table.compare td > span {
			font-size: .8em;
			display: block;
			}
			table.compare td > span.param-name {
			color: gray;
			}
			table.compare td > span.param-value {
			margin-bottom: 1rem;
			}
			table.compare{
			width: auto;
			min-width: 0%;
			}
		</style>
		<div class="path-container">
			<div class="path-container">
				<div class="path">
					<a href="{page/index_link}">Главная страница</a> <i class="fas fa-angle-right"></i> <a href="{page/catalog_link}">Каталог</a>
				</div>
				<xsl:call-template name="PRINT"/>
			</div>
		</div>
		<h1 class="page-title">Сравнение</h1>
		<div class="page-content m-t">
			<div class="table-responsive">
				<table class="compare">
					<tr class="catalog-items">
						<xsl:for-each select="page/product">
							<td>
								<xsl:apply-templates select="." />
							</td>
						</xsl:for-each>
					</tr>
					
					<xsl:variable name="product_params" select="page/product/params"/>
					<xsl:for-each-group select="page/product/params/param" group-by="@caption">
						<xsl:variable name="cgc" select="current-grouping-key()" />
						<tr>	
							<xsl:for-each select="$product_params">
								<xsl:variable name="params" select="current()"/>
								<xsl:variable name="p" select="$params/param[@caption = $cgc]"/>
								<td>
									<span class="param-name"><xsl:value-of select="$cgc"/></span>
									<span class="param-value" style="{'opacity: .35'[not($p != '')]}">
									<xsl:value-of select="if($p != '') then $p else 'нет данных'"/>
									</span>
								</td>
							</xsl:for-each>
							<!-- <xsl:apply-templates select="page/product/params" /> -->
						</tr>	
					</xsl:for-each-group>
				</table>
			</div>
		</div>
		<script type="text/javascript">
			$(document).ready(function(){
				var l = $("table.compare tr:eq(0) td").length;
				<!-- var pcnt = 100/l; -->
				<!-- $("table.compare tr:eq(0) td").css({width : pcnt+"%"}); -->
			});
		</script>
	</xsl:template>

	<xsl:template match="params">
		<td>
			<xsl:for-each select="param">
				<span class="param-name"><xsl:value-of select="@caption"/></span>
				<span class="param-value">
					<xsl:value-of select="."/>
				</span>
			</xsl:for-each>
		</td>
	</xsl:template>

</xsl:stylesheet>