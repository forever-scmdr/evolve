<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f"
		version="2.0">

	<xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;
		</xsl:text>
	</xsl:template>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:template match="/">
		<xsl:call-template name="DOCTYPE"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<base href="http://alfacomponent.com"/>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
				<title>Результат разбора файла</title><!-- ******************* TODO LOCAL ******************** -->
			</head>
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
			<body>
				<h1><xsl:value-of select="/page/operation"/></h1>
				<h2>Процесс выполнения</h2>
				<xsl:if test="/page/error">
					<h2>ошибки выполнения интеграции</h2>
					<table>
						<xsl:for-each select="/page/error">
							<tr>
								<td class="string-no">
									Строка: <span class="no"><xsl:value-of select="format-number(f:num(@line), '### ###', 'r')"/></span>
									Позиция: <span class="no"><xsl:value-of select="format-number(f:num(@coloumn), '### ###', 'r')"/></span>
								</td>
								<td class="error"><xsl:value-of select="."/></td>
							</tr>
						</xsl:for-each>
					</table>
				</xsl:if>
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
							<xsl:value-of select="format-number(f:num(/page/line), '### ###', 'r')"/> / <xsl:value-of select="format-number(f:num(/page/total-line-number), '### ###', 'r')"/>
						</td>
					</tr>
					<tr>
						<td>Создано разделов (классов):</td>
						<td class="error"><xsl:value-of select="format-number(f:num(/page/to_process), '### ###', 'r')"/></td>
					</tr>
					<tr>
						<td>Обработано товаров:</td>
						<td class="error"><xsl:value-of select="format-number(f:num(/page/processed), '### ###', 'r')"/></td>
					</tr>
					<tr>
						<td>Проиндексировано товаров:</td>
						<td class="error"><xsl:value-of select="format-number(f:num(/page/items-indexed), '### ###', 'r')"/></td>
					</tr>
					<tr><td colspan="2" align="center"><b>Хронология интеграции</b></td></tr>
					<xsl:for-each select="/page/message">
						<tr>
							<td class="string-no"><xsl:value-of select="@time"/></td>
							<td class="error"><xsl:value-of select="."/></td>
						</tr>
					</xsl:for-each>
					<xsl:if test="page/message = 'Интеграция в данный момент не выполняется. Результаты предыдущей интеграции ниже'">
						<xsl:variable name="file_name" select="page/log[starts-with(., 'pricelist-')]"/>
						Скачать файл: <a href="files/{$file_name}"><xsl:value-of select="$file_name" /></a>
					</xsl:if>
					<tr>
						<td colspan="2">
							Журнал опреации:
						</td>
					</tr>
					<xsl:for-each select="/page/log">
						<tr>
							<td class="string-no"><xsl:value-of select="@time"/></td>
							<td class="error"><xsl:value-of select="."/></td>
						</tr>
					</xsl:for-each>
				</table>
			</body>
		</html>
	</xsl:template>

	<xsl:decimal-format name="r" grouping-separator=" "/>
	<xsl:function name="f:num" as="xs:double">
		<xsl:param name="str" as="xs:string?"/>
		<xsl:sequence
				select="if ($str and $str != '') then number(replace(replace($str, '[&#160;\s]', ''), ',', '.')) else number(0)"/>
	</xsl:function>
</xsl:stylesheet>