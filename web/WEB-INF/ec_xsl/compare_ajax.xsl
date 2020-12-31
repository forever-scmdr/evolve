<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f"
		version="2.0">
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="prod" select="page/product"/>

	<xsl:template match="/">
		<xsl:if test="$prod">
			<div class="result" id="compare_ajax">
				<a href="{page/compare_link}" rel="nofollow"><i class="fas fa-balance-scale"/>Сравнение1 (<xsl:value-of select="count($prod)"/>)</a>
			</div>
			<xsl:for-each select="$prod">
				<div class="result" id="compare_list_{@id}">
					<a href="{//page/compare_link}" class="icon-link device__action-link device__action-link_active" rel="nofollow">
						<i class="fas fa-balance-scale"></i>сравнение2
					</a>
				</div>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="not($prod)">
			<div class="result" id="compare_ajax">
				<a><i class="fas fa-balance-scale"/>Сравнение3</a>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>