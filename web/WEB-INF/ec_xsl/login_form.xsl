<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="user_data_inputs.xsl"/>
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="success" select="page/variables/success = 'true'"/>
	<xsl:variable name="is_jur" select="page/user_jur//@validation-error"/>
	<xsl:variable name="registered" select="page/user/group/@name = 'registered'"/>

	<xsl:template name="CONTENT">
		<!-- CONTENT BEGIN -->
		<div class="path-container">
			<div class="path">
				<a href="/">Home Page</a> <i class="fas fa-angle-right"></i>
			</div>
			<!-- <xsl:call-template name="PRINT"/> -->
		</div>
		<xsl:if test="$registered">
			<h1 class="page-title">Вход выполнен.</h1>
			<p>Добро пожаловать!</p>
		</xsl:if>
		<xsl:if test="not($registered)">
			<h1 class="page-title">Вход</h1>

			<div class="page-content m-t">
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
			
				<div class="tab-content">
					<div role="tabpanel" class="tab-pane active" id="tab_phys">
						<p>Введите адрес электрнной почты и пароль.</p>
						<form action="{page/submit_form}" method="post" onsubmit="lock('tab_phys')">
							<div class="form-group">
								<label>Электронная почта:</label>
								<input type="text" class="form-control" name="email"/>
							</div>
							<div class="form-group">
								<label>Пароль:</label>
								<input type="password" class="form-control" name="password"/>
							</div>
							<input type="submit" value="Войти"/>
						</form>
					</div>
				</div>
			</div>
		</xsl:if>

		<xsl:call-template name="ACTIONS_MOBILE"/>
	</xsl:template>

</xsl:stylesheet>