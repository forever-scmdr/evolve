<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:import href="inputs.xsl"/>

	<xsl:template name="DOCTYPE">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" 
			"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;</xsl:text>
	</xsl:template>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text></xsl:template>

	<xsl:variable name="home_url" select="'http://eeee:8080/radio/'"/>

	<xsl:template name="TITLE">CMS - Items</xsl:template><!-- ******************* TODO LOCAL ******************** -->

	<!-- ****************************    ГРУППЫ И ПОЛЯ ВВОДА    ******************************** -->




	<xsl:template match="group">
		<div class="parameter">
			<div class="head"><xsl:value-of select="@name"/><a href="{//delete_group_link}{@id}" class="button totalDelete">Удалить</a></div>
			<a href="#" onclick="$('#gd_{@id}').toggle(200);return false;">детали</a>
			<form id="ugf_{@id}" action="{//update_group_link}{@id}" method="post">
				<div id="gd_{@id}" class="description" style="display: none;">
					<div class="inputField">
						<div class="label">Название группы</div>
						<input name="name" type="text" value="{@name}" />
					</div>
					<div class="selectSmall">
						<div class="label">знак</div>
						<select name="sign">
						<xsl:call-template name="check_option">
							<xsl:with-param name="value" select="' AND '"/>
							<xsl:with-param name="check" select="@sign"/>
							<xsl:with-param name="caption" select="'И'"/>
						</xsl:call-template>
						<xsl:call-template name="check_option">
							<xsl:with-param name="value" select="' OR '"/>
							<xsl:with-param name="check" select="@sign"/>
							<xsl:with-param name="caption" select="'ИЛИ'"/>
						</xsl:call-template>
						</select>
					</div>
					<div class="textArea">
						<div class="label">описание</div>
						<textarea name="description"><xsl:value-of select="@comment"/></textarea>
					</div>
					<a href="#" onclick="$('#ugf_{@id}').submit()" class="button totalSave">Сохранить</a>
				</div>
			</form>
			<xsl:apply-templates select="input" mode="inner"/>
			<a href="#" onclick="$('#ni_{@id}').toggle(200);return false;" class="button totalAdd">Добавить поле ввода</a>
			<div id="ni_{@id}" class="inputFieldBlock" style="display:none">
				<div class="inputFieldContainer">
					<form id="cif_{@id}" action="{//create_input_link}{@id}" method="post">
						<div class="inputField">
							<div class="label">поле ввода</div>
							<input type="text" name="name" />
						</div>
						<div class="properties">
							<div class="select">
								<div class="label">тип:</div>
								<select name="type">
									<option value="text">Текстовое поле</option>
									<option value="droplist">Выпадающий список</option>
									<option value="checkbox">Галочка</option>
									<option value="radiogroup">Точки</option>
									<option value="checkgroup">Галочки</option>
								</select>
							</div>
							<div class="select">
								<div class="label">домен:</div>
								<select name="domain">
									<option value=""> - </option>
									<xsl:for-each select="//domains/domain">
									<option value="{.}"><xsl:value-of select="."/></option>
									</xsl:for-each>
								</select>
							</div>
							<div class="textAreaSmall">
								<div class="label">описание</div>
								<textarea name="description"></textarea>
							</div>
							<div class="buttonSet">
								<a href="#" onclick="$('#cif_{@id}').submit()" class="button partialSave">Сохранить</a>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</xsl:template>




	<xsl:template match="input">
		<div class="parameter noGroup">
			<xsl:apply-templates select="." mode="inner"/>
		</div>
	</xsl:template>




	<xsl:template match="input" mode="inner">
		<div class="inputFieldBlock">
			<div class="expand">
				<a id="iar_{@id}" href="#" onclick="toggleInput('iar_{@id}', 'ipr_{@id}');return false;" class="button expanded">
					<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
				</a>
			</div>
			<div class="inputFieldContainer" >
				<form id="uif_{@id}" action="{//update_input_link}{@id}" method="post">
					<div class="inputField">
						<div class="label">поле ввода</div>
						<input type="text" name="name" value="{@caption}" />
					</div>
					<div id="ipr_{@id}" class="properties" style="display:none">
						<div class="select">
							<div class="label">тип:</div>
							<select name="type">
								<xsl:call-template name="check_option">
									<xsl:with-param name="value" select="'text'"/>
									<xsl:with-param name="check" select="@type"/>
									<xsl:with-param name="caption" select="'Текстовое поле'"/>
								</xsl:call-template>
								<xsl:call-template name="check_option">
									<xsl:with-param name="value" select="'droplist'"/>
									<xsl:with-param name="check" select="@type"/>
									<xsl:with-param name="caption" select="'Выпадающий список'"/>
								</xsl:call-template>
								<xsl:call-template name="check_option">
									<xsl:with-param name="value" select="'checkbox'"/>
									<xsl:with-param name="check" select="@type"/>
									<xsl:with-param name="caption" select="'Галочка'"/>
								</xsl:call-template>
								<xsl:call-template name="check_option">
									<xsl:with-param name="value" select="'radiogroup'"/>
									<xsl:with-param name="check" select="@type"/>
									<xsl:with-param name="caption" select="'Точки'"/>
								</xsl:call-template>
								<xsl:call-template name="check_option">
									<xsl:with-param name="value" select="'checkgroup'"/>
									<xsl:with-param name="check" select="@type"/>
									<xsl:with-param name="caption" select="'Галочки'"/>
								</xsl:call-template>
							</select>
						</div>
						<div class="select">
							<div class="label">домен:</div>
							<select name="domain">
								<xsl:variable name="domain" select="@domain"/>
								<xsl:call-template name="check_option">
									<xsl:with-param name="value" select="''"/>
									<xsl:with-param name="check" select="@domain"/>
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
						<div class="textAreaSmall">
							<div class="label">описание</div>
							<textarea name="description"><xsl:value-of select="@description"/></textarea>
						</div>
						<div class="buttonSet">
							<a class="button totalDelete" href="{//delete_input_link}{@id}">Удалить</a>
							<a href="#" onclick="$('#uif_{@id}').submit()" class="button partialSave">Сохранить</a>
							<a class="button totalAdd" href="#" onclick="$('#nc_{@id}').toggle(200);return false;">Добавить критерий</a>
						</div>
					</div>
				</form>
			</div>
			<div class="subParameters">
				<xsl:apply-templates select="criteria"/>
				<div id="nc_{@id}" class="subParameter" style="display:none">
					<form id="ncf_{@id}" method="post" action="{//create_crit_link}{@id}">
						<div class="selectSmall">
							<div class="label"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></div>
							<select name="sign">
								<option value="=">=</option>
								<option value="&lt;=">&gt;=</option>
								<option value="&gt;=">&lt;=</option>
								<option value="like">like</option>
								<option value="rlike">rlike</option>
							</select>
						</div>
						<div class="select">
							<div class="label">параметр:</div>
							<select name="critParamName">
								<xsl:for-each select="//base-item/parameter">
									<option value="{@name}"><xsl:value-of select="@caption"/></option>
								</xsl:for-each>
							</select>
						</div>
						<div class="inputFieldSmall">
							<div class="label">маска</div>
							<input type="text" name="pattern"/>
						</div>
						<a class="button partialSave" href="#" onclick="$('#ncf_{@id}').submit()">Сохранить</a>
					</form>
				</div>
			</div>
		</div>
	</xsl:template>
	
	
	
	
	<xsl:template match="criteria">
		<div class="subParameter">
			<form id="ucf_{@id}" method="post" action="{//update_crit_link}{@id}">
				<div class="selectSmall">
					<div class="label"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></div>
					<select name="sign">
						<xsl:call-template name="check_option">
							<xsl:with-param name="value" select="'='"/>
							<xsl:with-param name="check" select="@sign"/>
							<xsl:with-param name="caption" select="'='"/>
						</xsl:call-template>
						<xsl:call-template name="check_option">
							<xsl:with-param name="value" select="'&gt;='"/>
							<xsl:with-param name="check" select="@sign"/>
							<xsl:with-param name="caption" select="'&lt;='"/>
						</xsl:call-template>
						<xsl:call-template name="check_option">
							<xsl:with-param name="value" select="'&lt;='"/>
							<xsl:with-param name="check" select="@sign"/>
							<xsl:with-param name="caption" select="'&gt;='"/>
						</xsl:call-template>
						<xsl:call-template name="check_option">
							<xsl:with-param name="value" select="'like'"/>
							<xsl:with-param name="check" select="@sign"/>
							<xsl:with-param name="caption" select="'like'"/>
						</xsl:call-template>
						<xsl:call-template name="check_option">
							<xsl:with-param name="value" select="'rlike'"/>
							<xsl:with-param name="check" select="@type"/>
							<xsl:with-param name="caption" select="'rlike'"/>
						</xsl:call-template>
					</select>
				</div>
				<div class="select">
					<div class="label">параметр:</div>
					<select name="critParamName">
						<xsl:variable name="param" select="@param"/>
						<xsl:for-each select="//base-item/parameter">
							<xsl:call-template name="check_option">
								<xsl:with-param name="value" select="@name"/>
								<xsl:with-param name="check" select="$param"/>
								<xsl:with-param name="caption" select="@caption"/>
							</xsl:call-template>
						</xsl:for-each>
					</select>
				</div>
				<div class="inputFieldSmall">
					<div class="label">маска</div>
					<input type="text" value="{@pattern}"/>
				</div>
				<a class="button partialSave" href="#" onclick="$('#ucf_{@id}').submit()">Сохранить</a>
				<a class="button partialDelete" href="{//delete_crit_link}{@id}">Удалить</a>
			</form>
		</div>
	</xsl:template>
	
	
	
	
	<!-- ****************************    ФОРМЫ ДЛЯ НОВОГО ИНПУТА И ГРУППЫ    ******************************** -->
	
	<xsl:template name="NEW_GROUP_INPUT">
		<a href="#" onclick="$('#fg').toggle(200);return false;" class="button totalAdd">
			Добавить новую группу
		</a>
		<!-- новая группа -->
		<form style="display:none" id="fg" action="{//create_group_link}" method="post">
			<div class="parameter">
				<div class="description">
					<div class="inputField">
						<div class="label">Название группы</div>
						<input type="text" name="name"/>
					</div>
					<div class="selectSmall">
						<div class="label">знак</div>
						<select name="sign">
							<option value="AND">И</option>
							<option value="OR">ИЛИ</option>
						</select>
					</div>
					<div class="textArea">
						<div class="label">описание</div>
						<textarea name="description"></textarea>
					</div>
					<a href="#" onclick="$('#fg').submit();return false;" class="button totalSave">Создать</a>
				</div>
			</div>
		</form>
		<!-- / новая группа -->
		<!-- / новый инпут -->
		<a href="#" onclick="$('#ni_{//filter/@id}').toggle(200);return false;" class="button totalAdd">
			Добавить поле ввода
		</a>
		<div class="parameter noGroup">
			<div id="ni_{//filter/@id}" class="inputFieldBlock" style="display:none">
				<div class="inputFieldContainer">
					<form id="cif_{//filter/@id}" action="{//create_input_link}{//filter/@id}" method="post">
						<div class="inputField">
							<div class="label">поле ввода</div>
							<input type="text" name="name" />
						</div>
						<div class="properties">
							<div class="select">
								<div class="label">тип:</div>
								<select name="type">
									<option value="text">Текстовое поле</option>
									<option value="droplist">Выпадающий список</option>
									<option value="checkbox">Галочка</option>
									<option value="radiogroup">Точки</option>
									<option value="checkgroup">Галочки</option>
								</select>
							</div>
							<div class="select">
								<div class="label">домен:</div>
								<select name="domain">
									<option value=""> - </option>
									<xsl:for-each select="//domains/domain">
									<option value="{.}"><xsl:value-of select="."/></option>
									</xsl:for-each>
								</select>
							</div>
							<div class="textAreaSmall">
								<div class="label">описание</div>
								<textarea name="description"></textarea>
							</div>
							<div class="buttonSet">
								<a href="#" onclick="$('#cif_{//filter/@id}').submit()" class="button partialSave">
									Сохранить
								</a>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
		<!-- / новый инпут -->	
	</xsl:template>
	
	
	
	<!-- ****************************    СТРАНИЦА    ******************************** -->



	<xsl:template match="/">
		<xsl:call-template name="DOCTYPE"/>
		<html>
			<head>
			<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
			<meta http-equiv="Pragma" content="no-cache"/>
			<link href="admin/css/main_admin.css" rel="stylesheet" type="text/css"/>
			<xsl:text disable-output-escaping="yes">
				&lt;!--[if IE 7]&gt;
				&lt;link href="css/ie.css" rel="stylesheet" type="text/css" /&gt;
				&lt;![endif]--&gt;
			</xsl:text>
			<title><xsl:call-template name="TITLE"/></title><!-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! TODO LOCAL !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! -->
			<script type="text/javascript" src="admin/js/jquery-1.10.2.min.js"></script>
			</head>
			<body>
			<script language="javascript" type="text/javascript" src="admin/js/admin.js"></script>
			<script language="javascript" type="text/javascript">
				function toggleInput(arrowId, inputId) {
					if ($("#" + inputId).is(":visible"))
						$("#" + arrowId).attr("class", "button expanded");
					else
						$("#" + arrowId).attr("class", "button collapsed");
					$("#" + inputId).toggle(200);
				}
				$(document).ready(function() {
					window.opener.location.reload(false);
				});
			</script>
			<!-- ************************ Основная форма **************************** -->
			<!-- ******************************************************************** -->
			<div class="mainwrap">
				<table class="main_table">
				<tr>
					<td class="main">
						<div class="warning">
							<div class="tl">
								<div class="bl">
									<div class="message">
										<span><xsl:value-of select="//message"/></span>
									</div>
								</div>
							</div>
						</div>
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
								<table class="basicContainer">
									<xsl:if test="//filter/@item-desc = ''">
									<tr>
										<td class="sideColoumn">
											Базовый тип:
										</td>
										<td class="mainColoumn">
											<form id="cf" action="{//create_filter_link}" method="post">
												<select name="baseName">
													<xsl:for-each select="//itemdesc[@virtual='false']">
														<xsl:sort select="@caption"/>
														<option value="{@name}"><xsl:value-of select="@caption"/></option>
													</xsl:for-each>
												</select>
												<a href="#" onclick="$('#cf').submit();return false;" class="button totalSave">Создать новый фильтр</a>
											</form>
										</td>
									</tr>
									</xsl:if>
									<xsl:if test="//filter/@item-desc != ''">
									<tr>
										<td class="sideColoumn">
											Базовый тип:
										</td>
										<td class="mainColoumn">
											<form id="cf" action="{//create_filter_link}" method="post">
												<select name="baseId">
													<xsl:variable name="itemName" select="//filter/@item-desc"/>
													<xsl:for-each select="//itemdesc[@virtual='false'] | //base-item">
														<xsl:sort select="@caption"/>
														<xsl:call-template name="check_option">
															<xsl:with-param name="value" select="@name"/>
															<xsl:with-param name="check" select="$itemName"/>
															<xsl:with-param name="caption" select="@caption"/>
														</xsl:call-template>
													</xsl:for-each>
												</select>
												<a href="#" onclick="$('#cf').submit();return false;" class="button totalSave">Создать новый фильтр</a>
											</form>
										</td>
									</tr>
									<tr>
										<td class="sideColoumn">
											Параметры:
										</td>
										<td class="mainColoumn">
											<xsl:apply-templates select="//filter/group | //filter/input"/>
											<xsl:call-template name="NEW_GROUP_INPUT"/>
										</td>
									</tr>
									</xsl:if>
									<tr>
										<td class="sideColoumn"></td>
										<td class="mainColoumn"></td>
									</tr>
								</table>
							</td>
							<!-- ***************************************************************************************** -->
						</tr>
						</table>
					</td>
				</tr>
				</table>
			</div>
			</body>
		</html>
	</xsl:template>
		
</xsl:stylesheet>