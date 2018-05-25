/*$(function() {
    $('#yearMonthPicker .date').datepicker({
        format: 'yyyymm',
        language: 'ja',
        autoclose: true,
        minViewMode : 1
    });

});*/
$(document).ready(function() {
    var date = new Date();

    var month = date.getMonth() + 1;
    var year = date.getFullYear();

    if (month < 10) month = "0" + month;

    var today = year + "-" + month;
    $("#theDate").attr("value", today);
});