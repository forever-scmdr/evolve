<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="common_page_base.xsl" />
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*" />

	<xsl:variable name="pname" select="'internal'"/>

	<xsl:template name="CONTENT">
	<div class="content-container">
		<section class="p-t-default">
			<div class="container">
				<div class="row">
					<xsl:call-template name="INNER_CONTENT"/>
				</div>
			</div>
		</section>
	</div>
	</xsl:template>

	<xsl:template name="INNER_CONTENT"/>

</xsl:stylesheet>