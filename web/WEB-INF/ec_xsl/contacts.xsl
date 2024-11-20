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

	<xsl:variable name="title" select="'Контакты ТермоБрест'"/>

	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="success" select="page/variables/result = 'success'"/>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:variable name="active_mmi" select="'contacts'"/>
	<xsl:variable name="critical_item" select="page/contacts"/>

	<xsl:template name="CONTENT">
	<div class="spacer"></div>
	<div class="container main-content">
		<div class="row">
			<div class="col-xs-12">
				<div class="row">
					<div class="col-xs-12">
						<div class="path hidden-xs">
							<a href="{$base}">Главная страница</a>
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
					<div class="col-md-4" id="feedback_container">
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
							<form method="post" action="{$f/submit_link}" onsubmit="lock('feedback_container')">
								<input type="hidden" name="required" value="name,phone,email,message"/>
								<div class="form-group" title="обязательно для заполнения">
									<label>ФИО:*</label>
									<input type="text" name="{$f/name/@input}" value="{$f/name}" class="form-control"/>
								</div>
								<div class="form-group">
									<label>Наименование организации:</label>
									<input type="text" name="{$f/organization/@input}" value="{$f/organization}" class="form-control" />
								</div>
								<div class="form-group">
									<label>Адрес организации:</label>
									<input type="text" name="{$f/jur_address/@input}" value="{$f/jur_address}" class="form-control" />
								</div>
								<div class="form-group" title="обязательно для заполнения">
									<label>Контактный телефон:</label>
									<input type="text" name="{$f/phone/@input}" value="{$f/phone}" class="form-control" />
								</div>
								<div class="form-group mandatory-input" title="обязательно для заполнения">
									<label>Дополнительный E-mail:*</label>
									<input type="text" name="{$f/spam/@input}" value="{$f/spam}" class="form-control" />
								</div>
								<div class="form-group" title="обязательно для заполнения">
									<label>E-mail:*</label>
									<input type="text" name="{$f/email/@input}" value="{$f/email}" class="form-control" />
								</div>
								<div class="form-group" title="обязательно для заполнения">
									<label>Текст сообщения:*</label>
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