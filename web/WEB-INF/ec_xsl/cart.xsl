<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Список товаров'" />

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
						<xsl:apply-templates select="page/cart/bought" mode="avlb"/>
						<xsl:if test="page/cart/bought[f:num(product/price) = 0] or page/cart/bought[f:num(qty_avail) = 0]">
							<h2>Товары под заказ</h2>
							<xsl:apply-templates select="page/cart/bought" mode="preorder"/>
						</xsl:if>
						<div class="total">
							<xsl:if test="page/cart/sum != '0'">
								<p>Итого: <xsl:value-of select="f:currency_decimal(page/cart/sum)"/> р.</p>
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
			$(".qty-input").change(function(){
				$t = $(this);
				if($t.val() != $t.attr("data-old") &amp; validate($t.val())){
					$form = $(this).closest('form');
					$form.attr("action", '<xsl:value-of select="page/recalculate_link"/>');
					$form.submit();
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
		<xsl:if test="f:num(qty_avail) != 0 and f:num(product/price) != 0">
			<xsl:variable name="is_aux" select="aux != ''" />
			<xsl:variable name="p" select="product" />
			<xsl:variable name="price" select="if($is_aux) then f:cart_price_platan($p/price) else f:price_catalog($p/price, '')"/>
			<xsl:variable name="sum" select="if($is_aux) then f:cart_price_platan(sum) else f:price_catalog(sum, '')"/>

			<div class="item">
				<xsl:if test="$is_aux">
					<a class="image-container">
						<img src="img/no_image.png" alt="{$p/name}"/>
					</a>
					<a class="title">
						<xsl:value-of select="$p/name"/>
						<xsl:if test="$is_aux">
							<br/><span style="color: #000;">поставка в течение 7-10 дней</span>
						</xsl:if>
					</a>

				</xsl:if>
				<xsl:if test="not($is_aux)">
					<a href="{$p/show_product}" class="image-container">
						<img src="{$p/@path}{$p/main_pic}" alt="{$p/name}"/>
					</a>
					<a href="{$p/show_product}" class="title">
						<xsl:value-of select="$p/name"/>

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
					<input type="number" value="{qty}" name="{input/qty/@input}" min="1" class="qty-input" data-old="{qty}" />
					<br/>
					<span>Наличие</span>
					доступно: <xsl:value-of select="f:num($p/qty)" />
				</div>

				<div class="price all"><p><span>Сумма позиц.</span><xsl:value-of select="$sum"/></p></div>
				<a href="{delete}" class="delete"><i class="fas fa-times"/></a>

			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="bought" mode="preorder">
		<xsl:if test="f:num(product/price) = 0 or f:num(qty_avail) = 0">
			<xsl:variable name="p" select="product" />
			<xsl:variable name="is_aux" select="aux != ''" />

			<div class="item">
				<xsl:if test="$is_aux">
					<a class="image-container">
						<img src="img/no_image.png" alt="{$p/name}"/>
					</a>
					<a class="title">
						<xsl:value-of select="$p/name"/> (<xsl:value-of select="$p/product/name" />)
					</a>
				</xsl:if>
				<xsl:if test="not($is_aux)">
					<a href="{$p/show_product}" class="image-container">
						<img src="{if($p/main_pic != '') then concat($p/@path, $p/main_pic) else 'img/no_image.png'}" alt="{$p/name}"/>
					</a>
					<a href="{$p/show_product}" class="title">
						<xsl:value-of select="$p/name"/>
					</a>
				</xsl:if>
				<div class="price one">
					<p>
						<span>Цена</span>
						-
					</p>
				</div>
				<xsl:if test="f:num($p/qty) = 0">
					<div class="quantity">
						<span>Кол-во</span>
						<input type="number" value="{qty}" name="{input/qty/@input}" min="1" class="qty-input" data-old="{qty}" />
					</div>
				</xsl:if>
				<div class="price all"><p><span>Сумма позиц.</span> - </p></div>
				<a href="{delete}" class="delete"><i class="fas fa-times"/></a>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>