<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="../utils/multiple_prices.xsl"/>
	<xsl:import href="constants.xsl"/>

	<xsl:variable name="ops" select="page/optional_modules"/>
	<xsl:variable name="disp" select="$ops/display_settings"/>
	<xsl:variable name="is_fav" select="page/@name = 'fav'"/>
	<xsl:variable name="is_compare" select="page/@name = 'compare'"/>
	<xsl:variable name="has_one_click" select="$ops/one_click/status = 'on'"/>
	<xsl:variable name="has_my_price" select="$ops/my_price/status = 'on'"/>
	<xsl:variable name="has_subscribe" select="$ops/product_subscribe/status = 'on'"/>
	<xsl:variable name="has_quick_view" select="$disp/product_quick_view = 'on'"/>
	<xsl:variable name="has_fav" select="$disp/favourites = 'on'"/>
	<xsl:variable name="has_compare" select="$disp/compare = 'on'"/>
	<xsl:variable name="has_cart" select="$disp/cart = 'on'"/>
	<xsl:variable name="mp_link" select="if ($ops/my_price/link_name) then $ops/my_price/link_name else 'Моя цена'"/>
	<xsl:variable name="is_jur" select="page/registration[@type = 'user_jur']"/>
	<xsl:variable name="jur_price_on" select="$disp/jur_price = 'on'"/>
	<xsl:variable name="price_param_name" select="if ($is_jur and $jur_price_on) then 'price_opt' else 'price'"/>
	<xsl:variable name="price_old_param_name" select="if ($is_jur and $jur_price_on) then 'price_opt_old' else 'price_old'"/>
	<xsl:variable name="product_params_limit" select="6"/>
	<xsl:variable name="to_cart_api_link" select="page/to_cart_api"/>
	<xsl:variable name="is_admin" select="page/@name = 'admin_search'"/>
	<xsl:variable name="analogs" select="page/extra_query/analogs"/>
	<xsl:variable name="multiple_analog_sets" select="count($analogs/set) &gt; 1"/>
	<xsl:variable name="step_default" select="if (page/catalog/default_step) then f:num(page/catalog/default_step) else 1"/>
	<xsl:variable name="sel_sec" select="none"/>



	<!--///////////////////////////////////////////////////////////////////////////////////////////////-->
	<!--/////////////                                                                      ////////////-->
	<!--/////////////                    ТОВАР В ВИДЕ ТАБЛИЦЫ (плитка)                     ////////////-->
	<!--/////////////                                                                      ////////////-->
	<!--///////////////////////////////////////////////////////////////////////////////////////////////-->


	<xsl:template match="*" mode="product-table">
		<xsl:variable name="has_price" select="price and price != '0'"/>
		<xsl:variable name="prms" select="params/param"/>
		<xsl:variable name="has_lines" select="has_lines = '1'"/>
		<xsl:variable name="plain" select="if (section_name and not(section_name = '')) then section_name else plain_section/name"/>
		<div class="card device">
			<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
			<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>

			<!-- zoom icon (not displayed, delete <div> with display: none to show) -->
			<!--<div style="display: none">
				<xsl:if test="main_pic and number(main_pic/@width) &gt; 200">
					<a href="{concat(@path, main_pic)}" class="magnific_popup-image zoom-icon_card" title="{name}" rel="nofollow">
						<i class="fas fa-search-plus"></i>
					</a>
				</xsl:if>
			</div>-->

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
			<xsl:if test="$has_quick_view">
				<div>
					<a onclick="showDetails('{show_product_ajax}')" class="fast-preview-button" >Быстрый просмотр</a>
				</div>
			</xsl:if>

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
						<!-- Для обычных товаров (не из каталога price_catalog) -->
						<xsl:if test="not($plain)">
							<span class="price__value"><xsl:if test="$has_lines" >от </xsl:if><xsl:value-of select="f:exchange_cur(., $price_param_name, 0)"/></span>
						</xsl:if>
						<!-- Для товаров из каталога price_catalog -->
						<xsl:if test="$plain">
							<span class="price__value"><xsl:call-template name="ALL_PRICES">
								<xsl:with-param name="need_sum" select="false()"/>
								<xsl:with-param name="price_in_currency" select="f:exchange(current(), 'price', 0)"/>
								<xsl:with-param name="product" select="."/>
								<xsl:with-param name="section_name" select="$plain"/>
							</xsl:call-template></span>
						</xsl:if>
					</div>
				</div>
			</xsl:if>

			<xsl:call-template name="EXTRA_ORDERING_TYPES">
				<xsl:with-param name="p" select="current()"/>
			</xsl:call-template>

			<!-- device order -->
			<xsl:call-template name="CART_BUTTON">
				<xsl:with-param name="p" select="current()"/>
			</xsl:call-template>

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
				<xsl:call-template name="FAV_AND_COMPARE">
					<xsl:with-param name="p" select="current()"/>
				</xsl:call-template>
			</div>
		</div>
	</xsl:template>


	<!-- /////////////////////// -->
	<!-- ТОВАР В ВИДЕ ТАБЛИЦЫ (плитка), но для API -->
	<!-- /////////////////////// -->

	<xsl:template match="*" mode="product-table-api">
		<xsl:variable name="has_price" select="price and price != '0'"/>
		<xsl:variable name="multipe_prices" select="prices"/>
		<div class="card device">
			<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
			<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>

			<!-- zoom icon (not displayed, delete <div> with display: none to show) -->
			<div style="display: none">
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
				<xsl:for-each select="label">
					<div class="tag device__tag {f:translit(.)}">
						<xsl:value-of select="." />
					</div>
				</xsl:for-each>
			</div>

			<!-- quick view (not displayed, delete <div> with display: none to show) -->
			<xsl:if test="$has_quick_view">
				<div>
					<a onclick="showDetails('{show_product_ajax}')" class="fast-preview-button" >Быстрый просмотр</a>
				</div>
			</xsl:if>

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
						<!-- Для обычных товаров (не из каталога price_catalog) -->
						<xsl:if test="not($multipe_prices)">
							<span class="price__value"><xsl:value-of select="f:exchange_cur(., $price_param_name, 0)"/></span>
						</xsl:if>
						<!-- Для товаров из каталога price_catalog -->
						<xsl:if test="$multipe_prices">
							<span class="price__value"><xsl:call-template name="ALL_PRICES_API">
								<xsl:with-param name="need_sum" select="false()"/>
								<xsl:with-param name="product" select="."/>
							</xsl:call-template></span>
						</xsl:if>
					</div>
				</div>
			</xsl:if>

			<!-- device order -->
			<xsl:call-template name="CART_BUTTON_API">
				<xsl:with-param name="p" select="current()"/>
			</xsl:call-template>

		</div>
	</xsl:template>




	<!--///////////////////////////////////////////////////////////////////////////////////////////////-->
	<!--/////////////                                                                      ////////////-->
	<!--/////////////         ТОВАР ОТДЕЛЬНЫМИ СТРОКАМИ (расширенная информация)           ////////////-->
	<!--/////////////                                                                      ////////////-->
	<!--///////////////////////////////////////////////////////////////////////////////////////////////-->



	<xsl:template match="*" mode="product-list">
		<xsl:variable name="has_price" select="price and price != '0'"/>
		<xsl:variable name="prms" select="params/param"/>
		<xsl:variable name="has_lines" select="has_lines = '1'"/>
		<xsl:variable name="plain" select="if (section_name and not(section_name = '')) then section_name else plain_section/name"/>


		<div class="device device_row">
			<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
			<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>

			<div class="device__column">

				<!-- zoom icon (not displayed, delete <div> with display: none to show) -->
				<div style="display: none">
					<xsl:if test="main_pic and number(main_pic/@width) &gt; 200">
						<a href="{concat(@path, main_pic)}" class="magnific_popup-image zoom-icon" title="{name}">
							<i class="fas fa-search-plus"></i>
						</a>
					</xsl:if>
				</div>

				<!-- quick view (not displayed, delete <div> with display: none to show) -->
				<xsl:if test="$has_quick_view">
					<div style="display: none">
						<a onclick="showDetails('{show_product_ajax}')" class="fast-preview-button" style="display: none">Быстрый просмотр</a>
					</div>
				</xsl:if>

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

						<xsl:if test="//page/@name != 'fav'">
							<tbody>
								<xsl:for-each select="$captions[position() &lt;= $product_params_limit]">
									<xsl:variable name="param" select="$p/params/param[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()))]"/>
									<tr class="tr">
										<td><xsl:value-of select="$param/@caption"/></td>
										<td><xsl:value-of select="$param"/></td>
									</tr>
								</xsl:for-each>
								<xsl:if test="count($captions) &gt; $product_params_limit">
									<tr>
										<td colspan="2">
											<a class="toggle" href="#params-{@id}" rel="Скрыть параметры">Покзать параметры</a>
										</td>
									</tr>
								</xsl:if>
							</tbody>
							<xsl:if test="count($captions) &gt; $product_params_limit">
								<tbody id="params-{@id}" style="display:none;">
									<xsl:for-each select="$captions[position() &gt; $product_params_limit]">
										<xsl:variable name="param" select="$p/params/param[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()))]"/>
										<tr class="tr">
											<td><xsl:value-of select="$param/@caption"/></td>
											<td><xsl:value-of select="$param"/></td>
										</tr>
									</xsl:for-each>
								</tbody>
							</xsl:if>
						</xsl:if>
						<xsl:if test="//page/@name = 'fav'">
							<xsl:for-each select="$captions[position() &lt; 5]">
								<xsl:variable name="param" select="$p/params/param[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()))]"/>
								<tr class="tr">
									<td><xsl:value-of select="$param/@caption"/></td>
									<td><xsl:value-of select="$param"/></td>
								</tr>
							</xsl:for-each>
						</xsl:if>
					</table>

<!--					<xsl:value-of select="text" disable-output-escaping="yes"/>-->
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
							<!-- Для обычных товаров (не из каталога price_catalog) -->
							<xsl:if test="not($plain)">
								<span class="price__value"><xsl:if test="$has_lines" >от </xsl:if><xsl:value-of select="f:exchange_cur(., $price_param_name, 0)"/></span>
							</xsl:if>
							<!-- Для товаров из каталога price_catalog -->
							<xsl:if test="$plain">
								<span class="price__value"><xsl:call-template name="ALL_PRICES">
									<xsl:with-param name="need_sum" select="false()"/>
									<xsl:with-param name="price_in_currency" select="f:exchange(current(), 'price', 0)"/>
									<xsl:with-param name="product" select="."/>
									<xsl:with-param name="section_name" select="$plain"/>
								</xsl:call-template></span>
							</xsl:if>
						</div>
					</xsl:if>
					<xsl:if test="not($has_price)">
						<div></div>
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
				<xsl:call-template name="CART_BUTTON">
					<xsl:with-param name="p" select="current()"/>
				</xsl:call-template>

				<xsl:call-template name="EXTRA_ORDERING_TYPES">
					<xsl:with-param name="p" select="current()"/>
				</xsl:call-template>

				<!-- device actions (compare and favourites) -->
				<div class="add">
					<xsl:call-template name="FAV_AND_COMPARE">
						<xsl:with-param name="p" select="current()"/>
					</xsl:call-template>
				</div>
			</div>
		</div>
	</xsl:template>



	<!-- /////////////////////// -->
	<!-- ТОВАР ОТДЕЛЬНЫМИ СТРОКАМИ (расширенная информация), но для API -->
	<!-- /////////////////////// -->

	<xsl:template match="*" mode="product-list-api">
		<xsl:variable name="has_price" select="price and price != '0'"/>
		<xsl:variable name="multipe_prices" select="prices"/>
		<div class="device device_row">
			<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
			<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>

			<div class="device__column">

				<!-- zoom icon (not displayed, delete <div> with display: none to show) -->
				<div style="display: none">
					<xsl:if test="main_pic and number(main_pic/@width) &gt; 200">
						<a href="{concat(@path, main_pic)}" class="magnific_popup-image zoom-icon" title="{name}">
							<i class="fas fa-search-plus"></i>
						</a>de
					</xsl:if>
				</div>

				<!-- quick view (not displayed, delete <div> with display: none to show) -->
				<xsl:if test="$has_quick_view">
					<div style="display: none">
						<a onclick="showDetails('{show_product_ajax}')" class="fast-preview-button" style="display: none">Быстрый просмотр</a>
					</div>
				</xsl:if>

				<!-- device image -->
				<div class="device__image img">
					<a href="{show_product}">
						<img src="{$pic_path}" alt="" />
					</a>
				</div>

				<!-- device tags -->
				<div class="tags device__tags">
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

<!--					<table class="params">-->
<!--						<xsl:variable name="user_defined_params" select="tokenize($sel_sec/params_list, '[\|;]\s*')"/>-->
<!--						<xsl:variable name="is_user_defined" select="$sel_sec/params_list and not($sel_sec/params_list = '') and count($user_defined_params) &gt; 0"/>-->
<!--						<xsl:variable name="captions" select="if ($is_user_defined) then $user_defined_params else params/param/@caption"/>-->
<!--						<xsl:variable name="p" select="current()"/>-->

<!--						<xsl:if test="//page/@name != 'fav'">-->
<!--							<tbody>-->
<!--								<xsl:for-each select="$captions[position() &lt;= $product_params_limit]">-->
<!--									<xsl:variable name="param" select="$p/params/param[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()))]"/>-->
<!--									<tr class="tr">-->
<!--										<td><xsl:value-of select="$param/@caption"/></td>-->
<!--										<td><xsl:value-of select="$param"/></td>-->
<!--									</tr>-->
<!--								</xsl:for-each>-->
<!--								<xsl:if test="count($captions) &gt; $product_params_limit">-->
<!--									<tr>-->
<!--										<td colspan="2">-->
<!--											<a class="toggle" href="#params-{@id}" rel="Скрыть параметры">Покзать параметры</a>-->
<!--										</td>-->
<!--									</tr>-->
<!--								</xsl:if>-->
<!--							</tbody>-->
<!--							<xsl:if test="count($captions) &gt; $product_params_limit">-->
<!--								<tbody id="params-{@id}" style="display:none;">-->
<!--									<xsl:for-each select="$captions[position() &gt; $product_params_limit]">-->
<!--										<xsl:variable name="param" select="$p/params/param[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()))]"/>-->
<!--										<tr class="tr">-->
<!--											<td><xsl:value-of select="$param/@caption"/></td>-->
<!--											<td><xsl:value-of select="$param"/></td>-->
<!--										</tr>-->
<!--									</xsl:for-each>-->
<!--								</tbody>-->
<!--							</xsl:if>-->
<!--						</xsl:if>-->
<!--						<xsl:if test="//page/@name = 'fav'">-->
<!--							<xsl:for-each select="$captions[position() &lt; 5]">-->
<!--								<xsl:variable name="param" select="$p/params/param[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()))]"/>-->
<!--								<tr class="tr">-->
<!--									<td><xsl:value-of select="$param/@caption"/></td>-->
<!--									<td><xsl:value-of select="$param"/></td>-->
<!--								</tr>-->
<!--							</xsl:for-each>-->
<!--						</xsl:if>-->
<!--					</table>-->

					<!--					<xsl:value-of select="text" disable-output-escaping="yes"/>-->
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
							<!-- Для обычных товаров (не из каталога price_catalog) -->
							<xsl:if test="not($multipe_prices)">
								<span class="price__value"><xsl:value-of select="f:exchange_cur(., $price_param_name, 0)"/></span>
							</xsl:if>
							<!-- Для товаров из каталога price_catalog -->
							<xsl:if test="$multipe_prices">
								<span class="price__value"><xsl:call-template name="ALL_PRICES_API">
									<xsl:with-param name="need_sum" select="false()"/>
									<xsl:with-param name="product" select="."/>
								</xsl:call-template></span>
							</xsl:if>
						</div>
					</xsl:if>
					<xsl:if test="not($has_price)">
						<div></div>
					</xsl:if>
				</div>
			</div>

			<div class="device__column">
				<!-- device order -->
				<xsl:call-template name="CART_BUTTON_API">
					<xsl:with-param name="p" select="current()"/>
				</xsl:call-template>
			</div>
		</div>
	</xsl:template>




	<!--///////////////////////////////////////////////////////////////////////////////////////////////-->
	<!--/////////////                                                                      ////////////-->
	<!--/////////////         				ТОВАР В ОБЩЕЙ ТАБЛИЦЕ	 			           ////////////-->
	<!--/////////////                                                                      ////////////-->
	<!--///////////////////////////////////////////////////////////////////////////////////////////////-->



	<xsl:template match="*" mode="product-lines">
		<xsl:param name="multiple" select="false()"/>
		<xsl:param name="hidden" select="false()"/>
		<xsl:param name="number" select="-1"/>
		<xsl:param name="position" select="1"/>
		<xsl:param name="query" select="''"/>
		<xsl:param name="has_more" select="false()"/>
		<xsl:variable name="plain_section" select="plain_section"/>
		<xsl:variable name="plain" select="if (section_name and not(section_name = '')) then section_name else plain_section/name"/>

		<xsl:variable name="has_price" select="price and price != '0'"/>
		<xsl:variable name="prms" select="params/param"/>
		<xsl:variable name="has_lines" select="has_lines = '1'"/>

		<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
		<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>

		<xsl:variable name="user_defined_params" select="tokenize($sel_sec/params_list, '[\|;]\s*')"/>
		<xsl:variable name="is_user_defined" select="$sel_sec/params_list and not($sel_sec/params_list = '') and count($user_defined_params) &gt; 0"/>
		<xsl:variable name="captions" select="if ($is_user_defined) then $user_defined_params else params/param/@caption"/>
		<xsl:variable name="p" select="current()"/>
		<tr class="row2 prod_{if ($hidden) then $position else ''}" style="{'display: none'[$hidden]}">
			<xsl:if test="$multiple">
				<td>
					<div class="thn">Запрос</div>
					<div class="thd"><b><xsl:value-of select="$query" /></b></div>
				</td>
			</xsl:if>
			<td>
				<div class="thn">Название</div>
				<div class="thd">
					<a href="{show_product}"><xsl:value-of select="name"/></a>
					<xsl:if test="label"><p/></xsl:if>
					<xsl:for-each select="label">
						<div class="tag device__tag {f:translit(.)}" style="display: inline-block;">
							<xsl:value-of select="." />
						</div>
					</xsl:for-each>
					<p/>
					<xsl:if test="vendor and not(vendor = '')"><xsl:value-of select="vendor" /><p/></xsl:if>
					<xsl:call-template name="FAV_AND_COMPARE">
						<xsl:with-param name="p" select="current()"/>
						<xsl:with-param name="is_inline" select="true()"/>
					</xsl:call-template>
				</div>
			</td><!--название -->
			<xsl:if test="$is_admin">
				<td>
					<div class="thn">Поставщик</div>
					<div class="thd"><xsl:value-of select="$plain"/></div>
				</td>
				<td>
					<div class="thn">Дата прайса</div>
					<div class="thd"><xsl:value-of select="$plain_section/date"/></div>
				</td>
			</xsl:if>
			<td><!--описание -->
				<div class="thn">Описание</div>
				<div class="thd">
					<xsl:value-of select="description" disable-output-escaping="yes"/>
					<xsl:if test="not($plain)">
						<xsl:for-each select="$captions[position() &lt;= $product_params_limit]">
							<xsl:variable name="param" select="$p/params/param[lower-case(normalize-space(@caption)) = lower-case(normalize-space(current()))]"/>
							<xsl:value-of select="normalize-space(current())"/>: <xsl:value-of select="$param"/>;
						</xsl:for-each>
					</xsl:if>
				</div>
			</td>
			<td><!--дата поставки -->
				<div class="thn">Срок поставки</div>
				<div class="thd"><xsl:value-of select="next_delivery"/><!--<xsl:value-of select="available"/>--></div>
			</td>
			<td><!--количество на складе -->
				<div class="thn">Количество</div>
				<div class="thd"><xsl:value-of select="qty"/></div>
			</td>
			<td>
				<div class="thn">Цена</div>
				<div class="thd"><!--цена -->
					<xsl:if test="price_old">
						<div class="price__item_old">
							<span class="price__value"><xsl:value-of select="f:exchange_cur(., $price_old_param_name, 0)"/></span>
						</div>
					</xsl:if>
					<xsl:if test="$has_price">
						<!-- Для обычных товаров (не из каталога price_catalog) -->
						<xsl:if test="not($plain)">
							<xsl:if test="$has_lines" >от </xsl:if><xsl:value-of select="f:exchange_cur(., $price_param_name, 0)"/>
						</xsl:if>
						<!-- Для товаров из каталога price_catalog -->
						<xsl:if test="$plain">
							<xsl:call-template name="ALL_PRICES">
								<xsl:with-param name="need_sum" select="false()"/>
								<xsl:with-param name="price_in_currency" select="f:exchange(current(), 'price', 0)"/>
								<xsl:with-param name="product" select="."/>
								<xsl:with-param name="section_name" select="$plain"/>
							</xsl:call-template>
						</xsl:if>

					</xsl:if>
					<xsl:if test="not($has_price)"> - </xsl:if>
				</div>
			</td>
			<xsl:if test="$is_admin">
				<td>
					<div class="thn">Базовая цена</div>
					<div class="thd"><xsl:value-of select="f:exchange(current(), 'price', 0)" /></div>
				</td>
			</xsl:if>
			<td><!--заказать -->
				<div class="thn">Заказать</div>
				<div class="thd">
				<xsl:call-template name="CART_BUTTON">
					<xsl:with-param name="p" select="current()"/>
					<xsl:with-param name="default_qty" select="$number"/>
				</xsl:call-template>
				</div>
			</td>
			<xsl:if test="$has_one_click or $has_my_price or $has_subscribe"><!--дополнительно -->
				<td>
					<div class="thn">Дополнительно</div>
					<div class="thd">
						<xsl:call-template name="EXTRA_ORDERING_TYPES">
							<xsl:with-param name="p" select="current()"/>
						</xsl:call-template>
					</div>
				</td>
			</xsl:if>
			<xsl:if test="($multiple and not($analogs)) or $multiple_analog_sets"><td><div class="thn">Показать</div>
			<div class="thd"><xsl:if test="not($hidden) and $has_more"><a href="#" popup=".prod_{$position}">Показать другие</a></xsl:if></div></td></xsl:if>
		</tr>
	</xsl:template>




	<!-- /////////////////////// -->
	<!-- ТОВАР В ОБЩЕЙ ТАБЛИЦЕ, но для API -->
	<!-- /////////////////////// -->

	<xsl:template match="*" mode="product-lines-api">
		<xsl:param name="multiple" select="false()"/>
		<xsl:param name="hidden" select="false()"/>
		<xsl:param name="number" select="-1"/>
		<xsl:param name="position" select="1"/>
		<xsl:param name="query" select="''"/>
		<xsl:param name="has_more" select="false()"/>
		<xsl:variable name="multipe_prices" select="prices"/>

		<xsl:variable name="has_price" select="price and price != '0'"/>

		<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
		<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>
		<xsl:variable name="p" select="current()"/>
		<tr class="row2 prod_{if ($hidden) then $position else ''}" style="{'display: none'[$hidden]}">
			<xsl:if test="$multiple">
				<td>
					<div class="thn">Запрос</div>
					<div class="thd"><b><xsl:value-of select="$query" /></b></div>
				</td>
			</xsl:if>
			<td>
				<div class="thn">Название</div>
				<div class="thd">
					<a><xsl:value-of select="name"/></a>
					<p/>
					<xsl:if test="vendor and not(vendor = '')"><xsl:value-of select="vendor" /><p/></xsl:if>
				</div>
			</td><!--название -->
			<xsl:if test="$is_admin">
				<td>
					<div class="thn">Поставщик</div>
					<div class="thd"><xsl:value-of select="$p/category_id"/></div>
				</td>
				<td>
					<div class="thn">Дата прайса</div>
					<div class="thd"><xsl:value-of select="$p/pricedate"/></div>
				</td>
			</xsl:if>
			<td><!--описание -->
				<div class="thn">Описание</div>
				<div class="thd">
					<xsl:value-of select="description" disable-output-escaping="yes"/>
				</div>
			</td>
			<td><!--дата поставки -->
				<div class="thn">Срок поставки</div>
				<div class="thd"><xsl:value-of select="next_delivery"/><!--<xsl:value-of select="available"/>--></div>
			</td>
			<td><!--количество на складе -->
				<div class="thn">Количество</div>
				<div class="thd"><xsl:value-of select="qty"/></div>
			</td>
			<td>
				<div class="thn">Цена</div>
				<div class="thd"><!--цена -->
					<xsl:if test="price_old">
						<div class="price__item_old">
							<span class="price__value"><xsl:value-of select="f:exchange_cur(., $price_old_param_name, 0)"/></span>
						</div>
					</xsl:if>
					<xsl:if test="$has_price">
						<!-- Для обычных товаров (не из каталога price_catalog) -->
						<xsl:if test="not($multipe_prices)">
							<xsl:value-of select="f:exchange_cur(., $price_param_name, 0)"/>
						</xsl:if>
						<!-- Для товаров из каталога price_catalog -->
						<xsl:if test="$multipe_prices">
							<span class="price__value"><xsl:call-template name="ALL_PRICES_API">
								<xsl:with-param name="need_sum" select="false()"/>
								<xsl:with-param name="product" select="$p"/>
							</xsl:call-template></span>
						</xsl:if>

					</xsl:if>
					<xsl:if test="not($has_price)"> - </xsl:if>
				</div>
			</td>
			<xsl:if test="$is_admin">
				<td>
					<div class="thn">Базовая цена</div>
					<div class="thd">
						<xsl:if test="$has_price">
							<!-- Для обычных товаров (не из каталога price_catalog) -->
							<xsl:if test="not($multipe_prices)">
								<xsl:value-of select="price"/>&#160;<xsl:value-of select="currency_id"/>
							</xsl:if>
							<!-- Для товаров из каталога price_catalog -->
							<xsl:if test="$multipe_prices">
								<span class="price__value"><xsl:call-template name="ALL_PRICES_API">
									<xsl:with-param name="need_sum" select="false()"/>
									<xsl:with-param name="need_original" select="true()"/>
									<xsl:with-param name="product" select="$p"/>
								</xsl:call-template></span>
							</xsl:if>

						</xsl:if>
					</div>
				</td>
			</xsl:if>
			<td><!--заказать -->
				<div class="thn">Заказать</div>
				<div class="thd">
					<xsl:call-template name="CART_BUTTON_API">
						<xsl:with-param name="p" select="$p"/>
					</xsl:call-template>
				</div>
			</td>
			<xsl:if test="$has_one_click or $has_my_price or $has_subscribe"><!--дополнительно -->
				<td>
					<div class="thn">Дополнительно</div>
					<div class="thd">
						<xsl:call-template name="EXTRA_ORDERING_TYPES">
							<xsl:with-param name="p" select="current()"/>
						</xsl:call-template>
					</div>
				</td>
			</xsl:if>
			<xsl:if test="($multiple and not($analogs)) or $multiple_analog_sets"><td><div class="thn">Показать</div>
				<div class="thd"><xsl:if test="not($hidden) and $has_more"><a href="#" popup=".prod_{$position}">Показать другие</a></xsl:if></div></td></xsl:if>
		</tr>
	</xsl:template>



	<!--///////////////////////////////////////////////////////////////////////////////////////////////-->
	<!--/////////////                                                                      ////////////-->
	<!--/////////////         			КНОПКИ ЗАКАЗАТЬ И ДОПОЛНИТЕЛЬНЫЕ	 		       ////////////-->
	<!--/////////////                                                                      ////////////-->
	<!--///////////////////////////////////////////////////////////////////////////////////////////////-->



	<xsl:template name="CART_BUTTON">
		<xsl:param name="p" />
		<xsl:param name="default_qty" select="-1"/>
		<xsl:if test="$has_cart">
			<xsl:variable name="has_lines" select="$p/has_lines = '1'"/>
			<xsl:variable name="has_price" select="f:num($p/price) != 0"/>

			<!-- device order -->
			<xsl:if test="not($has_lines)">
				<div class="order device-order cart_list_{$p/@id}" id="cart_list_{$p/@id}">
					<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{$p/@id}">
						<input type="number"
							   class="input input_type_number" name="qty"
							   value="{if ($default_qty &gt; 0) then $default_qty else if ($p/min_qty) then min_qty else 1}"
							   min="{if ($p/min_qty) then $p/min_qty else 1}"
							   step="{if ($p/step) then f:num($p/step) else $step_default}" />

						<xsl:if test="$has_price">
							<button class="button" type="submit"><xsl:value-of select="$to_cart_available_label"/></button>
						</xsl:if>
						<xsl:if test="not($has_price)">
							<button class="button button_request" type="submit"><xsl:value-of select="$to_cart_na_label"/></button>
						</xsl:if>
					</form>
				</div>
			</xsl:if>
			<xsl:if test="$has_lines">
				<div class="order device-order">
					<a class="button" href="{$p/show_product}">Подробнее</a>
				</div>
			</xsl:if>
		</xsl:if>
	</xsl:template>



	<xsl:template name="CART_BUTTON_API">
		<xsl:param name="p" />
		<xsl:param name="default_qty" select="-1"/>
		<xsl:if test="$has_cart">
			<xsl:variable name="has_price" select="f:num($p/price) != 0"/>

			<!-- device order -->
			<div class="order device-order cart_list_{$p/code}" id="cart_list_{$p/code}">
				<form action="{$to_cart_api_link}" method="post" ajax="true" ajax-loader-id="cart_list_{$p/@id}">
					<input type="hidden" name="prod" value="-10"/>
					<textarea name="outer" style="display: none"><xsl:copy-of select="$p"/></textarea>
					<input type="number"
						   class="input input_type_number" name="qty"
						   value="{if ($default_qty &gt; 0) then $default_qty else if ($p/min_qty) then f:num($p/min_qty) else 1}"
						   min="{if ($p/min_qty) then f:num($p/min_qty) else 1}"
						   step="{if ($p/step) then f:num($p/step) else $step_default}" />

					<xsl:if test="$has_price">
						<button class="button" type="submit"><xsl:value-of select="$to_cart_available_label"/></button>
					</xsl:if>
					<xsl:if test="not($has_price)">
						<button class="button button_request" type="submit"><xsl:value-of select="$to_cart_na_label"/></button>
					</xsl:if>
				</form>
			</div>
		</xsl:if>
	</xsl:template>



	<xsl:template name="EXTRA_ORDERING_TYPES">
		<xsl:param name="p" />

		<div class="text_sm" style="margin-top: auto;">
			<a href="#" onclick="showDetails('{show_lines_ajax}'); return false;" >Склады</a>
		</div>

		<!-- one click -->
		<xsl:if test="$has_one_click">
			<div class="text_sm" style="margin-top: auto;">
				<a href="{$p/one_click_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-one_click">Купить в 1 клик</a>
			</div>
		</xsl:if>

		<!-- subscribe for update -->
		<xsl:if test="$has_subscribe">
			<div class="text_sm" style="margin-top: auto;">
				<a href="{$p/subscribe_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-subscribe">Сообщить о появлении</a>
			</div>
		</xsl:if>

		<!-- propose my price -->
		<xsl:if test="$has_my_price">
			<div class="text_sm" style="margin-top: auto;">
				<a href="{$p/my_price_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-my_price"><xsl:value-of select="$mp_link"/></a>
			</div>
		</xsl:if>
	</xsl:template>




	<xsl:template name="FAV_AND_COMPARE">
		<xsl:param name="p" />
		<xsl:param name="is_inline" select="false()"/>
		<xsl:if test="$has_fav">
			<xsl:choose>
				<xsl:when test="$is_fav">
					<a href="{$p/from_fav}" class="add__item icon-link">
						<div class="icon"><img src="img/icon-star.svg" alt="" /></div>
						<span><xsl:value-of select="$compare_remove_label"/></span>
					</a>
				</xsl:when>
				<xsl:otherwise>
					<div id="fav_list_{@id}" style="{'display: inline-block;'[$is_inline]}">
						<a href="{$p/to_fav}" class="add__item icon-link" ajax="true" ajax-loader-id="fav_list_{$p/@id}">
							<div class="icon"><img src="img/icon-star.svg" alt="" /></div>
							<span><xsl:value-of select="$compare_add_label"/></span>
						</a>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
		<xsl:if test="$has_compare">
			<xsl:if test="not($is_compare)">
				<div id="compare_list_{$p/@id}" style="{'display: inline-block;'[$is_inline]}">
					<a href="{$p/to_compare}" class="add__item icon-link" ajax="true" ajax-loader-id="compare_list_{$p/@id}">
						<div class="icon"><img src="img/icon-balance.svg" alt="" /></div>
						<span><xsl:value-of select="$go_to_compare_label"/></span>
					</a>
				</div>
			</xsl:if>
			<xsl:if test="$is_compare">
				<a href="{$p/from_compare}" class="add__item icon-link">
					<div class="icon"><img src="img/icon-balance.svg" alt="" /></div>
					<span><xsl:value-of select="$compare_remove_label"/></span>
				</a>
			</xsl:if>
		</xsl:if>
	</xsl:template>



	<!--///////////////////////////////////////////////////////////////////////////////////////////////-->
	<!--/////////////                                                                      ////////////-->
	<!--/////////////         			ТАБЛИЦА ДЛЯ ТАБЛИЧНОГО ВИДА			 		       ////////////-->
	<!--/////////////                                                                      ////////////-->
	<!--///////////////////////////////////////////////////////////////////////////////////////////////-->



	<xsl:template name="LINES_TABLE">
		<xsl:param name="products" select="emptynode"/>
		<xsl:param name="results_api" select="emptynode"/>
		<xsl:param name="multiple" select="false()"/>
		<xsl:param name="queries" select="queries"/>
		<xsl:param name="numbers" select="numbers"/>
		<xsl:variable name="colspan" select="8 + (if ($has_one_click or $has_my_price or $has_subscribe) then 1 else 0)"/>
		<div class="view-table">
			<table>
				<thead>
					<tr>
						<xsl:if test="$multiple"><th><xsl:value-of select="if ($multiple_analog_sets) then 'Запрос' else 'Аналоги'" /></th></xsl:if>
						<th>Название</th>
						<xsl:if test="$is_admin">
							<th>Поставщик</th>
							<th>Дата прайса</th>
						</xsl:if>
						<th>Описание</th>
						<th>Срок поставки</th>
						<th>Количество</th>
						<th>Цена</th>
						<xsl:if test="$is_admin"><th>Базовая цена</th></xsl:if>
						<th>Заказать</th>
						<xsl:if test="$has_one_click or $has_my_price or $has_subscribe"><th>Дополнительно</th></xsl:if>
						<xsl:if test="($multiple and not($analogs)) or $multiple_analog_sets"><th>Показать</th></xsl:if>
					</tr>
				</thead>
				<tbody>
					<xsl:if test="$multiple">
						<xsl:for-each select="$queries">
							<xsl:variable name="q" select="."/>
							<xsl:variable name="nn" select="$numbers[starts-with(., concat($q, ':'))][1]"/>
							<xsl:variable name="n" select="f:num(tokenize($nn, ':')[last()])"/>
							<xsl:variable name="p" select="position()"/>
							<xsl:variable name="price_query_products" select="$products[item_own_extras/query = $q]"/>
							<xsl:variable name="more_than_one" select="count($price_query_products) &gt; 1"/>
							<xsl:variable name="prev_q" select="$queries[$p - 1]"/>
							<xsl:if test="$analogs/set/analog = $q and $analogs/set/base = $prev_q">
								<tr>
									<td colspan="{$colspan + 1}">
										<div class="thd"><b>Аналоги <xsl:value-of select="$prev_q" /></b></div>
									</td>
								</tr>
							</xsl:if>
							<xsl:apply-templates select="$price_query_products[1]" mode="product-lines">
								<xsl:with-param name="multiple" select="true()"/>
								<xsl:with-param name="query" select="$q"/>
								<xsl:with-param name="number" select="$n"/>
								<xsl:with-param name="position" select="$p"/>
								<xsl:with-param name="has_more" select="$more_than_one"/>
							</xsl:apply-templates>
							<xsl:apply-templates select="$price_query_products[position() &gt; 1]" mode="product-lines">
								<xsl:with-param name="multiple" select="true()"/>
								<xsl:with-param name="query" select="$q"/>
								<xsl:with-param name="hidden" select="'hidden'"/>
								<xsl:with-param name="number" select="$n"/>
								<xsl:with-param name="position" select="$p"/>
							</xsl:apply-templates>
							<xsl:variable name="query_results_api" select="$results_api[query = $q]"/>
							<xsl:variable name="more_than_one_api" select="count($query_results_api/product) &gt; 1"/>
							<xsl:apply-templates select="$query_results_api/product[1]" mode="product-lines-api">
								<xsl:with-param name="multiple" select="true()"/>
								<xsl:with-param name="query" select="$q"/>
								<xsl:with-param name="number" select="$n"/>
								<xsl:with-param name="position" select="$p + 1000"/>
								<xsl:with-param name="has_more" select="$more_than_one_api"/>
							</xsl:apply-templates>
							<xsl:apply-templates select="$query_results_api/product[position() &gt; 1]" mode="product-lines-api">
								<xsl:with-param name="multiple" select="true()"/>
								<xsl:with-param name="query" select="$q"/>
								<xsl:with-param name="hidden" select="'hidden'"/>
								<xsl:with-param name="number" select="$n"/>
								<xsl:with-param name="position" select="$p + 1000"/>
							</xsl:apply-templates>
							<xsl:if test="not($price_query_products) and not($query_results_api)">
								<tr>
									<td>
										<div class="thn">Запрос</div>
										<div class="thd"><b><xsl:value-of select="." /></b></div>
									</td>
									<td colspan="{$colspan}">
										<div class="thn">Варианты</div>
										<div class="thd"><b>По запросу товары не найдены</b></div>
									</td>
								</tr>
							</xsl:if>
						</xsl:for-each>
					</xsl:if>
					<xsl:if test="not($multiple)">
						<xsl:apply-templates select="$products" mode="product-lines"/>
						<xsl:apply-templates select="$results_api/product" mode="product-lines-api"/>
					</xsl:if>
				</tbody>
			</table>
		</div>
	</xsl:template>

</xsl:stylesheet>