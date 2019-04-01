/**
 * Created by user on 22.03.2019.
 */
(function ($) {
    $.fn.truncate = function (options) {

        var regTag = /<(\w+)(\s+[\w+](=".*?")?)*\/?>|<\/(\w+)>/gm;


        var defaults = {
            length: 300,
            minTrail: 20,
            moreText: "читать полностью",
            lessText: "спрятать",
            ellipsisText: "..."
        };

        var options = $.extend(defaults, options);

        return this.each(function () {
// элемент DOM текущей итерации
            obj = $(this);
// извлекаем содержимое элемента в виде HTML разметки
            var body = obj.html();
            var match;
            var i = 0;
            var hasHtml = false;
            var matches = new Array();
            var textLength = 0;


            while (match = regTag.exec(body)) {
                matches[i] = match;
                if (match[0].indexOf("</") == 0) {
                    hasHtml = true;
                    var currentTag = match[4];
                    var previousMatch = matches[i - 1];

                    var fragmentLength = match.index - previousMatch.index - previousMatch[0].length;
                    textLength += fragmentLength;
                    var fragmentStart = (previousMatch.index + previousMatch[0].length);
                    var fragmentEnd = fragmentStart + fragmentLength;

                    if (textLength >= options.length) {
                        var delta = textLength - options.length;
                        splitLocation = fragmentStart + fragmentLength - delta;
                        truncateHtml(splitLocation, currentTag);
                        break;
                    }
                }
                i++;
                if(i>100) break;
            }


            if (body.length > options.length + options.minTrail && !hasHtml) {

// возвращаем позицию, после числа (options.length), с которой начинается совпадение
// в нашем случае это пробел
                var splitLocation = body.indexOf(' ', options.length);
// если совпадение найденно то
                if (splitLocation != -1) {
                    var splitLocation = body.indexOf(' ', options.length);
                    truncateText(splitLocation);
                }
            } // end if

            function truncateText(splitLocation) {
                var str1 = body.substring(0, splitLocation);
                var str2 = body.substring(splitLocation, body.length - 1);
                obj.html(str1 + '<span class="truncate_ellipsis">' + options.ellipsisText +
                    '</span>' + '<span  class="truncate_more">' + str2 + '</span>');
                obj.find('.truncate_more').css("display", "none");

// вставляем ссылку "читать полностью" в конец сцществующего содержимого
                obj.append(
                    '<div class="clearboth">' +
                    '<a href="#" class="truncate_more_link">' + options.moreText + '</a>' +
                    '</div>'
                );

//устанавливаем событие onclick для ссылки "читать полностью"/"спрятать"
                var moreLink = $('.truncate_more_link', obj);
                var moreContent = $('.truncate_more', obj);
//дополнительный текст за текстом, например "..."
                var ellipsis = $('.truncate_ellipsis', obj);
                moreLink.click(function () {
                    if (moreLink.text() == options.moreText) {
                        moreContent.show('normal');
                        moreLink.text(options.lessText);
                        ellipsis.css("display", "none");
                    } else {
                        moreContent.hide('normal');
                        moreLink.text(options.moreText);
                        ellipsis.css("display", "inline");
                    }
                    return false;
                });
            }

            function truncateHtml(splitLocation, tag) {

                var before = body.substring(0, splitLocation);
                var after = body.substring(splitLocation);
                var truncateShow = "<span class=\"truncate_ellipsis\">"+ options.ellipsisText+"</span>";
                var splitStart = splitLocation;
                var splitter = truncateShow+"</"+tag+"><div style=\"display: none;\"><"+tag+">'";
                obj.html(before+splitter+after+"</div>");
                var splitEnd =  obj.html().length -6;
                var oldEnd;
                obj.append(
                    '<div class="clearboth">' +
                    '<a href="#" class="truncate_more_link">' + options.moreText + '</a>' +
                    '</div>'
                );

                var moreLink = $('.truncate_more_link', obj);
                var ellipsis = $('.truncate_ellipsis', obj);
                moreLink.click(function (){showTruncated(); return false;});
                function hideTruncated() {
                    var html = obj.html();
                    var before = html.substring(0, splitStart);
                    var after1 = html.substring(splitStart, oldEnd);
                    console.log(oldEnd);
                    var after2 = html.substring(oldEnd);
                   // console.log(after2);
                    obj.html(before+splitter+after1+"</div>"+after2);
                    moreLink = $('.truncate_more_link', obj);
                    moreLink.text(options.moreText);
                    moreLink.click(function (){showTruncated(); return false;});
                }
                function showTruncated() {
                    var html = obj.html();
                    var before = html.substring(0, splitStart);
                    var after1 = html.substring(splitStart+splitter.length, splitEnd);
                    var after2 = html.substring(splitEnd+6);
                    obj.html(before+after1+after2);
                    oldEnd = splitStart+after1.length;
                    moreLink = $('.truncate_more_link', obj);
                    moreLink.text(options.lessText);
                    moreLink.click(function (){hideTruncated(); return false;});
                }
            }

        });


    };
})(jQuery);