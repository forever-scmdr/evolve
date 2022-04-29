<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="title" select="'Ваш заказ'" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="active_menu_item" select="'catalog'"/>
	<xsl:variable name="view" select="$pv/view"/>
	<xsl:variable name="cart" select="page/cart"/>
	<xsl:variable name="is_not_processed" select="not($cart/processed = '1')"/>
	<xsl:variable name="has_tab_cart" select="$cart/bought[qty != '0'] and $is_not_processed"/>
	<xsl:variable name="has_tab_order" select="$cart/bought[qty_zero != '0'] and $is_not_processed"/>
	<xsl:variable name="has_tab_fav" select="page/product"/>
	<xsl:variable name="tab_active" select="if ($pv/tab) then $pv/tab else if ($has_tab_cart) then 'tab_cart' else if ($has_tab_order) then 'tab_order' else 'tab_personal'"/>
	<xsl:variable name="catalog_settings" select="page/price_catalogs/price_catalog"/>


	<xsl:template match="bought">
		<xsl:param name="need_ship_time" select="true()"/>

		<xsl:variable name="p" select="product"/>
		<xsl:variable name="zero" select="not($p/is_service = '1') and f:num($p/qty) &lt; 0.001"/>
		<xsl:variable name="has_zero_qty" select="f:num(qty_zero) != 0"/>
		<xsl:variable name="price" select="if (f:num($p/price) != 0) then concat(f:currency_decimal($p/price), ' pуб.') else 'по запросу'"/>
		<xsl:variable name="sum" select="if (f:num($p/price) != 0) then concat(f:currency_decimal(sum), ' pуб.') else ''"/>

		<xsl:variable name="is_extra" select="$p/available = '-1'"/>
		<xsl:variable name="settings" select="$catalog_settings[name = $p/group_id]"/>
		<xsl:variable name="ship_time_calculated_text" select="$settings/ship_time[@key = current()/store]/@value"/>
		<xsl:variable name="ship_time_calculated_days" select="$settings/ship_time_days[@key = current()/store]/@value"/>
		<xsl:variable name="ship_time_text" select="if ($ship_time_calculated_text) then $ship_time_calculated_text else $settings/default_ship_time"/>
		<xsl:variable name="ship_time_days" select="if ($ship_time_calculated_days) then $ship_time_calculated_days else $settings/default_ship_time_days"/>

		<tr class="cart-item">
			<td class="cart-item__code">
				<div>Код:</div>
				<div><xsl:value-of select="if ($is_extra) then $p/extra_code else $p/code"/></div>
			</td>
			<td class="cart-item__name">
				<a href="{$p/show_product}"><xsl:value-of select="string-join(($p/name, $p/name_extra), ' ')"/></a>
				<div class="cart-item__qty_mobile">
					<div><xsl:value-of select="if ($is_extra) then $p/extra_code else $p/code"/></div>
				</div>
				<div><xsl:value-of select="vendor"/></div>
				<div class="cart-item__qty_mobile">
					<xsl:if test="$zero">
						<xsl:if test="not($p/soon != '0')">Нет в наличии</xsl:if>
						<xsl:if test="$p/soon != '0'">Ожидается: <xsl:value-of select="substring($p/soon, 1, 10)"/></xsl:if>
<!--						<a href="{$p/subscribe_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-subscribe">Уведомить о поступлении</a>-->
					</xsl:if>
					<xsl:if test="not($zero) and not($p/is_service = '1')">
						<xsl:if test="$has_zero_qty">
							Доступно: <span style="color: red;"><xsl:value-of select="concat(qty_avail, ' ', $p/unit)" /></span>.
							Под заказ: <span style="color: red;"><xsl:value-of select="qty_zero" /></span>
						</xsl:if>
						<xsl:if test="not($has_zero_qty)">
							В наличии: <xsl:value-of select="concat($p/qty, ' ', $p/unit)" />
						</xsl:if>
					</xsl:if>
				</div>
			</td>
			<td class="cart-item__image">
				<a href="{$p/show_product}">
					<xsl:if test="$is_extra"><img src="{$p/extra_pic[1]}" alt="{$p/name}" onerror="this.src = 'images/no-photo.jpg'; this.removeAttribute('onerror')"/></xsl:if>
					<xsl:if test="not($is_extra)"><img src="sitepics/{$p/pic_path}.jpg" alt="{$p/name}" onerror="this.src = 'images/no-photo.jpg'; this.removeAttribute('onerror')"/></xsl:if>
				</a>
			</td>
			<xsl:if test="$need_ship_time">
				<td class="cart-item__qty">
					<xsl:if test="not($is_extra)">
						<div><xsl:value-of select="f:day_month(current-date())"/></div>
						<div>Склад магазина</div>
					</xsl:if>
					<xsl:if test="$is_extra and $ship_time_days">
						<div><xsl:value-of select="f:day_month(current-date() + xs:dayTimeDuration(concat('P', $ship_time_days,'D')))"/></div>
						<div>Удаленный склад</div>
						<div>100% предоплата</div>
					</xsl:if>
				</td>
			</xsl:if>
			<td class="cart-item__qty">
				<input type="number" class="input qty-input" name="{input/qty/@input}" data-old="{qty}"
					   value="{qty_total}" min="{if ($p/min_qty) then $p/min_qty else 1}" step="{if ($p/step) then f:num($p/step) else 1}"/>
				<div class="hide_m">
					<xsl:if test="$zero">
						<xsl:if test="not($p/soon != '0')">Нет в наличии</xsl:if>
						<xsl:if test="$p/soon != '0'">Ожидается: <xsl:value-of select="substring($p/soon, 1, 10)"/></xsl:if>
<!--						<a href="{$p/subscribe_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-subscribe">Уведомить о поступлении</a>-->
					</xsl:if>
					<xsl:if test="not($zero) and not($p/is_service = '1')">
						<xsl:if test="$has_zero_qty">
							<div>Доступно: <span style="color: red; font-weight: bold;"><xsl:value-of select="concat(qty_avail, ' ', $p/unit)" /></span>.</div>
							<div>Под заказ: <span style="color: red;font-weight: bold;"><xsl:value-of select="qty_zero" /></span></div>
						</xsl:if>
						<xsl:if test="not($has_zero_qty)">
							В наличии:<strong><xsl:value-of select="concat($p/qty, ' ', $p/unit)" /></strong>
						</xsl:if>
					</xsl:if>
				</div>
			</td>
			<td class="cart-item__price">
				<xsl:if test="not($zero)">
					<xsl:value-of select="f:exchange_cur($p, $price_param_name, 0)"/>/<xsl:value-of select="$p/unit"/>
				</xsl:if>
				<xsl:if test="$zero">нет цены</xsl:if>
				<xsl:if test="$p/special_price = 'true' and not($zero)"><div style="color: red; font-weight: normal;">Спеццена</div></xsl:if>
				<xsl:if test="f:num($p/sec/discount_1) &gt; 0">
					<div class="sale">от <xsl:value-of select="$p/sec/limit_1"/>&#160;<xsl:value-of select="$p/unit"/> -
						<xsl:value-of select="$p/sec/discount_1"/>%<xsl:call-template name="BR"/>
						от <xsl:value-of select="$p/sec/limit_2"/>&#160;<xsl:value-of select="$p/unit"/> -
						<xsl:value-of select="$p/sec/discount_2"/>%
					</div>
				</xsl:if>
			</td>
			<td class="cart-item__sum">
				<xsl:if test="not($zero)">
					<xsl:variable name="sum_cur" select="tokenize(f:exchange_cur(., 'sum', 0), '\s')"/>
					<xsl:value-of select="$sum_cur[1]"/>
					<span class="hide_m" style="display: inline">&#160;<xsl:value-of select="$sum_cur[2]"/></span>
				</xsl:if>
				<xsl:if test="$zero">нет цены</xsl:if>
			</td>
			<td class="cart-item__close">
				<a href="{if ($zero) then delete_zero else delete_non_zero}">
					<img src="img/icon-close.png" alt=""/>
				</a>
			</td>
		</tr>
	</xsl:template>






	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a class="path__link" href="{$main_host}">Главная страница</a>
				<div class="path__arrow"></div>
				<a class="path__link" href="{page/catalog_link}">Каталог</a>
			</div>
		</div>
	</xsl:template>




	<xsl:template name="CART_TOTAL">
		<table class="cart-items">
			<tbody>
				<tr class="cart-item">
					<td style="vertical-align: top">
						<div>
							* Срок поставки в магазин для самовывоза
						</div>
					</td>
					<td>
						<div class="cart-total">
							<xsl:if test="f:num($cart/discount) &gt; 0 and $is_not_processed">
								<div class="cart-total__text">
									<div>Итого: <span><xsl:value-of select="f:exchange_cur($cart, 'simple_sum', 0)" /></span></div>
									<div>Скидка: <span><xsl:value-of select="$cart/discount"/>% - на товар не участвующий в спецпредложениях</span></div>
									<div>Сумма скидки: <span><xsl:value-of select="f:exchange_cur($cart, 'margin', 0)"/></span></div>
								</div>
							</xsl:if>
							<xsl:if test="f:num($cart/sum) &gt; 0 and $is_not_processed">
								<div class="cart-total__sum">К оплате: <xsl:value-of select="f:exchange_cur($cart, 'sum', 0)" /></div>
							</xsl:if>
							<div class="cart-total__warning">Реальная стоимость заказа может незначительно отличаться из-за округления цены в системе.</div>
							<div id="discount_rules">
								<a class="cart-total__link discount-link" href="#discount_rules">
									Правила предоставления скидок
								</a>
								<div class="discount_rules">
									<xsl:value-of select="page/discount_rules/text" disable-output-escaping="yes"/>
								</div>
							</div>

							<div class="cart-total__buttons">
								<a href="{/page/delete_all_link}" onclick="return confirm('Вы действительно хотите удалить все товары из корзины?');"
								   class="button button_secondary" style="text-decoration: none;">Очистить корзину</a>
								<span style="padding-left: 1rem;"></span>
								<button class="button" type="submit">К оформлению</button>
							</div>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
	</xsl:template>



	<xsl:template name="CONTENT_INNER">
		<div class="tabs">
			<div class="tabs__nav">
				<xsl:if test="$has_tab_cart">
					<a class="tab{' tab_active'[$tab_active = 'tab_cart']}" href="#tab_cart">
						<div class="tab__text">Корзина товаров</div>
						<div class="tab__qty"><xsl:value-of select="count($cart/bought[qty != '0'])"/></div>
					</a>
				</xsl:if>
				<xsl:if test="$has_tab_order">
					<a class="tab{' tab_active'[$tab_active = 'tab_order']}" href="#tab_order">
						<div class="tab__text">Позиции для запроса</div>
						<div class="tab__qty"><xsl:value-of select="count($cart/bought[qty_zero != '0'])"/></div>
					</a>
				</xsl:if>
				<a class="tab{' tab_active'[$tab_active = 'tab_personal']}" href="#tab_personal">
					<div class="tab__text">Персональный заказ</div>
					<div class="tab__qty"><xsl:value-of select="count($cart/custom_bought[nonempty = 'true'])"/></div>
				</a>
				<xsl:if test="$has_tab_fav">
					<a class="tab{' tab_active'[$tab_active = 'tab_favourite']}" href="#tab_favourite">
						<div class="tab__text">Избранное</div>
						<div class="tab__qty"><xsl:value-of select="count(page/product)"/></div>
					</a>
				</xsl:if>
			</div>
			<div class="tabs__content result" id="cart-info">
				<form action="{page/proceed_link}" method="post">
					<xsl:if test="$has_tab_cart">
						<div class="tab-container" id="tab_cart" style="{'display: none'[$tab_active != 'tab_cart']}">
							<table class="cart-items">
								<tbody>
									<tr class="cart-items__head">
										<td colspan="3">Описание</td>
										<td>Срок поставки *</td>
										<td>Количество</td>
										<td>Цена, <xsl:value-of select="replace($currency, 'RUB', 'RUR')"/></td>
										<td>Стоимость</td>
									</tr>
									<xsl:for-each-group select="$cart/bought[qty != '0']" group-by="type">
										<tr>
											<td colspan="7" style="background: #dddddd; font-size: 15px; padding: 5px 10px; font-weight: bold;">
												<xsl:value-of select="current-grouping-key()"/>
											</td>
											<tr><td colspan="7" style="padding-bottom: 15px;"></td></tr>
											<xsl:apply-templates select="current-group()"/>
										</tr>
									</xsl:for-each-group>
								</tbody>
							</table>
							<xsl:call-template name="CART_TOTAL"/>
						</div>
					</xsl:if>
					<xsl:if test="$has_tab_order">
						<div class="tab-container" id="tab_order" style="{'display: none'[$tab_active != 'tab_order']}">
							<table class="cart-items">
								<tbody>
									<tr class="cart-items__head">
										<td colspan="3">Описание</td>
										<td>Количество</td>
										<td>Цена, <xsl:value-of select="$currency"/></td>
										<td>Стоимость</td>
									</tr>
									<xsl:for-each-group select="$cart/bought[qty_zero != '0']" group-by="type">
										<tr>
											<td colspan="7" style="background: #dddddd; font-size: 15px; padding: 5px 10px; font-weight: bold;">
												<xsl:value-of select="current-grouping-key()"/>
											</td>
											<tr><td colspan="7" style="padding-bottom: 15px;"></td></tr>
											<xsl:apply-templates select="current-group()">
												<xsl:with-param name="need_ship_time" select="false()"/>
											</xsl:apply-templates>
										</tr>
									</xsl:for-each-group>
								</tbody>
							</table>
							<xsl:call-template name="CART_TOTAL"/>
						</div>
					</xsl:if>
					<div class="tab-container" id="tab_personal" style="{'display: none'[$tab_active != 'tab_personal']}">
						<div class="personal-order">

							<xsl:for-each select="$cart/custom_bought">
								<xsl:sort select="position"/>
								<xsl:if test="position &lt; 5">
									<xsl:variable name="i" select="input"/>
									<div class="personal-order__item personal">
										<div class="personal__mark">
											<input class="input" type="text" value="{mark}" name="{$i/mark/@input}"/>
										</div>
										<div class="personal__type">
											<input class="input" type="text" value="{type}" name="{$i/type/@input}"/>
										</div>
										<div class="personal__case">
											<input class="input" type="text" value="{case}" name="{$i/case/@input}"/>
										</div>
										<div class="personal__qty">
											<input class="input" type="text" value="{qty}" name="{$i/qty/@input}"/>
										</div>
										<div class="personal__link">
											<input class="input" type="text" value="{link}" name="{$i/link/@input}"/>
										</div>
										<div class="personal__more">
											<input class="input" type="text" value="{extra}" name="{$i/extra/@input}"/>
										</div>
									</div>
								</xsl:if>
							</xsl:for-each>
						</div>
						<xsl:call-template name="CART_TOTAL"/>
					</div>
				</form>
				<xsl:if test="$has_tab_fav">
					<div class="tab-container" id="tab_favourite" style="{'display: none'[$tab_active != 'tab_favourite']}">
						<xsl:if test="$view = 'table'">
							<div class="devices">
								<div class="devices__wrap">
									<xsl:apply-templates select="page/product"/>
								</div>
							</div>
						</xsl:if>
						<xsl:if test="$view = 'list'">
							<table class="cart-items hide_m">
								<tbody>
									<tr class="cart-items__head">
										<td colspan="2">Описание</td>
										<td>Количество</td>
										<td>Цена, <xsl:value-of select="$currency"/></td>
										<td> </td>
									</tr>
									<xsl:apply-templates select="page/product" mode="lines"/>
								</tbody>
							</table>
						</xsl:if>
					</div>
				</xsl:if>
			</div>
		</div>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<script type="text/javascript">
			$(document).on("change", ".qty-input", function(){

				if(typeof recalcTo != "undefined"){
					clearTimeout(recalcTo);
				}

				$t = $(this);
				if($t.val() != $t.attr("data-old") &amp; validate($t.val())){

					var $form = $(this).closest('form');
					var func = function(){
					$form.attr("action", '<xsl:value-of select="page/recalculate_link"/>');
						//$form.submit();
						postForm($form,'v', function(){ /*$t.attr("data-old", $t.val());*/ insertAjax('cart_ajax')});
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
		<xsl:call-template name="CART_SCRIPT"/>
		<xsl:call-template name="TAB_SCRIPT"/>
	</xsl:template>


</xsl:stylesheet>
