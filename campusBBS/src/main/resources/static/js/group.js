function uploadFile(){
    alert("happy")
    $.ajax({
        url: "http://up-z2.qiniup.com",
        method: "post",
        processData: false,
        contentType: false,
        data: new FormData($("#files")[0]),
        success: function(data) {
            $.post(
                CONTEXT_PATH+"/group/uploadFile",
                {"fileName":$("input[name='key']").val(),"gid":$("input[name='gid']").val()},
                function(data) {
                    data = $.parseJSON(data);
                    if(data.code == 0) {
                        window.location.reload();
                    } else {
                        alert(data.msg);
                    }
                })
        }
    });
    // 表示不要将表单提交
    return false;
}


function apply(){
    alert("apply")
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