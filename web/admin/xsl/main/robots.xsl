<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:import href="_inc_message.xsl"/>
	<xsl:import href="_inc_params.xsl"/>
	<xsl:import href="_documentation.xsl"/>


	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
			"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;</xsl:text>
	</xsl:template>

	<xsl:template name="HEAD">
		<head>
		<base href="{admin-page/domain}" />
			<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		</head>
	</xsl:template>

	<xsl:template match="/">
		<xsl:call-template name="DOCTYPE" />
		<html>
			<xsl:call-template name="HEAD" />
			<body>
				<xsl:call-template name="MESSAGE"/>
				<h1>Редактировать robots.txt</h1>
				<div class="wide">
					<div class="margin">
						<form name="mainForm" id="mainForm" action="admin_save_robots.action" method="POST">
							<textarea name="robots_content">
								<xsl:value-of select="//robots_txt" disable-output-escaping="yes"/>
							</textarea><br/>
							<input type="submit"/>
						</form>
					</div>
			</div>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>