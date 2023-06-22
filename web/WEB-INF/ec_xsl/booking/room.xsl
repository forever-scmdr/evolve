<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:import href="common_page_base.xsl" />
	<xsl:import href="../inputs_inc.xsl" />
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" />
	<xsl:strip-space elements="*" />

	<xsl:variable name="type" select="page/variables/type"/>
	<xsl:variable name="f" select="page/room_form"/>

	<xsl:template name="CONTENT">
		<h1>Заявки и номера</h1>
		<div class="room-sel">
			<form action="{$f/submit}" method="post" id="room_form">
				<table>
					<tr>
						<td>
							Тип номера:
							<select name="{$f/type/@input}" value="{$type}">
								<xsl:for-each select="page/type">
									<xsl:call-template name="check_option">
										<xsl:with-param name="value" select="@id"/>
										<xsl:with-param name="check" select="$type"/>
										<xsl:with-param name="caption" select="name"/>
									</xsl:call-template>
								</xsl:for-each>
							</select>
						</td>
						<td>
							№<br/>
							<input type="text" class="nmbr" name="{$f/num/@input}" value="{$f/num}"/>
							<input type="hidden" id="rfrom" name="{$f/from/@input}" value="{$f/from}"/>
							<input type="hidden" id="rto" name="{$f/to/@input}" value="{$f/to}"/>
						</td>
						<td>
							&#160;
							<a href="#" class="green-button" onclick="submitRoomForm(); return false;">Сохранить</a>
						</td>
					</tr>
				</table>
			</form>
			<a onclick="confirmLink('{page/free_room/delete}', 'Вы действительно хотите комнату?')" class="blue-button">Удалить</a>
		</div>
		<div id="ubertable"></div>
	</xsl:template>

	<xsl:template name="SCRIPTS">
	<script>
		$(document).ready(function(){
			var now = new Date();
			//-- строит календарь (дата +365 дней)
			//-- текущая дата - для примера 
			<xsl:if test="$f/@id = '0'">
			buildUbeTable(now);
			</xsl:if>
			<xsl:if test="$f/@id != '0'">
			buildUbeTable(now, <xsl:value-of select="$f/from"/>, <xsl:value-of select="$f/to"/>);
			</xsl:if>
			//alert();
			//settableInterval(1486080000000, 1491350400000);
			//console.log(getTableInterval());
		});
		
		function submitRoomForm() {
			var interval = getTableInterval();
			$('#rfrom').val(interval[0]);
			$('#rto').val(interval[1]);
			if (isIntervalSet()) {
				//alert($('#rfrom').val() + '       ' + $('#rto').val());
				$('#room_form').submit();
			} else {
				alert('Задайте интервал');
			}
		}
	</script>
	</xsl:template>

</xsl:stylesheet>