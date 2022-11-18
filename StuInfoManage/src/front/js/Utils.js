"use strict";

//中文字符解码
function decodeChinese(data) {
    for (let i = 0; i < data.length; i++) {
        data.at(i).s_name = decodeURI(data.at(i).s_name);
        data.at(i).s_number = decodeURI(data.at(i).s_number);
        data.at(i).s_grade = decodeURI(data.at(i).s_grade);
        data.at(i).s_class = decodeURI(data.at(i).s_class);
    }
}

//初始化数据
function init(data) {
    decodeChinese(data.students);
    students = data.students;
    for (let i = 0; i < students.length; i++) {
        let stu = students.at(i);
        numMap.set(stu.s_number, stu);
    }
    curstudents = Clone(students);
    tableInit();
}

//禁用enter换行
$(window).keydown(function (e) {
    let key = e.key;
    if (key.toString() === "Enter") {
        return false;
    }
});

//发送命令到服务器
function sendCommand(command, isRollback) {
    $.ajax({
        url: "http://localhost:8088/",
        data: JSON.stringify(command),
        dataType: "json",
        async: false,
        success: function (data) {
            console.log('success');
            if (data === true) {
                can = true;
                console.log(can);
            } else {
                can = false;
                console.log(can);
                if (isRollback) {
                    alert('该条数据已被删除或修改，撤销失败，数据将进行同步');
                    operations.pop();
                } else {
                    alert('该条数据已被删除或修改，待数据同步后请重试');
                }
                init(data);
            }
        },
        error: function (status) {
            console.log(status);
        }
    });
}

//克隆
function Clone(obj) {
    if (Array.isArray(obj)) {
        // 赋值数据 而不是地址
        // 对于引用数据类型 将对象中的值进行一次赋值
        const arr = [];
        for (let i = 0; i < obj.length; i++) {
            arr.push(Clone(obj[i]));
        }
        return arr;
    } else if (Object.prototype.toString.call(obj) === '[object Object]') {
        const newObj = {};
        for (let key in obj) {
            newObj[key] = Clone(obj[key]);
            // 基本数据类型不受影响，引用数据类型也不受影响
        }
        return newObj;
    } else {
        return obj; //这是是递归函数的出口
    }
}