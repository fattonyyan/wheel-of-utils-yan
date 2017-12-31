// 表单校验 Form
//参数：
//1. 具体的表单
//2. 键值对 顺序-判断条件
//3. 回调函数

function validate(form, conditionList, callbackFun){
    /*
    if(!form||form.length <1){
        return;
    }
    if( (typeof conditionList)!= "list"){
        return;
    } 
    
    if(form.element == null || form.element.length <1){
        return;
    }
    */
    var isValidated = true;
    
    for(var i=0;i<form.elements.length;i++){
        var el = form.elements[i];
        if(typeof(conditionList[i]) != "function"){
            continue;
        }
        var flag = conditionList[i](el.value);
        console.log(el.value);
        if(!flag){
            console.log(el.value, "未通过");
            callbackFun(el);
            isValidated = false;
            break;
        }
    }
    
    return isValidated;
}

// demo 验证 0~150 之间的数字
var conditionList = [];
conditionList[1] = function(val){
    console.log(val);
    if(val<150&&val>0){
        return true;
    }
    return false;
}

var flag = validate($infoAddForm[0], conditionList, function(el){
    var str = $(el).prev().html()
    str = str.substring(0, str.length-1);
    tipShow(str+"输入有误");
    // 需要停止外层函数
});

if(!flag){
    return;
}

// 辅助工具
function tipShow(text){
    $toastrMessage.html(text);
    $toastrWrap.show();
    timer = setTimeout("$toastrWrap.hide()", 2000);
}

function dataValidate(val){
    val||(val = '')
    return val;
}