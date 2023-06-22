<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="current_page_class" select="'rooms'"/>

	<xsl:variable name="room_id" select="page/variables/r"/>
	<xsl:variable name="room" select="if (not($room_id) or $room_id = '') then page/rooms/room[position() = 1] else /page/rooms/room[@key = $room_id]"/>

	<xsl:template name="FRAME">

				<div class="frame" id="frm">
					<div class="align_center">
						<div class="align_center_to_left">
							<div class="frame_content align_center_to_right" style="display: block; width: 500px;">
								<div class="close2">
										<img src="images/button_close.png" alt="close" onclick="$('#frm').hide();" style="cursor: pointer;"/>
								</div>					
								<div style="overflow-y: auto; font-size: 14px;" id="feedback_ajax">
									Loading...
									<script type="text/javascript">
										$(document).ready(function(){
											insertAjax('<xsl:value-of select="page/client_feedback_link"/>?rn=<xsl:value-of select="$room/@id" />');
										});
									</script>
								</div>
							</div>
						</div>
					</div>
				</div>
			
	</xsl:template>

	<xsl:template name="prices">
		<xsl:param name="room"/>
		<xsl:param name="type"/>
		<xsl:param name="country"/>
		<xsl:variable name="rooms" select="/page/rooms"/>
		<xsl:variable name="quot" select="number(translate($rooms/extra_quotient, ',', '.'))"/>

		<xsl:variable name="price_first" select="f:price($room, 'first', $type, $country)"/>
		<xsl:variable name="price_second" select="f:price($room, 'second', $type, $country)"/>
		<xsl:variable name="price_third" select="f:price($room, 'third', $type, $country)"/>

		<xsl:variable name="price_first_extra" select="f:price_extra($price_first, $quot, $country)"/>
		<xsl:variable name="price_second_extra" select="f:price_extra($price_second, $quot, $country)"/>
		<xsl:variable name="price_third_extra" select="f:price_extra($price_third, $quot, $country)"/>
		
		<xsl:variable name="cur" select="if ($country = 'bel') then 'BEL' else 'RUB'"/>

		<table style="display:none" id="{$type}_{$country}" class="prices_table">
			<tr>
				<td></td>
				<td colspan="3">
					Price, <div class="cur_name"><xsl:value-of select="if ($country = 'bel') then 'BYN' else 'RUR'"/></div>
					<span>*— add. seat price</span>
				</td>
				<td colspan="3" class="denom" style="{if ($country = 'bel') then '' else 'display: none'}">
						price before denomination
						<span>*— add. seat price</span>
				</td>
			</tr>
			<tr>
				<td>Days</td>
				<td>
					<select name="" id="sel_normal" onchange="setDays($(this).val(), ['first', 'second', 'third']); $('#sel_denom').val($(this).val())">
						<xsl:call-template name="number_option"><xsl:with-param name="max" select="number('30')"/></xsl:call-template>
					</select>
				</td>
				<td>12</td>
				<td>21</td>
				
				<td class="denom" style="{if ($country = 'bel') then '' else 'display: none'}">
					<select name="" id="sel_denom" onchange="setDays($(this).val(), ['first', 'second', 'third']); $('#sel_normal').val($(this).val())">
						<xsl:call-template name="number_option"><xsl:with-param name="max" select="number('30')"/></xsl:call-template>
					</select>
				</td>
				<td class="denom" style="{if ($country = 'bel') then '' else 'display: none'}">12</td>
				<td class="denom" style="{if ($country = 'bel') then '' else 'display: none'}">21</td>
			</tr>
			<tr>
				<td><xsl:value-of select="f:day_month($rooms/first_start)"/> — <xsl:value-of select="f:day_month($rooms/first_end)"/></td>
				<td class="day" price="first">
					<div class="pi" cur="{$cur}" price="{$price_first}"><xsl:value-of select="format-number($price_first, $f_mask, 'ru')"/></div>
					<span><div class="pi" cur="{$cur}" price="{$price_first_extra}"><xsl:value-of select="format-number($price_first_extra, $f_mask, 'ru')"/></div>*</span>
				</td>
				<td>
					<div class="pi" cur="{$cur}" price="{$price_first * 12}"><xsl:value-of select="format-number($price_first * 12, $f_mask, 'ru')"/></div>
					<span><div class="pi" cur="{$cur}" price="{$price_first_extra * 12}"><xsl:value-of select="format-number($price_first_extra * 12, $f_mask, 'ru')"/></div>*</span>
				</td>
				<td>
					<div class="pi" cur="{$cur}" price="{$price_first * 21}"><xsl:value-of select="format-number($price_first * 21, $f_mask, 'ru')"/></div>
					<span><div class="pi" cur="{$cur}" price="{$price_first_extra * 21}"><xsl:value-of select="format-number($price_first_extra * 21, $f_mask, 'ru')"/></div>*</span>
				</td>
				
				<td class="day denom" price="first" style="{if ($country = 'bel') then '' else 'display: none'}">
					<div class="pi" cur="{$cur}" price="{$price_first * 10000}"><xsl:value-of select="format-number($price_first * 10000, $f_mask, 'ru')"/></div>
					<span><div class="pi" cur="{$cur}" price="{$price_first_extra * 10000}"><xsl:value-of select="format-number($price_first_extra * 10000, $f_mask, 'ru')"/></div>*</span>
				</td>
				<td class="denom" style="{if ($country = 'bel') then '' else 'display: none'}">
					<div class="pi" cur="{$cur}" price="{$price_first * 12 * 10000}"><xsl:value-of select="format-number($price_first * 12 * 10000, $f_mask, 'ru')"/></div>
					<span><div class="pi" cur="{$cur}" price="{$price_first_extra * 12 * 10000}"><xsl:value-of select="format-number($price_first_extra * 12 * 10000, $f_mask, 'ru')"/></div>*</span>
				</td>
				<td class="denom" style="{if ($country = 'bel') then '' else 'display: none'}">
					<div class="pi" cur="{$cur}" price="{$price_first * 21 * 10000}"><xsl:value-of select="format-number($price_first * 21 * 10000, $f_mask, 'ru')"/></div>
					<span><div class="pi" cur="{$cur}" price="{$price_first_extra * 21 * 10000}"><xsl:value-of select="format-number($price_first_extra * 21 * 10000, $f_mask, 'ru')"/></div>*</span>
				</td>
			</tr>
			<tr>
				<td><xsl:value-of select="f:day_month($rooms/second_start)"/> — <xsl:value-of select="f:day_month($rooms/second_end)"/></td>
				<td class="day" price="second">
					<div class="pi" cur="{$cur}" price="{$price_second}"><xsl:value-of select="format-number($price_second, $f_mask, 'ru')"/></div>
					<span><div class="pi" cur="{$cur}" price="{$price_second_extra}"><xsl:value-of select="format-number($price_second_extra, $f_mask, 'ru')"/></div>*</span>
				</td>
				<td>
					<div class="pi" cur="{$cur}" price="{$price_second * 12}"><xsl:value-of select="format-number($price_second * 12, $f_mask, 'ru')"/></div>
					<span><div class="pi" cur="{$cur}" price="{$price_second_extra * 12}"><xsl:value-of select="format-number($price_second_extra * 12, $f_mask, 'ru')"/></div>*</span>
				</td>
				<td>
					<div class="pi" cur="{$cur}" price="{$price_second * 21}"><xsl:value-of select="format-number($price_second * 21, $f_mask, 'ru')"/></div>
					<span><div class="pi" cur="{$cur}" price="{$price_second_extra * 21}"><xsl:value-of select="format-number($price_second_extra * 21, $f_mask, 'ru')"/></div>*</span>
				</td>

				<td class="day denom" price="second" style="{if ($country = 'bel') then '' else 'display: none'}">
					<div class="pi" cur="{$cur}" price="{$price_second * 10000}"><xsl:value-of select="format-number($price_second * 10000, $f_mask, 'ru')"/></div>
					<span><div class="pi" cur="{$cur}" price="{$price_second_extra * 10000}"><xsl:value-of select="format-number($price_second_extra * 10000, $f_mask, 'ru')"/></div>*</span>
				</td>
				<td class="denom" style="{if ($country = 'bel') then '' else 'display: none'}">
					<div class="pi" cur="{$cur}" price="{$price_second * 12 * 10000}"><xsl:value-of select="format-number($price_second * 12 * 10000, $f_mask, 'ru')"/></div>
					<span><div class="pi" cur="{$cur}" price="{$price_second_extra * 12 * 10000}"><xsl:value-of select="format-number($price_second_extra * 12 * 10000, $f_mask, 'ru')"/></div>*</span>
				</td>
				<td class="denom" style="{if ($country = 'bel') then '' else 'display: none'}">
					<div class="pi" cur="{$cur}" price="{$price_second * 21 * 10000}"><xsl:value-of select="format-number($price_second * 21 * 10000, $f_mask, 'ru')"/></div>
					<span><div class="pi" cur="{$cur}" price="{$price_second_extra * 21 * 10000}"><xsl:value-of select="format-number($price_second_extra * 21 * 10000, $f_mask, 'ru')"/></div>*</span>
				</td>
			</tr>
			<tr>
				<td>с <xsl:value-of select="f:day_month($rooms/third_start)"/></td>
				<td class="day" price="third">
					<div class="pi" cur="{$cur}" price="{$price_third}"><xsl:value-of select="format-number($price_third, $f_mask, 'ru')"/></div>
					<span><div class="pi" cur="{$cur}" price="{$price_third_extra}"><xsl:value-of select="format-number($price_third_extra, $f_mask, 'ru')"/></div>*</span>
				</td>
				<td>
					<div class="pi" cur="{$cur}" price="{$price_third * 12}"><xsl:value-of select="format-number($price_third * 12, $f_mask, 'ru')"/></div>
					<span><div class="pi" cur="{$cur}" price="{$price_third_extra * 12}"><xsl:value-of select="format-number($price_third_extra * 12, $f_mask, 'ru')"/></div>*</span>
				</td>
				<td>
					<div class="pi" cur="{$cur}" price="{$price_third * 21}"><xsl:value-of select="format-number($price_third * 21, $f_mask, 'ru')"/></div>
					<span><div class="pi" cur="{$cur}" price="{$price_third_extra * 21}"><xsl:value-of select="format-number($price_third_extra * 21, $f_mask, 'ru')"/></div>*</span>
				</td>

				<td class="day denom" price="third" style="{if ($country = 'bel') then '' else 'display: none'}">
					<div class="pi" cur="{$cur}" price="{$price_third * 10000}"><xsl:value-of select="format-number($price_third * 10000, $f_mask, 'ru')"/></div>
					<span><div class="pi" cur="{$cur}" price="{$price_third_extra * 10000}"><xsl:value-of select="format-number($price_third_extra * 10000, $f_mask, 'ru')"/></div>*</span>
				</td>
				<td class="denom" style="{if ($country = 'bel') then '' else 'display: none'}">
					<div class="pi" cur="{$cur}" price="{$price_third * 12 * 10000}"><xsl:value-of select="format-number($price_third * 12 * 10000, $f_mask, 'ru')"/></div>
					<span><div class="pi" cur="{$cur}" price="{$price_third_extra * 12 * 10000}"><xsl:value-of select="format-number($price_third_extra * 12 * 10000, $f_mask, 'ru')"/></div>*</span>
				</td>
				<td class="denom" style="{if ($country = 'bel') then '' else 'display: none'}">
					<div class="pi" cur="{$cur}" price="{$price_third * 21 * 10000}"><xsl:value-of select="format-number($price_third * 21 * 10000, $f_mask, 'ru')"/></div>
					<span><div class="pi" cur="{$cur}" price="{$price_third_extra * 21 * 10000}"><xsl:value-of select="format-number($price_third_extra * 21 * 10000, $f_mask, 'ru')"/></div>*</span>
				</td>

			</tr>
		</table>
	</xsl:template>

	<xsl:template name="CONTENT">
	<div class="common">
		<div class="path"><a href="{/page/index_link}">Homepage</a><xsl:call-template name="arrow"/></div>
		<xsl:call-template name="PAGE_TITLE"><xsl:with-param name="page" select="/page/rooms"/></xsl:call-template>
		<!-- 
			Вывод простого текста, если номера не заполнены
		 -->
		<xsl:if test="not($room)">
		<xsl:apply-templates select="/page/rooms/text_part | /page/rooms/gallery_part"/>
		</xsl:if>
		<!-- 
			Вывод номеров, если они заполнены
		 -->
		<xsl:if test="$room">
		<div class="common_text">
			<xsl:value-of select="/page/rooms/text" disable-output-escaping="yes"/>
		</div>
		<ul class="rooms_menu"><!-- сделать в главном меню чтобы переходило на последний открытый номер -->
			<xsl:for-each select="/page/rooms/room">
			<li><a class="{'open'[current()/@id = $room/@id]}" href="{show_room}"><xsl:value-of select="name"/></a></li>
			</xsl:for-each>
		</ul>
		<div class="desc">
			<xsl:value-of select="$room/top_text" disable-output-escaping="yes"/>
		</div>
		<div class="fotorama small" data-nav="thumbs">
			<xsl:for-each select="$room/picture_pair">
			<a href="{@path}{big}"><img src="{@path}{small}" alt="{name}" width="90" height="60"/></a>
			</xsl:for-each>
		</div>
		<div class="room_info">
			<xsl:if test="$room/side_table and $room/side_table != ''">
			<div class="side_table">
				<h3>Ближайшие даты заезда</h3>
				<xsl:value-of select="$room/side_table" disable-output-escaping="yes"/>
			</div>
			</xsl:if>
			<div>
				Call us if ypu want to book a room:<br/>
				<span class="number"><xsl:value-of select="/page/common/phone"/></span>
				<xsl:if test="/page/common/phone_hidden and /page/common/phone_hidden != ''">
				<span class="javascript">all phones</span>
				<div class="hidden phones">
					<xsl:value-of select="/page/common/phone_hidden" disable-output-escaping="yes"/>
				</div>
				</xsl:if>
			</div>
			<xsl:if test="$room/feedback">
				<h3>Comments</h3>
				<div class="review scroll">
					<xsl:for-each select="$room/feedback">
						<div class="r_content">
							<xsl:value-of select="room_feedback" disable-output-escaping="yes"/>
							<xsl:value-of select="service_feedback" disable-output-escaping="yes"/>
						</div>
						<strong><xsl:value-of select="fio"/>, <xsl:value-of select="f:day_month_year(live_date)"/></strong>
						<!-- <img src="images/flag_belarus.png" alt="" /> -->
						<xsl:value-of select="country"/>

						<xsl:if test="answer != ''">
							<p style="margin-top: 15px;"><b>Reply:</b></p>
							<xsl:value-of select="answer" disable-output-escaping="yes"/>
						</xsl:if>
					</xsl:for-each>
				</div>
			</xsl:if>
			<div class="r_buttons">
				<!-- <a href="{page/client_feedback_link}" class="big_button" >Оставить свой отзыв</a> -->
				<a onclick="$('#frm').css('display', 'block');" class="big_button" >Leave a comment</a>
			</div>
<!-- 			<xsl:value-of select="$room/side_text" disable-output-escaping="yes"/> -->
		</div>
		</xsl:if>
	</div>
	<div class="price_room">
		<h3>Prices</h3>
		<div style="display: inline-block">
			<strong>Voucher type</strong>
			<input type="radio" id="san" class="prices_menu" onchange="setPrices('san', currCountry)"/><label for="san">Medical voucher</label>
			<input type="radio" id="ozd" class="prices_menu" onchange="setPrices('ozd', currCountry)"/><label for="ozd">Wellness voucher</label>
		</div>
		<div style="display: inline-block">
			<strong style="margin-left: 25px;">Citizenship</strong>
			<input type="radio" id="bel" class="prices_menu menu bel" onchange="setPrices(currType, 'bel')"/><label for="bel">Residents of the Republic of Belarus</label>
			<input type="radio" id="rus" class="prices_menu menu rus" onchange="setPrices(currType, 'rus')"/><label for="rus">Non-residents of the Republic of Belarus</label>
		</div>
		<div style="margin-left:-25px;">
		<xsl:call-template name="CURRENCY_PLACEHOLDER" />
		</div>
		<xsl:call-template name="prices">
			<xsl:with-param name="room" select="$room"/>
			<xsl:with-param name="type" select="'san'"/>
			<xsl:with-param name="country" select="'bel'"/>
		</xsl:call-template>
		<xsl:call-template name="prices">
			<xsl:with-param name="room" select="$room"/>
			<xsl:with-param name="type" select="'san'"/>
			<xsl:with-param name="country" select="'rus'"/>
		</xsl:call-template>
		<xsl:call-template name="prices">
			<xsl:with-param name="room" select="$room"/>
			<xsl:with-param name="type" select="'ozd'"/>
			<xsl:with-param name="country" select="'bel'"/>
		</xsl:call-template>
		<xsl:call-template name="prices">
			<xsl:with-param name="room" select="$room"/>
			<xsl:with-param name="type" select="'ozd'"/>
			<xsl:with-param name="country" select="'rus'"/>
		</xsl:call-template>
		
	</div>
	<div class="clear"/>
		<script>
			var currType = 'san';
			var currCountry = 'bel';
			
			function setPrices(type, country) {
				// Сброс текущего состояния
				$('.prices_menu').prop('checked', false);
				$('.prices_table').hide();
				// Установка нового состояния
				$('#' + type).prop('checked', true);
				$('#' + country).prop('checked', true);
				$('#' + type + "_" + country).show();
				currType = type;
				currCountry = country;
				window.curCur = country == 'rus' ? 'RUB' : 'BEL';
				return false;
			}
			
			$(document).ready(function() {
				setPrices(currType, currCountry);			
			});
		</script>
	</xsl:template>


</xsl:stylesheet>