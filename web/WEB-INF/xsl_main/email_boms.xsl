<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f" version="2.0">
	<xsl:import href="utils/utils.xsl" />
	<xsl:template name="TITLE" />
	<xsl:variable name="cart" select="page/cart" />
	<xsl:variable name="base" select="page/base" />

	
	<xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;
		</xsl:text>
	</xsl:template>
	
	
	<xsl:template match="/">
		<xsl:call-template name="DOCTYPE" />
		<html>
			<head>
				<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>
				<base href="{page/base}" />
				<title>Заказ от: <xsl:value-of select="f:format_date(current-date())" /></title>
				<style>		
			        @page {
			            size: A4 portrait;
			        }
			
			        @media print {
			            .new_page {
			                page-break-after: always;
			            }
			        }			
					*{ font-family: 'Arial Unicode MS';}
					table, td{border-collapse: collapse; cell-padding:0; cell-spacing:0; font-size: 12px;}
					td{border: 1px solid #bbb; padding: 10px 5px; vertical-align: middle;}
					.name{width: 80%;}
					.pheader{font-style: normal; font-size: 40;}
				</style>
			</head>
			<body>
				<div>
					<h1>
						<b>Список BOM-листов</b>
					</h1>
					<p>Онлайн-площадка <a href="{page/base}">partnumber.ru</a></p>
					<xsl:for-each select="page/bom_list">
						<h2><xsl:value-of select="name" /></h2>
						<p><xsl:value-of select="description" /></p>
						<table>
							<tr>
								<td class="name">Парнтомер</td>
								<td>Кол-во</td>
							</tr>
							<xsl:for-each select="line">
								<tr>
									<td><xsl:value-of select="@key" /></td>
									<td><xsl:value-of select="@value" /></td>
								</tr>
							</xsl:for-each>
						</table>
					</xsl:for-each>
				</div>
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>