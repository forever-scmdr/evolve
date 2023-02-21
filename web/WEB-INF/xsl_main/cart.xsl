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
							<xsl:variable name="price" select="if (f:num($p/price) != 0) then f:exchange_cur(., 'price', 0) else 'по запросу'"/>
							<xsl:variable name="sum" select="if (f:num($p/price) != 0) then f:exchange_cur(., 'sum', 0) else ''"/>
							<div class="cart-list__item cart-item">
								<xsl:if test="not($p/product)">
									<div class="cart-item__image">
										<a href="{$p/show_product}">
											<xsl:if test="$p/main_pic"><img src="{$p/@path}{$p/main_pic}" alt="{$p/name}" /></xsl:if>
											<xsl:if test="not($p/main_pic)"><img src="img/no_image.png" alt="{$p/name}"/></xsl:if>
										</a>
									</div>
									<div class="cart-item__info">
										<a class="cart-item__name" href="{$p/show_product}"><xsl:value-of select="$p/name"/></a>
										<div class="cart-item__artnumber">Артикул: <xsl:value-of select="$p/code"/></div>
										<p/>
										<div class="cart-item__artnumber">Норма упк.: <xsl:value-of select="if ($p/packquantity and not($p/packquantity = '')) then $p/packquantity else '1'" /></div>
										<div class="cart-item__artnumber">Кратность: <xsl:value-of select="if ($p/step and not($p/step = '')) then $p/step else '1'" /></div>
										<div class="cart-item__artnumber">Мин. партия: <xsl:value-of select="if ($p/min_qty and not($p/min_qty = '')) then $p/min_qty else '1'" /></div>
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

									<input type="number" value="{f:num(qty)}" name="{input/qty/@input}" class="input qty-input" data-old="{f:num(qty)}"
										   min="{if ($p/min_qty) then f:num($p/min_qty) else 1}" step="{if ($p/step) then f:num($p/step) else 1}" />
								</div>
								<xsl:if test="not($sum = '')">
									<div class="cart-item__sum">
										<span class="text-label">Сумма</span>
										<span id="sum-{code}"><xsl:value-of select="$sum"/></span>
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
								<div class="cart-total__text" id="cart-total">Итого: <xsl:value-of select="f:exchange_cur(page/cart, 'sum', 0)"/></div>
							</xsl:if>
							<div class="cart-total__buttons">
								<button class="button button_2 cart-total__button" type="submit"
										id="recalc" onclick="$(this).closest('form').attr('action', '{page/recalculate_link}'); postForm($(this).closest('form')); return false;">Пересчитать</button>
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

				if(typeof recalcTo != "undefined"){
					clearTimeout(recalcTo);
				}

				$t = $(this);
				if($t.val() != $t.attr("data-old") &amp; validate($t.val())){

					var $form = $(this).closest('form');
					var func = function(){
					$form.attr("action", '<xsl:value-of select="page/recalculate_link"/>');
						//$form.submit();
						postForm($form,'v', function(){ $t.attr("data-old", $t.val()); });
					};

					recalcTo = setTimeout(func, 500);

				} else if(!validate($t.val())){
					if(validate($t.attr("data-old"))){
						$t.val($t.attr("data-old"));
					}else{
						$t.val("1");
					}
				}
			});

			function validate(val){
				return parseFloat(val) &gt; 0;
			}
		</script>
	</xsl:template>

</xsl:stylesheet>