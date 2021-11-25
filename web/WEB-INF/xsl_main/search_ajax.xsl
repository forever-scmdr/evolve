<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="utils/utils.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes" exclude-result-prefixes="#all"/>

	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="success" select="page/variables/result = 'success'"/>
	<xsl:variable name="form" select="page/feedback_form"/>
	<xsl:variable name="is_advanced" select="page/optional_modules/display_settings/catalog_quick_search = 'advanced'"/>
	<xsl:variable name="is_jur" select="page/registration[@type = 'user_jur']"/>
	<xsl:variable name="jur_price_on" select="page/optional_modules/display_settings/jur_price = 'on'"/>
	<xsl:variable name="price_param_name" select="if ($is_jur and $jur_price_on) then 'price_opt' else 'price'"/>
	<xsl:variable name="price_old_param_name" select="if ($is_jur and $jur_price_on) then 'price_opt_old' else 'price_old'"/>


	<xsl:template name="result">
		<xsl:param name="result_id"/>
		<div class="result" id="{$result_id}">
			<xsl:if test="not(page/product)">
				<div class="suggest__text">По Вашему запросу ничего не найдено</div>
			</xsl:if>
			<xsl:if test="page/product">
				<div class="suggest__text">Продолжайте вводить текст или выберите результат</div>
				<div class="suggest__results">
					<xsl:apply-templates select="page/product"/>
				</div>
				<a class="suggest__all" href="{page/show_all}">Показать все результаты</a>
			</xsl:if>
		</div>
	</xsl:template>


	<xsl:template match="product">
		<xsl:variable name="zero" select="not(is_service = '1') and f:num(qty) &lt; 0.001"/>
		<xsl:variable name="has_price" select="price and f:num(price) &gt; 0.001"/>
		<div class="suggest__result suggest-result">
			<a class="suggest-result__link" href="{show_product}"><xsl:value-of select="string-join((name, name_extra), ' ')"/></a>
<!-- 			<div class="suggest-result__info">
				<div class="suggest-result__code">(код <xsl:value-of select="code"/>)</div>
				<div class="suggest-result__vendor"><xsl:value-of select="vendor"/></div>
				<div class="suggest-result__price">
					<xsl:if test="$has_price">
						<xsl:value-of select="price"/> руб./<xsl:value-of select="unit"/>
					</xsl:if>
					<xsl:if test="not($has_price)">
						нет цены
					</xsl:if>
				</div>
				<div class="suggest-result__status">
					<xsl:if test="not($zero) and not(is_service = '1')">
						на складе: <strong><xsl:value-of select="concat(qty, ' ', unit)"/></strong>
					</xsl:if>
					<xsl:if test="$zero">
						<xsl:if test="soon != '0'">
							ожидается: <xsl:value-of select="substring(soon, 1, 10)"/>
						</xsl:if>
						<xsl:if test="not(soon != '0')">
							<div class="status__na">нет в наличии</div>
						</xsl:if>
					</xsl:if>
				</div>
			</div> -->
			<span class="suggest-result__info"> 
				<span class="suggest-result__code">(код <xsl:value-of select="code"/>)</span>
				<span class="suggest-result__vendor"><xsl:value-of select="vendor"/></span>
				<span class="suggest-result__price">
					<xsl:if test="$has_price">
						<xsl:value-of select="price"/> руб./<xsl:value-of select="unit"/>
					</xsl:if>
					<xsl:if test="not($has_price)">
						нет цены
					</xsl:if>
				</span>
				<span class="suggest-result__status">
					<xsl:if test="not($zero) and not(is_service = '1')">
						на складе: <strong><xsl:value-of select="concat(qty, ' ', unit)"/></strong>
					</xsl:if>
					<xsl:if test="$zero">
						<xsl:if test="soon != '0'">
							ожидается: <xsl:value-of select="substring(soon, 1, 10)"/>
						</xsl:if>
						<xsl:if test="not(soon != '0')">
							<span class="status__na">нет в наличии</span>
						</xsl:if>
					</xsl:if>
				</span>
			</span>
		</div>
	</xsl:template>


	<xsl:template match="/">
		<xsl:call-template name="result">
			<xsl:with-param name="result_id" select="'search-result'"/>
		</xsl:call-template>

		<xsl:call-template name="result">
			<xsl:with-param name="result_id" select="'search-result-mobile'"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
