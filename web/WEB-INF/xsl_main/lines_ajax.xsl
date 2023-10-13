<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="snippets/product.xsl"/>

	<xsl:variable name="p" select="page/product"/>

	<xsl:template match="/">
		<div>
			<div class="popup result" id="product-ajax-popup">
				<div class="popup__body">
					<div class="popup__content">
						<a class="popup__close" onclick="clearProductAjax();">×</a>
						<div class="popup__title title title_2">
							<xsl:value-of select="$p/name" />
						</div>
						<xsl:if test="page/plain_catalog/product">
							<xsl:call-template name="LINES_TABLE">
								<xsl:with-param name="products" select="page/plain_catalog/product"/>
							</xsl:call-template>
						</xsl:if>
						<xsl:if test="not(page/plain_catalog/product)">
							<h3>На складах не найден</h3>
						</xsl:if>
						<script type="text/javascript">
							insertAjax('cart_ajax');
						</script>
					</div>
				</div>
			</div>
		</div>
	</xsl:template>



</xsl:stylesheet>