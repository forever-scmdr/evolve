<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f"
		version="2.0">
	<xsl:import href="utils/utils.xsl"/>
	<xsl:import href="snippets/constants.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes" exclude-result-prefixes="#all"/>

	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="success" select="page/variables/result = 'success'"/>
	<xsl:variable name="form" select="page/feedback_form"/>
	<xsl:variable name="is_advanced" select="page/optional_modules/display_settings/catalog_quick_search = 'advanced'"/>
	<xsl:variable name="is_jur" select="page/registration[@type = 'user_jur']"/>
	<xsl:variable name="jur_price_on" select="page/optional_modules/display_settings/jur_price = 'on'"/>
	<xsl:variable name="price_param_name" select="if ($is_jur and $jur_price_on) then 'price_opt' else 'price'"/>
	<xsl:variable name="price_old_param_name" select="if ($is_jur and $jur_price_on) then 'price_opt_old' else 'price_old'"/>

	<xsl:template match="/">
		<div class="result" id="search-result">
			<xsl:if test="not(page/product)">
				По Вашему запросу ничего не найдено.
			</xsl:if>
			<xsl:if test="page/product">
				<xsl:if test="not($is_advanced)">
					<ul>
						<xsl:for-each select="page/product">
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
						<xsl:for-each select="page/product">
							<div class="item">
								<a href="{show_product}" class="image-container">
									<img src="{@path}{main_pic}" alt=""/>
								</a>
								<a href="{show_product}" class="title">
									<xsl:value-of select="name"/>
									<span>
										№ для заказа: <xsl:value-of select="code"/>
									</span>
								</a>
								<div class="price one"><span>Цена</span><xsl:value-of select="f:exchange_cur(., $price_param_name, 'под заказ')"/></div>
							</div>
						</xsl:for-each>
					</div>
				</xsl:if>
				<a class="show-all" href="{page/show_all}"><strong>Показать все результаты</strong></a>
			</xsl:if>
		</div>

		<div class="result" id="search-result-mobile">
			<xsl:if test="not(page/product)">
				По Вашему запросу ничего не найдено.
			</xsl:if>
			<xsl:if test="page/product">
				<div class="cart-container">
					<xsl:for-each select="page/product">
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
								<div class="price one"><span>Цена</span><xsl:value-of select="f:exchange_cur(., $price_param_name, 'под заказ')"/></div>
							</a>
						</div>
					</xsl:for-each>
				</div>
				<a class="show-all" href="{//search_link}"><strong>Показать все результаты</strong></a>
			</xsl:if>
		</div>

	</xsl:template>

</xsl:stylesheet>
