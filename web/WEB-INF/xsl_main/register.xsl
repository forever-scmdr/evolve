<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="user_data_inputs.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="title" select="'Регистрация'"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="user" select="page/user"/>

	<xsl:variable name="message" select="$user/item_own_extras/user_message"/>
	<xsl:variable name="success" select="page/variables/success = ('true', 'yes')"/>
	<xsl:variable name="is_jur" select="$user/@type = 'user_jur'"/>
	<xsl:variable name="is_phys" select="not($is_jur)"/>




	<xsl:template name="PAGE_PATH"></xsl:template>


	<xsl:template name="CONTENT_INNER">
		<xsl:if test="$message and not($success)">
			<div class="alert alert-danger" style="background: #ffffcc; border: 1px solid #bb8; color: #bb0000;">
				<p><xsl:value-of select="$message"/></p>
			</div>
		</xsl:if>
		<xsl:if test="$message and $success">
			<div class="alert alert-success" style="background: #eeffee; border: 1px solid #9c9; color: #007700;">
				<p><xsl:value-of select="$message"/></p>
			</div>
		</xsl:if>
		<div class="tabs">
			<div class="tabs__nav">
				<a class="tab{' tab_active'[$is_phys]}" href="#tab_phys">
					<div class="tab__text">Физическое лицо</div>
				</a>
				<a class="tab{' tab_active'[$is_jur]}" href="#tab_jur">
					<div class="tab__text">Юридическое лицо или ИП</div>
				</a>
			</div>
			<div class="tabs__content">
				<div class="tab-container" id="tab_phys" style="{'display: none;'[$is_jur]}">
					<xsl:variable name="inp" select="page/user_phys/input"/>
					<xsl:variable name="u" select="page/user[@type='user_phys']"/>
					<form class="form" action="{page/confirm_link}" method="post" onsubmit="lock('tab_phys')">
						<input type="hidden" name="{$inp/pseudo/@input}" value="pseudo"/>
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
								<div>Телефон (формат +375 29 1234567): <span>*</span></div>
							</div>
							<input class="input" type="text"
								   name="{$inp/phone/@input}" value="{f:not_empty($inp/phone, $u/phone)}" error="{$inp/phone/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Email: <span>*</span></div>
							</div>
							<input class="input" type="text"
								   name="{$inp/email/@input}" value="{f:not_empty($inp/email, $u/email)}" error="{$inp/email/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Индекс:</div>
							</div>
							<input class="input" type="text"
								   name="{$inp/post_index/@input}" value="{f:not_empty($inp/post_index, $u/post_index)}" error="{$inp/post_index/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Область:</div>
							</div>
							<input class="input" type="text"
								   name="{$inp/post_region/@input}" value="{f:not_empty($inp/post_region, $u/post_region)}" error="{$inp/post_region/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Населенный пункт (город, деревня):</div>
							</div>
							<input class="input" type="text"
								   name="{$inp/post_city/@input}" value="{f:not_empty($inp/post_city, $u/post_city)}" error="{$inp/post_city/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Адрес (улица, дом, квартира):</div>
							</div>
							<input class="input" type="text"
								   name="{$inp/post_address/@input}" value="{f:not_empty($inp/post_address, $u/post_address)}" error="{$inp/post_address/@validation-error}"/>
						</div>
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
						<button class="button button_big" type="submit">Зарегистрироваться</button>
					</form>
				</div>
				<div class="tab-container" id="tab_jur" style="{'display: none;'[$is_phys]}">
					<xsl:variable name="inp" select="page/user_jur/input"/>
					<xsl:variable name="u" select="page/user[@type='user_jur']"/>
					<form class="form" action="{page/confirm_link}" method="post" onsubmit="lock('tab_jur')">
						<input type="hidden" name="{$inp/pseudo/@input}" value="pseudo"/>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Наименование организации: <span>*</span>
								</div>
							</div>
							<input class="input" type="text"
								   name="{$inp/organization/@input}" value="{f:not_empty($inp/organization, $u/organization)}" error="{$inp/organization/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>УНП: <span>*</span>
								</div>
							</div>
							<input class="input" type="text"
								   name="{$inp/unp/@input}" value="{f:not_empty($inp/unp, $u/unp)}" error="{$inp/unp/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Телефон/факс: <span>*</span>
								</div>
							</div>
							<input class="input" type="text"
								   name="{$inp/phone/@input}" value="{f:not_empty($inp/phone, $u/phone)}" error="{$inp/phone/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Электронный адрес: <span>*</span>
								</div>
							</div>
							<input class="input" type="text"
								   name="{$inp/email/@input}" value="{f:not_empty($inp/email, $u/email)}" error="{$inp/email/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Юридический адрес: <span>*</span>
								</div>
							</div>
							<input class="input" type="text"
								   name="{$inp/address/@input}" value="{f:not_empty($inp/address, $u/address)}" error="{$inp/address/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Должность руководителя: <span>*</span>
								</div>
							</div>
							<input class="input" type="text"
								   name="{$inp/boss/@input}" value="{f:not_empty($inp/boss, $u/boss)}" error="{$inp/boss/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Ф.И.О руководителя: <span>*</span>
								</div>
							</div>
							<input class="input" type="text"
								   name="{$inp/director/@input}" value="{f:not_empty($inp/director, $u/director)}" error="{$inp/director/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Действует на основании: <span>*</span>
								</div>
							</div>
							<select name="{$inp/base/@input}" value="{f:not_empty($inp/base, $u/base)}" id="base">
								<xsl:for-each select="('', 'Устава', 'Доверенности', 'Свидетельства', 'Приказа')">
									<option value="{.}"><xsl:value-of select="."/></option>
								</xsl:for-each>
							</select>
						</div>
						<div class="form__item form-item base_inputs" style="display: none" id="base_extra">
							<div class="form-item__label">
								<div style="display: inline-block">№:
									<input class="input" type="text" style="width: 100px"
										   name="{$inp/base_number/@input}" value="{f:not_empty($inp/base_number, $u/base_number)}" error="{$inp/base_number/@validation-error}"/>
								</div>
								<div style="display: inline-block">от:
									<input class="input" type="text" style="width: 100px" placeholder="01.01.2000"
										   name="{$inp/base_date/@input}" value="{f:not_empty($inp/base_date, $u/base_date)}" error="{$inp/base_date/@validation-error}"/>
								</div>
							</div>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Источник финансирования: <span>*</span>
								</div>
							</div>
							<select name="{$inp/fund/@input}" value="{f:not_empty($inp/fund, $u/fund)}">
								<option></option>
							</select>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Контактное лицо: <span>*</span>
								</div>
							</div>
							<input class="input" type="text"
								   name="{$inp/contact_name/@input}" value="{f:not_empty($inp/contact_name, $u/contact_name)}" error="{$inp/contact_name/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Телефон контактного лица: <span>*</span>
								</div>
							</div>
							<input class="input" type="text"
								   name="{$inp/contact_phone/@input}" value="{f:not_empty($inp/contact_phone, $u/contact_phone)}" error="{$inp/contact_phone/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>
									Расчетный счет: <span>*</span>
								</div>
								<div>
									<label>
										<input type="checkbox" name="{$inp/no_account/@input}" value="да" id="no_account_checkbox">
											<xsl:if test="'да' = $inp/no_account or 'да' = $u/no_account">
												<xsl:attribute name="checked" select="'checked'"/>
											</xsl:if>
										</input>
										Нет расчетного счета
									</label>
								</div>
							</div>
							<input class="input bank_input" type="text"
								   name="{$inp/account/@input}" value="{f:not_empty($inp/account, $u/account)}" error="{$inp/account/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Название банка: <span>*</span>
								</div>
							</div>
							<input class="input bank_input" type="text"
								   name="{$inp/bank/@input}" value="{f:not_empty($inp/bank, $u/bank)}" error="{$inp/bank/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Адрес банка: <span>*</span>
								</div>
							</div>
							<input class="input bank_input" type="text"
								   name="{$inp/bank_address/@input}" value="{f:not_empty($inp/bank_address, $u/bank_address)}" error="{$inp/bank_address/@validation-error}"/>
						</div>
						<div class="form__item form-item">
							<div class="form-item__label">
								<div>Код банка: <span>*</span>
								</div>
							</div>
							<input class="input bank_input" type="text"
								   name="{$inp/bank_code/@input}" value="{f:not_empty($inp/bank_code, $u/bank_code)}" error="{$inp/bank_code/@validation-error}"/>
						</div>
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
						<button class="button button_big" type="submit">Зарегистрироваться</button>
					</form>
				</div>
			</div>
		</div>
	</xsl:template>



<!--
	<xsl:template name="CONTENT">

		<div class="tabs tabs_product">
			<xsl:if test="$message and not($success)">
				<div class="alert alert-danger">
					<h4>Ошибка</h4>
					<p><xsl:value-of select="$message"/></p>
				</div>
			</xsl:if>
			<xsl:if test="$message and $success">
				<div class="alert alert-success">
					<p><xsl:value-of select="$message"/></p>
				</div>
			</xsl:if>

			<div class="tabs__nav">
				<a href="#tab_login" class="tab{' tab_active'[$is_login]}">Вход</a>
				<a href="#tab_phys" class="tab{' tab_active'[not($is_jur) and not($is_login)]}">Физическое лицо</a>
				<a href="#tab_jur" class="tab{' tab_active'[$is_jur]}">Юридическое лицо</a>
			</div>
			<div class="tabs__content">

				<div class="tab-container" id="tab_login" style="{'display: none'[not($is_login)]}">
					<div class="text form__text">
						<p>Введите адрес электрнной почты и пароль.</p>
					</div>
					<form action="{page/submit_login}" method="post" onsubmit="lock('tab_login')">
						<div class="form__item">
							<label class="form-label" for="login">Электронная почта:</label>
							<input class="input form__element" type="text" id="login" name="email"/>
						</div>
						<div class="form__item">
							<label class="form-label" for="password">Пароль:</label>
							<input class="input form__element" type="password" id="password" name="password"/>
						</div>
						<div class="form__proceed">
							<input type="submit" class="button button_size_lg" value="Войти"/>
						</div>
					</form>
				</div>

				<div class="tab-container" id="tab_phys" style="{'display: none'[$is_jur or $is_login]}">
					<div class="text form__text">
						<p>
							Заполните, пожалуйста, форму регистрации. Ваш email будет использован в качестве логина.
							Если не указан email, будет использован номер телефона.
						</p>
					</div>
					<form action="{page/confirm_link}" method="post" onsubmit="lock('tab_phys')">
						<xsl:variable name="inp" select="page/user_phys/input"/>
						<xsl:call-template name="USER_PHYS_INPUTS">
							<xsl:with-param name="inp" select="$inp"/>
							<xsl:with-param name="vals" select="page/phys"/>
						</xsl:call-template>
						<div class="form__item">
							<label class="form-label" for="formid">Пароль:</label>
							<input class="input form__element" type="text" id="formid"
								   name="{$inp/password/@input}" value="{$inp/password}" error="{$inp/password/@validation-error}"/>
						</div>
						<div class="form__proceed">
							<input type="submit" class="button button_size_lg" value="Отправить анкету"/>
						</div>
					</form>
				</div>

				<div class="tab-container" id="tab_jur" style="{'display: none'[not($is_jur)]}">
					<div class="text form__text">
						<p>
							Заполните, пожалуйста, форму регистрации. Ваш email будет использован в качестве логина.
							Если не указан email, будет использован номер телефона.
						</p>
					</div>
					<form action="{page/confirm_link}" method="post" onsubmit="lock('tab_jur')">
						<xsl:variable name="inp" select="page/user_jur/input"/>
						<xsl:call-template name="USER_JUR_INPUTS">
							<xsl:with-param name="inp" select="$inp"/>
							<xsl:with-param name="vals" select="page/jur"/>
						</xsl:call-template>
						<div class="form__item">
							<label class="form-label" for="formid">Пароль:</label>
							<input class="input form__element" type="text" id="formid"
								   name="{$inp/password/@input}" value="{$inp/password}" error="{$inp/password/@validation-error}"/>
						</div>
						<div class="form__proceed">
							<input type="submit" class="button button_size_lg" value="Отправить анкету"/>
						</div>
					</form>
				</div>

			</div>
		</div>

	</xsl:template>
-->

	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="TAB_SCRIPT"/>
		<script  type="text/javascript">
			function toggleBase() {
				if ($('#base').val() == 'Устава' || $('#base').val() == '') {
					$('#base_extra').hide();
					$('#base_extra').find('input').val('');
				} else {
					$('#base_extra').show();
				}
			}

			function toggleBank() {
				if ($("#no_account_checkbox").prop("checked")) {
					$('.bank_input').prop('disabled', true);
					$('.bank_input').val('');
				} else {
					$('.bank_input').prop('disabled', false);
				}
			}

			$(document).ready(function() {
				toggleBank();
				toggleBase();
				$('#no_account_checkbox').change(toggleBank);
				$('#base').change(toggleBase);
			});
		</script>
	</xsl:template>

</xsl:stylesheet>