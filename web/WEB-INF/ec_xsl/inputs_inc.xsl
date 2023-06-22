<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>



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

	<xsl:template name="simple_check_option">
		<xsl:param name="value"/>
		<xsl:param name="check"/>
		<xsl:choose>
			<xsl:when test="$value = $check">
				<option value="{$value}" selected="selected"><xsl:value-of select="$check"/></option>
			</xsl:when>
			<xsl:otherwise>
				<option value="{$value}"><xsl:value-of select="$check"/></option>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="simple_check_options">
		<xsl:param name="values"/>
		<xsl:param name="check"/>
		<xsl:for-each select="$values">
			<xsl:choose>
				<xsl:when test=". = $check">
					<option value="{.}" selected="selected"><xsl:value-of select="."/></option>
				</xsl:when>
				<xsl:otherwise>
					<option value="{.}"><xsl:value-of select="."/></option>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="check_radio">
		<xsl:param name="value"/>
		<xsl:param name="check"/>
		<xsl:param name="name"/>
		<xsl:choose>
			<xsl:when test="$value = $check">
				<input name="{$name}" type="radio" group="{$name}" checked="checked" value="{$value}" />
			</xsl:when>
			<xsl:otherwise>
				<input name="{$name}" type="radio" group="{$name}" value="{$value}" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="check_checkbox">
		<xsl:param name="value"/>
		<xsl:param name="check"/>
		<xsl:param name="name"/>
		<xsl:choose>
			<xsl:when test="$value = $check">
				<input name="{$name}" type="checkbox" checked="checked" value="{$value}" />
			</xsl:when>
			<xsl:otherwise>
				<input name="{$name}" type="checkbox" value="{$value}" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="check_checkbox_style">
		<xsl:param name="value"/>
		<xsl:param name="check"/>
		<xsl:param name="name"/>
		<xsl:param name="style"/>
		<xsl:choose>
			<xsl:when test="$value = $check">
				<input name="{$name}" type="checkbox" checked="checked" value="{$value}" style="{$style}"/>
			</xsl:when>
			<xsl:otherwise>
				<input name="{$name}" type="checkbox" value="{$value}" style="{$style}"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Вставка переменной в ссылку (добавление как query string). match соответствует ссылке -->
	<xsl:template match="*" mode="querystr_var">
		<xsl:param name="name"/>
		<xsl:param name="value"/>
		<xsl:if test="contains(., '?')">
			<xsl:value-of select="."/>&amp;<xsl:value-of select="$name"/>=<xsl:value-of select="$value"/>
		</xsl:if>
		<xsl:if test="not(contains(., '?'))">
			<xsl:value-of select="."/>?<xsl:value-of select="$name"/>=<xsl:value-of select="$value"/>
		</xsl:if>
	</xsl:template>


</xsl:stylesheet>