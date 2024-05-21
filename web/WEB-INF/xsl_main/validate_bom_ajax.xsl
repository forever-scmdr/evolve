<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]><xsl:stylesheet		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"		xmlns="http://www.w3.org/1999/xhtml"		xmlns:f="f:f"		version="2.0">	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes" exclude-result-prefixes="#all"/>	<xsl:variable name="command_xml" select="page/formatted/xml"/>	<xsl:template match="/">		<div class="result" id="modal_popup">			<xsl:if test="$command_xml/bom/query[@qty = '0']">				<div class="popup__body">					<div class="popup__content">						<a class="popup__close" href="#">X</a>						<div class="popup__title title title_2">Ошибка BOM</div>						<div class="alert alert_danger">							<div class="alert__text">								<p>Для некоторых позиций не указано количество. Эти позиции не будут участвовать в поиске и не будут отображаться в результатах</p>							</div>						</div>						<form action="{page/search_api_link}" method="post" id="{'validate_form'}" onsubmit="lock('modal_popup')">							<input class="form__element" type="hidden" name="q" value="{page/variables/q}"/>							<div class="form__proceed">								<input class="button" type="submit" value="Выполнить поиск" />								<input class="button popup_close" type="button" value="Отменить поиск" style="margin-left: 10px"/>							</div>						</form>					</div>				</div>			</xsl:if>			<xsl:if test="count($command_xml/bom/query[@qty = '0']) = 0">				<form action="{page/search_api_link}" method="post" id="{'validate_form'}">					<textarea name="q" style="display:none"><xsl:value-of select="page/variables/q" /></textarea>				</form>				<script>					lock('modal_popup');					$('#validate_form').submit();				</script>			</xsl:if>		</div>	</xsl:template></xsl:stylesheet>