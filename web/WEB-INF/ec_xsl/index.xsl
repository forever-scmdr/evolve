<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="CONTENT">
		<section class="main_news">
			<div class="container flx">
				
				<div class="left">
					<xsl:variable name="slide_1" select="page/news_wrap/featured[1]" />
					<xsl:variable name="slide_2" select="page/news_wrap/featured[2]" />
					<xsl:variable name="slide_3" select="page/news_wrap/featured[3]" />


					<div class="news_item slide">
						<a class="cover" href="{$slide_1/show_page}"></a>
						<img src="{$slide_1/@path}{$slide_1/main_pic}" alt="{$slide_1/name}" />
						<div class="inner_text">
							<a href="{$slide_1/news/show_page}" class="tag"><xsl:value-of select="$slide_1/news/name"/></a>
							<p class="date"><xsl:value-of select="$slide_1/date"/></p>
							<a href="{$slide_1/show_page}"><xsl:value-of select="$slide_1/name"/></a>
						</div>
					</div>

					<div class="two_news">
						<div class="news_item sm slide">
							<a class="cover" href="{$slide_2/show_page}"></a>
							<img src="{$slide_2/@path}{$slide_2/main_pic}" alt="{$slide_2/name}" />
							<div class="inner_text">
								<p><a href="{$slide_2/news/show_page}" class="tag"><xsl:value-of select="$slide_2/news/name"/></a></p>
								<p><a href="{$slide_2/show_page}"><xsl:value-of select="$slide_2/name"/></a></p>
								<p class="date"><xsl:value-of select="$slide_2/date"/></p>
							</div>
						</div>
						<div class="news_item sm slide">
							<a class="cover" href="{$slide_3/show_page}"></a>
							<img src="{$slide_3/@path}{$slide_3/main_pic}" alt="{$slide_3/name}" />
							<div class="inner_text">
								<p><a href="{$slide_3/news/show_page}" class="tag"><xsl:value-of select="$slide_3/news/name"/></a></p>
								<p><a href="{$slide_3/show_page}"><xsl:value-of select="$slide_3/name"/></a></p>
								<p class="date"><xsl:value-of select="$slide_3/date"/></p>
							</div>
						</div>
					</div>

				</div>


				<div class="rec_list">
					<div class="head">
						<h2 class="title">Рекомендации</h2>
						<div class="line"></div>
						<a href="{page/recommended_link}" class="look_all">Смотреть все 
							<img src="img/look_all_right.svg" alt="look_all" class="light" />
							<img src="img/look_all_right_wh.svg" alt="look_all" class="shadow" />
						</a>
					</div>


					<div class="rec_slider">
						<xsl:for-each select="/page/recommended">
							<a href="{show_page}" class="news_item sm">
								<img src="{@path}{small_pic}" alt="news_rec_img" />
								<div class="inner_text">
									<p><xsl:value-of select="name"/></p>
									<p class="date"><xsl:value-of select="date" /></p>
								</div>
							</a>
						</xsl:for-each>
					</div>

					<div class="last_news">
						<div class="head">
							<h2 class="title">Последнее</h2>
							<div class="line"></div>
							<a href="{page/news_link}" class="look_all">Смотреть все 
								<img src="img/look_all_right.svg" alt="look_all" class="light" />
								<img src="img/look_all_right_wh.svg" alt="look_all" class="shadow" />
							</a>
						</div>
						<div class="hor_list">
							<xsl:for-each select="/page/small_news_item">
								<a href="{show_page}" title="news" class="item">
									<span class="left_side">
										<span class="top_info_box">
											<!-- <div class="name">Источник: <xsl:value-of select="if (source != '') then source else 'Respectiva'"/></div> -->
											<span class="dot"></span>
											<span class="when"  data-millis="{date/@millis}"><xsl:value-of select="date"/></span>
										</span>
										<p><xsl:value-of select="name"/></p>
									</span>
									<img src="{@path}{small_pic}" alt="hor_img" class="hor_img" />
								</a>
							</xsl:for-each>
						</div>
					</div>
				</div>
			</div>
		</section>
	
		<section class="trends_list">

			<xsl:variable name="t" select="page/hot_tags/tag"/>

			<div class="container">
				<h2>Тренды</h2>
				<ul>
					<xsl:for-each select="$t">
						<li><a href="{hot_link}"><xsl:value-of select="name"/></a></li>
					</xsl:for-each>
				</ul>
			</div>
		</section>

		<xsl:variable name="pop" select="page/popular"/>
		<section class="popular">
			<div class="container">
				<div class="head">
					<h2 class="title">Популярное</h2>
					<p class="line"></p>
					<a href="{/page/popular_link}" class="look_all">Смотреть все 
						<img src="img/look_all_right.svg" alt="look_all" class="light" />
						<img src="img/look_all_right_wh.svg" alt="look_all" class="shadow" />
					</a>
				</div>
				<div class="cols_list">
					<div class="col_nar">
						<a href="{$pop[2]/show_page}" class="news_outer">
							<img src="{$pop[2]/@path}{$pop[2]/small_pic}" alt="{$pop[2]/name}" />
							<span class="top_info_box">
								<div class="name"><xsl:value-of select="if ($pop[2]/source != '') then 'Respectiva' else 'Respectiva'"/></div>
								<span class="dot"></span>
								<span class="when" data-millis="{$pop[2]/date/@millis}"><xsl:value-of select="$pop[2]/date"/></span>
							</span>
							<p><xsl:value-of select="$pop[2]/name"/></p>
							<span class="top_info_box">
								<div class="when"><xsl:value-of select="$pop[2]/news/name"/></div>
								<span class="dot"></span>
								<span class="when"><xsl:value-of select="$pop[2]/read_time"/> читать</span>
							</span>
						</a>

						<a href="{$pop[3]/show_page}" class="news_outer">
							<img src="{$pop[3]/@path}{$pop[3]/small_pic}" alt="{$pop[3]/name}" />
							<span class="top_info_box">
								<div class="name"><xsl:value-of select="if ($pop[3]/source != '') then 'Respectiva' else 'Respectiva'"/></div>
								<span class="dot"></span>
								<span class="when"  data-millis="{$pop[3]/date/@millis}"><xsl:value-of select="$pop[3]/date"/></span>
							</span>
							<p><xsl:value-of select="$pop[3]/name"/></p>
							<span class="top_info_box">
								<div class="when"><xsl:value-of select="$pop[3]/news/name"/></div>
								<span class="dot"></span>
								<span class="when"><xsl:value-of select="$pop[3]/read_time"/> читать></span>
							</span>
						</a>
					</div>
					<a href="{$pop[1]/show_page}" class="news_item">
						<img src="{$pop[1]/@path}{$pop[1]/small_pic}" alt="{$pop[1]/name}" />
						<span class="inner_text">
							<span class="top_info_box">
								<span class="name"><xsl:value-of select="if ($pop[1]/source != '') then 'Respectiva' else 'Respectiva'"/></span>
								<span class="dot"></span>
								<span class="when"  data-millis="{$pop[1]/date/@millis}"><xsl:value-of select="$pop[3]/date"/></span>
							</span>
							<p><xsl:value-of select="$pop[1]/name"/></p>
							<span class="top_info_box">
								<span class="when"><xsl:value-of select="$pop[1]/news/name"/></span>
								<span class="dot"></span>
								<span class="when"><xsl:value-of select="$pop[1]/read_time"/> читать</span>
							</span>
						</span>
					</a>
					<div class="col_nar">
						<a href="{$pop[4]/show_page}" class="news_outer">
							<img src="{$pop[4]/@path}{$pop[4]/small_pic}" alt="{$pop[4]/name}" />
							<span class="top_info_box">
								<div class="name"><xsl:value-of select="if ($pop[4]/source != '') then 'Respectiva' else 'Respectiva'"/></div>
								<span class="dot"></span>
								<span class="when"  data-millis="{$pop[4]/date/@millis}"><xsl:value-of select="$pop[4]/date"/></span>
							</span>
							<p><xsl:value-of select="$pop[4]/name"/></p>
							<span class="top_info_box">
								<div class="when"><xsl:value-of select="$pop[4]/news/name"/></div>
								<span class="dot"></span>
								<span class="when"><xsl:value-of select="$pop[4]/read_time"/> читать</span>
							</span>
						</a>

						<a href="{$pop[5]/show_page}" class="news_outer">
							<img src="{$pop[5]/@path}{$pop[5]/small_pic}" alt="{$pop[5]/name}" />
							<span class="top_info_box">
								<div class="name"><xsl:value-of select="if ($pop[5]/source != '') then 'Respectiva' else 'Respectiva'"/></div>
								<span class="dot"></span>
								<span class="when"  data-millis="{$pop[5]/date/@millis}"><xsl:value-of select="$pop[5]/date"/></span>
							</span>
							<p><xsl:value-of select="$pop[5]/name"/></p>
							<span class="top_info_box">
								<div class="when"><xsl:value-of select="$pop[5]/news/name"/></div>
								<span class="dot"></span>
								<span class="when"><xsl:value-of select="$pop[5]/read_time"/> читать</span>
							</span>
						</a>
					</div>
				</div>
			</div>
		</section>
	
		<section class="soc_list">
			<div class="container">
				<div class="head">
					<h2 class="title">Мы в соцсетях</h2>
					<div class="line"></div>
				</div>
				<ul>
					<xsl:variable name="icons" select="tokenize('img/soc_x.svg,img/facebook.svg,img/inst.svg,img/vk.svg,img/tg.svg', ',')"/>
					<xsl:for-each select="$common/soc_link">
						<xsl:variable name="p" select="position()"/>
						<li><a href="{link}"><img src="{$icons[$p]}" alt="{name}" /></a></li>
					</xsl:for-each>
				</ul>
			</div>
		</section>


		<section class="finance">
			<div class="container">
				<div class="head">
					<h2 class="title">Финансы</h2>
					<div class="line"></div>
					<a href="finance/" class="look_all">Смотреть все 
						<img src="img/look_all_right.svg" alt="look_all" class="light"/>
						<img src="img/look_all_right_wh.svg" alt="look_all" class="shadow"/>
					</a>
				</div>
			
				<a href="{page/fin[1]/show_page}" class="news_item full">
					<img src="{page/fin[1]/@path}{page/fin[1]/main_pic}" alt="news_rec_img" />
					<span class="inner_text">
						<span class="top_info_box">
							<span class="name"><xsl:value-of select="if (page/fin[1]/source != '') then 'Respectiva' else 'Respectiva'"/></span>
							<span class="dot"></span>
							<span class="when" data-millis="{$pop[2]/date/@millis}"><xsl:value-of select="$pop[2]/date"/></span>
						</span>
						<p><xsl:value-of select="page/fin[1]/name"/></p>
						<span class="top_info_box">
							<span class="when">Финансы</span>
							<span class="dot"></span>
							<span class="when"><xsl:value-of select="page/fin[1]/read_time"/> читать</span>
						</span>
					</span>
				</a>

				<div class="four_items">
					<xsl:variable name="first_id" select="page/fin[1]/@id"/>
					<xsl:for-each select="page/fin[@id != $first_id]">
						<a href="{show_page}" class="news_outer">
							<img src="{@path}{small_pic}" alt="fin_img_out"/>
							<span class="top_info_box">
								<span class="name"><xsl:value-of select="if (source != '') then 'Respectiva' else 'Respectiva'"/></span>
								<span class="dot"></span>
								<span class="when"><xsl:value-of select="date"/></span>
							</span>
							<p><xsl:value-of select="name"/></p>
							<span class="top_info_box">
								<span class="when"><xsl:value-of select="read_time"/> читать</span>
							</span>
						</a>
					</xsl:for-each>
				</div>
			</div>

		</section>

		<section class="business">

			<xsl:variable name="biz" select="page/biz"/>
			<xsl:variable name="biz_2_ms" select="$biz[2]/date/@millis"/>
			<xsl:variable name="biz_4_ms" select="$biz[4]/date/@millis"/>

			<xsl:variable name="small_biz" select="$biz[date/@millis &lt; $biz_2_ms]"/>
			<xsl:variable name="small_biz_l" select="$small_biz[date/@millis &gt; $biz_4_ms or date/@millis = $biz_4_ms]"/>
			<xsl:variable name="small_biz_r" select="$small_biz[date/@millis &lt; $biz_4_ms]"/>


			<div class="container">
				<div class="head">
					<h2 class="title">Бизнес</h2>
					<div class="line"></div>
					<a href="business/" class="look_all">Смотреть все 
						<img src="img/look_all_right.svg" alt="look_all" class="light" />
						<img src="img/look_all_right_wh.svg" alt="look_all" class="shadow" />
					</a>
				</div>
				
				<div class="busi_lines">
					<div class="col">
						<a href="{$biz[1]/show_page}" class="news_item">
							<img src="{$biz[1]/@path}{$biz[1]/medium_pic}" alt="news_rec_img" />
							<div class="inner_text">
								<span class="top_info_box">
									<span class="name"><xsl:value-of select="if ($biz[1]/source != '') then 'Respectiva' else 'Respectiva'"/></span>
									<span class="dot"></span>
									<span class="when" data-millis="{$biz[1]/date/@millis}"><xsl:value-of select="$biz[1]/date"/></span>
								</span>
								<p><xsl:value-of select="$biz[1]/name"/></p>
								<span class="top_info_box">
									<div class="name">Бизнес</div>
									<span class="dot"></span>
									<span class="when"><xsl:value-of select="$biz[1]/read_time"/> читать</span>
								</span>
							</div>
						</a>

						<xsl:for-each select="$small_biz_l">
							<a href="{show_page}" class="busi_singl">
								<img src="{@path}{small_pic}" alt="{name}" style="max-width: 180px" />
								<span class="right_side">
									<span class="top_info_box">
										<span class="name"><xsl:value-of select="if (source != '') then 'Respectiva' else 'Respectiva'"/></span>
										<span class="dot"></span>
										<span class="when" data-millis="{date/@millis}"><xsl:value-of select="date"/></span>
									</span>
									<p><xsl:value-of select="name"/></p>
								</span>
							</a>
						</xsl:for-each>

					</div>
					<div class="col">
						<a href="{$biz[2]/show_page}" class="news_item">
							<img src="{$biz[2]/@path}{$biz[2]/medium_pic}" alt="news_rec_img" />
							<div class="inner_text">
								<span class="top_info_box">
									<span class="name"><xsl:value-of select="if ($biz[2]/source != '') then 'Respectiva' else 'Respectiva'"/></span>
									<span class="dot"></span>
									<span class="when" data-millis="{$biz[2]/date/@millis}"><xsl:value-of select="$biz[2]/date"/></span>
								</span>
								<p><xsl:value-of select="$biz[2]/name"/></p>
								<span class="top_info_box">
									<div class="name">Бизнес</div>
									<span class="dot"></span>
									<span class="when"><xsl:value-of select="$biz[2]/read_time"/> читать</span>
								</span>
							</div>
						</a>

						<xsl:for-each select="$small_biz_r">
							<a href="{show_page}" class="busi_singl">
								<img src="{@path}{small_pic}" alt="{name}" style="max-width: 180px" />
								<span class="right_side">
									<span class="top_info_box">
										<span class="name"><xsl:value-of select="if (source != '') then 'Respectiva' else 'Respectiva'"/></span>
										<span class="dot"></span>
										<span class="when" data-millis="{date/@millis}"><xsl:value-of select="date"/></span>
									</span>
									<p><xsl:value-of select="name"/></p>
								</span>
							</a>
						</xsl:for-each>
					</div>	
				</div>
			</div>
		</section>
	

		<xsl:variable name="audio" select="/page/audio"/>

		<xsl:if test="$audio">
			<section class="audio_sect">

				<xsl:variable name="audio" select="/page/audio_ni"/>

				<div class="container">
					<div class="head">
						<h2 class="title">Аудио истории</h2>
						<div class="line"></div>
						<a href="#" class="look_all">Смотреть все 
							<img src="img/look_all_right.svg" alt="look_all" class="light"/>
							<img src="img/look_all_right_wh.svg" alt="look_all" class="shadow"/>
						</a>
					</div>

					<div class="audio_row">


						<div class="video_box">
							<img src="{}"/>
							<div class="text">
								<p>Стартапы революционизируют отрасли с помощью инновационных технологий и подходов.</p>
								<figure>
									<audio controls="" src="img/audio.mp3"></audio>
								</figure>
							</div>
						</div>

						<div class="ver_list">
							<div class="item">
								<img src="img/audio_img.png" alt="audio_img" />
								<div class="text">
									<p>Стартапы революционизируют отрасли с помощью инновационных технологий и подходов.</p>
									<figure>
										<audio controls="" src="img/audio.mp3"></audio>
									</figure>
								</div>
							</div>

							<div class="item">
								<img src="img/audio_img.png" alt="audio_img"/>
								<div class="text">
									<p>Стартапы революционизируют отрасли с помощью инновационных технологий и подходов.</p>
									<figure>
										<audio controls="" src="img/audio.mp3"></audio>
									</figure>
								</div>
							</div>
						</div>
						
					</div>

				</div>
			</section>
		</xsl:if>

		<section class="market_sect">
			
			<xsl:variable name="market" select="page/market_ni"/>

			<div class="container">
				<div class="head">
					<h2 class="title">Биржа</h2>
					<div class="line"></div>
					<a href="stock_exchange/" class="look_all">Смотреть все 
						<img src="img/look_all_right.svg" alt="look_all" class="light" />
						<img src="img/look_all_right_wh.svg" alt="look_all" class="shadow" />
					</a>
				</div>

				<div class="market_list_box">
					<div class="left_side">
						<a href="{$market[1]/show_page}" class="news_item">
							<img src="{$market[1]/@path}{$market[1]/medium_pic}" alt="{market[1]/name}"/>
							<div class="inner_text">
								<span class="top_info_box">
									<div class="name"><xsl:value-of select="if ($market[1]/source != '') then 'Respectiva' else 'Respectiva'"/></div>
									<span class="dot"></span>
									<span class="when" data-millis="{$market[1]/date/@millis}"><xsl:value-of select="$market[1]/date"/></span>
								</span>
								<p><xsl:value-of select="$market[1]/name"/></p>
								<span class="top_info_box">
									<div class="when">Биржа</div>
									<span class="dot"></span>
									<span class="when"><xsl:value-of select="$market[1]/read_time"/> читать</span>
								</span>
							</div>
						</a>
						<div class="market_list">

							<xsl:variable name="market_other" select="$market[./@id != $market[1]/@id]"/>

							<xsl:for-each select="$market_other">
								<a href="{show_page}" class="item_box">
									<span class="text">
										<span class="top_info_box">
											<div class="name">Биржа</div>
											<span class="dot"></span>
											<span class="when" data-millis="{date/@millis}"><xsl:value-of select="date"/></span>
										</span>
										<p><xsl:value-of select="name"/></p>
									</span>
									<img src="{@path}{small_pic}" alt="market_small" />
								</a>
							</xsl:for-each>
						</div>
					</div>
					<div class="right_side">
						<xsl:value-of select="/page/main_page/stock_right_code" disable-output-escaping="yes"/>
					</div>
				</div>
			</div>
		</section>

		<section class="other_sect">
			<div class="container">
				<div class="head">
					<h2 class="title">Другое</h2>
					<div class="line"></div>
					<a href="{page/news_link}" class="look_all">Смотреть все 
						<img src="img/look_all_right.svg" alt="look_all" class="light"/>
						<img src="img/look_all_right_wh.svg" alt="look_all" class="shadow"/>
					</a>
				</div>
				<div class="other_list">
					<xsl:for-each select="page/news_item">
						 <div class="news_outer">
							<div class="img_box">
								<img src="{@path}{small_pic}" alt="news_other"/>
								<a href="{./news/show_page}"><span class="name_title"><xsl:value-of select="news/name"/></span></a>
							</div>
							<a href="{show_page}"><xsl:value-of select="name"/></a>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</section>
	</xsl:template>

</xsl:stylesheet>
