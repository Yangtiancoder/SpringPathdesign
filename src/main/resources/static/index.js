
// 接口文件
const obj = {
    map: [], // 初始地图
    mapWidth: 26, // 地图宽度像素
    mapHeight: 7, // 地图高度像素
    task: [],
    ticks: 130,
    route: [],
};

// 货物种类版
const cargoColor = ['blue', 'skyblue', 'purple', 'orange', 'pink']
const cargoSize = [1, 1, 2, 2, 3]



$("#getMap").click(getMap);
const mapWidth = obj.mapWidth;
const mapHeight = obj.mapHeight;
let route = obj.route;
const colorPanel = ['black', 'lightgreen', 'red', 'yellow', 'orange']
let mapArr = []; // map数组 3 * width * height
let speed = 300; // 运动展示的速度



let map = document.getElementsByClassName('map');

// 绑定事件
$("#drawMap").click(drawMap);
$("#createRandom").click(createRandom);
$("#initCargo").click(initCargo);
$("#moveCargo").click(moveCargo);


/*
  * 绘制地图
  * desc: 根据mapArr数组 生成地图
  * arguments: no arguments 
*/
function drawMap() {
    if (mapArr.length == 0) {
        alert("请选择生成地图的方式！");
        return;
    }
    // 初始化地图
    $('.map').html("");
    // 根据mapArr绘制图形
    for (let table = 0; table < 3; table++) {
        map[table].style.gridTemplateColumns = 'repeat(' + mapWidth + ',1fr)';
        map[table].style.gridTemplateRows = 'repeat(' + mapHeight + ',1fr)';
        for (let i = 0; i < mapWidth * mapHeight; i++) {
            let li = document.createElement('li');
            li.setAttribute("table", table);
            li.setAttribute("pos", i);
            li.style.backgroundColor = colorPanel[mapArr[table][i]];
            map[table].appendChild(li);
        }
    }
}

/*
  * 后台获取MAP数据以及route数据
  * desc: 和后端的接口将写在这里 重新为obj里的数据赋值 覆盖掉假数据
  * arguments: no arguments
*/
function getMap() {
    $.get('/loadMap', (data, status) => {
        obj.map = eval(data.map)
        // map数组(1*all)切片成mapArr(3*(width*height))
        for (let i = 0; i < 3; i++) {
            mapArr.push(obj.map.splice(0, mapWidth * mapHeight));
        }
        drawMap();
    })

    $.get('/loadTask',(data,status)=>{
        obj.task = eval(data.task)
    })
    $.get('/loadPlan',(data,status)=>{
        console.log(data)
        obj.route = eval(data.plan)
        route = obj.route
    })

}


/*
  * 随机生成地图
  * desc: 和"随机生成地图"按钮绑定 点击触发
  * arguments: isNamed是否指定数据,不指定将随机  named指定的值
*/
function createRandom(event, isNamed = false, named = 1) {
    // 初始化数据
    obj['map'] = [];
    mapArr = [];
    // 制造假数据 3 * height * width 数据范围[0,1,2,3]
    // 如果isNamed为真 全部赋值named值
    for (let i = 0; i < 3 * mapHeight * mapWidth; i++) {
        obj['map'].push(isNamed ? named : parseInt(Math.random() * 4));
    }

    // map数组(1*all)切片成mapArr(3*(width*height))
    for (let i = 0; i < 3; i++) {
        mapArr.push(obj.map.splice(0, mapWidth * mapHeight));
    }
    // if (!isNamed) {
    //     $(".mapCode").stop().toggle().text(`MAP-CODE:[${mapArr}]`)
    // }
    drawMap();
}


/*
  * 初始化货物
  * desc:和初始化货物的按钮绑定，点击执行函数
*/
function initCargo() {
    if (mapArr.length === 0) {
        alert("需要先生成一个地图！");
        return;
    }

    const tasks = obj.task
    tasks.forEach((task, index) => {
        let size = cargoSize[task[3]],
            table = task[0],
            row = task[1],
            col = task[2],
            color = cargoColor[task[3]];
        drawCargo(size, table, row, col, color, false, index + 1)
    })
}

/*
  * 货物轨迹绘制
  * desc: 货物运动按钮绑定
*/
function moveCargo() {
    if (mapArr.length === 0) {
        alert("需要先生成一个地图！");
        return;
    }
    let tick = 1;
    const timer = setInterval(function () {
        // 设置定时器
        // 清楚上一tick的所有画面
        // if (tick != 1) {
        //     let lastTick = tick - 1;
        //     route.forEach(cargo => {
        //         // 当这一tick与上一tick发生变化时
        //         if (String(cargo[tick]) != String(cargo[lastTick])) {
        //             // let mapPos = cargo[tick][1] * mapWidth + cargo[tick][2]
        //             // let mapColor = colorPanel[mapArr[cargo[tick][0]][mapPos]]
        //             drawMap()
        //         }
        //     })
        // }
        drawMap();

        // 绘制这一tick的画面
        route.forEach((cargo, index) => {
            if (typeof cargo[tick] != "undefined")
                drawCargo(cargoSize[cargo[0]], cargo[tick][0], cargo[tick][1], cargo[tick][2], cargoColor[cargo[0]], false, index + 1);
        })
        tick++;
        if (tick > obj.ticks) {
            // tick 大于最终ticks 清楚定时器
            clearInterval(timer);
            alert("End!")
        }
    }, speed);
}
/*
  * 根据货物／地图建筑 的初始点(左上角坐标)绘制货物
  * desc: 根据货物／地图建筑 的初始点(左上角坐标)绘制货物
  * args: size货物大小 table地图编号[1,2,3] row,col坐标行列 color绘制颜色 isClear是否为清除的标志
*/
function drawCargo(size, table, row, col, color, isClear = false, isCargo = 0) {
    for (let i = row; i < row + size; i++) {
        for (let j = col; j < col + size; j++) {
            let pos = i * mapWidth + j;
            let object = $(`li[table='${table}'][pos='${pos}']`)
            let border = "1px solid white"
            object.css("backgroundColor", color).css("position", "relative")
            if (isClear === true) {
                object.css("border", border)
            } else {
                object.css("border", "none")
                if (i == row) object.css("borderTop", border)
                if (i == row + size - 1) object.css("borderButtom", border)
                if (j == col) object.css("borderLeft", border)
                if (j == col + size - 1) object.css("borderRight", border)
            }

            let numPos = row * mapWidth + col;
            $(`li[table='${table}'][pos='${numPos}']`).text(isCargo ? isCargo : "")

        }
    }


}












