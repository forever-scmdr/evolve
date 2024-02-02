<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="user_data_inputs.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Оформление заявки'"/>
	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="is_jur" select="true()"/>
	<!--<xsl:variable name="is_jur" select="page/user_jur//@validation-error or page/user_jur/input/organization != '' or page/jur or page/user_jur/input/field != ''"/>-->

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>

	<xsl:template name="PAGE_HEADING">
		<div class="title title_1">Оформление заказа</div>
	</xsl:template>

	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a href="{$main_host}" class="path__link">Главная страница</a>
				<div class="path__arrow"></div>
				<a href="{page/cart_link}" class="path__link">Корзина</a>
				<div class="path__arrow"></div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="CONTENT">
		<div class="tabs tabs_product">

			<xsl:if test="$message and not($success)">
				<div class="alert alert_danger">
					<div class="alert__title">Ошибка.</div>
					<div class="alert__text">
						<p><xsl:value-of select="$message"/>.</p>
					</div>
				</div>
			</xsl:if>
			<xsl:if test="$message and $success">
				<div class="alert alert_success">
					<div class="alert__text">
						<p><xsl:value-of select="$message"/></p>
					</div>
				</div>
			</xsl:if>

			<div class="tabs__content">

				<div class="tab-container" id="tab_jur">
					<div class="text form__text">
						<p>Заполните, пожалуйста, форму ниже. Эти данные нужны для правильного оформления заявки.</p>
					</div>
					<form action="{page/confirm_link}" method="post" onsubmit="lock('tab_jur')" enctype="multipart/form-data">
						<xsl:variable name="inp" select="page/user_jur/input"/>
						<xsl:call-template name="USER_JUR_INPUTS">
							<xsl:with-param name="inp" select="$inp"/>
							<xsl:with-param name="vals" select="page/jur"/>
						</xsl:call-template>
						<div class="form__item">
							<label class="form-label" for="formid">Комментарий:</label>
							<textarea class="form__element" rows="3" name="{$inp/comment/@input}"><xsl:value-of select="$inp/comment"/></textarea>
						</div>
						<div class="form__item">
							<label class="form-label" for="formid">Прикрепить файл:</label>
							<input class="form__element" name="{$inp/extrafile/@input}" type="file"><xsl:value-of select="$inp/extra_file"/></input>
						</div>
						<xsl:call-template name="TOTAL"/>
					</form>
				</div>

			</div>
		</div>

	</xsl:template>


	<xsl:template name="TOTAL">
		<div class="cart-total">
			<div class="cart-total__text">Итого: <xsl:value-of select="f:exchange_cur(page/cart, 'sum', 0)"/></div>
<!-- 			<xsl:if test="f:num(page/cart/sum) &gt; f:num(page/cart/sum_discount)">
				<div class="discount-total">
					Итоговая скидка: <xsl:value-of select="round((f:num(page/cart/sum) - f:num(page/cart/sum_discount)) * 100) div 100"/> руб.
					Сумма без учета скидки: <xsl:value-of select="page/cart/sum"/> руб.
				</div>
			</xsl:if> -->
			<div class="cart-total__buttons">
				<input type="submit" class="button button_2 cart-total__button" value="Оформить заказ" onclick="$(this).closest('form').attr('action', '{page/confirm_link}')"/>
			</div>
		</div>
	</xsl:template>


</xsl:stylesheet>