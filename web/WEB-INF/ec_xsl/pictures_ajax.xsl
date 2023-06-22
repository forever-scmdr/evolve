<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	version="1.0">
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="serv" select="/page/service"/>

	<xsl:variable name="success" select="page/variables/result = 'success'"/>
	<xsl:variable name="message" select="page/variables/message"/>
	<xsl:variable name="f" select="page/form"/>

	<xsl:template match="/">
		<div>
			<div class="room-pics result pageText" id="room-pics">
				<h3 style="font-size: 20px;"><xsl:value-of select="page/room/name"/></h3>
				<br/>
				<div class="fotorama" id="ftrm-popup" data-width="100%" data-allowfullscreen="true" data-nav="thumbs">
					<xsl:for-each select="page/room/picture_pair">
						<a href="{@path}{big}" data-caption="{name}"><img src="{@path}{small}" alt="{name}" title="{name}" /></a>
					</xsl:for-each>
				</div>
				<br/>
					<a href="{page/room/show_room}" target="blank">Подробнее</a>
				
				<script type="text/javascript">
					$("#ftrm-popup").fotorama();
				</script>
			</div>
		</div>
	</xsl:template>


</xsl:stylesheet>