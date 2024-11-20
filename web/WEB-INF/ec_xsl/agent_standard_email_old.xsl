<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY rarr "&#x02192;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">

	<xsl:variable name="text" select="page/email_post" />
	<xsl:variable name="base" select="page/base" />

	<xsl:template match="/">
	<body width="100%" bgcolor="#222222" style="Margin: 0;">
		<style type="text/css">
		        html,
		        body {
			        margin: 0 auto !important;
		            padding: 0 !important;
		            height: 100% !important;
		            width: 100% !important;
		        }
		        * {
		            -ms-text-size-adjust: 100%;
		            -webkit-text-size-adjust: 100%;
		        }
		        div[style*="margin: 16px 0"] {
		            margin:0 !important;
		        }
		        table,
		        td {
		            mso-table-lspace: 0pt !important;
		            mso-table-rspace: 0pt !important;
		        }
		        table {
		            border-spacing: 0 !important;
		            border-collapse: collapse !important;
		            table-layout: fixed !important;
		            Margin: 0 auto !important;
		        }
		        table table table {
		            table-layout: auto;
		        }
		        img {
		            -ms-interpolation-mode:bicubic;
		        }
		        .mobile-link--footer a,
		        a[x-apple-data-detectors] {
		            color:inherit !important;
		            text-decoration: underline !important;
		        }
		</style>
		<style>
		        .button-td,
		        .button-a {
		            transition: all 100ms ease-in;
		        }
		        .button-td:hover,
		        .button-a:hover {
		            background: #ED1C24 !important;
		            border-color: #ED1C24 !important;
		        }
		</style>
		<center style="width: 100%; background: #e6e6e6;">
		<div style="display:none;font-size:1px;line-height:1px;max-height:0px;max-width:0px;opacity:0;overflow:hidden;mso-hide:all;font-family: sans-serif;">
			<topic><xsl:value-of select="$text/topic"/></topic>
		</div>
		<div style="max-width: 600px; margin: auto;">
			<xsl:text disable-output-escaping="yes">
			&lt;!--[if (gte mso 9)|(IE)]&gt;
		            &lt;table cellspacing="0" cellpadding="0" border="0" width="600" align="center"&gt;
		            &lt;tr&gt;
		            &lt;td&gt;
		            &lt;![endif]--&gt;
			</xsl:text>
			<table cellspacing="0" cellpadding="0" border="0" align="center" width="100%" style="max-width: 600px;">
			<tr>
				<td style="padding: 20px 0; text-align: center">
					<img src="intra_img/termobrest_logo.png" width="201" height="75" alt="Термобрест" border="0"/>
				</td>
			</tr>
			</table>
			<table cellspacing="0" cellpadding="0" border="0" align="center" bgcolor="#ffffff" width="100%" style="max-width: 600px;">
			<tr>
				<td>
					<img src="{$text/@path}{$text/main_pic}" width="600" height="" alt="alt_text" border="0" align="center" style="width: 100%; max-width: 600px;"/>
				</td>
			</tr>
			<tr>
				<td>
					<table cellspacing="0" cellpadding="0" border="0" width="100%">
					<tr>
						<td style="padding: 40px; font-family: sans-serif; font-size: 15px; mso-height-rule: exactly; line-height: 20px; color: #555555;">
							<h2 style="line-height: 25px;"><xsl:value-of select="$text/header"/></h2>
							<xsl:value-of select="$text/body" disable-output-escaping="yes"/>
						</td>
					</tr>
					</table>
				</td>
			</tr>
			</table>
			<table cellspacing="0" cellpadding="0" border="0" align="center" width="100%" style="max-width: 680px;">
			<tr>
				<td style="padding: 40px 10px;width: 100%;font-size: 12px; font-family: sans-serif; mso-height-rule: exactly; line-height:18px; text-align: center; color: #888888;">
					 СП «ТермоБрест» ООО<br/>
					<span class="mobile-link--footer">Республика Беларусь, 224014,<br/>
					г. Брест, ул. Писателя Смирнова, 168</span>
					<br/>
					<br/>
<!-- 					<unsubscribe style="color:#888888; text-decoration:underline;"><a style="color: inherit;" href="">отписаться от рассылки</a></unsubscribe> -->
				</td>
			</tr>
			</table>
			<xsl:text disable-output-escaping="yes">
			&lt;!--[if (gte mso 9)|(IE)]&gt;
		            &lt;/td&gt;
		            &lt;/tr&gt;
		            &lt;/table&gt;
		            &lt;![endif]--&gt;
			</xsl:text>
		</div>
	</center>
	</body>
	</xsl:template>

</xsl:stylesheet>