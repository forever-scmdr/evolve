<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:import href="_inc_message.xsl"/>
	<xsl:import href="_inc_params.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>


	<!--**************************************************************************-->
	<!--**************************    СТРАНИЦА    ********************************-->
	<!--**************************************************************************-->


	<xsl:template match="/">
	<xsl:call-template name="MESSAGE"/>
	<xsl:if test="$form">
		<hr></hr>
		<div class="forms">
			<!-- ************************ Основные (одиночные) параметры айтема **************************** -->
			<form id="iform_{$form/@id}" action="{$form/@action}" enctype="multipart/form-data" method="post">
				<xsl:for-each select="$form/hidden/field">
					<input type="hidden" name="{@input}" value="{@value}"/>
				</xsl:for-each>
				<input type="hidden" name="inl" value="true"/>
				<h2 class="title">Основные параметры</h2>
				<xsl:for-each select="$form/field[@quantifier='single']">
					<div class="form_item">
						<p class="form_title"><xsl:value-of select="@caption"/></p>
						<xsl:if test="@description != ''">
							<p class="form_comment">[<xsl:value-of select="@description"/>]</p>
						</xsl:if>
						<xsl:apply-templates select="." mode="single"/>
					</div>
				</xsl:for-each>	
				<input type="submit" value="Сохранить элемент"/>
			</form>
			<script>
			// Код вставляется в место с id = inline_@id
			prepareForm("iform_<xsl:value-of select="$form/@id"/>", "inline_<xsl:value-of select="$form/@id"/>", "hidden_mes", "message_main");
			</script>
		</div>
	</xsl:if>
	<xsl:call-template name="TINY_MCE"/>
	</xsl:template>
		
</xsl:stylesheet>