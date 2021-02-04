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


	<xsl:template name="CONTENT">
		<div class="compare-items">
			<xsl:apply-templates select="$params_xml_item"/>
		</div>
		<!-- <div class="compare-items">
			<xsl:apply-templates select="$params_xml_item" mode="test"/>
		</div> -->
	</xsl:template>

	<xsl:template match="params_xml" mode="test">
		<xsl:variable name="groups" select="parse-xml(concat('&lt;params&gt;', xml, '&lt;/params&gt;'))"/>

			<xsl:variable name="test_params" select="$products/params/param[. != '']"/>


			<ol>
				<xsl:for-each select="$groups/params/*">
					<xsl:variable name="gn" select="normalize-space(lower-case(name))"/>
					<xsl:variable name="has_values" select="$test_params[normalize-space(lower-case(@caption)) = $gn]"/>
					<li>
						<xsl:value-of select="name"/>
						=<xsl:value-of select="count($has_values)" />
					</li>
				</xsl:for-each>
			</ol>
	</xsl:template>

	<xsl:template match="params_xml">
		<xsl:variable name="groups" select="parse-xml(concat('&lt;params&gt;', xml, '&lt;/params&gt;'))"/>

		<xsl:variable name="test_params" select="$products/params/param[. != '']"/>

		<xsl:for-each select="$products">
			<xsl:variable name="product_params" select="params/param"/>
			<div class="compare-items__item">
				<xsl:apply-templates select="." />
				<ul class="compare-items__params">
					<!-- params in groups -->
					<xsl:for-each select="$groups/params/*">
						
						<xsl:variable name="gn" select="parameter/normalize-space(lower-case(name))"/>
						<xsl:variable name="group_has_values" select="$test_params[normalize-space(lower-case(@caption)) = $gn]"/>

						<xsl:if test="$group_has_values">
							<xsl:apply-templates select=".">
								<xsl:with-param name="global_values" select="$test_params"/>
								<xsl:with-param name="values" select="$product_params"/>
							</xsl:apply-templates>
						</xsl:if>
					</xsl:for-each>

					<!-- ungrouped params -->
					<xsl:if test="$groups/params/group and $groups/params/parameter">
						<li>
							<b>Прочие папраметры</b>
						</li>
					</xsl:if>
					<xsl:apply-templates select="$groups/params/parameter">
						<xsl:with-param name="global_values" select="$test_params"/>
						<xsl:with-param name="values" select="$product_params"/>
					</xsl:apply-templates>
				</ul>
			</div>
			<!-- <xsl:variable name="product_params" select="params/param"/>
			
				
				<ul class="compare-items__params">
					<xsl:apply-templates select="$groups">
						<xsl:with-param name="values" select="$product_params" />
					</xsl:apply-templates>
				</ul>
			</div> -->
		</xsl:for-each>



		

	</xsl:template>

	<xsl:template match="group">
		<xsl:param name="values"/>
		<xsl:param name="global_values"/>

		<li><b><xsl:value-of select="if(@name != '') then @name else 'Дополнительно'"/></b></li>
		<xsl:apply-templates select="parameter">
			<xsl:with-param name="values" select="$values"/>
			<xsl:with-param name="global_values" select="$global_values"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="parameter">
		<xsl:param name="values"/>
		<xsl:param name="global_values"/>

		<xsl:variable name="name" select="lower-case(normalize-space(current()/name))"/>
		
		<xsl:variable name="has_values" select="$global_values[lower-case(normalize-space(@caption)) = $name]"/>
		<xsl:if test="$has_values">
			<xsl:variable name="v" select="$values[lower-case(normalize-space(@caption)) = $name]" />
			<li>
				
				<span><xsl:value-of select="name"/></span>
				<span><xsl:value-of select="$v"/></span>
			</li>
		</xsl:if>	
	</xsl:template>
	<!-- <xsl:template match="group">
			<xsl:param name="values">
			<li>
				<b><xsl:value-of select="@name"/></b>
			</li>	
			<xsl:apply-templates select="parameter">
				<xsl:with-param name="values" select="$values" />
			</xsl:apply-templates>	
	</xsl:template>
	<xsl:template match="parameter">
			<xsl:param name="values">
			<xsl:variable name="v" select="$values[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()/name))]" />	
			<li>
				<span><xsl:value-of select="name"/></span>
				<span><xsl:value-of select="."/></span>
			</li>
	</xsl:template> -->

</xsl:stylesheet>