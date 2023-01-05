<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Ваш заказ'" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>

	<xsl:variable name="cart" select="page/cart"/>
	<xsl:variable name="regular_bought" select="$cart/bought[f:num(is_complex) = 0]"/>
	<xsl:variable name="complex_bought" select="$cart/bought[f:num(is_complex) = 1]"/>
	<xsl:variable name="has_both" select="$regular_bought != '' and $complex_bought != '' and not($cart/processed = 1)"/>
	<xsl:variable name="cart_is_empty" select="$cart/processed = 1 or not($cart/bought)"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>


	<xsl:template name="CONTENT">

		<xsl:if test="$has_both">
			<xsl:call-template name="TABS_WRAPPED_CART"/>
		</xsl:if>

		<xsl:if test="not($has_both)">
			<form method="post">
				<xsl:if test="$regular_bought">
					<xsl:call-template name="REGULAR_CART"/>
				</xsl:if>
				<xsl:if test="$complex_bought">
					<xsl:call-template name="COMPLEX_CART"/>
				</xsl:if>
			</form>
			<xsl:if test="$cart_is_empty">
				<h3>Корзина пуста</h3>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template name="REGULAR_CART">
		<div class="cart-list">
			<xsl:for-each select="$regular_bought">
				<xsl:variable name="p" select="product"/>
				<xsl:variable name="price" select="if (f:num($p/price) != 0) then f:exchange_cur($p, 'price', 0) else 'по запросу'"/>
				<xsl:variable name="sum" select="if (f:num($p/price) != 0) then f:exchange_cur(., 'sum', 0) else ''"/>

				<div class="cart-list__item cart-item">
					<div class="cart-item__image">
						<a href="{$p/show_product}">
							<xsl:if test="$p/main_pic"><img src="{$p/@path}{$p/main_pic}" alt="{$p/name}" /></xsl:if>
							<xsl:if test="not($p/main_pic)"><img src="img/no_image.png" alt="{$p/name}"/></xsl:if>
						</a>
					</div>
					<div class="cart-item__info">
						<a class="cart-item__name" href="{$p/show_product}"><xsl:value-of select="$p/name"/></a>
<!--						<div class="cart-item__artnumber">Артикул: <xsl:value-of select="$p/code"/></div>-->
					</div>

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
		</div>
		<div class="cart-total">
			<xsl:call-template name="REGULAR_SUM" />
			<xsl:call-template name="BUTTONS" />
		</div>
	</xsl:template>

	<xsl:template name="COMPLEX_CART">
		<div class="cart-list">
			<xsl:for-each select="$complex_bought">
				<xsl:variable name="p" select="product"/>
				<xsl:variable name="price" select="if (f:num($p/price) != 0) then f:exchange_cur($p, 'price', 0) else 'по запросу'"/>
				<xsl:variable name="sum" select="if (f:num($p/price) != 0) then f:exchange_cur(., 'sum', 0) else ''"/>

				<div class="cart-list__item cart-item">
					<div class="cart-item__image">
						<a href="{$p/show_product}">
							<xsl:if test="$p/main_pic"><img src="{$p/@path}{$p/main_pic}" alt="{$p/name}" /></xsl:if>
							<xsl:if test="not($p/main_pic)"><img src="img/no_image.png" alt="{$p/name}"/></xsl:if>
						</a>
					</div>
					<div class="cart-item__info">
						<a class="cart-item__name" href="{$p/show_product}"><xsl:value-of select="$p/name"/></a>
						<!--						<div class="cart-item__artnumber">Артикул: <xsl:value-of select="$p/code"/></div>-->
					</div>

					<div class="cart-item__price">
						<div>
							<span class="text-label">Цена</span>
							<span><xsl:value-of select="$price"/></span>
							<xsl:if test="not_available = '1'"><span>нет в наличии - под заказ</span></xsl:if>
						</div>
						<div style="margin-top: 10px;">
							<span class="text-label">Стоимость комплектации</span>
							<span><xsl:value-of select="$sum"/></span>
						</div>
					</div>
					<div class="cart-item__delete">
						<a href="{delete}">×</a>
					</div>
					<div class="extra-values" style="background: #fff;padding:0;">
						<div class="cart-list__item cart-item" style="padding:0; border:0 none; margin-bottom:0;border-bottom: 1px solid #ccc;">
							<div class="cart-item__info">
								<h3 style="margin-bottom:0; display: inline;">
									<xsl:value-of select="сomplectation_name"/>
								</h3>
							</div>
						</div>

						<xsl:for-each select="pseudo_option">
							<div class="cart-list__item cart-item" style="padding:0; border:0 none; border-bottom: 1px solid #ccc; margin-bottom:0;">
<!--								<div class="cart-item__image"></div>-->
								<div class="cart-item__info">
									<a class="cart-item__name">
										<xsl:value-of select="name"/>
									</a>
								</div>
								<div class="cart-item__price">
									<span class="text-label">Цена</span>
									<span>
										<xsl:value-of select="f:exchange_cur(., 'price', 0)"/>
									</span>
								</div>
							</div>
						</xsl:for-each>
					</div>
				</div>
			</xsl:for-each>
		</div>
		<div class="cart-total">
			<xsl:call-template name="COMPLEX_SUM" />
			<xsl:call-template name="BUTTONS" />
		</div>
	</xsl:template>

	<xsl:template name="REGULAR_SUM">
		<xsl:if test="$cart/sum != '0'">
			<div class="cart-total__text" id="cart-total">
				Без скидок: <xsl:value-of select="f:exchange_cur(page/cart, 'sum', 0)"/><br/>
				Итого: <xsl:value-of select="f:exchange_cur(page/cart, 'sum_discount', 0)"/><br/>
				Сэкономлено: <xsl:value-of select="f:exchange_cur(page/cart, 'sum_saved', 0)"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="COMPLEX_SUM">
		<xsl:if test="$cart/p_sum != '0'">
			<div class="cart-total__text" id="cart-complex-total">
				Без скидок: <xsl:value-of select="f:exchange_cur($cart, 'p_sum', 0)"/><br/>
				Итого: <xsl:value-of select="f:exchange_cur($cart, 'p_sum_discount', 0)"/><br/>
				Сэкономлено: <xsl:value-of select="f:exchange_cur($cart, 'p_sum_saved', 0)"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="BUTTONS">
		<div class="cart-total__buttons">
			<button class="button button_2 cart-total__button" type="submit"
					id="recalc" onclick="$(this).closest('form').attr('action', '{page/recalculate_link}'); postForm($(this).closest('form')); return false;">Пересчитать</button>
			<button class="button button_2 cart-total__button" type="submit"
					onclick="$(this).closest('form').attr('action', '{page/proceed_link}')">Продолжить</button>
		</div>
	</xsl:template>

	<xsl:template name="TABS_WRAPPED_CART">
		<div class="tabs">
			<div class="tabs__nav">
				<a href="#regular" class="tab tab_active">Заказ (обычные товары)</a>
				<a href="#complex" class="tab">Предаказ (товары с опциями)</a>
			</div>
			<form method="post">
				<div class="tabs__content">
					<div class="tab-container" id="regular">
						<xsl:call-template name="REGULAR_CART"/>
					</div>
					<div class="tab-container" id="complex" style="display:none;">
						<xsl:call-template name="COMPLEX_CART"/>
					</div>
				</div>
			</form>
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