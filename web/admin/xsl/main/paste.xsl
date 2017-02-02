<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:import href="_inc_message.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text></xsl:template>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:template match="/">
	<xsl:if test="admin-page/item">
		<!-- Буфер обмена -->
		<div id="pasteBuffer">
			<div class="side_block">
				<div class="head" style="background:url(admin/admin_img/side_block_head_copy.png) left top no-repeat;">
					<span>Буфер обмена (вставить):</span>
				</div>
				<div class="items">
					<xsl:for-each select="admin-page/item">
						<div class="exist_item">
							<div class="item_type">
								[<xsl:value-of select="@type-caption"/>]
							</div>
							<table>
								<tr>
									<td class="link">
										<xsl:variable name="caption" select="@caption | @type-caption[current()/@caption = '']"/>
										<a href="{edit-link}"><xsl:value-of select="$caption"/></a>
									</td>
									<td class="action">
										<a href="#" onclick="insertAjaxView('{delete-link}', 'pasteBuffer'); return false;">
											<img src="admin/admin_img/action_delete.png" />
										</a>
									</td>
								</tr>
							</table>
							<div class="copyWhat">
								<div class="copySpacer"></div>
								<xsl:if test="paste-link">
									<div class="copyButton">
										<a href="#" onclick="insertAjaxView('{paste-link}', 'subitems', false, 'hidden_mes', 'message_main'); return false;">вставить</a>
									</div>
								</xsl:if>
								<xsl:if test="not(paste-link)">
									<div class="noPasteButton">
										вставить
									</div>
								</xsl:if>
							</div>
						</div>
						<div class="spacer"></div>
					</xsl:for-each>
				</div>
				<div class="bottom"></div>
			</div>
		</div>
	</xsl:if>
	</xsl:template>
		
</xsl:stylesheet>