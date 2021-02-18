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
    <!-- ссылка в шапке -->
		<xsl:if test="$prod">
			<div class="result" id="compare_ajax">
        <div class="header-icon__icon">
          <img src="img/icon-compare.png" alt="" />
          <div class="header-icon__label"><xsl:value-of select="count($prod)"/></div>
        </div>
        <a class="header-icon__link" href="{page/compare_link}" rel="nofollow"></a>
			</div>

      <!-- ссылка в товаре -->
			<xsl:for-each select="$prod">
				<div class="result" id="compare_list_{@id}">
					<a href="{//page/compare_link}" class="add__item icon-link">
						<div class="icon"><img src="img/icon-balance-active.svg" alt="" /></div>
						<span>Сравнение</span>
					</a>
				</div>
			</xsl:for-each>
		</xsl:if>
    
    <!-- шапка, ничего дне добавлено в сравнение -->
		<xsl:if test="not($prod)">
			<div class="result" id="compare_ajax">
				<div class="header-icon__icon">
          <img src="img/icon-compare.png" alt="" />
        </div>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
