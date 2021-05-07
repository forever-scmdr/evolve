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


	<xsl:template match="accessory | set | probe | product | assoc | hit | new">
		<xsl:variable name="zero" select="not(is_service = '1') and f:num(qty) &lt; 0.001"/>
		<xsl:variable name="has_price" select="price and price != '0'"/>
		<xsl:variable name="prms" select="params/param"/>
		<xsl:variable name="has_lines" select="has_lines = '1'"/>

		<div class="device">
			<div class="device__image">
				<img src="sitepics/{pic_path}.jpg" alt="{name}" onerror="$('#fancy-{code}').remove(); this.src = 'images/no-photo.jpg'" />
				<div class="device__hover">
					<a class="device__zoom" href="sitepics/{pic_path}b.jpg" id="fancy-{code}"
					   title="Фотографии товаров являются наглядными примерами и могут отличаться от реального вида товара. Это не влияет на технические характеристики.">
						<img src="img/icon-device-zoom.png" alt="" />
					</a>
					<a class="device__parent-link" href="{show_product}">
						<img src="img/icon-device-goto.png" alt=""/>
					</a>
				</div>
			</div>
			<div class="device__name">
				<a href="{show_product}"><xsl:value-of select="string-join((name, name_extra), ' ')"/></a>
				<div><xsl:value-of select="vendor"/></div>
			</div>
			<div class="device__icons">
				<xsl:if test="param[not(@caption = 'Сертификат')]">
					<a class="deivce-icon" href="#" rel="info_{code}">
						<img src="img/icon-device-icon-01.png" alt=""/>
					</a>
				</xsl:if>
				<xsl:for-each select="file[. != '']">
					<a class="deivce-icon" href="sitedocs/{.}" title="скачать документацию (datasheet) по {../mark} в формате pdf" download="sitedocs/{.}">
						<img src="img/icon-device-icon-02.png" alt=""/>
					</a>
				</xsl:for-each>
				<xsl:if test="name_extra != ''">
					<a class="deivce-icon" href="#" rel="text_{code}">
						<img src="img/icon-device-icon-03.png" alt=""  title="описание"/>
					</a>
				</xsl:if>
				<xsl:if test="analog_code != ''">
					<a class="deivce-icon" href="#" link="{analog_ajax_link}" rel="analog_{code}" title="аналоги">
						<img src="img/icon-device-icon-04.png" alt=""/>
					</a>
				</xsl:if>
			</div>
			<div class="device__code">
				Код: <xsl:value-of select="code"/>
				<xsl:if test="special_price = 'true' and not($zero)"><span>Спеццена</span></xsl:if>
			</div>
			<div class="device__price">
				<xsl:if test="not($zero)">
					<xsl:value-of select="price"/> руб./<xsl:value-of select="unit"/>
				</xsl:if>
				<xsl:if test="$zero">
					<a href="{subscribe_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-subscribe">Уведомить о поступлении</a>
				</xsl:if>
			</div>
			<div class="device__status status">
				<xsl:if test="$zero">
					<xsl:if test="soon != '0'">
						<div class="status__wait">Ожидается: <xsl:value-of select="substring(soon, 1, 10)"/></div>
					</xsl:if>
					<xsl:if test="not(soon != '0')">
						<div class="status__na">Нет в наличии</div>
					</xsl:if>
				</xsl:if>
				<xsl:if test="f:num(sec/discount_1) &gt; 0">
					<div class="sale">от <xsl:value-of select="sec/limit_1"/>&#160;<xsl:value-of select="unit"/> -
						<xsl:value-of select="sec/discount_1"/>%<xsl:call-template name="BR"/>
						от <xsl:value-of select="sec/limit_2"/>&#160;<xsl:value-of select="unit"/> -
						<xsl:value-of select="sec/discount_2"/>%
					</div>
				</xsl:if>
				<xsl:if test="not($zero) and not(is_service = '1')">
					В наличии: <strong><xsl:value-of select="concat(qty, ' ', unit)"/></strong>
				</xsl:if>
			</div>
			<div class="device__order" id="cart_list_{@id}">
				<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
					<xsl:if test="$has_price">
						<input type="number" class="input input_type_number" name="qty"
							   value="{if (min_qty) then min_qty else 1}" min="{if (min_qty) then min_qty else 0}" step="{if (step) then f:num(step) else 0.1}" />
						<button class="button" type="submit"><xsl:value-of select="$to_cart_available_label"/></button>
					</xsl:if>
					<xsl:if test="not($has_price)">
						<input type="hidden" class="input input_type_number" name="qty"
							   value="{if (min_qty) then min_qty else 1}" min="{if (min_qty) then min_qty else 0}" step="{if (step) then f:num(step) else 0.1}" />
						<button class="button button_secondary" type="submit"><xsl:value-of select="$to_cart_na_label"/></button>
					</xsl:if>
				</form>
			</div>
			<div class="device__add">
				<xsl:choose>
					<xsl:when test="$is_fav">
						<div>
							<a href="{from_fav}"><xsl:value-of select="$fav_remove_label"/></a>
						</div>
					</xsl:when>
					<xsl:otherwise>
						<div id="fav_list_{@id}">
							<a href="{to_fav}" ajax="true" ajax-loader-id="fav_list_{@id}"><xsl:value-of select="$fav_add_label"/></a>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</div>
	</xsl:template>


	<xsl:template match="accessory | set | probe | product | assoc" mode="lines">
		<xsl:variable name="zero" select="not(is_service = '1') and f:num(qty) &lt; 0.001"/>
		<xsl:variable name="has_price" select="price and price != '0'"/>
		<xsl:variable name="prms" select="params/param"/>
		<xsl:variable name="has_lines" select="has_lines = '1'"/>


		<tr class="device_row">
			<td class="device__image">
				<img src="sitepics/{pic_path}.jpg" alt="{name}" onerror="$('#fancy-{code}').remove(); this.src = 'images/no-photo.jpg'"/>
				<div class="device__hover">
					<a class="device__zoom" href="sitepics/{pic_path}b.jpg" id="fancy-{code}"
					   title="Фотографии товаров являются наглядными примерами и могут отличаться от реального вида товара. Это не влияет на технические характеристики.">
						<img src="img/icon-device-zoom.png" alt=""/>
					</a>
					<a class="device__parent-link" href="{show_product}">
						<img src="img/icon-device-goto.png" alt=""/>
					</a>
				</div>
			</td>
			<td class="device__info">
				<div class="device__name">
					<a href="{show_product}"><xsl:value-of select="string-join((name, name_extra), ' ')"/></a>
					<div><xsl:value-of select="vendor"/></div>
				</div>
				<div class="device__code">Код: <xsl:value-of select="code"/></div>
				<div class="device__icons">
					<xsl:if test="param[not(@caption = 'Сертификат')]">
						<a class="deivce-icon" href="#" rel="info_{code}">
							<img src="img/icon-device-icon-01.png" alt=""/>
						</a>
					</xsl:if>
					<xsl:for-each select="file[. != '']">
						<a class="deivce-icon" href="sitedocs/{.}" title="скачать документацию (datasheet) по {../mark} в формате pdf" download="sitedocs/{.}">
							<img src="img/icon-device-icon-02.png" alt=""/>
						</a>
					</xsl:for-each>
					<xsl:if test="name_extra != ''">
						<a class="deivce-icon" href="#" rel="text_{code}">
							<img src="img/icon-device-icon-03.png" alt=""  title="описание"/>
						</a>
					</xsl:if>
					<xsl:if test="analog_code != ''">
						<a class="deivce-icon" href="#" link="{analog_ajax_link}" rel="analog_{code}" title="аналоги">
							<img src="img/icon-device-icon-04.png" alt=""/>
						</a>
					</xsl:if>
				</div>
			</td>
			<td class="device__param">4x15</td>
			<td class="device__param">4x15</td>
			<td class="device__param">4x15</td>
			<td class="device__status status">
				<xsl:if test="$zero">
					<xsl:if test="not(soon != '0')">
						<div class="status__na">Нет в наличии</div>
					</xsl:if>
					<xsl:if test="soon != '0'">
						<div class="status__wait">Ожидается: <xsl:value-of select="substring(soon, 1, 10)"/></div>
					</xsl:if>
					<a href="{subscribe_link}" rel="nofollow" ajax="true" data-toggle="modal" data-target="#modal-subscribe">Уведомить о поступлении</a>
				</xsl:if>
				<xsl:if test="not($zero) and not(is_service = '1')">
					<strong class="qty"><xsl:value-of select="concat(qty, ' ', unit)"/></strong>
				</xsl:if>
			</td>
			<td class="device__price">
				<xsl:if test="not($zero)">
					<xsl:value-of select="price"/> руб./<xsl:value-of select="unit"/>
				</xsl:if>
				<xsl:if test="$zero">-</xsl:if>
				<xsl:if test="special_price = 'true' and not($zero)"><div>Спеццена</div></xsl:if>
				<xsl:if test="f:num(sec/discount_1) &gt; 0">
					<div class="sale">от <xsl:value-of select="sec/limit_1"/>&#160;<xsl:value-of select="unit"/> -
						<xsl:value-of select="sec/discount_1"/>%<xsl:call-template name="BR"/>
						от <xsl:value-of select="sec/limit_2"/>&#160;<xsl:value-of select="unit"/> -
						<xsl:value-of select="sec/discount_2"/>%
					</div>
				</xsl:if>
			</td>
			<td>
				<div class="device__order" id="cart_list_{@id}">
					<form action="{to_cart}" method="post" ajax="true" ajax-loader-id="cart_list_{@id}">
						<xsl:if test="$has_price">
							<input type="number" class="input" name="qty"
								   value="{if (min_qty) then min_qty else 1}" min="{if (min_qty) then min_qty else 0}" step="{if (step) then f:num(step) else 0.1}" />
							<a class="button" href="#" onclick="$(this).closest('form').submit(); return false;">
								<img src="img/icon-button-cart.png" alt=""/>
							</a>
						</xsl:if>
						<xsl:if test="not($has_price)">
							<input type="hidden" class="input" name="qty"
								   value="{if (min_qty) then min_qty else 1}" min="{if (min_qty) then min_qty else 0}" step="{if (step) then f:num(step) else 0.1}" />
							<button class="button button_secondary" type="submit"><xsl:value-of select="$to_cart_na_label"/></button>
						</xsl:if>
					</form>
				</div>
				<div class="device__add">
					<xsl:choose>
						<xsl:when test="$is_fav">
							<div>
								<a href="{from_fav}"><xsl:value-of select="$fav_remove_label"/></a>
							</div>
						</xsl:when>
						<xsl:otherwise>
							<div id="fav_list_{@id}">
								<a href="{to_fav}" ajax="true" ajax-loader-id="fav_list_{@id}"><xsl:value-of select="$fav_add_label"/></a>
							</div>
						</xsl:otherwise>
					</xsl:choose>
				</div>
			</td>
		</tr>
	</xsl:template>

</xsl:stylesheet>