<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:f="f:f"
	version="2.0">
	<xsl:import href="common_page_base.xsl" />
	<xsl:output method="xhtml" encoding="UTF-8" media-type="text/html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:strip-space elements="*" />

	<xsl:variable name="main" select="/page/main" />

	<xsl:template match="block_mp_part">
	<section class="banners p-t-default">
		<div class="container">
			<div class="row">
				<div class="col-xs-12">
					<div class="center">
						<h1><xsl:value-of select="header"/></h1>
						<xsl:value-of select="text" disable-output-escaping="yes"/>
					</div>
				</div>
			</div>
			<xsl:for-each select="mp_row">
				<div class="row">
					<xsl:variable name="size" select="12 div count(mp_block)"/>
					<xsl:for-each select="mp_block">
						<div class="col-md-{$size} banner">
							<a class="thumb" href="{link}" style="background: url({@path}{img}); background-position: 50% 50%; -webkit-background-size: cover; background-size: cover;"></a>
							<a href="{link}" class="content">
								<span class="title"><span class="title-content"><xsl:value-of select="name"/></span></span>
								<span class="subtitle">
									<span><xsl:value-of select="text" disable-output-escaping="yes"/></span>
									<span class="more"><button type="button" class="btn btn-primary btn-md">Подробнее</button></span>
								</span>
							</a>
						</div>
					</xsl:for-each>
				</div>
			</xsl:for-each>
		</div>		
	</section>
	</xsl:template>

	<xsl:template match="video_mp_part">
	<section class="banners p-t-default">
		<div class="container">
			<div class="row">
				<div class="col-xs-12">
					<div class="center">
						<h1><xsl:value-of select="header"/></h1>
						<xsl:value-of select="text" disable-output-escaping="yes"/>
					</div>
					<div class="video-container">
						<video autoplay="autoplay" loop="loop" muted="muted">
							<!-- <source src="http://clips.vorwaerts-gmbh.de/big_buck_bunny.webm" type="video/webm"> -->
							<source src="{link}" type="video/mp4"/>
						</video>
						<div></div>
					</div>
				</div>
			</div>
		</div>
	</section>		
	</xsl:template>

	<xsl:template match="news_mp_part">
	<section class="p-t-default">
		<div class="container">
			<div class="row">
				<div class="col-xs-12">
					<div class="center">
						<h1><xsl:value-of select="header"/></h1>
					</div>
				</div>
			</div>
			<div class="row">
				<xsl:for-each select="//page/news_item">
					<div class="col-md-3">
						<a href="{show_news_item}">
							<h4><xsl:value-of select="header"/></h4>
						</a>
						<xsl:value-of select="short" disable-output-escaping="yes"/>
						<span class="date"><xsl:value-of select="date"/></span>
					</div>
				</xsl:for-each>
			</div>
		</div>
	</section>
	</xsl:template>

	<xsl:template match="map_mp_part">
	<section class="map m-t-default">
		<div class="container-fluid">
			<div class="row">
				<xsl:value-of select="code" disable-output-escaping="yes"/>
			</div>
		</div>
	</section>
	</xsl:template>

	<xsl:template match="numbers_mp_part[mp_number]">
	<section class="numbers p-t-default">
		<div class="container">
			<div class="row">
				<div class="col-xs-12">
					<div class="center">
						<h1><xsl:value-of select="header"/></h1>
						<xsl:value-of select="text" disable-output-escaping="yes"/>
					</div>
				</div>
			</div>
			<div class="row">
				<xsl:variable name="size" select="12 div count(mp_number)"/>
				<xsl:for-each select="mp_number">
				<div class="col-md-{$size} digits-container">
					<div class="digits"><xsl:value-of select="number"/></div>
					<p><xsl:value-of select="caption"/></p>
				</div>
				</xsl:for-each>
			</div>
		</div>
	</section>
	</xsl:template>

	<xsl:template match="frame"/>

	<xsl:template name="CONTENT">
		<div class="hero" id="hero-stage">
			<div class="dim" style="z-index: 1;"></div>
			<div class="tagline" style="z-index: 2;">
				<!-- <h1>Санаторий Спутник</h1> -->
				<xsl:value-of select="page/main/frame[1]/text" disable-output-escaping="yes"/>
				<!-- <button type="button" class="btn btn-primary btn-lg">Подробнее о санатории</button> -->
			</div>
			<div class="scroll" style="z-index: 1;">
				<a href="#" onclick="$('html, body').animate({{scrollTop: $('#hero-stage').height() - 80}}, 1000); return false;">
					<i class="fa fa-arrow-circle-down fa-3x"></i>
				</a>
			</div>

			<xsl:for-each select="page/main/frame">
				<div class="hero-slide{if (position() = 1) then ' active' else ''}" style="
						position: absolute;
						z-index: 0;
						width: 100%;
						height: 100%;
						display: block;
						opacity: {if (position() = 1) then '1' else '0'};
						top: 0;
						left: 0;
						right: 0;
						bottom: 0;
						background: url('{@path}{img}') center no-repeat;
						background-size: cover;
						">
									
				</div>
			</xsl:for-each>
			<div class="navigation-dots" id="hero-navigation-dots" style="position: absolute; z-index: 1;">
				<ul>
					<xsl:for-each select="page/main/frame">
						<li><a href="{position() - 1}" class="{if (position() = 1) then 'active' else ''}"></a></li>
					</xsl:for-each>
				</ul>
			</div>
		</div>
		<xsl:apply-templates select="page/main/*[ends-with(name(.), 'mp_part')]"/>
	</xsl:template>

	<xsl:template name="SCRIPTS">
	<script type="text/javascript" src="js/scroll.js"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#hero-navigation-dots").find("a").click(function(e){
				e.preventDefault();
				
				if($(this).is(".active, .locked"))return;
				$("#hero-navigation-dots").find("a").addClass("locked");
				$("#hero-navigation-dots").find("a").removeClass("active");
				$(this).addClass("active");
				var idx = $(this).attr("href");
				
				slide = $(".hero-slide:eq("+idx+")");
				activeSlide = $(".hero-slide.active");
				slide.css({"z-index": 1});
				activeSlide.css({"z-index":0});

				
					slide.animate({opacity : 1}, 1000, function(){
						$("#hero-navigation-dots").find("a").removeClass("locked");
						slide.addClass("active");
						activeSlide.removeClass("active");
						activeSlide.css({opacity:0});
					});
					activeSlide.animate({opacity : 0.75});
			});
			play(4000);
		});
		function play(time){
			strt = new Date().getTime();
			if(typeof playTimeout != "undefined"){
				clearTimeout(playTimeout);
			}
			activeButton = $("#hero-navigation-dots").find("a.active");
			idx = activeButton.index("#hero-navigation-dots a")+1;
			<xsl:text disable-output-escaping="yes">
				idx = (idx &lt; $("#hero-navigation-dots a").length)? idx : 0;
			</xsl:text>
			if($(".navbar-nav").find(".open").length == 0){
				$("#hero-navigation-dots a:eq("+idx+")").trigger("click");
			}
			elapsed = new Date().getTime() - strt;
			playTimeout = setTimeout(function(){play(time)},time - elapsed);
		}
	</script>
	</xsl:template>

</xsl:stylesheet>