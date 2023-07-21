<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="user_data_inputs.xsl"/>
	<xsl:import href="utils/multiple_prices.xsl"/>
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Ваш заказ'" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
    <xsl:variable name="message" select="page/variables/message"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>


	<xsl:template name="CONTENT">
        <xsl:if test="$message and not($success)">
            <div class="alert alert_danger">
                <div class="alert__title">Ошибка.</div>
                <div class="alert__text">
                    <p><xsl:value-of select="$message"/>.</p>
                </div>
            </div>
        </xsl:if>
        <xsl:if test="$message and $success">
            <div class="alert alert_success">
                <div class="alert__text">
                    <p><xsl:value-of select="$message"/></p>
                </div>
            </div>
        </xsl:if>
		<div class="cart-list">
			<xsl:choose>
				<xsl:when test="page/cart/bought and not(page/cart/processed = '1')">
					<form method="post">
						<xsl:for-each select="page/cart/bought">
							<xsl:variable name="p" select="product"/>
							<xsl:variable name="outer" select="parse-xml(concat('&lt;prod&gt;', $p/extra_xml, '&lt;cool&gt;eeee&lt;/cool&gt;&lt;/prod&gt;'))"/>
							<xsl:variable name="multipe_prices" select="$outer/prod/product/prices"/>
							<xsl:variable name="price" select="if (f:num($p/price) != 0) then f:exchange_cur(., 'price', 0) else 'по запросу'"/>
							<xsl:variable name="sum" select="if (f:num($p/price) != 0) then f:exchange_cur(., 'sum', 0) else ''"/>
							<xsl:variable name="not_api" select="$p/@id &gt; 0"/>
							<xsl:variable name="total_qty" select="if ($not_api and not($p/qty)) then '1000' else $p/qty"/>
							<xsl:variable name="dlv" select="if ($not_api and not($p/next_delivery)) then '7' else $p/next_delivery"/>
							<div class="cart-list__item cart-item">
								<xsl:if test="not($p/product)">
									<div class="cart-item__image">
										<xsl:if test="$not_api">
											<a href="{$p/show_product}">
												<xsl:if test="$p/main_pic"><img src="{$p/@path}{$p/main_pic}" alt="{$p/name}" /></xsl:if>
												<xsl:if test="not($p/main_pic)"><img src="img/no_image.png" alt="{$p/name}"/></xsl:if>
											</a>
										</xsl:if>
									</div>
									<div class="cart-item__info">
										<xsl:if test="$not_api">
											<a class="cart-item__name" href="{$p/show_product}"><xsl:value-of select="$p/name"/></a>
										</xsl:if>
										<xsl:if test="not($not_api)">
											<span class="cart-item__name"><xsl:value-of select="$p/name"/></span>
										</xsl:if>
										<p/>
										<div class="cart-item__artnumber">Норма упк.: <xsl:value-of select="if ($p/packquantity and not($p/packquantity = '')) then $p/packquantity else '1'" /></div>
										<div class="cart-item__artnumber">Кратность: <xsl:value-of select="if ($p/step and not($p/step = '')) then $p/step else '1'" /></div>
										<div class="cart-item__artnumber">Мин. партия: <xsl:value-of select="if ($p/min_qty and not($p/min_qty = '')) then $p/min_qty else '1'" /></div>
										<div class="cart-item__artnumber">Количество: <xsl:value-of select="$total_qty" /></div>
										<div class="cart-item__artnumber">Срок поставки: <xsl:value-of select="if (normalize-space($dlv) = '0') then 'на складе' else $dlv" /></div>
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
									<span>
										<xsl:if test="not($multipe_prices)" ><xsl:value-of select="$price"/></xsl:if>
										<xsl:if test="$multipe_prices">
											<xsl:call-template name="ALL_PRICES_API">
												<xsl:with-param name="need_sum" select="false()"/>
												<xsl:with-param name="product" select="$outer/prod/product"/>
											</xsl:call-template>
										</xsl:if>
									</span>
									<xsl:if test="not_available = '1'"><span>нет в наличии - под заказ</span></xsl:if>
								</div>
								<div class="cart-item__quantity">
									<span class="text-label">Кол-во</span>

									<input type="number" value="{f:num(qty)}" name="{input/qty/@input}" min="{if ($p/min_qty) then f:num($p/min_qty) else 1}" step="{if ($p/step) then f:num($p/step) else 1}" max="{f:num($total_qty)}" 
										class="input qty-input" data-old="{f:num(qty)}"/>
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
					</form>
					<form action="{page/confirm_link}" method="post" enctype="multipart/form-data" onsubmit="return isValid">
						<xsl:variable name="inp" select="page/user_jur/input"/>
						<div style="display:none">
							<xsl:call-template name="USER_JUR_INPUTS">
								<xsl:with-param name="inp" select="$inp"/>
								<xsl:with-param name="vals" select="page/jur"/>
							</xsl:call-template>
						</div>
						<div class="cart-total">
							<xsl:if test="page/cart/sum != '0'">
								<div class="cart-total__text" id="cart-total">Итого: <xsl:value-of select="f:exchange_cur(page/cart, 'sum', 0)"/></div>
							</xsl:if>
							<div class="cart-total__buttons">
								<button class="button button_2 cart-total__button" type="submit">Оформить заказ</button>
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
			var isValid = true;
			$(".qty-input").change(function(){

				if(typeof recalcTo != "undefined"){
					clearTimeout(recalcTo);
				}

				var t = $(this);
				isValid = t[0].checkValidity();

				if(t.val() != t.attr("data-old") &amp;&amp; isValid){
					t.popover('hide');

					var $form = $(this).closest('form');
					var func = function(){
					$form.attr("action", '<xsl:value-of select="page/recalculate_link"/>');
						//$form.submit();
						postForm($form,'v', function(){ t.attr("data-old", t.val()); });
					};

					recalcTo = setTimeout(func, 500);

				} else {
			        t.popover({
			            placement:'bottom',
			            trigger:'manual',
			            html:true,
			            content:'<div style="background-color: #FFDDDD; width: 60px;">max ' + t.attr('max') + '</div>'
			        });
			        t.popover('show');
				}
			});


			$(document).ready(function() {
				$(window).keydown(function(event){
					if(event.keyCode == 13) {
						event.preventDefault();
						return false;
					}
				});
			});
		</script>
	</xsl:template>

</xsl:stylesheet>