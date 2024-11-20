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

	<xsl:variable name="success" select="page/variables/result = 'success'"/>
	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="f" select="page/form"/>
	
	<xsl:template name="CONTENT">
		<xsl:if test="$success">
			<script type="text/javascript" src="js/jquery-1.12.0.min.js"></script>
			<script type="text/javascript" src="js/gop-stop.js"></script>
			<script>
				setCookie("chotki_patsan", "ok", 30);
				document.location.replace("/termobrest");
			</script>
		</xsl:if>
		
		<xsl:if test="not($success)">		
			<script type="text/javascript" src="js/jquery-1.12.0.min.js"></script>
			<script type="text/javascript" src="js/gop-stop.js"></script>
			<script type="text/javascript">
				$(document).ready(function(){
					$("body a").click(function(e){e.preventDefault(); alert(); return false;});
					var cookie = getCookie("chotki_patsan");
					if(typeof cookie == "undefined" || cookie == null){
						setCookie("chotki_patsan", "neh", 30);
					}
				});
			</script>
		
			<div class="spacer"></div>
			<div class="container main-content">
				<style type="text/css">
					.form-group:last-of-type{
						height:0;
						overflow: hidden;
						margin:0;
						padding:0;
					}
				</style>
				<div class="row">
					<div class="col-md-12">
						<h1>Пожалуйста заполните форму, чтобы продолжить работу с сайтом</h1>
						<form method="post" action="{$f/submit_link}">
							<div class="form-group">
								<label for="xi">ФИО:*</label>
								<input type="text" name="{$f/name/@input}" value="{$f/name}" class="form-control" id="xi"/>
							</div>
							<div class="form-group">
								<label for="xi">Контактный телефон:*</label>
								<input type="text" name="{$f/phone/@input}" value="{$f/phone}" class="form-control" id="xi"/>
							</div>
							<div class="form-group">
								<label for="xi">E-mail:*</label>
								<input type="text" name="{$f/email/@input}" value="{$f/email}" class="form-control" id="xi"/>
							</div>
							<div class="form-group">
								Для юридических лиц:
							</div>
							<div class="form-group">
								<label for="xi">Наименование организации:</label>
								<input type="text" name="{$f/organization/@input}" value="{$f/organization}" class="form-control" id="xi"/>
							</div>
							<div class="form-group">
								<label for="xi">Род деятельности организации:</label>
								<input type="text" name="{$f/field_of_work/@input}" value="{$f/field_of_work}" class="form-control" id="xi"/>
							</div>
							<div class="form-group">
								<label for="xi">Email организации:</label>
								<input type="text" name="{$f/spam/@input}" value="{$f/spam}" class="form-control" id="xi"/>
							</div>
							<button type="submit" class="btn btn-primary btn-block" onclick="$(this).closest('form').submit();">Отправить</button>
						</form>
					</div>
				</div>
			</div>
		</xsl:if>
	</xsl:template>
	
	
	<xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
	</xsl:template>
	
	</xsl:stylesheet>