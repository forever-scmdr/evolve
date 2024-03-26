<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f"
		version="2.0">
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes" exclude-result-prefixes="#all"/>

	<xsl:variable name="is_advanced" select="false()"/>
	<xsl:variable name="products" select="page/product | page/plain_catalog/product | page/catalog/product"/>


	<xsl:template match="/">
		<div class="result" id="search-result">
			<xsl:if test="not($products)">
				По Вашему запросу ничего не найдено.
			</xsl:if>
			<xsl:if test="$products">
				<xsl:if test="not($is_advanced)">
					<ul>
						<xsl:for-each select="$products">
							<li>
								<a href="{show_product}">
									<xsl:value-of select="name"/>
								</a>
							</li>
						</xsl:for-each>
					</ul>
				</xsl:if>
				<xsl:if test="$is_advanced">
					<div class="cart-container">
						<xsl:for-each select="$products">
							<div class="item">
								<a href="{show_product}" class="image-container">
									<img src="{@path}{main_pic}" alt=""/>
								</a>
								<a href="{show_product}" class="title">
									<xsl:value-of select="name"/>
									<p/>
									<span>
										№ для заказа: <xsl:value-of select="code"/>
									</span>
								</a>
								<div class="price one">
									<a class="button" href="#" onclick="showDetails('{show_lines_ajax}'); return false;" style="padding: 5px">Цены</a>
<!--									<span>Цены</span><xsl:value-of select="f:exchange_cur(., $price_param_name, 'под заказ')"/>-->
								</div>
							</div>
						</xsl:for-each>
					</div>
				</xsl:if>
				<a class="show-all" href="{page/show_all}"><strong>Показать все результаты</strong></a>
			</xsl:if>
		</div>

		<div class="result" id="search-result-mobile">
			<xsl:if test="not($products)">
				По Вашему запросу ничего не найдено.
			</xsl:if>
			<xsl:if test="$products">
				<div class="cart-container">
					<xsl:for-each select="$products">
						<div class="item">
							<a href="{show_product}" class="image-container">
								<img src="{@path}{main_pic}" alt=""/>
							</a>
							<a href="{show_product}" class="title">
								<xsl:value-of select="name"/>
								<br />
								<span>
									№ для заказа: <xsl:value-of select="code"/>
								</span>
								<div class="price one"><span>Смотреть цены</span></div>
							</a>
						</div>
					</xsl:for-each>
				</div>
				<a class="show-all" href="{//search_link}"><strong>Показать все результаты</strong></a>
			</xsl:if>
		</div>

	</xsl:template>

</xsl:stylesheet>
