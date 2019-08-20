<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0"> 
	<xsl:import href="user_data_inputs.xsl"/>
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="is_jur" select="page/user_jur//@validation-error or page/user_jur/organization != '' or page/jur"/>

	<xsl:variable name="title" select="'Список товаров'" />

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
						<xsl:for-each select="page/cart/bought">
							<xsl:variable name="p" select="product"/>
							<xsl:variable name="has_price" select="if ($is_reg_jur) then ($p/price_opt and $p/price_opt != '0') else ($p/price and $p/price != '0')"/>
							<xsl:variable name="price" select="if ($is_reg_jur and $has_price) then f:number_decimal(f:num($p/price_opt) div 100 * (100 - $discount)) else f:num($p/price)"/>

							<xsl:variable name="price_old" select="if ($is_reg_jur) then $p/price_opt_old else $p/price_old"/>

							<xsl:variable name="discount_percent" select="f:discount(string($price), $price_old)"/>
							<xsl:variable name="available_qty" select="if ($p/qty and f:num($p/qty) &gt; 0) then f:num($p/qty) else 0"/>
							<xsl:variable name="max" select="if ($available_qty &gt; 0) then $available_qty else 1000000"/>

							<xsl:variable name="price_out" select="if ($price != 0) then concat($price, ' p.') else 'по запросу'"/>
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
										<xsl:value-of select="$price_out"/>
<!-- UPDATE 10.06/2019 discount label -->
					<xsl:if test="$discount_percent != ''">
						<span class="discount" style="color: red; font-weight: bold;">Скидка: <xsl:value-of select="$discount_percent" />%</span>
					</xsl:if>	
<!-- END_UPDATE 10.06/2019 discount label -->
										<xsl:if test="not_available = '1'"><br/>нет в наличии - под заказ</xsl:if>
									</p>
								</div>
								<div class="quantity">
									<span>Кол-во</span>
									<input type="number" value="{f:num(qty_total)}" name="{input/qty/@input}" min="0" max="{$max}"/>
								</div>
								<!-- <div class="price all"><p><span>Сумма позиц.</span><xsl:value-of select="$sum"/></p></div> -->
								<a href="{delete}" class="delete"><i class="fas fa-times"/></a>
							</div>
						</xsl:for-each>
						<div class="total">
							<xsl:if test="page/cart/sum_discount != '0'">
								<p>Итого: <xsl:value-of select="f:currency_decimal(page/cart/sum_discount)"/> р.</p>
							</xsl:if>
							<xsl:if test="f:num(page/cart/sum) &gt; f:num(page/cart/sum_discount)">
								<div class="discount-total">
									Итоговая скидка: <xsl:value-of select="round((f:num(page/cart/sum) - f:num(page/cart/sum_discount)) * 100) div 100"/> руб.
									Сумма без учета скидки: <xsl:value-of select="page/cart/sum"/> руб.
								</div>
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