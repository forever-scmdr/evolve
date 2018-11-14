<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]><xsl:stylesheet	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"	xmlns="http://www.w3.org/1999/xhtml"	xmlns:f="f:f"	version="2.0">	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>	<xsl:variable name="prod" select="page/product"/>	<xsl:template match="/">		<xsl:if test="$prod">			<div class="result" id="fav_ajax">				<p><i class="fas fa-star"/> <a href="{page/fav_link}">Избранное (<xsl:value-of select="count($prod)"/>)</a></p>			</div>			<xsl:for-each select="$prod">				<div class="result" id="fav_list_{code}">					<a href="{//page/fav_link}" class="icon-link device__action-link"><i class="fas fa-star"></i>в избранное</a>				</div>			</xsl:for-each>		</xsl:if>		<xsl:if test="not($prod)">			<div class="result" id="fav_ajax">				<p><i class="fas fa-star"/> <a>Избранное &#160;&#160;&#160;</a></p>			</div>		</xsl:if>	</xsl:template></xsl:stylesheet>