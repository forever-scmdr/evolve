<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:import href="_inc_message.xsl"/>
	<xsl:import href="_inc_params.xsl"/>
	<xsl:import href="_documentation.xsl"/>


	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template match="/">
		<xsl:call-template name="MESSAGE"/>
		<div class="wide">
			<div class="margin">
				<form name="mainForm" id="mainForm" action="save_robots.action" enctype="multipart/form-data" method="post">
					<textarea name="robotsContent">
						<xsl:value-of select="robots_txt" disable-output-escaping="yes"/>
					</textarea>
				</form>
			</div>
		</div>
	</xsl:template>
</xsl:stylesheet>