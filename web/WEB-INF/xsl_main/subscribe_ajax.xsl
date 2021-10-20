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
				  select="if (page/product) then string-join((page/product/name, page/product/name_extra), ' ') else $form/input/product_name"/>
	<xsl:variable name="pcode"
				  select="if (page/product) then page/product/code else $form/input/product_code"/>

    <xsl:template match="/">
		<div class="result popup__body" id="subscribe_form">

			<div class="popup__content" style="text-align: center; width: 440px">
				<div class="popup__header">
					<div class="popup__title">Известить о наличии</div>
					<a class="popup__close">
						<img src="img/icon-menu-close.png" alt=""/>
					</a>
				</div>
				<div class="popup__info">
					<xsl:if test="$message and not($success)">
						<div class="alert alert-danger" style="background: #ffffcc; border: 1px solid #bb8; color: #bb0000;">
							<p><xsl:value-of select="$message"/></p>
						</div>
					</xsl:if>
					<xsl:if test="$message and $success">
						<div class="alert alert-success" style="background: #eeffee; border: 1px solid #9c9; color: #007700;">
							<p><xsl:value-of select="$message"/></p>
						</div>
					</xsl:if>
					<xsl:if test="not($success)">
						<form class="form" action="{page/submit_link}" method="post" ajax="true" ajax-loader-id="login_form">
							<div class="form__item form-item">
								<div class="form-item__label" style="margin-bottom: 0px">
									<div>Товар:</div>
								</div>
								<input class="input" value="{$pname}" readonly="readonly"/><br/>
								<input class="input" name="{$form/input/observable/@input}" value="{$pcode}" readonly="readonly" style="margin-top: 5px;"/>
							</div>
							<div class="form__item form-item">
								<div class="form-item__label" style="margin-bottom: 0px">
									<div>Email: </div>
								</div>
								<input class="input" name="{$form/input/observer/@input}" value="{$form/input/observer}" placeholder="name@server.com"/>
							</div>
							<button class="button button_big" type="submit" style="display: inline-block; position: relative; left: 50%; margin-left: -41px;">OK</button>
						</form>
					</xsl:if>
				</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="SUBSCRIBE_FORM">
		<div class="popup popup_basic" style="display: none;" id="modal-subscribe">
			<div class="popup__body" id="subscribe_form">
				<h3>SUBSCRIBE</h3>
			</div>
		</div>

    </xsl:template>

</xsl:stylesheet>