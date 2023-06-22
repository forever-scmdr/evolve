<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="feedback_form.xsl"/>
	<xsl:import href="inner_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="room_id" select="page/variables/r"/>
	<xsl:variable name="room" select="if (not($room_id) or $room_id = '') then page/rooms/room[position() = 1] else /page/rooms/room[@key = $room_id]"/>



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
		
		<xsl:variable name="cur" select="if ($country = 'eur') then 'евро' else if ($country = 'rus') then 'рос. руб.' else 'бел. руб.'"/>

		<table style="display:none" id="{$type}_{$country}" class="prices_table">
		<tbody>
			<tr>
				<td></td>
				<td colspan="3">
					Стоимость, <div class="cur_name"><xsl:value-of select="$cur"/></div>
					<span>*— с дополнительным местом</span>
				</td>
			</tr>
			<tr>
				<td>Дней</td>
				<td>
					<select name="" id="sel_normal" onchange="setDays(this, ['first', 'second', 'third']);">
						<xsl:call-template name="number_option"><xsl:with-param name="max" select="number('30')"/></xsl:call-template>
					</select>
				</td>
				<td>12</td>
				<td>21</td>
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
			</tr>
		</tbody>
		</table>
	</xsl:template>




	<xsl:template name="INNER_CONTENT">
	<div class="col-xs-12">
		<div class="path hidden-xs">
			<a href="{page/index_link}">Главная страница</a> →
		</div>
		<h2 class="m-t-zero">Номера</h2>
		<ul class="nav nav-tabs special-tabs" role="tablist"> 
			<xsl:for-each select="/page/rooms/room">
			<li role="presentation" class="{'active'[current()/@id = $room/@id]}"><a href="{show_room}"><xsl:value-of select="name"/></a></li>
			</xsl:for-each>
		</ul>
		
		<div class="row">
			<div class="col-xs-12">
				<div class="tab-content">
					<div class="tab-pane active" role="tabpanel" id="tab-1">
						<h3><xsl:value-of select="$room/name"/></h3>
						<xsl:value-of select="$room/top_text" disable-output-escaping="yes"/>
						<div class="row p-t-default">
							<div class="col-md-9">
								<div class="fotorama" data-nav="thumbs" data-fit="cover">
									<xsl:for-each select="$room/picture_pair">
									<img src="{@path}{big}" alt="{name}" data-caption="{name}"/>
									</xsl:for-each>
								</div>
							</div>
							<div class="col-md-3">
								<p>Для бронирования номера позвоните нам:</p>
								<h3 class="m-t-zero"><i class="fa fa-phone"></i> <xsl:value-of select="/page/common/phone"/>
									<a href="" data-toggle="modal" data-target="#modal-phones"><span class="caret"></span></a>
								</h3>
								<h4 class="p-t-small">Ближайшие даты заезда</h4>
								<xsl:value-of select="$room/side_table" disable-output-escaping="yes"/>
								<h4 class="p-t-small">Отзывы</h4>
								<div class="review-scroll">
									<xsl:for-each select="$room/feedback">
										<div class="well">
											<xsl:value-of select="room_feedback" disable-output-escaping="yes"/>
											<xsl:value-of select="service_feedback" disable-output-escaping="yes"/>
											<p><i>
												<xsl:value-of select="if(live_date != '') then concat(f:day_month_year(live_date), '.') else ''"/>
												<xsl:value-of select="fio"/>
												<xsl:if test="country != ''">
												(<xsl:value-of select="country"/>).
												</xsl:if>
											</i></p>
										</div>										
										<!-- 
										<xsl:if test="answer != ''">
											<p style="margin-top: 15px;"><b>Ответ:</b></p>
											<xsl:value-of select="answer" disable-output-escaping="yes"/>
										</xsl:if>
										 -->
									</xsl:for-each>
								</div>
								<button class="btn btn-primary btn-block" type="submit" data-toggle="modal" data-target="#modal-guest-feedback">Оставить свой отзыв о номере</button>
							</div>
						</div>
						<div class="row">
							<div class="col-xs-12">
								<h3 class="p-t-default">Стоимость отдыха</h3>
								<ul class="nav nav-tabs"> 
									<li id="san" class="p_type active"><a href="#" onclick="setPrices('san', currCountry); return false;">Санаторно-курортная путевка</a></li>
									<li id="ozd" class="p_type"><a href="#" onclick="setPrices('ozd', currCountry); return false;">Оздоровительная путевка</a></li>
								</ul>
								<div class="row">
									<div class="col-xs-12">
										<div class="tab-content">
											<div class="tab-pane active">
												<div class="btn-group p-t-small">
													<button id="bel" type="button" class="btn btn-primary p_country" onclick="setPrices(currType, 'bel')">Для граждан Беларуси</button>
													<button id="rus" type="button" class="btn btn-default p_country" onclick="setPrices(currType, 'rus')">Для иностранных граждан (RUR)</button>
													<button id="eur" type="button" class="btn btn-default p_country" onclick="setPrices(currType, 'eur')">Для иностранных граждан (EUR)</button>
												</div>
												<div class="table-responsive room-price p-t-small">
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
														<xsl:with-param name="type" select="'san'"/>
														<xsl:with-param name="country" select="'eur'"/>
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
													<xsl:call-template name="prices">
														<xsl:with-param name="room" select="$room"/>
														<xsl:with-param name="type" select="'ozd'"/>
														<xsl:with-param name="country" select="'eur'"/>
													</xsl:call-template>												
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	</xsl:template>




	<xsl:template name="SCRIPTS">
		<xsl:call-template name="FEEDBACK_MODAL">
			<xsl:with-param name="feedback_link" select="$room/feedback_link"/>
		</xsl:call-template>
		<script>
		<xsl:text disable-output-escaping="yes">
			var currType = 'san';
			var currCountry = 'bel';
			
			function setPrices(type, country) {
				// Сброс текущего состояния
				$('.p_type').removeClass('active');
				$('.p_country').removeClass('btn-primary');
				$('.p_country').addClass('btn-default');
				$('.prices_table').hide();
				// Установка нового состояния
				$('#' + type).addClass('active');
				$('#' + country).removeClass('btn-default');
				$('#' + country).addClass('btn-primary');
				$('#' + type + "_" + country).show();
				currType = type;
				currCountry = country;
			
				window.curCur = country == 'rus' ? 'RUB' : 'BEL';
				return false;
			}
			
			$(document).ready(function() {
				setPrices(currType, currCountry);			
			});
		</xsl:text>
		</script>
	</xsl:template>


</xsl:stylesheet>