<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns="http://www.w3.org/1999/xhtml"
        xmlns:f="f:f"
        version="2.0">

    <xsl:import href="utils/price_conversions.xsl"/>
        
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes" exclude-result-prefixes="#all"/>

	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="success" select="page/variables/result = 'success'"/>
	<xsl:variable name="form" select="page/form"/>

	<xsl:variable name="pname"
	              select="if (page/variables/pname) then page/variables/pname else $form/input/product_name"/>
	<xsl:variable name="pcode"
	              select="if (page/variables/pcode) then page/variables/pcode else $form/input/product_code"/>
	<xsl:variable name="price"
	              select="if (page/variables/price) then page/variables/price else $form/input/price"/>              

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
							<form action="{page/submit_link}" method="post" ajax="true" ajax-loader-id="modal-one_click" id="one-click-form">
								<div class="form-group">
									<label>Телефон:</label>
									<input type="text" id="one-click-phone"  class="form-control" name="{$form/input/phone/@input}" value="{$form/input/phone}"/>
								</div>
								<div class="form-group">
									<label>Имя:</label>
									<input type="text" id="one-click-name" class="form-control" name="{$form/input/name/@input}" value="{$form/input/name}"/>
								</div>
								<div class="form-group">
									<label>Товар:</label>
									<input type="text" class="form-control"
									       name="{$form/input/product_name/@input}" value="{$pname}" readonly="readonly"/><br/>
									<label>Артикул:</label>      
									<input type="text" class="form-control"
									       name="{$form/input/product_code/@input}" value="{$pcode}" readonly="readonly"/><br/>
									<label>Цена:</label>
									<input type="text" class="form-control"
									       name="{$form/input/price/@input}" value="{format-number(f:num($price), '#0.00')} руб." readonly="readonly"/>
								</div>
								<input name="topic" value="{concat($pname, '. Арт: ', $pcode)}" type="hidden"/>
								<input type="submit" value="Заказать"/>
							</form>
						</xsl:if>
					</div>
				</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="ONE_CLICK_FORM">
		<div class="modal fade" tabindex="-1" role="dialog" id="modal-one_click">
		+++
		</div>
	</xsl:template>

</xsl:stylesheet>