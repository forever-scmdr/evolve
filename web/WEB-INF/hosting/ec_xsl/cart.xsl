<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Список товаров'" />

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Главная страница</a> &gt;
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1>Список товаров</h1>

		<div class="cart-container">
			<xsl:choose>
				<xsl:when test="page/cart/bought and not(page/cart/processed = '1')">
					<form method="post">
						<xsl:for-each select="page/cart/bought">
							<xsl:variable name="p" select="product"/>
							<xsl:variable name="price" select="if (f:num($p/price) != 0) then concat($p/price, ' p.') else 'по запросу'"/>
							<xsl:variable name="sum" select="if (f:num($p/price) != 0) then concat(sum, ' p.') else ''"/>
							<div class="item">
								<a href="{$p/show_product}" class="image-container">
									<img src="{$p/@path}{$p/main_pic}" alt="{$p/name}"/>
								</a>
								<a href="{$p/show_product}" class="title"><xsl:value-of select="$p/name"/></a>
								<div class="price one">
									<p>
										<span>Цена</span>
										<xsl:value-of select="$price"/>
									</p>
								</div>
								<div class="quantity">
									<span>Кол-во</span>
									<input type="number" value="{qty}" name="{input/qty/@input}" min="0"/>
								</div>
								<!-- <div class="price all"><p><span>Сумма позиц.</span><xsl:value-of select="$sum"/></p></div> -->
								<a href="{delete}" class="delete"><i class="fas fa-times"/></a>
							</div>
						</xsl:for-each>
						<div class="total">
							<xsl:if test="page/cart/sum != '0'">
								<p>Итого: <xsl:value-of select="page/cart/sum"/> р.</p>
							</xsl:if>
							<input type="submit" value="Пересчитать" onclick="$(this).closest('form').attr('action', '{page/recalculate_link}')"/>
							<input type="submit" value="Продолжить" onclick="$(this).closest('form').attr('action', '{page/proceed_link}')"/>
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