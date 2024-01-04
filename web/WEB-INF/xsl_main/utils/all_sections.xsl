<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<xsl:template name="BR"><xsl:text disable-output-escaping="yes">&lt;br /&gt;</xsl:text></xsl:template>



	<!-- ****************************    ПУСТЫЕ ЧАСТИ ДЛЯ ПЕРЕОПРЕДЕЛЕНИЯ    ******************************** -->


	<xsl:template name="MAIN_CONTENT">
		<div class="content">
			<div class="container">
				<div class="content__wrap">
					<ul>
						<xsl:for-each select="page/catalog/section">
							<li><xsl:value-of select="name" /></li>
							<xsl:if test="section">
								<ul>
									<xsl:for-each select="section">
										<li><xsl:value-of select="name" /></li>
										<xsl:if test="section">
											<ul>
												<xsl:for-each select="section">
													<li><xsl:value-of select="name" /></li>
												</xsl:for-each>
											</ul>
										</xsl:if>
									</xsl:for-each>
								</ul>
							</xsl:if>
						</xsl:for-each>
					</ul>
				</div>
			</div>
		</div>
	</xsl:template>



	<!-- ****************************    СТРАНИЦА    ******************************** -->


	<xsl:template match="/">
	<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;
	</xsl:text>
		<html lang="ru">
			<head>
				<meta charset="utf-8"/>
				<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
				<meta name="viewport" content="width=device-width, initial-scale=1"/>
			</head>
			<body>
				<!-- ALL CONTENT BEGIN -->
				<div class="wrapper">
					<xsl:call-template name="MAIN_CONTENT"/>
				</div>
				<!-- ALL CONTENT END -->


			</body>
		</html>
	</xsl:template>






</xsl:stylesheet>
