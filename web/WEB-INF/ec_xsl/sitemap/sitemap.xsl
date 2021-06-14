<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="2.0">

	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="no"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="base" select="if(page/url_seo_wrap/main_host != '') then page/url_seo_wrap/main_host else page/base"/>

	<xsl:variable name="schema_location"
				  select="'http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd'"/>

	<xsl:variable name="settings" select="page/sitemap_settings" />
	<xsl:variable name="text_freq" select="$settings/changefreq"/>
	<xsl:variable name="news_freq" select="$settings/changefreq_news"/>
	<xsl:variable name="sec_freq" select="$settings/changefreq_section"/>

	<xsl:template match="/">
		<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<url>
				<loc>
					<xsl:value-of select="page/base"/>
				</loc>
				<changefreq>
					<xsl:value-of select="$settings/changefreq_index"/>
				</changefreq>
				<priority>1.00</priority>
			</url>
			<url>
				<loc>
					<xsl:value-of select="concat($base, '/', page/catalog_link)"/>
				</loc>
				<changefreq>
					<xsl:value-of select="$settings/changefreq_catalog"/>
				</changefreq>
				<priority>1.00</priority>
			</url>
			<xsl:for-each select="page/news">
				<url>
					<loc>
						<xsl:value-of select="concat($base, '/', show_page)"/>
					</loc>
					<changefreq>
						<xsl:value-of select="$news_freq"/>
					</changefreq>
					<priority>0.70</priority>
				</url>
			</xsl:for-each>
			<url>
				<loc>
					<xsl:value-of select="concat($base, '/', page/contacts_link)"/>
				</loc>
				<changefreq>
					<xsl:value-of select="$text_freq"/>
				</changefreq>
				<priority>0.70</priority>
			</url>
			<xsl:apply-templates select="page/catalog/section"/>
			<xsl:for-each select="page/custom_page">
				<url>
					<loc>
						<xsl:value-of select="concat($base, show_page)"/>
					</loc>
					<changefreq>
						<xsl:value-of select="$sec_freq"/>
					</changefreq>
					<priority>0.70</priority>
				</url>
			</xsl:for-each>
			<xsl:for-each select="page/news_item">
				<url>
					<loc>
						<xsl:value-of select="concat($base, show_news_item)"/>
					</loc>
					<changefreq>
						<xsl:value-of select="$text_freq"/>
					</changefreq>
					<priority>0.70</priority>
				</url>
			</xsl:for-each>
		</urlset>
	</xsl:template>

	<xsl:template match="section">
		<xsl:if test="section">
			<url>
				<loc>
					<xsl:value-of select="concat($base, '/', show_section)"/>
				</loc>
				<changefreq><xsl:value-of select="$sec_freq"/></changefreq>
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
					<xsl:value-of select="concat($base, show_products)"/>
				</loc>
				<changefreq><xsl:value-of select="$sec_freq"/></changefreq>
				<priority>0.80</priority>
			</url>
		</xsl:if>
	</xsl:template>


</xsl:stylesheet>