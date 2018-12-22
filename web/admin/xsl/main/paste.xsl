<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:import href="_inc_message.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="base-vars" select="substring-after(admin-page/item[1]/paste-link,'?')"/>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text></xsl:template>

	<!-- ****************************    СТРАНИЦА    ******************************** -->

	<xsl:template match="/">
		<xsl:if test="admin-page/item">
			<!-- Буфер обмена -->
			<div id="pasteBuffer" class="result">
				<div class="list">
					<h4>Буфер обмена (вставить)</h4>

					<ul class="edit">
						<li style="padding-top: 8px;" >
							<div class="buffer-actions-all" style="text-align: center; padding: 2px 5px; background: #fff; border: 1px solid #ccc;">
								<a class="copy paste" onclick="insertAjaxView('admin_paste_all.action?{$base-vars}', 'subitems', false, 'hidden_mes', 'message_main')" title="вставить все">вставить</a>
								<a class="copy move" onclick="insertAjaxView('admin_move_all.action?{$base-vars}', 'subitems', false, 'hidden_mes', 'message_main')" title="Переместить  все. Оригиналы будут удалены!">переместить</a>
								<a class="delete" onclick="insertAjaxView('admin_clear_paste_buffer.action?{$base-vars}', 'pasteBuffer', false, 'hidden_mes', 'message_main')" title="Очистить буфер обмена">удалить</a>
							</div>
						</li>
						<xsl:for-each select="admin-page/item">

							<xsl:variable name="caption" select="@caption | @type-caption[current()/@caption = '']" />

							<li class="drop-zone"></li>
							<li class="dragable visible multiple">
								<div class="selection-overlay buffer" data-id="{@id}"></div>
								<a href="{edit-link}" class="name" title="радактировать">
									<span class="description">[<xsl:value-of select="@type-caption" />]</span>
									<br />
									<xsl:value-of select="$caption" />
								</a>
								<a href="#" onclick="insertAjaxView('{paste-link}', 'subitems', false, 'hidden_mes', 'message_main'); return false;" class="copy paste" title="вставить">вставить</a>
								<a href="#" onclick="insertAjaxView('{move-to-link}', 'subitems', false, 'hidden_mes', 'message_main'); return false;" class="copy move" title="Переместить. Оригинал будет удален!">переместить</a>
								<a href="#" onclick="insertAjaxView('{delete-link}', 'pasteBuffer'); return false;"  class="delete" title="удалить">удалить</a>
							</li>
						</xsl:for-each>
						<li class="drop-zone"></li>
					</ul>
				</div>
			</div>
		</xsl:if>
	</xsl:template>
		
</xsl:stylesheet>