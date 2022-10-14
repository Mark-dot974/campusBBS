//点击评论图标或字事件
function showComments(){
    var showCommentdiv = document.getElementById("reply_comments").style.display;
    if (showCommentdiv == "none"){
        document.getElementById("reply_comments").style.display="block";
    }else{
        document.getElementById("reply_comments").style.display="none";
    }
}
//点击评论收回或展开评论
function showReplyInput1(){
    var showCommentdiv = document.getElementById("reply_input1").style.display;
    if (showCommentdiv == "none"){
        document.getElementById("reply_input1").style.display="block";
    }else{
        document.getElementById("reply_input1").style.display="none";
    }
}
function showReplyInput2(){
    var showCommentdiv = document.getElementById("reply_input2").style.display;
    if (showCommentdiv == "none"){
        document.getElementById("reply_input2").style.display="block";
    }else{
        document.getElementById("reply_input2").style.display="none";
    }
}

function like(btn, entityType, entityId, entityUserId,postId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"postId":postId},
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