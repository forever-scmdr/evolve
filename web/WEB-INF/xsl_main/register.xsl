<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="user_data_inputs.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="success" select="page/variables/success = 'true'"/>
	<xsl:variable name="is_login" select="page/variables/login = 'true'"/>
	<xsl:variable name="is_restore" select="page/variables/restore = 'true'"/>
	<xsl:variable name="is_jur" select="not($is_login) and not($is_restore)"/>


	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="COMMON_LEFT_COLOUMN"/>
	</xsl:template>

	<xsl:template name="PAGE_HEADING">
		<div class="title title_1">Вход/Регистрация</div>
	</xsl:template>

	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a href="{$main_host}" class="path__link">Главная страница</a>
				<div class="path__arrow"></div>
			</div>
		</div>
	</xsl:template>



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
				<a href="#tab_jur" class="tab{' tab_active'[$is_jur]}">Регистрация</a>
				<a href="#tab_phys" class="tab{' tab_active'[$is_restore]}">Восстановить пароль</a>
			</div>
			<div class="tabs__content">

				<div class="tab-container" id="tab_login" style="{'display: none'[not($is_login)]}">
					<div class="text form__text">
						<p>Введите адрес вашей электронной почты и пароль.</p>
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
						<div class="form__item">
							<label class="form-label" for="formid">Подтверждение пароля:</label>
							<input class="input form__element" type="text" id="formid"
								   name="{$inp/password2/@input}" value="{$inp/password2}" error="{$inp/password2/@validation-error}"/>
						</div>
						<div class="form__proceed">
							<input type="submit" class="button button_size_lg" value="Отправить анкету"/>
						</div>
					</form>
				</div>

				<div class="tab-container" id="tab_restore" style="{'display: none'[not($is_restore)]}">
					<div class="text form__text">
						<p>
							Введите, пожалуйста, адрес электронной почты, используемый для логина.
							На него будет выслан пароль.
						</p>
					</div>
					<form action="{page/confirm_link}" method="post" onsubmit="lock('tab_restore')">
						<xsl:variable name="inp" select="page/user_phys/input"/>
						<div class="form__item">
							<label class="form-label" for="formid">Электронная почта / логин:</label>
							<input class="input form__element" type="text" id="formid"
								   name="{$inp/email/@input}" value="{$inp/email}" error="{$inp/email/@validation-error}"/>
						</div>
						<div class="form__proceed">
							<input type="submit" class="button button_size_lg" value="Отправить"/>
						</div>
					</form>
				</div>

			</div>
		</div>

	</xsl:template>

</xsl:stylesheet>