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
		<xsl:param name="has_extra" select="false()"/>
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
		<xsl:if test="$has_extra">
			<xsl:variable name="forms" select="page/forms"/>
			<div class="form__item">
				<label class="form-label" for="form_ship">Способ покупки:</label>
				<select class="form__element buytype" id="buytype_selectf"
						name="{$inp/buytype/@input}" value="{f:not_empty($inp/buytype, $vals/buytype)}" error="{$inp/buytype/@validation-error}">
					<option value="">Выберите способ покупки</option>
					<xsl:for-each select="$forms/buy_options/option">
						<option><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
			</div>

			<div class="form__item region" id="paycurrencyf" style="display: none">
				<label class="form-label" for="form_ship">Валюта коммерческого предложения:</label>
				<select class="form__element" id="currency_selectf"
						name="{$inp/paycurrency/@input}" value="{f:not_empty($inp/paycurrency, $vals/paycurrency)}" error="{$inp/paycurrency/@validation-error}">
					<option value="">Выберите валюту</option>
					<xsl:for-each select="$forms/buy_options/currency">
						<option><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
			</div>

			<div class="form__item region" id="region1f" style="display: none">
				<label class="form-label" for="form_ship">Регион:</label>
				<select class="form__element" id="region1_selectf"
						name="{$inp/region1/@input}" value="{f:not_empty($inp/region1, $vals/region1)}" error="{$inp/region1/@validation-error}">
					<option value="">Выберите регион</option>
					<xsl:for-each select="$forms/dealer_regions/dealer_region">
						<option rid="{if (dealer_region) then concat('regionf_', @id) else ''}"
								did="{if (dealer) then concat('regionf_', @id) else ''}"><xsl:value-of select="name"/></option>
					</xsl:for-each>
				</select>
			</div>

			<div class="form__item region2" id="region2f" style="display: none">
				<label class="form-label" for="form_ship">Округ:</label>
				<select class="form__element"
						name="{$inp/region2/@input}" value="{f:not_empty($inp/region2, $vals/region2)}" error="{$inp/region2/@validation-error}">
					<option value="">Выберите округ</option>
					<xsl:for-each select="$forms/dealer_regions/dealer_region/dealer_region">
						<option did="regionf_{@id}" prid="regionf_{../@id}" class="option"><xsl:value-of select="name"/></option>
					</xsl:for-each>
				</select>
			</div>

			<div class="form__item dealer" id="dealerf" style="display: none">
				<label class="form-label" for="form_ship">Дилер/партнер:</label>
				<select class="form__element" id="form_shipf"
						name="{$inp/dealer/@input}" value="{f:not_empty($inp/dealer, $vals/dealer)}" error="{$inp/dealer/@validation-error}">
					<option value="">Выберите дилера</option>
					<xsl:for-each select="$forms//dealer">
						<option prid="regionf_{../@id}" class="option"><xsl:value-of select="name"/></option>
					</xsl:for-each>
				</select>
			</div>
			<!--
			<div class="form__item"><label class="form-label" for="form_pay">Способ оплаты:</label>
				<select class="form__element" id="form_pay"
						name="{$inp/pay_type/@input}" value="{f:not_empty($inp/pay_type, $vals/pay_type)}" error="{$inp/pay_type/@validation-error}">
					<option value="">Выберите способ оплаты</option>
					<xsl:for-each select="page/common/payment/option">
						<option><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
			</div>
			-->
		</xsl:if>
		<script>
			function showRegionsf(el) {
				$('#paycurrencyf, #region1f, #region2f, #dealerf').hide();
				var val = $(el).val();
				if (val.toUpperCase().indexOf("ДИЛЕР") >= 0) {
					var regionSelect = $('#region1f');
					regionSelect.show();
					showSubregionsAndDealersf(regionSelect);
				} else if (val.toUpperCase().indexOf("ЗАПРОС") >= 0) {
					$('#paycurrencyf').show();
				}
			}

			function showSubregionsAndDealersf(el) {
				$('#region2f, #dealerf').hide();
				var parentRegionId = $(el).find(':selected').attr('rid');
				if (parentRegionId &amp;&amp; parentRegionId != '') {
					$('#region2f').show();
					$('#region2f').find('.option[prid != ' + parentRegionId + ']').hide();
					$('#region2f').find('.option[prid = ' + parentRegionId + ']').show();
					showDealersf($('#region2f'));
				} else {
					showDealersf($('#region1f'));
				}
			}

			function showDealersf(el) {
				var parentRegionId = $(el).find(':selected').attr('did');
				if (parentRegionId &amp;&amp; parentRegionId != '') {
					$('#dealerf').show();
					$('#dealerf').find('.option[prid != ' + parentRegionId + ']').hide();
					$('#dealerf').find('.option[prid = ' + parentRegionId + ']').show();
				}
			}

			var currentRegionId = '0';
			$(document).ready(function() {
				$('#buytype_selectf').change(function() {
					showRegionsf(this);
					$('#paycurrencyf, #region1f, #region2f, #dealerf').find('select').val('');
				});
				$('#region1f').change(function() {
					showSubregionsAndDealersf(this);
					$('#region2f, #dealerf').find('select').val('');
				});
				$('#region2f').change(function() {
					showDealersf(this);
					$('#dealerf').find('select').val('');
				});
				showRegionsf($('#buytype_selectf'));
			});
		</script>
	</xsl:template>






	<xsl:template name="USER_JUR_INPUTS">
		<xsl:param name="inp"/>
		<xsl:param name="vals" select="$inp"/>
		<xsl:param name="has_extra" select="false()"/>
		<div class="form__item">
			<label class="form-label" for="form_org">Наименование организации:</label>
			<input class="input form__element" type="text" id="form_org"
				   name="{$inp/organization/@input}" value="{f:not_empty($inp/organization, $vals/organization)}" error="{$inp/organization/@validation-error}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_org">Телефон/факс:</label>
			<input class="input form__element" type="text" id="form_org"
				   name="{$inp/phone/@input}" value="{f:not_empty($inp/phone, $vals/phone)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_email">E-mail:</label>
			<input class="input form__element" type="text" id="form_email"
				   name="{$inp/email/@input}" value="{f:not_empty($inp/email, $vals/email)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_contact">Контактное лицо:</label>
			<input class="input form__element" type="text" id="form_contact"
				   name="{$inp/contact_name/@input}" value="{f:not_empty($inp/contact_name, $vals/contact_name)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_contact_phone">Телефон контактного лица:</label>
			<input class="input form__element" type="text" id="form_contact_phone"
				   name="{$inp/contact_phone/@input}" value="{f:not_empty($inp/contact_phone, $vals/contact_phone)}"/>
		</div>
		<!--
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
			<div class="form__item"><label class="form-label" for="form_pay">Способ оплаты:  <a href="oplata">подробнее</a></label>
				<select class="form__element" id="form_pay"
						name="{$inp/pay_type/@input}" value="{f:not_empty($inp/pay_type, $vals/pay_type)}" error="{$inp/pay_type/@validation-error}">
					<option value="">Выберите способ оплаты</option>
					<xsl:for-each select="page/common/payment/option">
						<option><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
			</div>
		</xsl:if>
		-->
		<xsl:if test="$has_extra">
			<xsl:variable name="forms" select="page/forms"/>
			<div class="form__item">
				<label class="form-label" for="form_ship">Способ покупки:</label>
				<select class="form__element buytype" id="buytype_select"
						name="{$inp/buytype/@input}" value="{f:not_empty($inp/buytype, $vals/buytype)}" error="{$inp/buytype/@validation-error}">
					<option value="">Выберите способ покупки</option>
					<xsl:for-each select="$forms/buy_options/option">
						<option><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
			</div>

			<div class="form__item region" id="paycurrency" style="display: none">
				<label class="form-label" for="form_ship">Валюта коммерческого предложения:</label>
				<select class="form__element" id="currency_select"
						name="{$inp/paycurrency/@input}" value="{f:not_empty($inp/paycurrency, $vals/paycurrency)}" error="{$inp/paycurrency/@validation-error}">
					<option value="">Выберите валюту</option>
					<xsl:for-each select="$forms/buy_options/currency">
						<option><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
			</div>

			<div class="form__item region" id="region1" style="display: none">
				<label class="form-label" for="form_ship">Регион:</label>
				<select class="form__element" id="region1_select"
						name="{$inp/region1/@input}" value="{f:not_empty($inp/region1, $vals/region1)}" error="{$inp/region1/@validation-error}">
					<option value="">Выберите регион</option>
					<xsl:for-each select="$forms/dealer_regions/dealer_region">
						<option rid="{if (dealer_region) then concat('region_', @id) else ''}"
								did="{if (dealer) then concat('region_', @id) else ''}"><xsl:value-of select="name"/></option>
					</xsl:for-each>
				</select>
			</div>

			<div class="form__item region2" id="region2" style="display: none">
				<label class="form-label" for="form_ship">Округ:</label>
				<select class="form__element"
						name="{$inp/region2/@input}" value="{f:not_empty($inp/region2, $vals/region2)}" error="{$inp/region2/@validation-error}">
					<option value="">Выберите округ</option>
					<xsl:for-each select="$forms/dealer_regions/dealer_region/dealer_region">
						<option did="region_{@id}" prid="region_{../@id}" class="option"><xsl:value-of select="name"/></option>
					</xsl:for-each>
				</select>
			</div>

			<div class="form__item dealer" id="dealer" style="display: none">
				<label class="form-label" for="form_ship">Дилер/партнер:</label>
				<select class="form__element" id="form_ship"
						name="{$inp/dealer/@input}" value="{f:not_empty($inp/dealer, $vals/dealer)}" error="{$inp/dealer/@validation-error}">
					<option value="">Выберите дилера</option>
					<xsl:for-each select="$forms//dealer">
						<option prid="region_{../@id}" class="option"><xsl:value-of select="name"/></option>
					</xsl:for-each>
				</select>
			</div>
			<!--
			<div class="form__item"><label class="form-label" for="form_pay">Способ оплаты:</label>
				<select class="form__element" id="form_pay"
						name="{$inp/pay_type/@input}" value="{f:not_empty($inp/pay_type, $vals/pay_type)}" error="{$inp/pay_type/@validation-error}">
					<option value="">Выберите способ оплаты</option>
					<xsl:for-each select="page/common/payment/option">
						<option><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
			</div>
			-->
		</xsl:if>
		<div class="form__item">
			<label class="form-label" for="form_ja">Юридический адрес:</label>
			<input class="input form__element" type="text" id="form_ja"
				   name="{$inp/address/@input}" value="{f:not_empty($inp/address, $vals/address)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_account">Расчетный счет:</label>
			<input class="input form__element" type="text" id="form_account"
				   name="{$inp/account/@input}" value="{f:not_empty($inp/account, $vals/account)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_bank">Название банка:</label>
			<input class="input form__element" type="text" id="form_bank"
				   name="{$inp/bank/@input}" value="{f:not_empty($inp/bank, $vals/bank)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_banka">Адрес банка:</label>
			<input class="input form__element" type="text" id="form_banka"
				   name="{$inp/bank_address/@input}" value="{f:not_empty($inp/bank_address, $vals/bank_address)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_bankc">Код банка:</label>
			<input class="input form__element" type="text" id="form_bankc"
				   name="{$inp/bank_code/@input}" value="{f:not_empty($inp/bank_code, $vals/bank_code)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_unp">УНП:</label>
			<input class="input form__element" type="text" id="form_unp"
				   name="{$inp/unp/@input}" value="{f:not_empty($inp/unp, $vals/unp)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_dir">Ф.И.О директора:</label>
			<input class="input form__element" type="text" id="form_dir"
				   name="{$inp/director/@input}" value="{f:not_empty($inp/director, $vals/director)}"/>
		</div>
		<div class="form__item">
			<label class="form-label" for="form_base">Действует на основании:</label>
			<input class="input form__element" type="text" id="form_base"
				   name="{$inp/base/@input}" value="{f:not_empty($inp/base, $vals/base)}"/>
		</div>
		<script>
			function showRegions(el) {
				$('#paycurrency, #region1, #region2, #dealer').hide();
				var val = $(el).val();
				if (val.toUpperCase().indexOf("ДИЛЕР") >= 0) {
					var regionSelect = $('#region1');
					regionSelect.show();
					showSubregionsAndDealers(regionSelect);
				} else if (val.toUpperCase().indexOf("ЗАПРОС") >= 0) {
					$('#paycurrency').show();
				}
			}

			function showSubregionsAndDealers(el) {
				$('#region2, #dealer').hide();
				var parentRegionId = $(el).find(':selected').attr('rid');
				if (parentRegionId &amp;&amp; parentRegionId != '') {
					$('#region2').show();
					$('#region2').find('.option[prid != ' + parentRegionId + ']').hide();
					$('#region2').find('.option[prid = ' + parentRegionId + ']').show();
					showDealers($('#region2'));
				} else {
					showDealers($('#region1'));
				}
			}

			function showDealers(el) {
				var parentRegionId = $(el).find(':selected').attr('did');
				if (parentRegionId &amp;&amp; parentRegionId != '') {
					$('#dealer').show();
					$('#dealer').find('.option[prid != ' + parentRegionId + ']').hide();
					$('#dealer').find('.option[prid = ' + parentRegionId + ']').show();
				}
			}

			var currentRegionId = '0';
			$(document).ready(function() {
				$('#buytype_select').change(function() {
					showRegions(this);
					$('#paycurrency, #region1, #region2, #dealer').find('select').val('');
				});
				$('#region1').change(function() {
					showSubregionsAndDealers(this);
					$('#region2, #dealer').find('select').val('');
				});
				$('#region2').change(function() {
					showDealers(this);
					$('#dealer').find('select').val('');
				});
				showRegions($('#buytype_select'));
			});
		</script>
	</xsl:template>


</xsl:stylesheet>