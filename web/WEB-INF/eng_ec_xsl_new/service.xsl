<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	version="1.0">
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="serv" select="/page/service"/>

	<xsl:template match="/">
	<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;
	</xsl:text>
	<head>
		<title>Санаторий «Спутник»</title>
		<base href="http://test6.forever-ds.com"/>
		<link rel="stylesheet" type="text/css" href="css/main.css" />
		<link rel="stylesheet" type="text/css" href="nivo-slider/nivo-slider.css" />
		<link rel="stylesheet" type="text/css" href="js/fotorama/fotorama.css" />
		<link rel="stylesheet" href="nivo-slider/themes/default/default.css" type="text/css" media="screen" />
		<xsl:text disable-output-escaping="yes">
		&lt;!--[if lte IE 8]&gt;
		  &lt;link rel="stylesheet" type="text/css" href="css/ie.css" /&gt;
		&lt;![endif]--&gt;
		&lt;!--[if lte IE 7]&gt;
			 &lt;link rel="stylesheet" type="text/css" href="css/ie7.css" /&gt;
		&lt;![endif]--&gt;
		</xsl:text>
		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
		<script type="text/javascript" src="js/fancybox/source/jquery.fancybox.js?v=2.1.4"></script>
		<link rel="stylesheet" type="text/css" href="js/fancybox/source/jquery.fancybox.css?v=2.1.4" media="screen" />
		<script type="text/javascript" src="js/fotorama/fotorama.js"></script>
		<script type="text/javascript" src="js/sputnik.js"></script>
	</head>
	<body style="min-height: 0; padding: 0;">
		<div id="container">
			<img  class="main_img" src="{$serv/@path}{$serv/pic_big}" width="500" height="293" alt="{$serv/name}" />
			<div class="title">
				<xsl:value-of select="$serv/text" disable-output-escaping="yes"/>
			</div>
		</div>
	</body>
	</xsl:template>


</xsl:stylesheet>