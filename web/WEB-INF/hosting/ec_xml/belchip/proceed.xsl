<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:saxon="http://saxon.sf.net/"
	xmlns:ext="http://exslt.org/common"
	xmlns="http://www.w3.org/1999/xhtml"
	version="1.0"
	exclude-result-prefixes="xsl saxon ext">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="inputs.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="menu_style" select="'menu-container3'"/>
	<xsl:variable name="cform" select="//contacts_form"/>
	<xsl:variable name="ns" select="page/variables/not_set"/>
	<xsl:variable name="message" select="//cart_contacts/user_message"/>

	<!-- ****************************    СТРАНИЦА    ******************************** -->


	<xsl:template name="CONTENT">
		<!--center column -->
		<td class="center-c">
			<div class="c-div">
				<div class="c-top">
					<table class="c-table">
						<tr>
							<td>
								<xsl:call-template name="SEARCH"/>
							</td>
							<td width="21"></td>
							<xsl:call-template name="NEWS"/>
						</tr>
					</table>
				</div>
				<p style="padding: 15px 0; font-size:15px;">
					<a href="{//cart_link}">назад к резерву</a>
				</p>
				<h1>Оформление резерва</h1>
				<xsl:if test="$message and $message != ''">
					<div class="message">
						<xsl:value-of select="$message"/>
						<xsl:if test="starts-with($message, 'Минимальная сумма')">
							<xsl:text> </xsl:text>&#160;&#160;&#160;<xsl:text> </xsl:text><a href="{page/cart_link}">Открыть корзину</a>
						</xsl:if>
					</div>
				</xsl:if>
				<xsl:variable name="forms" select="//cart_contacts"/>
				<div class="c-block clearfix" id="submit_page">
					<!--left -->
					<div class="c-left">
						<xsl:if test="not(//register_jur)">
						<h3>Физическое лицо</h3>
						<p>Эта информация нужна для того, чтобы мы могли связаться в вами для
							уточнения резерва</p>
						<form action="{//contacts_form/post_phys_link}" method="post">
							Фамилия:
							<span>*</span>
							<xsl:call-template name="BR"/>
							<input type="text" class="inp2" style="{'background: #FFC4C4'[$ns = 'second_name']}" 
								name="{$cform/field[@name='second_name']/@input}" value="{$forms/second_name}" />
							<xsl:call-template name="BR"/>
							Имя:
							<span class="post_mandatory" style="display: none">*</span>
							<xsl:call-template name="BR"/>
							<input type="text" class="inp2" name="{$cform/field[@name='name']/@input}" 
								value="{$forms/name}" style="{'background: #FFC4C4'[$ns = 'name']}" />
							<xsl:call-template name="BR"/>
							Отчество:
							<span class="post_mandatory" style="display: none">*</span>
							<xsl:call-template name="BR"/>
							<input type="text" class="inp2" name="{$cform/field[@name='s_name']/@input}" 
								value="{$forms/s_name}" style="{'background: #FFC4C4'[$ns = 's_name']}"/>
							<xsl:call-template name="BR"/>
							Телефон (формат: +375 (29) 123 45 67):
							<span>*</span>
							<xsl:call-template name="BR"/>
							<input type="text" class="inp2" style="{'background: #FFC4C4'[$ns = 'phone']}" 
								name="{$cform/field[@name='phone']/@input}" value="{$forms/phone}" placeholder="формат: +375 (29) 123 45 67" />
							<xsl:call-template name="BR"/>
							E-mail:
							<span>*</span>
							<xsl:call-template name="BR"/>
							<input type="text" class="inp2" style="{'background: #FFC4C4'[$ns = 'email']}" 
								name="{$cform/field[@name='email']/@input}" value="{$forms/email}" />
							<xsl:call-template name="BR"/>
							<label>
								<xsl:call-template name="check_checkbox">
									<xsl:with-param name="value" select="'да'"/>
									<xsl:with-param name="name" select="$cform/need_post_address/@input"/>
									<xsl:with-param name="check" select="$forms/need_post_address"/>
									<xsl:with-param name="id" select="'needPostPhys'"/>
								</xsl:call-template>
								Отправить заказ почтой
							</label>
							<xsl:call-template name="BR"/>
							<xsl:call-template name="BR"/>
							<script>
								function myTrim(x) {
								    return x.replace(/^\s+|\s+$/gm,'');
								}
								function disableRadio() {
									var onPlaceInp = $('#on_place').find('input');
									var addressInp = $('#postAddressPhys');
									if ($('#needPostPhys').prop('checked')) {
										onPlaceInp.prop('disabled', true);
										onPlaceInp.prop('checked', false);
										addressInp.show();
										$('.post_mandatory').show();
										$('#getOrderFrom').hide();
									} else {
										onPlaceInp.prop('disabled', false);
										addressInp.val('');
										addressInp.hide();
										$('.post_mandatory').hide();
										$('#getOrderFrom').show();
									}
								}
								$(document).ready(function() {
									$('#needPostPhys').click(function() {
										disableRadio();
									});
									if (!$('#needPostPhys').prop('checked')) {
										$('#postAddressPhys').hide();
									} else {
										$('#on_place').find('input').prop('disabled', true);
										$('.post_mandatory').show();
										$('#getOrderFrom').hide();
									}
								});
							</script>
							<div id="postAddressPhys">
								Адрес:
								<span>*</span>
								<xsl:call-template name="BR"/>
								<input type="text" class="inp2" style="{'background: #FFC4C4'[$ns = 'post_address']}" 
									name="{$cform/field[@name='post_address']/@input}" value="{$forms/post_address}" placeholder="ул..."/>
								<xsl:call-template name="BR"/>
								Индекс:
								<span>*</span>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="http://ex.belpost.by/addressbook/" target="_blank">Узнать свой индекс</a>
								<xsl:call-template name="BR"/>
								<input type="text" class="inp2" style="{'background: #FFC4C4'[$ns = 'post_index']}"
									name="{$cform/field[@name='post_index']/@input}" value="{$forms/post_index}" placeholder="220000"/>
								<xsl:call-template name="BR"/>
								Город:
								<span>*</span>
								<xsl:call-template name="BR"/>
								<input type="text" class="inp2" style="{'background: #FFC4C4'[$ns = 'post_city']}" 
									name="{$cform/field[@name='post_city']/@input}" value="{$forms/post_city}"/>
								<xsl:call-template name="BR"/>
							</div>
							Дополнительная информация:
							<xsl:call-template name="BR"/>
							<textarea class="inp2" name="{$cform/field[@name='phys_message']/@input}">
							<xsl:value-of select="$forms/phys_message"/>
							</textarea>
							<xsl:call-template name="BR"/>
							В случае отсутствия некоторых позиций:
							<span>*</span>
							<xsl:call-template name="BR"/>
							<ul style="margin-top: 10px{'; background: #FFC4C4'[$ns = 'if_absent']}">
								<li>
									<label>
									<xsl:call-template name="check_radio">
										<xsl:with-param name="value" select="'собрать заказ без них'"/>
										<xsl:with-param name="check" select="$forms/if_absent"/>
										<xsl:with-param name="name" select="$cform/if_absent/@input"/>
									</xsl:call-template>
									зарезервировать без них
									</label>
								</li>
								<li>
									<label>
									<xsl:call-template name="check_radio">
										<xsl:with-param name="value" select="'сообщить об этом по телефону'"/>
										<xsl:with-param name="check" select="$forms/if_absent"/>
										<xsl:with-param name="name" select="$cform/if_absent/@input"/>
									</xsl:call-template>
									сообщить об этом по телефону
									</label>
								</li>
								<li id="on_place">
									<label>
									<xsl:call-template name="check_radio">
										<xsl:with-param name="value" select="'подберу замену на месте'"/>
										<xsl:with-param name="check" select="$forms/if_absent"/>
										<xsl:with-param name="name" select="$cform/if_absent/@input"/>
									</xsl:call-template>
									подберу замену на месте
									</label>
								</li>
								<!--
								<xsl:call-template name="BR"/>
								<label>
									<xsl:call-template name="check_checkbox">
										<xsl:with-param name="value" select="'card'"/>
										<xsl:with-param name="name" select="$cform/pay_by/@input"/>
										<xsl:with-param name="check" select="$forms/pay_by"/>
									</xsl:call-template>
									Оплата кредитной карточкой (не работает, находиться в стадии тестирования)
								</label>
							-->
							</ul>
							<xsl:call-template name="BR"/>
							<div id="getOrderFrom">
								Пункт самовывоза:
								<span>*</span>
								<xsl:call-template name="BR"/>
								<ul style="margin-top: 10px{'; background: #FFC4C4'[$ns = 'get_order_from']}">
									<li>
										<label>
										<xsl:call-template name="check_radio">
											<xsl:with-param name="value" select="'ул. Л. Беды, 45'"/>
											<xsl:with-param name="check" select="$forms/get_order_from"/>
											<xsl:with-param name="name" select="$cform/get_order_from/@input"/>
										</xsl:call-template>
										ул. Л. Беды, 45
										</label>
									</li>
									<li>
										<label>
										<xsl:call-template name="check_radio">
											<xsl:with-param name="value" select="'ул. Скрыганова, 4А'"/>
											<xsl:with-param name="check" select="$forms/get_order_from"/>
											<xsl:with-param name="name" select="$cform/get_order_from/@input"/>
										</xsl:call-template>
										ул. Скрыганова, 4А
										</label>
									</li>
								</ul>
								<xsl:call-template name="BR"/>
							</div>
							<input type="submit" class="sendorder buttonNew  buttonNew-green" value="Отправить заказ" onclick="lock('submit_page')"/>
<!-- 							<input type="submit" class="sendorder" value="" onclick="lock('submit_page')"/> -->
						</form>
						</xsl:if>
						<xsl:if test="not(//register_phys)">
						<h3>Юридическое лицо</h3>
						<p>
							Эта информация нужна для того, чтобы мы могли связаться в вами для
							уточнения резерва
						</p>
						<form action="{$cform/post_jur_link}" method="post">
							Наименование организации:
							<span>*</span>
							<xsl:call-template name="BR"/>
							<input type="text" class="inp2" style="{'background: #FFC4C4'[$ns = 'organization']}" 
								name="{$cform/field[@name='organization']/@input}" value="{$forms/organization}" />
							<xsl:call-template name="BR"/>
							Телефон/факс:
							<span>*</span>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<xsl:call-template name="check_radio">
								<xsl:with-param name="value" select="'факс'"/>
								<xsl:with-param name="check" select="if ($forms/send_contract_to) then $forms/send_contract_to else 'факс'"/>
								<xsl:with-param name="name" select="$cform/send_contract_to/@input"/>
							</xsl:call-template>
							Отправить договор на факс
							<xsl:call-template name="BR"/>
							<input type="text" class="inp2" style="{'background: #FFC4C4'[$ns = 'jur_phone']}"
								 name="{$cform/field[@name='jur_phone']/@input}" value="{$forms/jur_phone}" placeholder="формат: +375 (29) 123 45 67" />
							<xsl:call-template name="BR"/>
							Электронный адрес:
							<span>*</span>
							&nbsp;&nbsp;
							<xsl:call-template name="check_radio">
								<xsl:with-param name="value" select="'email'"/>
								<xsl:with-param name="check" select="$forms/send_contract_to"/>
								<xsl:with-param name="name" select="$cform/send_contract_to/@input"/>
							</xsl:call-template>
							Отправить договор на email
							<xsl:call-template name="BR"/>
							<input type="text" class="inp2" style="{'background: #FFC4C4'[$ns = 'jur_email']}"
								name="{$cform/field[@name='jur_email']/@input}" value="{$forms/jur_email}" />
							<xsl:call-template name="BR"/>
							<label>
								<xsl:call-template name="check_checkbox">
									<xsl:with-param name="value" select="'да'"/>
									<xsl:with-param name="name" select="$cform/jur_need_post_address/@input"/>
									<xsl:with-param name="check" select="$forms/jur_need_post_address"/>
									<xsl:with-param name="id" select="'needPostJur'"/>
								</xsl:call-template>
								Требуется отправка почтой
							</label>
							<xsl:call-template name="BR"/>
							<xsl:call-template name="BR"/>
							<script>
								function toggleAddress() {
									var address = $('#postAddressJur');
									if ($('#needPostJur').prop('checked')) {
										address.show();
									} else {
										address.find('input').val('');
										address.hide();
									}
								}
								$(document).ready(function() {
									$('#needPostJur').click(function() {
										toggleAddress();
									});
									if (!$('#needPostJur').prop('checked')) {
										$('#postAddressJur').hide();
									}
								});
							</script>
							<div id="postAddressJur">
								Почтовый адрес:
								<span>*</span>
								<xsl:call-template name="BR"/>
								<input type="text" class="inp2" style="{'background: #FFC4C4'[$ns = 'jur_post_address']}" 
									name="{$cform/field[@name='jur_post_address']/@input}" value="{$forms/jur_post_address}" placeholder="ул..."/>
								<xsl:call-template name="BR"/>
								Индекс:
								<span>*</span>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="http://ex.belpost.by/addressbook/" target="_blank">Узнать свой индекс</a>
								<xsl:call-template name="BR"/>
								<input type="text" class="inp2" style="{'background: #FFC4C4'[$ns = 'jur_post_index']}"
									name="{$cform/field[@name='jur_post_index']/@input}" value="{$forms/jur_post_index}" placeholder="220000"/>
								<xsl:call-template name="BR"/>
								Город:
								<span>*</span>
								<xsl:call-template name="BR"/>
								<input type="text" class="inp2" style="{'background: #FFC4C4'[$ns = 'jur_post_city']}" 
									name="{$cform/field[@name='jur_post_city']/@input}" value="{$forms/jur_post_city}"/>
								<xsl:call-template name="BR"/>
							</div>							
							<xsl:call-template name="BR"/>
							Контактное лицо:
							<span>*</span>
							<xsl:call-template name="BR"/>
							<input type="text" class="inp2"  style="{'background: #FFC4C4'[$ns = 'contact_name']}"
								name="{$cform/field[@name='contact_name']/@input}" value="{$forms/contact_name}" />
							<xsl:call-template name="BR"/>
							Телефон контактного лица:
							<span>*</span>
							<xsl:call-template name="BR"/>
							<input type="text" class="inp2" style="{'background: #FFC4C4'[$ns = 'contact_phone']}" 
								name="{$cform/field[@name='contact_phone']/@input}" value="{$forms/contact_phone}" />
							<xsl:call-template name="BR"/>
							Юридический адрес:
							<span>*</span>
							<xsl:call-template name="BR"/>
							<input type="text" class="inp2" style="{'background: #FFC4C4'[$ns = 'address']}" 
								name="{$cform/field[@name='address']/@input}" value="{$forms/address}" />
							<xsl:call-template name="BR"/>
							<script>
								$(document).ready(function() {
									$('.account').prop('disabled', $("#no_account").is(':checked'));
									$("#no_account").click(function() {
										$('.account').prop('disabled', this.checked);
										$('.account').val('');
									});
								});
							</script>
							Расчетный счет:
							<span>*</span>
							&nbsp;&nbsp;
							<xsl:call-template name="check_checkbox">
								<xsl:with-param name="value" select="'да'"/>
								<xsl:with-param name="check" select="$forms/no_account"/>
								<xsl:with-param name="name" select="$cform/no_account/@input"/>
								<xsl:with-param name="id" select="'no_account'"/>
							</xsl:call-template>
							Нет расчетного счета
							<xsl:call-template name="BR"/>
							<input type="text" class="inp2 account" style="{'background: #FFC4C4'[$ns = 'account']}" 
								name="{$cform/account/@input}" value="{$forms/account}" />
							<xsl:call-template name="BR"/>
							Название банка:
							<span>*</span>
							<xsl:call-template name="BR"/>
							<input type="text" class="inp2 account" style="{'background: #FFC4C4'[$ns = 'bank']}" 
								name="{$cform/bank/@input}" value="{$forms/bank}" />
							<xsl:call-template name="BR"/>
							Адрес банка:
							<span>*</span>
							<xsl:call-template name="BR"/>
							<input type="text" class="inp2 account" style="{'background: #FFC4C4'[$ns = 'bank_address']}" 
								name="{$cform/bank_address/@input}" value="{$forms/bank_address}" />
							<xsl:call-template name="BR"/>
							Код банка:
							<span>*</span>
							<xsl:call-template name="BR"/>
							<input type="text" class="inp2 account" style="{'background: #FFC4C4'[$ns = 'bank_code']}" 
								name="{$cform/bank_code/@input}" value="{$forms/bank_code}" />
							<xsl:call-template name="BR"/>
							УНП:
							<span>*</span>
							<xsl:call-template name="BR"/>
							<input type="text" class="inp2" style="{'background: #FFC4C4'[$ns = 'unp']}" 
								name="{$cform/field[@name='unp']/@input}" value="{$forms/unp}" />
							<xsl:call-template name="BR"/>
							Ф.И.О директора (индивидуального предпринимателя):
							<span>*</span>
							<xsl:call-template name="BR"/>
							<input type="text" class="inp2" style="{'background: #FFC4C4'[$ns = 'director']}" 
								name="{$cform/field[@name='director']/@input}" value="{$forms/director}" />
							<xsl:call-template name="BR"/>
							
							Действует на основании:
							<span>*</span>
							<xsl:call-template name="BR"/>
							<script>
								function toggleBase() {
									if ($('#base').val() != 'Устава') {
										$('#base_extra').show();
									} else {
										$('#base_extra').hide();
										$('#base_extra').find('input').val('');
									}
								}
								$(document).ready(function() {
									toggleBase();
									$.datepicker.setDefaults($.datepicker.regional["ru"]);
									$(".datepicker").datepicker();
								});
							</script>
							<select style="height: 25px; width: 140px; {'background: #FFC4C4'[$ns = 'base']}" 
								id="base" class="inp2" name="{$cform/base/@input}" onchange="toggleBase()">
								<xsl:call-template name="check_option">
									<xsl:with-param name="value" select="'Устава'"/>
									<xsl:with-param name="check" select="$forms/base"/>
									<xsl:with-param name="caption" select="'Устава'"/>
								</xsl:call-template>
								<xsl:call-template name="check_option">
									<xsl:with-param name="value" select="'Доверенности'"/>
									<xsl:with-param name="check" select="$forms/base"/>
									<xsl:with-param name="caption" select="'Доверенности'"/>
								</xsl:call-template>
								<xsl:call-template name="check_option">
									<xsl:with-param name="value" select="'Свидетельства'"/>
									<xsl:with-param name="check" select="$forms/base"/>
									<xsl:with-param name="caption" select="'Свидетельства'"/>
								</xsl:call-template>
							</select>
							<span id="base_extra" style="color: black">
								№:
								<input type="text" class="inp2" style="width:50px;{' background: #FFC4C4'[$ns = 'base_number']}" 
									name="{$cform/base_number/@input}" value="{$forms/base_number}" />
								от:
								<input type="text" class="inp2" style="width:70px;{' background: #FFC4C4'[$ns = 'base_date']}" 
									name="{$cform/base_date/@input}" value="{$forms/base_date}" placeholder="01.01.1980"/>
							</span>
							<xsl:call-template name="BR"/>
							<!-- 
							Почтовый адрес (при доставке товара почтой):
							<xsl:call-template name="BR"/>
							<input type="text" class="inp2" name="{$cform/field[@name='jur_post_address']/@input}" value="{$forms/jur_post_address}" />
							<xsl:call-template name="BR"/>
							 -->
							Цель приобретения:
							<span>*</span>
							<xsl:call-template name="BR"/>
							<select style="height: 25px; {'background: #FFC4C4'[$ns = 'jur_aim']}" id="base" class="inp2" name="{$cform/jur_aim/@input}">
								<option></option>
								<xsl:call-template name="simple_check_options">
									<xsl:with-param name="values" select="('Для собственных нужд', 'Для оптовой и (или) розничной торговли')"/>
									<xsl:with-param name="check" select="$forms/jur_aim"/>
								</xsl:call-template>
							</select>
							<xsl:call-template name="BR"/>
							Источник финансирования:
							<span>*</span>
							<xsl:call-template name="BR"/>
							<select style="height: 25px; {'background: #FFC4C4'[$ns = 'jur_fund']}" id="base" class="inp2" name="{$cform/jur_fund/@input}">
								<option></option>
								<xsl:call-template name="simple_check_options">
									<xsl:with-param name="values" select="('Собственные средства', 'Бюджетные средства', 'Внебюджетные средства')"/>
									<xsl:with-param name="check" select="$forms/jur_fund"/>
								</xsl:call-template>
							</select>
							<xsl:call-template name="BR"/>
							
							Дополнительная информация:
							<xsl:call-template name="BR"/>
							<textarea class="inp2" name="{$cform/field[@name='jur_message']/@input}">
							<xsl:value-of select="$forms/jur_message"/>
							</textarea>
							<xsl:call-template name="BR"/>
							<input type="submit" class="sendorder buttonNew  buttonNew-green" value="Отправить заказ" onclick="lock('submit_page')"/>
<!-- 							<input type="submit" class="sendorder" value="" onclick="lock('submit_page')"/> -->
						</form>
						</xsl:if>
					</div>
					<!--/left -->
					<!--right -->
					<!-- <div class="c-right" onmouseover="hideOverlay('c-right','c-left');"> -->
					<div class="c-right">
						<h3>
						Войти&#160;
						<xsl:if test="//register_jur or //register_phys">в другую учетную запись</xsl:if>
						</h3>
						<xsl:if test="page/variables/message and page/variables/message != ''">
							<p style="color: red"><xsl:value-of select="page/variables/message"/></p>
						</xsl:if>
						<form action="{$cform/login_link}" method="post">
							Имя пользователя:
							<xsl:call-template name="BR"/>
							<input type="text" class="inp2" name="{$cform/field[@name='login']/@input}" />
							<xsl:call-template name="BR"/>
							Пароль:
							<xsl:call-template name="BR"/>
							<input type="password" class="inp2" name="{$cform/field[@name='password']/@input}" />
							<xsl:call-template name="BR"/>
							<div>
								<a style="float: left; margin-top: 5px" href="{page/lost_password_link}">Забыли данные для входа?</a>
<!-- 								<input type="submit" value="" class="enter-btn" style="margin-left: 0; float: right"/> -->
								<input type="submit" class="buttonNew  buttonNew-green" value="Войти" style="margin-left: 0; float: right"/>
							</div>
						</form>
						<xsl:if test="not(//register_jur) and not(//register_phys)">
							<h3>Регистрация</h3>
							<p>После регистрации вам не придется вводить свои данные при
								следующих резервах. Достаточно будет ввести логин и пароль.</p>
							<a href="{//register_link}">Начать регистрацию</a>
						</xsl:if>
						<!-- <div class="overlay"></div> -->
					</div>
					<!--/right -->
				</div>
			</div>
		</td>
	</xsl:template>

</xsl:stylesheet>