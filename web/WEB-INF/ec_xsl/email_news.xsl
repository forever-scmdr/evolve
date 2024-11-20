<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY rarr "&#x02192;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">

	<xsl:variable name="info" select="page/news_section" />
	<xsl:variable name="base" select="'https://termobrest.ru/'" />

	<xsl:template match="/">
		<div>
			<table style="width:950px; font-family: calibri;">
				<tbody>
					<tr>
						<td style="background-image: url('{$base}images/top.png'); background-position: 50% 50%; background-repeat: repeat-x; background-size: contain;">
							<a href="{$base}/{page/index_link}">
								<img alt="СП «Термобрест» ООО" src="{$base}images/termo_logo.jpg" />
								
							</a>
						</td>
					</tr>
					<tr>
						<td >
							<h1>
								<xsl:value-of select="$info/name" />
							</h1>
						</td>
					</tr>
					<xsl:for-each select="$info/news_item">

					<tr>
						<td>
							<h2>
								<a href="{$base}{show_news_item}" target="_blank"><xsl:value-of select="header" /></a>
							</h2>
							<xsl:value-of select="short" disable-output-escaping="yes" />
						</td>
					</tr>
					</xsl:for-each>
					<tr>
						<td >
							<p>
								<a href="{$base}{page/refuse_link}">Отказаться от рассылки</a>
							</p>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</xsl:template>

</xsl:stylesheet>