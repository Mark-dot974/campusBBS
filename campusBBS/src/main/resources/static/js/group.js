function apply(){
    alert("apply")
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