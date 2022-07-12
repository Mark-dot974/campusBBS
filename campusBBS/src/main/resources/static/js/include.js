var CONTEXT_PATH = "/campusBBS";
$(function () {
    $.get(CONTEXT_PATH+"/header",function (data) {
        $("#header").html(data);
    });
    $.get(CONTEXT_PATH+"/footer",function (data) {
        $("#footer").html(data);
    });
    $.get(CONTEXT_PATH+"/aside",function (data) {
        $("#aside").html(data);
    });
    $.get(CONTEXT_PATH+"/aside_left",function (data) {
        $("#aside_left").html(data);
    });
});