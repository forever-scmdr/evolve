<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>

	<xsl:template name="CONTENT">
		<h1 class="title">Создать новый подкласс для класса '<xsl:value-of select="//parent-itemdesc/@caption"/>'</h1>
		<!-- tabs -->
		<div class="tabs_container">
			<div class="tabs">
				<div class="clear">
				</div>
			</div>
		</div>
		<!-- /tabs -->
		<table class="type_1">
		<tr>
			<!-- ************************ Основные параметры айтема **************************** -->
			<td>
				<form id="f1" action="{//save_link}" method="post">
					<table class="basicContainer">
						<tr>
							<td class="sideColoumn">
								Название:
							</td>
							<td class="mainColoumn">
								<input type="text" class="textForm" name="name" value="{//data/name}"/>
							</td>
						</tr>
						<tr>
							<td class="sideColoumn">
								Описание:
							</td>
							<td class="mainColoumn">
								<textarea name="description"><xsl:value-of select="//data/description"/></textarea>
							</td>
						</tr>
						<tr>
							<td class="sideColoumn"></td>
							<td class="mainColoumn">
								<a href="javascript:$('#f1').submit()" class="button totalSave">Создать новый тип</a>
							</td>
						</tr>
					</table>
				</form>
			</td>
			<!-- ***************************************************************************************** -->
		</tr>
		</table>
	</xsl:template>

</xsl:stylesheet>