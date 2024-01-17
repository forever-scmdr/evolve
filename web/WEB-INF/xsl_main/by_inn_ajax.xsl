<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="com" select="result/company/data"/>

	<xsl:template match="/">
		<div>
			<div class="result" id="input_organization"><xsl:value-of select="$com/name/short_with_opf" /></div>
			<div class="result" id="input_kpp"><xsl:value-of select="$com/kpp" /></div>
			<div class="result" id="input_address"><xsl:if test="$com" ><xsl:value-of select="$com/address/data/postal_code" />, <xsl:value-of select="$com/address/organization" /></xsl:if></div>
<!--			<div class="result" id="input_corp_email"><xsl:value-of select="$com/kpp" /></div>-->
<!--			<div class="result" id="input_phone">555</div>-->
			<div class="result" id="input_boss"><xsl:value-of select="$com/management/name" /></div>
			<div class="result" id="input_boss_position"><xsl:value-of select="$com/management/post" /></div>
		</div>
	</xsl:template>


</xsl:stylesheet>