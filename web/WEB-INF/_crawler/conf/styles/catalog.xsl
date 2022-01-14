<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:f="f:f"
	version="2.0"
	exclude-result-prefixes="xsl f">
	<xsl:import href="../styles_fwk/utils.xsl"/>
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>


	<xsl:variable name="isCatalog" select="//h2 = 'Каталог выпускаемой и поставляемой продукции'"/>


	<xsl:template match="/">
		<xsl:if test="$isCatalog">
			<xsl:variable name="menuDiv" select="//div[@id = 'block-menu-menu-produckt']"/>
			<xsl:variable name="activeLI" select="$menuDiv//li[a/@class = 'active-trail active']"/>
			<xsl:variable name="crumbs" select="$menuDiv//li[.//a/@class = 'active-trail active']"/>
			<xsl:variable name="content" select="//div[@id = 'block-system-main']//div[@class = 'field-item even']"/>
			<xsl:variable name="firstTable" select="$content/table"/>
			<xsl:variable name="isSection" select="$activeLI/ul"/>
			<result>

				<!-- Путь (родители) -->

				<xsl:for-each select="$crumbs[position() != last()]">
					<xsl:variable name="pos" select="position()"/>
					<xsl:variable name="id" select="f:create_id(a/@href)"/>
					<section id="{$id}">
						<xsl:if test="$crumbs[position() = $pos - 1]">
							<xsl:variable name="parentId" select="f:create_id($crumbs[position() = $pos - 1]/a/@href)"/>
							<xsl:if test="$id != $parentId">
								<h_parent parent="{$parentId}" element="section"/>
							</xsl:if>
						</xsl:if>
						<name ><xsl:value-of select="a" /></name>
					</section>
				</xsl:for-each>

				<!-- Страница раздела -->

				<xsl:if test="$isSection">
					<xsl:variable name="hasSubs" select="$firstTable//tr[position() mod 2 = 1 and .//a/@href]"/>
					<section id="{f:create_id($activeLI/a/@href)}">
						<h_parent parent="{f:create_id($crumbs[position() = last() - 1]/a/@href)}" element="section"/>
						<h1><xsl:value-of select="//h1" /></h1>
						<name><xsl:value-of select="$activeLI/a" /></name>
						<xsl:if test="not($hasSubs)">
							<text><xsl:copy-of select="$firstTable"/></text>
							<text_pics>
								<xsl:for-each select="$firstTable//img">
									<pic download="https://www.meandr.ru{@src}" link="{@src}"/>
								</xsl:for-each>
							</text_pics>
						</xsl:if>
					</section>
					<xsl:if test="$hasSubs">
						<xsl:for-each select="$firstTable//tr[position() mod 2 = 1]">
							<xsl:variable name="position" select="position()"/>
							<xsl:variable name="nameTr" select="current()"/>
							<xsl:variable name="contentTr" select="$firstTable//tr[position() = 2 * $position]"/>
							<xsl:variable name="as" select="$nameTr//a"/>
							<xsl:variable name="sideMenuElement" select="$menuDiv//a[@href = $as[1]/@href]"/>
							<xsl:variable name="isSubsection" select="$sideMenuElement/following-sibling::ul"/>
							<xsl:if test="$sideMenuElement and $isSubsection">
								<section id="{f:create_id($as[1]/@href)}" pos="{$position}">
									<h_parent parent="{f:create_id($activeLI/a/@href)}" element="section"/>
									<name><xsl:value-of select="$nameTr//a" /></name>
									<main_pic download="https://www.meandr.ru{$contentTr/td[1]//img/@src}" link="{$contentTr/td[1]//img/@src}"/>
									<short><xsl:copy-of select="$contentTr/td[2]/*[position() != last()]"/></short>
								</section>
							</xsl:if>
							<xsl:if test="$sideMenuElement and not($isSubsection)">
								<product id="{f:create_id($as[1]/@href)}" pos="{$position}">
									<h_parent parent="{f:create_id($activeLI/a/@href)}" element="section"/>
									<name><xsl:value-of select="$nameTr//a" /></name>
									<main_pic download="https://www.meandr.ru{$contentTr/td[1]//img/@src}" link="{$contentTr/td[1]//img/@src}"/>
									<short><xsl:copy-of select="$contentTr/td[2]/*[position() != last()]"/></short>
								</product>
							</xsl:if>
							<xsl:if test="not($sideMenuElement)">
								<section id="{f:create_id($activeLI/a/@href)}">
									<text><xsl:copy-of select="$firstTable"/></text>
									<text_pics>
										<xsl:for-each select="$firstTable//img">
											<pic download="https://www.meandr.ru{@src}" link="{@src}"/>
										</xsl:for-each>
									</text_pics>
								</section>
							</xsl:if>
						</xsl:for-each>
					</xsl:if>
				</xsl:if>

				<!-- Страница товара -->

				<xsl:if test="not($isSection)">
					<product id="{f:create_id($activeLI/a/@href)}">
						<xsl:if test="count($crumbs) &gt; 1">
							<h_parent parent="{f:create_id($crumbs[position() = last() - 1]/a/@href)}" element="section"/>
						</xsl:if>
						<h1><xsl:value-of select="//h1" /></h1>
						<name><xsl:value-of select="$activeLI/a" /></name>
						<gallery>
							<xsl:copy-of select="$firstTable"/>
						</gallery>
						<gallery_pics>
							<xsl:for-each select="$firstTable//img">
								<pic download="https://www.meandr.ru{@src}" link="{@src}"/>
							</xsl:for-each>
						</gallery_pics>
						<products>
							<xsl:copy-of select="$content/div[table][1]/table"/>
						</products>
						<description>
							<xsl:copy-of select="$content/div[table][1]/following-sibling::*"/>
						</description>
						<description_pics>
							<xsl:for-each select="$content/div[table][1]/following-sibling::*//img">
								<pic download="https://www.meandr.ru{@src}" link="{@src}"/>
							</xsl:for-each>
							<xsl:for-each select="$content/div[table][1]/following-sibling::*//a[starts-with(@href, 'files') or starts-with(@href, '/files')]">
								<pic download="https://www.meandr.ru{@href}" link="{@href}"/>
							</xsl:for-each>
						</description_pics>
					</product>
				</xsl:if>


			</result>
		</xsl:if>


<!--
			<xsl:variable name="crumbs" select="html//div[@id='breadcrumbs_container'][1]//ol[1]/li[position() &gt; 1 and a]"/>
			<xsl:for-each select="$crumbs">
				<xsl:variable name="pos" select="position()"/>
				<section id="{a/@href}">
					<xsl:if test="$crumbs[position() = $pos - 1]">
						<h_parent parent="{$crumbs[position() = $pos - 1]/a/@href}" element="section"/>
					</xsl:if>
					<name><xsl:value-of select="a/span" /></name>
				</section>
			</xsl:for-each>
			<xsl:variable name="model_block" select="html//div[contains(@class, 's-nomenclature__sales-block-inner')]"/>
			<xsl:variable name="code" select="html//p[contains(@class, 's-nomenclature__articul')]/span[2]"/>
			<xsl:variable name="name" select="$model_block//span[contains(@class, 'js-panel-value')]"/>
			<product id="{$code}">
				<h_parent parent="{$crumbs[position() = last()]/a/@href}" element="section"/>
				<header><xsl:value-of select="//h1"/></header>
				<name><xsl:value-of select="if (starts-with($name, ':')) then normalize-space(substring-after($name, ':')) else $name"/></name>
				<code><xsl:value-of select="$code" /></code>
				<short>
					<xsl:copy-of select="html//div[@id = 'main_attributes']/ul/li"/>
				</short>
				<xsl:for-each select="html//li[contains(@class, 's-video__item')]">
					<video>
						<link><xsl:value-of select="a/@target" /></link>
						<pic download="{a/img/@src}"><xsl:value-of select="a/img/@src" /></pic>
					</video>
				</xsl:for-each>
				<description>
					<xsl:copy-of select="html//div[@id='text_attributes']"/>
				</description>
				<tech>
					<xsl:for-each select="html//div[@id = 'list_attributes']//tr[th]">
						<parameter>
							<name><xsl:value-of select="th"/></name>
							<value><xsl:value-of select="td"/></value>
						</parameter>
					</xsl:for-each>
				</tech>
				<xsl:for-each select="html//div[@id = 'files_lists']//li">
					<manual>
						<file download="{a/@href}"><xsl:value-of select="a/@href" /></file>
						<title><xsl:value-of select="div/a" /></title>
					</manual>
				</xsl:for-each>
				<gallery>
					<xsl:for-each select="html//div[contains(@class, 's-nomenclature__photo-item')]//img">
						<pic download="https://suzuki.ru{@data-full}" link="https://suzuki.ru{@data-full}"/>
					</xsl:for-each>
				</gallery>
				<assoc>
					<xsl:for-each select="html//ul[contains(@class, 's-catalog-groups__list')]/li">
						<name><xsl:value-of select="current()//div[2]/a/span"/></name>
					</xsl:for-each>
				</assoc>
			</product>
-->
	</xsl:template>

</xsl:stylesheet>