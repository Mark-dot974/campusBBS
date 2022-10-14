// $(function(){
//     var file = $("#image").val();
//     if(file!=null && file.length>0){
//         upload();
//     }
// });

function upload() {
    $.ajax({
        url: "http://up-z2.qiniup.com",
        method: "post",
        processData: false,
        contentType: false,
        data: new FormData($("#uploadForm")[0]),
        success: function(data) {
                // 将文件路径存到数据库中
               if(data && data.code == 0) {
                   // 更新头像访问路径
                   $.post(
                       CONTEXT_PATH + "/user/updateHeader",
                       {"fileName":$("input[name='key']").val()},
                       function(data) {
                           data = $.parseJSON(data);
                           if(data.code == 0) {
                               window.location.reload();
                           } else {
                               alert(data.msg);
                           }
                       }
                   );
               }
               else {
                   alert("上传失败!");
               }
           }
    });
    // 表示不要将表单提交
    return false;
}

function uploadFile(){
    alert("uploadFile")
    $.ajax({
        url: "http://up-z2.qiniup.com",
        method: "post",
        processData: false,
        contentType: false,
        data: new FormData($('#headerForm')[0]),
        success: function(data) {
            alert(data);
        }
    });
    // 表示不要将表单提交
    return false;
}
