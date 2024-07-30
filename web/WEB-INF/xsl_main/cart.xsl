<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Ваш заказ'" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
    <xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="step_default" select="if (page/catalog/default_step) then f:num(page/catalog/default_step) else 1"/>
	<xsl:variable name="prcat" select="page/price_catalogs/price_catalog"/>

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
							<xsl:variable name="price" select="if (f:num($p/price) != 0) then f:exchange_cur(., 'price', 0) else 'по запросу'"/>
							<xsl:variable name="sum" select="if (f:num($p/price) != 0) then f:exchange_cur(., 'sum', 0) else ''"/>
							<xsl:variable name="plain_section" select="$p/plain_section"/>
                            <xsl:variable name="plain" select="if ($p/section_name and not($p/section_name = '')) then $p/section_name else $p/plain_section/name"/>
							<xsl:variable name="pc" select="$prcat[name = $plain]"/>
							<div class="cart-list__item cart-item" style="{if ($plain) then 'grid-template-columns: 104px 1fr 200px 100px 100px 20px;' else ''}">
								<xsl:if test="not($p/product)">
									<div class="cart-item__image">
										<a href="{$p/show_product}">
											<xsl:if test="$p/main_pic"><img src="{$p/@path}{$p/main_pic}" alt="{$p/name}" /></xsl:if>
											<xsl:if test="not($p/main_pic)"><img src="img/no_image.png" alt="{$p/name}"/></xsl:if>
										</a>
									</div>
									<div class="cart-item__info">
										<a class="cart-item__name" href="{$p/show_product}"><xsl:value-of select="$p/name"/></a><br/>
										<div class="cart-item__artnumber">Артикул: <xsl:value-of select="$p/code"/></div>
										<xsl:if test="$pc/other_name"><br/><div class="cart-item__artnumber">Поставщик: <b><xsl:value-of select="$pc/other_name"/></b></div></xsl:if>
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
										<!-- Для обычных товаров (не из каталога price_catalog) -->
										<xsl:if test="not($plain)">
											<xsl:value-of select="$price"/>
										</xsl:if>
										<!-- Для товаров из каталога price_catalog -->
										<xsl:if test="$plain">
											<xsl:call-template name="ALL_PRICES">
												<xsl:with-param name="need_sum" select="false()"/>
												<xsl:with-param name="price_in_currency" select="f:exchange($p, 'price', 0)"/>
												<xsl:with-param name="product" select="$p"/>
												<xsl:with-param name="section_name" select="$plain"/>
											</xsl:call-template>
										</xsl:if>
									</span>
									<!--<xsl:if test="not_available = '1'"><span>нет в наличии - под заказ</span></xsl:if>-->
								</div>
								<div class="cart-item__quantity">
									<span class="text-label">Кол-во</span>

									<input type="number" value="{f:num(qty)}" name="{input/qty/@input}" class="input qty-input" data-old="{f:num(qty)}" style="width: 70px"
										   min="{if ($p/min_qty) then f:num($p/min_qty) else 1}" step="{if ($p/step) then f:num($p/step) else $step_default}" />
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
		</script>
	</xsl:template>

</xsl:stylesheet>