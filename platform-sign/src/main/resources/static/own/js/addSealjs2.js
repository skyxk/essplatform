//当前的待签章的对象
var cur=null;

$(function () {
    $(".ele").draggable({containment: "parent"});
    //双击对象删除
    $(".ele").dblclick(function () {
        $(this).remove();
        //清空当前对象
        cur=null;
    });
//
//     //点击缩放
//     $(".ele").click(function () {
//          cur=this;
//          //点击置底，为了盖章置顶
//         if (this.parentNode.lastChild != this) {
//             $(this).insertAfter(this.parentNode.lastChild);
//         }
//             if ($(this).attr("type") == "nam") {
//             //签名按照这个比例缩放
//                 $(this).resizable({aspectRatio: 340/155});
//             } else {
//             //签章按照这个比例
//                 $(this).resizable({aspectRatio: 1/1});
//             }
// /*
//         */
//     });
//     //单击页面别的地方缩放功能失效
//     $(".pic").dblclick(function () {
//         $(cur).resizable("disable");
//     });
//  //点击签章对象实现缩放的小黑点
//     $(".ele").click(function () {
//         $(cur).resizable("enable");
//     });
//
});