<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">
	<xsl:import href="common_page_base.xsl"/>
	<xsl:output method="html" encoding="UTF-8" media-type="text/xhtml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:template name="LEFT_COLOUMN">
		<xsl:call-template name="CATALOG_LEFT_COLOUMN"/>
	</xsl:template>

	<xsl:variable name="title">Поиск по запросу "<xsl:value-of select="page/variables/q"/>"</xsl:variable>
	<xsl:variable name="h1">Поиск по запросу "<xsl:value-of select="page/variables/q"/>"</xsl:variable>
	<xsl:variable name="active_menu_item" select="'catalog'"/>

	<xsl:variable name="view" select="page/variables/view"/>
	<xsl:variable name="results" select="page/mobilife/xml"/>
	<xsl:variable name="only_available" select="page/variables/minqty = '0'"/>


	<xsl:template name="PAGE_PATH">
		<div class="path path_common">
			<div class="path__item">
				<a class="path__link" href="{$main_host}">Главная страница</a>
				<div class="path__arrow"></div>
				<a class="path__link" href="{page/catalog_link}">Поиск заказа</a>
			</div>
		</div>

	</xsl:template>


	<xsl:template name="CONTENT">
        <div class="devices devices_section">
            <xsl:if test="not($results/item)">
                <xsl:if test="$results/result = 'incorrect_query'">
                    <h3><xsl:value-of select="$results/message"/></h3>
                </xsl:if>
                <h4>По заданным критериям заказы не найдены</h4>
            </xsl:if>
            <div class="devices__wrap devices__wrap_rows">
                <table>
                    <xsl:for-each select="$results/item">
                        <tr><td>Номер заказа:</td><td><xsl:value-of select="num" /></td></tr>
                        <tr><td>IMEI:</td><td><xsl:value-of select="imei" /></td></tr>
                        <tr><td>Статус заказа:</td><td><xsl:value-of select="status" /></td></tr>
                        <tr><td>Стоимость:</td><td><xsl:value-of select="cost" /></td></tr>
                    </xsl:for-each>
                </table>
            </div>
        </div>
	</xsl:template>


	<xsl:template name="EXTRA_SCRIPTS">
		<xsl:call-template name="CART_SCRIPT"/>
	</xsl:template>

</xsl:stylesheet>