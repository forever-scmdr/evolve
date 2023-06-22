<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text></xsl:template>

	<xsl:variable name="form" select="admin-page/form"/>


	<!--********************************************************************************** 
						     ОДИНОЧНЫЕ ПАРАМЕТРЫ (ПОЛЯ ВВОДА)
	***********************************************************************************-->


	<!-- Фильтр -->
	<xsl:template match="field[ @type='filter' ]" mode="single">
	<xsl:if test="$form/@id &gt; 0">
		<a href="#" onclick="openFilter('fil_{@id}', {$form/@id}, '{@name}');return false;">Редактировать фильтр</a>
		<textarea id="fil_{@id}" style="display:none" name="{@input}"><xsl:value-of select="."/></textarea>
		<xsl:call-template name="BR"/>
	</xsl:if>
	</xsl:template>

	<!-- Одиночный файл -->
	<xsl:template match="field[ @type='file' ]" mode="single">
		<input type="file" name="{@input}" value=""/><xsl:call-template name="BR"/>
		<xsl:if test=". != ''"><a href="{$form/@file-path}{.}">Скачать файл</a></xsl:if>
		<xsl:if test=". != ''">
			<a href="javascript:defaultView('admin_delete_parameter.action?multipleParamId={@id}&amp;itemId={$form/@id}', 'main_view', true)">
				<img src="admin/admin_img/action_delete.png" alt="" />
			</a>
		</xsl:if>
	</xsl:template>

	<!-- Одиночная картинка -->
	<xsl:template match="field[ @type='picture' ]" mode="single">
		<input type="file" name="{@input}" value=""/><xsl:call-template name="BR"/>
		<xsl:if test=". != ''">
			<a href="javascript:defaultView('admin_delete_parameter.action?multipleParamId={@id}&amp;itemId={$form/@id}', 'main_view', true)">
				<img src="admin/admin_img/action_delete.png" alt="" />
			</a>
			<xsl:call-template name="BR"/><xsl:call-template name="BR"/>
			<img src="{$form/@file-path}{.}" alt="" />
		</xsl:if>
	</xsl:template>

	<!-- Длинный текст -->
	<xsl:template match="field[ @type='text' ]" mode="single">
		<textarea class="mce_big" name="{@input}" cols="" rows=""><xsl:value-of select="." disable-output-escaping="yes"/></textarea>
	</xsl:template>

	<!-- Средний текст -->
	<xsl:template match="field[ @type='short-text' ]" mode="single">
		<textarea class="mce_medium" name="{@input}" cols="" rows=""><xsl:value-of select="." disable-output-escaping="yes"/></textarea>
	</xsl:template>

	<!-- Маленикий текст -->
	<xsl:template match="field[ @type='tiny-text' ]" mode="single">
		<textarea class="mce_small" name="{@input}" cols="" rows=""><xsl:value-of select="." disable-output-escaping="yes"/></textarea>
	</xsl:template>

	<!-- Текст без форматирования -->
	<xsl:template match="field[ @type='plain-text' ]" mode="single">
		<textarea name="{@input}" cols="" rows="" style="{@format}"><xsl:value-of select="." disable-output-escaping="yes"/></textarea>
	</xsl:template>

	<!-- XML код без форматирования -->
	<xsl:template match="field[ @type='xml' ]" mode="single">
		<textarea name="{@input}" cols="" rows="" style="{@format}"><xsl:value-of select="." disable-output-escaping="yes"/></textarea>
	</xsl:template>
	
	<!-- Простое поле ввода -->
	<xsl:template match="field" mode="single">
		<xsl:choose>
		<xsl:when test="@domain">
			<select style="width: 300px; float: left;" onchange="this.nextElementSibling.value = this.value">
				<option/>
				<xsl:for-each select="//domain[@name=current()/@domain]/value">
					<xsl:sort select="."/>
					<option><xsl:value-of select="."/></option>
				</xsl:for-each>
			</select>
			<input 
				style="width: 280px; margin-left: -299px; margin-top: 1px; border: none; float: left;" 
				class="field" type="text" name="{@input}" value="{.}"/>
			<div style="clear: both"/>
		</xsl:when>
		<xsl:otherwise>
			<input class="field" type="text" name="{@input}" value="{.}" style="width: 280px;"/>
		</xsl:otherwise>
		</xsl:choose>
<!-- 		<xsl:choose> -->
<!-- 		<xsl:when test="@domain"> -->
<!-- 			<select class="field" name="{@input}"> -->
<!-- 			<xsl:call-template name="domain_select"> -->
<!-- 				<xsl:with-param name="domain" select="//domain[@name=current()/@domain]"/> -->
<!-- 				<xsl:with-param name="value" select="."/> -->
<!-- 			</xsl:call-template> -->
<!-- 			</select> -->
<!-- 		</xsl:when> -->
<!-- 		<xsl:otherwise> -->
<!-- 			<input class="field" type="text" name="{@input}" value="{.}"/> -->
<!-- 		</xsl:otherwise> -->
<!-- 		</xsl:choose> -->
	</xsl:template>

	<!-- Дата -->
	<xsl:template match="field[ @type='date']" mode="single">
		<!-- Дата и время -->
		<div class="timeStamp" style="font-size: 14px; width: 128px; height: 20px; padding: 4px 0; margin-bottom: 10px;">
			<label style="float:left;padding-right: 5px;">
				<input type="text" class="datepicker" style="width: 62px;"/>
			</label>
			<label style="float:left;">
				<input type="text" class="time" style="width: 42px;text-align:center;"/>
			</label>
			<!-- этот инпут отправляется. Дата в формате dd.mm.yy, hh:mm -->
			<input class="whole" type="hidden" name="{@input}" value="{.}" />
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
	<script type="text/javascript">
	var startUploadUrl = "<xsl:value-of select="admin-page/upload-link"/>";
	var openAssocUrl = "<xsl:value-of select="admin-page/open-associated-link"/>";
	<xsl:text disable-output-escaping="yes">
	tinymce.init({
	    selector: "textarea.mce_big",
	    language : 'ru',
	    theme: "modern",
//	    content_css: "css/main.css", - стили этого файла потом появляются в выпадающем меню Формат
	    autoresize_min_height: 300,
	    autoresize_max_height: 700,
	    plugins: [
//	    	 "importcss", - плагин, который отвечает за вывод списка стилей файла content_css в выпадающем меню
	    	 "autoresize",
	    	 "imageUpload", 
	         "advlist autolink link image lists charmap print preview hr anchor pagebreak spellchecker",
	         "searchreplace wordcount visualblocks visualchars code fullscreen insertdatetime media nonbreaking",
	         "table contextmenu directionality template paste textcolor"
	   ],
	   toolbar: "insertfile undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link upload image | print preview media fullpage | forecolor backcolor | code" 
//	   style_formats : [
//		    {title : 'Крутая таблица', selector : 'table', classes : 'table_cool'}
//	   ]
	 });
	tinymce.init({
	    selector: "textarea.mce_medium",
	    language : 'ru',
	    plugins: ["code paste"],
	    menubar : false,
	    statusbar: false,
	    height: 200,
	    theme: "modern",
	    toolbar: "insertfile undo redo | styleselect | bold italic | code",
	 });
	tinymce.init({
	    selector: "textarea.mce_small",
	    language : 'ru',
	    plugins: ["code paste"],
	    menubar : false,
	    statusbar: false,
	    height: 100,
	    theme: "modern",
	    toolbar: "insertfile undo redo | styleselect | bold italic | code",
	 });
	 
	// Открытие окна редактирования фильтра
	function openFilter(filterId, itemId, paramId) {
		var url = "admin_filter_init.afilter?input=" + filterId + "&amp;itemId=" + itemId + "&amp;paramName=" + paramId;
		var winW = 630, winH = 460;
		if (document.body &amp;&amp; document.body.offsetWidth) {
			winW = document.body.offsetWidth;
			winH = document.body.offsetHeight;
		}
		if (document.compatMode=='CSS1Compat' &amp;&amp;
		    document.documentElement &amp;&amp;
		    document.documentElement.offsetWidth ) {
			winW = document.documentElement.offsetWidth;
			winH = document.documentElement.offsetHeight;
		}
		if (window.innerWidth &amp;&amp; window.innerHeight) {
			winW = window.innerWidth;
			winH = window.innerHeight;
		}
		var w = winW - 300;
		var h = winH - 100;
		var x = 150;
		var y = 50;
		window.open(
				url, 
				"Filter", 
				"toolbar=no,scrollbars=yes,menubar=no,status=no,directories=no,width=" + w + ",height=" + h + ",left=" + x + ",top=" + y).focus();
	}
	
	// Открытие окна редактирования ассоциированных элементов
	function openAssoc(paramId) {
		var url = openAssocUrl + paramId;
		var winW = 630, winH = 460;
		if (document.body &amp;&amp; document.body.offsetWidth) {
			winW = document.body.offsetWidth;
			winH = document.body.offsetHeight;
		}
		if (document.compatMode=='CSS1Compat' &amp;&amp;
		    document.documentElement &amp;&amp;
		    document.documentElement.offsetWidth ) {
			winW = document.documentElement.offsetWidth;
			winH = document.documentElement.offsetHeight;
		}
		if (window.innerWidth &amp;&amp; window.innerHeight) {
			winW = window.innerWidth;
			winH = window.innerHeight;
		}
		var w = winW - 300;
		var MAX_W = 800;
		w = w &lt; MAX_W ? w : MAX_W;
		var h = winH - 100;
		var x = (winW - w) / 2;
		var y = 50;
		window.open(
				url, 
				"Associated", 
				"toolbar=no,scrollbars=yes,menubar=no,status=no,directories=no,width=" + w + ",height=" + h + ",left=" + x + ",top=" + y).focus();
	}
	</xsl:text>
	<xsl:if test="//field[ @type='date']">
		//-- всегда
		$.datepicker.setDefaults($.datepicker.regional["ru"]);
		$(".datepicker").datepicker();
		var now = new Date().toLocaleString("ru").substring(0,17).replace(',', '');
		$(".timeStamp").each(function() {
			var targ = $(this).find(".whole");
			var date = $(this).find(".datepicker");
			var time = $(this).find(".time");
		
			var dv = $(targ).val();
			var tls = (dv == "")? now : dv;
			date.val(tls.substring(0,10));
			time.val(tls.substring(11));
			if(dv == "") {
				targ.val(tls);
			}
			if(time.val() == ""){
				time.val("0:0");
			}
			date.change(function() {
				makeVal(targ, date, time);
			});
			time.change(function() {
				validateTime(this);
				makeVal(targ, date, time);
			});
		});
		function validateTime(el) {
			tv = $(el).val().substring(0,5);
			arr = tv.split(':');
			<xsl:text disable-output-escaping="yes">
			arr[0]=(arr[0]*1 &gt; 23)? 23 : arr[0];
			arr[1]=(arr[1]*1 &gt; 59)? 59 : arr[1];
			arr[0]=(arr[0]*1 &lt; 0)? 0 : arr[0];
			arr[1]=(arr[1]*1 &lt; 0)? 0 : arr[1];
			</xsl:text>
			$(el).val(arr.join(":"));
		}
		function makeVal(target, date, time) {
			$(target).val($(date).val()+' '+$(time).val());
		}
	</xsl:if>
	</script>
	</xsl:template>
		
</xsl:stylesheet>