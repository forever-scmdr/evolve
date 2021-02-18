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
			<!-- ссылка в шапке -->
			<div class="result" id="fav_ajax" rel="nofollow">
        <div class="header-icon__icon">
          <img src="img/icon-star.png" alt="" />
          <div class="header-icon__label"><xsl:value-of select="count($prod)"/></div>
        </div>
        <a class="header-icon__link" href="{page/fav_link}"></a>
			</div>

      <!-- избранное в списке товаров и на странице товара -->
			<xsl:for-each select="$prod">
				<div class="result" id="fav_list_{@id}">
					<a href="{//page/fav_link}" class="add__item icon-link">
						<div class="icon"><img src="img/icon-star-active.svg" alt="" /></div>
						<span>Избранное</span>
					</a>
				</div>
			</xsl:for-each>
		</xsl:if>

    <!-- шапка, ничего не добавлено в избранное -->
		<xsl:if test="not($prod)">
			<div class="result" id="fav_ajax">
				<div class="header-icon__icon">
          <img src="img/icon-star.png" alt="" />
        </div>
			</div>
		</xsl:if>
	</xsl:template>


</xsl:stylesheet>
