<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp "&#160;"><!ENTITY copy "&#x000A9;" >]>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:ext="http://exslt.org/common"
		xmlns="http://www.w3.org/1999/xhtml"
		version="2.0"
		xmlns:f="f:f"
		exclude-result-prefixes="xsl ext">
	<xsl:import href="utils/price_conversions.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>

	<xsl:variable name="is_jur" select="not(page/user_jur/input/organization = '')"/>
	<xsl:variable name="is_phys" select="not($is_jur)"/>
	<xsl:variable name="cart" select="page/cart"/>
	<xsl:variable name="contacts" select="if ($is_jur) then page/user_jur/input else page/user_phys/input"/>
	<xsl:variable name="main_host" select="if(page/url_seo_wrap/main_host != '') then page/url_seo_wrap/main_host else page/base" />

	<xsl:variable name="registration" select="page/registration[f:num(@id) &gt; 0]"/>
	<xsl:variable name="is_reg_jur" select="$registration/@type = 'user_jur'"/>



	<xsl:template match="/">
		<html>
			<head>
				<meta charset="UTF-8"/>
				<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
				<meta http-equiv="X-UA-Compatible" content="ie=edge"/>
				<title>Document</title>
			</head>
			<body>
				<p>Dear <xsl:value-of select="string-join((//status, //name, //surname), ' ')"/></p>
				<p>Hello and thank you for your email. Our assistant will read your message and contact you within the next 48 hours providing you with all the details that you have requested.</p>
				<p><strong>Your request information</strong></p>
				<ol>
					<li>
						<strong>phone: </strong><xsl:value-of select="//phone"/>
					</li>
					<li>
						<strong>subject: </strong><xsl:value-of select="//topic"/>
					</li>
					<xsl:if test="//nationality != ''">
						<li>
							<strong>nationality: </strong>
							<xsl:value-of select="//nationality"/>
						</li>
					</xsl:if>
					<li>
						<strong>country of stay: </strong>
						<xsl:value-of select="//country"/>
					</li>
					<li>
						<strong>message: </strong>
						<blockquote><xsl:value-of select="//message"/></blockquote>
					</li>
				</ol>
				<p>We wish you all the best and appreciate your interest in MHR GI services!</p>
				<p>Kind regards, <br/>MHR GI team</p>
				<img src="https://mhr-gi.net/img/logo.png" alt="" />
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>