<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f"
		version="2.0">
	<xsl:import href="section.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Сравнение товаров'" />
	<xsl:variable name="params_xml_item" select="page/product[1]/params_xml"/>

	<xsl:variable name="products" select="page/product"/>
	<xsl:variable name="test_params" select="$products/params/param[. != '']"/>


	<xsl:template name="CONTENT">
		<div class="compared-container">
			<table class="compared">
				<tr>
					<xsl:for-each select="$products">
						<th>
							<xsl:apply-templates select="." />
						</th>
					</xsl:for-each>
				</tr>
				<xsl:apply-templates select="$params_xml_item"/>
			</table>
		</div>
		<!-- <div class="compare-items">
			<xsl:apply-templates select="$params_xml_item" mode="test"/>
		</div> -->
	</xsl:template>



	<xsl:template match="params_xml">
		<xsl:variable name="params_xml" select="parse-xml(concat('&lt;params&gt;', xml, '&lt;/params&gt;'))/params"/>

		<xsl:for-each select="$params_xml/group">
			<xsl:variable name="group_param_names" select="parameter/normalize-space(lower-case(name))"/>
			<xsl:variable name="group_has_values" select="$test_params[normalize-space(lower-case(@caption)) = $group_param_names]"/>
			<xsl:variable name="group" select="."/>
			<xsl:if test="$group_has_values">
				<tr>
					<xsl:for-each select="$products">
						<td class="compared__section"><xsl:value-of select="if($group/@name != '') then $group/@name else 'Дополнительно'"/></td>
					</xsl:for-each>
				</tr>
				<xsl:apply-templates select="parameter"/>
			</xsl:if>
		</xsl:for-each>


		<xsl:if test="$params_xml/group and $params_xml/parameter">
			<tr>
				<xsl:for-each select="$products">
					<td class="compared__section">Прочие папраметры</td>
				</xsl:for-each>
			</tr>
		</xsl:if>

		<xsl:apply-templates select="$params_xml/parameter"/>

	</xsl:template>


	<xsl:template match="parameter">
		<xsl:variable name="caption" select="name"/>
		<xsl:variable name="name" select="lower-case(normalize-space(current()/name))"/>
		<xsl:variable name="has_values" select="$test_params[lower-case(normalize-space(@caption)) = $name]"/>
		<xsl:if test="$has_values">
			<tr>
				<xsl:for-each select="$products">
					<td class="compared__name"><xsl:value-of select="$caption" /></td>
				</xsl:for-each>
			</tr>
			<tr>
				<xsl:for-each select="$products">
					<xsl:variable name="v" select="params/param[lower-case(normalize-space(@caption)) = $name]" />
					<td class="compared__value"><xsl:value-of select="normalize-space(concat($v, ' ', $v/@description))" /></td>
				</xsl:for-each>
			</tr>
		</xsl:if>
	</xsl:template>


</xsl:stylesheet>