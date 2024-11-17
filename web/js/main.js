$(document).ready(function(){

    $("*[data-millis]").each(function(){
        $t = $(this)

        const minute = 60
        const hour = 60 * minute
        const day = 24 * hour
 
        secs = Math.floor((new Date().getTime() - $t.attr('data-millis'))/1000)

        txt = ''

        if (secs < minute){
            txt = 'Только что'
        }
        else if(secs < hour){
            txt = Math.floor(secs/minute) + ' мин. назад'}
        else if (secs < day){
            h = Math.floor(secs/hour)
            m = Math.floor(secs%hour/minute)
            txt = h + ' час. ' + m + ' мин. назад'
        }
        
        if(txt != ''){
            $t.text(txt)
        }

    });
    
    if (window.innerWidth > 1024) {
        $('.burger').on('click', function(){
            $('.desc_menu_box').toggleClass('active');
            $(this).toggleClass('active');
        });
    } else {
        $('.burger').on('click', function(){
            $(this).toggleClass('active');
            $(".first_screen nav").fadeToggle(500);
            $('header').toggleClass('active');
            $('header .lang_box').toggleClass('active_mob');
        });
    }
    
    $('.rec_slider').slick({
        dots: true,
        arrows: false,
    });

    // $('.first_screen nav ul li.other > a').on('click', function(e){
    //     e.preventDefault();
    //     $('.first_screen nav ul li .more_box').slideToggle(300);
    // });

    const theme = localStorage.getItem('theme');
    if(theme == 'dark'){
        $('header .right .night_mode label').checked = true
        $('body').addClass('dark')
        $('header .right .night_mode label').addClass('active')

    }else{
        $('body').removeClass('dark')
        $('header .right .night_mode label').removeClass('active')
    }

    $('header .right .night_mode label').on('change', function(){
        $(this).toggleClass('active');
        $('body').toggleClass('dark');

        if ($('body').is('.dark')){
            localStorage.setItem('theme', 'dark');

        }else{
            localStorage.setItem('theme', '');
        }
    });

    $('header .lang_box').on('click', function(){
        $(this).toggleClass('active');
    });

    $('.dots_mob').on('click', function(){
        $('.market_box .categ_wrap').toggleClass('active');
    });

    $('.read_also_list .arrow_top').on('click', function(){
        $("html, body").animate({ scrollTop: 0 }, 1000);
    });

    $(document).mouseup(function(e) {
    var container = $(".first_screen nav ul, header, .market_box .categ_wrap ul");

    // if the target of the click isn't the container nor a descendant of the container
    if (!container.is(e.target) && container.has(e.target).length === 0){
        if (window.innerWidth < 1024){
            $(".first_screen nav").fadeOut(500);
        }
        $('header').removeClass('active');
        $('header .lang_box').removeClass('active_mob');
        $('.market_box .categ_wrap').removeClass('active');
    }    


    var form = document.querySelector('header .search_box form');
    var button = form.querySelector('button');

    button.addEventListener('click', function(e) {
        if (form.classList.contains('active')) {
            // Якщо клас 'active' є, форма відправиться при натисканні кнопки
            console.log('Форма відправлена');
        } else {
            // Якщо класу 'active' немає, форма не відправляється, додається клас 'active'
            e.preventDefault();
            form.classList.add('active');
        }
    });

    form.addEventListener('submit', function(e) {
        if (!form.classList.contains('active')) {
            e.preventDefault();
        } else {
            console.log('Форма відправлена');
        }
    });
    

});

});