<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Список товаров'" />


	<xsl:template match="bought">
		<xsl:variable name="p" select="product"/>
		<xsl:variable name="has_price" select="f:num($p/price) != 0"/>
		<xsl:variable name="price" select="if ($has_price) then concat(f:currency_decimal($p/price), ' p.') else 'по запросу'"/>
		<div class="item">
			<xsl:if test="not($p/product)">
				<a href="{$p/show_product}" class="image-container">
					<img src="{$p/@path}{$p/main_pic}" alt="{$p/name}"/>
				</a>
				<a href="{$p/show_product}" class="title"><xsl:value-of select="$p/name"/></a>
			</xsl:if>
			<xsl:if test="$p/product">
				<a href="{$p/product/show_product}" class="image-container">
					<img src="{$p/product/@path}{$p/product/main_pic}" alt="{$p/name}"/>
				</a>
				<a href="{$p/product/show_product}" class="title">
					<xsl:value-of select="$p/name"/> (<xsl:value-of select="$p/product/name" />)
				</a>
			</xsl:if>
			<div class="price one">
				<xsl:if test="$has_price">
					<p>
						<span>Цена</span>
						<xsl:value-of select="$price"/>
					</p>
				</xsl:if>
			</div>
			<div class="quantity">
				<span>Кол-во</span>
				<input type="number" value="{qty}" name="{input/qty/@input}" min="0"
					   max="{if ($p/qty and f:num($p/qty) != 0) then $p/qty else ''}"/>
			</div>
			<!-- <div class="price all"><p><span>Сумма позиц.</span><xsl:value-of select="$sum"/></p></div> -->
			<a href="{delete}" class="delete"><i class="fas fa-times"/></a>
		</div>
	</xsl:template>


	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="{$main_host}">Главная страница</a> <i class="fas fa-angle-right"></i>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title">Список товаров</h1>

		<div class="cart-container">
			<xsl:choose>
				<xsl:when test="page/cart/bought and not(page/cart/processed = '1')">
					<form method="post">
						<xsl:apply-templates select="page/cart/bought[f:num(product/price) != 0]"/>
						<xsl:if test="page/cart/bought[f:num(product/price) = 0]">
							<h3>Товары не в наличии:</h3>
							<span>Эти товары не будут включены в заказ и оплату</span>
							<xsl:apply-templates select="page/cart/bought[f:num(product/price) = 0]"/>
						</xsl:if>
						<div class="total">
							<xsl:if test="page/cart/sum != '0'">
								<p>Итого: <xsl:value-of select="f:currency_decimal(page/cart/sum)"/> р.</p>
							</xsl:if>
							<input type="submit" class="button" value="Пересчитать" onclick="$(this).closest('form').attr('action', '{page/recalculate_link}')"/>
							<input type="submit" class="button" value="Продолжить" onclick="$(this).closest('form').attr('action', '{page/proceed_link}')"/>
						</div>
					</form>
				</xsl:when>
				<xsl:otherwise>
					<h3>Корзина пуста</h3>
				</xsl:otherwise>
			</xsl:choose>
		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

</xsl:stylesheet>