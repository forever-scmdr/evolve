<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Ваш заказ'" />

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a> &gt;
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1>Ваш заказ</h1>

		<div class="cart-container">
			<xsl:choose>
				<xsl:when test="page/cart/bought and not(page/cart/processed = '1')">
					<form method="post">
						<xsl:for-each select="page/cart/bought">
							<xsl:variable name="p" select="product"/>
							<xsl:variable name="price" select="if ($p/price) then concat($p/price, ' p.') else 'под заказ'"/>
							<xsl:variable name="sum" select="if ($p/price) then concat(sum, ' p.') else 'под заказ'"/>
							<div class="item">
								<a href="{$p/show_product}" class="image-container">
									<img src="{$p/@path}{$p/main_pic}" alt=""/>
								</a>
								<a href="{$p/show_product}" class="title"><xsl:value-of select="$p/name"/></a>
								<div class="price one"><p><span>Цена за ед.</span><xsl:value-of select="$price"/></p></div>
								<div class="quantity">
									<span>Кол-во</span>
									<input type="number" value="{qty}" name="{input/qty/@input}" min="0"/>
								</div>
								<div class="price all"><p><span>Сумма позиц.</span><xsl:value-of select="$sum"/></p></div>
								<a href="{delete}" class="delete"><i class="fas fa-times"/></a>
							</div>
						</xsl:for-each>

						<div class="total">
							<p>Итого:
								<xsl:if test="f:num(page/cart/simple_sum) &gt; f:num(page/cart/sum)">
								<span style="text-decoration: line-through; padding-right: 10px; color: #ccc;"><xsl:value-of select="page/cart/simple_sum"/> р.</span>
								</xsl:if>
								<xsl:value-of select="page/cart/sum"/> р.</p>
							<div>
							<input type="submit" value="Пересчитать" onclick="$(this).closest('form').attr('action', '{page/recalculate_link}')"/>
							<input type="submit" value="Продолжить" onclick="$(this).closest('form').attr('action', '{page/proceed_link}')"/>
						</div>
						</div>
						<xsl:if test="f:num(page/cart/simple_sum) &gt; f:num(page/cart/sum)">
							<div class="total" style="margin-top:0;">
								<p>Сэкономьте: <xsl:value-of select="f:currency_decimal(string(f:num(page/cart/simple_sum) - f:num(page/cart/sum)))"/> р.</p>
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