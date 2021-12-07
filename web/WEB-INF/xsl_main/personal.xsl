<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="user_data_inputs.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="title" select="'Персональные данные'"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>
	<xsl:variable name="user" select="page/user"/>

	<xsl:variable name="message" select="$user/item_own_extras/user_message | $pv/message"/>
	<xsl:variable name="success" select="page/variables/success = ('true', 'yes')"/>
	<xsl:variable name="is_jur" select="$user/@type = 'user_jur'"/>
	<xsl:variable name="is_phys" select="not($is_jur)"/>




	<xsl:template name="PAGE_PATH"></xsl:template>


	<xsl:template name="PASSWORD_3">
		<xsl:param name="inp"/>
		<div class="form__item form-item">
			<a href="#" onclick="return togglePass()">
				<span id="pass_show">Изменить пароль</span><span id="pass_hide" style="display: none">Отменить</span>
			</a>
		</div>
		<div id="pass_3" style="display: none">
			<div class="form__item form-item">
				<div class="form-item__label">
					<div>Старый пароль: <span>*</span>
					</div>
				</div>
				<input class="input" type="password"
					   name="{$inp/p1/@input}" error="{$inp/p1/@validation-error}"/>
			</div>
			<div class="form__item form-item">
				<div class="form-item__label">
					<div>Новый пароль: <span>*</span>
					</div>
				</div>
				<input class="input" type="password"
					   name="{$inp/p2/@input}" error="{$inp/p2/@validation-error}"/>
			</div>
			<div class="form__item form-item">
				<div class="form-item__label">
					<div>Новый пароль еще раз: <span>*</span>
					</div>
				</div>
				<input class="input" type="password"
					   name="{$inp/p3/@input}" error="{$inp/p3/@validation-error}"/>
			</div>
		</div>
		<div class="form__buttons">
			<button class="button button_big" type="submit">Сохранить изменения</button>
			<button class="button button_big button_secondary" type="submit" onclick="document.location.replace('{page/personal_link}'); return false;">Отмена</button>
		</div>
	</xsl:template>


	<xsl:template name="CONTENT_INNER">
		<div class="form-text">
			<p>Эта информация нужна, чтобы мы могли связаться с вами для уточнения заказа</p>
		</div>
		<xsl:call-template name="MESSAGE"/>
		<xsl:if test="$is_phys">
			<xsl:variable name="inp" select="page/user/input"/>
			<form id="form" class="form" action="{page/user/confirm_link}" method="post" onsubmit="lock('form')">
				<input type="hidden" name="{$inp/pseudo/@input}" value="pseudo"/>
				<xsl:call-template name="USER_PHYS_INPUTS">
					<xsl:with-param name="inp" select="$inp"/>
					<xsl:with-param name="u" select="page/user[@type='user_phys']"/>
				</xsl:call-template>
				<div class="form__item form-item">
					<div class="card-div">
						<div class="form-item__label">
							<div><b>Номер скидочной карты:</b></div>
						</div>
						<input class="input" type="text"/>
					</div>
					<div><b>Номинал скидочной карты: <xsl:value-of select="if ($user/discount and not($user/discount = '')) then $user/discount else '0'"/>%</b></div>
					<img src="/images/card0.jpg"/>
				</div>
				<xsl:call-template name="PASSWORD_3">
					<xsl:with-param name="inp" select="$inp"/>
				</xsl:call-template>
			</form>
		</xsl:if>
		<xsl:if test="$is_jur">
			<xsl:variable name="inp" select="page/user/input"/>
			<form id="form" class="form" action="{page/user/confirm_link}" method="post" onsubmit="lock('form')">
				<input type="hidden" name="{$inp/pseudo/@input}" value="pseudo"/>
				<xsl:call-template name="USER_JUR_INPUTS">
					<xsl:with-param name="inp" select="$inp"/>
					<xsl:with-param name="u" select="page/user[@type='user_phys']"/>
				</xsl:call-template>
				<xsl:call-template name="PASSWORD_3">
					<xsl:with-param name="inp" select="$inp"/>
				</xsl:call-template>
			</form>
		</xsl:if>
	</xsl:template>



	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="USER_DATA_SCRIPT"/>
		<script  type="text/javascript">
			function togglePass() {
				$('#pass_3').toggle();
				$('#pass_hide').toggle();
				$('#pass_show').toggle();
				$('#pass_3').find('input').val('');
				return false;
			}
		</script>
	</xsl:template>

</xsl:stylesheet>