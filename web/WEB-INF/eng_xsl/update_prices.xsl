<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="current_page_class" select="'book'"/>
	
	<xsl:variable name="rooms" select="/page/rooms"/>

	<xsl:template name="CONTENT">
	<div class="common">
		<div class="path"><a href="admin_initialize.action">Назад в систему управления</a><xsl:call-template name="arrow"/></div>
		<xsl:call-template name="PAGE_TITLE"><xsl:with-param name="page" select="/page/book"/></xsl:call-template>
		<!-- 
			Вывод простого текста, если номера не заполнены
		 -->
		<xsl:if test="not(/page/book/book_general_rb)">
		<xsl:apply-templates select="/page/book/text_part | /page/book/gallery_part"/>
		</xsl:if>
		<!-- 
			Вывод номеров, если они заполнены
		 -->
		<div class="common_text"></div>
		
		<div class="prices">
			<div class="price_room">
				<form method="post" action="{page/submit_prices}" id="prices">
				<table style="background-color: #ffff99">
					<tr>
						<td rowspan="2">Категория номера</td>
						<td rowspan="2">Путевка</td>
						<td colspan="2">Стоимость за первый интервал</td>
						<td colspan="2">Стоимость за второй интервал</td>
						<td colspan="2">Стоимость за третий интервал</td>
					</tr>
					<tr>
						<td colspan="2">
							<input type="text" class="datepicker pdate pinp" name="{$rooms/first_start_inp}" value="{$rooms/first_start}"/> - 
							<input type="text" class="datepicker pdate pinp" name="{$rooms/first_end_inp}" value="{$rooms/first_end}"/>
						</td>
						<td colspan="2">
							<input type="text" class="datepicker pdate pinp" name="{$rooms/second_start_inp}" value="{$rooms/second_start}"/> - 
							<input type="text" class="datepicker pdate pinp" name="{$rooms/second_end_inp}" value="{$rooms/second_end}"/>
						</td>
						<td colspan="2">
							<input type="text" class="datepicker pdate pinp" name="{$rooms/third_start_inp}" value="{$rooms/third_start}"/>
						</td>
					</tr>
					<tr>
						<td>Отдыхающие</td>
						<td>&#160;</td>
						<td>Граждане РБ<br/> (BYR)</td>
						<td>Иностранцы<br/> (RUR)</td>
						<td>Граждане РБ<br/> (BYR)</td>
						<td>Иностранцы<br/> (RUR)</td>
						<td>Граждане РБ<br/> (BYR)</td>
						<td>Иностранцы<br/> (RUR)</td>
					</tr>
					<xsl:for-each select="$rooms/room">
						<tr><td colspan="8" style="background-color: white; padding-top: 3px; padding-bottom: 3px"></td></tr>
						<tr style="background-color: #cceeff">
							<td rowspan="2" style="background-color: white"><a href="{show_room}"><xsl:value-of select="name"/></a></td>
							
							<td style="border-right: 6px solid white">Санаторно-курортная</td>
							
							<td class="day" price="first">
								<input type="text" value="{price_san_bel_first}" name="{price_san_bel_first_inp}" class="pinp psan"/>
							</td>
							<td>
								<input type="text" value="{price_san_rus_first}" name="{price_san_rus_first_inp}" class="pinp psan"/>
							</td>
							
							<td class="day" price="second" style="border-left: 6px solid white">
								<input type="text" value="{price_san_bel_second}" name="{price_san_bel_second_inp}" class="pinp psan"/>
							</td>
							<td>
								<input type="text" value="{price_san_rus_second}" name="{price_san_rus_second_inp}" class="pinp psan"/>
							</td>
							
							<td class="day" price="third" style="border-left: 6px solid white">
								<input type="text" value="{price_san_bel_third}" name="{price_san_bel_third_inp}" class="pinp psan"/>
							</td>
							<td>
								<input type="text" value="{price_san_rus_third}" name="{price_san_rus_third_inp}" class="pinp psan"/>
							</td>
							
						</tr>
						<tr style="background-color: #80d4ff">
						
							<td style="border-right: 6px solid white">Оздоровительная</td>

							<td class="day" price="first">
								<input type="text" value="{price_ozd_bel_first}" name="{price_ozd_bel_first_inp}" class="pinp pozd"/>
							</td>
							<td>
								<input type="text" value="{price_ozd_rus_first}" name="{price_ozd_rus_first_inp}" class="pinp pozd"/>
							</td>
							
							<td class="day" price="second" style="border-left: 6px solid white">
								<input type="text" value="{price_ozd_bel_second}" name="{price_ozd_bel_second_inp}" class="pinp pozd"/>
							</td>
							<td>
								<input type="text" value="{price_ozd_rus_second}" name="{price_ozd_rus_second_inp}" class="pinp pozd"/>
							</td>
							
							<td class="day" price="third" style="border-left: 6px solid white">
								<input type="text" value="{price_ozd_bel_third}" name="{price_ozd_bel_third_inp}" class="pinp pozd"/>
							</td>
							<td>
								<input type="text" value="{price_ozd_rus_third}" name="{price_ozd_rus_third_inp}" class="pinp pozd"/>
							</td>
						</tr>
					</xsl:for-each>
				</table>
				<input type="submit" value="Сохранить изменения" onclick="lock('prices')"/>
				</form>
			</div>
		</div>

	</div>
	<script type="text/javascript" src="admin/js/jquery-ui-1.10.3.custom.min.js"></script>
	<script type="text/javascript" src="admin/js/regional-ru.js"></script>
	<script>
		$(document).ready(function(){
			$.datepicker.setDefaults($.datepicker.regional['ru']);
			$(".datepicker").datepicker();
			
			$(".pinp").each(function() {
				$(this).data("initial", $(this).val());
			});
			
			$(".pinp").change(function() {
				var inp = $(this);
				if (inp.val() != inp.data('initial')) {
					inp.css('background-color', '#ff9900');
				} else {
					inp.css('background', 'transparent');
				}
			});
		});
	</script>
	</xsl:template>



</xsl:stylesheet>