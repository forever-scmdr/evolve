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
			<title>Результат интеграции</title><!-- ******************* TODO LOCAL ******************** -->
		<style>
			body{font-family: Consolas; padding-left: 40px;}
			table{border: 1px solid silver;}
			td{padding: 6px; font-size: 14px;}
			td{border: 1px solid silver;}
			.no{color: #dc0000; font-weight: bold;}
			.path{color: #0071bc;}
		</style>
			<script>
				function endsWith(str, suffix) {
					return str.indexOf(suffix, str.length - suffix.length) !== -1;
				}

				refreshTimeout = setTimeout(
						function () {
							h = document.location.href;
							h = h.replace("_start", "");
							h = h.replace("?action=start", "");
							document.location.replace(h);
						},
						5000
				);
				function toggleRefresh() {
					refresher = document.getElementById('refresher');
					if (refresher.classList.contains("clicked")) {
						h = document.location.href;
						h = h.replace("_start", "");
						h = h.replace("?action=start", "");
						document.location.replace(h);
					} else {
						clearTimeout(refreshTimeout);
						refresher.classList.add("clicked");
					}
				}
			</script>
		</head>
		<body>
			<h1><xsl:value-of select="/page/operation"/></h1>
			<h2>Процесс выполнения</h2>
			<table>
				<tr>
					<td colspan="2">
						<input type="button" id="refresher" value="выключить/включить обновление страницы" onclick="toggleRefresh();"/>
					</td>
				</tr>
				<tr>
					<td>Строка файла:</td>
					<td class="error">
						<span id="prcnt"></span>
						<span id="progressBar"></span>
						<xsl:value-of select="/page/line"/> / <xsl:value-of select="/page/total-line-number"/>
					</td>
				</tr>
				<tr>
					<td>Всего элементов для обработки:</td>
					<td class="error"><xsl:value-of select="/page/to_process"/></td>
				</tr>
				<tr>
					<td>Элементов обработано:</td>
					<td class="error"><xsl:value-of select="/page/processed"/></td>
				</tr>
				<tr><td colspan="2" align="center"><b>Хронология интеграции</b></td></tr>
				<xsl:for-each select="/page/message">
					<tr>
						<td class="string-no"><xsl:value-of select="@time"/></td>
						<td class="error"><xsl:value-of select="."/></td>
					</tr>
				</xsl:for-each>
				<xsl:for-each select="/page/error">
					<tr>
						<td class="string-no"><xsl:value-of select="@line"/></td>
						<td class="error"><xsl:value-of select="."/></td>
					</tr>
				</xsl:for-each>
			</table>
			<xsl:if test="/page/log">
				<h2>Сообщения</h2>
				<table>
					<xsl:for-each select="/page/log">
						<tr>
							<td class="string-no">
							Время: <span class="no"><xsl:value-of select="@time"/></span> 
							</td>
							<td class="error"><xsl:value-of select="." disable-output-escaping="yes"/></td>
						</tr>
					</xsl:for-each>
				</table>
			</xsl:if>
			
		</body>
		
		</html>
	</xsl:template>

</xsl:stylesheet>