// $(function(){
//     var file = $("#image").val();
//     if(file!=null && file.length>0){
//         upload();
//     }
// });

function upload() {
    alert("ajax")
    $.ajax({
        url: "http://upload-z2.qiniup.com",
        method: "post",
        processData: false,
        contentType: false,
        data: new FormData($("#uploadForm")[0]),
        success: function(data) {
           if(data && data.code == 0){
                // 将文件路径存到数据库中
               $.post(
                   CONTEXT_PATH+"/"
               )
           }
        }
    });
    // 表示不要将表单提交
    return false;

    // if(data && data.code == 0) {
    //                 // 更新头像访问路径
    //                 $.post(
    //                     CONTEXT_PATH + "/user/updateHeader",
    //                     {"fileName":$("input[name='key']").val()},
    //                     function(data) {
    //                         data = $.parseJSON(data);
    //                         if(data.code == 0) {
    //                             window.location.reload();
    //                         } else {
    //                             alert(data.msg);
    //                         }
    //                     }
    //                 );
    //             } else {
    //                 alert("上传失败!");
    //             }
}