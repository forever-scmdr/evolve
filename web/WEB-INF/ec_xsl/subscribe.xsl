<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="success" select="page/variables/result = 'success'"/>
	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="f" select="page/form"/>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:variable name="subs_ajax_id" select="'subscribe_form'"/>

	<xsl:template match="/">
		<div class="result" id="{$subs_ajax_id}">
			<xsl:if test="not($success)">
				<xsl:if test="$message">
					<p style="color: red">
						<xsl:value-of select="$message" />
					</p>
				</xsl:if>
				<form method="post" action="{$f/submit_link}">
					<div class="form-group">
						<label>E-mail:</label>
						<input type="email" name="{$f/email/@input}" value="{$f/email}" class="form-control" id="xi"/>
					</div>
					<div class="form-group mandatory-input" title="обязательно для заполнения" style="display: none;">
						<label>Дополнительный E-mail:*</label>
						<input type="text" name="{$f/spam/@input}" value="{$f/spam}" class="form-control" />
					</div>
					<p>Что вам интересно?</p>
					<xsl:for-each select="/page/news_section">
						<div class="checkbox">
							<label>
								<input type="checkbox" name="{$f/tags/@input}" value="{name}"/><xsl:value-of select="name" />
							</label>
						</div>
					</xsl:for-each>
					<button type="submit" class="btn btn-primary btn-block" onclick="postForm($(this).closest('form'), '{$subs_ajax_id}'); return false;">Подписаться</button>
					<button type="button" class="btn btn-info btn-block" onclick="$(this).closest('form').attr('action', '{//dismiss_link}'); postForm($(this).closest('form'), '{$subs_ajax_id}'); return false;">Отписаться от всего</button>
				</form>
			</xsl:if>
			<xsl:if test="$success">
				<p>
					<xsl:value-of select="$message" />
				</p>
			</xsl:if>
		</div>

	</xsl:template>
	
	<xsl:template name="SUBSCRIBE_FORM">
	<div class="modal fade" id="subscribe" tabindex="-1">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span>×</span>
					</button>
					<h4 class="modal-title">Рассылка</h4>
				</div>
				<div class="modal-body" id="{$subs_ajax_id}">

				</div>
			</div>
		</div>
	</div>
	<script>
		function getSubscribeForm() {
			insertAjax('<xsl:value-of select="concat('https://termobrest.ru/', page/subscribe_link)"/>', '<xsl:value-of select="$subs_ajax_id"/>', function() { <!--initSelects();--> });
		}

		$(document).ready(function() {
			getSubscribeForm();
		});
		
	</script>
	</xsl:template>

	<xsl:template name="SUBSCRIBE_BUTTON">
		<xsl:param name="block"/>
		<xsl:variable name="spec_name" select="page/footer/footer_link[code = '2']"/>
		<a onclick="getSubscribeForm()" class="btn btn-sm btn-success{' btn-block'[$block]}" type="button" data-toggle="modal" data-target="#subscribe">
			<xsl:value-of select="if ($spec_name) then $spec_name/name else 'Подписаться на новости'"/>
		</a>
	</xsl:template>

</xsl:stylesheet>