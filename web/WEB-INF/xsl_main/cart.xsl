<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Ваш заказ'" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
    <xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="step_default" select="if (page/catalog/default_step) then f:num(page/catalog/default_step) else 1"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="COMMON_LEFT_COLOUMN"/>
	</xsl:template>


	<xsl:template name="PAGE_HEADING">
		<div class="title title_1 vl_{$classvl}" style="display: table;table-layout: fixed;width: 100%;">
			<div style="display: table-cell;"><xsl:value-of select="$h1"/></div>
			<div style="display: table-cell;" id="cur_div">
				<ul class="currency-options" style="float: right;">
					<xsl:variable name="currency_link" select="page/set_currency"/>
					<xsl:for-each select="$currencies/*[ends-with(name(), '_rate')]">
						<xsl:variable name="cur" select="substring-before(name(), '_rate')"/>
						<xsl:variable name="active" select="$currency = $cur"/>
						<li class="{'active'[$active]}">
							<xsl:if test="not($active)"><a href="{concat($currency_link, $cur)}"><xsl:value-of select="$cur"/></a></xsl:if>
							<xsl:if test="$active"><xsl:value-of select="$cur"/></xsl:if>
						</li>
					</xsl:for-each>
					<li><i class="far fa-money-bill-alt"/>&#160;<strong>Валюта</strong></li>
				</ul>
			</div>
		</div>
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
		<div class="search_cart">
			<form action="/cart/" method="post">
				<input class="input header-search__input"  type="text" placeholder="Введите название товара для поиска в корзине" autocomplete="off"/>
				<button class="button header-search__button" type="submit">Поиск по корзине</button>
			</form>
		</div>


		<div class="cart-list">
			<xsl:choose>
				<xsl:when test="page/cart/bought and not(page/cart/processed = '1')">
					<form method="post">
						<xsl:for-each select="page/cart/bought">
							<xsl:variable name="p" select="product"/>
							<xsl:variable name="outer" select="parse-xml(concat('&lt;prod&gt;', $p/extra_xml, '&lt;/prod&gt;'))"/>
							<xsl:variable name="po" select="$outer/prod/product"/>
							<xsl:variable name="multipe_prices" select="$po/prices"/>
							<xsl:variable name="price" select="if (f:num($p/price) != 0) then f:exchange_cur(., 'price', 0) else 'по запросу'"/>
							<xsl:variable name="sum" select="if (f:num(sum) != 0) then f:exchange_cur(., 'sum', 0) else ''"/>
							<xsl:variable name="not_api" select="$p/@id &gt; 0"/>
							<xsl:variable name="ajax_suffix" select="if ($not_api) then code else product/@key"/>
							<xsl:variable name="total_qty" select="if ($not_api and not($p/qty)) then '1000' else $p/qty"/>
							<xsl:variable name="dlv" select="if ($not_api and not($p/next_delivery)) then '7' else $p/next_delivery"/>
							<div class="cart-list__item cart-item">
								<xsl:if test="not($p/product)">
									<!--
									<div class="cart-item__image">
										<xsl:if test="$not_api">
											<a href="{$p/show_product}">
												<xsl:if test="$p/main_pic"><img src="{$p/@path}{$p/main_pic}" alt="{$p/name}" /></xsl:if>
												<xsl:if test="not($p/main_pic)"><img src="img/no_image.png" alt="{$p/name}"/></xsl:if>
											</a>
										</xsl:if>
									</div>
									-->
									<div class="cart-item__info">
										<xsl:if test="$not_api">
											<a class="cart-item__name" href="{$p/show_product}"><xsl:value-of select="$p/name"/></a>
										</xsl:if>
										<xsl:if test="not($not_api)">
											<span class="cart-item__name"><xsl:value-of select="$p/name"/></span>
										</xsl:if>
										<p/>
										<xsl:if test="not($po)">
											<div class="cart-item__artnumber">Норма упк.: <xsl:value-of select="if ($p/packquantity and not($p/packquantity = '')) then $p/packquantity else '1'" /></div>
											<div class="cart-item__artnumber">Кратность: <xsl:value-of select="if ($p/step and not($p/step = '')) then $p/step else '1'" /></div>
											<div class="cart-item__artnumber">Мин. партия: <xsl:value-of select="if ($p/min_qty and not($p/min_qty = '')) then $p/min_qty else '1'" /></div>
											<div class="cart-item__artnumber">Количество: <xsl:value-of select="$total_qty" /></div>
											<div class="cart-item__artnumber">Срок поставки: <xsl:value-of select="if (normalize-space($dlv) = '0') then 'на складе' else $dlv" /></div>
										</xsl:if>
										<xsl:if test="$po">
											<div class="cart-item__artnumber">Поставщик: <xsl:value-of select="$po/category_id"/></div><br/>
											<div class="cart-item__artnumber">Производитель: <xsl:value-of select="$po/vendor"/></div><br/>
											<div class="cart-item__artnumber">Описание: <xsl:value-of select="$po/description"/></div><br/>
											<div><a onclick="showDetails('product_ajax?prod={normalize-space($po/name)}')" >Полное описание</a></div>
										</xsl:if>
									</div>
								</xsl:if>
								<xsl:if test="$p/product">
									<div class="cart-item__image"><img src="{$p/product/@path}{$p/product/main_pic}" alt="{$p/name}" /></div>
									<div class="cart-item__info">
										<span class="cart-item__name">
											<xsl:value-of select="$p/name"/> (<xsl:value-of select="$p/product/name" />)
										</span>
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
												<xsl:with-param name="product" select="$po"/>
											</xsl:call-template>
										</xsl:if>
									</span>
									<!--<xsl:if test="not_available = '1'"><span>нет в наличии - под заказ</span></xsl:if>-->
								</div>
								<div class="cart-item__ostatok">
									<span class="text-label">Доступно</span>
									<span><xsl:value-of select="$po/qty" /></span>
								</div>
								<div class="cart-item__postavka">
									<xsl:variable name="dlv_" select="replace(lower-case($po/next_delivery), 'weeks', 'недели')"/>
									<xsl:variable name="dlv" select="replace(lower-case($dlv_), 'days', 'дни')"/>
									<span class="text-label">Срок поставки</span>
									<span><xsl:value-of select="if (not($dlv = '')) then $dlv else 'согласуется после оформления заказа'"/></span>
								</div>
								<div class="cart-item__quantity">
									<span class="text-label">Кол-во</span>

									<input type="number" value="{f:num(qty)}" name="{input/qty/@input}" class="input qty-input" data-old="{f:num(qty)}"
										   min="{if ($p/min_qty) then f:num($p/min_qty) else 1}" step="{if ($p/step) then f:num($p/step) else 1}" style="width: 70px"/>
								</div>
								<xsl:if test="not($sum = '')">
									<div class="cart-item__sum">
										<span class="text-label">Сумма</span>
										<span id="sum_{$ajax_suffix}"><xsl:value-of select="$sum"/></span>
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
								<button class="button button_2 cart-total__button" type="submit" style="display: none"
										id="recalc" onclick="$(this).closest('form').attr('action', '{page/recalculate_link}'); postForm($(this).closest('form')); return false;">Пересчитать</button>
								<button class="button button_2 cart-total__button" type="submit"
										onclick="$(this).closest('form').attr('action', '{page/proceed_link}')">Продолжить</button>
								<button class="button button_2 cart-total__button" type="submit">Сохранить список BOM</button>
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