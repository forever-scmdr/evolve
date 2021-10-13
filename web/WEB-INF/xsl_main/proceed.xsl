<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f" version="2.0">
	<xsl:import href="user_data_inputs.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="title" select="'Оформление заказа'"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="cart" select="page/cart"/>

	<xsl:variable name="message" select="$cart/item_own_extras/user_message | $user/item_own_extras/user_message"/>
	<xsl:variable name="success" select="page/variables/success = ('true', 'yes')"/>
	<xsl:variable name="is_jur" select="$user/@type = 'user_jur'"/>
	<xsl:variable name="is_phys" select="not($is_jur)"/>
	<xsl:variable name="is_register" select="$pv/register = 'register'"/>



	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a href="{page/cart_link}" class="path__link">Корзина заказов</a>
				<div class="path__arrow"></div>
				<a href="{page/cart_print_link}" class="path__link">Печать корзины</a>
				<div class="path__arrow"></div>
			</div>
		</div>
	</xsl:template>


	<xsl:template name="CONTENT_INNER">
		<xsl:call-template name="MESSAGE"/>
		<div class="tabs">
			<xsl:if test="not($is_user_registered)">
				<div class="tabs__nav">
					<a class="tab{' tab_active'[$is_phys]}" href="#tab_phys">
						<div class="tab__text">Физическое лицо</div>
					</a>
					<a class="tab{' tab_active'[$is_jur]}" href="#tab_jur">
						<div class="tab__text">Юридическое лицо или ИП</div>
					</a>
				</div>
			</xsl:if>
			<div class="tabs__content">
				<div class="tab-container" id="tab_phys" style="{'display: none;'[$is_jur]}">
					<xsl:variable name="inp" select="page/user_phys/input"/>
					<form class="form" action="{page/confirm_phys_link}" method="post" onsubmit="lock('tab_phys');$(this).find('.input:hidden').val('');$(this).find('select').val('');">
						<input type="hidden" name="{$inp/pseudo/@input}" value="pseudo"/>
						<xsl:variable name="u" select="page/user[@type='user_phys']"/>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Фамилия: <span>*</span></div>
							</div>
							<input class="input" type="text"
								   name="{$inp/second_name/@input}" value="{f:not_empty($inp/second_name, $u/second_name)}" error="{$inp/second_name/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Имя: <span>*</span></div>
							</div>
							<input class="input" type="text"
								   name="{$inp/name/@input}" value="{f:not_empty($inp/name, $u/name)}" error="{$inp/name/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Отчество:</div>
							</div>
							<input class="input" type="text"
								   name="{$inp/middle_name/@input}" value="{f:not_empty($inp/middle_name, $u/middle_name)}" error="{$inp/middle_name/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Телефон (формат +375 29 1234567): <span>*</span></div>
							</div>
							<input class="input" type="text"
								   name="{$inp/phone/@input}" value="{f:not_empty($inp/phone, $u/phone)}" error="{$inp/phone/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>
									Email: <span>*</span>
									<span style="padding-left: 5px; color: silver; font-size:11px;">
										будет использоваться в качестве логина при последующем входе
									</span>
								</div>
							</div>
							<input class="input" type="text"
								   name="{$inp/email/@input}" value="{f:not_empty($inp/email, $u/email)}" error="{$inp/email/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Способ получения заказа: <span>*</span></div>
							</div>
							<ul style="margin-top: 8px;" id="delivery">
								<xsl:apply-templates select="page/delivery"/>
							</ul>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Способ олаты: <span>*</span></div>
							</div>
							<ul style="margin-top: 8px;">
								<xsl:apply-templates select="page/payment"/>
							</ul>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>В случае отсутствия некоторых позиций: <span>*</span></div>
							</div>
							<ul style="margin-top: 8px;">
								<xsl:apply-templates select="page/absent"/>
							</ul>
						</div>

						<div id="postAddressPhys" class="delivery-hideable" style="margin-top: 20px; display: none;">
							<div class="form__item form-item">
								<div class="form-item__label">
									<div>Индекс: <span>*</span><a href="http://ex.belpost.by/addressbook/" target="_blank">Узнать свой индекс</a></div>

								</div>
								<input class="input" type="text"
									   name="{$inp/post_index/@input}" value="{f:not_empty($inp/post_index, $u/post_index)}" error="{$inp/post_index/@validation-error}"/>
							</div>
							<div class="form__item form-item">
								<div class="form-item__label">
									<div>Страна: <span>*</span></div>
								</div>
								<select name="{$inp/post_country/@input}" value="{f:not_empty(f:not_empty($inp/post_country, $u/post_country), 'Беларусь')}">
									<xsl:for-each-group select="page/delivery/country" group-by=".">
										<option value="{current-group()[1]}"><xsl:value-of select="current-group()[1]"/></option>
									</xsl:for-each-group>
								</select>
							</div>
							<div class="form__item form-item">
								<div class="form-item__label">
									<div>Область: <span>*</span></div>
								</div>
								<input class="input" type="text"
									   name="{$inp/post_region/@input}" value="{f:not_empty($inp/post_region, $u/post_region)}" error="{$inp/post_region/@validation-error}"/>
							</div>
							<div class="form__item form-item">
								<div class="form-item__label">
									<div>Населенный пункт (город, деревня): <span>*</span></div>
								</div>
								<input class="input" type="text"
									   name="{$inp/post_city/@input}" value="{f:not_empty($inp/post_city, $u/post_city)}" error="{$inp/post_city/@validation-error}"/>
							</div>
							<div class="form__item form-item">
								<div class="form-item__label">
									<div>Адрес (улица, дом, квартира): <span>*</span></div>
								</div>
								<input class="input" type="text"
									   name="{$inp/post_address/@input}" value="{f:not_empty($inp/post_address, $u/post_address)}" error="{$inp/post_address/@validation-error}"/>
							</div>
						</div>

						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Дополнительная информация:</div>
							</div>
							<textarea class="input" name="{$inp/comment/@input}">
								<xsl:value-of select="$u/comment"/>
							</textarea>
						</div>

						<div id="passwordPhys" class="password_hidden" style="margin-top: 30px;{' display: none;'[not($is_register) or $success]}">
							<div class="form__item form-item">
								<div class="form-item__label">
									<div>Пароль: <span>*</span>
									</div>
								</div>
								<input class="input" type="password"
									   name="{$inp/password/@input}" error="{$inp/password/@validation-error}"/>
							</div>
							<div class="form__item form-item">
								<div class="form-item__label">
									<div>Пароль еще раз: <span>*</span>
									</div>
								</div>
								<input class="input" type="password"
									   name="{$inp/p1/@input}" error="{$inp/p1/@validation-error}"/>
							</div>
						</div>
						<xsl:if test="not($is_user_registered)">
							<xsl:if test="not($is_register)">
								<button class="button button_big button_secondary register_show" type="submit" style="margin-right: 10px;">Запомнить данные</button>
							</xsl:if>
							<button class="button button_big register_submit" type="submit" style="margin-right: 10px;{' display: none;'[not($is_register)]}">Запомнить данные</button>
						</xsl:if>
						<button class="button button_big" type="submit">Отправить заказ</button>
					</form>
				</div>
				<div class="tab-container" id="tab_jur" style="{'display: none;'[$is_phys]}">
					<xsl:variable name="inp" select="page/user_jur/input"/>
					<xsl:variable name="u" select="if (not($inp/organization = '')) then $inp else page/user[@type='user_jur']"/>
					<form class="form" action="{page/confirm_jut_link}" method="post" onsubmit="lock('tab_jur')">
						<input type="hidden" name="{$inp/pseudo/@input}" value="pseudo"/>
						<xsl:call-template name="USER_JUR_INPUTS">
							<xsl:with-param name="inp" select="$inp"/>
							<xsl:with-param name="u" select="$u"/>
							<xsl:with-param name="is_proceed" select="true()"/>
						</xsl:call-template>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Дополнительная информация:</div>
							</div>
							<textarea class="input" name="{$inp/comment/@input}">
								<xsl:value-of select="$u/comment"/>
							</textarea>
						</div>
						<div id="passwordJur" class="password_hidden" style="margin-top: 30px;{' display: none;'[not($is_register) or $success]}">
							<div class="form__item form-item">
								<div class="form-item__label">
									<div>Пароль: <span>*</span>
									</div>
								</div>
								<input class="input" type="password"
									   name="{$inp/password/@input}" error="{$inp/password/@validation-error}"/>
							</div>
							<div class="form__item form-item">
								<div class="form-item__label">
									<div>Пароль еще раз: <span>*</span>
									</div>
								</div>
								<input class="input" type="password"
									   name="{$inp/p1/@input}" error="{$inp/p1/@validation-error}"/>
							</div>
						</div>
						<xsl:if test="not($is_user_registered)">
							<xsl:if test="not($is_register)">
								<button class="button button_big button_secondary register_show" type="submit" style="margin-right: 10px;">Запомнить данные</button>
							</xsl:if>
							<button class="button button_big register_submit" type="submit" style="margin-right: 10px;{' display: none;'[not($is_register)]}">Запомнить данные</button>
						</xsl:if>
						<button class="button button_big" type="submit">Отправить заказ</button>
					</form>
				</div>
			</div>
		</div>
	</xsl:template>


	<xsl:variable name="inp" select="page/user_phys/input"/>
	<xsl:variable name="u" select="page/user[@type='user_phys']"/>

	<xsl:template match="delivery">
		<xsl:variable name="show" select="concat('#', string-join((payment/@id, absent/@id),', #'), if(f:num(ask_address) = 1) then ', #postAddressPhys' else '')" />
		<li>
			<label data-show="{$show}" data-country="{string-join(country,',')}">
				<xsl:call-template name="check_radio">
					<xsl:with-param name="value" select="@id"/>
					<xsl:with-param name="check" select="if ($inp) then $inp/get_order_from else $u/get_order_from"/>
					<xsl:with-param name="name" select="$inp/get_order_from/@input"/>
				</xsl:call-template>&#160;
				<xsl:value-of select="name"/>
			</label>
		</li>
	</xsl:template>


	<xsl:template match="payment">
		<li id="{@id}" class="delivery-hideable">
			<label>
				<xsl:call-template name="check_radio">
					<xsl:with-param name="value" select="@id"/>
					<xsl:with-param name="name" select="$inp/payment/@input"/>
					<xsl:with-param name="check" select="if ($inp) then $inp/payment else $u/payment"/>
				</xsl:call-template>&#160;
				<xsl:value-of select="name" />
			</label>
		</li>
	</xsl:template>


	<xsl:template match="absent">
		<li id="{@id}" class="delivery-hideable">
			<label>
				<xsl:call-template name="check_radio">
					<xsl:with-param name="value" select="name"/>
					<xsl:with-param name="name" select="$inp/if_absent/@input"/>
					<xsl:with-param name="check" select="$inp/if_absent"/>
				</xsl:call-template>&#160;
				<xsl:value-of select="name" />
			</label>
		</li>
	</xsl:template>



	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="TAB_SCRIPT"/>
		<xsl:call-template name="USER_DATA_SCRIPT"/>
		<script type="text/javascript" src="js/proceed.js"></script>
		<script type="text/javascript">
			$(document).ready(function() {
				$('.register_show').click(function(e) {
				    e.preventDefault();
					$(this).closest('form').find('.register_submit').show();
					$(this).closest('form').find('.password_hidden').show();
					$(this).hide();
				});
				$('.register_submit').click(function() {
				    $(this).closest('form').attr('action', '<xsl:value-of select="page/register_submit_link" disable-output-escaping="yes"/>');
				});
			});
		</script>
	</xsl:template>

</xsl:stylesheet>