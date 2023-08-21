<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="/">
	<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
	<html xmlns="http://www.w3.org/1999/xhtml">
		<head>
			<!--  <script type="text/javascript"
				src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
			<script type="text/javascript">
				window.jQuery || document.write("<script src='jquery-1.9.1.js'><\x3C/script>");
			</script> -->
			<style type="text/css">
				*{font-family: Arial;font-size: 14px;}
				body{padding: 10px;}
				label{display: block; margin-bottom: 15px; width: 400px;}
				label input{float: right; width: 200px;}
			</style>
		</head>
		<body>
			
		<!-- SUCCESS -->
		<p style="font-size: 18px;">Картинка добавлена.</p>
		<p><a href="{admin-page/upload-link}">добавить еще</a></p>
		<script type="text/javascript">
			var currentEditor = top.tinymce.activeEditor;
			<xsl:for-each select="admin-page/path">
				var picture<xsl:value-of select="position"/> = 
				"<xsl:text disable-output-escaping="yes">&lt;</xsl:text>img src='sitefiles/<xsl:value-of select="."/>' alt='<xsl:value-of select="@alt"/>' /<xsl:text disable-output-escaping="yes">&gt;</xsl:text>";
				currentEditor.execCommand('mceInsertContent', false, picture<xsl:value-of select="position"/>);
			</xsl:for-each>
		</script>
		<!--  -->
			
			
		<!-- ERROR 
		<p  style="font-size: 18px; color: #900000;">Не удалось загрузить изображение.</p>
		<p>[Описание ошибки]</p>
		<p><a href="page.htm">попробовать еще раз...</a></p>
		  -->
			
		</body>
	</html>
	</xsl:template>
	
</xsl:stylesheet>