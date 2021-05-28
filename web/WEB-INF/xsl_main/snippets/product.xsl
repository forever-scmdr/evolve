<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="../utils/utils.xsl"/>
	<xsl:import href="constants.xsl"/>

	<xsl:variable name="is_fav" select="page/@name = 'fav'"/>
	<xsl:variable name="is_compare" select="page/@name = 'compare'"/>
	<xsl:variable name="is_one_click" select="page/optional_modules/one_click/status = 'on'"/>
	<xsl:variable name="is_my_price" select="page/optional_modules/my_price/status = 'on'"/>
	<xsl:variable name="is_subscribe" select="page/optional_modules/product_subscribe/status = 'on'"/>
	<xsl:variable name="mp_link" select="if (page/optional_modules/my_price/link_name) then page/optional_modules/my_price/link_name else 'Моя цена'"/>
	<xsl:variable name="is_jur" select="page/registration[@type = 'user_jur']"/>
	<xsl:variable name="jur_price_on" select="page/optional_modules/display_settings/jur_price = 'on'"/>
	<xsl:variable name="price_param_name" select="if ($is_jur and $jur_price_on) then 'price_opt' else 'price'"/>
	<xsl:variable name="price_old_param_name" select="if ($is_jur and $jur_price_on) then 'price_opt_old' else 'price_old'"/>


	<xsl:template match="accessory | set | probe | product | assoc">
		<xsl:variable name="has_price" select="price and price != '0'"/>
		<xsl:variable name="prms" select="params/param"/>
		<xsl:variable name="has_lines" select="has_lines = '1'"/>
		<div class="card device">
			<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
			<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>

			<!-- zoom icon (not displayed, delete <div> with display: none to show) -->
			<div style="display: none;">
				<xsl:if test="main_pic and number(main_pic/@width) &gt; 200">
					<a href="{concat(@path, main_pic)}" class="magnific_popup-image zoom-icon_card" title="{name}" rel="nofollow">
						<i class="fas fa-search-plus"></i>
					</a>
				</xsl:if>
			</div>

			<!-- device image -->
			<a href="{show_product}" class="device__image img"><img src="{$pic_path}" alt="" /></a>

			<!-- device tags -->
			<div class="tags device__tags">
				<!--
				<xsl:for-each select="tag">
					<div class="tag device__tag"><xsl:value-of select="." /></div>
				</xsl:for-each>
				<xsl:for-each select="mark">
					<div class="tag device__tag"><xsl:value-of select="." /></div>
				</xsl:for-each>
				-->
				<xsl:for-each select="label">

					<div class="tag device__tag {f:translit(.)}">
						<xsl:value-of select="." />
					</div>
				</xsl:for-each>
			</div>

			<!-- quick view (not displayed, delete <div> with display: none to show) -->
			<!-- TODO add display check -->
			<div style="display: none">
				<a onclick="showDetails('{show_product_ajax}')" class="fast-preview-button" >Быстрый просмотр</a>
			</div>

			<!-- device title -->
			<a href="{show_product}" class="device__name" title="{name}"><span><xsl:value-of select="name"/></span></a>

			<!-- device identification code -->
			<div class="text_size_sm"><xsl:value-of select="code"/></div>

			<!-- device price (why <span class="price__value"> is doubled? fixed) -->
			<xsl:if test="$has_price">
				<div class="price device__price">
					<xsl:if test="price_old">
						<div class="price__item_old">
							<span class="price__value"><xsl:value-of select="f:exchange_cur(., $price_old_param_name, 0)"/></span>
						</div>
					</xsl:if>
					<div class="price__item_new">
						<span class="price__value"><xsl:if test="$has_lines" >от </xsl:if><xsl:value-of select="f:exchange_cur(., $price_param_name, 0)"/></span>
					</div>
				</div>
			</xsl:if>

			<xsl:if test="not($has_price)">
				<div class="price device__price">
					<div class="price__item_new">
						Ожидается
					</div>
				</div>
			</xsl:if>

			<!-- one click -->
			<xsl:if test="$is_one_click">
				<div class="text_sm" style="margin-top: auto;">
					<a href="{one_click_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-one_click">Купить в 1 клик</a>
				</div>
			</xsl:if>

			<!-- subscribe for update -->
			<xsl:if test="$is_subscribe">
				<div class="text_sm" style="margin-top: auto;">
					<a href="{subscribe_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-subscribe">Сообщить о появлении</a>
				</div>
			</xsl:if>

			<!-- propose my price -->
			<xsl:if test="$is_my_price">
				<div class="text_sm" style="margin-top: auto;">
					<a href="{my_price_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-my_price"><xsl:value-of select="$mp_link"/></a>
				</div>
			</xsl:if>

			<!-- device order -->
			<xsl:if test="not($has_lines)">
				<div class="order device-order" id="cart_list_{@id}">
					<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
						<xsl:if test="$has_price">
							<input type="number" class="input input_type_number" name="qty"
								   value="{if (min_qty) then f:num(min_qty) else 1}" min="{if (min_qty) then f:num(min_qty) else 0}" step="{if (step) then f:num(step) else 0.1}" />
							<button class="button button_not-available" type="submit"><xsl:value-of select="$to_cart_available_label"/></button>
						</xsl:if>
						<!-- правильн ли сделан блок для товара без цены -->
						<xsl:if test="not($has_price)">
							<input type="number" class="input input_type_number" name="qty"
								   value="{if (min_qty) then f:num(min_qty) else 1}" min="{if (min_qty) then f:num(min_qty) else 0}" step="{if (step) then f:num(step) else 0.1}" />
							<!-- кнопка запросить цену в списке товаров -->
							<button class="button" type="submit"><xsl:value-of select="$to_cart_na_label"/></button>
						</xsl:if>
					</form>
				</div>
			</xsl:if>
			<xsl:if test="$has_lines">
				<a class="button" href="{show_product}">Подробнее</a>
			</xsl:if>

			<!-- stock status (not displayed, delete <div> with display: none to show) -->
			<div style="display: none">
				<xsl:if test="(qty and number(qty) &gt; 0) or $has_lines">
					<div class="text_sm">в наличии</div>
				</xsl:if>
				<xsl:if test="(not(qty) or number(qty) &lt;= 0) and not($has_lines)">
					<div class="text_sm">под заказ</div>
				</xsl:if>
			</div>

			<!-- device actions (compare and favourites) -->
			<div class="add">
				<xsl:choose>
					<xsl:when test="$is_fav">
						<a href="{from_fav}" class="add__item icon-link">
							<div class="icon"><img src="img/icon-star.svg" alt="" /></div>
							<span><xsl:value-of select="$compare_remove_label"/></span>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<div id="fav_list_{@id}">
							<a href="{to_fav}" class="add__item icon-link" ajax="true" ajax-loader-id="fav_list_{@id}">
								<div class="icon"><img src="img/icon-star.svg" alt="" /></div>
								<span><xsl:value-of select="$compare_add_label"/></span>
							</a>
						</div>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:if test="not($is_compare)">
					<div id="compare_list_{@id}">
						<a href="{to_compare}" class="add__item icon-link" ajax="true" ajax-loader-id="compare_list_{@id}">
							<div class="icon"><img src="img/icon-balance.svg" alt="" /></div>
							<span><xsl:value-of select="$go_to_compare_label"/></span>
						</a>
					</div>
				</xsl:if>
				<xsl:if test="$is_compare">
					<a href="{from_compare}" class="add__item icon-link">
						<div class="icon"><img src="img/icon-balance.svg" alt="" /></div>
						<span><xsl:value-of select="$compare_remove_label"/></span>
					</a>
				</xsl:if>
			</div>
		</div>
	</xsl:template>


	<xsl:template match="accessory | set | probe | product | assoc" mode="lines">
		<xsl:variable name="has_price" select="price and price != '0'"/>
		<xsl:variable name="prms" select="params/param"/>
		<xsl:variable name="has_lines" select="has_lines = '1'"/>


		<div class="device device_row">
			<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
			<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>

			<div class="device__column">

				<!-- zoom icon (not displayed, delete <div> with display: none to show) -->
				<div style="display: none;">
					<xsl:if test="main_pic and number(main_pic/@width) &gt; 200">
						<a href="{concat(@path, main_pic)}" class="magnific_popup-image zoom-icon" title="{name}">
							<i class="fas fa-search-plus"></i>
						</a>
					</xsl:if>
				</div>

				<!-- quick view (not displayed, delete <div> with display: none to show) -->
				<!-- TODO add display check -->
				<div style="display: none">
					<a onclick="showDetails('{show_product_ajax}')" class="fast-preview-button" style="display: none">Быстрый просмотр</a>
				</div>

				<!-- device image -->
				<div class="device__image img">
					<a href="{show_product}">
						<img src="{$pic_path}" alt="" />
					</a>
				</div>

				<!-- device tags -->
				<div class="tags device__tags">
					<!--
					<xsl:for-each select="tag">
						<div class="tag device__tag"><xsl:value-of select="." /></div>
					</xsl:for-each>
					<xsl:for-each select="mark">
						<div class="tag device__tag"><xsl:value-of select="." /></div>
					</xsl:for-each>
					-->
					<xsl:for-each select="label">
						<div class="tag device__tag {f:translit(.)}">
							<xsl:value-of select="." />
						</div>
					</xsl:for-each>
				</div>

			</div>

			<div class="device__column">

				<!-- device title -->
				<a href="{show_product}" class="device__name"><span><xsl:value-of select="name"/></span></a>

				<!-- device identification code -->
				<div class="text_size_sm"><xsl:value-of select="code"/></div>

				<!-- device description parameters -->
				<div class="device__info">
					<table class="params">
						<xsl:variable name="user_defined_params" select="tokenize($sel_sec/params_list, '[\|;]\s*')"/>
						<xsl:variable name="is_user_defined" select="$sel_sec/params_list and not($sel_sec/params_list = '') and count($user_defined_params) &gt; 0"/>
						<xsl:variable name="captions" select="if ($is_user_defined) then $user_defined_params else params/param/@caption"/>
						<xsl:variable name="p" select="current()"/>
						<!-- <xsl:if test="//page/@name != 'fav'">
							<xsl:for-each select="$captions[position() &lt;5]">
								<xsl:variable name="param" select="$p/params/param[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()))]"/>
								<tr class="tr">
									<td><xsl:value-of select="$param/@caption"/></td>
									<td><xsl:value-of select="$param"/></td>
								</tr>
							</xsl:for-each>
						</xsl:if> -->
						<!-- <xsl:if test="//page/@name = 'fav'"> -->
							<xsl:for-each select="$captions[position() &lt; 5]">
								<xsl:variable name="param" select="$p/params/param[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()))]"/>

								<xsl:variable name="param_unit" select="$param/@description"/>

								<tr class="tr">
									<td><xsl:value-of select="$param/@caption"/></td>
									<td><xsl:value-of select="normalize-space(concat($param, ' ', $param_unit))"/></td>
								</tr>
							</xsl:for-each>
						<!-- </xsl:if> -->
					</table>
				</div>

			</div>

			<!-- device price -->
			<div class="device__column">
				<div class="price device__price">
					<xsl:if test="$has_price">
						<xsl:if test="price_old">
							<div class="price__item_old">
								<span class="price__value"><xsl:value-of select="f:exchange_cur(., $price_old_param_name, 0)"/></span>
							</div>
						</xsl:if>
						<div class="price__item_new">
							<span class="price__value"><xsl:if test="$has_lines" >от </xsl:if><xsl:value-of select="f:exchange_cur(., $price_param_name, 0)"/></span>
						</div>
					</xsl:if>
					<xsl:if test="not($has_price)">
						<div class="price__item_new">
							Ожидается
						</div>
					</xsl:if>
				</div>
			</div>

			<!-- stock status (not displayed, delete display: none to show) -->
			<div class="device__column" style="display: none">
				<xsl:if test="(qty and number(qty) &gt; 0) or $has_lines">
					<div class="">в наличии</div>
				</xsl:if>
				<xsl:if test="(not(qty) or number(qty) &lt;= 0) and not($has_lines)">
					<div class="">под заказ</div>
				</xsl:if>
			</div>

			<div class="device__column">

				<!-- device order -->
				<xsl:if test="not($has_lines)">
					<div class="order device-order" id="cart_list_{@id}">
						<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
							<xsl:if test="$has_price">
								<input type="number" class="input input_type_number" name="qty"
									   value="{if (min_qty) then f:num(min_qty) else 1}" min="{if (min_qty) then f:num(min_qty) else 0}" step="{if (step) then f:num(step) else 0.1}" />
								<button class="button" type="submit"><xsl:value-of select="$to_cart_available_label"/></button>
							</xsl:if>
							<xsl:if test="not($has_price)">
								<input type="number" class="input input_type_number" name="qty"
									   value="{if (min_qty) then f:num(min_qty) else 1}" min="{if (min_qty) then f:num(min_qty) else 0}" step="{if (step) then f:num(step) else 0.1}" />
								<button class="button" type="submit"><xsl:value-of select="$to_cart_na_label"/></button>
							</xsl:if>
						</form>
					</div>
				</xsl:if>
				<xsl:if test="$has_lines">
					<div class="order device-order">
						<a class="button" href="{show_product}">Подробнее</a>
					</div>
				</xsl:if>

				<!-- one click -->
				<xsl:if test="$is_one_click">
					<a href="{one_click_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-one_click">Купить в 1 клик</a>
				</xsl:if>

				<!-- subscribe for update -->
				<xsl:if test="$is_subscribe">
					<a href="{subscribe_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-subscribe">Сообщить о появлении</a>
				</xsl:if>

				<!-- propose my price -->
				<xsl:if test="$is_my_price">
					<a href="{my_price_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-my_price"><xsl:value-of select="$mp_link"/></a>
				</xsl:if>

				<!-- device actions (compare and favourites) -->
				<div class="add">
					<xsl:choose>
						<xsl:when test="$is_fav">
							<a href="{from_fav}" class="add__item icon-link">
								<div class="icon"><img src="img/icon-star.svg" alt="" /></div>
								<span><xsl:value-of select="$compare_remove_label"/></span>
							</a>
						</xsl:when>
						<xsl:otherwise>
							<div id="fav_list_{@id}">
								<a href="{to_fav}" class="add__item icon-link" ajax="true" ajax-loader-id="fav_list_{@id}">
									<div class="icon"><img src="img/icon-star.svg" alt="" /></div>
									<span><xsl:value-of select="$compare_add_label"/></span>
								</a>
							</div>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:if test="not($is_compare)">
						<div id="compare_list_{@id}">
							<a href="{to_compare}" class="add__item icon-link" ajax="true" ajax-loader-id="compare_list_{@id}">
								<div class="icon"><img src="img/icon-balance.svg" alt="" /></div>
								<span><xsl:value-of select="$go_to_compare_label"/></span>
							</a>
						</div>
					</xsl:if>
					<xsl:if test="$is_compare">
						<a href="{from_compare}" class="add__item icon-link">
							<div class="icon"><img src="img/icon-balance.svg" alt="" /></div>
							<span><xsl:value-of select="$compare_remove_label"/></span>
						</a>
					</xsl:if>
				</div>
			</div>
		</div>


	</xsl:template>

</xsl:stylesheet>
