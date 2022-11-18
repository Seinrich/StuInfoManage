"use strict";

//页面加载完成立即执行
window.onload = function () {

    //从服务器接收初始化数据
    $.ajax({
        type: "get",
        url: "http://localhost:8088/",
        data: JSON.stringify(new Command('init', null, null)),
        dataType: "json",
        async: false,
        success: function (data) {
            console.log("success init");
            init(data);
        },
        error: function (status) {
            console.log(status);
        }
    });

    //文本域按enter搜索
    document.getElementById('text').addEventListener('keydown', function (event) {
        if (event.key === "Enter") {
            search();
        }
    });
}

//根据数据重新设置表格
function tableInit() {

    let table = document.getElementById('information');
    for (let j = table.children.length - 1; j >= 1; j--) {
        table.children.item(j).remove();
    }

    for (let i = 0; i < curstudents.length; i++) {
        let stu = curstudents.at(i);
        let row = document.createElement('tr');
        row.id = 'row' + i;
        row.tabIndex = -1;

        let btntd = document.createElement('td');
        btntd.innerHTML = '<button onclick="del(this)">-</button>';
        row.appendChild(btntd);

        let name = document.createElement('td');
        name.textContent = stu.s_name;
        row.appendChild(name);

        let number = document.createElement('td');
        number.textContent = stu.s_number;
        row.appendChild(number);

        let grade = document.createElement('td');
        grade.textContent = stu.s_grade;
        row.appendChild(grade);

        let sclass = document.createElement('td');
        sclass.textContent = stu.s_class;
        row.appendChild(sclass);

        document.getElementById('information').appendChild(row);

        //给每行设置监听器以实现修改功能
        row.addEventListener("blur", function () {

            //触发监听器后只检查当前页码
            for (let i = (page - 1) * 15 + 1, j = 0; i < curstudents.length + 1 && j < 15; i++, j++) {
                let change = 0;
                let row = table.children.item(i);
                let num = $(row).index();
                let stu = curstudents.at(num - 1);
                if (row.children.item(1).textContent === stu.s_name && row.children.item(2).textContent === stu.s_number && row.children.item(3).textContent === stu.s_grade && row.children.item(4).textContent === stu.s_class) {
                } else {
                    if (row.children.item(1).textContent !== stu.s_name) {

                        //检查姓名格式
                        let data = row.children.item(1).textContent;
                        if (data.length > 6 || data.length < 2) {
                            alert('姓名格式不正确，将自动撤销该操作');
                            row.children.item(1).textContent = curstudents.at(i - 1).s_name;
                        } else {
                            change = 1;
                        }
                    } else if (row.children.item(2).textContent !== stu.s_number) {

                        // let data = row.children.item(2).textContent;
                        // if (data.length !== 8 || isNaN(parseInt(data)) || (numMap.has(data) && data !== curstudents.at(i - 1).s_number)) {
                        //     alert('学号格式不正确，将自动撤销该操作');
                        //     row.children.item(2).textContent = curstudents.at(i - 1).s_number;
                        // } else {
                        //change = 2;
                        // }

                        //学号不可修改
                        alert('学号为识别学生信息的唯一标准，不可修改');
                        row.children.item(2).textContent = curstudents.at(i - 1).s_number;

                    } else if (row.children.item(3).textContent !== stu.s_grade) {

                        //检查年级格式
                        let data = row.children.item(3).textContent;
                        if (data !== '大一' && data !== '大二' && data !== '大三' && data !== '大四') {
                            alert('年级格式不正确，将自动撤销该操作');
                            row.children.item(3).textContent = curstudents.at(i - 1).s_grade;
                        } else {
                            change = 3;
                        }
                    } else if (row.children.item(4).textContent !== stu.s_class) {

                        //检查班级格式
                        let data = row.children.item(4).textContent;
                        if ((!data.includes('魏国') && !data.includes('蜀国') && !data.includes('吴国')) || !data.includes('班') || data.length >= 5) {
                            alert('班级格式不正确，将自动撤销该操作');
                            row.children.item(4).textContent = curstudents.at(i - 1).s_class;
                        } else {
                            change = 4;
                        }
                    }
                    if (change !== 0) {

                        //如果发生修改，则返回修改指令至服务端
                        sendCommand(new Command('change',
                            curstudents.at(i - 1), new Student(
                                row.children.item(1).textContent, row.children.item(2).textContent,
                                row.children.item(3).textContent, row.children.item(4).textContent,
                                curstudents.at(i - 1).s_version
                            )), false);

                        if (can === false) {
                            return;
                        }

                        //更新版本号
                        curstudents.at(i - 1).s_version = curstudents.at(i - 1).s_version + 1;

                        if (change === 1) {
                            alert('修改成功！\n姓名 改前：' + curstudents.at(i - 1).s_name + '  改后：' + row.children.item(1).textContent);
                        } else if (change === 2) {
                            alert('修改成功！\n学号 改前：' + curstudents.at(i - 1).s_number + '  改后：' + row.children.item(2).textContent);
                        } else if (change === 3) {
                            alert('修改成功！\n年级 改前：' + curstudents.at(i - 1).s_grade + '  改后：' + row.children.item(3).textContent);
                        } else if (change === 4) {
                            alert('修改成功！\n班级 改前：' + curstudents.at(i - 1).s_class + '  改后：' + row.children.item(4).textContent);
                        }

                        //撤销时反向修改以还原
                        let stunum = students.indexOf(numMap.get(curstudents.at(i - 1).s_number));
                        operations.push(new Operation('change', Clone(curstudents.at(i - 1)), stunum));
                        //修改操作
                        let stu = new Student(row.children.item(1).textContent, row.children.item(2).textContent,
                            row.children.item(3).textContent, row.children.item(4).textContent, curstudents.at(i - 1).s_version + 1);
                        //更新map
                        numMap.delete(students.at(stunum).s_number);
                        students.splice(stunum, 1, Clone(stu));
                        numMap.set(students.at(stunum).s_number, students.at(stunum));
                        curstudents.splice(i - 1, 1, Clone(stu));
                    }
                }
            }
        });
    }

    //设置可编辑表格
    table.contentEditable = 'true';
    table.children.item(0).contentEditable = 'false';
    for (let j = 1; j < table.children.length; j++) {
        table.children.item(j).children.item(0).contentEditable = 'false';
    }

    //初始化页码为1
    document.getElementById('page').innerHTML = 'total:' + Math.ceil(curstudents.length / 15) + ' cur:1';
    page = 1;

    ///将不在当前页码的表格隐藏
    for (let i = 16; i < table.children.length; i++) {
        let row = table.children.item(i);
        row.style.display = 'none';
    }
}

//搜索功能
function search() {
    curstudents = [];
    let searchContent = document.getElementById("text").value;
    //根据curstudents的数据刷新表格
    for (let i = 0, j = 0; i < students.length - j; i++) {
        let stu = students.at(i);
        if (stu.s_name.includes(searchContent) || stu.s_number.includes(searchContent) || stu.s_grade.includes(searchContent) || stu.s_class.includes(searchContent)) {
            curstudents.push(Clone(stu));
        } else {
            j++;
        }
    }
    tableInit();
    document.getElementById("text").value = "";
}

//删除数据
function del(btn) {
    let row = btn.parentNode.parentNode;
    let next = row.nextSibling;
    let num = $(row).index();
    let stu = curstudents.at(num - 1);
    let number = stu.s_number;

    //将删除指令传至服务器端
    sendCommand(new Command('delete', stu, null), false);

    if (can === false) {
        return;
    }

    //撤销时反向创建以还原
    operations.push(new Operation('create', new Student(stu.s_name, stu.s_number, stu.s_grade, stu.s_class, stu.s_version), -1));
    //删除操作
    numMap.delete(students.indexOf(numMap.get(number)));
    students.splice(students.indexOf(numMap.get(number)), 1);
    curstudents.splice(num - 1, 1);
    row.parentNode.removeChild(row);

    //删除后后面数据补上
    while (next !== null && next.style.display !== 'none') {
        next = next.nextSibling;
    }
    if (next !== null) {
        $(next).show();
        // next.style.display = (document.all ? "block" : "table-row");
    }
    document.getElementById('page').innerHTML = 'total:' + Math.ceil(curstudents.length / 15) + ' cur:' + page;
}

//检查新建的学生信息格式是否正确
function check(btn) {
    let row = btn.parentNode;
    let info;

    info = document.getElementById('tname').textContent;
    if (info.length < 2 || info.length > 6) {
        alert('姓名格式错误，新建无效');
        row.parentNode.removeChild(row);
        document.getElementById("text").value = "";
        search();
        return;
    }
    info = document.getElementById('tnumber').textContent;
    if (info.length !== 8 || numMap.has(info)) {
        alert('学号格式错误，新建无效');
        row.parentNode.removeChild(row);
        document.getElementById("text").value = "";
        search();
        return;
    }
    info = document.getElementById('tgrade').textContent;
    if (info !== '大一' && info !== '大二' && info !== '大三' && info !== '大四') {
        alert('年级格式错误，新建无效');
        row.parentNode.removeChild(row);
        document.getElementById("text").value = "";
        search();
        return;
    }
    info = document.getElementById('tclass').textContent;
    if ((!info.includes('魏国') && !info.includes('蜀国') && !info.includes('吴国')) || !info.includes('班') || info.length >= 5) {
        alert('班级格式错误，新建无效');
        row.parentNode.removeChild(row);
        document.getElementById("text").value = "";
        search();
        return;
    }

    //如果正确则新建学生信息
    let stu = new Student(document.getElementById('tname').textContent,
        document.getElementById('tnumber').textContent,
        document.getElementById('tgrade').textContent,
        document.getElementById('tclass').textContent, 0);

    //将新建指令传至服务器端
    sendCommand(new Command('create', null, stu), false);

    if (can === false) {
        return;
    }

    //更新版本号
    stu.s_version = stu.s_version + 1;

    //新建后注意此页码中的变化以及最后一页的变化
    let i;
    for (i = 0; i < students.length; i++) {
        if (stu.s_number < students.at(i).s_number) {
            students.splice(i, 0, Clone(stu));
            operations.push(new Operation('delete', Clone(students.at(i)), -1));
            numMap.set(students.at(i).s_number, students.at(i));
            break;
        }
    }
    if (i === students.length) {
        students.push(Clone(stu));
        numMap.set(students.at(i).s_number, students.at(i));
    }
    for (i = 0; i < curstudents.length; i++) {
        if (stu.s_number < curstudents.at(i).s_number) {
            curstudents.splice(i, 0, Clone(stu));
            break;
        }
    }
    if (i === curstudents.length) {
        curstudents.push(Clone(stu));
    }

    row.parentNode.removeChild(row);
    document.getElementById("text").value = "";
    //新建后刷新表格
    search();
    alert('添加成功！\n' + "新增学生信息：" + stu.s_name + ' ' + stu.s_number + ' ' + stu.s_grade + ' ' + stu.s_class);
}


//新建学生信息
function create() {
    if (document.getElementById('trow') === null) {
        let row = document.createElement('tr');
        row.id = 'trow';

        let btntd = document.createElement('td');
        btntd.innerHTML = '<button onclick="check(this)">+</button>';
        row.appendChild(btntd);

        let name = document.createElement('td');
        name.id = 'tname';
        row.appendChild(name);

        let number = document.createElement('td');
        number.id = 'tnumber';
        row.appendChild(number);

        let grade = document.createElement('td');
        grade.id = 'tgrade';
        row.appendChild(grade);

        let sclass = document.createElement('td');
        sclass.id = 'tclass';
        row.appendChild(sclass);

        document.getElementById('information').appendChild(row);
    } else {
        alert('请先完善正在新建的学生信息');
    }
}

//撤销操作
function rollback() {
    if (operations.length > 0) {
        //初始化
        document.getElementById("text").value = "";
        search();

        let operation = operations.at(operations.length - 1);

        //反操作为创建，即撤销删除操作
        if (operation.op === 'create') {

            //将新建指令传送至服务器端
            sendCommand(new Command('create', null, operation.stu), true);

            if (can === false) {
                return;
            }

            //更新版本号
            operation.stu.s_version = operation.stu.s_version + 1;

            let i;
            for (i = 0; i < students.length; i++) {
                if (operation.stu.s_number < students.at(i).s_number) {
                    students.splice(i, 0, Clone(operation.stu));
                    numMap.set(students.at(i).s_number, students.at(i));
                    break;
                }
            }
            if (i === students.length) {
                students.push(Clone(operation.stu));
                numMap.set(students.at(i).s_number, students.at(i));
            }
            for (i = 0; i < curstudents.length; i++) {
                if (operation.stu.s_number < curstudents.at(i).s_number) {
                    curstudents.splice(i, 0, Clone(operation.stu));
                    break;
                }
            }
            if (i === curstudents.length) {
                curstudents.push(Clone(operation.stu));
            }
            alert('撤销成功！\n已恢复学生信息: ' + operation.stu.s_name + ' ' + +operation.stu.s_number + ' ' + operation.stu.s_grade + ' ' + operation.stu.s_class);
        }
        //反操作为修改，即撤销修改操作
        else if (operation.op === 'change') {

            //将修改指令传送至服务器端
            sendCommand(new Command('change', students.at(operation.num), operation.stu), true);

            if (can === false) {
                return;
            }

            //更新版本号
            operation.stu.s_version = operation.stu.s_version + 1;

            //更新map
            numMap.delete(students.at(operation.num).s_number);
            students.splice(operation.num, 1, Clone(operation.stu));
            numMap.set(students.at(operation.num).s_number, students.at(operation.num));
            curstudents.splice(operation.num, 1, Clone(operation.stu));
            alert('撤销成功！\n已还原学生信息: ' + operation.stu.s_name + ' ' + +operation.stu.s_number + ' ' + operation.stu.s_grade + ' ' + operation.stu.s_class);
        }
        //反操作为删除，即撤销新建操作
        else if (operation.op === 'delete') {
            let stu = operation.stu;
            let num = students.indexOf(numMap.get(stu.s_number));

            //将删除指令传送至服务器端
            sendCommand(new Command('delete', operation.stu, null), true);

            if (can === false) {
                return;
            }

            students.splice(num, 1);
            numMap.delete(stu.s_number);
            curstudents.splice(num, 1);
            alert('撤销成功！\n已删除学生信息: ' + stu.s_name + ' ' + +stu.s_number + ' ' + stu.s_grade + ' ' + stu.s_class);
        }
        operations.pop();
        //初始化
        document.getElementById("text").value = "";
        search();

    } else {
        alert('没有操作可以撤销');
    }
}

function explain() {
    alert('姓名不大于6位，不小于2位，可重名\n' + '学号为8位数字，并且是区分信息的唯一标准\n年级只能为大一、大二、大三、大四共四个年级\n' + '班级格式为：魏（蜀，吴）国x班，其中x应为10以内的数字\n' + '新建、删除和修改成功的操作可以撤销，但撤销为不可逆操作\n' + '提交修改后会对不符合要求的修改予以回退，修改成功的操作可撤销');
}
