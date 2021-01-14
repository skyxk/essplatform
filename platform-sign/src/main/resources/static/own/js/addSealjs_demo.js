
var sealType = 0;//判断拖入的是签名还是签章
var name = "";  //签名图片位置
var sealId = "";

var percent =  0.35;//缩放比例

var sss = 1;

var pfx=1;     //签章选项 第一个还是第二个
var up=1;       //选择签章还是签名
var din=1;   //中心定位还是左上角定位
var first=1; //关键字的次序，第一次后者最后一次
var idnum=0;  //签章前台对象的id数字
var json = ""; //后台传来的关于pdf文档的签章信息
var region;  //当前选中的签章热区域
//判断是否是pc还是移动设备
// var cur=null;
function IsPC()
{
    var userAgentInfo = navigator.userAgent;
    var Agents = new Array("Android", "iPhone", "SymbianOS", "Windows Phone", "iPad", "iPod");
    var flag = true;
    for (var v = 0; v < Agents.length; v++) {
        if (userAgentInfo.indexOf(Agents[v]) > 0) { flag = false; break; }
    }
    if(flag){
         $("#eraser").css("display","none")
         $("#eraser2").css("display","none")
         $("#eraser3").css("display","none")
    }
}

//给移动设备设置的删除签章的方法，后续可删除采用更合适的
function remove() {
    if(idnum>0){
        $("#x"+idnum).remove();
        idnum=idnum-1;
        layui.use('layer', function () {
            var layer = layui.layer;
            layer.msg('最后一个签章或签名已删除', {icon: 1});
        });
    }else{
        layui.use('layer', function () {
            var layer = layui.layer;
            layer.msg('所有签章或签名已删除', {icon: 2});
        });
    }
}
//PDF预加载获得已经存在的签章位置和相关验证的信息
function getSeal(){
       $.ajax({
             url:"/sealinfo",
             type:"POST",
             dataType:"text",
             success:function (data) {
                 setregion(data)
             },
             error:function () {
             }
       })
}

// 获得最大的页面根据缩放比例设置所有的页面大小
function setSize() {
    var width = $("#view").css("width")//页面的显示宽度
    var pages = ($("#view").children());// pdf页面的集合

    var arraywidth = new Array();
    maxPage = pages.length;
    for (var i = 1; i <= pages.length; i++) {
        var page = $(pages[i - 1]).children()[0];//第一个元素是图片div剩下的是页码div
        var paperSize = $(page).attr("page");
        arraywidth.push(paperSize.split("_")[0]);
    }
    arraywidth.sort();
    var maxwidth = arraywidth[arraywidth.length - 1];
    // var widthNum = width.split("px")[0] * 0.96;
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
    IsPC();
    $("#chose").css("display", "none");
    $("#seal").css("display", "block");

    $(".names").click(function () {
        //点击选取印章
        //去掉其他选中效果
        $(".names").css("border", "none");
        //添加选中效果
        $(this).css("border", "solid 2px green");
        //取出选中图片
        name = $(this).attr("src");
        sealId = $(this).attr("id");
        //判断选中印章类型
        var imgWidth = $(this).css("width");
        if(imgWidth=="100px"){
            sealType =3;
        }else {
            sealType =1;
        }
    });
    if($(".names")[0]!=null){
        $(".names")[0].click();
    }

    $("#addName").click(function (event) {//防止右边的菜单栏被拖动
        event.preventDefault();
    });

    $("#addName").draggable({     //拖动签名效果
        cursor: "move",
        cursorAt: {
            top: 77.5,
            left: 170
        },
        helper: function (event) {
           if (sealType==3){
               var width = (percent*119).toFixed(1);
               var aa = "<img width='"+width+"' height='"+width+"' src='" + name + "'></img>";
               return $(aa);//拖动时看到的图像
            }else{
               var width = (percent*60).toFixed(1);
               var height = (percent*25).toFixed(1);
                return $("<img width='"+width+"' height='"+height+"' src='" + name + "'></img>");//拖动时看到的图像
            }
        }
    });

    $(".pic").droppable({      //拖动的目标接收操作
        accept: ".list-group-item",
        drop: function (event, ui) {
            var top = ui.offset.top;     //确定放入的坐标
            var left = ui.offset.left;
            var offsetx;
            var offsety;
            offsetx = $(event.target).offset().left;
            offsety = $(event.target).offset().top;
            top = top - offsety;
            left = left - offsetx;
            var picW = $(this).css("width").split("px")[0];
            var picH = $(this).css("height").split("px")[0];
            var width;
            var height;
            if (sealType==3){
                 width = (percent*119).toFixed(1);
                 height = (percent*119).toFixed(1);
            }else{
                 width = (percent*60).toFixed(1);
                 height = (percent*25).toFixed(1);
            }
            if ((Number(left)+Number(width))>picW){
                left = Number(picW)-Number(width);
            }
            if ((Number(top)+Number(height))>picH){
                top = Number(picH)-Number(height);
            }
            if(top<0){
                top =0;
            }
            if (left<0){
                left = 0
            }
            event.target.appendChild(addNewNode());//添加新节点
            $(event.target.lastChild).css({
                "position": "absolute",
                "top": top,
                "left": left,
                "cursor": "move"
            });
            $.getScript("/sign/own/js/addSealjs2.js"); //添加新的js实现对加入页面的相关操作
       }
    });
    setSize(); //设置pdf显示的大小

    $("div.pic").lazyload();

    //getSeal();//获得已经存在的签章 并设置显示在页面中
    var height = $(window).height();
    $("#center").css("height", height - 2 + "px"); //设置中间区域高度

    $("#loPageNum").change(function () {  //坐标定位的时候输入页码 调到对应的页码
        scrollPage();
    });

    $("#x").change(function () {  //x辅助线
        addline("X");
    });

    $("#y").change(function () {//y辅助线
        addline("Y");
    });

    $("#wordbutton").click(function () {
        key();  //关键字搜索
    });

    $(".btn-danger").click(function () {
        add2file($("#documentCode").val());   //确认盖章
    });

    $(".pfx").click(function () {//切换证书
        pfx = $(this).val();
    });

    $(".up").click(function () {//切换签名或者签章图片
        up = $(this).val();
    });

    $(".din").click(function () {//切换定位方式
        din = $(this).val();
    });

    $(".first").click(function () {//切换关键字的次序
        first = $(this).val();
    });

    // $(".ele").draggable({containment: "parent"});
    // //双击对象删除
    // $(".ele").dblclick(function () {
    //     $(this).remove();
    //     //清空当前对象
    //     cur=null;
    // });

});

function getPDFXY(dot) {
    return (1588*percent*dot)/595
}
//添加新节点 用于前台盖章的显示效果
function addNewNode() {
    idnum=idnum+1;
    var nNode;
    nNode = document.createElement("img");
    $(nNode).attr("src",name);
    var width = 100;
    var height = 100;
    if (sealType == 1) {//签名大小的div
         width = (percent*60).toFixed(1);
         height = (percent*25).toFixed(1);

    } else if (sealType == 3) {//签章大小的div
         width = (percent*119).toFixed(1);
         height = (percent*119).toFixed(1);
    }
    $(nNode).css({
        "width": width+"px",
        "height": height+"px",
        "background-size": "cover"
    });
    nNode.setAttribute("type", "nam");
    nNode.setAttribute("class", "ele");
    nNode.setAttribute("sealId", sealId);
    nNode.setAttribute('id','x'+idnum);
    return nNode;
}

//后台真正的盖章信号
function add2file(documentCode) {
    var arrays = new Array()

    let result = true;
    //循环每个页面
    for (var i = 1; i <= maxPage; i++) {
        //循环每个签章对象的标签
        for (var j = 1; j <= $("#" + i).find(".ele").length; j++) {
            var ele= $($("#" + i).find(".ele")[j - 1]);
            //根据缩放比例算出实际宽度
            var width =ele.css("width").split("px")[0];
            var sealId = ele.attr("sealid");
            var Type ;
            //算出实际的x坐标
            var x = 0;
            var y = 0;
            var top = ele.css("top").split("px")[0];
            var sealWidth = (percent*119).toFixed(1);
            if((width-sealWidth)<1&&(width-sealWidth)>-1){
                var sealHeight = (percent*119).toFixed(1);
                top = Number(top) + Number(sealHeight);
                x = ele.css("left").split("px")[0]/(percent);
                y = 841 - top/(percent);
                Type =3;
            }else {
                var sealHeight = (percent*25).toFixed(1);
                top = Number(top) + Number(sealHeight);
                x = (ele.css("left").split("px")[0])/(percent);
                y = 841 - top/(percent);
                Type =1;
            }
            x = x.toFixed(1);
            y = y.toFixed(1);
            //UK签章例子
            $.ajax({
                url: "http://127.0.0.1:9980/ESSSign/SetDocInfo",
                type: "get",
                data: {"DOCID" : documentCode,"RANDOM": randomFrom()+"ESSEND"},
                success: function (data) {
                    if (data.indexOf("ERROR") != -1) {
                        const obj = eval('('+ data +')');
                        alert(obj.ERROR);
                    } else {
                        const obj = eval('('+ data +')');
                        const CERT = obj.CERT;
                        if (CERT)
                        //生成数组
                        var s={"page": i, "sealId": sealId, "x": x, "y": y, "Type": Type,"CERT":CERT};
                        arrays.push(s);
                        var jsonString = JSON.stringify(arrays);
                        $.ajax({
                            url:"/sign/pdf/sign_demo",
                            data:{"data": jsonString},
                            type:"post",
                            success:function (data) {
                                alert("签章完成！")
                                $("#chose").css("display","none");
                                $("#seal").css("display","none");
                                $("#download").css("display","block");
                                layui.use('layer', function () {
                                    var layer = layui.layer;
                                    layer.msg('盖章成功', {icon: 1});
                                });
                            },
                            error:function () {
                                layui.use('layer', function () {
                                    var layer = layui.layer;
                                    layer.msg('盖章发生错误', {icon: 5});
                                });
                            }
                        });
                    }
                },
                error:function (data) {
                    alert("客户端连接错误！")
                }
            });
        }
    }
}

function js_getDPI() {
    var arrDPI = new Array;
    if (window.screen.deviceXDPI) {
        arrDPI[0] = window.screen.deviceXDPI;
        arrDPI[1] = window.screen.deviceYDPI;
    } else {
        var tmpNode = document.createElement("DIV");
        tmpNode.style.cssText = "width:1in;height:1in;position:absolute;left:0px;top:0px;z-index:99;visibility:hidden";
        document.body.appendChild(tmpNode);
        arrDPI[0] = parseInt(tmpNode.offsetWidth);
        arrDPI[1] = parseInt(tmpNode.offsetHeight);
        tmpNode.parentNode.removeChild(tmpNode);
    }
    return arrDPI;
}
function randomFrom() {const d = new Date();return d.getTime();}
//根据获得的信息在页面上设置相关的热区域，实现验章功能
function setregion(s) {
    s = JSON.parse(s); //创建json对象
    json = s;
    for (var x = 0; x < s.length; x++) { //遍历json中的章的信息组，每组代表每个章
        var page = s[x]["page"];         //确定页码数字
        var node = document.createElement("div");//创造div 制造鼠标热区域
        node.setAttribute("class", "regions");
        node.setAttribute("regionid", s[x]["SealFieldName"]); //设置相关属性
        //已经存在的签章右键菜单目录
        $(node).contextmenu(function (event) {
            event.preventDefault();
            var myMenu = document.getElementById("my");
            myMenu.style.display = "block";
            //获取鼠标视口位置
            myMenu.style.top = event.clientY + "px";
            myMenu.style.left = event.clientX + "px";
            region = this; //更新当前右键点击对象
        })
        var regid = s[x]["SealFieldName"];
        var thJson="";
        var content="";
        for (var sx = 0; sx < json.length; sx++) { //找到这个章的相关信息
            if (regid == json[sx]["SealFieldName"]) {
                thJson = json[sx];
                console.log(thJson)
                if (thJson["vertify"]) {  //验证签章有效的话
                    if (!thJson["coversWhole"]) { //不包含整个文档
                        var xz = true;              //后面的章是否有无效的
                        var isenter=false;  //后面是否有章了
                        for (var sy = sx + 1; sy < json.length; sy++) {
                            isenter=true;
                            if (json[sy]["vertify"]) {
                                continue;
                            } else {
                                x = false;
                                break;
                            }
                        }

                        if (isenter){
                            if (xz) {
                                content = "<div style='margin-left: 10px;font-family: 微软雅黑;font-size: large'>签名有效，由‘" + thJson['SealName'] + "’签名</div></br><div style='margin-left: 40px;font-family: 微软雅黑;font-size: large'>-自应用本签名以来，文档未被修改</div>";
                            }
                        }else {
                            content = "<div style='margin-left: 10px;font-family: 微软雅黑;font-size: large'>签名有效，由‘" + thJson['SealName'] + "’签名</div></br><div style='margin-left: 40px;font-family: 微软雅黑;font-size: large'>-签名后产生了签名允许的修改</div></br><div style='margin-left: 40px;font-family: 微软雅黑;font-size: large'>-签名允许填写表单，签名，批注</div>";
                        }
                    } else {
                        content = "<div style='margin-left: 10px;font-family: 微软雅黑;font-size: large'>签名有效，由‘" + thJson['SealName'] + "’签名</div></br><div style='margin-left: 40px;font-family: 微软雅黑;font-size: large'>-自应用本签名以来，文档未被修改</div>";
                    }
                } else {
                    content = "<div style='margin-left: 10px;font-family: 微软雅黑;font-size: large'>验证签名发生错误</div></br><div style='margin-left: 40px;font-family: 微软雅黑;font-size: large'>-签名包含不正确的，无法识别的，已经损坏或者可疑的数据</div>"
                }

            }
        }
        $(node).click(function (e) {  //点击实现验章弹出层
            layui.use('layer', function () {
                layer.open({
                    type: 1 //Page层类型
                    , area: ['500px', '300px']
                    , title: '签章验证'
                    , shade: 0.6 //遮罩透明度
                    , maxmin: false //允许全屏最小化
                    , anim: 1 //0-6的动画形式，-1不开启
                    , content: content
                });
            });

        })

        var left = s[x]["left"] * percent;
        var bottom = s[x]["bottom"] * percent;
        var width = s[x]["widht"] * percent;
        var height = s[x]["height"] * percent;
        $(node).css({
            "width": width + "px",
            "height": height + "px",
            "cursor": "pointer",
            "margin-left": left + "px",
            "margin-top": $("#" + page).css("height").split("px")[0] - bottom - height + "px",
            "position": "absolute"
        })
        //把热区域透明div放入页面中
        document.getElementById(page).appendChild(node)
    }
}


//选择拖拽盖章
function tuoZhuai() {
    $("#chose").css("display", "none");
    $("#seal").css("display", "block");
}
// //选择坐标盖章
// function zuobiao() {
//     $("#chose").css("display", "none");
//     $("#location").css("display", "block");
// }
//选择关键字盖章
function word() {
    $("#chose").css("display", "none");
    $("#word").css("display", "block");
}
//坐标盖章滚动到当前选中的坐标合适位置 Y轴方向
function scrollPage() {
    $(".xline").remove();
    $(".yline").remove();
    var num = $("#loPageNum").val();
    var flag = true;
    if (maxPage < num || num < 1) {
        flag = false;
        layui.use('layer', function () {
            var layer = layui.layer;
            layer.msg('页面数错误', {icon: 5});
        });
    }

    if (flag) { //页码正常的时候滚动页面，否则不滚动
        var dis = $("#" + num).offset().top;
        $("#center").scrollTop($("#center").scrollTop() + dis - 90);
    }


}

var poX = 0;//
var poY = 0;

//坐标盖章增加辅助线
function addline(type) {

    var num = $("#loPageNum").val();
    if (num < maxPage + 1 && num > 0) {
        var line = document.createElement("div");
        if (type == "X") {
            var x = $("#x").val();
            x = x * percent;
            poX = x;
            var height = $("#" + num).css("height");
            line.setAttribute("class", "xline");
            $(line).css({
                "width": "1px",
                "height": height,
                "border-left": "dashed 1px red",
                "position": "absolute",
                "left": x + "px"
            })
            $(".xline").remove();
        } else {
            var y = $("#y").val();
            y = y * percent;
            y = $("#" + num).css("height").split("px")[0] - y;
            var dis=$("#" + num).offset().top+y;
            $("#center").scrollTop($("#center").scrollTop() + dis-200);
            poY = y;
            line.setAttribute("class", "yline");
            $(line).css({
                "width": "100%",
                "height": "1px",
                "border-top": "dashed 1px blue",
                "position": "absolute",
                "top": y + "px"
            })
            $(".yline").remove();
        }
        document.getElementById(num).appendChild(line);
    }

}
//待签对象放入页面
function insert() {
        var num = $("#loPageNum").val();
        if (num < maxPage + 1 && num > 0) {
            if (up==1) {//签名对象
                var a = document.createElement("div");
                a.setAttribute("class", "ele");
                a.setAttribute("pfx", pfx);
                a.setAttribute("type","nam");
                $(a).css({
                    "width": "340px",
                    "height": "155px",
                    "background-image": "url('" + name + "')",
                    "background-size": "cover",
                    "position": "absolute",
                    "top": poY - 77.5,
                    "left": poX - 140
                })
                if(din==2){//左上角定位
                    $(a).css({ "top": poY ,
                        "left": poX })
                }
                document.getElementById(num).appendChild(a);
            }
            if (up==2) {//签章对象
                var a = document.createElement("div");
                a.setAttribute("class", "ele");
                a.setAttribute("pfx", pfx);
                $(a).css({
                    "width": "159px",
                    "height": "159px",
                    "background-image": "url('" + seal + "')",
                    "background-size": "cover",
                    "position": "absolute",
                    "top": poY - 79.5,
                    "left": poX - 79.5
                })
                if(din==2){//左上角定位
                    $(a).css({ "top": poY ,
                        "left": poX })
                }
                document.getElementById(num).appendChild(a);//放入页面
            }
            $.getScript("/sign/own/js/addSealjs2.js");//加载签章相关的js

        }

}
//关键字搜索
function key() {
    var key = $("#key").val();
    if(key==""){
        layui.use('layer', function () {
            var layer = layui.layer;
            layer.msg('关键字为空！', {icon: 5});
        });
        return;
    }
    var flag=true;
    if(first==2){
        flag=false;
    }
    $.ajax({
        url: "/keyword",
        type: "post",
        data: {"keyword": key,"order":flag},
        datType: "text",
        success: function (data) {
            if (data != "") {
                wordAddtoPage(data);
            } else {
                layui.use('layer', function () {
                    var layer = layui.layer;
                    layer.msg('未搜索到关键字!', {icon: 5});
                });
            }
        },
        error: function () {
            alert("ajax错误");
        }
    });
}
//根据返回的关键字信息 给页面加入待签对象
function wordAddtoPage(data) {

    var width = data.width * 0.5;
    if (up==1) {
        var left = data.x * percent - 170 + width;//170为340的一半，340为签名图片的长度
        var top = $("#" + data.pageNum).css("height").split("px")[0] - (data.y * percent + 77.5);
         if(din==2){
             left = data.x * percent + width;
             top = $("#" + data.pageNum).css("height").split("px")[0] - (data.y * percent);
         }
        var dis = $("#" + data.pageNum).offset().top + top;
        $("#center").scrollTop($("#center").scrollTop() + dis - 90);

        var node = document.createElement("div");
        node.setAttribute("class", "ele");
        node.setAttribute("pfx", pfx);
        node.setAttribute("type", "nam");
        $(node).css({
            "width": "340px",
            "height": "155px",
            "background-image": "url('" + name + "')",
            "position": "absolute",
            "top": top,
            "left": left,
            "background-size": "cover",
            "cursor": "move"
        });
        document.getElementById(data.pageNum).appendChild(node);

    }
    if (up==2) {
        var left = data.x * percent - 79.5 + width;
        var top = $("#" + data.pageNum).css("height").split("px")[0] - data.y * percent - 79.5;
        var dis = $("#" + data.pageNum).offset().top + top;
        if(din==2){
            var left = data.x * percent+ width;
            top = $("#" + data.pageNum).css("height").split("px")[0] - data.y * percent;
        }

        $("#center").scrollTop($("#center").scrollTop() + dis - 90);

        var node2 = document.createElement("div");
        node2.setAttribute("class", "ele");
        node2.setAttribute("pfx", pfx);
        $(node2).css({
            "width": "159px",
            "height": "159px",
            "background-image": "url('" + seal + "')",
            "background-size": "cover",
            "position": "absolute",
            "top": top,
            "left": left,
            "cursor": "move"
        });
        document.getElementById(data.pageNum).appendChild(node2);
    }
    // $.getScript("/own/js/addSealjs2.js");
}


//右键验证签名的时候关联点击动作
function  trig() {
    $(region).trigger("click");
    $("#my").css("display", "none");
}
//查看签章时版本
function version(){
    layui.use('layer', function () {
        var layer = layui.layer;
        var index = layer.load(1, {
            shade: [0.1,'#fff'] //0.1透明度的白色背景
        });
    var id = $(region).attr("regionid");
    $.ajax({
        url: "/version",
        type: "POST",
        dataType:"text",
        data:{"id":id},
        success:function(data){
            layer.close(index);
            viewversion(data);
        },
        error:function(){

        }
    }); });
}
//根据签章版本返回的信息，展示当时的版本
function viewversion(info){

    layui.use('layer', function () {
        var layer = layui.layer;
        var index = layer.open({
            type: 2,
            content: 'http://localhost:8080/viewversion',
            area: ['1200px', '600px'],
            maxmin: false
        });
    });

}