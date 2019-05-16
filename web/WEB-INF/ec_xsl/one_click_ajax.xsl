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
	<xsl:variable name="form" select="page/form"/>

	<xsl:variable name="pname"
	              select="if (page/variables/pname) then page/variables/pname else $form/input/product_name"/>
	<xsl:variable name="pcode"
	              select="if (page/variables/pcode) then page/variables/pcode else $form/input/product_code"/>

    <xsl:template match="/">
		<div class="result" id="modal-one_click">
			<div class="modal-dialog modal-sm" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">❌</span></button>
						<h4 class="modal-title">Купить в один клик</h4>
					</div>
					<div class="modal-body">
						<xsl:if test="$message">
							<div class="alert alert-{if ($success) then 'success' else 'danger'}">
								<xsl:value-of select="$message"/>
							</div>
						</xsl:if>
						<xsl:if test="not($success)">
							<form action="{page/submit_link}" method="post" ajax="true" ajax-loader-id="modal-one_click">
								<div class="form-group">
									<label>Товар:</label>
									<input type="text" class="form-control"
									       name="{$form/input/product_name/@input}" value="{$pname}" readonly="readonly"/><br/>
									<input type="text" class="form-control"
									       name="{$form/input/product_code/@input}" value="{$pcode}" readonly="readonly"/>
								</div>
								<div class="form-group">
									<label>Телефон:</label>
									<input type="text" class="form-control" name="{$form/input/phone/@input}" value="{$form/input/phone}"/>
								</div>
								<input type="submit" value="Отправить сообщение"/>
							</form>
						</xsl:if>
					</div>
				</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="ONE_CLICK_FORM">
		<div class="modal fade" tabindex="-1" role="dialog" id="modal-one_click"></div>
    </xsl:template>

</xsl:stylesheet>