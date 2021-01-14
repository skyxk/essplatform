
var sealType = 0;//判断拖入的是签名还是签章
var name = "";  //签名图片位置
var sealId = "";

var percent =  0.35;//缩放比例

var pfx=1;     //签章选项 第一个还是第二个
var up=1;       //选择签章还是签名
var din=1;   //中心定位还是左上角定位
var first=1; //关键字的次序，第一次后者最后一次
var idnum=0;  //签章前台对象的id数字
var json = ""; //后台传来的关于pdf文档的签章信息
var region;  //当前选中的签章热区域
//判断是否是pc还是移动设备

// 获得最大的页面根据缩放比例设置所有的页面大小
function setSize() {
    var arraywidth = new Array();
    var width = $("#view").css("width")//页面的显示宽度
    var pages = ($("#view").children());// pdf页面的集合
    maxPage = pages.length;
    for (var i = 1; i <= pages.length; i++) {
        var page = $(pages[i - 1]).children()[0];//第一个元素是图片div剩下的是页码div
        var paperSize = $(page).attr("page");
        arraywidth.push(paperSize.split("_")[0]);
    }
    arraywidth.sort();
    var maxwidth = arraywidth[arraywidth.length - 1];
    var widthNum = width.split("px")[0] * 0.96;
    var widthNum = width.split("px")[0] * 0.96;
    percent = widthNum / maxwidth;
    percent = percent.toFixed(1);

    for (var i = 1; i <= pages.length; i++) {
        var page = $(pages[i - 1]).children()[0];
        var paperSize = $(page).attr("page");
        var w = paperSize.split("_")[0] * percent ;
        var h = paperSize.split("_")[1] * percent ;
        $(page).css({
            "width": w + "px",
            "height": h + "px"
        });
    }
}
$(document).ready(function () {
    setSize(); //设置pdf显示的大小
    $("div.pic").lazyload();
    //getSeal();//获得已经存在的签章 并设置显示在页面中
    var height = $(window).height();
    $("#center").css("height", height - 2 + "px"); //设置中间区域高度
});
