// function createGroup(){
//     alert(1);
//     $.post(
//         CONTEXT_PATH + "/group/create",
//         $('#createGroup').serialize(),
//         function (data){
//             if(data.code == 0){
//                 window.location.reload();
//             }else{
//                 layer.msg("该协作圈已存在!")
//             }
//         }
//     )
// }

function updateGroup() {
    $('#myModal').modal();
}

function uploadFile(){
    // alert("happy")
    var form = new FormData($("#files")[0]);
    $.ajax({
        url: "http://up-z2.qiniup.com",
        method: "post",
        processData: false,
        contentType: false,
        data: new FormData($("#files")[0]),
        success: function(data) {
            $.ajax({
                url:CONTEXT_PATH+"/group/uploadFile",
                method: "post",
                processData: false,
                contentType: false,
                data: form,
                success:function(data) {
                    data = $.parseJSON(data);
                    if(data.code == 0) {
                        window.location.reload();
                    } else {
                        alert(data.msg);
                    }
                }
                })
        }
    });
    // 表示不要将表单提交
    return false;
}


function updateFile(){
    $.ajax({
        url: "http://up-z2.qiniup.com",
        method: "post",
        processData: false,
        contentType: false,
        data: new FormData($("#updateFileForm")[0]),
        success: function(data) {
            alert("上传成功！")
            $.ajax({
                url:CONTEXT_PATH+"/group/updateFile",
                method: "post",
                processData: false,
                contentType: false,
                data: new FormData($("#updateFileForm")[0]),
                success:function(data) {
                    data = $.parseJSON(data);
                    if(data.code == 0) {
                        window.location.reload();
                    } else {
                        alert(data.msg);
                    }
                }
            })
        }
    });
    // 表示不要将表单提交
    return false;
}


function apply(){
    // alert("apply")
    sessionStorage.setItem("apply","apply");
    $.post(
        CONTEXT_PATH+"/group/apply",
        $('#applyForm').serialize(),
        function (data){
            data = $.parseJSON(data);
            if(data.code == 0)
            {
                alert("已发送申请，等待圈主同意");
            }
        }
    )
}

function agree(){
    sessionStorage.setItem("apply","agree");
    $.post(
        CONTEXT_PATH+"/group/operate",
        $('#accept').serialize(),
        function (data){
            data = $.parseJSON(data);
            if(data.code == 0)
            {
                window.location.reload();
            }
        }
    )
}

function denied(){
    sessionStorage.setItem("apply","denied");
    $.post(
        CONTEXT_PATH+"/group/operate",
        $('#denied').serialize(),
        function (data){
            data = $.parseJSON(data);
            if(data.code == 0)
            {
                window.location.reload();
            }
        }
    )
}

function inviteMember(){
    alert("invite");
    $.post(
        CONTEXT_PATH+"/group/invite",
        $('#inviteForm').serialize(),
        function (data){
            data = $.parseJSON(data);
            if(data.code == 0)
            {
                window.location.reload();
            }else {
                alert(data.msg);
            }
        }
    )
}


$(function(){
    if(sessionStorage.getItem("apply") == "agree") {
        $('#apply').text("已同意");
        $('#hi').html("");
        sessionStorage.clear();
    }
    if(sessionStorage.getItem("apply") == "denied") {
        $('#denied_text').text("已拒绝");
        $('#hid').html("<div style='width: 80px'></div>");
        sessionStorage.clear();
    }

})

function memberForm(){
    $('#invite').modal();
}