<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:import href="_inc_message.xsl"/>
	<xsl:import href="mount_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="view_type" select="'moveTo'"/>
	<xsl:variable name="mount_text">Переместить "<xsl:value-of select="$item/@caption"/>" в:</xsl:variable>
	<xsl:variable name="form_action" select="'admin_move_to.action'"/>
	<xsl:variable name="input_type" select="'radio'"/>
	<xsl:variable name="button_img" select="'admin/admin_img/move.png'"/>
	
</xsl:stylesheet>