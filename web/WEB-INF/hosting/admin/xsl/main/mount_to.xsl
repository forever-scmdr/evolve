<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:import href="_inc_message.xsl"/>
	<xsl:import href="mount_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<!-- Меняется в зависимости от страницы -->
	<xsl:variable name="view_type" select="'mountTo'"/>
	<xsl:variable name="form_action" select="'admin_create_mount_to.action'"/>
	<xsl:variable name="mount_text">Добавить ссылку на "<xsl:value-of select="$item/@caption"/>" в:</xsl:variable>
	<xsl:variable name="mounted_text">Ссылки на "<xsl:value-of select="$item/@caption"/>" содержатся в:</xsl:variable>

</xsl:stylesheet>