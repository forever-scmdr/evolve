<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;
		</xsl:text>
	</xsl:template>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:template match="/">
		<xsl:call-template name="DOCTYPE"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
			<title>Результат разбора файла</title><!-- ******************* TODO LOCAL ******************** -->
		<style>
			body{font-family: Consolas; padding-left: 40px;}
			table{border: 1px solid silver;}
			td{padding: 6px; font-size: 14px;}
			td{border: 1px solid silver;}
			.no{color: #dc0000; font-weight: bold;}
			.path{color: #0071bc;}
		</style>
		<script>
			setTimeout(function(){
				locatus = document.location.href;
				idx = locatus.indexOf('?');
				locatus = locatus.split('?')[0];
				//locatus = locatus.replace("?action=start", "");
				document.location.replace(locatus);
			}, 5000);
		</script>
		</head>
		<body>
			<h1><xsl:value-of select="/page/operation"/></h1>
			<h2>Процесс выполнения</h2>
			<table>
				<tr>
					<td>Строка файла:</td>
					<td class="error">
						<span id="prcnt"></span>
						<span id="progressBar"></span>
						<span><xsl:value-of select="sheet"/></span>
						<xsl:value-of select="/page/line"/> / <xsl:value-of select="/page/total-line-number"/>
					</td>
				</tr>
				<tr>
					<td>Создано разделов (классов):</td>
					<td class="error"><xsl:value-of select="/page/sections-created"/></td>
				</tr>
				<tr>
					<td>Обработано товаров:</td>
					<td class="error"><xsl:value-of select="/page/products-created"/></td>
				</tr>
				<tr>
					<td>Проиндексировано товаров:</td>
					<td class="error"><xsl:value-of select="/page/items-indexed"/></td>
				</tr>
				<tr><td colspan="2" align="center"><b>Хронология интеграции</b></td></tr>
				<xsl:for-each select="/page/message">
					<tr>
						<td class="string-no"><xsl:value-of select="@time"/></td>
						<td class="error"><xsl:value-of select="."/></td>
					</tr>
				</xsl:for-each>
			</table>
			<xsl:if test="/page/error">
				<h2>ошибки выполнения интеграции</h2>
				<table>
					<xsl:for-each select="/page/error">
						<tr>
							<td class="string-no">
							Лист: <span class="no"><xsl:value-of select="@sheet"/></span>
							Строка: <span class="no"><xsl:value-of select="@line"/></span>
							Позиция: <span class="no"><xsl:value-of select="@coloumn"/></span>
							</td>
							<td class="error"><xsl:value-of select="."/></td>
						</tr>
					</xsl:for-each>
				</table>
			</xsl:if>
			
		</body>
		
		</html>
	</xsl:template>

</xsl:stylesheet>