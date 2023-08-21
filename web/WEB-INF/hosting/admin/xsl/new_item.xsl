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
							<td class="sideColoumn" style="vertical-align: middle; padding-right: 10px;">
								Название:
							</td>
							<td class="mainColoumn">
								<input type="text" id="name" class="textForm" name="name" value="{//data/name}"/>
							</td>
						</tr>
						<tr>
							<td class="sideColoumn" style="vertical-align: middle; padding-right: 10px;">
								Описание:
							</td>
							<td class="mainColoumn">
								<input type="text" name="description" value="{//data/description}" />
							</td>
						</tr>
						<tr>
							<td class="sideColoumn"></td>
							<td class="mainColoumn">
								<a style="padding: 5px 10px; color: #fff; background: #56C493; display: block; text-align: center; text-decoration: none; margin-top: 7px;" onclick="$('#f1').submit();" class="button totalSave">Создать новый тип</a>
							</td>
						</tr>
					</table>
				</form>
			</td>
			<!-- ***************************************************************************************** -->
		</tr>
		</table>
		<script type="text/javascript">
			$("#f1").submit(function(e){
				if($("#name").val() == ""){
					e.preventDefault();
					alert("Введите название для нового типа!");
				}
			});
		</script>
	</xsl:template>

</xsl:stylesheet>