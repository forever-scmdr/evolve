<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:f="f:f"
	version="2.0"
	exclude-result-prefixes="xsl f">
	<xsl:import href="../styles_fwk/utils.xsl"/>
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="desc_div" select="//div[@class='desc']"/>
	<xsl:variable name="table" select="$desc_div//table[@class='info-table']"/>
	<xsl:variable name="id" select="normalize-space($table//tr[normalize-space(td[1]) = 'Mfr. Part #']/td[2])"/>


	<xsl:template match="/">
		<result>
			<catalog id="catalog">
				<name>Catalog</name>
			</catalog>
			<product id="{$id}">
				<h_parent parent="catalog" element="catalog"/>
				<parameter_1>
					<name>Name</name>
					<value><xsl:value-of select="normalize-space($desc_div//h1)" /></value>
				</parameter_1>
				<parameter_2>
					<name>Manufacturer</name>
					<value><xsl:value-of select="normalize-space($table//tr[normalize-space(td[1]) = 'Manufacturer']/td[2])" /></value>
				</parameter_2>
				<parameter_3>
					<name>Mfr. Part #</name>
					<value><xsl:value-of select="normalize-space($table//tr[normalize-space(td[1]) = 'Mfr. Part #']/td[2])" /></value>
				</parameter_3>
				<parameter_4>
					<name>LCSC Part #</name>
					<value><xsl:value-of select="normalize-space($table//tr[normalize-space(td[1]) = 'LCSC Part #']/td[2])" /></value>
				</parameter_4>
				<parameter_5>
					<name>Description</name>
					<value><xsl:value-of select="normalize-space($table//tr[normalize-space(td[1]) = 'Description']/td[2])" /></value>
				</parameter_5>
			</product>
		</result>


	</xsl:template>



</xsl:stylesheet>