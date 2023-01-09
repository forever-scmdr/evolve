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
		<xsl:call-template name="MESSAGE"/>
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
					<form class="form" action="{page/confirm_link}" method="post" onsubmit="lock('tab_phys')">
						<input type="hidden" name="{$inp/pseudo/@input}" value="pseudo"/>
						<xsl:call-template name="USER_PHYS_INPUTS">
							<xsl:with-param name="inp" select="$inp"/>
							<xsl:with-param name="u" select="page/user[@type='user_phys']"/>
						</xsl:call-template>
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
						<div class="form__item form-item">
							<label>
								Даю согласие на обработку моих персональных данных:&#160;
								<input type="checkbox" value="да"
									   name="{$inp/confirmpersonal/@input}" error="{$inp/confirmpersonal/@validation-error}"/>
							</label>
						</div>
						<button class="button button_big" type="submit">Зарегистрироваться</button>
					</form>
				</div>
				<div class="tab-container" id="tab_jur" style="{'display: none;'[$is_phys]}">
					<xsl:variable name="inp" select="page/user_jur/input"/>
					<xsl:variable name="u" select="page/user[@type='user_jur']"/>
					<form class="form" action="{page/confirm_link}" method="post" onsubmit="lock('tab_jur')">
						<input type="hidden" name="{$inp/pseudo/@input}" value="pseudo"/>
						<xsl:call-template name="USER_JUR_INPUTS">
							<xsl:with-param name="inp" select="$inp"/>
							<xsl:with-param name="u" select="page/user[@type='user_phys']"/>
						</xsl:call-template>
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
						<div class="form__item form-item">
							<label>
								Даю согласие на обработку моих персональных данных:&#160;
								<input type="checkbox" value="да"
									   name="{$inp/confirmpersonal/@input}" error="{$inp/confirmpersonal/@validation-error}"/>
							</label>
						</div>
						<button class="button button_big" type="submit">Зарегистрироваться</button>
					</form>
				</div>
			</div>
		</div>
	</xsl:template>



	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="TAB_SCRIPT"/>
		<xsl:call-template name="USER_DATA_SCRIPT"/>
	</xsl:template>

</xsl:stylesheet>