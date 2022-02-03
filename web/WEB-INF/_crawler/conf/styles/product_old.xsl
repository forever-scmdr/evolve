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


	<xsl:template match="/">
		<xsl:if test="$isSection or $isProduct">
			<result>

				<xsl:if test="$isSection">

				</xsl:if>


				<xsl:if test="$isProduct">
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
				</xsl:if>

			</result>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>