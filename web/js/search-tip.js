/**
 * Created by user on 19.06.2019.
 */
$(document).ready(function () {
    $("#q-ipt").keyup(function(){
        searchAjax(this, '#search-result');
    });
    $("#q-ipt-mobile").keyup(function(){
        searchAjax(this, '#search-result-mobile');
    });
});
function searchAjax(input, output){
    var $el = $(input);
    var $out = $(output);
    <!-- console.log($el); -->
    var val = $el.val();
    if(val.length > 2){

        var $form = $("<form>",{'method' : 'post', 'action' : 'search_ajax', 'id' : 'tmp-form'}
    );

        var $ipt2 = $("<input>",{'type' : 'text', 'value': val, 'name' : 'q'});

        $ipt2.val(val);

        $form.append($ipt2);
        $('body').append($form);
        postForm('tmp-form', 'search-result');
        $('#tmp-form').remove();
        $out.show();
    }
}

$(document).on('click', 'body', function(e){
    var $trg = $(e.target);
    if($trg.closest('#search-result').length > 0 || $trg.is('#search-result') || $trg.is('input') || $trg.is('#search-result-mobile') || $trg.closest('#search-result-mobile').length > 0) return;
    $('#search-result, #search-result-mobile').hide();
    $('#search-result, #search-result-mobile').html('');
});