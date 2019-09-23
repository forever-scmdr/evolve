var mceSettings = {
     "mce_big" : {
          selector : "textarea.mce_big"
         ,language: "ru"
         ,language_url : '/admin/tinymce_5/langs/ru.js'
         ,content_css : ["css/text-style.css", "font-awesome-4.6.3/css/font-awesome.min.css" ]
     }
    //  ,"mce_medium" : { selector : "textarea.mce_medium"
    //
    //     ,language_url : '/admin/tinymce_5/langs/pl.js'
    // }
    //  ,"mce_small" : {selector : "textarea.mce_small"
    //
    //     ,language_url : '/admin/tinymce_5/langs/pl.js'
    //  }
}
$(document).ready(function () {
    for(var setting in mceSettings){
        tinymce.init(mceSettings[setting]);
    }
});