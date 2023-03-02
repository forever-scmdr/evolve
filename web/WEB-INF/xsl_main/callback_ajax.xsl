<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f"
		version="2.0">
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes" exclude-result-prefixes="#all"/>

	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="success" select="page/variables/result = 'success'"/>
	<xsl:variable name="form" select="page/generic_form"/>

	<xsl:template match="/">
		<div class="result" id="modal_popup">
			<div class="popup__body">
				<div class="popup__content">
					<a class="popup__close" href="#">Х</a>
					<div class="popup__title title title_2" style="font-weight: normal;">Закажите обратный звонок</div>

					<!-- <p style="margin-bottom: 16px; text-align: center; color: #707070;">Наш специалист перезвонит Вам в течение минуты</p> -->

					<xsl:if test="$message">
						<div class="alert alert_{if ($success) then 'success' else 'danger'}">
							<div class="alert__text">
								<p><xsl:value-of select="$message"/></p>
							</div>
						</div>
					</xsl:if>
					<form action="{page/submit_link}" method="post" ajax="true" ajax-loader-id="modal_popup">
						<input class="form__element" type="hidden" name="{$form/input/pseudo/@input}" value="form"/>
						<div class="form__item">
							<label class="form-label">Ваше имя:</label>
							<input class="form__element" type="text" name="{$form/input/name/@input}" value="{$form/input/name}"/>
						</div>
						<div class="form__item">
							<label class="form-label">Телефон:</label>
							<input class="form__element" type="text" name="{$form/input/phone/@input}" value="{$form/input/phone}"/>
						</div>
						<div class="form__proceed">
							<input class="button" type="submit" value="Отправить" />
						</div>
					</form>
				</div>
			</div>
		</div>
	</xsl:template>


</xsl:stylesheet>