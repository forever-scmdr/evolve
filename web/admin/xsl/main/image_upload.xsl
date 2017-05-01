<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="/">
	<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
	<html xmlns="http://www.w3.org/1999/xhtml">
		<head>
			<style type="text/css">
				*{font-family: Arial;font-size: 14px;}
				body{padding: 10px;}
				label{display: block; margin-bottom: 15px; width: 400px;}
				label input{float: right; width: 200px;}
			</style>
		</head>
		<body>
		<form action="{admin-page/upload-link}" id="uploadForm" enctype="multipart/form-data" method="post">
			<label>
				Загрузите файл:
				<input type="file" name="multipleParamValue" id="file" multiple="multiple"/>
			</label>
			<label>
				Описание:
				<input type="text" id="alt" name="alt" />
			</label>
			<label>
				Ширина:
				<input type="number" id="width" name="width"/>
			</label>
			<label>
				Высота:
				<input type="number" id="height" name="height"/>
			</label>
			<input type="submit" value="загрузить"/>
		</form>	
		<script type="text/javascript" src="upload_handler.js"></script>
		</body>
	</html>
	</xsl:template>
	
</xsl:stylesheet>