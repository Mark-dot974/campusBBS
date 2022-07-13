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

function invite(){
    $.post(
        CONTEXT_PATH+"/group/invite",
        $('#inviteForm').serialize(),
        function (data){
            data = $.parseJSON(data);
            if(data.code == 0)
            {
                window.location.reload();
            }
        }
    )
}


$(function(){
    if(sessionStorage.getItem("apply") == "agree") {
        $('#apply').text("已同意");
        $('#hi').html("");
    }
    if(sessionStorage.getItem("apply") == "denied") {
        $('#apply').text("已拒绝");
        $('#hi').html("");
    }

})