"use strict";

//页码回退一页
function back() {
    if (page > 1) {
        let table = document.getElementById('information');
        for (let i = (page - 1) * 15 + 1, j = 0; i < curstudents.length + 1 && j < 15; i++, j++) {
            let row = table.children.item(i);
            row.style.display = 'none';
        }
        page--;
        for (let i = (page - 1) * 15 + 1, j = 0; j < 15; i++, j++) {
            let row = table.children.item(i);
            $(row).show();
            // row.style.display = (document.all ? "block" : "table-row");
        }
    }
    document.getElementById('page').innerHTML = 'total:' + Math.ceil(curstudents.length / 15) + ' cur:' + page;
}

//页码回退至第一页
function backk() {
    if (page > 1) {
        let table = document.getElementById('information');

        for (let i = (page - 1) * 15 + 1, j = 0; i < curstudents.length + 1 && j < 15; i++, j++) {
            let row = table.children.item(i);
            row.style.display = 'none';
        }
        while (page !== 1) {
            page--;
        }
        for (let i = (page - 1) * 15 + 1, j = 0; j < 15; i++, j++) {
            let row = table.children.item(i);
            $(row).show();
            // row.style.display = (document.all ? "block" : "table-row");
        }
    }
    document.getElementById('page').innerHTML = 'total:' + Math.ceil(curstudents.length / 15) + ' cur:' + page;
}

//下一页
function forward() {
    if (page < Math.ceil(curstudents.length / 15)) {
        let table = document.getElementById('information');
        for (let i = (page - 1) * 15 + 1, j = 0; j < 15; i++, j++) {
            let row = table.children.item(i);
            row.style.display = 'none';
        }
        page++;
        for (let i = (page - 1) * 15 + 1, j = 0; i < curstudents.length + 1 && j < 15; i++, j++) {
            let row = table.children.item(i);
            $(row).show();
            // row.style.display = (document.all ? "block" : "table-row");
        }
    }
    document.getElementById('page').innerHTML = 'total:' + Math.ceil(curstudents.length / 15) + ' cur:' + page;
}

//最后一页
function forwardd() {
    if (page < Math.ceil(curstudents.length / 15)) {
        let table = document.getElementById('information');

        for (let i = (page - 1) * 15 + 1, j = 0; j < 15; i++, j++) {
            let row = table.children.item(i);
            row.style.display = 'none';
        }
        while (page !== Math.ceil(curstudents.length / 15)) {
            page++;
        }
        for (let i = (page - 1) * 15 + 1, j = 0; i < curstudents.length + 1 && j < 15; i++, j++) {
            let row = table.children.item(i);
            $(row).show();
            // row.style.display = (document.all ? "block" : "table-row");
        }
    }
    document.getElementById('page').innerHTML = 'total:' + Math.ceil(curstudents.length / 15) + ' cur:' + page;
}