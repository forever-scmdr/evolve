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
			<xsl:apply-templates select="$params_xml_item" />
			<!-- <xsl:for-each select="page/product">
				<div class="compare-items__item">
					<xsl:apply-templates select="." />
					<ul class="compare-items__params">
						<xsl:for-each select="params/param">
							<li>
								<span><xsl:value-of select="@caption"/></span>
								<span><xsl:value-of select="."/></span>
							</li>
						</xsl:for-each>
					</ul>
				</div>
			</xsl:for-each> -->
		</div>
	</xsl:template>

	<xsl:template match="params_xml">
		<xsl:variable name="groups" select="parse-xml(concat('&lt;params&gt;', xml, '&lt;/params&gt;'))"/>

		<xsl:for-each select="$products">
			<xsl:variable name="product_params" select="params/param"/>
			<div class="compare-items__item">
				<xsl:apply-templates select="." />
				<ul class="compare-items__params">
					<xsl:for-each select="$groups/params/*">
						<xsl:apply-templates select=".">
							<xsl:with-param name="values" select="$product_params"/>
						</xsl:apply-templates>
					</xsl:for-each>
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
		<li><b><xsl:value-of select="if(@name != '') then @name else 'Дополнительно'"/></b></li>
		<xsl:apply-templates select="parameter">
			<xsl:with-param name="values" select="$values"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="parameter">
		<xsl:param name="values"/>
		<xsl:variable name="v" select="$values[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()/name))]" />
		<li>
			<span><xsl:value-of select="name"/></span>
			<span><xsl:value-of select="$v"/></span>
		</li>
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