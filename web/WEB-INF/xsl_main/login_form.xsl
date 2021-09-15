<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="user_data_inputs.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="title" select="'Вход'"/>
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>



	<xsl:template name="PAGE_PATH"></xsl:template>


	<xsl:template name="CONTENT_INNER">
		<xsl:if test="$is_user_registered">
			<script type="text/javascript">
				history.go(-2);
			</script>
		</xsl:if>
		<xsl:call-template name="MESSAGE"/>
		<form class="form" action="{page/submit_form}" method="post">
			<div class="form__item form-item">
				<div class="form-item__label">
					<div>Электронная почта: <span>*</span>
					</div>
				</div>
				<input class="input" type="text" name="login"/>
			</div>
			<div class="form__item form-item">
				<div class="form-item__label">
					<div>Пароль: <span>*</span>
					</div>
				</div>
				<input class="input" type="password" name="password"/>
			</div>
			<button class="button button_big" type="submit">Войти</button>
		</form>
	</xsl:template>


</xsl:stylesheet>