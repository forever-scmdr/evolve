<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes" exclude-result-prefixes="#all"/>

	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="success" select="page/variables/result = 'success'"/>
	<xsl:variable name="form" select="page/feedback_form"/>

	<xsl:template match="/">
		<div class="result" id="search-result">
			<xsl:if test="not(page/product)">
				По Вашему запросу ничего не найдено.
			</xsl:if>
			<xsl:if test="page/product">
				<ul>
					<xsl:for-each select="page/product">
						<li>
							<a href="{show_product}">
								<xsl:value-of select="name"/>
							</a>
						</li>
					</xsl:for-each>
				</ul>
				<a class="show-all" href="{page/show_all}"><strong>Показать все результаты</strong></a>
			</xsl:if>
		</div>
	</xsl:template>

</xsl:stylesheet>
