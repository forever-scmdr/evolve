<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:f="f:f" version="2.0">

	<xsl:variable name="need_container" select="/page/@name = 'index' or $hide_side_menu"/>

	<!-- в 2 колонки подблоки -->

	<xsl:template match="custom_block[type='в 2 колонки']">
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_top" />
		</xsl:call-template>
		<div class="block catalog-block pb">
			<div class="{'container'[$need_container]}">
				<div class="title title_2 ptb"><xsl:value-of select="header" /></div>
				<div class="catalog-items">
					<div class="catalog-items__wrap">
						<xsl:apply-templates select="custom_block | section  | custom_page" mode="sub-2-cols"/>
					</div>
				</div>
			</div>
		</div>
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_bottom" />
		</xsl:call-template>
	</xsl:template>

	

	<!-- custom_block -->
	<xsl:template match="custom_block" mode="sub-2-cols">
		<div class="catalog-item cover-link-wrap">
			<xsl:if test="link != ''">
				<a href="{link}" class="cover-link"></a>
			</xsl:if>
			<div class="catalog-item__image img">
				<img src="{concat(@path, image)}" onerror="{$onerror}" alt="{header}" />
			</div>
			<div class="catalog-item__info">
				<div class="catalog-item__title">
					<xsl:value-of select="header"/>
				</div>
				<div class="catalog-item__text">
					<xsl:value-of select="text" disable-output-escaping="yes"/>
				</div>
			</div>
		</div>
	</xsl:template>

	<!-- product -->
	<xsl:template match="product" mode="sub-2-cols">
		
		<xsl:variable  name="main_pic" select="if(small_pic != '') then small_pic else main_pic"/>
		<xsl:variable name="pic_path" select="if ($main_pic) then concat(@path, $main_pic) else 'img/no_image.png'"/>

		<div class="catalog-item cover-link-wrap">
			<a href="{show_product}" class="cover-link"></a>
			<div class="catalog-item__image img">
				<img src="{$pic_path}" onerror="{$onerror}" alt="{name}" />
			</div>
			<div class="catalog-item__info">
				<div class="catalog-item__title">
					<xsl:value-of select="name"/>
				</div>
				<div class="catalog-item__text">
					<xsl:value-of select="description" disable-output-escaping="yes"/>
				</div>
			</div>
		</div>
	</xsl:template>

	<!-- section -->
	<xsl:template match="section" mode="sub-2-cols">
		<xsl:variable name="pic_path" select="if (main_pic) then concat(@path, main_pic) else 'img/no_image.png'"/>

		<div class="catalog-item cover-link-wrap">
			<a href="{show_products}" class="cover-link"></a>
			<div class="catalog-item__image img">
				<img src="{$pic_path}" onerror="{$onerror}" alt="{name}" />
			</div>
			<div class="catalog-item__info">
				<div class="catalog-item__title">
					<xsl:value-of select="name"/>
				</div>
				<div class="catalog-item__text">
					<xsl:value-of select="short" disable-output-escaping="yes"/>
				</div>
			</div>
		</div>
	</xsl:template>

	<!-- custom_page -->
	<xsl:template match="custom_page" mode="sub-2-cols">
		<xsl:variable name="pic_path" select="if (main_pic) then concat(@path, main_pic) else 'img/no_image.png'"/>

		<div class="catalog-item cover-link-wrap">
			<a href="{show_page}" class="cover-link"></a>
			<div class="catalog-item__image img">
				<img src="{$pic_path}" onerror="{$onerror}" alt="{header}" />
			</div>
			<div class="catalog-item__info">
				<div class="catalog-item__title">
					<xsl:value-of select="header"/>
				</div>
				<div class="catalog-item__text">
					<xsl:value-of select="short" disable-output-escaping="yes"/>
				</div>
			</div>
		</div>
	</xsl:template>

	<!-- END в 2 колонки подблоки -->


	<!-- карусель -->

	<xsl:template match="custom_block[type='карусель']">

		<xsl:variable name="type" select="type"/>
		<xsl:variable name="p" select="count(.|preceding-sibling::custom_block[type=$type])"/>

		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_top" />
		</xsl:call-template>
		<div class="{'container '[$need_container]}vl_pos_rel pb">
			<h2 class="ptb banner-numbers ">
				<b class="color2"><xsl:value-of select="header"/></b>
				<xsl:if test="subheader != ''">
					&#160;<b class="color1"><xsl:value-of select="subheader"/></b>
				</xsl:if>
			</h2>
			<div class="vl_c_carousel">
				<div class="vl_c_content">
					<xsl:apply-templates select="custom_block | section | custom_page" mode="carousel-1"/>
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


	<!-- custom_block -->
	<xsl:template match="custom_block" mode="carousel-1">
		<div  class="vl_c_item" style="{if(image_bgr != '') then concat('background-image: url(',@path,'/', image_bgr,'); background-size: contain; background-position: center; background-repeat: no-repeat;') else ''}">
			<xsl:if test="link != ''">
				<a href="{link}" class="cover-link"></a>
			</xsl:if>
			<div class="vl_c_img">
				<xsl:if test="image != ''">
					<img src="{concat(@path, image)}" onerror="{$onerror}"/>
				</xsl:if>
			</div>
			<p>
			<b><xsl:value-of select="header" /></b>
				<xsl:if test="subheader != ''">
					&#160;<b class="color1"><xsl:value-of select="subheader"/></b>
				</xsl:if>
			</p>
			<xsl:value-of select="text" disable-output-escaping="yes"/>
			<xsl:value-of select="code" disable-output-escaping="yes"/>
		</div>
	</xsl:template>

	<xsl:template match="section" mode="carousel-1">
		<xsl:variable name="pic_path" select="if (main_pic) then concat(@path, main_pic) else 'img/no_image.png'"/>

		<div class="vl_c_item">
			<a href="{show_products}" class="cover-link"></a>
			<div class="vl_c_img">
				<img src="{$pic_path}" 
					onerror="{$onerror}"
					alt="{name}"
				/>
			</div>
			<p>
				<b>
					<xsl:value-of select="name" disable-output-escaping="yes"/>
				</b>
			</p>
			<xsl:value-of select="short" disable-output-escaping="yes"/>
		</div>
	</xsl:template>

	<xsl:template match="product" mode="carousel-1">
		<div class="vl_c_item" style="padding:0;overflow:hidden;">
			<xsl:apply-templates select="." mode="product-table"/>
		</div>
	</xsl:template>	


	<xsl:template match="custom_page" mode="carousel-1">
		<xsl:variable name="pic_path" select="if (main_pic) then concat(@path, main_pic) else 'img/no_image.png'"/>

		<div class="vl_c_item">
			<a href="{show_page}" style="position:absolute; top:0; bottom:0; left:0; right:0;"></a>
			<div class="vl_c_img">
				<img src="{$pic_path}" 
					onerror="{$onerror}"
					alt="{name}"
				/>
			</div>
			<p>
				<b>
					<xsl:value-of select="header" disable-output-escaping="yes"/>
				</b>
			</p>
			<xsl:value-of select="short" disable-output-escaping="yes"/>
		</div>
	</xsl:template>


	<!-- END карусель -->


	<!-- Цитата -->

	<xsl:template match="custom_block[type='цитата']">
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_top" />
		</xsl:call-template>

		<div class="{'container'[$need_container]}">
			<div class="block blockquote-block ptb">
				<div class="blockquote-block__title title title_2"><xsl:value-of select="header" /></div>
					<div class="blockquote-block__text">
					<p><xsl:value-of select="text" disable-output-escaping="yes" /></p>
				</div>
			</div>
		</div>

		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_bottom" />
		</xsl:call-template>
	</xsl:template>

	<!-- END Цитата -->

	<!-- шахматный порядок -->
	<xsl:template match="custom_block[type='шахматный порядок']">
		<xsl:variable name="children" select="custom_block | section | custom_page"/>

		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_top" />
		</xsl:call-template>


		<div class="{'container'[$need_container]}">
			<div class="block chess-block ptb">
				<xsl:if test="(text | header) != ''">
					<div class="text">
						<h2>
							<xsl:value-of select="header"/>
						</h2>
						<xsl:value-of select="text" disable-output-escaping="yes"/>
					</div>
				</xsl:if>
				<xsl:for-each select="$children">

						<xsl:variable name="first" select="position() = 1"/>
						<xsl:variable name="odd" select="position() mod 2 = 1"/>

						<xsl:variable name="name" select="name()"/>
						<xsl:variable name="header" select="if($name != 'section') then header else name"/>
						<xsl:variable name="pic" select="if($name = 'custom_block') then concat(@path, image) else concat(@path, main_pic)"/>
						<xsl:variable name="text" select="if($name = 'custom_block') then text else short"/>

						<div class="chess-row{if($odd) then ' odd' else ' even'}">
							<xsl:if test="$odd">
								<div class="chess-pic" style="background-image: url('{$pic}');">
									<!-- <img src="{$pic}"/> -->
								</div>
							</xsl:if>
							<div class="text chess-text" style="{'padding-top:0'[$first]}">
								<h2>
									<xsl:value-of select="$header"/>
								</h2>
								<xsl:value-of select="text" disable-output-escaping="yes"/>
							</div>
							<xsl:if test="not($odd)">
								<div class="chess-pic" style="background-image: url('{$pic}');">
									<!-- <img src="{$pic}"/> -->
								</div>
							</xsl:if>
						</div>
				</xsl:for-each>
			</div>
		</div>

		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_bottom" />
		</xsl:call-template>

	</xsl:template>
	<!-- END шахматный порядок -->

	<!-- в одну колонку -->
	<xsl:template match="custom_block[type='в одну колонку']">
		
		<xsl:variable name="children" select="custom_block | section | custom_page"/>

		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_top" />
		</xsl:call-template>

		<xsl:variable name="odd_style" select="odd_style"/>

		<div class="{'container'[$need_container]}">
			<div class="block onecol-block pt">

				<xsl:if test="(text | header) != ''">
					<div class="text">
						<h2>
							<xsl:value-of select="header"/>
						</h2>
						<xsl:value-of select="text" disable-output-escaping="yes"/>
					</div>
				</xsl:if>

				<xsl:for-each select="$children">
					<xsl:variable name="name" select="name()"/>
					<xsl:variable name="odd" select="if(f:num($odd_style) = 1) then position() mod 2 = 1 else 1=1"/>
					<xsl:variable name="header" select="if($name != 'section') then header else name"/>
					<xsl:variable name="pic" select="if($name = 'custom_block') then concat(@path, image) else concat(@path, main_pic)"/>
					<xsl:variable name="text" select="if($name = 'custom_block') then text else short"/>
					<xsl:variable name="link">
						<xsl:choose>
							<xsl:when test="$name = 'custom_block'"><xsl:value-of select="link"/></xsl:when>
							<xsl:when test="$name = 'custom_page'"><xsl:value-of select="show_page"/></xsl:when>
							<xsl:when test="$name = 'section'"><xsl:value-of select="show_products"/></xsl:when>
						</xsl:choose>
					</xsl:variable>

					<xsl:call-template name="ONECOL_LINE">
						<xsl:with-param name="odd" select="$odd"/>
						<xsl:with-param name="link" select="$link"/>
						<xsl:with-param name="text" select="$text"/>
						<xsl:with-param name="header" select="$header"/>
						<xsl:with-param name="pic" select="$pic"/>
					</xsl:call-template>
				</xsl:for-each>
			</div>
		</div>

		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_bottom" />
		</xsl:call-template>
	</xsl:template>


	<xsl:template name="ONECOL_LINE">
		<xsl:param name="link"/>
		<xsl:param name="odd"/>
		<xsl:param name="header"/>
		<xsl:param name="text"/>
		<xsl:param name="pic"/>



		<div class="onecol-block__wrap pb" style="{'justify-content: flex-start;'[$odd]}">
			<xsl:if test="$odd">
				<img src="{$pic}" onerror="{$onerror}" class="onecol-img"/>
			</xsl:if>
			<div class="onecol-block__text">
				<h2>
					<xsl:if test="not($link != '')">
						<xsl:value-of select="$header" />
					</xsl:if>
					<xsl:if test="$link != ''">
						<a href="{$link}"><xsl:value-of select="$header" /></a>
					</xsl:if>
				</h2>
				<xsl:value-of select="$text" disable-output-escaping="yes" />
			</div>
			<xsl:if test="not($odd)">
				<img src="{$pic}" class="onecol-img" onerror="{$onerror}" style="margin-left: 15px; margin-rigt:0;" />
			</xsl:if>
		</div>
	</xsl:template>

	<!-- END в одну колонку -->


	<xsl:template match="custom_block[type='в 4 колонки' or type='в 5 колонок']">
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_top" />
		</xsl:call-template>
		<div class="block sections-block ptb">
			<div class="{'container'[$need_container]}">
				<div class="title title_2"><xsl:value-of select="header" /></div>
					<div class="sections-block_wrap{if(type='в 4 колонки') then '-4' else '-5'}">
						<xsl:for-each select="custom_block | section | custom_page">
							
							<xsl:variable name="name" select="name()"/>
							<xsl:variable name="header" select="if($name != 'section') then header else name"/>
							<xsl:variable name="pic" select="if($name = 'custom_block') then concat(@path, image) else concat(@path, main_pic)"/>
							<xsl:variable name="link">
								<xsl:choose>
									<xsl:when test="$name = 'custom_block'"><xsl:value-of select="link"/></xsl:when>
									<xsl:when test="$name = 'custom_page'"><xsl:value-of select="show_page"/></xsl:when>
									<xsl:when test="$name = 'section'"><xsl:value-of select="show_products"/></xsl:when>
								</xsl:choose>
							</xsl:variable>

							<div class="banner-sections" data-aos="flip-left" data-aos-easing="ease-out-cubic" data-aos-duration="2000">
								<div class="banner-sections__image img">
									<img src="{$pic}" onerror="{$onerror}" />
								</div>
								<div class="banner-sections__title"><xsl:value-of select="$header" /></div>
								<a href="{$link}" class="banner-sections__link"></a>
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
			<div class="{'container'[$need_container]}">
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

	<xsl:template match="custom_block[type='Преимущества']">
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_top" />
		</xsl:call-template>
		<div class="block icons-block ptb">
			<div class="{'container'[$need_container]}">
				<div class="title title_2"><xsl:value-of select="header" /></div>
				<div class="icons-block__wrap">
					<xsl:for-each select="custom_block">
						<!-- добавляется ссылка (тег a href=...) перед блоком и закрывается после блока -->
						<xsl:variable name="has_link" select="link and not(link = '')"/>
						<xsl:if test="$has_link"><xsl:text disable-output-escaping="yes">&lt;a href=</xsl:text><xsl:value-of select="link" /><xsl:text disable-output-escaping="yes">&gt;</xsl:text></xsl:if>
						<div class="banner-icons">
							<div class="banner-icons__image"><img src="{@path}{image}" alt="" /></div>
							<div class="banner-icons__title"><xsl:value-of select="header" /></div>
							<div class="banner-icons__text"><xsl:value-of select="text" disable-output-escaping="yes" /></div>
						</div>
						<xsl:if test="$has_link"><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text></xsl:if>
					</xsl:for-each>
				</div>
			</div>
		</div>
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_bottom" />
		</xsl:call-template>
	</xsl:template>

	<!-- carousel 2 -->
	<xsl:template match="custom_block[type='Товары']">
		<div class="block devices-block ptb">
			<div class="{'container'[$need_container]}">
				<div class="title title_2"><xsl:value-of select="header"/></div>
				<div class="devices-block__wrap device-carousel">
					<xsl:for-each select="product">
						<xsl:apply-templates select="." mode="product-table"/>
					</xsl:for-each>
				</div>
				<div class="device-nav"></div>
			</div>
		</div>
	</xsl:template>


	<!-- END carousel-2 -->

	<xsl:template match="custom_block[type='подарки']">
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_top" />
		</xsl:call-template>
		<xsl:if test="f:num(divider_top) = 1">
			<div class="pb"></div>
		</xsl:if>
		<div class="block gifts-block ptb">
			<div class="{'container'[$need_container]}">
				<div class="gifts-block__wrap">
					<xsl:for-each select="custom_block">
						<!-- добавляется ссылка (тег a href=...) перед блоком и закрывается после блока -->
						<xsl:variable name="has_link" select="link and not(link = '')"/>
						<xsl:if test="$has_link"><xsl:text disable-output-escaping="yes">&lt;a href=</xsl:text><xsl:value-of select="link" /><xsl:text disable-output-escaping="yes">&gt;</xsl:text></xsl:if>
						<div class="banner-gift" style="transform: perspective(500px) rotateX(0deg) rotateY(0deg);">
							<div class="banner-gift__image"><img src="{@path}{image}" alt="" /></div>
							<div class="banner-gift__title"><xsl:value-of select="header" /></div>
							<div class="banner-gift__text"><xsl:value-of select="text" disable-output-escaping="yes" /></div>
						</div>
						<xsl:if test="$has_link"><xsl:text disable-output-escaping="yes">&lt;/a&gt;</xsl:text></xsl:if>
					</xsl:for-each>
				</div>
			</div>
		</div>
		<xsl:if test="f:num(divider_bottom) = 1">
			<div class="pb"></div>
		</xsl:if>
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_bottom" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="custom_block[type='яндекс-карта']">
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_top" />
		</xsl:call-template>
		<div class="block contacts-block ptb">
				<div class="{'container'[$need_container]}">
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



	<xsl:template match="custom_block[news]">
		<xsl:call-template name="DIVIDER">
			<xsl:with-param name="need" select="divider_top" />
		</xsl:call-template>
		<div class="block sections-block ptb" data-aos="fade-up">
			<div class="{'container'[$need_container]}">
				<div class="title title_2"><xsl:value-of select="header" /></div>
<!--				<div class="icons-block__wrap">-->
				<div class="info-items__wrap">
					<xsl:for-each select="news/news_item">
						<xsl:variable name="news_pic" select="if(main_pic != '') then concat(@path, main_pic) else concat(../@path, ../main_pic)"/>
						<div class="info-item card">
							<div class="info-item__image img"><img src="{$news_pic}" alt="" style="max-width:100%;" /></div>
							<div class="info-item__info">
								<div class="info-item__date"><xsl:value-of select="date"/></div>
								<a href="{show_news_item}" class="info-item__title"><xsl:value-of select="header"/></a>
								<div class="info-item__text"><xsl:value-of select="short" disable-output-escaping="yes"/></div>
							</div>
							<a href="{show_news_item}" class="info-item__link"></a>
						</div>
					</xsl:for-each>
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
