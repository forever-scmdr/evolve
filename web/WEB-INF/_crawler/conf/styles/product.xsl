<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:f="f:f"
	version="2.0"
	exclude-result-prefixes="xsl f">
	<xsl:import href="../styles_fwk/utils.xsl"/>
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="crumbs" select="html//div[contains(@id, 'breadcrumbs')]//a"/>
	<xsl:variable name="isCatalog" select="normalize-space($crumbs[2]/bdi) = 'Товары'"/>
	<xsl:variable name="isSection" select="html//div[contains(@class, 'side-grid')] and $isCatalog"/>
	<xsl:variable name="isProduct" select="$isCatalog and not($isSection)"/>

	<xsl:variable name="href_id" select="f:create_id(html/body/@source, 'meandr-shop.ru/')"/>

	<xsl:template match="/">
		<xsl:if test="$isSection or $isProduct">
			<result>

				<xsl:if test="$isSection">
					<section id="{$href_id}">
						<xsl:for-each select="$crumbs[position() &gt; 2]" >
							<h_parent parent="{f:create_id(@href, 'meandr-shop.ru/')}" element="section"/>
						</xsl:for-each>
						<name><xsl:value-of select="//h1/span"/></name>
					</section>
					<xsl:variable name="prods_container" select="html//div[@id = 'categories_view_pagination_contents']"/>
					<xsl:for-each select="$prods_container//div['ty-product-list' = tokenize(@class, ' ')]">
						<xsl:variable name="image_container" select=".//div[contains(@id, 'list_image')]"/>
						<xsl:variable name="img_el" select="$image_container//img"/>
						<product id="{f:create_id($image_container//a[1]/@href, 'meandr-shop.ru/')}">
							<xsl:if test="$img_el/@data-src">
								<main_pic download="{$image_container//img/@data-src}" link="{$image_container//img/@data-src}"/>
							</xsl:if>
							<xsl:if test="not($img_el/@data-src)">
								<main_pic download="{$image_container//img/@src}" link="{$image_container//img/@src}"/>
							</xsl:if>
							<name><xsl:value-of select="normalize-space(.//div[@class = 'ut2-pl__item-name']//a)" /></name>
							<code><xsl:value-of select=".//span[contains(@id, 'product_code')]" /></code>
							<short>
								<xsl:copy-of select=".//div[contains(@class, 'description')]"/>
								<xsl:copy-of select=".//div[contains(@class, 'feature')]"/>
							</short>
						</product>
					</xsl:for-each>
				</xsl:if>


				<xsl:if test="$isProduct">
					<xsl:for-each select="$crumbs[position() &gt; 2]">
						<xsl:variable name="pos" select="position() + 2"/>
						<section id="{f:create_id(@href, 'meandr-shop.ru/')}">
							<xsl:if test="$crumbs[position() = $pos - 1] and $pos &gt; 3">
								<h_parent parent="{f:create_id($crumbs[position() = $pos - 1]/@href, 'meandr-shop.ru/')}" element="section"/>
							</xsl:if>
							<name><xsl:value-of select="bdi" /></name>
						</section>
					</xsl:for-each>

					<xsl:variable name="pic_block" select="html//div[contains(@id, 'product_images') and contains(@id, 'update')]"/>
					<xsl:variable name="code" select="html//span[contains(@id, 'product_code')]"/>
					<xsl:variable name="name" select="html//h1/bdi"/>
					<product id="{$href_id}">
						<h_parent parent="{f:create_id($crumbs[position() = last()]/@href, 'meandr-shop.ru/')}" element="section"/>
						<code><xsl:value-of select="$code" /></code>
						<header><xsl:value-of select="$name"/></header>
<!--						<name><xsl:value-of select="if (starts-with($name, ':')) then normalize-space(substring-after($name, ':')) else $name"/></name>-->
                        <name><xsl:value-of select="$name"/></name>
                        <description>
                            <xsl:copy-of select="html//div[@class = 'ty-features-list']/*"/>
                        </description>
<!--
                        <xsl:for-each select="html//li[contains(@class, 's-video__item')]">
                            <video>
                                <link><xsl:value-of select="a/@target" /></link>
                                <pic download="{a/img/@src}"><xsl:value-of select="a/img/@src" /></pic>
                            </video>
                        </xsl:for-each>
                        -->
						<xsl:variable name="text_block" select="html//div[@id = 'content_description']/div"/>
						<text>
							<xsl:copy-of select="$text_block/*"/>
						</text>
						<text_pics>
							<xsl:for-each select="$text_block//img">
								<pic download="{substring-before(@src, '?')}" link="{substring-before(@src, '?')}"/>
							</xsl:for-each>
						</text_pics>
                        <tech>
                            <xsl:apply-templates select="html//div[@id = 'content_features']/*"/>
                        </tech>
                        <xsl:for-each select="html//p[@class = 'attachment__item']">
                            <xsl:variable name="name" select="substring-before(substring-after(., '('), ',')"/>
							<xsl:variable name="size" select="normalize-space(substring-before(substring-after(., ','), ')'))"/>
							<xsl:variable name="label" select="normalize-space(substring-before(., '('))"/>
							<manual>
                                <file download="{a/@href}"><xsl:value-of select="$name" /></file>
                                <title><xsl:value-of select="$label" /></title>
								<size><xsl:value-of select="$size" /></size>
                            </manual>
                        </xsl:for-each>
                        <gallery>
                            <xsl:for-each select="html//meta[@itemprop = 'image']">
                                <pic download="{@content}" link="{@content}"/>
                            </xsl:for-each>
                        </gallery>
						<assoc>
                            <xsl:for-each select="html//div[contains(@id, 'scroll_list')][1]/div">
                                <url><xsl:value-of select="f:create_id(.//a[@class = 'product-title']/@href, 'meandr-shop.ru/')"/></url>
                            </xsl:for-each>
                        </assoc>
					</product>

				</xsl:if>

			</result>
		</xsl:if>
	</xsl:template>


	<xsl:template match="div[@class = 'ty-product-feature-group']">
		<group>
			<name><xsl:value-of select="normalize-space(./div[@class= 'ty-subheader'])" /></name>
			<xsl:apply-templates select="./div[@class = 'ty-product-feature']"/>
		</group>
	</xsl:template>

	<xsl:template match="div[@class = 'ty-product-feature']">
		<parameter>
			<name><xsl:value-of select="normalize-space(./div[@class= 'ty-product-feature__label'])" /></name>
			<value><xsl:value-of select="normalize-space(./div[@class= 'ty-product-feature__value'])" /></value>
		</parameter>
	</xsl:template>



</xsl:stylesheet>