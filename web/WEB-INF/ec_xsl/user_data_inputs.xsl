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
		<div class="form-group">
			<label>Ваше имя *:</label>
			<input type="text" class="form-control" name="{$inp/name/@input}" value="{f:not_empty($inp/name, $vals/name)}" error="{$inp/name/@validation-error}"/>
		</div>
		<div class="form-group">
			<label for="">Адрес:</label>
			<input type="text" class="form-control" name="{$inp/address/@input}" value="{f:not_empty($inp/address, $vals/address)}" error="{$inp/address/@validation-error}"/>
		</div>
		<div class="form-group">
			<label>Способ доставки <a href="dostavka">Подробнее об условиях доставки</a></label>
			<select class="form-control" name="{$inp/ship_type/@input}" value="{f:not_empty($inp/ship_type, $vals/ship_type)}" error="{$inp/ship_type/@validation-error}">
				<xsl:for-each select="page/common/delivery/option">
					<option><xsl:value-of select="."/></option>
				</xsl:for-each>
			</select>
		</div>
		<div class="form-group">
			<label>Способ оплаты</label>
			<select class="form-control" name="{$inp/pay_type/@input}" value="{f:not_empty($inp/pay_type, $vals/pay_type)}" error="{$inp/pay_type/@validation-error}">
				<xsl:for-each select="page/common/payment/option">
					<option><xsl:value-of select="."/></option>
				</xsl:for-each>
			</select>
		</div>
		<div class="form-group">
			<label>Телефон:</label>
			<input type="text" class="form-control" name="{$inp/phone/@input}" value="{f:not_empty($inp/phone, $vals/phone)}" error="{$inp/phone/@validation-error}"/>
		</div>
		<div class="form-group">
			<label>Электронная почта:</label>
			<input type="text" class="form-control" name="{$inp/email/@input}" value="{f:not_empty($inp/email, $vals/email)}" error="{$inp/email/@validation-error}"/>
		</div>
	</xsl:template>






	<xsl:template name="USER_JUR_INPUTS">
		<xsl:param name="inp"/>
		<xsl:param name="vals" select="$inp"/>
		<div class="form-group">
			<label>Наименование организации *:</label>
			<input type="text" class="form-control" name="{$inp/organization/@input}"
			       value="{f:not_empty($inp/email, $vals/email)}" error="{$inp/organization/@validation-error}"/>
		</div>
		<div class="form-group">
			<label for="">Телефон/факс *:</label>
			<input type="text" class="form-control" name="{$inp/phone/@input}" value="{f:not_empty($inp/phone, $vals/phone)}"/>
		</div>
		<div class="form-group">
			<label>Способ доставки <a href="dostavka">Подробнее об условиях доставки</a></label>
			<select class="form-control" name="{$inp/ship_type/@input}" value="{f:not_empty($inp/ship_type, $vals/ship_type)}">
				<xsl:for-each select="page/common/delivery/option">
					<option><xsl:value-of select="."/></option>
				</xsl:for-each>
			</select>
		</div>
		<div class="form-group">
			<label>Способ оплаты</label>
			<select class="form-control" name="{$inp/pay_type/@input}" value="{f:not_empty($inp/pay_type, $vals/pay_type)}">
				<xsl:for-each select="page/common/payment/option">
					<option><xsl:value-of select="."/></option>
				</xsl:for-each>
			</select>
		</div>
		<div class="form-group">
			<label>E-mail:</label>
			<input type="text" class="form-control" name="{$inp/email/@input}" value="{f:not_empty($inp/email, $vals/email)}"/>
		</div>
		<div class="form-group">
			<label>Контактное лицо *:</label>
			<input type="text" class="form-control" name="{$inp/contact_name/@input}" value="{f:not_empty($inp/contact_name, $vals/contact_name)}"/>
		</div>
		<div class="form-group">
			<label>Телефон контактного лица:</label>
			<input type="text" class="form-control" name="{$inp/contact_phone/@input}" value="{f:not_empty($inp/contact_phone, $vals/contact_phone)}"/>
		</div>
		<div class="form-group">
			<label>Юридический адрес:</label>
			<input type="text" class="form-control" name="{$inp/address/@input}" value="{f:not_empty($inp/address, $vals/address)}"/>
		</div>
		<div class="form-group">
			<label>Расчетный счет:</label>
			<input type="text" class="form-control" name="{$inp/account/@input}" value="{f:not_empty($inp/account, $vals/account)}"/>
		</div>
		<div class="form-group">
			<label>Название банка:</label>
			<input type="text" class="form-control" name="{$inp/bank/@input}" value="{f:not_empty($inp/bank, $vals/bank)}"/>
		</div>
		<div class="form-group">
			<label>Адрес банка:</label>
			<input type="text" class="form-control" name="{$inp/bank_address/@input}" value="{f:not_empty($inp/bank_address, $vals/bank_address)}"/>
		</div>
		<div class="form-group">
			<label>Код банка:</label>
			<input type="text" class="form-control" name="{$inp/bank_code/@input}" value="{f:not_empty($inp/bank_code, $vals/bank_code)}"/>
		</div>
		<div class="form-group">
			<label>УНП:</label>
			<input type="text" class="form-control" name="{$inp/unp/@input}" value="{f:not_empty($inp/unp, $vals/unp)}"/>
		</div>
		<div class="form-group">
			<label>Ф.И.О директора:</label>
			<input type="text" class="form-control" name="{$inp/director/@input}" value="{f:not_empty($inp/director, $vals/director)}"/>
		</div>
		<div class="form-group">
			<label>Действует на основании:</label>
			<input type="text" class="form-control" name="{$inp/base/@input}" value="{f:not_empty($inp/base, $vals/base)}"/>
		</div>
	</xsl:template>


</xsl:stylesheet>