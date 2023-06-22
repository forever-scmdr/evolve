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
			<div class="result" id="callback_ajax" style="display:block; font-size:14px;">
				<xsl:if test="$success">
					<h2 style="font-size: 20px; margin-bottom: 12px;">Звонок заказан. Наш менеджер вскоре Вам перезвонит</h2>
					<a onclick="insertAjax('callback_form/')" style="text-decoration: underline; cursor: pointer;">Отправить сообщение еще раз</a>
				</xsl:if>
				<xsl:if test="not($success)">
					<xsl:if test="$message">
						<p><b style="color: {if (page/variables/result = 'error') then 'red' else 'green'}"><xsl:value-of select="$message"/></b></p>
					</xsl:if>
					<form action="{$f/submit_link}" method="post" id="callback_form">
						<div class="form-group">
							<label for="">Ваше имя:</label>
							<input type="text" name="{$f/name/@input}" value="{$f/name}" class="form-control" id="" placeholder=""/>
						</div>
						<div class="form-group">
							<label for="">Телефон:</label>
							<input type="text" name="{$f/phone/@input}" value="{$f/phone}" class="form-control" id="" placeholder=""/>
						</div>
						<button type="submit" class="btn btn-primary" onclick="postFormAjax('callback_form', 'callback_ajax'); return false;">Заказать звонок</button>
						<input type="text" name="company" style="display:none;"/>
					</form>
				</xsl:if>
			</div>
		</div>
	</xsl:template>


	<xsl:template name="CALLBACK_MODAL">
		<xsl:param name="callback_link"/>
		<div class="modal fade" id="modal-callback" tabindex="-1" role="dialog">
			<div class="modal-dialog modal-md" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span></button>
						<h4 class="modal-title">Заказать обратный звонок</h4>
					</div>
					<div class="modal-body" id="callback_ajax">

					</div>
				</div>
			</div>
		</div>
		<script>
			$(document).ready(function() {
				insertAjax('<xsl:value-of select="$callback_link"/>');
			});
		</script>
	</xsl:template>

</xsl:stylesheet>