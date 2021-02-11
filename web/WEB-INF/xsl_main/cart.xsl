<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Ваш заказ'" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>

	<xsl:template name="CONTENT">
		<div class="cart-list">
			<xsl:choose>
				<xsl:when test="page/cart/bought and not(page/cart/processed = '1')">
					<form method="post">
						<xsl:for-each select="page/cart/bought">
							<xsl:variable name="p" select="product"/>
							<xsl:variable name="price" select="if (f:num($p/price) != 0) then concat(f:currency_decimal($p/price), ' pуб.') else 'по запросу'"/>
							<xsl:variable name="sum" select="if (f:num($p/price) != 0) then concat(f:currency_decimal(sum), ' pуб.') else ''"/>
							<div class="cart-list__item cart-item">
								<xsl:if test="not($p/product)">
									<div class="cart-item__image">
										<a href="{$p/show_product}">
											<img src="{$p/@path}{$p/main_pic}" alt="{$p/name}" />
										</a>
									</div>
									<div class="cart-item__info">
										<a class="cart-item__name" href="{$p/show_product}"><xsl:value-of select="$p/name"/></a>
										<div class="cart-item__artnumber">Артикул: <xsl:value-of select="$p/code"/></div>
									</div>
								</xsl:if>
								<xsl:if test="$p/product">
									<div class="cart-item__image"><img src="{$p/product/@path}{$p/product/main_pic}" alt="{$p/name}" /></div>
									<div class="cart-item__info">
										<a class="cart-item__name" href="{$p/product/show_product}">
											<xsl:value-of select="$p/name"/> (<xsl:value-of select="$p/product/name" />)
										</a>
										<div class="cart-item__artnumber">Артикул: <xsl:value-of select="$p/product/code" /></div>
									</div>
								</xsl:if>
								<div class="cart-item__price">
									<span class="text-label">Цена</span>
									<span><xsl:value-of select="$price"/></span>
									<xsl:if test="not_available = '1'"><span>нет в наличии - под заказ</span></xsl:if>
								</div>
								<div class="cart-item__quantity">
									<span class="text-label">Кол-во</span>
									<input type="number" value="{qty}" name="{input/qty/@input}" class="input qty-input" data-old="{qty}"
										   min="{if (min_qty) then min_qty else 1}" step="{if (min_qty) then min_qty else 0.1}" />
								</div>
								<xsl:if test="not($sum = '')">
									<div class="cart-item__sum">
										<span class="text-label">Сумма</span>
										<span><xsl:value-of select="$sum"/></span>
									</div>
								</xsl:if>
								<div class="cart-item__delete">
									<a href="{delete}">×</a>
								</div>

								<!-- новый блок -->
								<xsl:if test="$p/extra_input">
									<xsl:variable name="b" select="."/>
									<div class="extra-values">
										<xsl:for-each select="$p/extra_input">
											<xsl:variable name="pos" select="position()"/>
											<div class="extra-values__row">
												<div class="extra-values__value">
													<input type="text"
														   name="{$b/input/*[name() = concat('extra', $pos)]/@input}"
														   value="{$b/input/*[name() = concat('extra', $pos)]}"	/>
												</div>
												<div class="extra-values__parameter"><p><xsl:value-of select="." /></p></div>
											</div>
										</xsl:for-each>
									</div>
								</xsl:if>
							</div>
						</xsl:for-each>
						<div class="cart-total">
							<xsl:if test="page/cart/sum != '0'">
								<div class="cart-total__text">Итого: <xsl:value-of select="f:currency_decimal(page/cart/sum)"/> руб.</div>
							</xsl:if>
							<div class="cart-total__buttons">
								<button class="button button_2 cart-total__button" type="submit"
										id="recalc" onclick="$(this).closest('form').attr('action', '{page/recalculate_link}')">Пересчитать</button>
								<button class="button button_2 cart-total__button" type="submit"
										onclick="$(this).closest('form').attr('action', '{page/proceed_link}')">Продолжить</button>
							</div>
						</div>
					</form>
				</xsl:when>
				<xsl:otherwise>
					<h3>Корзина пуста</h3>
				</xsl:otherwise>
			</xsl:choose>

		</div>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript">
			$(".qty-input").change(function(){
				$t = $(this);
				if($t.val() != $t.attr("data-old") &amp; validate($t.val())){
					$form = $(this).closest('form');
					$form.attr("action", '<xsl:value-of select="page/recalculate_link"/>');
					$form.submit();
				} else if(!validate($t.val())){
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

</xsl:stylesheet>