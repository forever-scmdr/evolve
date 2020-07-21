<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Список товаров'" />
	<xsl:variable name="cart" select="page/cart"/>
	<xsl:variable name="boughts" select="$cart/bought"/>
	<xsl:variable name="prods" select="$boughts/product"/>
	<xsl:variable name="no_weight" select="//page/product/params[not(param[starts-with(lower-case(@caption), 'вес')])] or //page/product/params/param[starts-with(lower-case(@caption), 'вес')] = ''"/>

	<xsl:variable name="weights"><xsl:for-each select="$boughts"><xsl:value-of select="f:num(qty) * f:num(//page/product[code = current()/code]/params/param[starts-with(lower-case(@caption), 'вес')])" />,</xsl:for-each></xsl:variable>

	<xsl:variable name="gifts" select="page/gifts/gift_section"/>
	<xsl:variable name="min_gift" select="f:num($gifts[1]/sum)"/>

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

							<xsl:variable name="price" select="if (f:num($p/price) != 0) then concat(f:currency_decimal(string(f:num($p/price) * f:num($cart/discount))), ' р.') else 'запрошена у продавца'"/>
							<xsl:variable name="sum" select="if (f:num($p/price) != 0) then concat(f:currency_decimal(sum), ' p.') else ''"/>
							<div class="item">
								<xsl:if test="not($p/product)">
									<a href="{$p/show_product}" class="image-container">
										<img src="{$p/@path}{$p/main_pic}" alt="{$p/name}"/>
									</a>
									<a href="{$p/show_product}" class="title">
										<xsl:value-of select="$p/name"/>
									</a>

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
									<xsl:if test="$weight != 0">
										<p>
											<span>Вес</span>
											<xsl:value-of select="$weight * f:num(qty)" /> кг
										</p>
									</xsl:if>
								</div>
								<div class="price one">
									<p>
										<span>Цена</span>
										<xsl:if test="not(starts-with($p/code, 'gift-'))"><xsl:value-of select="$price"/></xsl:if>
										<xsl:if test="starts-with($p/code, 'gift-')"><b style="color:red;">Подарок</b></xsl:if>
									</p>
								</div>
								<div class="quantity">
									<span>Кол-во</span>
									<input type="number" value="{qty}" name="{input/qty/@input}" min="0"/>
									<!--<xsl:value-of select="$p2/params/@id" />-->
								</div>
								<!-- <div class="price all"><p><span>Сумма позиц.</span><xsl:value-of select="$sum"/></p></div> -->
								<a href="{delete}" class="delete"><i class="fas fa-times"/></a>
							</div>
						</xsl:for-each>
						<p><a href="dostavka#calculator">Узнать стоимость доставки</a></p>
						<xsl:if test="(f:num($cart/sum) &gt;= $min_gift) and $gifts/product">
							<p><a onclick="$('#gifts').show();">Выбрать подарок</a></p>
						</xsl:if>
						<xsl:if test="(not(f:num($cart/sum) &gt;= $min_gift)) and $gifts/product">
							<p>Сделайте заказ на сумму от <b><xsl:value-of select="f:currency_decimal($gifts[1]/sum)" /> р.</b> чтобы получить <a onclick="$('#gifts').show();">подарок</a></p>
						</xsl:if>

						<div class="total">
							<input type="submit" class="button" value="Пересчитать" onclick="$(this).closest('form').attr('action', '{page/recalculate_link}')"/>
							<input type="submit" class="button" value="Продолжить" onclick="$(this).closest('form').attr('action', '{page/proceed_link}')"/>
							<xsl:if test="page/cart/sum != '0'">
								<p>Итого:
									<xsl:if test="page/cart/simple_sum">
										<span style="text-decoration: line-through; padding-right: 10px; color: #ccc;"><xsl:value-of select="page/cart/simple_sum"/> р.</span>
									</xsl:if>
									<xsl:value-of select="f:currency_decimal(page/cart/sum)"/> р.
								</p>
								<p class="{if($no_weight) then 'no-data' else ''}">Общий вес: <span><xsl:value-of select="format-number(sum(for $s in tokenize($weights, ',') return f:num($s)),'#0.00')"/></span> кг.</p>
							</xsl:if>
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
		<xsl:call-template name="GIFTS" />
		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

	<xsl:template name="GIFTS">
		<xsl:if test="$gifts/product">
			<div class="popup" style="display: none;" id="gifts">
				<div class="popup__body">
				<div class="popup__content">
					<a class="popup__close" onclick="$('#gifts').hide();">×</a>
					<div class="popup__title title title_2">Доступны подарки</div>
					<div class="gift-list">
						<xsl:for-each select="$gifts">
							<xsl:variable name="sum" select="f:num(sum)"/>
							<xsl:for-each select="product">
								<xsl:variable name="code" select="code"/>

								<div class="gift-list__item gift">
									<a class="gift__image" href="{show_product}">
										<img src="{concat(@path, main_pic)}" />
									</a>

									<a href="{show_product}" class="gift__name"><xsl:value-of select="name"/></a>

									<div class="gift__status" id="cart_list_g_{@id}">
										<xsl:if test="not(//bought[code = $code]) and $sum &lt;= f:num($cart/sum)">
											<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_g_{@id}">
												<input type="hidden" name="qty" value="1"/>
												<input type="hidden" name="sum" value="{$sum}"/>
												<input type="submit" class="button" value="Выбрать"/>
											</form>
										</xsl:if>
										<xsl:if test="//bought[code = $code]">
											<div class="gift__status">
												Вы выбрали этот подарок
											</div>
										</xsl:if>
										<xsl:if test="$sum &gt; f:num($cart/sum)">
											<div class="gift__status">
												<div class="gift__sum">+<xsl:value-of select="f:currency_decimal(string($sum - f:num($cart/sum)))"/> руб</div>
												<div class="small-text">Добавьте в корзину товар на эту сумму, чтобы получить подарок</div>
											</div>
										</xsl:if>
									</div>
								</div>
							</xsl:for-each>
						</xsl:for-each>
					</div>
				</div>
				</div>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>