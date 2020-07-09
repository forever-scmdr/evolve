<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="../utils/utils.xsl"/>

	<xsl:variable name="is_fav" select="page/@name = 'fav'"/>
	<xsl:variable name="is_compare" select="page/@name = 'compare'"/>
	<xsl:variable name="is_one_click" select="page/optional_modules/one_click/status = 'on'"/>
	<xsl:variable name="is_my_price" select="page/optional_modules/my_price/status = 'on'"/>
	<xsl:variable name="mp_link" select="if (page/optional_modules/my_price/link_name) then page/optional_modules/my_price/link_name else 'Моя цена'"/>
	<xsl:variable name="is_jur" select="page/registration[@type = 'user_jur']"/>
	<xsl:variable name="jur_price_on" select="page/optional_modules/display_settings/jur_price = 'on'"/>
	<xsl:variable name="price_param_name" select="if ($is_jur and $jur_price_on) then 'price_opt' else 'price'"/>
	<xsl:variable name="price_old_param_name" select="if ($is_jur and $jur_price_on) then 'price_opt_old' else 'price_old'"/>


	<xsl:template match="accessory | set | probe | product | assoc">
		<xsl:variable name="has_price" select="price and price != '0'"/>
		<xsl:variable name="prms" select="params/param"/>
		<xsl:variable name="has_lines" select="has_lines = '1'"/>
		<div class="card device_card">
			<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
			<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>


			<!-- device image -->
			<a href="{show_product}" class="imgg__container">
				<img src="{$pic_path}" class="device__imgg" />
			</a>

			<!-- zoom icon -->
			<xsl:if test="main_pic and number(main_pic/@width) &gt; 200">
				<a href="{concat(@path, main_pic)}" class="magnific_popup-image zoom-icon_card" title="{name}" rel="nofollow">
					<i class="fas fa-search-plus"></i>
				</a>
			</xsl:if>

			<!-- device title -->
			<a href="{show_product}" class="dvc__title" title="{name}"><xsl:value-of select="name"/></a>

			<!-- article number -->
			<div class="text_sm">арт. <xsl:value-of select="code"/></div>

			<!-- device price -->
			<xsl:if test="$has_price">
				<div class="price price_device">
					<div>
						<strong>
							<xsl:if test="$has_lines" >от </xsl:if>
							<xsl:value-of select="f:exchange_cur(., $price_param_name, 0)"/>
						</strong>
					</div>
					<xsl:if test="price_old">
						<div class="price_old">
							<xsl:value-of select="f:exchange_cur(., $price_old_param_name, 0)"/>
						</div>
					</xsl:if>
				</div>
			</xsl:if>
			<xsl:if test="not($has_price)">
				<div></div>
			</xsl:if>

			<!-- one click -->
			<xsl:if test="$is_one_click">
				<div class="text_sm" style="margin-top: auto;">
					<a href="{one_click_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-one_click">Купить в 1 клик</a>
				</div>
			</xsl:if>

			<!-- subscribe -->
			<div class="text_sm" style="margin-top: auto;">
				<a href="{subscribe_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-subscribe">Сообщить о появлении</a>
			</div>

			<xsl:if test="$is_my_price">
				<div class="text_sm" style="margin-top: auto;">
					<a href="{my_price_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-my_price"><xsl:value-of select="$mp_link"/></a>
				</div>
			</xsl:if>

			<!-- device order -->
			<div class="order_card">
				<xsl:if test="not($has_lines)">
					<div id="cart_list_{@id}">
						<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
							<xsl:if test="$has_price">
								<input type="number" class="text-input" name="qty" value="1" min="0"/>
								<input type="submit" class="button" value="В корзину"/>
							</xsl:if>
							<xsl:if test="not($has_price)">
								<input type="hidden" class="text-input" name="qty" value="1" min="0"/>
								<input type="submit" class="button not_available" value="Запросить цену"/>
							</xsl:if>
						</form>
					</div>
				</xsl:if>
				<xsl:if test="$has_lines">
					<a class="button" href="{show_product}">Подробнее</a>
				</xsl:if>
			</div>

			<!-- stock status -->
			<xsl:if test="(qty and number(qty) &gt; 0) or $has_lines">
				<div class="text_sm">в наличии</div>
			</xsl:if>
			<xsl:if test="(not(qty) or number(qty) &lt;= 0) and not($has_lines)">
				<div class="text_sm">под заказ</div>
			</xsl:if>

			<!-- device actions -->
			<div class="text_sm actions_device">
				<xsl:if test="not($is_compare)">
					<div id="compare_list_{@id}">
						<a href="{to_compare}" ajax="true" ajax-loader-id="compare_list_{@id}">
							<i class="fas fa-balance-scale"></i>сравнить
						</a>
					</div>
				</xsl:if>
				<xsl:if test="$is_compare">
					<span><i class="fas fa-balance-scale"></i>&#160;<a href="{from_compare}">убрать</a></span>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="$is_fav">
						<a href="{from_fav}" class="icon-link device__action-link"><i class="fas fa-star"></i>убрать</a>
					</xsl:when>
					<xsl:otherwise>
						<div id="fav_list_{@id}">
							<a href="{to_fav}" ajax="true" ajax-loader-id="fav_list_{@id}">
								<i class="fas fa-star"></i>отложить
							</a>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</div>

			<!-- device tags -->
			<div class="tags_card">
				<xsl:for-each select="tag">
					<div class="tag"><xsl:value-of select="." /></div>
				</xsl:for-each>
				<xsl:for-each select="mark">
					<div class="tag"><xsl:value-of select="." /></div>
				</xsl:for-each>
			</div>
		</div>
	</xsl:template>

	<!-- device backup -->
	<!-- <xsl:template match="accessory | set | probe | product | assoc">
		<xsl:variable name="has_price" select="price and price != '0'"/>
		<xsl:variable name="prms" select="params/param"/>
		<xsl:variable name="has_lines" select="has_lines = '1'"/>
		<div class="device items-catalog__device">
			<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
			<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>

			<xsl:if test="main_pic and number(main_pic/@width) &gt; 200">
				<a href="{concat(@path, main_pic)}" class="magnific_popup-image zoom-icon" title="{name}" rel="nofollow">
					<i class="fas fa-search-plus"></i>
				</a>
			</xsl:if>
			<a href="{show_product}" class="device__image" style="background-image: {concat('url(',$pic_path,');')}"></a>
			<a href="{show_product}" class="device__title" title="{name}"><xsl:value-of select="name"/></a>
			<div class="device__article-number"><xsl:value-of select="code"/></div>
			<xsl:if test="$has_price">
				<div class="device__price">
					<xsl:if test="price_old"><div class="price_old"><span><xsl:value-of select="price_old"/> руб.</span></div></xsl:if>
					<div class="price_normal"><xsl:if test="$has_lines" >от </xsl:if><xsl:value-of select="price"/> руб.</div>
				</div>
			</xsl:if>
			<xsl:if test="not($has_price)">
				<div class="device__price">

				</div>
			</xsl:if>
			<xsl:if test="$is_one_click">
				<div class="extra-links">
					<a href="{one_click_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-one_click">Купить в 1 клик</a>
				</div>
			</xsl:if>
			<div class="device__order">
				<xsl:if test="not($has_lines)">
					<div id="cart_list_{@id}">
						<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
							<xsl:if test="$has_price">
								<input type="number" class="text-input" name="qty" value="1" min="0"/>
								<input type="submit" class="button" value="В корзину"/>
							</xsl:if>
							<xsl:if test="not($has_price)">
								<input type="hidden" class="text-input" name="qty" value="1" min="0"/>
								<input type="submit" class="button not_available" value="Запросить цену"/>
							</xsl:if>
						</form>
					</div>
				</xsl:if>
				<xsl:if test="$has_lines">
					<a class="button" href="{show_product}">Подробнее</a>
				</xsl:if>
			</div>
			<xsl:if test="(qty and number(qty) &gt; 0) or $has_lines">
				<div class="device__in-stock"><i class="fas fa-check"></i> в наличии</div>
			</xsl:if>
			<xsl:if test="(not(qty) or number(qty) &lt;= 0) and not($has_lines)">
				<div class="device__in-stock device__in-stock_no"><i class="far fa-clock"></i> под заказ</div>
			</xsl:if>
			<div class="device__actions">
				<xsl:if test="not($is_compare)">
					<div id="compare_list_{@id}">
						<a href="{to_compare}" class="icon-link device__action-link" ajax="true" ajax-loader-id="compare_list_{@id}">
							<i class="fas fa-balance-scale"></i>сравнить
						</a>
					</div>
				</xsl:if>
				<xsl:if test="$is_compare">
					<span><i class="fas fa-balance-scale"></i>&#160;<a href="{from_compare}">убрать</a></span>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="$is_fav">
						<a href="{from_fav}" class="icon-link device__action-link"><i class="fas fa-star"></i>убрать</a>
					</xsl:when>
					<xsl:otherwise>
						<div id="fav_list_{@id}">
							<a href="{to_fav}" class="icon-link device__action-link" ajax="true" ajax-loader-id="fav_list_{@id}">
								<i class="fas fa-star"></i>отложить
							</a>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</div>
			<xsl:for-each select="mark">
				<div class="device__tag"><xsl:value-of select="." /></div>
			</xsl:for-each>
		</div>
	</xsl:template> -->



	<!-- <xsl:template match="accessory | set | probe | product | assoc" mode="lines">
		<xsl:variable name="has_price" select="price and price != '0'"/>
		<xsl:variable name="prms" select="params/param"/>
		<xsl:variable name="has_lines" select="has_lines = '1'"/>
		<div class="device device_row">
			<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
			<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>
			<xsl:if test="main_pic and number(main_pic/@width) &gt; 200">
				<a href="{concat(@path, main_pic)}" class="magnific_popup-image zoom-icon" title="{name}">
					<i class="fas fa-search-plus"></i>
				</a>
			</xsl:if>
			<a href="{show_product}" class="device__image device_row__image" style="background-image: {concat('url(',$pic_path,');')}">&#160;</a>
			<div class="device__info">
				<a href="{show_product}" class="device__title"><xsl:value-of select="name"/></a>
				<div class="device__description">
					<xsl:value-of select="description" disable-output-escaping="yes"/>
					<xsl:for-each select="params/param">
						<span style="color: #616161;"><xsl:value-of select="@caption"/></span>&#160;-&#160;<xsl:value-of select="."/>
						<xsl:text>; </xsl:text>
					</xsl:for-each>
				</div>
			</div>
			<div class="device__article-number"><xsl:value-of select="code"/></div>
			<div class="device__actions device_row__actions">
				<xsl:if test="not($is_compare)">
					<div id="compare_list_{@id}">
						<a href="{to_compare}" class="icon-link device__action-link" ajax="true" ajax-loader-id="compare_list_{@id}">
							<i class="fas fa-balance-scale"></i>сравнить
						</a>
					</div>
				</xsl:if>
				<xsl:if test="$is_compare">
					<span><i class="fas fa-balance-scale"></i>&#160;<a href="{from_compare}">убрать</a></span>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="$is_fav">
						<a href="{from_fav}" class="icon-link device__action-link"><i class="fas fa-star"></i>убрать</a>
					</xsl:when>
					<xsl:otherwise>
						<div id="fav_list_{@id}">
							<a href="{to_fav}" class="icon-link device__action-link" ajax="true" ajax-loader-id="fav_list_{@id}">
								<i class="fas fa-star"></i>отложить
							</a>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</div>
			<xsl:if test="$has_price">
				<div class="device__price device_row__price">
					<xsl:if test="price_old"><div class="price_old"><span><xsl:value-of select="price_old"/> руб.</span></div></xsl:if>
					<div class="price_normal"><xsl:if test="$has_lines" >от </xsl:if><xsl:value-of select="price"/> руб.</div>
				</div>
			</xsl:if>
			<xsl:if test="not($has_price)">
				<div class="device__price device_row__price">

				</div>
			</xsl:if>
			<div class="device__order device_row__order">
				<xsl:if test="$is_one_click">
					<a href="{one_click_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-one_click">Купить в 1 клик</a>
				</xsl:if>
				<xsl:if test="not($has_lines)">
					<div id="cart_list_{@id}">
						<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
							<xsl:if test="$has_price">
								<input type="number" class="text-input" name="qty" value="1" min="0"/>
								<input type="submit" class="button" value="В корзину"/>
							</xsl:if>
							<xsl:if test="not($has_price)">
								<input type="hidden" class="text-input" name="qty" value="1" min="0"/>
								<input type="submit" class="button not_available" value="Запросить цену"/>
							</xsl:if>
						</form>
					</div>
				</xsl:if>
				<xsl:if test="$has_lines">
					<a class="button" href="{show_product}">Подробнее</a>
				</xsl:if>
				<xsl:if test="(qty and number(qty) &gt; 0) or $has_lines">
					<div class="device__in-stock device_row__in-stock"><i class="fas fa-check"></i> в наличии</div>
				</xsl:if>
				<xsl:if test="(not(qty) or number(qty) &lt;= 0) and not($has_lines)">
					<div class="device__in-stock device_row__in-stock"><i class="fas fa-check"></i> под заказ</div>
				</xsl:if>
			</div>
			<xsl:for-each select="tag">
				<div class="device__tag device_row__tag"><xsl:value-of select="." /></div>
			</xsl:for-each>
		</div>
	</xsl:template> -->

	<xsl:template match="accessory | set | probe | product | assoc" mode="lines">
		<xsl:variable name="has_price" select="price and price != '0'"/>
		<xsl:variable name="prms" select="params/param"/>
		<xsl:variable name="has_lines" select="has_lines = '1'"/>
		<div class="card device_row">
			<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
			<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>

			<div>
				<!-- zoom icon -->
				<xsl:if test="main_pic and number(main_pic/@width) &gt; 200">
					<a href="{concat(@path, main_pic)}" class="magnific_popup-image zoom-icon" title="{name}">
						<i class="fas fa-search-plus"></i>
					</a>
				</xsl:if>
				
				<!-- device image -->
				<a href="{show_product}" class="device__image device_row__image" style="background-image: {concat('url(',$pic_path,');')}"></a>
			</div>

			<!-- device info -->
			<div>
				<!-- device tags -->
				<div class="tags_row">
					<xsl:for-each select="tag">
						<div class="tag">
							<xsl:value-of select="." />
						</div>
					</xsl:for-each>
					<xsl:for-each select="mark">
						<div class="tag">
							<xsl:value-of select="." />
						</div>
					</xsl:for-each>
				</div>

				<!-- device title -->
				<a href="{show_product}" class="device__title"><xsl:value-of select="name"/></a>

				<!-- article number -->
				<div class="device__article-number"><xsl:value-of select="code"/></div>

				<!-- device description -->
				<div class="device__description">
					<!-- <xsl:value-of select="description" disable-output-escaping="yes"/> -->
					<xsl:for-each select="params/param">
						<span><xsl:value-of select="@caption"/></span>&#160;-&#160;<xsl:value-of select="."/>
						<xsl:text>; </xsl:text>
					</xsl:for-each>
				</div>
			</div>

			

			

			<!-- device price -->
			<div>
				<xsl:if test="$has_price">
					<div style="white-space: nowrap;">
						<div class="price_normal"><xsl:if test="$has_lines" >от </xsl:if><xsl:value-of select="f:exchange_cur(., $price_param_name, 0)"/></div>
						<xsl:if test="price_old"><div class="price_old"><span><xsl:value-of select="f:exchange_cur(., $price_old_param_name, 0)"/></span></div></xsl:if>
					</div>
				</xsl:if>
				<xsl:if test="not($has_price)">
					<div>---</div>
				</xsl:if>
				
				<!-- stock status -->
				<xsl:if test="(qty and number(qty) &gt; 0) or $has_lines">
					<div class="">в наличии</div>
				</xsl:if>
				<xsl:if test="(not(qty) or number(qty) &lt;= 0) and not($has_lines)">
					<div class="">под заказ</div>
				</xsl:if>
			</div>

			<!-- device order -->
			<div>
				<xsl:if test="not($has_lines)">
					<div id="cart_list_{@id}">
						<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
							<xsl:if test="$has_price">
								<input type="number" class="text-input" name="qty" value="1" min="0"/>
								<input type="submit" class="button" value="В корзину"/>
							</xsl:if>
							<xsl:if test="not($has_price)">
								<input type="hidden" class="text-input" name="qty" value="1" min="0"/>
								<input type="submit" class="button not_available" value="Запросить цену"/>
							</xsl:if>
						</form>
					</div>
				</xsl:if>
				<xsl:if test="$has_lines">
					<a class="button" href="{show_product}">Подробнее</a>
				</xsl:if>

				<!-- one click -->
				<xsl:if test="$is_one_click">
					<a href="{one_click_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-one_click">Купить в 1 клик</a>
				</xsl:if>

				<!-- subscribe -->
				<a href="{subscribe_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-subscribe">Сообщить о появлении</a>

				<xsl:if test="$is_my_price">
					<a href="{my_price_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-my_price"><xsl:value-of select="$mp_link"/></a>
				</xsl:if>

				<!-- device actions -->
				<div class="device__actions device_row__actions">
					<xsl:if test="not($is_compare)">
						<div id="compare_list_{@id}">
							<a href="{to_compare}" class="icon-link device__action-link" ajax="true" ajax-loader-id="compare_list_{@id}">
								<i class="fas fa-balance-scale"></i>сравнить
							</a>
						</div>
					</xsl:if>
					<xsl:if test="$is_compare">
						<span><i class="fas fa-balance-scale"></i>&#160;<a href="{from_compare}">убрать</a></span>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="$is_fav">
							<a href="{from_fav}" class="icon-link device__action-link"><i class="fas fa-star"></i>убрать</a>
						</xsl:when>
						<xsl:otherwise>
							<div id="fav_list_{@id}">
								<a href="{to_fav}" class="icon-link device__action-link" ajax="true" ajax-loader-id="fav_list_{@id}">
									<i class="fas fa-star"></i>отложить
								</a>
							</div>
						</xsl:otherwise>
					</xsl:choose>
				</div>

				
			</div>

			
		</div>
	</xsl:template>

</xsl:stylesheet>