<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="current_page_class" select="'book'"/>

	<xsl:template name="prices_old">
		<xsl:param name="prices"/>
		<xsl:param name="type"/>
		<xsl:param name="country"/>
		<div style="display:none" class="prices" id="{$type}_{$country}">
			<div class="small wide">
				<xsl:value-of select="$prices/text" disable-output-escaping="yes"/>
			</div>
			<div class="room_info narrow">
				<div style="padding-botom: 15px;"></div>
				<div>
					<table border="0">
						<tbody>
							<tr>
								<td>
									<p style="text-align: center;">
										<img
											style="margin-left: 5px; margin-right: 5px; vertical-align: middle;"
											src="images/kub.jpg" alt="" />
									</p>
								</td>
								<td style="text-align: center;">
									<a class="fancyframe" href="http://sender.forever.by/sansputnik1.php">
										<strong> Бронирование по заявке</strong>
									</a>
								</td>
							</tr>
						</tbody>
					</table>
					Для бронирования номера позвоните нам:
					<br />
					<span class="number">
						<xsl:value-of select="/page/common/phone" />
					</span>
					<xsl:if
						test="/page/common/phone_hidden and /page/common/phone_hidden != ''">
						<span class="javascript">все телефоны</span>
						<div class="hidden phones">
							<xsl:value-of select="/page/common/phone_hidden"
								disable-output-escaping="yes" />
						</div>
					</xsl:if>
				</div>
				<xsl:value-of select="$prices/side_text" disable-output-escaping="yes"/>
				<p class="download">
					<a href="{$prices/@path}{$prices/contract}" >скачать договор</a><br/>
				</p>
			</div>
		</div>
	</xsl:template>
	
	
	<xsl:template name="prices">
		<xsl:param name="prices"/>
		<xsl:param name="type"/>
		<xsl:param name="country"/>
		<xsl:variable name="rooms" select="/page/rooms"/>
		<xsl:variable name="quot" select="number(translate($rooms/extra_quotient, ',', '.'))"/>
		<div style="display:none" class="prices" id="{$type}_{$country}">
			<div class="small wide">
				<div class="price_room">
					<table>
						<tr>
							<td rowspan="2">Категория номера</td>
							<td colspan="2">
								Цена путёвки, <div class="cur_name"><xsl:value-of select="if ($country = 'bel') then 'бел. руб.' else 'российский рубль'"/></div>
								<span>*- цена доп. места</span>
								<span class="denom" style="{if ($country = 'bel') then '' else 'display: none'}">цена в рублях до деноминации</span>
							</td>
							<td colspan="2">
								Цена путёвки, <div class="cur_name"><xsl:value-of select="if ($country = 'bel') then 'бел. руб.' else 'российский рубль'"/></div>
								<span>*- цена доп. места</span>
								<span class="denom" style="{if ($country = 'bel') then '' else 'display: none'}">цена в рублях до деноминации</span>
							</td>
							<td colspan="2">
								Цена путёвки, <div class="cur_name"><xsl:value-of select="if ($country = 'bel') then 'бел. руб.' else 'российский рубль'"/></div>
								<span>*- цена доп. места</span>
								<span class="denom" style="{if ($country = 'bel') then '' else 'display: none'}">цена в рублях до деноминации</span>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<xsl:value-of select="f:day_month($rooms/first_start)"/> — <xsl:value-of select="f:day_month($rooms/first_end)"/>
							</td>
							<td colspan="2">
								<xsl:value-of select="f:day_month($rooms/second_start)"/> — <xsl:value-of select="f:day_month($rooms/second_end)"/>
							</td>
							<td colspan="2">
								с <xsl:value-of select="f:day_month($rooms/third_start)"/>
							</td>
						</tr>
						<tr>
							<td>Дни</td>
							<td>
								<select name="" id="" onchange="setDays($(this).val(), 'first')">
									<xsl:call-template name="number_option"><xsl:with-param name="max" select="number('30')"/></xsl:call-template>
								</select>
							</td>
							<td>12</td>
							<td>
								<select name="" id="" onchange="setDays($(this).val(), 'second')">
									<xsl:call-template name="number_option"><xsl:with-param name="max" select="number('30')"/></xsl:call-template>
								</select>
							</td>
							<td>12</td>
							<td>
								<select name="" id="" onchange="setDays($(this).val(), 'third')">
									<xsl:call-template name="number_option"><xsl:with-param name="max" select="number('30')"/></xsl:call-template>
								</select>
							</td>
							<td>12</td>
						</tr>
						<xsl:for-each select="$rooms/room">
							<xsl:variable name="price_first" select="f:price(., 'first', $type, $country)"/>
							<xsl:variable name="price_second" select="f:price(., 'second', $type, $country)"/>
							<xsl:variable name="price_third" select="f:price(., 'third', $type, $country)"/>
							
							<xsl:variable name="price_first_extra" select="f:price_extra($price_first, $quot, $country)"/>
							<xsl:variable name="price_second_extra" select="f:price_extra($price_second, $quot, $country)"/>
							<xsl:variable name="price_third_extra" select="f:price_extra($price_third, $quot, $country)"/>
							
							<xsl:variable name="cur" select="if ($country = 'bel') then 'BEL' else 'RUB'"/>
							<tr>
								<td>
									<a href="{show_photos}" class="ajax-link" rel="fotorama-popup"><xsl:value-of select="name"/></a>
								</td>
								
								<td class="day" price="first">
									<nobr><div class="pi" cur="{$cur}" price="{$price_first}"><xsl:value-of select="format-number($price_first, $f_mask, 'ru')"/></div></nobr>
									<span><div class="pi" cur="{$cur}" price="{$price_first_extra}"><xsl:value-of select="format-number($price_first_extra, $f_mask, 'ru')"/></div>*</span>
									<div class="denom" style="{if ($country = 'bel') then '' else 'display: none'}">
										<nobr><div class="pi" cur="{$cur}" price="{$price_first * 10000}"><xsl:value-of select="format-number($price_first * 10000, $f_mask, 'ru')"/></div></nobr>
										<span><div class="pi" cur="{$cur}" price="{$price_first_extra * 10000}"><xsl:value-of select="format-number($price_first_extra * 10000, $f_mask, 'ru')"/></div>*</span>
									</div>
								</td>
								<td>
									<nobr><div class="pi" cur="{$cur}" price="{$price_first * 12}"><xsl:value-of select="format-number($price_first * 12, $f_mask, 'ru')"/></div></nobr>
									<span><div class="pi" cur="{$cur}" price="{$price_first_extra * 12}"><xsl:value-of select="format-number($price_first_extra * 12, $f_mask, 'ru')"/></div>*</span>
									<div class="denom" style="{if ($country = 'bel') then '' else 'display: none'}">
										<nobr><div class="pi" cur="{$cur}" price="{$price_first * 10000 * 12}"><xsl:value-of select="format-number($price_first * 10000 * 12, $f_mask, 'ru')"/></div></nobr>
										<span><div class="pi" cur="{$cur}" price="{$price_first_extra * 10000 * 12}"><xsl:value-of select="format-number($price_first_extra * 10000 * 12, $f_mask, 'ru')"/></div>*</span>
									</div>
								</td>
								
								<td class="day" price="second">
									<nobr><div class="pi" cur="{$cur}" price="{$price_second}"><xsl:value-of select="format-number($price_second, $f_mask, 'ru')"/></div></nobr>
									<span><div class="pi" cur="{$cur}" price="{$price_second_extra}"><xsl:value-of select="format-number($price_second_extra, $f_mask, 'ru')"/></div>*</span>
									<div class="denom" style="{if ($country = 'bel') then '' else 'display: none'}">
										<nobr><div class="pi" cur="{$cur}" price="{$price_second * 10000}"><xsl:value-of select="format-number($price_second * 10000, $f_mask, 'ru')"/></div></nobr>
										<span><div class="pi" cur="{$cur}" price="{$price_second_extra * 10000}"><xsl:value-of select="format-number($price_second_extra * 10000, $f_mask, 'ru')"/></div>*</span>
									</div>
								</td>
								<td>
									<nobr><div class="pi" cur="{$cur}" price="{$price_second * 12}"><xsl:value-of select="format-number($price_second * 12, $f_mask, 'ru')"/></div></nobr>
									<span><div class="pi" cur="{$cur}" price="{$price_second_extra * 12}"><xsl:value-of select="format-number($price_second_extra * 12, $f_mask, 'ru')"/></div>*</span>
									<div class="denom" style="{if ($country = 'bel') then '' else 'display: none'}">
										<nobr><div class="pi" cur="{$cur}" price="{$price_second * 10000 * 12}"><xsl:value-of select="format-number($price_second * 10000 * 12, $f_mask, 'ru')"/></div></nobr>
										<span><div class="pi" cur="{$cur}" price="{$price_second_extra * 10000 * 12}"><xsl:value-of select="format-number($price_second_extra * 10000 * 12, $f_mask, 'ru')"/></div>*</span>
									</div>
								</td>
								
								<td class="day" price="third">
									<nobr><div class="pi" cur="{$cur}" price="{$price_third}"><xsl:value-of select="format-number($price_third, $f_mask, 'ru')"/></div></nobr>
									<span><div class="pi" cur="{$cur}" price="{$price_third_extra}"><xsl:value-of select="format-number($price_third_extra, $f_mask, 'ru')"/></div>*</span>
									<div class="denom" style="{if ($country = 'bel') then '' else 'display: none'}">
										<nobr><div class="pi" cur="{$cur}" price="{$price_third * 10000}"><xsl:value-of select="format-number($price_third * 10000, $f_mask, 'ru')"/></div></nobr>
										<span><div class="pi" cur="{$cur}" price="{$price_third_extra * 10000}"><xsl:value-of select="format-number($price_third_extra * 10000, $f_mask, 'ru')"/></div>*</span>
									</div>
								</td>
								<td>
									<nobr><div class="pi" cur="{$cur}" price="{$price_third * 12}"><xsl:value-of select="format-number($price_third * 12, $f_mask, 'ru')"/></div></nobr>
									<span><div class="pi" cur="{$cur}" price="{$price_third_extra * 12}"><xsl:value-of select="format-number($price_third_extra * 12, $f_mask, 'ru')"/></div>*</span>
									<div class="denom" style="{if ($country = 'bel') then '' else 'display: none'}">
										<nobr><div class="pi" cur="{$cur}" price="{$price_third * 10000 * 12}"><xsl:value-of select="format-number($price_third * 10000 * 12, $f_mask, 'ru')"/></div></nobr>
										<span><div class="pi" cur="{$cur}" price="{$price_third_extra * 10000 * 12}"><xsl:value-of select="format-number($price_third_extra * 10000 * 12, $f_mask, 'ru')"/></div>*</span>
									</div>
								</td>
								
							</tr>
						</xsl:for-each>
					</table>
				</div>
			</div>
			<div class="room_info narrow">
				<div style="padding-botom: 15px;"></div>
				<div>
					<table border="0">
						<tbody>
							<tr>
								<td>
									<p style="text-align: center;">
										<img
											style="margin-left: 5px; margin-right: 5px; vertical-align: middle;"
											src="images/kub.jpg" alt="" />
									</p>
								</td>
								<td style="text-align: center;">
									<a class="fancyframe" href="http://sender.forever.by/sansputnik1.php">
										<strong> Бронирование по заявке</strong>
									</a>
								</td>
							</tr>
						</tbody>
					</table>
					Для бронирования номера позвоните нам:
					<br />
					<span class="number">
						<xsl:value-of select="/page/common/phone" />
					</span>
					<xsl:if
						test="/page/common/phone_hidden and /page/common/phone_hidden != ''">
						<span class="javascript">все телефоны</span>
						<div class="hidden phones">
							<xsl:value-of select="/page/common/phone_hidden"
								disable-output-escaping="yes" />
						</div>
					</xsl:if>
				</div>
				<xsl:value-of select="$prices/side_text" disable-output-escaping="yes"/>
				<p class="download">
					<a href="{$prices/@path}{$prices/contract}" >скачать договор</a><br/>
				</p>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="CONTENT">
	<div class="common">
		<div class="path"><a href="{/page/index_link}">Главная страница</a><xsl:call-template name="arrow"/></div>
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
		<xsl:if test="/page/book/book_general_rb">
		<div class="common_text"></div>
		<script>
			var currType = 'san';
			var currCountry = 'bel';
			
			function setPrices(type, country) {
				// Сброс текущего состояния
				$('.menu').removeClass('open');
				$('.prices').hide();
				// Установка нового состояния
				$('.menu.' + type).addClass('open');
				$('.menu.' + country).addClass('open');
				$('#' + type + "_" + country).show();
				window.currType = type;
				window.currCountry = country;
				window.curCur = country == 'rus' ? 'RUB' : 'BEL';
				return false;
			}
		</script>
		<p><b>Санаторно-курортная путёвка:</b> проживание, питание и лечение.</p>
	    <p><b>Оздоровительная путёвка:</b>  проживание, питание, ежедневно бассейн, кислородный коктейль (ежедневно, кроме выходных и праздничных дней).</p>
	    <div style="float:right;">
		<xsl:call-template name="CURRENCY_PLACEHOLDER" />
		</div>
    	<ul class="rooms_menu book"><!-- сделать в главном меню чтобы переходило на последний открытый номер -->
			<li><a class="room_1 menu san open" href="#" onclick="return setPrices('san', currCountry)">Санаторно-крурортная путевка</a></li>
			<li><a class="room_2 menu ozd" href="#" onclick="return setPrices('ozd', currCountry)">Оздоровительная путевка</a></li>
		</ul>
		<div class="serv-sec">
			<a class="menu bel open" href="#" onclick="return setPrices(currType, 'bel')">Для граждан Беларуси</a>
			<a class="menu rus" href="#" onclick="return setPrices(currType, 'rus')">Для иностранных граждан</a>
		</div>
		<xsl:call-template name="prices">
			<xsl:with-param name="prices" select="/page/book/book_general_rb"/>
			<xsl:with-param name="type" select="'san'"/>
			<xsl:with-param name="country" select="'bel'"/>
		</xsl:call-template>
		<xsl:call-template name="prices">
			<xsl:with-param name="prices" select="/page/book/book_general_foreign"/>
			<xsl:with-param name="type" select="'san'"/>
			<xsl:with-param name="country" select="'rus'"/>
		</xsl:call-template>
		<xsl:call-template name="prices">
			<xsl:with-param name="prices" select="/page/book/book_med_rb"/>
			<xsl:with-param name="type" select="'ozd'"/>
			<xsl:with-param name="country" select="'bel'"/>
		</xsl:call-template>
		<xsl:call-template name="prices">
			<xsl:with-param name="prices" select="/page/book/book_med_foreign"/>
			<xsl:with-param name="type" select="'ozd'"/>
			<xsl:with-param name="country" select="'rus'"/>
		</xsl:call-template>
		</xsl:if>
		<script>
			$(document).ready(function() {
				setPrices(currType, currCountry);			
			});
		</script>
		<p><b><a href="{/page/book/@path}{/page/book/price}" download="download"> Скачать весь прайс-лист >>></a></b></p>
		<p></p>
		<xsl:value-of select="page/book/text" disable-output-escaping="yes"/>
		<xsl:apply-templates select="page/book/text_part | page/book/gallery_part"/>

	</div>


	
	<div id="fotorama-popup" class="popup" style="display: none;">
		<a class="close">Закрыть</a>
		<div id="room-pics" class="pageText">
			
		</div>
	</div>
	
	</xsl:template>



</xsl:stylesheet>