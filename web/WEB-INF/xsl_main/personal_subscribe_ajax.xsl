<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f"
		version="2.0">
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes" exclude-result-prefixes="#all"/>


    <xsl:template match="/">
		<xsl:for-each select="page/observer">
			<div class="result popup__body" id="subs_{observable}">
				<span style="font-size: 12px; font-weight: 400">&#128276;</span>&#160;<a href="{delete_link}" style="color: grey" ajax="true">Снять уведомление</a>
			</div>
		</xsl:for-each>
		<xsl:for-each select="page/deleted">
			<div class="result popup__body" id="subs_{code}">
				<div id="subs_{code}"><a href="{subscribe_link}" rel="nofollow" popup="modal-subscribe">Уведомить о поступлении</a></div>
			</div>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="PERSONAL_SUBSCRIBE_SCRIPT">
		<script>
			$(document).ready(function() {
				insertAjax('<xsl:value-of select="page/personal_subscribe_ajax_link" />');
			});
		</script>
    </xsl:template>

</xsl:stylesheet>