<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:f="f:f"
	version="2.0">

	<xsl:output method="html" encoding="UTF-8" media-type="text/html"
		indent="yes" />
	<xsl:strip-space elements="*" />
	
	<xsl:template match="/">
		<div>
			<div class="result" id="currency">
				<strong style="margin-left: 25px;">Валюта</strong>&#160;
				<select id="currency_converter" title="" onchange="currencyChange($(this))">
					
					<option class="bel" id="belrub" value="BEL">белорусский рубль</option>
					<option class="eur" value="EUR">евро</option>
					<option id="rusrub" class="rub" value="RUB">российский рубль</option>
				</select>
				<script type="text/javascript">
					var RATES = [];
					RATES['USD'] = <xsl:value-of select="DailyExRates/Currency[CharCode = 'USD']/Rate"/>;
					RATES['EUR'] = <xsl:value-of select="DailyExRates/Currency[CharCode = 'EUR']/Rate"/>;
					RATES['RUB'] = <xsl:value-of select="DailyExRates/Currency[CharCode = 'RUB']/Rate"/> / 100;
					RATES['BEL'] = 1;
					var curCur = 'BEL';
					var curDays = {"first" : 1, "second" : 1, "third" : 1};
					
				</script>
			</div>
			<div class="result" id="informerBelarusbank" >
			<table>
				<tr>
					<td>EUR</td>
					<td><xsl:value-of select="round(DailyExRates/Currency[CharCode = 'EUR']/Rate * 100) div 100"/></td>
				</tr>
				<tr>
					<td>100 RUB</td>
					<td><xsl:value-of select="round(DailyExRates/Currency[CharCode = 'RUB']/Rate * 100) div 100"/></td>
				</tr>
			</table>
			</div>
		</div>
	</xsl:template>


	<xsl:template name="CURRENCY_PLACEHOLDER">
		<div id="currency" style="display: inline-block">
			<p>Загрузка конвертера валют...</p>
		</div>
		<script type="text/javascript">
			$(document).ready(function() {
      			
      			insertAjax('<xsl:value-of select="/page/currency_link"/>',"", selectDefault);
      			
      		});
			
			function selectDefault(container){
				pt = "<xsl:value-of select="//pay_type"/>";
				switch(pt){
					case "rus":  $("#currency_converter").find("option[value='RUB']").attr("selected", "selected"); window.curCur = "RUB";
					$("#currency_converter").find("option[value='EUR']").remove();
					break;
					case "eur": $("#currency_converter").find("option[value='EUR']").attr("selected", "selected"); window.curCur = "EUR"; 
					$("#currency_converter").find("option[value='RUB']").remove();
					break;
					default: $("#currency_converter").find("option[value='BEL']").attr("selected", "selected"); window.curCur = "BEL"; break;
				};
				return pt;
			}
			
			function recalc(curName) {
				console.log(curName);
				$('.price_room').find('.pi').each(function() {
					var days = $(this).closest('td.day').length != 0 ? curDays[$(this).closest('td.day').attr('price')] : 1;
					var price = $(this).attr('price') * days;
					var cur = $(this).attr('cur');
					var belPrice = price * RATES[cur];
					var newPrice = belPrice / RATES[curName];
					//newPrice = $.number(newPrice, 0, ',', ' ');
					newPrice = currencyFormatRU(newPrice);
					$(this).html(newPrice);
				});
				if (curName != 'BEL')
					$('.price_room').find('.denom').hide();
				else
					$('.price_room').find('.denom').show();
				window.curCur = curName;
				$('.cur_name').html($('option[value=' + curName + ']').text());
			}
			
			<!-- function setDays(days, priceType) {
				if (priceType instanceof Array) {
					$.each(priceType, function(index, value) { curDays[value] = days; });
				} else {
					curDays[priceType] = days;
				}
				recalc(window.curCur);
			} -->

			$(document).on("change", "#currency_converter", function() {
				$("#currency_converter").data("user_curr", true);
				var currency = $(this).val();
				recalc(currency);
			});
			
			function currencyFormatRU(num) {
			    num = Math.round(num * 100) / 100;
			    return num.toString().replace(".", ",").replace(/(\d)(?=(\d{3})+(?!\d))/g, "$1 ")
			}
		</script>
	</xsl:template>

</xsl:stylesheet>