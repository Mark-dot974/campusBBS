var CONTEXT_PATH = "";
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
    $.get(CONTEXT_PATH+"/message_center_aside",function (data) {
        $("#message_center_aside").html(data);
    });
});