<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:f="f:f"
	version="2.0"
	exclude-result-prefixes="xsl f">
	<xsl:import href="../styles_fwk/utils.xsl"/>
	<xsl:output method="xml" encoding="UTF-8" media-type="text/xml" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*"/>

	<xsl:variable name="crumbs" select="body//ol[contains(@class, 'MuiBreadcrumbs-ol')]//a"/>
	<xsl:variable name="href_id" select="f:create_id(body/@source, 'digikey/')"/>
	<xsl:variable name="tables" select="body//table[contains(@class, 'MuiTable-root')]"/>
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
					<value><xsl:value-of select="normalize-space($table_1//tr[.//td[normalize-space(child::div) = 'Manufacturer']]/td[2]/div)" /></value>
				</parameter_1>
				<parameter_2>
					<name>Manufacturer Product Number</name>
					<value><xsl:value-of select="normalize-space($table_1//td[@data-testid = 'mfr-number']/div)" /></value>
				</parameter_2>
				<parameter_3>
					<name>Description</name>
					<value><xsl:value-of select="normalize-space($table_1//tr[.//td[normalize-space(child::div) = 'Description']]/td[2]/div)" /></value>
				</parameter_3>
				<parameter_4>
					<name>Digi-Key Part Number</name>
					<value><xsl:value-of select="normalize-space($table_1//tr[.//td[normalize-space(child::div) = 'Digi-Key Part Number']]/td[2]/div)" /></value>
				</parameter_4>
				<xsl:variable name="cats" select="$table_2//tr[.//td[normalize-space(child::div) = 'Category']]/td[2]//a"/>
				<parameter_5>
					<name>First Category</name>
					<value><xsl:value-of select="normalize-space($cats[1])" /></value>
				</parameter_5>
				<parameter_6>
					<name>Second Category</name>
					<value><xsl:value-of select="normalize-space($cats[2])" /></value>
				</parameter_6>
				<parameter_7>
					<name>Package</name>
					<value><xsl:for-each select="$table_2//tr[.//td[normalize-space(child::div) = 'Package']]/td[2]/div/div"><xsl:value-of select="normalize-space(.)" /><xsl:if test="position() != last()">, </xsl:if></xsl:for-each></value>
				</parameter_7>
				<parameter_8>
					<name>ECCN</name>
					<value><xsl:value-of select="normalize-space(body//div[@data-testid = 'data-table-Environmental &amp; Export Classifications']//tr[.//td[normalize-space(.) = 'ECCN']]/td[2])" /></value>
				</parameter_8>
				<attributes>
					<xsl:variable name="param_trs" select="$table_2//tr[.//td[normalize-space(child::div) = 'Product Status']]/following-sibling::tr"/>
					<xsl:variable name="table" select="//div[normalize-space(text()) = 'Product Attributes']/following-sibling::table"/>
					<xsl:variable name="src_trs" select="if ($table) then $table/tbody/tr else $param_trs"/>
					<name>Product Attributes</name>
					<value>
						<xsl:for-each select="$src_trs">
							<param>
								<name><xsl:value-of select="normalize-space(td[1])" /></name>
								<value><xsl:value-of select="normalize-space(td[2])" /></value>
							</param>
						</xsl:for-each>
<!--						<xsl:value-of select="normalize-space(string-join($param_trs/td[2]/div/div[normalize-space(.) != '-'], 'ｦｸ'))" />-->
					</value>
				</attributes>
				<docs>
					<xsl:variable name="table" select="//div[normalize-space(text()) = 'Documents &amp; Media']/following-sibling::table"/>
					<name>Documents and Media</name>
					<value>
						<xsl:for-each select="$table/tbody/tr">
							<param>
								<name><xsl:value-of select="normalize-space(td[1])" /></name>
								<xsl:for-each select="td[2]//a">
									<value><xsl:copy-of select="."/></value>
								</xsl:for-each>
							</param>
						</xsl:for-each>
					</value>
				</docs>
				<environmental>
					<xsl:variable name="table" select="//div[normalize-space(text()) = 'Environmental &amp; Export Classifications']/following-sibling::table"/>
					<name>Environmental and Export Classifications</name>
					<value>
						<xsl:for-each select="$table/tbody/tr">
							<param>
								<name><xsl:value-of select="normalize-space(td[1])" /></name>
								<value><xsl:value-of select="normalize-space(td[2])" /></value>
							</param>
						</xsl:for-each>
					</value>
				</environmental>
				<additional>
					<xsl:variable name="table" select="//div[normalize-space(text()) = 'Additional Resources']/following-sibling::table"/>
					<name>Additional Resources</name>
					<value>
						<xsl:for-each select="$table/tbody/tr">
							<param>
								<name><xsl:value-of select="normalize-space(td[1])" /></name>
								<xsl:if test="td[2]/div/div">
									<xsl:for-each select="td[2]/div/div">
										<value><xsl:value-of select="normalize-space(.)" /></value>
									</xsl:for-each>
								</xsl:if>
								<xsl:if test="not(td[2]/div/div)">
									<value><xsl:value-of select="normalize-space(td[2]/div)" /></value>
								</xsl:if>
							</param>
						</xsl:for-each>
					</value>
				</additional>
			</product>
		</result>


	</xsl:template>



</xsl:stylesheet>