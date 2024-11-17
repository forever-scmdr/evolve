<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="active_menu_item" select="'catalog'"/>
	<xsl:variable name="qot">"</xsl:variable>

	<xsl:variable name="news_items" select="/page/news_item | page/news_wrap/news_item"/>
	<xsl:variable name="news_parts" select="/page/text_part[news_item]"/>
	<xsl:variable name="small_news_parts" select="/page/text_part[small_news_item]"/>

	<xsl:variable name="small_news" select="page/small_news_item | page/small_news/small_news_item"/>

	<xsl:variable name="h1_1">
		<xsl:choose>
			<xsl:when test="page/@name = 'search'">
				<xsl:value-of select="concat('Статьи по запросу: ',$qot,page/variables/search,$qot)"/>
			</xsl:when>
			<xsl:when test="page/@name = 'tag'">
				<xsl:value-of select="concat('Новости по тегу: ',$qot,page/variables/tag,$qot)"/>
			</xsl:when>
			<xsl:when test="page/@name = 'author'">
				<xsl:value-of select="concat('Пубилкации автора: ',$qot,page/variables/author,$qot)"/>
			</xsl:when>
		</xsl:choose>
	</xsl:variable>

	<xsl:variable name="pagination" select="/page/news_item_pages"/>


	<xsl:variable name="title" select="$h1_1" />


	<xsl:template name="CONTENT">
		<section class="category_page">

			<div class="container">
				<h1><xsl:value-of select="$h1_1"/></h1>
				<div class="category_list" id="news_feed" data-page="1" data-link="{/page/pages_link}" data-max-page="{if ($pagination) then max($pagination/page[last()]/number(number)) else 1}">
					<xsl:apply-templates select="/page/news_item[small_pic != '']"/>
					<!-- <xsl:apply-templates select="/page/news_item[not(small_pic != '')]"/> -->
				</div>
				<xsl:if test="page/variables/search">

					<h1 style="margin-top: 30px; margin-bottom: 20px;"><xsl:value-of select="concat('Новости по запросу: ',$qot,page/variables/search,$qot)"/></h1>
					<div class="category_list">
						<xsl:apply-templates select="/page/news_item[not(small_pic != '')]"/>
					</div>
				</xsl:if>
			</div>
		</section>
	</xsl:template>

	<xsl:template match="news_item">
		<xsl:variable name="t" select="@type"/>
		<xsl:variable name="link" select="if($t = 'news_item') then show_news_item else show_small_news_item"/>

		<a href="{$link}" class="item">
			<xsl:if test="small_pic != ''">
				<img src="{@path}{small_pic}" alt="{name}" />
			</xsl:if>
			<span class="text_box">
				<span class="top_info_box">
					<!-- <div class="name"><xsl:value-of select="if (source != '') then 'Respectiva' else 'Respectiva'"/></div> -->
					<span class="dot"></span>
					<span class="when"><xsl:value-of select="date"/></span>
				</span>
				<span class="title"><xsl:value-of select="name"/></span>
				<p><xsl:value-of select="twitter_description"/></p>
				<span class="time_to_read"><xsl:value-of select="read_time"/> читать</span>
			</span>
		</a>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<script>
			newsFeed = document.getElementById("news_feed")
			
			function ajaxCall(){
				var request = new XMLHttpRequest();
				var pageNumber = 1 * newsFeed.getAttribute('data-page') + 1;
				sep = newsFeed.getAttribute('data-link').indexOf('?') > -1 ? '&amp;' : '?'
				var url = newsFeed.getAttribute('data-link') + sep + 'page=' + pageNumber;

				// block onscroll while loading
				news_feed.setAttribute('data-status', 'loading');

				request.onreadystatechange = function(){
					if(request.readyState === XMLHttpRequest.DONE){
						if(request.status === 200) {
							news_feed.setAttribute('data-page', pageNumber)
							news_feed.innerHTML += request.responseText; 
						}else {
							console.log()
							console.log(request)
						}
						newsFeed.removeAttribute('data-status')
					}
				}

				request.open('GET', url);
				request.send();
			}		

			window.onscroll = function(){
				if (newsFeed.getAttribute('data-status') == 'loading'){
					return;
				}
				if(window.scrollY + window.innerHeight &gt;= newsFeed.offsetTop + newsFeed.offsetHeight){
					if (newsFeed.getAttribute('data-page') * 1 &lt; newsFeed.getAttribute('data-max-page') * 1){
						ajaxCall();
					}
				}
			};
		</script>
	</xsl:template>

</xsl:stylesheet>