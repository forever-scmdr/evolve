<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Список товаров'" />

	<xsl:variable name="processed" select="f:num(page/cart/processed) = 1"/>
	<xsl:variable name="h1" select="if($processed) then concat('Заявка №', page/cart/order_num) else 'Список товаров'"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="{$main_host}">Главная страница</a> &gt;
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1><xsl:value-of select="$h1"/></h1>

		<xsl:if test="$processed">
			<p>Заказ уже был успешно отправлен нашим менеджерам. С Вами свяжутся в ближайшее время для уточнения деталей.</p>
		</xsl:if>

		<div class="cart-container">
			<xsl:choose>
				<xsl:when test="page/cart/bought">
					<form method="post">
						<xsl:for-each select="page/cart/bought">
							<xsl:variable name="p" select="product"/>
							<xsl:variable name="price" select="if (f:num($p/price) != 0) then concat(f:pack($p/price, $p/pack_db, $p/pack), ' p.') else 'по запросу'"/>
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
									<xsl:if test="not($processed)">
										<input type="number" value="{qty}" name="{input/qty/@input}" min="0"/>
									</xsl:if>
									<xsl:if test="$processed">
										<xsl:value-of select="qty"/>
									</xsl:if>
								</div>
								<!-- <div class="price all"><p><span>Сумма позиц.</span><xsl:value-of select="$sum"/></p></div> -->
								<xsl:if test="not($processed)">
								<a href="{delete}" class="delete"><i class="fas fa-times"/></a>
								</xsl:if>
							</div>
						</xsl:for-each>
						<div class="total">
							<xsl:if test="page/cart/sum != '0'">
								<p>Итого: <xsl:value-of select="f:currency_decimal(page/cart/sum)"/> р.</p>
							</xsl:if>
							<xsl:if test="not($processed)">
								<input type="submit" class="button" value="Пересчитать" onclick="$(this).closest('form').attr('action', '{page/recalculate_link}')"/>
								<input type="submit" class="button" value="Продолжить" onclick="$(this).closest('form').attr('action', '{page/proceed_link}')"/>
							</xsl:if>
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