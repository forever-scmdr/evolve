<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f" version="2.0">
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
		<xsl:param name="u" select="$inp"/>
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
				<div>Отчество: </div>
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
	</xsl:template>






	<xsl:template name="USER_JUR_INPUTS">
		<xsl:param name="inp"/>
		<xsl:param name="u" select="$inp"/>
		<xsl:param name="is_proceed" select="false()"/>
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
                    <xsl:if test="$is_proceed">
                        &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
                        <xsl:call-template name="check_radio">
							<xsl:with-param name="name" select="$inp/send_contract_to/@input"/>
                            <xsl:with-param name="value" select="'факс'"/>
                            <xsl:with-param name="check" select="$u/send_contract_to"/>
                        </xsl:call-template>
                        Отправить договор на факс
                    </xsl:if>
				</div>
			</div>
			<input class="input" type="text"
				   name="{$inp/phone/@input}" value="{f:not_empty($inp/phone, $u/phone)}" error="{$inp/phone/@validation-error}"/>
		</div>
		<div class="form__item form-item">
			<div class="form-item__label">
				<div>Электронный адрес: <span>*</span>
					<xsl:if test="not($is_proceed)">
						<span style="padding-left: 5px; color: silver; font-size:11px;">
							будет использоваться в качестве логина при последующем входе
						</span>
					</xsl:if>
                    <xsl:if test="$is_proceed">
						&#160;&#160;
						<xsl:variable name="value_or_default" select="if ($u/send_contract_to != '') then $u/send_contract_to else 'email'"/>
                        <xsl:call-template name="check_radio">
							<xsl:with-param name="name" select="$inp/send_contract_to/@input"/>
                            <xsl:with-param name="value" select="'email'"/>
                            <xsl:with-param name="check" select="$value_or_default"/>
                        </xsl:call-template>
                        Отправить договор на email
                    </xsl:if>
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
			<select name="{$inp/boss/@input}" value="{f:not_empty($inp/boss, $u/boss)}">
				<xsl:for-each select="page/common/boss">
					<option value="{.}"><xsl:value-of select="."/></option>
				</xsl:for-each>
			</select>
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
				<xsl:for-each select="('', 'Собственные средства', 'Бюджетные средства', 'Внебюджетные средства')">
					<option value="{.}"><xsl:value-of select="."/></option>
				</xsl:for-each>
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
	</xsl:template>


	<xsl:template name="USER_DATA_SCRIPT">
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