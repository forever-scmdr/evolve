<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*" />
	
	<xsl:template name="CONTENT">
		<div class="content-container">
			<section class="p-t-default">
				<div class="container">
					<div class="row">
						<div class="col-xs-12">
							<div class="path hidden-xs">
								<a href="{page/index_link}">Главная страница</a> →
							</div>
							<h2 class="m-t-zero">Результаты поиска</h2>
							<div class="common search-results">
							<!--
							<script src="http://www.google.com/jsapi" type="text/javascript"></script><script type="text/javascript">
												google.load('search', '1', {language : 'ru'});
												google.setOnLoadCallback(function() {
													var customSearchControl = new google.search.CustomSearchControl('017466889289341380484:8ynbg5pjulo');
													customSearchControl.setResultSetSize(google.search.Search.FILTERED_CSE_RESULTSET);
													var options = new google.search.DrawOptions();
													options.setSearchFormRoot('cse-search-form');
													customSearchControl.draw('cse', options);
													customSearchControl.execute('<xsl:value-of select="/page/variables/query"/>');
											  }, true);
											</script>
											 <div id="cse-search-form" style="display:none"></div>
							                     <div id="cse" style="width:100%"></div>
							                 -->
								         <script>
								  (function() {
								    var cx = '001392179569280732313:yi9uwab5aso';
								    var gcse = document.createElement('script');
								    gcse.type = 'text/javascript';
								    gcse.async = true;
								    gcse.src = 'https://cse.google.com/cse.js?cx=' + cx;
								    var s = document.getElementsByTagName('script')[0];
								    s.parentNode.insertBefore(gcse, s);
								  })();
								</script>
								<xsl:text disable-output-escaping="yes">
								&lt;gcse:searchresults-only&gt;&lt;/gcse:searchresults-only&gt;
								</xsl:text>
							        </div>
						</div>
					</div>
				</div>
			</section>
		</div>
	</xsl:template>

</xsl:stylesheet>