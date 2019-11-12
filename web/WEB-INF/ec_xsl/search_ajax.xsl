<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
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
				<div class="cart-container">
					<xsl:for-each select="page/product">
						<xsl:variable name="price" select="if (price) then concat(price, ' p.') else 'под заказ'"/>
						<div class="item">
							<a href="{show_product}" class="image-container">
								<img src="{@path}{main_pic}" alt=""/>
							</a>
							<a href="{show_product}" class="title">
								<xsl:value-of select="name"/>
<!--								<span>-->
<!--									№ для заказа: <xsl:value-of select="code"/>-->
<!--								</span>-->
							</a>
							<div class="price one"><span>Цена</span><xsl:value-of select="$price"/></div>
						</div>
					</xsl:for-each>
				</div>
				<a class="show-all" href="{//search_link}"><strong>Показать все результаты</strong></a>
			</xsl:if>
		</div>
		<div class="result" id="search-result-mobile">
			<xsl:if test="not(page/product)">
				По Вашему запросу ничего не найдено.
			</xsl:if>
			<xsl:if test="page/product">
				<div class="cart-container">
					<xsl:for-each select="page/product">
						<xsl:variable name="price" select="if (price) then concat(price, ' p.') else 'под заказ'"/>
						<div class="item">
							<a href="{show_product}" class="image-container">
								<img src="{@path}{main_pic}" alt=""/>
							</a>
							<a href="{show_product}" class="title">
								<xsl:value-of select="name"/>
								<br />
<!--								<span>-->
<!--									№ для заказа: <xsl:value-of select="code"/>-->
<!--								</span>-->
								<div class="price one"><span>Цена</span><xsl:value-of select="$price"/></div>
							</a>
						</div>
					</xsl:for-each>
				</div>
				<a class="show-all" href="{//search_link}"><strong>Показать все результаты</strong></a>
			</xsl:if>
		</div>
    </xsl:template>

</xsl:stylesheet>