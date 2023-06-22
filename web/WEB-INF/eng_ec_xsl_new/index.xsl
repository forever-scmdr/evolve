<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:import href="common_page_base.xsl" />
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes" />
	<xsl:strip-space elements="*" />

	<xsl:variable name="main" select="/page/main" />

	<xsl:template match="/">
		<xsl:call-template name="DOCTYPE" />
		<html xmlns="http://www.w3.org/1999/xhtml">
			<xsl:call-template name="HEAD" />
			<body>

				<div id="forever-banner-left-client_6" class="forever-banner" cid="6"></div>
				<div id="forever-banner-right-client_6" class="forever-banner"></div>
				<div id="forever-banner-top-client_6" class="forever-banner"></div>
				<script src="http://test9.must.by/js/insert_banner.js"></script>

				<div class="page">
					<div class="header">
						<div class="index">
							<div class="top" style="position: relative; z-index: 100;">
								<div class="w1k">
									<div class="item blank">
										<a href="{/page/index_link}" class="logo" style="margin-left: -92px;">
											<xsl:call-template name="NBSP" />
										</a>
									</div>
									<div class="item phones">
										<p>
											<xsl:value-of select="/page/common/phone" />
										</p>
										<span class="javascript show">все телефоны</span>
										<div class="hidden" style="margin-left: -46px;">
											<div class="arrow-top"></div>
											<xsl:value-of select="/page/common/phone_hidden" disable-output-escaping="yes" />
										</div>
									</div>
									<div class="item docs">
										<a href="about/dogovory_/">Скачать договоры</a>
									</div>
									<div class="item mail">
										<xsl:variable name="form" select="/page/fform" />
										<script>
											function postForm() {
											$.post("email.item", $("#feedback_form").serialize() );
											$(".send").hide();
											$(".msg_ok").show();
											}
											function postMore() {
											$('.msg_ok').hide();
											$('.send').show();
											return false;
											}
										</script>
										<a href="#" id="mail" onclick="return false;">Написать сообщение</a>
										<a href="mailto:info@sansputnik.by">info@sansputnik.by</a>
										<div class="hidden2 mail">
											<div class="close">
												<img src="images/button_close.png" alt="Закрыть" />
											</div>
											<div class="send">
												<h3>
													<img src="images/title_soobschenie.png" alt="Сообщение" />
												</h3>
												<form id="feedback_form" action="email.item" method="post">
													<input type="hidden" name="targetUrl" value="{/page/post_feedback_link}" />
													<xsl:for-each select="$form/hidden/field">
														<input type="hidden" name="{@input}" value="{@value}" />
													</xsl:for-each>
													Ваше имя:
													<input type="text" name="{$form/field[@name='name']/@input}" />
													Адрес электронной почты или телефон:
													<input type="text" name="{$form/field[@name='phone']/@input}" />
													Сообщение:
													<textarea name="{$form/field[@name='message']/@input}"></textarea>
													<a class="submit" href="javascript:postForm()">Отправить</a>
												</form>
											</div>
											<div class="msg_ok">
												<img src="images/title_soobschenie_otpr.png" alt="сообщение отправлено" />
												<a href="#" id="one_more" onclick="return postMore()">Написать еще сообщение</a>
											</div>
										</div>
									</div>
									<div class="item map">
										<p>
											<a id="map" href="#hidden_map" rel="fancybox">
												pacположение
												<xsl:call-template name="BR" />
												санатория на карте
											</a>
										</p>
										<div id="hidden_map" style="display: none;">
											<div class="yamap" style="background: url('images/map.jpg') center no-repeat;  margin-top:0;">
												<script type="text/javascript" charset="utf-8" src="//api-maps.yandex.ru/services/constructor/1.0/js/?sid=l3Ko5dt7TfnGTfkTmVWbgR73lQMAUtAg&amp;width=974&amp;height=500"></script>
											</div>
										</div>
									</div>
									<div class="item calendar">
										<p>
											<span id="calendar" class="">
												показать
												<xsl:call-template name="BR" />
												календарь
											</span>
										</p>
										<div style="display:none;">
											<input name="data" />
										</div>
									</div>
									<div class="item bank last">
										<a href="http://belarusbank.by/" target="blank">ОАО АСБ "Беларусбанк"</a>
									</div>
								</div>
							</div>
							<div class="menu">
								<table style="width: 100%;">
									<tr>
										<td class="side">

										</td>
										<td style="width:1200px; position: relative;" id="td-f">
											<ul class="main" id="main-menu">
												<li class="about lnk has_sub">
													<a href="{/page/about/abstract_page[1]/show_page}" class="lvl1">О санатории</a>
													<ul style="display: none;" class="submenu">
														<xsl:for-each select="/page/about/abstract_page">
															<li>
																<a href="{show_page}">
																	<xsl:value-of select="header" />
																</a>
															</li>
														</xsl:for-each>
													</ul>
												</li>
												<li class="lnk">
													<a class="lvl1" href="{/page/rooms_link}">Номера</a>
												</li>
												<xsl:for-each select="page/services">
													<li class="about lnk has_sub">
														<a href="{show_page}" class="lvl1">
															<xsl:value-of select="header" />
														</a>
														<ul style="display: none;" class="submenu">
															<xsl:for-each select="service">
																<li>
																	<a href="{show_page}">
																		<xsl:value-of select="name" />
																	</a>
																	<xsl:if test="service">
																		<ul class="lv3">
																			<xsl:for-each select="service">
																				<li>
																					<a href="{show_page}">
																						<xsl:value-of select="name" />
																					</a>
																				</li>
																			</xsl:for-each>
																		</ul>
																	</xsl:if>
																</li>
															</xsl:for-each>
														</ul>
													</li>
												</xsl:for-each>
												<li class="lnk">
													<a style="line-height:11px; height: 30px; padding: 13px 30px 12px; text-decoration: none;" class="lvl1" href="{/page/book_link}">
														<span style=" text-decoration: underline;">Бронирование</span>
														<br />
														<span style="font-size: 12px; font-weight: normal;">(цены)</span>
													</a>
												</li>
												<li class="lnk">
													<a href="{/page/news_link}" class="lvl1">Новости</a>
												</li>
												<li class="lnk">
													<a href="{/page/contacts_link}" class="lvl1" style="">Контакты</a>
												</li>
											</ul>
											<div class="clear"></div>
											<a id="lang-switch" class="eng-link" href="http://eng.sansputnik.by/" title="Go to english version">english</a>
										</td>
										<td class="side"></td>
									</tr>
								</table>
							</div>

						</div>
						<div id="nivo" class="align_center">
							<div class="align_center_to_left">
								<div class="slideshow align_center_to_right slider-wrapper theme-default">
									<div id="slider" class="nivoSlider">
										<xsl:for-each select="$main/frame">
											<img src="{@path}{img}" alt="" title="#htmlcaption_{@id}" />
										</xsl:for-each>
									</div>
									<xsl:for-each select="$main/frame">
										<div id="htmlcaption_{@id}" class="nivo-html-caption">
											<div class="pos">
												<xsl:value-of select="text" disable-output-escaping="yes" />
												<a href="{link}" class="big">
													<xsl:call-template name="NBSP" />
												</a>
											</div>
										</div>
									</xsl:for-each>
									<script type="text/javascript" src="nivo-slider/jquery.nivo.slider.js"></script>
									<script type="text/javascript">
										<xsl:text disable-output-escaping="yes">
					$(window).load(function() {
						$('#slider').nivoSlider({
							effect: 'fade', // Specify sets like: 'fold,fade,sliceDown'(http://dev7studios.com/nivo-slider/#/documentation)
							slices: 5, // For slice animations
							boxCols: 3, // For box animations
							boxRows: 6, // For box animations
							animSpeed: 500, // Slide transition speed
							pauseTime: 5000, // How long each slide will show
							startSlide: 0, // Set starting Slide (0 index)
							directionNav: true, // Next and Prev navigation
							controlNav: false, // 1,2,3... navigation
							controlNavThumbs: false, // Use thumbnails for Control Nav
							pauseOnHover: false, // Stop animation while hovering
							manualAdvance: false, // Force manual transitions
							prevText: '&amp;laquo;', // Prev directionNav text
							nextText: '&amp;raquo;', // Next directionNav text
							randomStart: false, // Start on a random slide
							beforeChange: function(){}, // Triggers before a slide transition
							afterChange: function(){}, // Triggers after a slide transition
							slideshowEnd: function(){}, // Triggers after all slides have been shown
							lastSlide: function(){}, // Triggers when last slide is shown
							afterLoad: function(){
								resize();
								$(window).resize(function(){resize();})
							}
						});
					});
					function resize(){
						var uagent = navigator.userAgent.indexOf("MSIE");
								if(uagent > -1){
									var ver = navigator.userAgent.substring(uagent+5, uagent+8);
									ver = parseInt(ver);
									ww = $(window).width();
									sw = $('.slideshow').width();
									if(ver &lt; 8 &amp;&amp; ww &lt; sw){
										delta = -1*(sw-ww-36)/2;
										
										//$('.phones').html(ww);
										$('.align_center_to_left, .align_center_to_right').css('float', 'none');
															
										$('#nivo').css('height', $('.slideshow').height()+'px');
										
										//$('#nivo').css('max-height', '384px');
										$('.align_center_to_right').css('position', 'absolute');
										$('.align_center_to_right').css('right',delta+'px');
										$('.align_center_to_left').css('left','0');
										$('.align_center_to_left').css('right','0');
										
		
									}
									else if(ver &lt; 8 &amp;&amp; ww &gt; sw){
										$('.align_center_to_left, .align_center_to_right').css('float', '');
										$('#nivo').css('height', '');
										$('#nivo').css('max-height', '');
										$('.align_center_to_right').css('position', '');
										$('.align_center_to_right').css('right','');
										$('.align_center_to_left').css('left','');
										$('.align_center_to_left').css('right','');
									}
								}
								
					}
					</xsl:text>
									</script>
								</div>
							</div>
						</div>
					</div>
					<div class="mainwrap index2">
						<div class="pics">
							<a href="{$main/link_1}">
								<img src="{$main/@path}{$main/pic_1}" alt="разнообразное меню" />
							</a>
							<a href="{$main/link_2}">
								<img src="{$main/@path}{$main/pic_2}" alt="SPA-центр" />
							</a>
							<a href="{$main/link_3}">
								<img src="{$main/@path}{$main/pic_3}" alt="более 150 лечебных и оздоровительных процедур" />
							</a>
						</div>
						<p style="text-align: center;">
							<xsl:value-of select="$main/banner" disable-output-escaping="yes" />
						</p>
						<div class="text">
							<div class="left">
								<a href="3d_tour/build.html" class="fancyframe">
									<img src="images/banner_3d.png" alt="3D панарама" />
								</a>
							</div>
							<div class="news">
								<div class="train" style="">
									<xsl:for-each select="/page/news_item">
										<div class="item">
											<h3>
												<a href="{show_news_item}">
													<xsl:value-of select="header" />
												</a>
											</h3>
											<xsl:value-of select="short" disable-output-escaping="yes" />
											<a class="all" href="{/page/news_link}">все новости</a>
										</div>
									</xsl:for-each>
								</div>
								<script type="text/javascript">
									<xsl:text disable-output-escaping="yes">
						$(window).load(function(){setTimeout(go, 4000);});
						function go(){
							el = $('.train');	
							step = 333;
							max = ($('.train .item').length)* 336;
							$(el).width(max);
							ml = $(el).css('margin-left').replace('px','')*1;
							ml -= step;
							ml = (Math.abs(ml)&lt;$(el).width()-step)? ml:0;
							if(ml == 0) {
								$(el).css({marginLeft:step});
								$(el).animate({marginLeft:ml}, 1000, function(){setTimeout(go, 4000);});
							} else {
								$(el).animate({marginLeft:ml}, 1000, function(){setTimeout(go, 4000);});
							}
						}
						</xsl:text>
								</script>
							</div>
							<div class="clear"></div>
						</div>
					</div>
				</div>
				<xsl:call-template name="FOOTER" />
				<!-- Yandex.Metrika counter -->
				<script type="text/javascript">
					(function (d, w, c) {
					(w[c] = w[c] || []).push(function() {
					try {
					w.yaCounter19430230 = new Ya.Metrika({id:19430230,
					webvisor:true,
					clickmap:true,
					trackLinks:true,
					accurateTrackBounce:true});
					} catch(e) { }
					});

					var n = d.getElementsByTagName("script")[0],
					s = d.createElement("script"),
					f = function () { n.parentNode.insertBefore(s, n); };
					s.type = "text/javascript";
					s.async = true;
					s.src = (d.location.protocol == "https:" ? "https:" : "http:") + "//mc.yandex.ru/metrika/watch.js";

					if (w.opera == "[object Opera]") {
					d.addEventListener("DOMContentLoaded", f, false);
					} else { f(); }
					})(document, window, "yandex_metrika_callbacks");
				</script>
				<noscript>
					<div>
						<img src="//mc.yandex.ru/watch/19430230" style="position:absolute; left:-9999px;" alt="" />
					</div>
				</noscript>
				<!-- /Yandex.Metrika counter -->
				<!-- BEGIN JIVOSITE CODE {literal} -->
				<script type='text/javascript'>
					(function(){ var widget_id = 'STWrPUxCZk';
					var s = document.createElement('script'); s.type = 'text/javascript'; s.async = true; s.src = '//code.jivosite.com/script/widget/'+widget_id; var ss = document.getElementsByTagName('script')[0];
					ss.parentNode.insertBefore(s, ss);})();
				</script>
				<!-- {/literal} END JIVOSITE CODE -->
				
				<script type="text/javascript" src="js/menu_aim.js"></script>
				<script type="text/javascript" src="js/menu_driver.js"></script>
				
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>