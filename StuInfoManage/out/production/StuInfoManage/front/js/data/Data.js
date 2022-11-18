"use strict";
//学生信息
let students = [], curstudents = [];
//页码
let page = 1;
//回退的操作集合
let operations = [];
//由学号找到学生
let numMap = new Map();
//能否撤销
let can = true;