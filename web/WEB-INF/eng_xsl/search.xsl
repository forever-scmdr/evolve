<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/html" indent="yes"/>
	<xsl:strip-space elements="*" />
	
	<xsl:template name="CONTENT">
		<div class="common" style="background: #fff;">
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
        </div>
	</xsl:template>

</xsl:stylesheet>