<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:import href="_inc_message.xsl"/>
	<xsl:import href="mount_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<!-- Меняется в зависимости от страницы -->
	<xsl:variable name="view_type" select="'toMount'"/>
	<xsl:variable name="mount_text">Добавить в "<xsl:value-of select="$item/@caption"/>" ссылку на:</xsl:variable>
	<xsl:variable name="mounted_text">"<xsl:value-of select="$item/@caption"/>" содержит ссылки на:</xsl:variable>
	<xsl:variable name="form_action" select="'admin_create_to_mount.action'"/>
	
</xsl:stylesheet>