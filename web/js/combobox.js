$(document).on('change', '.combobox select', function function_name(e) {
	ipt = $(this).prev("input").val($(this).val());
})