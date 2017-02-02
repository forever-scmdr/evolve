<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="current_page_class" select="'about'"/>

	<xsl:variable name="page_id" select="/page/variables/p"/>
	<xsl:variable name="page" select="/page/about/abstract_page[position() = 1 and $page_id = ''] | /page/about/abstract_page[@key = $page_id]"/>

	<xsl:template name="CONTENT">
	<div class="submenu">
		<ul>
			<xsl:for-each select="/page/about/abstract_page">
			<li><a class="{'open'[current()/@id = $page/@id]}" href="{show_page}"><xsl:value-of select="header"/></a></li>
			</xsl:for-each>
		</ul>
	</div>
	<div class="right">
		<div class="path"><a href="{/page/index_link}">Главная страница</a><xsl:call-template name="arrow"/></div>
		<xsl:call-template name="PAGE_TITLE"><xsl:with-param name="page" select="$page"/></xsl:call-template>
		<xsl:if test="$page_id = 'otzyvy_'">
		<p><a onclick="$('#frm').css('display', 'block');" class="big_button" >
			Оставить отзыв
		</a></p>
		</xsl:if>
		
		<xsl:if test="//feedback and $page_id = 'otzyvy_'">
			<div class="review">
				<xsl:for-each select="//feedback">
					<div>
						<div class="">
							<xsl:value-of select="room_feedback" disable-output-escaping="yes"/>
							<xsl:value-of select="service_feedback" disable-output-escaping="yes"/>
						</div>
						<strong><xsl:value-of select="fio"/><xsl:if test="live_date != ''">, <xsl:value-of select="f:day_month_year(live_date)" />,</xsl:if><xsl:call-template name="NBSP"/><xsl:value-of select="country"/>
						</strong>
						<xsl:if test="answer != ''">
							<p style="margin-top: 15px;"><b>Ответ:</b></p>
							<xsl:value-of select="answer" disable-output-escaping="yes"/>
						</xsl:if>
					</div>
					<hr />
					<div style="padding-bottom: 20px;"></div>
				</xsl:for-each>
			</div>
		</xsl:if>
		<xsl:value-of select="$page/text" disable-output-escaping="yes"/>
		<xsl:apply-templates select="$page/text_part | $page/gallery_part"/>
	</div>
	</xsl:template>

	<xsl:template name="FRAME">

				<div class="frame" id="frm">
					<div class="align_center">
						<div class="align_center_to_left">
							<div class="frame_content align_center_to_right" style="display: block; width: 500px;">
								<div class="close2">
										<img src="images/button_close.png" alt="close" onclick="$('#frm').hide();" style="cursor: pointer;"/>
								</div>					
								<div style="overflow-y: auto; font-size: 14px;" id="feedback_ajax">
									Загрузка...
									<script type="text/javascript">
										$(document).ready(function(){
											insertAjax('<xsl:value-of select="page/client_feedback_link"/>');
										});
									</script>
								</div>
							</div>
						</div>
					</div>
				</div>
			
	</xsl:template>

</xsl:stylesheet>