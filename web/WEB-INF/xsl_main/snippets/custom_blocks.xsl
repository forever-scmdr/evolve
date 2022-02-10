<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">


	<xsl:template match="custom_block[type='в 2 колонки']">
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_top" />
		</xsl:call-template>
		<div class="block catalog-block ptb">
			<div class="container">
				<div class="title title_2 ptb"><xsl:value-of select="header" /></div>
				<div class="catalog-items">
					<div class="catalog-items__wrap">
						<xsl:for-each select="custom_block">
							<a href="{link}" class="catalog-item">
								<div class="catalog-item__image img">
									<img src="{concat(@path, image)}" onerror="$(this).attr('src', 'img/no_image.png'); this.removeAttribute('onerror')" alt="{header}" />
								</div>
								<div class="catalog-item__info">
									<div class="catalog-item__title">
										<xsl:value-of select="header"/>
									</div>
									<div class="catalog-item__text">
										<xsl:value-of select="text" disable-output-escaping="yes"/>
									</div>
								</div>
							</a>
						</xsl:for-each>
					</div>
				</div>
			</div>
		</div>
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_bottom" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="custom_block[type='карусель']">

		<xsl:variable name="type" select="type"/>
		<xsl:variable name="p" select="count(.|preceding-sibling::custom_block[type=$type])"/>

		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_top" />
		</xsl:call-template>
		<div class="container vl_pos_rel ptb">
			<h2 class="ptb banner-numbers ">
				<b class="color2"><xsl:value-of select="header"/></b>
				<xsl:if test="subheader != ''">
					&#160;<b class="color1"><xsl:value-of select="subheader"/></b>
				</xsl:if>
			</h2>
			<div class="vl_c_carousel">
        		<div class="vl_c_content">
        			<xsl:for-each select="custom_block">
        				<a href="{link}" class="vl_c_item" style="{if(image_bgr != '') then concat('background-image: url(',@path,'/', image_bgr,'); background-size: contain; background-position: center; background-repeat: no-repeat;') else ''}">
						    <div class="vl_c_img"><img src="{concat(@path, image)}" alt=""/></div>
						  	<p>
								<b><xsl:value-of select="header" /></b>
								<xsl:if test="subheader != ''">
									&#160;<b class="color1"><xsl:value-of select="subheader"/></b>
								</xsl:if>
							</p>
							<xsl:value-of select="text" disable-output-escaping="yes"/>
						  </a>
        			</xsl:for-each>
        		</div>
        	</div>
        	 <button class="vl_c_prev">
				<svg
				  xmlns="http://www.w3.org/2000/svg"
				  width="24"
				  height="24"
				  viewBox="0 0 24 24"
				>
				  <path fill="none" d="M0 0h24v24H0V0z" />
				  <path d="M15.61 7.41L14.2 6l-6 6 6 6 1.41-1.41L11.03 12l4.58-4.59z" />
				</svg>
			  </button>
			  <button class="vl_c_next">
				<svg
				  xmlns="http://www.w3.org/2000/svg"
				  width="24"
				  height="24"
				  viewBox="0 0 24 24"
				>
				  <path fill="none" d="M0 0h24v24H0V0z" />
				  <path d="M10.02 6L8.61 7.41 13.19 12l-4.58 4.59L10.02 18l6-6-6-6z" />
				</svg>
			  </button>
		</div>
		<xsl:if test="$p = count(//custom_block[type=$type])">
			<script src="js/script-sl.js"></script>
		</xsl:if>
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_bottom" />
		</xsl:call-template>
	</xsl:template>



	<xsl:template match="custom_block[type='в 2 колонки с текстом и заголовком']">
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_top" />
		</xsl:call-template>

		<xsl:if test="f:num(odd_style) != 1">
			<div class="block blockquote-block ptb">
				<div class="container">
					<div class="blockquote-block__wrap">
						<div class="blockquote-block__title title title_2"><xsl:value-of select="header" /></div>
						<div class="blockquote-block__text">
							<p><xsl:value-of select="text" disable-output-escaping="yes" /></p>
						</div>
					</div>
				</div>
			</div>
		</xsl:if>

		<xsl:if test="f:num(odd_style) = 1">
			<div class="block blockquote-block ptb">
				<xsl:variable name="type" select="type"/>
				<xsl:variable name="p" select="count(.|preceding-sibling::custom_block[type=$type])"/>
				<xsl:variable name="odd" select="$p mod 2 = 1"/>
				<div class="container">
					<div class="blockquote-block__wrap">
						<div class="blockquote-block__title title title_2"><xsl:value-of select="header" /></div>
						<xsl:if test="$odd">
							<img src="{@path}{image}" alt="" />
						</xsl:if>
						<div class="blockquote-block__text">
							<xsl:value-of select="text" disable-output-escaping="yes" />
						</div>
						<xsl:if test="not($odd)">
							<img src="{@path}{image}" alt="" style="margin-left: 15px; margin-rigt:0;" />
						</xsl:if>
					</div>
				</div>
			</div>
		</xsl:if>

		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_bottom" />
		</xsl:call-template>
	</xsl:template>


	<xsl:template match="custom_block[type='в 4 колонки']">
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_top" />
		</xsl:call-template>
		<div class="block sections-block ptb">
			<div class="container">
				<div class="title title_2"><xsl:value-of select="header" /></div>
					<div class="sections-block_wrap">
						<xsl:for-each select="custom_block">
							<div class="banner-sections">
								<div class="banner-sections__image img">
									<img src="{@path}{image}" alt="" />
								</div>
								<div class="banner-sections__title"><xsl:value-of select="header" /></div>
								<a href="{link}" class="banner-sections__link"></a>
							</div>
						</xsl:for-each>
					</div>
			</div>
		</div>
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_bottom" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="custom_block[type='счетчик']">
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_top" />
		</xsl:call-template>
		<div class="block numbers-block ptb">
			<div class="container">
				<div class="numbers-block__wrap">
					<xsl:for-each select="custom_block">
						<div class="banner-numbers">
							<div class="banner-numbers__title"><xsl:value-of select="header" /></div>
							<div class="banner-numbers__text"><xsl:value-of select="subheader" /></div>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</div>
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_bottom" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="custom_block[type='УТП']">
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_top" />
		</xsl:call-template>
		<div class="block icons-block ptb">
			<div class="container">
				<div class="title title_2"><xsl:value-of select="header" /></div>
				<div class="icons-block__wrap">
					<xsl:for-each select="custom_block">
						<div class="banner-icons">
							<div class="banner-icons__image"><img src="{@path}{image}" alt="" /></div>
							<div class="banner-icons__title"><xsl:value-of select="header" /></div>
							<div class="banner-icons__text"><xsl:value-of select="text" disable-output-escaping="yes" /></div>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</div>
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_bottom" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="custom_block[type='Товары']">
		<div class="block devices-block ptb">
			<div class="container">
				<div class="title title_2"><xsl:value-of select="header"/></div>
				<div class="devices-block__wrap device-carousel">
					<xsl:for-each select="product">
						<!-- <div class="devices-block__column"> -->
								<xsl:apply-templates select="." mode="product-table"/>
						<!-- </div> -->
					</xsl:for-each>
				</div>
				<div class="device-nav"></div>
			</div>
		</div>
	</xsl:template>	

	<xsl:template match="custom_block[type='подарки']">
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_top" />
		</xsl:call-template>
		<div class="block gifts-block ptb">
			<div class="container">
				<div class="gifts-block__wrap">
					<xsl:for-each select="custom_block">
						<div class="banner-gift">
							<div class="banner-gift__image"><img src="{@path}{image}" alt="" /></div>
							<div class="banner-gift__title"><xsl:value-of select="header" /></div>
							<div class="banner-gift__text"><xsl:value-of select="text" disable-output-escaping="yes" /></div>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</div>
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_bottom" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="custom_block[type='яндекс-карта']">
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_top" />
		</xsl:call-template>
		<div class="block contacts-block ptb">
				<div class="container">
					<div class="contacts-block__wrap">
						<div class="contacts-block_title title title_2"><xsl:value-of select="header" /></div>
						<div class="contacts-block_subtitle"><xsl:value-of select="subheader" /></div>
						<div class="contacts-block_text"><xsl:value-of select="text" disable-output-escaping="yes" /></div>
					</div>
					<div class="map">
						<xsl:value-of select="code" disable-output-escaping="yes"/>
					</div>
				</div>
			</div>
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_bottom" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="DIVIDER">
		<xsl:param name="need"/>
		<xsl:if test="f:num($need) = 1"><div class="divider"></div></xsl:if>
	</xsl:template>

</xsl:stylesheet>
