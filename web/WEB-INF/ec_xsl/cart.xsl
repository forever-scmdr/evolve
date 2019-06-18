<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Список товаров'" />
	<xsl:variable name="boughts" select="page/cart/bought"/>
	<xsl:variable name="prods" select="$boughts/product"/>
	<xsl:variable name="no_weight" select="//page/product/params[not(param[starts-with(lower-case(@caption), 'вес')])] or //page/product/params/param[starts-with(lower-case(@caption), 'вес')] = ''"/>

	<xsl:variable name="weights"><xsl:for-each select="$boughts"><xsl:value-of select="f:num(qty) * f:num(//page/product[code = current()/code]/params/param[starts-with(lower-case(@caption), 'вес')])" />,</xsl:for-each></xsl:variable>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="{$main_host}">Главная страница</a> <i class="fas fa-angle-right"></i>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title">Корзина</h1>

		<div class="cart-container">
			<xsl:choose>
				<xsl:when test="page/cart/bought and not(page/cart/processed = '1')">
					<form method="post">
						<xsl:for-each select="$boughts">
							<xsl:variable name="p" select="product"/>
							<xsl:variable name="p2" select="//page/product[code = $p/code]"/>

							<xsl:variable name="weight" select="f:num($p2/params/param[starts-with(lower-case(@caption), 'вес')])"/>

							<xsl:variable name="price" select="if (f:num($p/price) != 0) then concat(f:currency_decimal($p/price), ' p.') else 'по запросу'"/>
							<xsl:variable name="sum" select="if (f:num($p/price) != 0) then concat(f:currency_decimal(sum), ' p.') else ''"/>
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
									<p>
										<span>Цена</span>
										<xsl:value-of select="$price"/>
									</p>
								</div>
								<div class="quantity">
									<span>Кол-во</span>
									<input type="number" value="{qty}" name="{input/qty/@input}" min="0"/>
									<xsl:if test="$weight != 0">
										<p>вес: <xsl:value-of select="$weight * f:num(qty)" /> кг</p>
									</xsl:if>
									<!--<xsl:value-of select="$p2/params/@id" />-->
								</div>
								<!-- <div class="price all"><p><span>Сумма позиц.</span><xsl:value-of select="$sum"/></p></div> -->
								<a href="{delete}" class="delete"><i class="fas fa-times"/></a>
							</div>
						</xsl:for-each>

						<div class="total">
							<!--<xsl:if test="page/cart/simple_sum">-->
								<!--<span style="text-decoration: line-through; padding-right: 10px; color: #ccc;"><xsl:value-of select="page/cart/simple_sum"/> р.</span>-->
							<!--</xsl:if>-->

							<xsl:if test="page/cart/sum != '0'">
								<p>Итого: <xsl:value-of select="f:currency_decimal(page/cart/sum)"/> р.</p>
								<p class="{if($no_weight) then 'no-data' else ''}">Общий вес: <span><xsl:value-of select="format-number(sum(for $s in tokenize($weights, ',') return f:num($s)),'#0.00')"/></span> кг.</p>
							</xsl:if>

							<input type="submit" class="button" value="Пересчитать" onclick="$(this).closest('form').attr('action', '{page/recalculate_link}')"/>
							<input type="submit" class="button" value="Продолжить" onclick="$(this).closest('form').attr('action', '{page/proceed_link}')"/>
						</div>
						<xsl:if test="page/cart/simple_sum">
							<div class="total">
								<p>Сэкономьте: <xsl:value-of select="format-number(f:num(page/cart/simple_sum) - f:num(page/cart/sum), '#0.00')"/> р.</p>
							</div>
						</xsl:if>
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