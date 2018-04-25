<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="2.0">

    <xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="no"/>
    <xsl:strip-space elements="*"/>

    <xsl:variable name="base" select="page/base"/>

    <xsl:template match="/">
        <urlset xsi:schemaLocation="http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd">
            <url>
                <loc><xsl:value-of select="page/base"/></loc>
                <changefreq>daily</changefreq>
                <priority>1.00</priority>
            </url>
            <url>
                <loc><xsl:value-of select="concat($base, '/', page/catalog_link)"/></loc>
                <changefreq>daily</changefreq>
                <priority>1.00</priority>
            </url>
            <url>
                <loc><xsl:value-of select="concat($base, '/', page/news_link)"/></loc>
                <changefreq>daily</changefreq>
                <priority>0.70</priority>
            </url>
            <url>
                <loc><xsl:value-of select="concat($base, '/', page/contacts_link)"/></loc>
                <changefreq>monthly</changefreq>
                <priority>0.70</priority>
            </url>
            <url>
                <loc><xsl:value-of select="concat($base, '/', page/dealers_link)"/></loc>
                <changefreq>monthly</changefreq>
                <priority>0.70</priority>
            </url>
            <url>
                <loc><xsl:value-of select="concat($base, '/', page/docs_link)"/></loc>
                <changefreq>monthly</changefreq>
                <priority>0.70</priority>
            </url>
            <url>
                <loc><xsl:value-of select="concat($base, '/', page/articles_link)"/></loc>
                <changefreq>monthly</changefreq>
                <priority>0.70</priority>
            </url>
            <xsl:apply-templates select="page/catalog/section"/>
            <xsl:for-each select="page/news_item">
                <url>
                    <loc><xsl:value-of select="concat($base, '/', show_news_item)"/></loc>
                    <changefreq>monthly</changefreq>
                    <priority>0.70</priority>
                </url>
            </xsl:for-each>
        </urlset>
    </xsl:template>

    <xsl:template match="section">
        <xsl:if test="section">
            <url>
                <loc><xsl:value-of select="concat($base, '/', show_section)"/></loc>
                <changefreq>daily</changefreq>
                <priority>0.80</priority>
            </url>
            <xsl:apply-templates select="section"/>
        </xsl:if>
        <xsl:if test="not(section)">
            <xsl:text disable-output-escaping="yes">&lt;!--</xsl:text>
                <xsl:value-of select="@key"/>
            <xsl:text disable-output-escaping="yes">--&gt;</xsl:text>

            <url>
                <loc>
                    <xsl:value-of select="show_products"/>
                </loc>
                <changefreq>daily</changefreq>
                <priority>0.80</priority>
            </url>
        </xsl:if>
    </xsl:template>


</xsl:stylesheet>