<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY rarr "&#x02192;" >]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">

	<xsl:variable name="text" select="page/email_post" />
	<xsl:variable name="base" select="page/base" />

	<xsl:template match="/">
	<body style="background-color: #E6E6E6; margin: 0;">
		<div style="display:none;font-size:1px;line-height:1px;max-height:0px;max-width:0px;opacity:0;overflow:hidden;mso-hide:all;font-family: sans-serif;">
			<topic><xsl:value-of select="$text/topic"/></topic>
		</div>
		<table cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td align="center" style="background-color: #E6E6E6;">
					<div style="max-width: 600px; width: 600px; margin: auto; background-color: #fff; font-family: Arial, 'Helvetica Neue', Helvetica, sans-serif; font-size: 13px;">
						<table style="background-color: #fff;" cellspacing="0" cellpadding="0" width="600">
							<tr>
								<td style="padding: 0; font-size: 0;">
									<img src="img/header_2.jpg" alt=""/>
								</td>
							</tr>
						</table>
						<xsl:value-of select="$text/body" disable-output-escaping="yes"/>
						<table cellspacing="0" cellpadding="0" width="600" style="background-color: #2F2F2F; color: #fff;">
							<tr>
								<td style=" padding: 20px 15px;"><h4 style="margin-top: 0;">СП «ТермоБрест» ООО</h4></td>
							</tr>
						</table>
						<table cellspacing="0" cellpadding="0" width="600" style="background-color: #2F2F2F; color: #939393;">
							<tr>
								<td width="400" style="width: 400px; vertical-align: top; padding: 0 15px 30px;">224014 Республика Беларусь, г.&nbsp;Брест, ул.&nbsp;Писателя Смирнова, 168</td>
								<td width="200" style="width: 200px; vertical-align: top; padding: 0 15px; text-align: right;">
									<a href="https://termobrest.ru"><img src="img/icon_site.jpg" alt=""/></a>
									<a href="https://www.youtube.com/channel/UC0gvt4On84SIDHkMO4Y85cA"><img src="img/icon_yt.jpg" alt=""/></a>
									<a href="https://www.facebook.com/termobrestvalves/"><img src="img/icon_fb.jpg" alt=""/></a>
								</td>
							</tr>
						</table>
						<table style="background-color: #e6e6e6;" cellspacing="0" cellpadding="0" width="600">
							<tr>
								<td width="200" valign="top" style="width: 200px; padding: 30px 15px; font-size: 13px">
									<p><strong>Приемная&nbsp;ТЕРМОБРЕСТ:</strong></p>
									<p>
										+375 (162) 53-63-90;
									</p>
									<p><a style="color: black" href="info@termobrest.ru">info@termobrest.ru</a></p>
									
								</td>
								<td width="200" valign="top" style="width: 200px; padding: 30px 15px; font-size: 13px">
									<p><strong>Отдел маркетинга:</strong></p>
									<p>
										+375 (162) 53-64-70;
									</p>
									<p><a style="color: black" href="sproject@termobrest.ru">sproject@termobrest.ru</a></p>
									
								</td>
								<td width="200" valign="top" style="width: 200px; padding: 30px 15px; font-size: 13px">
									<p><strong>Техническая поддержка:</strong></p>
									<p>
										+375 (162) 53-64-13;<br/>
									</p>
									<p><a style="color: black" href="konstruktor@termobrest.ru">konstruktor@termobrest.ru</a></p>
								</td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
		</table>
	</body>
	</xsl:template>

</xsl:stylesheet>