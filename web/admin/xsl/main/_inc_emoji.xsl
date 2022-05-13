<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<xsl:template name="EMOJI_CATEGORY">
		<xsl:param name="name"/>
		<xsl:param name="codes" />
		<xsl:param name="names" select="''" />
		<div class="emoji-container">
			<h3>
				<xsl:value-of select="string($name)"/>
			</h3>
			<div class="emoji">
				<xsl:for-each select="$codes">
					<div class="btn" title="{if($names != '') then $names[position()] else ''}" data-val="{.}">
						<xsl:if test="string-length(string(.)) &gt; 1">
							<xsl:value-of select="concat('&amp;#', ., ';')" disable-output-escaping="yes"/>
						</xsl:if>
						<xsl:if test="string-length(string(.)) = 1">
							<xsl:value-of select="." disable-output-escaping="yes"/>
						</xsl:if>
					</div></xsl:for-each>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="FACES">
		<xsl:call-template name="EMOJI_CATEGORY">
			<xsl:with-param name="name" select="'Смайлики'"/>
			<xsl:with-param name="codes" select="('128512','128515','128516','128513','128518','128517','129315','128514','128578','128579','128521','128522','128519','129392','128525','129321','128536','128535','128538','128537','128523','128539','128540','129322','128541','129297','129303','129325','129323','129300','129296','129320','128528','128529','128566','128527','128530','128580','128556','129317','128524','128532','128554','129316','128564','128567','129298','129301','129314','129326','129319','129397','129398','129396','128565','129327','129312','129395','128526','129299','129488','128533','128543','128577','9785','128558','128559','128562','128563','129402','128550','128551','128552','128560','128549','128546','128557','128561','128534','128547','128542','128531','128553','128555','128548','128545','128544','129324','128520','128127','128128','9760','128169','129313','128121','128122','128123','128125','128126','129302','128570','128568','128569','128571','128572','128573','128576','128575','128574','128584','128585','128586')"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="FLAGS">
		<xsl:call-template name="EMOJI_CATEGORY">
			<xsl:with-param name="name" select="'Флаги'"/>
			<xsl:with-param name="codes" select="''"/>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>