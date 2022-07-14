//点击评论图标或字事件
function showComments(){
    var showCommentdiv = document.getElementById("reply_comments").style.display;
    if (showCommentdiv == "none"){
        document.getElementById("reply_comments").style.display="block";
    }else{
        document.getElementById("reply_comments").style.display="none";
    }
}
function showComment_reply(){
    var showCommentdiv = document.getElementById("Comment_reply").style.display;
    if (showCommentdiv == "none"){
        document.getElementById("Comment_reply").style.display="block";
    }else{
        document.getElementById("Comment_reply").style.display="none";
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
//点赞样式变换
//
// var like = document.getElementsByClassName("like")[0];
// var like1 = document.getElementsByClassName("like")[1];
// var like2 = document.getElementsByClassName("like")[2];
// var like3 = document.getElementsByClassName("like")[3];
// var like4 = document.getElementsByClassName("like")[4];
// like.onclick=changecolor1();
// like1.onclick=changecolor1();
// like2.onclick=changecolor2();
// like3.onclick=changecolor3();
// like4.onclick=changecolor4();
// var i=0;
// // function changecolor1() {
// //     i++;
// //     if (i % 2 == 0) {
// //         document.getElementsByClassName("like")[0].setAttribute('class','like active');
// //         document.getElementsByClassName("like")[1].setAttribute('class','like active');
// //     } else {
// //         document.getElementsByClassName("like")[0].setAttribute('class','like');
// //         document.getElementsByClassName("like")[1].setAttribute('class','like');
// //     }
// // }
// var a=0;
// function changecolor2() {
//     a++;
//     if (a % 2 == 0) {
//         // 点赞
//         document.getElementsByClassName("like")[2].setAttribute('class','like active');
//     } else {
//         // 取消点赞
//         document.getElementsByClassName("like")[2].setAttribute('class','like');
//     }
// }
// var b=0;
// function changecolor3() {
//     b++;
//     if (b % 2 == 0) {
//         document.getElementsByClassName("like")[3].setAttribute('class','like active');
//     } else {
//         document.getElementsByClassName("like")[3].setAttribute('class','like');
//     }
// }
// var c=0;
// function changecolor4() {
//     c++;
//     if (c % 2 == 0) {
//         document.getElementsByClassName("like")[4].setAttribute('class','like active');
//     } else {
//         document.getElementsByClassName("like")[4].setAttribute('class','like');
//     }
// }

function like(btn, entityType, entityId, entityUserId,postId) {
    alert("like");
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