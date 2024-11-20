<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="agent_domains.xsl"/>
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="success" select="page/variables/result = 'success'"/>
	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="f" select="page/form"/>

	<xsl:variable name="ajax_id" select="page/variables/original_src"/>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:template match="/">
		<div class="result" id="{$ajax_id}">
			<xsl:if test="not($success)">
				<p>
				для отправки сообщения пожалуйста пройдите регистрацию.
				</p>
				<xsl:if test="$message">
					<p style="color: red">
						<xsl:if test="$message != 'Заполните, пожалуйста, обязательные поля'">
							<xsl:value-of select="$message"/>
						</xsl:if>
						<xsl:if test="$message = 'Заполните, пожалуйста, обязательные поля'">
							Заполните, пожалуйста, ВСЕ поля
						</xsl:if>
					</p>
				</xsl:if>
				<form method="post" action="{$f/submit_link}">
					<input type="hidden" name="required" value="country, region, city, organization, address, phone, email, site, contact_name, boss_name, type, branch"/>
					<input type="hidden" name="email" value="info@termobrest.ru, sproject@termobrest.ru, forever@forever-ds.com" />

					<div class="form-group">
						<label>Страна:</label>
						<input type="text" name="{$f/country/@input}" value="{$f/country}" class="form-control"/>
					</div>
					
					<div class="form-group">
						<label>Область:</label>
						<input type="text" name="{$f/region/@input}" value="{$f/region}" class="form-control"/>
					</div>
					
					<div class="form-group">
						<label>Город:</label>
						<input type="text" name="{$f/city/@input}" value="{$f/city}" class="form-control"/>
					</div>
					
					<div class="form-group">
						<label>Организация:</label>
						<input type="text" name="{$f/organization/@input}" value="{$f/organization}" class="form-control"/>
					</div>
					
					<div class="form-group">
						<label>Адрес:</label>
						<input type="text" name="{$f/address/@input}" value="{$f/address}" class="form-control"/>
					</div>
					
					<div class="form-group">
						<label>Контактный телефон:</label>
						<input type="text" name="{$f/phone/@input}" value="{$f/phone}" class="form-control"/>
					</div>
					
					<div class="form-group">
						<label>Email:</label>
						<input type="text" name="{$f/email/@input}" value="{$f/email}" class="form-control"/>
					</div>
					
					<div class="form-group">
						<label>Web-сайт:</label>
						<input type="text" name="{$f/site/@input}" value="{$f/site}" class="form-control"/>
					</div>
					
					<div class="form-group">
						<label>Контактное лицо:</label>
						<input type="text" name="{$f/contact_name/@input}" value="{$f/contact_name}" class="form-control"/>
					</div>
					
					<div class="form-group">
						<label>Руководитель:</label>
						<input type="text" name="{$f/boss_name/@input}" value="{$f/boss_name}" class="form-control"/>
					</div>
					
					<div class="form-group">
						<label>Род деятельности организации:</label>
						<select class="form-control" id="type" name="{$f/type/@input}" value="{$f/type}">
							<xsl:for-each select="$agent_types"><option><xsl:value-of select="."/></option></xsl:for-each>
						</select>
					</div>
					
					<div class="form-group">
						<label>Отрасль:</label>
						<select class="form-control" id="branch" name="{$f/branch/@input}" value="{$f/branch}">
							<xsl:for-each select="$agent_branches"><option><xsl:value-of select="."/></option></xsl:for-each>
						</select>
					</div>

					<button type="submit" class="btn btn-primary btn-block" onclick="postForm($(this).closest('form'), '{$ajax_id}', initSelects); return false;">Отправить запрос</button>
				</form>
			</xsl:if>
			<xsl:if test="$success">
				<p><xsl:value-of select="$message"/></p>
			</xsl:if>
		</div>
	</xsl:template>

</xsl:stylesheet>