<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
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
					<a href="{page/index_link}">Главная страница</a> &gt; <a href="{page/catalog_link}">Каталог</a>
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
					<tr>
						<xsl:variable name="prods" select="page/product"/>
						<xsl:for-each-group select="$prods/params/param" group-by="@caption">
							<tr>
							<xsl:variable name="param_name" select="current-grouping-key()"/>
							<xsl:for-each select="$prods">
								<td>
									<span class="param-name"><xsl:value-of select="$param_name"/></span>
									<span class="param-value">
										<xsl:if test="params/param[@caption = $param_name]">
											<xsl:value-of select="params/param[@caption = $param_name]"/>
										</xsl:if>
										<xsl:if test="not(params/param[@caption = $param_name])">
											<span style="opacity: .5">нет данных</span>
										</xsl:if>
									</span>
								</td>
							</xsl:for-each>
							</tr>					
						</xsl:for-each-group>
						<!-- <xsl:apply-templates select="page/product/params" /> -->
					</tr>
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