<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="2.0">

    <xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="no"/>
    <xsl:strip-space elements="*"/>

    <xsl:variable name="base" select="if(page/url_seo_wrap/main_host != '') then page/url_seo_wrap/main_host else page/base"/>
    <xsl:variable name="schema_location"
                  select="'http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd'"/>

    <xsl:variable name="freq" select="page/sitemap_settings/changefreq_product"/>

    <xsl:template match="/">
        <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<xsl:text disable-output-escaping="yes">
   &lt;!--**********************************************************************************--&gt;
</xsl:text>
            <xsl:text disable-output-escaping="yes">
    &lt;!--**********   </xsl:text><xsl:value-of select="page/section/name"/><xsl:text disable-output-escaping="yes">  ********--&gt;
</xsl:text>
            <xsl:text disable-output-escaping="yes">
    &lt;!--**********************************************************************************--&gt;
</xsl:text>
<!--            <xsl:text disable-output-escaping="yes"> &lt;!- - tags  - -></xsl:text>-->
<!--            <xsl:for-each select="/page/section/tag">-->
<!--                <url>-->
<!--                    <loc>-->
<!--                        <xsl:value-of select="concat($base, canonical)"/>-->
<!--                    </loc>-->
<!--                    <changefreq>daily</changefreq>-->
<!--                    <priority>0.80</priority>-->
<!--                </url>-->
<!--            </xsl:for-each>-->
<!--            <xsl:text disable-output-escaping="yes"> &lt;!- - END_tags  - -></xsl:text>-->
            <next><xsl:value-of select="page/section/product_pages/next/link"/></next>
            <xsl:for-each select="/page/section/product">
                <url>
                    <loc>
                        <xsl:value-of select="concat($base, show_product)"/>
                    </loc>
                    <changefreq>daily</changefreq>
                    <priority>0.80</priority>
                </url>
            </xsl:for-each>

        </urlset>
    </xsl:template>

</xsl:stylesheet>