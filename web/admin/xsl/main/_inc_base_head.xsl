<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template name="HEAD">
	<head>
		<base href="{admin-page/domain}"/>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<meta http-equiv="Pragma" content="no-cache"/>
		<link href="admin/css/main_admin.css" rel="stylesheet" type="text/css"/>
		<link href="admin/fancybox/jquery.fancybox.css" rel="stylesheet" type="text/css" media="screen" />
		<link href="admin/jquery_css/ui-lightness/jquery-ui-1.10.3.custom.css" rel="stylesheet" type="text/css" media="screen" />
		<title>Система управления сайтом <xsl:value-of select="/admin-page/domain"/></title>
		<script type="text/javascript" src="admin/js/jquery-1.10.2.min.js"></script>
		<script type="text/javascript" src="admin/js/jquery.form.min.js"></script>
		<script type="text/javascript" src="admin/js/jquery-ui-1.10.3.custom.min.js"></script>
		<script type="text/javascript" src="admin/fancybox/jquery.fancybox.pack.js"></script>
		<script type="text/javascript" src="admin/js/admin.js" language="javascript"></script>
		<script type="text/javascript" src="admin/tinymce/tinymce.min.js"></script>
		<script type="text/javascript" src="admin/js/regional-ru.js"></script>
		<script type="text/javascript">
			jQuery(document).ready(function() {
			  $(".fancybox").fancybox();
			})
		</script>
	</head>
	</xsl:template>
	
	<xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
			"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;</xsl:text>
	</xsl:template>
</xsl:stylesheet>