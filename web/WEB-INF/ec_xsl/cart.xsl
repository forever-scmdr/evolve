<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Список товаров'" />
	<xsl:variable name="cart" select="page/cart"/>

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
				<xsl:when test="page/cart/bought and not($cart/processed = '1')">
					<form method="post">
						<div id="cart-bought-list">
							<xsl:apply-templates select="$cart/bought[f:num(sum) != 0]" mode="avlb"/>
							<xsl:if test="page/cart/bought[f:num(sum) = 0] or page/cart/bought[f:num(qty_avail) = 0]">
								<h2>Товары под заказ</h2>
								<xsl:apply-templates select="$cart/bought" mode="preorder"/>
							</xsl:if>
						</div>
						<div class="total">
							<xsl:if test="page/cart/sum != '0'">
								<p>Итого: <xsl:value-of select="f:cart_sum($cart/sum)"/></p>
							</xsl:if>
							 <input type="submit" class="button inverted" value="Пересчитать" id="recalc" onclick="$(this).closest('form').attr('action', '{page/recalculate_link}')"/>
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

	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript">
			$(document).on('change', ".qty-input", function(){

				alert();

				$t = $(this);
				if($t.val() != $t.attr("data-old") &amp; validate($t.val())){
					$form = $(this).closest('form');
					$form.attr("action", '<xsl:value-of select="page/recalculate_link"/>');
					postForm($form);
				}else if(!validate($t.val())){
					if(validate($t.attr("data-old"))){
						$t.val($t.attr("data-old"));
					}else{
						$t.val("1");
					}
				}
			});

			function validate(val){
				return parseInt(val) &gt; 0;
			}
		</script>
	</xsl:template>


	<xsl:template match="bought" mode="avlb">

		<xsl:variable name="aux" select="aux"/>
		<xsl:variable name="is_aux" select="aux != ''"/>
		<xsl:variable name="shop" select="/page/shop[name = $aux]"/>
		<xsl:variable name="p" select="product"/>
		<xsl:variable name="price"  select="f:cart_sum(price)"/>
		<xsl:variable name="sum"  select="f:cart_sum(sum)"/>
		<xsl:variable name="img"  select="if(item_own_extras/img != '') then item_own_extras/img else 'img/no_image.png'"/>


		<div class="item">
			<xsl:if test="$is_aux">
				<span class="image-container">
					<img src="{$img}" alt="{$p/name}"/>
				</span>
				<span class="title">
					<xsl:value-of select="$p/name"/>
					<xsl:if test="$is_aux">
						<br/>
						<span style="color: #000;">поставка в течение <xsl:value-of select="$shop/delivery_string"/></span>
					</xsl:if>
				</span>
			</xsl:if>
			<xsl:if test="not($is_aux)">
				<a href="{$p/show_product}" class="image-container">
					<img src="{$img}" alt="{$p/name}"/>
				</a>
				<a href="{$p/show_product}" class="title">
					<xsl:value-of select="$p/name"/>
				</a>
			</xsl:if>

			<div class="quantity">
				<span>Кол-во</span>
				<input type="number" value="{qty}" name="{input/qty/@input}" min="{product/min_qty}"
					   step="{product/min_qty}" class="qty-input" data-old="{qty}"/>
				<br/>
				<span>Наличие</span>
				доступно:
				<xsl:value-of select="f:num($p/qty)"/>
			</div>

			<div class="price one">
				<p>
					<span>Цена</span>
					<xsl:value-of select="$price"/>
					<xsl:if test="$p/spec_price">
						<xsl:variable name="sq" select="$p/spec_qty"/>
						<xsl:variable name="x">
							<xsl:for-each select="$p/spec_price">
								<xsl:variable name="pos" select="position()"/>
								<xsl:variable name="q" select="$sq[$pos]"/>
								<xsl:variable name="display_spec" select="f:price_ictrade(.)"/>
								<xsl:value-of select="concat($q, '+ ', '&lt;strong&gt;', $display_spec,' ', upper-case($curr), '&lt;/strong&gt;', '&lt;br/&gt;')"/>
							</xsl:for-each>
						</xsl:variable>
						<br/>
						<a data-container="body" data-html="true" data-toggle="popover" data-placement="top" data-content="{$x}" style="font-size:12px;">подробнее</a>
					</xsl:if>
				</p>
			</div>

			<div class="price all">
				<p>
					<span>Сумма позиц.</span>
					<xsl:value-of select="$sum"/>
				</p>
			</div>
			<a href="{delete}" class="delete">
				<i class="fas fa-times"/>
			</a>

		</div>

	</xsl:template>

	<xsl:template match="bought" mode="preorder">
		<xsl:if test="f:num(product/price) = 0 or f:num(qty_avail) = 0">
			<xsl:variable name="p" select="product" />
			<xsl:variable name="is_aux" select="aux != ''" />
			<xsl:variable name="img" select="if(item_own_extras/img != '') then item_own_extras/img else 'img/no_image.png'"/>
			<xsl:variable name="main_pic" select="if($p/main_pic != '') then concat($p/@path, $p/main_pic) else 'img/no_image.png'"/>

			<div class="item">
				<xsl:if test="$is_aux">

					<span class="image-container">
						<img src="{$img}" alt="{$p/name}"/>
					</span>
					<span class="title">
						<xsl:value-of select="$p/name"/> (<xsl:value-of select="$p/product/name" />)
					</span>
				</xsl:if>
				<xsl:if test="not($is_aux)">
					<a href="{$p/show_product}" class="image-container">
						<img src="{$main_pic}" alt="{$p/name}"/>
					</a>
					<a href="{$p/show_product}" class="title">
						<xsl:value-of select="$p/name"/>
					</a>
				</xsl:if>

				<div class="quantity">
					<span>Кол-во</span>
					<input type="number" value="{qty}" name="{input/qty/@input}" min="1" class="qty-input" data-old="{qty}" />
				</div>

				<div class="price one">
					<p>
						<span>Цена</span>
						-
					</p>
				</div>

				<div class="price all"><p><span>Сумма позиц.</span> - </p></div>
				<a href="{delete}" class="delete"><i class="fas fa-times"/></a>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>