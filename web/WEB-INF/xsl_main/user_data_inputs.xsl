<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:function name="f:not_empty">
		<xsl:param name="first"/>
		<xsl:param name="second"/>
		<xsl:value-of select="if (not($first = '')) then $first else $second"/>
	</xsl:function>


	<xsl:template name="USER_PHYS_INPUTS">
		<xsl:param name="inp"/>
		<xsl:param name="vals" select="$inp"/>
		<div class="form__item">
			<label class="form-label" for="form_name">Ваше имя:</label>
			<input class="input form__element" type="text" id="form_name"
				   name="{$inp/name/@input}" value="{f:not_empty($inp/name, $vals/name)}" error="{$inp/name/@validation-error}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_address">Адрес:</label>
			<input class="input form__element" type="text" id="form_address"
				   name="{$inp/address/@input}" value="{f:not_empty($inp/address, $vals/address)}" error="{$inp/address/@validation-error}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_phone">Телефон:</label>
			<input class="input form__element" type="text" id="form_phone"
				   name="{$inp/phone/@input}" value="{f:not_empty($inp/phone, $vals/phone)}" error="{$inp/phone/@validation-error}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_name">Электронная почта:</label>
			<input class="input form__element" type="text" id="form_name"
				   name="{$inp/email/@input}" value="{f:not_empty($inp/email, $vals/email)}" error="{$inp/email/@validation-error}"/>
		</div>
		<xsl:if test="page/@name != 'register'">
			<div class="form__item"><label class="form-label" for="form_ship">Способ доставки: <a href="dostavka">подробнее</a></label>
				<select class="form__element" id="form_ship"
						name="{$inp/ship_type/@input}" value="{f:not_empty($inp/ship_type, $vals/ship_type)}" error="{$inp/ship_type/@validation-error}">
					<option value="">Выберите способ доставки</option>
					<xsl:for-each select="page/common/delivery/option">
						<option><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
			</div>
			<div class="form__item"><label class="form-label" for="form_pay">Способ оплаты:</label>
				<select class="form__element" id="form_pay"
						name="{$inp/pay_type/@input}" value="{f:not_empty($inp/pay_type, $vals/pay_type)}" error="{$inp/pay_type/@validation-error}">
					<option value="">Выберите способ оплаты</option>
					<xsl:for-each select="page/common/payment/option">
						<option><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
			</div>
		</xsl:if>
	</xsl:template>






	<xsl:template name="USER_JUR_INPUTS">
		<xsl:param name="inp"/>
		<xsl:param name="vals" select="$inp"/>
		<xsl:param name="register" select="false()"/>
		<div class="title title_2">Организация</div>
		<div class="form__item">
			<label class="form-label" for="form_email">*E-mail (логин):</label>
			<input class="input form__element" type="text" id="form_email"
				   name="{$inp/email/@input}" value="{f:not_empty($inp/email, $vals/email)}"/>
		</div>
		<xsl:if test="$register">
			<div class="form__item">
				<label class="form-label" for="formid">*Пароль:</label>
				<input class="input form__element" type="password" id="formid"
					   name="{$inp/password/@input}" value="{$inp/password}" error="{$inp/password/@validation-error}"/>
			</div>
			<div class="form__item">
				<label class="form-label" for="formid">*Подтвердите пароль:</label>
				<input class="input form__element" type="password" id="formid"
					   name="{$inp/password2/@input}" value="{$inp/password2}" error="{$inp/password2/@validation-error}"/>
			</div>
		</xsl:if>
		<div class="form__item">
			<label class="form-label" for="form_org">*Наименование организации:</label>
			<input class="input form__element" type="text" id="form_org"
				   name="{$inp/organization/@input}" value="{f:not_empty($inp/organization, $vals/organization)}" error="{$inp/organization/@validation-error}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_org">*ОГРН:</label>
			<input class="input form__element" type="text" id="form_org"
				   name="{$inp/orgn/@input}" value="{f:not_empty($inp/orgn, $vals/orgn)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_org">*КПП:</label>
			<input class="input form__element" type="text" id="form_org"
				   name="{$inp/kpp/@input}" value="{f:not_empty($inp/kpp, $vals/kpp)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_org">*ИНН:</label>
			<input class="input form__element" type="text" id="form_org"
				   name="{$inp/inn/@input}" value="{f:not_empty($inp/inn, $vals/inn)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_ja">*Юридический адрес:</label>
			<input class="input form__element" type="text" id="form_ja"
				   name="{$inp/address/@input}" value="{f:not_empty($inp/address, $vals/address)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_ja">Фактический адрес: <input type="checkbox" style="margin-left: 30px;" id="form_fa_ch"/> Совпадает с юридическим</label>
			<input class="input form__element" type="text" id="form_fa"
				   name="{$inp/fact_address/@input}" value="{f:not_empty($inp/fact_address, $vals/fact_address)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_ja">Почтовый адрес: <input type="checkbox" style="margin-left: 51px;" id="form_pa_ch"/> Совпадает с юридическим</label>
			<input class="input form__element" type="text" id="form_pa"
				   name="{$inp/post_address/@input}" value="{f:not_empty($inp/post_address, $vals/post_address)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_org">Сайт организации:</label>
			<input class="input form__element" type="text" id="form_org"
				   name="{$inp/web_site/@input}" value="{f:not_empty($inp/web_site, $vals/web_site)}"/>
		</div>
		<div class="title title_2">Контактное лицо</div>
		<div class="form__item">
			<label class="form-label" for="form_contact">*ФИО:</label>
			<input class="input form__element" type="text" id="form_contact"
				   name="{$inp/contact_name/@input}" value="{f:not_empty($inp/contact_name, $vals/contact_name)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_contact_phone">*Телефон:</label>
			<input class="input form__element" type="text" id="form_contact_phone"
				   name="{$inp/contact_phone/@input}" value="{f:not_empty($inp/contact_phone, $vals/contact_phone)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_contact_phone">E-mail:</label>
			<input class="input form__element" type="text" id="form_contact_phone"
				   name="{$inp/contact_email/@input}" value="{f:not_empty($inp/contact_email, $vals/contact_email)}"/>
		</div>
		<xsl:if test="not($register)">
			<div class="title title_2">Дополнительно</div>
		</xsl:if>
		<script>
			$(document).ready(function() {
				$('#form_fa_ch').change(function() {
					if ($(this).is(":checked")) {
						$('#form_fa').attr('readonly', true);
						$('#form_fa').val($('#form_ja').val());
					} else {
						$('#form_fa').attr('readonly', false);
					}
				});
				$('#form_pa_ch').change(function() {
					if ($(this).is(":checked")) {
						$('#form_pa').attr('readonly', true);
						$('#form_pa').val($('#form_ja').val());
					} else {
						$('#form_pa').attr('readonly', false);
					}
				});
				$('#form_ja').on('input', function() {
					var val = $(this).val();
					if ($('#form_fa_ch').is(":checked")) {
						$('#form_fa').val(val);
					}
					if ($('#form_pa_ch').is(":checked")) {
						$('#form_pa').val(val);
					}
				});
			});
		</script>
	</xsl:template>


</xsl:stylesheet>