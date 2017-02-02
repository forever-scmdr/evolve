<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:import href="inputs.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>

	<xsl:template name="MOVE_SCRIPT">
		<script>
			function up(id) {
				var size = $("[id^='pord_']").length;
				var param = document.getElementById(id);
				var index = $("[id^='pord_']").index(param);
				if (index &gt; 0)
					param.parentNode.insertBefore(param, $("[id^='pord_']").get(index - 1));
			}
			
			function down(id) {
				var size = $("[id^='pord_']").length;
				var param = document.getElementById(id);
				var index = $("[id^='pord_']").index(param);
				if (index &lt; size - 1)
					param.parentNode.insertBefore(param, $("[id^='pord_']").get(index + 1).nextSibling);
			}
			
			function list() {
				var paramArray = $("[id^='pord_']");
				var result = "";
				for (i = 0; i &lt; paramArray.length; i++) {
					if (i > 0) result += ',';
					result += paramArray.get(i).getAttribute("name");
				}
				return result;
			}
			
			function submitItem() {
				$("#paramOrder").val(list());
				$('#item').submit();
			}
		</script>
	</xsl:template>

	<xsl:template match="itemdesc" mode="OPTION">
		<option value="{@id}"><xsl:value-of select="@caption"/></option>
		<xsl:apply-templates select="itemdesc" mode="OPTION"/>
	</xsl:template>

	<xsl:template match="updating-itemdesc[@user-def = 'true']">
		<xsl:call-template name="MOVE_SCRIPT"/>
		<table class="type_1">
		<tr>
			<!-- ************************ Основные параметры айтема **************************** -->
			<td>
				<div class="buttonBar">
					<a href="#deleteOptions" class="button totalDelete fancybox">Удалить</a>
					<a href="{create_link}" class="button totalAdd">Создать подкласс</a>
				</div>
				<div id="deleteOptions" style="display: none;">
					<table>
						<tr>
							<td>
								<form id="new_par" action="{//updating-itemdesc/delete_link}" method="post">
									<h3>Изменить родительский элемент</h3>
									<select name="newId">
										<xsl:apply-templates select="//items/itemdesc" mode="OPTION"/>
									</select>
									<a href="javascript:$('#new_par').submit()" class="button totalSave">Изменить</a>
								</form>
							</td>
							<td class="delete">
								<h3>Удалить навсегда вместе с элементами каталога</h3>
								<p>Все связанные позиции каталога также будут удалены</p>
								<a href="{//updating-itemdesc/delete_link}" class="button totalDelete">Удалить</a>
							</td>
						</tr>
					</table>
				</div>
				<table class="basicContainer">
					<form id="item" action="{update_link}" method="post">
						<input id="paramOrder" type="hidden" name="paramOrder" value=""/>
						<input type="hidden" name="extends" value="{@extends}"/>
						<input type="hidden" name="name" value="{@name}"/>
						<tr>
							<td class="sideColoumn">
								Название:
							</td>
							<td class="mainColoumn">
								<input type="text" class="textForm" value="{@caption}" name="caption" />
							</td>
						</tr>
						<tr>
							<td class="sideColoumn">
								Описание:
							</td>
							<td class="mainColoumn">
								<textarea name="description"><xsl:value-of select="@description"/></textarea>
							</td>
						</tr>
					</form>
					<tr>
						<td class="sideColoumn"></td>
						<td class="mainColoumn">
							<h2>Параметры:</h2>
						</td>
					</tr>
					<tr>
						<td class="sideColoumn"></td>
						<td class="mainColoumn">
							<xsl:variable name="item_id" select="@id"/>
							<xsl:for-each select="parameter">
								<xsl:if test="@owner-id != $item_id">
									<div><xsl:value-of select="@caption"/></div>
								</xsl:if>
								<xsl:if test="@owner-id = $item_id">
									<div id="pord_{@id}" name="{@id}">
										<span class="setPosition">
											<a href="#" onclick="up('pord_{@id}');return false;"><img src="admin/admin_img/upArrow.png" alt="" /></a>
											<a href="#" onclick="down('pord_{@id}');return false;"><img src="admin/admin_img/downArrow.png" alt="" /></a>
										</span>
										<a href="#" onclick="$('#par_{@id}').toggle(200);return false;"><xsl:value-of select="@caption"/></a>
										<a href="{delete_link}"><img src="admin/admin_img/deleteButtonTiny.png" alt="" /></a>
										<!-- редактирование свойств параметра -->
										<div id="par_{@id}" class="parameterOptions" style="display:none">
											<form id="id_{@id}" action="{update_link}" method="post">
												<input name="paramName" type="hidden" value="{@name}" />
												<div class="inputField">
													<div class="label">название:</div>
													<input name="caption" type="text" value="{@caption}" />
												</div>
												<div class="radioButtons">
													<div class="label">количество зачений:</div>
													<xsl:call-template name="check_radio">
														<xsl:with-param name="value" select="'single'"/>
														<xsl:with-param name="check" select="@quantifier"/>
														<xsl:with-param name="name" select="'quantifier'"/>
													</xsl:call-template>
													<label for="single">одиночный</label>
													<xsl:call-template name="check_radio">
														<xsl:with-param name="value" select="'multiple'"/>
														<xsl:with-param name="check" select="@quantifier"/>
														<xsl:with-param name="name" select="'quantifier'"/>
													</xsl:call-template>
													<label for="multiple">множественный</label>
												</div>
												<div class="select">
													<div class="label">тип данных:</div>
													<select name="typeName">
														<xsl:call-template name="check_option">
															<xsl:with-param name="value" select="'string'"/>
															<xsl:with-param name="check" select="@type"/>
															<xsl:with-param name="caption" select="'Строковый'"/>
														</xsl:call-template>
														<xsl:call-template name="check_option">
															<xsl:with-param name="value" select="'integer'"/>
															<xsl:with-param name="check" select="@type"/>
															<xsl:with-param name="caption" select="'Целочисленный'"/>
														</xsl:call-template>
														<xsl:call-template name="check_option">
															<xsl:with-param name="value" select="'long'"/>
															<xsl:with-param name="check" select="@type"/>
															<xsl:with-param name="caption" select="'Целочисл. длинный'"/>
														</xsl:call-template>
														<xsl:call-template name="check_option">
															<xsl:with-param name="value" select="'double'"/>
															<xsl:with-param name="check" select="@type"/>
															<xsl:with-param name="caption" select="'Дробный'"/>
														</xsl:call-template>
														<xsl:call-template name="check_option">
															<xsl:with-param name="value" select="'text'"/>
															<xsl:with-param name="check" select="@type"/>
															<xsl:with-param name="caption" select="'Текстовый'"/>
														</xsl:call-template>
														<xsl:call-template name="check_option">
															<xsl:with-param name="value" select="'date'"/>
															<xsl:with-param name="check" select="@type"/>
															<xsl:with-param name="caption" select="'Дата'"/>
														</xsl:call-template>
													</select>
												</div>
												<div class="select">
													<div class="label">предопределенное значение:</div>
													<select name="domainName">
														<xsl:variable name="domain" select="@domain"/>
														<xsl:call-template name="check_option">
															<xsl:with-param name="value" select="''"/>
															<xsl:with-param name="check" select="$domain"/>
															<xsl:with-param name="caption" select="' - '"/>
														</xsl:call-template>
														<xsl:for-each select="//domains/domain">
															<xsl:call-template name="check_option">
																<xsl:with-param name="value" select="."/>
																<xsl:with-param name="check" select="$domain"/>
																<xsl:with-param name="caption" select="."/>
															</xsl:call-template>
														</xsl:for-each>
													</select>
												</div>
												<div class="inputField">
													<div class="label">формат:</div>
													<input name="format" type="text" value="{@format}" />
												</div>
												<div class="textArea">
													<div class="label">описание:</div>
													<textarea name="description"><xsl:value-of select="@description"/></textarea>
												</div>
												<a href="#" onclick="$('#id_{@id}').submit();return false;" class="button partialSave">Сохранить</a>
											</form>
										</div>
									</div>
								</xsl:if>
							</xsl:for-each>
							<!-- /редактирование свойств параметра -->
							<a href="javascript:submitItem()" class="button totalSave">Сохранить класс</a>
						</td>
					</tr>
					<form id="new_param" action="{new_param_link}" method="post">
						<tr>
							<td class="sideColoumn"></td>
							<td class="mainColoumn">
								<h2>Новый параметр:</h2>
							</td>
						</tr>
						<tr>
							<td class="sideColoumn">
								название:
							</td>
							<td class="mainColoumn">
								<input name="paramName" type="text" class="textForm" value="{//data/paramName}" />
							</td>
						</tr>
						<tr>
							<td class="sideColoumn">
								количество зачений:
							</td>
							<td class="mainColoumn">
								<!-- 
								<xsl:call-template name="check_radio">
									<xsl:with-param name="value" select="'single'"/>
									<xsl:with-param name="check" select="//data/quantifier"/>
									<xsl:with-param name="name" select="'quantifier'"/>
								</xsl:call-template>
								 -->
								<input name="quantifier" type="radio" group="qu" checked="checked" value="single" />
								<label for="single">одиночный</label>
								<!-- 
								<xsl:call-template name="check_radio">
									<xsl:with-param name="value" select="'multiple'"/>
									<xsl:with-param name="check" select="//data/quantifier"/>
									<xsl:with-param name="name" select="'quantifier'"/>
								</xsl:call-template>
								-->
								<input name="quantifier" type="radio" group="qu" value="multiple" />
								<label for="multiple">множественный</label>
							</td>
						</tr>
						<tr>
							<td class="sideColoumn">
								тип данных:
							</td>
							<td class="mainColoumn">
								<select name="typeName">
									<xsl:call-template name="check_option">
										<xsl:with-param name="value" select="'string'"/>
										<xsl:with-param name="check" select="//data/typeName"/>
										<xsl:with-param name="caption" select="'Строковый'"/>
									</xsl:call-template>
									<xsl:call-template name="check_option">
										<xsl:with-param name="value" select="'integer'"/>
										<xsl:with-param name="check" select="//data/typeName"/>
										<xsl:with-param name="caption" select="'Целочисленный'"/>
									</xsl:call-template>
									<xsl:call-template name="check_option">
										<xsl:with-param name="value" select="'long'"/>
										<xsl:with-param name="check" select="//data/typeName"/>
										<xsl:with-param name="caption" select="'Целочисл. длинный'"/>
									</xsl:call-template>
									<xsl:call-template name="check_option">
										<xsl:with-param name="value" select="'double'"/>
										<xsl:with-param name="check" select="//data/typeName"/>
										<xsl:with-param name="caption" select="'Дробный'"/>
									</xsl:call-template>
									<xsl:call-template name="check_option">
										<xsl:with-param name="value" select="'text'"/>
										<xsl:with-param name="check" select="//data/typeName"/>
										<xsl:with-param name="caption" select="'Текстовый'"/>
									</xsl:call-template>
									<xsl:call-template name="check_option">
										<xsl:with-param name="value" select="'date'"/>
										<xsl:with-param name="check" select="//data/typeName"/>
										<xsl:with-param name="caption" select="'Дата'"/>
									</xsl:call-template>
								</select>
							</td>
						</tr>
						<tr>
							<td class="sideColoumn">
								предопределенное значение:
							</td>
							<td class="mainColoumn">
								<select name="domainName">
									<xsl:call-template name="check_option">
										<xsl:with-param name="value" select="''"/>
										<xsl:with-param name="check" select="//data/domainName"/>
										<xsl:with-param name="caption" select="' - '"/>
									</xsl:call-template>
									<xsl:for-each select="//domains/domain">
										<xsl:call-template name="check_option">
											<xsl:with-param name="value" select="."/>
											<xsl:with-param name="check" select="//data/domainName"/>
											<xsl:with-param name="caption" select="."/>
										</xsl:call-template>
									</xsl:for-each>
								</select>
							</td>
						</tr>
						<tr>
							<td class="sideColoumn">
								Описание:
							</td>
							<td class="mainColoumn">
								<textarea name="description"></textarea>
							</td>
						</tr>
						<tr>
							<td class="sideColoumn"></td>
							<td class="mainColoumn">
								<a href="#" onclick="$('#new_param').submit();return false;" class="button totalAdd">Создать параметр</a>
							</td>
						</tr>
					</form>
				</table>
			</td>
			<!-- ***************************************************************************************** -->
		</tr>
		</table>	
	</xsl:template>

	<xsl:template match="updating-itemdesc[@user-def = 'false']">
		<table class="type_1">
		<tr>
			<!-- ************************ Основные параметры айтема **************************** -->
			<td>
				<div class="buttonBar">
					<a href="{create_link}" class="button totalAdd">Создать подкласс</a>
				</div>
				<table class="basicContainer">
					<tr>
						<td class="sideColoumn">
							Название:
						</td>
						<td class="mainColoumn">
							<xsl:value-of select="@name"/>
						</td>
					</tr>
					<tr>
						<td class="sideColoumn">
							Описание:
						</td>
						<td class="mainColoumn">
							<xsl:value-of select="@description"/>
						</td>
					</tr>
					<tr>
						<td class="sideColoumn"></td>
						<td class="mainColoumn">
							<h2>Параметры:</h2>
						</td>
					</tr>
					<tr>
						<td class="sideColoumn"></td>
						<td class="mainColoumn">
							<xsl:for-each select="parameter">
								<div>
									<xsl:value-of select="@caption"/>
								</div>
							</xsl:for-each>
						</td>
					</tr>
				</table>
			</td>
			<!-- ***************************************************************************************** -->
		</tr>
		</table>	
	</xsl:template>


	<xsl:template name="CONTENT">
		<h1 class="title"><xsl:value-of select="//updating-itemdesc/@caption"/></h1>
		<!-- tabs -->
		<div class="tabs_container">
			<div class="tabs">
				<div class="clear">
				</div>
			</div>
		</div>
		<!-- /tabs -->
		<xsl:apply-templates select="//updating-itemdesc"/>
	</xsl:template>

</xsl:stylesheet>