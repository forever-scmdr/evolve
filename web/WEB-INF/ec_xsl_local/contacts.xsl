<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="title" select="'Контакты'"/>

	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="success" select="page/variables/result = 'success'"/>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:variable name="active_mmi" select="'contacts'"/>

	<xsl:template name="CONTENT">
	<div class="spacer"></div>
	<div class="container main-content">
		<div class="row">
			<div class="col-xs-12">
				<div class="row">
					<div class="col-xs-12">
						<div class="path hidden-xs">
							<a href="{page/index_link}">Главная страница</a>
							→
						</div>
						<h2 class="no-top-margin">Контакты</h2>
					</div>
				</div>

				<div class="row">
					<div class="col-md-4">
						<xsl:value-of select="page/contacts/first_col" disable-output-escaping="yes"/>
					</div>
					<div class="col-md-4">
						<xsl:value-of select="page/contacts/second_col" disable-output-escaping="yes"/>
					</div>
					<div class="col-md-4">
						<h3>Обратная связь</h3>
						<xsl:if test="$success">
							<h4 class="first-heading">Сообщение отправлено</h4>
							<p>
								<xsl:value-of select="$message"/>
							</p>
							<p><a href="{page/contacts_link}">Написать еще сообщение</a></p>
						</xsl:if>
						<xsl:if test="not($success)">
							<xsl:if test="$message">
								<p style="color: red"><xsl:value-of select="$message"/></p>
							</xsl:if>
							<form method="post" action="{$f/submit_link}">
								<input type="hidden" name="required" value="name,phone,message"/>
								<div class="form-group">
									<label>Как вас зовут:</label>
									<input type="text" name="{$f/name/@input}" value="{$f/name}" class="form-control"/>
								</div>
								<div class="form-group">
									<label>Наименование организации, адрес:</label>
									<input type="text" name="{$f/organization/@input}" value="{$f/organization}" class="form-control" />
								</div>
								<div class="form-group">
									<label>Контактный телефон, email:</label>
									<input type="text" name="{$f/phone/@input}" value="{$f/phone}" class="form-control" />
								</div>
								<div class="form-group">
									<label>Сообщение:</label>
									<textarea name="{$f/message/@input}" class="form-control"><xsl:value-of select="$f/message"/></textarea>
								</div>
								<button type="submit" class="btn btn-default">Отправить сообщение</button>
							</form>
						</xsl:if>
					</div>
				</div>
				<div class="row">
					<!-- карта -->
					<xsl:value-of select="page/contacts/map" disable-output-escaping="yes"/>
					<!-- /карта -->
				</div>
			</div>
		</div>
	</div>
	</xsl:template>

</xsl:stylesheet>