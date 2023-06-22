<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">

	<xsl:output method="html" encoding="UTF-8" media-type="text/html"
		indent="yes" />
	<xsl:strip-space elements="*" />
	
	<xsl:template match="/">
		<div>
			<div class="result" id="currency">
	<!-- 			<p>Пересчитать цены по курсу Нац. банка.</p> -->
				<strong style="margin-left: 25px;">Currency</strong>&#160;
				<select id="currency_converter" title="" onchange="currencyChange($(this))">
					<option class="bel" id="belrub" value="BEL">BYN</option>
					<!-- <option class="usd" value="USD">USD</option> -->
					<option class="eur" value="EUR">EUR</option>
					<option id="rusrub" class="rub" value="RUB">RUB</option>
				</select>
				<script type="text/javascript">
					var RATES = [];
					RATES['USD'] = <xsl:value-of select="round(DailyExRates/Currency[CharCode = 'USD']/Rate * 100) div 100"/>;
					RATES['EUR'] = <xsl:value-of select="round(DailyExRates/special_eur/@ratio * 100) div 100"/>;
					RATES['RUB'] = <xsl:value-of select="round(DailyExRates/Currency[CharCode = 'RUB']/Rate * 100) div 100"/> / 100;
					RATES['BEL'] = 1;
					var curCur = 'BEL';
					var curDays = {"first" : 1, "second" : 1, "third" : 1};
					//precalcPrices(<xsl:value-of select="round(DailyExRates/Currency[CharCode = 'RUB']/Rate * 100) div 100"/>);
					$(document).on("click", ".menu.rus", function(){
						if(!$("#currency_converter").data("user_curr")){
							$("#currency_converter").val($("#rusrub").attr("value"));
						}
					});
					$(document).on("click", ".menu.bel", function(){
						if(!$("#currency_converter").data("user_curr")){
							$("#currency_converter").val($("#belrub").attr("value"));
						}
					});
				</script>
			</div>
			<div class="result" id="informerBelarusbank" >
				<h2>Currency exchange rates</h2>
			<table>
				<!-- <tr>
					<td>USD</td>
					<td><xsl:value-of select="round(DailyExRates/Currency[CharCode = 'USD']/Rate * 100) div 100"/></td>
				</tr> -->
				<tr>
					<td>EUR</td>
					<td><xsl:value-of select="round(DailyExRates/special_eur/@ratio * 100) div 100"/></td>
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
			<p>Loading currency converter...</p>
		</div>
		<script type="text/javascript">
			$(document).ready(function() {
      			insertAjax('<xsl:value-of select="/page/currency_link"/>');
			});
			
			function recalc(curName) {
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
			
			function setDays(days, priceType) {
				if (priceType instanceof Array) {
					$.each(priceType, function(index, value) { curDays[value] = days; });
				} else {
					curDays[priceType] = days;
				}
				recalc(window.curCur);
			}
/*
			function currencyChange(select) {
				alert(select.val());
				select.data("user_curr", true);
				var currency = select.val();
				recalc(currency);
			}
*/
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