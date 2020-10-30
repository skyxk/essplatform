var lastTime = new Date().getTime();
var currentTime = new Date().getTime();
var timeOut = 7 * 60 * 1000; //设置超时时间： 7分

$(function () {
    /* 鼠标移动事件 */
    $(document).mouseover(function () {
        lastTime = new Date().getTime(); //更新操作时间
    });

    /* 键盘移动事件 */
    $(document).keydown(function () {
        lastTime = new Date().getTime(); //更新操作时间
    });

    /* 鼠标点击事件 */
    $(document).mousedown(function () {
        lastTime = new Date().getTime(); //更新操作时间
    });

});

function testTime() {
    currentTime = new Date().getTime(); //更新当前时间
    if (currentTime - lastTime > timeOut) { //判断是否超时
        top.location.href = "/EssSealPlatform/unit/logout";
    }
}

/* 定时器  间隔1秒检测是否长时间未操作页面  */
window.setInterval(testTime, 1000);



/**
 * 根据选项获取授权值
 * @param data
 */
function getTheCheckBoxValue() {
    var test = $("input[name='sProblem']:checked");
    var checkBoxValue = "";
    var iAuth = 0;
    test.each(function () {
        var value = $(this).val();
        if(value.indexOf("office") != -1 && value.indexOf("annotation") != -1 )
        {
            iAuth = iAuth | 1;
        }else if(value.indexOf("web") != -1 && value.indexOf("annotation") != -1 ){
            iAuth = iAuth | 2;
        }else if(value.indexOf("ESSWebSign") != -1){
            iAuth = iAuth | 4;
        }else if(value.indexOf("ESSWordSign") != -1){
            iAuth = iAuth | 8;
        }else if(value.indexOf("ESSExcelSign") != -1){
            iAuth = iAuth | 16;
        }else if(value.indexOf("ESSPdfSign") != -1){
            iAuth = iAuth | 32;
        }else if(value.indexOf("ESSMidWare") != -1){
            iAuth = iAuth | 64;
        }else{

        }
    });
    return iAuth;
}

/**
 * 检查输入值是否为空
 * @param data
 * @returns {boolean}
 */
function isNull(val) {
    if (val == '' || val == undefined || val == null) {
        //空
        return true;
    } else {
        // 非空
        return false;
    }
}

function GetProductInfoFromAuthNumber(iAuth){
    var sRet = "";
    if((iAuth & 1) != 0){
        sRet = sRet + "office annotation@" ;
    }
    if((iAuth & 2) != 0){
        sRet = sRet + "web annotation@" ;
    }
    if((iAuth & 4) != 0){
        sRet = sRet + "ESSWebSign@" ;
    }
    if((iAuth & 8) != 0){
        sRet = sRet + "ESSWordSign@" ;
    }
    if((iAuth & 16) != 0){
        sRet = sRet + "ESSExcelSign@" ;
    }
    if((iAuth & 32) != 0){
        sRet = sRet + "ESSPdfSign@" ;
    }
    if((iAuth & 64) != 0){
        sRet = sRet + "ESSMidWare@" ;
    }
    return sRet;
}


//对时间进行年的加减操作
function dateOperator(date,years) {
    date = date.replace(/-/g,"/"); //更改日期格式
    var nd = new Date(date);

    nd.setFullYear(nd.getFullYear()+parseInt(years));

    var y = nd.getFullYear();
    var m = nd.getMonth()+1;
    var d = nd.getDate();
    if(m <= 9) m = "0"+m;
    if(d <= 9) d = "0"+d;
    var cdate = y+"-"+m+"-"+d;
    return cdate;
}

/**
 * @param str 需要操作的字符串
 * @param str1 需要替换的字符串
 * @param str2 替换结果字符串
 *
 */
function replaceAll(str,str1,str2) {

    re = new RegExp(str1,"g"); //定义正则表达式
    //第一个参数是要替换掉的内容，第二个参数"g"表示替换全部（global）。
    var Newstr = str.replace(re, str2); //第一个参数是正则表达式。
    //本例会将全部匹配项替换为第二个参数。
    return Newstr;
}

function checkIDCard(idcode){
    // 加权因子
    var weight_factor = [7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2];
    // 校验码
    var check_code = ['1', '0', 'X' , '9', '8', '7', '6', '5', '4', '3', '2'];

    var code = idcode + "";
    var last = idcode[17];//最后一位

    var seventeen = code.substring(0,17);

    // ISO 7064:1983.MOD 11-2
    // 判断最后一位校验码是否正确
    var arr = seventeen.split("");
    var len = arr.length;
    var num = 0;
    for(var i = 0; i < len; i++){
        num = num + arr[i] * weight_factor[i];
    }

    // 获取余数
    var resisue = num%11;
    var last_no = check_code[resisue];

    // 格式的正则
    // 正则思路
    /*
    第一位不可能是0
    第二位到第六位可以是0-9
    第七位到第十位是年份，所以七八位为19或者20
    十一位和十二位是月份，这两位是01-12之间的数值
    十三位和十四位是日期，是从01-31之间的数值
    十五，十六，十七都是数字0-9
    十八位可能是数字0-9，也可能是X
    */
    var idcard_patter = /^[1-9][0-9]{5}([1][9][0-9]{2}|[2][0][0|1][0-9])([0][1-9]|[1][0|1|2])([0][1-9]|[1|2][0-9]|[3][0|1])[0-9]{3}([0-9]|[X])$/;

    // 判断格式是否正确
    var format = idcard_patter.test(idcode);

    // 返回验证结果，校验码和格式同时正确才算是合法的身份证号码
    return last === last_no && format ? true : false;
}

function getBLen(str) {
    if (str == null) return 0;
    if (typeof str != "string"){
        str += "";
    }
    return str.replace(/[^\x00-\xff]/g,"01").length;
}


/**
 * 校验特殊符号
 * @param temp_str
 * @returns {Boolean}
 */
function is_forbid(temp_str) {
    temp_str = trimTxt(temp_str);
    temp_str = temp_str.replace('*', "@");
    temp_str = temp_str.replace('--', "@");
    temp_str = temp_str.replace('/', "@");
    temp_str = temp_str.replace('+', "@");
    temp_str = temp_str.replace('\'', "@");
    temp_str = temp_str.replace('\\', "@");
    temp_str = temp_str.replace('$', "@");
    temp_str = temp_str.replace('^', "@");
    temp_str = temp_str.replace('.', "@");
    temp_str = temp_str.replace(';', "@");
    temp_str = temp_str.replace('<', "@");
    temp_str = temp_str.replace('>', "@");
    temp_str = temp_str.replace('"', "@");
    temp_str = temp_str.replace('=', "@");
    temp_str = temp_str.replace('{', "@");
    temp_str = temp_str.replace('}', "@");
    var forbid_str = new String('@,%,~,&');
    var forbid_array = new Array();
    forbid_array = forbid_str.split(',');
    for (i = 0; i < forbid_array.length; i++) {
        if (temp_str.search(new RegExp(forbid_array[i])) != -1)
            return false;
    }
    return true;
}

//正则 特殊符号
function trimTxt(txt) {
    return txt.replace(/(^\s*)|(\s*$)/g, "");
}

