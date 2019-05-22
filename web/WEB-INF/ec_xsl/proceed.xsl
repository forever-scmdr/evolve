<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="user_data_inputs.xsl"/>
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Оформление заявки'"/>
	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="is_jur" select="page/user_jur/input/field[@name != 'registered'] != '' or page/jur"/>
	<xsl:variable name="is_phys" select="not($is_jur)"/>
	<!--<xsl:variable name="is_jur" select="page/user_jur//@validation-error or page/user_jur/input/organization != '' or page/jur or page/user_jur/input/field != ''"/>-->

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="{$main_host}">Главная страница</a> <i class="fas fa-angle-right"></i>
				<a href="{page/cart_link}">Корзина</a> <i class="fas fa-angle-right"></i>
			</div>
			<xsl:call-template name="PRINT"/>
		</div>
		<h1 class="page-title">Анкета покупателя</h1>

		<div class="page-content m-t">
			<xsl:if test="$message">
				<div class="alert alert-danger">
					<h4>Ошибка</h4>
					<p><xsl:value-of select="$message"/></p>
				</div>
			</xsl:if>
			<ul class="nav nav-tabs" role="tablist">
				<li role="presentation" class="{'active'[not($is_jur)]}"><a href="#tab_phys" role="tab" data-toggle="tab">Физическое лицо</a></li>
				<li role="presentation" class="{'active'[$is_jur]}"><a href="#tab_jur" role="tab" data-toggle="tab">Юридическое лицо</a></li>
			</ul>
			<div class="tab-content">

				<div role="tabpanel" class="tab-pane{' active'[not($is_jur)]}" id="tab_phys">
					<p>Заполните, пожалуйста, форму ниже. Эти данные нужны для правильного оформления заявки.</p>
					<form action="{page/confirm_link}" method="post" onsubmit="lock('tab_phys')">
						<xsl:variable name="inp" select="page/user_phys/input"/>
						<div class="cart-form cart-form__phys">
							<xsl:call-template name="USER_PHYS_INPUTS">
								<xsl:with-param name="inp" select="$inp"/>
								<xsl:with-param name="vals" select="page/phys"/>
							</xsl:call-template>
						</div>
						<div class="form-group">
							<label>Комментарий:</label>
							<textarea class="form-control" rows="3" name="{$inp/comment/@input}"><xsl:value-of select="$inp/comment"/></textarea>
						</div>
						<xsl:call-template name="TOTAL"/>
					</form>
				</div>

				<div role="tabpanel" class="tab-pane{' active'[$is_jur]}" id="tab_jur">
					<p>Заполните, пожалуйста, форму ниже. Эти данные нужны для правильного оформления заказа.</p>
					<form action="{page/confirm_link}" method="post" onsubmit="lock('tab_jur')">
						<xsl:variable name="inp" select="page/user_jur/input"/>
						<div class="cart-form">
							<xsl:call-template name="USER_JUR_INPUTS">
								<xsl:with-param name="inp" select="$inp"/>
								<xsl:with-param name="vals" select="page/jur"/>
							</xsl:call-template>
						</div>
						<div class="form-group">
							<label>Комментарий:</label>
							<textarea class="form-control" rows="3" name="{$inp/comment/@input}"><xsl:value-of select="$inp/comment"/></textarea>
						</div>
						<xsl:call-template name="TOTAL"/>
					</form>
				</div>
			</div>

		</div>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>


	<xsl:template name="TOTAL">
		<div class="total">
			<p>Итого: <xsl:value-of select="f:currency_decimal(page/cart/sum_discount)"/> р.</p>
			<xsl:if test="f:num(page/cart/sum) &gt; f:num(page/cart/sum_discount)">
				<div class="discount-total">
					Итоговая скидка: <xsl:value-of select="round((f:num(page/cart/sum) - f:num(page/cart/sum_discount)) * 100) div 100"/> руб.
					Сумма без учета скидки: <xsl:value-of select="page/cart/sum"/> руб.
				</div>
			</xsl:if>
			<input type="submit" class="button" value="Отправить заказ" onclick="$(this).closest('form').attr('action', '{page/confirm_link}')"/>
		</div>
	</xsl:template>


</xsl:stylesheet>