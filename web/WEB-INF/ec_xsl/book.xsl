<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="inner_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="rest_type" select="page/variables/rest_type" />
	<xsl:variable name="pay_type" select="page/variables/pay_type" />
	
	
	<xsl:template name="prices">
		<xsl:param name="prices"/>
		<xsl:param name="type"/>
		<xsl:param name="country"/>
		<xsl:variable name="rooms" select="/page/rooms"/>
		<xsl:variable name="quot" select="number(translate($rooms/extra_quotient, ',', '.'))"/>
		<div class="row" id="{$type}_{$country}">
			<div class="col-md-9">
				<div class="table-responsive room-price p-t-small">
					<table>
						<tr>
							<td rowspan="2">Категория номера</td>
							<td colspan="2">
								Цена путёвки, <div class="cur_name"><xsl:value-of select="if ($country = 'bel') then 'бел. руб.' else if ($country = 'rus') then 'российские рубли' else 'евро'"/></div>
								<span>*- цена доп. места</span>
							</td>
							<td colspan="2">
								Цена путёвки, <div class="cur_name">
									<xsl:value-of select="if ($country = 'bel') then 'бел. руб.' else if ($country = 'rus') then 'российские рубли' else 'евро'"/>
								</div>
								<span>*- цена доп. места</span>
							</td>
							<td colspan="2">
								Цена путёвки, <div class="cur_name">
										<xsl:value-of select="if ($country = 'bel') then 'бел. руб.' else if ($country = 'rus') then 'российские рубли' else 'евро'"/>
									</div>
								<span>*- цена доп. места</span>
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
								<select name="" id="" onchange="setDays(this, 'first')">
									<xsl:call-template name="number_option"><xsl:with-param name="max" select="number('30')"/></xsl:call-template>
								</select>
							</td>
							<td>12</td>
							<td>
								<select name="" id="" onchange="setDays(this, 'second')">
									<xsl:call-template name="number_option"><xsl:with-param name="max" select="number('30')"/></xsl:call-template>
								</select>
							</td>
							<td>12</td>
							<td>
								<select name="" id="" onchange="setDays(this, 'third')">
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
							
							<xsl:variable name="cur" select="if ($country = 'bel') then 'BEL' else if ($country = 'rus') then 'RUB' else 'EUR'"/>
							<tr>
								<td>
									<a href="{show_photos}" class="ajax-link" data-toggle="modal" data-target="#modal-gallery"><xsl:value-of select="name"/></a>
								</td>
								
								<td class="day" price="first">
									<nobr><div class="pi" cur="{$cur}" price="{$price_first}"><xsl:value-of select="format-number($price_first, $f_mask, 'ru')"/></div></nobr>
									<span><div class="pi" cur="{$cur}" price="{$price_first_extra}"><xsl:value-of select="format-number($price_first_extra, $f_mask, 'ru')"/></div>*</span>
								</td>
								<td>
									<nobr><div class="pi" cur="{$cur}" price="{$price_first * 12}"><xsl:value-of select="format-number($price_first * 12, $f_mask, 'ru')"/></div></nobr>
									<span><div class="pi" cur="{$cur}" price="{$price_first_extra * 12}"><xsl:value-of select="format-number($price_first_extra * 12, $f_mask, 'ru')"/></div>*</span>
								</td>
								
								<td class="day" price="second">
									<nobr><div class="pi" cur="{$cur}" price="{$price_second}"><xsl:value-of select="format-number($price_second, $f_mask, 'ru')"/></div></nobr>
									<span><div class="pi" cur="{$cur}" price="{$price_second_extra}"><xsl:value-of select="format-number($price_second_extra, $f_mask, 'ru')"/></div>*</span>
								</td>
								<td>
									<nobr><div class="pi" cur="{$cur}" price="{$price_second * 12}"><xsl:value-of select="format-number($price_second * 12, $f_mask, 'ru')"/></div></nobr>
									<span><div class="pi" cur="{$cur}" price="{$price_second_extra * 12}"><xsl:value-of select="format-number($price_second_extra * 12, $f_mask, 'ru')"/></div>*</span>
								</td>
								
								<td class="day" price="third">
									<nobr><div class="pi" cur="{$cur}" price="{$price_third}"><xsl:value-of select="format-number($price_third, $f_mask, 'ru')"/></div></nobr>
									<span><div class="pi" cur="{$cur}" price="{$price_third_extra}"><xsl:value-of select="format-number($price_third_extra, $f_mask, 'ru')"/></div>*</span>
								</td>
								<td>
									<nobr><div class="pi" cur="{$cur}" price="{$price_third * 12}"><xsl:value-of select="format-number($price_third * 12, $f_mask, 'ru')"/></div></nobr>
									<span><div class="pi" cur="{$cur}" price="{$price_third_extra * 12}"><xsl:value-of select="format-number($price_third_extra * 12, $f_mask, 'ru')"/></div>*</span>
								</td>
								
							</tr>
						</xsl:for-each>
					</table>
				</div>
			</div>
			<div class="col-md-3">
				<button type="button" class="btn btn-primary btn-lg toggle-button btn-block" rel="#booking-online">Онлайн-бронирование</button>
				<p>&#160;</p>
				<h3 class="m-t-zero"><i class="fa fa-phone"></i> <xsl:value-of select="/page/common/phone"/>
					<a href="" data-toggle="modal" data-target="#modal-phones"><span class="caret"></span></a>
				</h3>
				<xsl:value-of select="$prices/side_text" disable-output-escaping="yes"/>
				<p class="p-t-small"><i class="fa fa-arrow-down"></i> <a href="{$prices/@path}{$prices/contract}">Скачать договор</a></p>
			</div>
		</div>
	</xsl:template>




	<xsl:template name="INNER_CONTENT">
	<div class="col-xs-12">
		<div class="path hidden-xs">
			<a href="{page/index_link}">Главная страница</a> →
		</div>
		<h2 class="m-t-zero">Бронирование и цены</h2>
		<p>
			<a href="about/putevki">Описание санаторно-курортной путевки</a>&#160;&#160;&#160;&#160;&#160;
			<a href="about/putevki">Описание оздоровительной путевки путевки</a>
		</p>
 	    <!-- 
 	    <xsl:if test="$pay_type != 'bel'">
	 	    <div style="float:right;"> 
				<xsl:call-template name="CURRENCY_PLACEHOLDER" /> 
			</div>
		</xsl:if>
		 -->
		<ul class="nav nav-tabs m-t-small"> 
			<li role="presentation" class="{'active'[$rest_type = 'san']}"><a href="{page/bel_san}">Санаторно-курортная путевка</a></li>
			<li role="presentation" class="{'active'[$rest_type = 'ozd']}"><a href="{page/bel_ozd}">Оздоровительная путевка</a></li>
		</ul>
		<div class="row">
			<div class="col-xs-12">
				<div class="tab-content">
					<div class="tab-pane active">
						<div class="btn-group p-t-small">
							<xsl:variable name="pay_link_rus" select="if ($rest_type = 'san') then page/ru_san else page/ru_ozd" />
							<xsl:variable name="pay_link_eur" select="if ($rest_type = 'san') then page/eur_san else page/eur_ozd" />
							<xsl:variable name="pay_link_bel" select="if ($rest_type = 'san') then page/bel_san else page/bel_ozd" />
			
							<button type="button" class="btn btn-{if ($pay_type = 'bel') then 'primary' else 'default'}" onclick="location.replace('{$pay_link_bel}')">
								Для граждан Беларуси
							</button>
							<button type="button" class="btn btn-{if ($pay_type = 'rus') then 'primary' else 'default'}" onclick="location.replace('{$pay_link_rus}')">
								Для иностранных граждан (RUR)
							</button>
							<button type="button" class="btn btn-{if ($pay_type = 'eur') then 'primary' else 'default'}" onclick="location.replace('{$pay_link_eur}')">
								Для иностранных граждан (EUR)
							</button>
						</div>
						<xsl:choose>
							<xsl:when test="$rest_type = 'san' and $pay_type = 'bel'">
								<xsl:call-template name="prices">
									<xsl:with-param name="prices" select="//page/book/book_general_rb"/>
									<xsl:with-param name="type" select="$rest_type"/>
									<xsl:with-param name="country" select="$pay_type"/>
								</xsl:call-template>
							</xsl:when>
							<xsl:when test="$rest_type = 'san' and $pay_type != 'bel'">
								<xsl:call-template name="prices">
									<xsl:with-param name="prices" select="//page/book/book_general_foreign"/>
									<xsl:with-param name="type" select="$rest_type"/>
									<xsl:with-param name="country" select="$pay_type"/>
								</xsl:call-template>
							</xsl:when>
							<xsl:when test="$rest_type = 'ozd' and $pay_type = 'bel'">
								<xsl:call-template name="prices">
									<xsl:with-param name="prices" select="//page/book/book_med_rb"/>
									<xsl:with-param name="type" select="$rest_type"/>
									<xsl:with-param name="country" select="$pay_type"/>
								</xsl:call-template>
							</xsl:when>
							<xsl:when test="$rest_type = 'ozd' and $pay_type != 'bel'">
								<xsl:call-template name="prices">
									<xsl:with-param name="prices" select="//page/book/book_med_foreign"/>
									<xsl:with-param name="type" select="$rest_type"/>
									<xsl:with-param name="country" select="$pay_type"/>
								</xsl:call-template>
							</xsl:when>
						</xsl:choose>							
					</div>
				</div>
				<div class="row p-t-small">
					<div class="col-xs-12">
						<xsl:apply-templates select="page/book" mode="content"/>
					</div>
				</div>
			</div>
		</div>
		<!-- 
		<div id="fotorama-popup" class="popup" style="display: none;">
			<a class="close">Закрыть</a>
			<div id="room-pics" class="pageText">
				
			</div>
		</div>
		 -->
	</div>
	</xsl:template>

</xsl:stylesheet>