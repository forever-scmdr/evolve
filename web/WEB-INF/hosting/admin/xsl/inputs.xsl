<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>

	<xsl:template name="check_option">
		<xsl:param name="value"/>
		<xsl:param name="check"/>
		<xsl:param name="caption"/>
		<xsl:choose>
			<xsl:when test="$value = $check">
				<option value="{$value}" selected="selected"><xsl:value-of select="$caption"/></option>
			</xsl:when>
			<xsl:otherwise>
				<option value="{$value}"><xsl:value-of select="$caption"/></option>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="check_radio">
		<xsl:param name="value"/>
		<xsl:param name="check"/>
		<xsl:param name="name"/>
		<xsl:choose>
			<xsl:when test="$value = $check">
				<input name="{$name}" type="radio" group="qu" checked="checked" value="{$value}" id="{$value}" />
			</xsl:when>
			<xsl:otherwise>
				<input name="{$name}" type="radio" group="qu" value="{$value}" id="{$value}" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>