<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:f="f:f"
	version="2.0"
	exclude-result-prefixes="xsl f">
	<xsl:import href="../styles_fwk/utils.xsl"/>
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="crumbs" select="html//ol[contains(@class, 'MuiBreadcrumbs-ol')]//a"/>
	<xsl:variable name="href_id" select="f:create_id(html/body/@source, 'digikey/')"/>
	<xsl:variable name="tables" select="html//table[contains(@class, 'MuiTable-root')]"/>
	<xsl:variable name="table_1" select="$tables[1]"/>
	<xsl:variable name="table_2" select="$tables[2]"/>

	<xsl:template match="/">
		<result>
			<xsl:for-each select="$crumbs[position() &gt; 1]">
				<xsl:variable name="pos" select="position()"/>
				<section id="{@href}">
					<h_parent parent="{$crumbs[position() = $pos]/@href}" element="section"/>
					<name><xsl:value-of select="." /></name>
				</section>
			</xsl:for-each>

			<product id="{$href_id}">
				<h_parent parent="{f:create_id($crumbs[position() = last()]/@href, 'digikey/')}" element="section"/>
				<parameter_1>
					<name>Manufacturer</name>
					<value><xsl:value-of select="$table_1//tr[@data-testid = 'overview-manufacturer']/td[2]//a" /></value>
				</parameter_1>
				<parameter_2>
					<name>Manufacturer Product Number</name>
					<value><xsl:value-of select="$table_1//td[@data-testid = 'mfr-number']/div" /></value>
				</parameter_2>
				<parameter_3>
					<name>Description</name>
					<value><xsl:value-of select="$table_1//tr[.//div = 'Description']/td[2]/div" /></value>
				</parameter_3>
				<parameter_4>
					<name>Digi-Key Part Number</name>
					<value><xsl:value-of select="$table_1//tr[.//div = 'Digi-Key Part Number']/td[2]/div" /></value>
				</parameter_4>
				<xsl:variable name="cats" select="$table_2//tr[.//div = 'Category']/td[2]//a"/>
				<parameter_5>
					<name>First Category</name>
					<value><xsl:value-of select="$cats[1]" /></value>
				</parameter_5>
				<parameter_6>
					<name>Second Category</name>
					<value><xsl:value-of select="$cats[2]" /></value>
				</parameter_6>
				<parameter_7>
					<name>Package</name>
					<value><xsl:for-each select="$table_2//tr[.//div = 'Package']/td[2]/div/div"><xsl:value-of select="normalize-space(.)" /><xsl:if test="position() != last()">, </xsl:if></xsl:for-each></value>
				</parameter_7>
				<parameter_8>
					<xsl:variable name="param_trs" select="$table_2//tr[.//div = 'Product Status']/following-sibling::tr"/>
					<name>Description</name>
					<value><xsl:value-of select="string-join($param_trs/td[2]/div/div[. != '-'], 'ｦｸ')" /></value>
				</parameter_8>
				<parameter_9>
					<name>ECCN</name>
					<value><xsl:value-of select="html//div[@data-testid = 'data-table-Environmental &amp; Export Classifications']//tr[.//td = 'ECCN']/td[2]" /></value>
				</parameter_9>
			</product>
		</result>

		<!--
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

                        <name><xsl:value-of select="$name"/></name>
                        <description>
                            <xsl:copy-of select="html//div[@class = 'ty-features-list']/*"/>
                        </description>
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
		-->
	</xsl:template>

<!--
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
-->


</xsl:stylesheet>