<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
		xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:xs="http://www.w3.org/2001/XMLSchema"
		xmlns="http://www.w3.org/1999/xhtml"
		xmlns:f="f:f"
		version="2.0">

	<xsl:import href="_inc_emoji.xsl" />

	<!-- Перевод даты из CMS вида (23.11.2017 00:00) в XSL вид -->
	<xsl:function name="f:xsl_date" as="xs:dateTime">
		<xsl:param name="str_date"/>
		<xsl:variable name="big_parts" select="tokenize($str_date, '\s+')" />
		<xsl:variable name="parts" select="tokenize($big_parts[1], '\.')"/>
		<xsl:sequence select="if ($parts[3]) then xs:dateTime(concat($parts[3], '-', $parts[2], '-', $parts[1], 'T', $big_parts[2], ':00Z')) else xs:dateTime('1970-01-01T00:00:00Z')"/>
	</xsl:function>

	<xsl:function name="f:date_to_millis">
		<xsl:param name="date" as="xs:dateTime"/>
		<xsl:sequence select="($date - xs:dateTime('1970-01-01T00:00:00Z')) div xs:dayTimeDuration('PT0.001S')"/>
	</xsl:function>

	<!-- Перевод миллисекунд в XSL дату -->
	<xsl:function name="f:millis_to_date_time" as="xs:dateTime">
		<xsl:param name="millis"/>
		<xsl:sequence select="if ($millis) then xs:dateTime('1970-01-01T00:00:00Z') + $millis * xs:dayTimeDuration('PT0.001S') else xs:dateTime('1970-01-01T00:00:00Z')"/>
	</xsl:function>

	<!-- Перевод даты из XSL вида в CMS вид (23.11.2017) -->
	<xsl:function name="f:format_date">
		<xsl:param name="date" as="xs:dateTime"/>
		<xsl:sequence select="format-dateTime($date, '[D01].[M01].[Y0001] [H01]:[m01]')"/>
	</xsl:function>

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text></xsl:template>


	<!--********************************************************************************** 
							 ОДИНОЧНЫЕ ПАРАМЕТРЫ (ПОЛЯ ВВОДА)
	***********************************************************************************-->


	<!-- Фильтр -->
	<xsl:template match="field[ @type='filter' ]" mode="single">
		<xsl:variable name="form" select=".."/>

		<xsl:if test="$form/@id &gt; 0">
			<div style="position: relative; width: 175px;">
				<a href="#" onclick="openFilter('fil_{@id}', {$form/@id}, '{@name}');return false;">Редактировать фильтр</a>
				<a href="#" onclick="$('#fil_{@id}').val(''); document.mainForm.submit(); return false;" class="delete" title="Принудительно очистить фильтр."></a>
			</div>
			<textarea id="fil_{@id}" style="display:none" name="{@input}"><xsl:value-of select="."/></textarea>
			<xsl:call-template name="BR"/>
		</xsl:if>
	</xsl:template>

	<!-- Одиночный файл -->
	<xsl:template match="field[ @type='file' ]" mode="single">
		<xsl:variable name="form" select=".."/>
		<p class="form_title">
			<xsl:value-of select="@caption" />
		</p>
		<xsl:if test="@description != ''">
			<p class="form_comment">[<xsl:value-of select="@description" />]</p>
		</xsl:if>
		<table style="margin-top: 8px;">
			<tbody>
				<tr>
					<td>
						<div>
							<div class="file-load-block">
								<input type="text" placeholder="Введие URL файла" class="url" name="{@input}" title="для загрузки файлов из интернета" />
								<input type="text" style="display: none;" class="text_ipt" />
								<label class="file">
									Прикрепите или перетащите файл
									<input type="file" class="file" name="{@input}" />
								</label>
							</div>
						</div>
					</td>
					<xsl:if test=". != ''">
						<td style="padding-left: 20px; vertical-align: top;">
							<div style="position: relative; padding-right: 20px;">
								<a id="param-{@id}" class="delete" href="javascript:confirmAjaxView('admin_delete_parameter.action?multipleParamId={@id}&amp;itemId={$form/@id}', 'main_view', null, '#param-{@id}')"></a>
								<a href="{$form/@file-path}{.}" style="display: block;" target="blank">Открыть файл</a>
							</div>
						</td>
					</xsl:if>
				</tr>
			</tbody>
		</table>
	</xsl:template>

	<!-- Одиночная картинка -->
	<xsl:template match="field[ @type='picture' ]" mode="single">
		<xsl:variable name="form" select=".."/>
		<p class="form_title">
			<xsl:value-of select="@caption" />
		</p>
		<xsl:if test="@description != ''">
			<p class="form_comment">[<xsl:value-of select="@description" />]</p>
		</xsl:if>
		<table style="margin-top: 8px;">
			<tbody>
				<tr>
					<td>
						<div>
							<div class="file-load-block">
								<input type="text" placeholder="Введие URL картинки" class="url" name="{@input}" title="для загрузки файлов из интернета" />
								<input type="text" style="display: none;" class="text_ipt" />
								<label class="file">
									Прикрепите или перетащите картинку
									<input type="file" class="file" name="{@input}" />
								</label>
							</div>
						</div>
					</td>
					<xsl:if test=". != ''">
						<td style="padding-left: 20px; vertical-align: top;">
							<div style="position: relative;">
								<a id="param-{@id}" class="delete" href="javascript:confirmAjaxView('admin_delete_parameter.action?multipleParamId={@id}&amp;itemId={$form/@id}', 'main_view', null, '#param-{@id}')"></a>
								<img style="max-width:400px; max-height:250px;" alt="{@caption}" src="{$form/@file-path}{.}" />
							</div>
						</td>
					</xsl:if>
				</tr>
			</tbody>
		</table>
	</xsl:template>

	<!-- Длинный текст -->
	<xsl:template match="field[ @type='text' ]" mode="single">
		<label>
			<span class=""><xsl:value-of select="@caption" /></span>
			<xsl:if test="@description != ''">
				<p class="form_comment" style="padding: 4px 0 6px;">
					[<xsl:value-of select="@description" />]
				</p>
			</xsl:if>
			<textarea class="mce_big" name="{@input}" cols="" rows="">
				<xsl:value-of select="." disable-output-escaping="yes" />
			</textarea>					
		</label>
	</xsl:template>

	<!-- Средний текст -->
	<xsl:template match="field[ @type='short-text' ]" mode="single">
		<label>
			<span class=""><xsl:value-of select="@caption" /></span>
			<xsl:if test="@description != ''">
				<p class="form_comment" style="padding: 4px 0 6px;">
					[<xsl:value-of select="@description" />]
				</p>
			</xsl:if>
			<textarea class="mce_medium" name="{@input}" cols="" rows="">
				<xsl:value-of select="." disable-output-escaping="yes" />
			</textarea>
		</label>
	</xsl:template>

	<!-- Маленикий текст -->
	<xsl:template match="field[ @type='tiny-text' ]" mode="single">
		<label>
			<span class=""><xsl:value-of select="@caption" /></span>
			<xsl:if test="@description != ''">
				<p class="form_comment" style="padding: 4px 0 6px;">
					[<xsl:value-of select="@description" />]
				</p>
			</xsl:if>
			<textarea class="mce_small" name="{@input}" cols="" rows="">
				<xsl:value-of select="." disable-output-escaping="yes" />
			</textarea>
		</label>
	</xsl:template>

	<!-- Текст без форматирования -->
	<xsl:template match="field[ @type='plain-text' or @type='xml']" mode="single">
		<label>
			<span class=""><xsl:value-of select="@caption" /></span>
			<xsl:if test="@description != ''">
				<p class="form_comment" style="padding: 4px 0 6px;">
					[<xsl:value-of select="@description" />]
				</p>
			</xsl:if>
			<textarea style="{if (@format and not(@format = '')) then @format else 'width: 100%; height: 300px'}" name="{@input}" cols="" rows="">
				<xsl:value-of select="." disable-output-escaping="yes" />
			</textarea>
		</label>
	</xsl:template>


	<!-- Emoji -->
	<xsl:template match="field[@type='char']" mode="single">

		<div class="char-param">
			<label>
				<span class=""><xsl:value-of select="@caption" /></span>
				<xsl:if test="@description != ''">
					<p class="form_comment" style="padding: 4px 0 6px;">
						[<xsl:value-of select="@description" />]
					</p>
				</xsl:if>
				
				<textarea name="{@input}" style="width: 44px; height: 44px; font-size: 32px; padding: 5px; resize: none;" ><xsl:value-of select="if(. != '') then concat('&amp;#', . ,';') else ''" disable-output-escaping="yes"/></textarea>
<!--				<input class="field" type="text" name="{@input}" value="{.}" style="width: 44px; font-size: 32px; padding: 5px;" />-->
			</label>
			<div class="emoji-toggle" style="display: none;">
				<xsl:call-template name="FACES"/>
			</div>
		</div>
	</xsl:template>
	
	<!-- Простое поле ввода -->
	<xsl:template match="field" mode="single">
		<xsl:choose>
		<xsl:when test="@domain">
			<label for="{@input}">
				<span class=""><xsl:value-of select="@caption" /></span>
				<xsl:if test="@description != ''">
					<p class="form_comment" style="padding: 4px 0 6px;">
						[<xsl:value-of select="@description" />]
					</p>
				</xsl:if>
			</label>
			<div class="combobox" >
				<select onchange="this.nextElementSibling.value = this.value">
					<option/>
					<xsl:for-each select="//domain[@name=current()/@domain]/value">
						<xsl:sort select="."/>
						<option><xsl:value-of select="."/></option>
					</xsl:for-each>
				</select>
				<input id="{@input}" class="field" type="text" name="{@input}" value="{.}"/>
			</div>
		</xsl:when>
		<xsl:otherwise>
			<label>
				<span class=""><xsl:value-of select="@caption" /></span>
				<xsl:if test="@description != ''">
					<p class="form_comment" style="padding: 4px 0 6px;">
						[<xsl:value-of select="@description" />]
					</p>
				</xsl:if>
				<input class="field" type="text" name="{@input}" value="{.}" style="width: 280px;"/>
			</label>
		</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- Дата -->
	<xsl:template match="field[ @type='date']" mode="single">
		<xsl:variable name="value" select="."/>
		<!-- Дата и время -->
		<div class="timeStamp" style="">

			<xsl:variable name="date" select="if(. != '') then f:xsl_date(.) else current-dateTime()"/>
			<xsl:variable name="ms" select="f:date_to_millis($date)"/>
			<xsl:variable name="display_ms" select="$ms + (3*60*60*1000)"/>
			<xsl:variable name="display_date" select="f:format_date(f:millis_to_date_time($display_ms))"/>
			<xsl:variable name="utc_ms" select="f:date_to_millis(current-dateTime())"/>
			<xsl:variable name="utc_date" select="f:format_date(f:millis_to_date_time($utc_ms))"/>

			<p style="clear: both;">
				<span class=""><xsl:value-of select="@caption" /></span>
				<xsl:if test="@description != ''">
					<p class="form_comment" style="padding: 4px 0 6px;">
						[<xsl:value-of select="@description" />]
					</p>
				</xsl:if>
			</p>
			<label style="float:left;padding-right: 5px;">
				<input type="text" class="datepicker date-time" style="width: 80px; padding: 4px 0; text-align: center;" value="{substring($display_date,1,10)}"/>
			</label>
			<xsl:if test="@format = '' or @format = 'dd.MM.yyyy HH:mm'">
				<label style="float:left;">
					<input type="text" class="time date-time" value="{substring($display_date,12)}" style="width: 42px;text-align:center; padding: 4px 0;"/>
				</label>
			</xsl:if>
		
			<!-- этот инпут отправляется. Дата в формате dd.mm.yy, hh:mm -->
			<input class="whole" type="hidden" name="{@input}" value="{if(. != '') then . else $utc_date}" />
		</div>
	</xsl:template>



	<!--********************************************************************************** 
									 ДОПОЛНИТЕЛЬНО
	***********************************************************************************-->


	<!-- Значения селекта -->
	<xsl:template name="domain_select">
		<xsl:param name="domain"/>
		<xsl:param name="value"/>
		<xsl:for-each select="$domain/value">
		<xsl:choose>
		<xsl:when test="$value = .">
			<option value="{.}" selected="selected"><xsl:value-of select="."/></option>
		</xsl:when>
		<xsl:otherwise>
			<option value="{.}"><xsl:value-of select="."/></option>
		</xsl:otherwise>
		</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<!-- TINY_MCE -->
	<xsl:template name="TINY_MCE">
		<xsl:variable name="form" select="/admin-page/form"/>

		<script type="text/javascript">
			var startUploadUrl = "<xsl:value-of select="admin-page/upload-link"/>";
			var openAssocUrl = "<xsl:value-of select="admin-page/open-associated-link"/>";
			//-- mce image upload settings for tinyMCE
			window.uploadPath = "<xsl:value-of select="$form/@file-path"/>";



			<xsl:if test="$form/@id != '0'">
				window.itemId = <xsl:value-of select="$form/@id" />;
				<xsl:if test="$form/field[ @type='picture' and @quantifier = 'multiple']">
					window.imgId =	<xsl:value-of select="$form/field[ @type='picture' and @quantifier = 'multiple']/@id" />;
				</xsl:if>
				<xsl:if test="$form/field[ @type='file' and @quantifier = 'multiple']">
					window.fileId = <xsl:value-of select="$form/field[ @type='file' and @quantifier = 'multiple']/@id" />;
				</xsl:if>
			</xsl:if>
		</script>

		<script type="text/javascript" src="admin/js/mce-setup.js?v=1.01"></script>
		<script type="text/javascript" src="admin/js/inputs_script.js"></script>
	</xsl:template>
		
</xsl:stylesheet>