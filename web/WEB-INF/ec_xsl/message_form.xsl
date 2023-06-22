<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	version="1.0">
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="serv" select="/page/service"/>

	<xsl:variable name="success" select="page/variables/result = 'success'"/>
	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="f" select="page/form"/>


	<xsl:template match="/">
		<div>
			<div class="result" id="message_ajax" style="display:block; font-size:14px;">
				<xsl:if test="$success">
					<h2 style="font-size: 20px; margin-bottom: 12px;">Спасибо за Ваше сообщение</h2>
					<a onclick="insertAjax('message_form/')" style="text-decoration: underline; cursor: pointer;">Написать еще сообщение</a>
				</xsl:if>
				<xsl:if test="not($success)">
					<xsl:if test="$message">
						<p><b style="color: {if (page/variables/result = 'error') then 'red' else 'green'}"><xsl:value-of select="$message"/></b></p>
					</xsl:if>
					<form action="{$f/submit_link}" method="post" id="message_form">
						<div class="form-group">
							<label for="">Ваше имя:</label>
							<input type="text" name="{$f/name/@input}" value="{$f/name}" class="form-control" id="" placeholder=""/>
						</div>
						<div class="form-group">
							<label for="">Адрес электронной почты или телефон:</label>
							<input type="text" name="{$f/phone/@input}" value="{$f/phone}" class="form-control" id="" placeholder=""/>
						</div>
						<div class="form-group">
							<label for="">Сообщение:</label>
							<textarea class="form-control" rows="3" name="{$f/message/@input}"><xsl:value-of select="$f/message"/></textarea>
						</div>
						<button type="submit" class="btn btn-primary" onclick="postFormAjax('message_form', 'message_ajax'); return false;">Отправить сообщение</button>
						<input type="text" name="company" style="display:none;"/>
					</form>
				</xsl:if>
			</div>
		</div>
	</xsl:template>


	<xsl:template name="MESSAGE_MODAL">
		<xsl:param name="message_link"/>
		<div class="modal fade" id="modal-message" tabindex="-1" role="dialog">
			<div class="modal-dialog modal-md" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
						<h4 class="modal-title">Написать сообщение</h4>
					</div>
					<div class="modal-body" id="message_ajax">

					</div>
				</div>
			</div>
		</div>
		<script>
			$(document).ready(function() {
				insertAjax('<xsl:value-of select="$message_link"/>');
			});
		</script>
	</xsl:template>

</xsl:stylesheet>