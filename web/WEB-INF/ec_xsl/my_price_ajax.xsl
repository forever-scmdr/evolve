<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
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

	<xsl:variable name="my_price" select="page/my_price"/>

	<xsl:template match="/">
		<div class="result" id="modal-my_price">
			<div class="modal-dialog modal-sm" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">❌</span></button>
						<h4 class="modal-title"><xsl:value-of select="$my_price/name"/></h4>
					</div>
					<div class="modal-body">
						<xsl:if test="$message">
							<div class="alert alert-{if ($success) then 'success' else 'danger'}">
								<xsl:value-of select="$message"/>
							</div>
						</xsl:if>
						<xsl:if test="not($success)">
							<form action="{page/submit_link}" method="post" ajax="true" ajax-loader-id="modal-my_price">
								<div class="form-group">
									<label>Товар:</label>
									<input type="text" class="form-control"
									       name="{$form/input/product_name/@input}" value="{$pname}" readonly="readonly"/><br/>
									<input type="text" class="form-control"
									       name="{$form/input/product_code/@input}" value="{$pcode}" readonly="readonly"/>
								</div>
								<div class="form-group">
									<label>Ваше имя:</label>
									<input type="text" class="form-control" name="{$form/input/name/@input}" value="{$form/input/name}"/>
								</div>
								<div class="form-group">
									<label>Ваша цена:</label>
									<input type="text" class="form-control" name="{$form/input/price/@input}" value="{$form/input/price}"/>
								</div>
								<div class="form-group">
									<label>Телефон:</label>
									<input type="text" class="form-control" name="{$form/input/phone/@input}" value="{$form/input/phone}"/>
								</div>
								<div class="form-group">
									<label>Электронная почта:</label>
									<input type="text" class="form-control" name="{$form/input/email/@input}" value="{$form/input/email}"/>
								</div>
								<div class="form-group">
									<label>Комментарий:</label>
									<textarea class="form-control" rows="3"
									          name="{$form/input/message/@input}"><xsl:value-of select="$form/input/message"/></textarea>
								</div>
								<input type="submit" value="Отправить сообщение"/>
							</form>
						</xsl:if>
					</div>
				</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="MY_PRICE_FORM">
		<div class="modal fade" tabindex="-1" role="dialog" id="modal-my_price">
		+++
		</div>
	</xsl:template>

</xsl:stylesheet>