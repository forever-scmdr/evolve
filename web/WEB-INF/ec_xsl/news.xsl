<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="default_title" select="'Новости'"/>
	<xsl:variable name="sel_name" select="/page/selected_news/name"/>

	<xsl:variable name="title" select="if ($sel_name) then '$sel_name' else if ($current_tag != '') then $current_tag else $default_title" />
	<xsl:variable name="h1" select="if($seo/h1 != '') then $seo/h1 else $title"/>

	<xsl:variable name="pagination" select="/page/selected_news/news_item_pages"/>
	<xsl:variable name="sel" select="page/variables/sel"/>
	<xsl:variable name="quote">"</xsl:variable>

	<xsl:template name="CONTENT">
		<xsl:if test="page/cool_tags/tag">

			<xsl:variable name="t" select="page/cool_tags/tag"/>

			<section class="trends_list">
				<div class="container">
					<ul>
						<xsl:for-each select="$t">
							<li><a href="{hot_link}"><xsl:value-of select="name"/></a></li>
						</xsl:for-each>
					</ul>
				</div>
			</section>
		</xsl:if>

		<section class="category_page">
			<div class="container">
				<div class="category_list" id="news_feed" data-page="1" data-link="{/page/pages_link}" data-max-page="{if ($pagination) then $pagination/page[last()]/number else 1}">
					<xsl:for-each select="/page/selected_news/news_item | /page/search/success/news_item | /page/search/success/small_news_item">
						<a href="{show_page}" class="item">
							<img src="{@path}{small_pic}" alt="{name}" />
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
					</xsl:for-each>
				</div>
			</div>
		</section>
	</xsl:template>

	<xsl:template name="EXTRA_SCRIPTS">
		<script>
			newsFeed = document.getElementById("news_feed")
			
			function ajaxCall(){
				var request = new XMLHttpRequest();
				var pageNumber = 1 * newsFeed.getAttribute('data-page') + 1;
				var url = newsFeed.getAttribute('data-link') + '?page=' + pageNumber;


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