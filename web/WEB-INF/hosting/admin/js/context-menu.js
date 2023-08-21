/**
 * Created by Anton on 22.05.2017.
 */
(function () {

    "use strict";

    var taskItemClassName = "call-context-menu";
    var activeClassName = "context-menu--active";
    var menuState = 0;
    var menu;
    var el = false;

    var menuPosition;
    var menuPositionX;
    var menuPositionY;

    var clickCoords;
    var clickCoordsX;
    var clickCoordsY;

    var menuWidth;
    var menuHeight;
    var windowWidth;
    var windowHeight;

    function init() {
        contextListener();
        clickListener();
        escListener();
    }

    init();

    //-- Вызов контекстного меню
    function contextListener() {
        document.addEventListener("contextmenu", function (e) {
            el = false;
            el = clickInsideElement(e, taskItemClassName);
            if (el) {
                e.preventDefault();
                menu = null;
                var classList = el.classList;

                if (classList.contains("default")) {
                    menu = document.querySelector("#context_menu-default");

                    if(typeof menu == "undefined") return;

                    hideMenu();
                    toggleMenu();
                    positionMenu(e);
                    addInfoDefault();
                }
                if (classList.contains("active")) {
                    classList.remove("active");
                } else {
                    classList.add("active");
                }
            }
        });
    }

    function addInfoDefault() {
        var editLink = el.getAttribute("data-link");
        var links = $(menu).find(".link");
        links.attr("href", editLink);
    }

    //-- Скрыть по клику
    function clickListener() {
        document.addEventListener("click", function (e) {
            var button = e.which || e.button;
            if (button === 1 && !clickInsideElement(e, "context-menu")) {
                hideMenu();
            }
        });
    }

    function escListener() {
        window.onkeyup = function (e) {
            if (e.keyCode === 27) {
                hideMenu();
            }
        }
    }

    //-- Показать контекстное меню
    function toggleMenu() {
        if (menuState !== 1) {
            menuState = 1;
            menu.classList.add(activeClassName);
        } else if (menuState !== 0) {
            menuState = 0;
            menu.classList.remove(activeClassName);
        }
    }

    function resizeListener() {
        window.onresize = function (e) {
            hideMenu();
        };
    }

    //-- Скрыть
    function hideMenu() {
        if(typeof menu == "undefined") return;
        menuState = 0;
        menu.classList.remove(activeClassName);
        var menuParents = document.querySelector(".call-context-menu.active");
        if (menuParents != null) {
            menuParents.classList.remove("active");
        }
    }

    function positionMenu(e) {
        clickCoords = getPosition(e);
        clickCoordsX = clickCoords.x;
        clickCoordsY = clickCoords.y;

        var doc = document.documentElement;
        // var scrollTop = (window.pageYOffset || doc.scrollTop)  - (doc.clientTop || 0);

        menuWidth = menu.offsetWidth + 4;
        menuHeight = menu.offsetHeight + 4;

        windowWidth = window.innerWidth;
        windowHeight = window.innerHeight;

        if ((windowWidth - clickCoordsX) < menuWidth) {
            menu.style.left = windowWidth - menuWidth + "px";
        } else {
            menu.style.left = clickCoordsX + "px";
        }

        // if ( (windowHeight - clickCoordsY) < menuHeight ) {
        //     menu.style.top = windowHeight - menuHeight + "px";
        // } else {
        //     menu.style.top = clickCoordsY + "px";
        // }
        menu.style.top = clickCoordsY + "px";
    }

    ///////////////////////////////////////
    ///////////////////////////////////////
    //
    // ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ
    //
    ///////////////////////////////////////
    ///////////////////////////////////////

    //--
    function clickInsideElement(e, className) {
        var el = e.srcElement || e.target;

        if (el.classList.contains(className)) {
            return el;
        } else {
            while (el = el.parentNode) {
                if (el.classList && el.classList.contains(className)) {
                    return el;
                }
            }
        }

        return false;
    }

    //-- получить положение курсора на странице
    function getPosition(e) {
        var posx = 0;
        var posy = 0;

        if (!e) var e = window.event;

        if (e.pageX || e.pageY) {
            posx = e.pageX;
            posy = e.pageY;
        } else if (e.clientX || e.clientY) {
            posx = e.clientX + document.body.scrollLeft +
                document.documentElement.scrollLeft;
            posy = e.clientY + document.body.scrollTop +
                document.documentElement.scrollTop;
        }

        return {
            x: posx,
            y: posy
        }
    }


})();

//-- context menu onclick assigments
$(document).on("click", "a[data-action=copy]", function (e) {
    e.preventDefault();
    copyToClipboard($(this).attr("href"));
});

$(document).on("click", "a[data-action=new_window]", function (e) {
    e.preventDefault();
    href = $(this).attr("href");
    window.open(href, "_blank", "width=1024,height=768");
});

function copyToClipboard(txt) {
    id = "div-" + new Date().getTime();
    div = $("<div>", {text: txt, id: id});
    $("body").append(div);
    if (document.selection) {
        var range = document.body.createTextRange();
        range.moveToElementText(document.getElementById(id));
        range.select().createTextRange();
        document.execCommand("Copy");
        alert("Ссылка скопирована в буфер обмена");
    } else if (window.getSelection) {
        var range = document.createRange();
        range.selectNode(document.getElementById(id));
        window.getSelection().addRange(range);
        document.execCommand("Copy");
        alert("Ссылка скопирована в буфер обмена");
    }
    div.remove();
}

