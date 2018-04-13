# **概述**

可侧滑item的列表模块（内含iOS和Android）

APICloud 的 UIListView 模块是对原生 listView 控件的封装。目的是为了让 APICloud 的广大开发者只需用 html+js 即可快速、高效的集成炫酷的列表模块。本模块最大的特定是实现了 item（cell）的侧滑效果，有效的解决了 html+js 代码在手机上运行不流畅的问题。由于本模块 UI 布局界面为固定模式，不能满足日益增长的广大开发者对侧滑列表模块样式的需求。因此，广大原生模块开发者，可以参考此模块的开发方式、接口定义等开发规范，或者基于此模块开发出更多符合产品设计的新 UI 布局的模块，希望此模块能起到抛砖引玉的作用。

# **模块接口文档**

<p style="color: #ccc; margin-bottom: 30px;">来自于：APICloud 官方</p>

<div class="outline">

[open](#open)
[close](#close)
[show](#show)
[hide](#hide)
[setAttr](#setAttr)
[getIndex](#getIndex)
[getDataByIndex](#getDataByIndex)
[setSwipeBtns](#setSwipeBtns)
[reloadData](#reloadData)
[deleteItem](#deleteItem)
[updateItem](#updateItem)
[insertItem](#insertItem)
[appendData](#appendData)
[getCount](#getCount)
[setRefreshHeader](#setRefreshHeader)
[setRefreshFooter](#setRefreshFooter)

</div>

## **模块概述**

UIListView 模块封装了一个数据列表控件，列表项水平侧滑可出现控制按钮；开发者可自定义列表的数据源，及列表的样式，支持列表项的增、删、改、查，支持批量更新、追加数据，支持下拉刷新和上拉加载事件。**UIListView 模块是 listView 模块的优化版。**本模块的源码开源地址为：[https://github.com/apicloudcom/UIListView](https://github.com/apicloudcom/UIListView)

![图片说明](http://docs.apicloud.com/img/docImage/listView.jpg)

<div id="open"></div>

# **open**

打开 UIListView 模块

open({params}, callback(ret, err))

## params

rect：

- 类型：JSON 对象
- 描述：（可选项）模块的位置及尺寸
- 备注：Android 必须传此参数。
- 内部字段：

```js
{
    x: 0,   //（可选项）数字类型；模块左上角的 x 坐标（相对于所属的 Window 或 Frame）；默认值：0
    y: 0,   //（可选项）数字类型；模块左上角的 y 坐标（相对于所属的 Window 或 Frame）；默认值：0
    w: 320, //（可选项）数字类型；模块的宽度；默认值：所属的 Window 或 Frame 的宽度
    h: 480  //（可选项）数字类型；模块的高度；默认值：所属的 Window 或 Frame 的高度
}
```

data：

- 类型：数组
- 描述：列表的数据源，**开发者可以自定义唯一的键值对信息（如：uid: '1001'），供 getIndex 使用**
- 内部字段：

```js
[{
    imgPath: '',            //（可选项）字符串类型；列表项的配图路径，支持http://、https://、widget://、fs://等协议，网络图片会被缓存到本地，若不传则标题和子标题靠最左侧显示
    title: '',              //（可选项）字符串类型；标题，若不传或为空则 subTitle 上下位置居中显示
    subTitle: '',           //（可选项）字符串类型；子标题，若不传或为空则 title 上下位置居中显示
    remark: '',             //（可选项）字符串类型；右边备注文字
    icon: '',               //（可选项）字符串类型；右侧备注的图标路径（本地路径，支持fs://、widget://）
    forbidden: false,       //（可选项）布尔类型；是否屏蔽本条数据的点击事件和侧滑露出按钮功能，屏蔽后的样式可通过 styles->forbidden参数配置
    rightBtns: []           //（可选项）数组类型；列表项向左滑动露出的按钮组，配置某一项的特定按钮组，若不传则显示通用按钮，内部字段同下方 rightBtns 参数
}]
```

rightBtns：

- 类型：数组
- 描述：（可选项）列表项向左滑动露出的按钮组，**建议：配置列表每项的通用按钮，用此参数；配置某一项的特定按钮，可在 `data` 数组的指定项传入 `rightBtns` 参数**
- 内部字段：

```js
[{
    bgColor: '#388e8e', //（可选项）字符串类型；按钮背景色，支持 rgb、rgba、#；默认：'#388e8e'
    activeBgColor: '',  //（可选项）字符串类型；按钮按下时的背景色，支持 rgb、rgba、#
    width: 70,          //（可选项）数字类型；按钮的宽度；默认：w / 4       
    title: '',          //（可选项）字符串类型；按钮标题，水平、垂直居中
    titleSize: 12,      //（可选项）数字类型；按钮标题文字大小；默认：12
    titleColor: '#fff', //（可选项）字符串类型；按钮标题文字颜色，支持 rgb、rgba、#；默认：'#ffffff'
    icon: '',           //（可选项）字符串类型；按钮标题前的图标路径（本地路径，支持fs://、widget://），水平、垂直居中，图标为正方形
    iconWidth: 20       //（可选项）数字类型；按钮标题前的图标宽度，图标为正方形；默认：20
}]
```

bounces:

- 类型：布尔
- 描述：（可选项）是否开启弹动，android 平台不支持本参数
- 默认：false

styles:

- 类型：JSON 对象
- 描述：（可选项）模块各部分的样式
- 内部字段：

```js
{
    borderColor: '#696969',             //（可选项）字符串类型；列表分割线的颜色，支持 rgb、rgba、#；默认：'#696969'
    item: {                             //（可选项）JSON对象；列表项的样式
        bgColor: '#AFEEEE',             //（可选项）字符串类型；列表项的背景色，支持 rgb、rgba、#；默认：'#AFEEEE'
        activeBgColor: '#F5F5F5',       //（可选项）字符串类型；列表项按下时的背景色，支持 rgb、rgba、#；默认：'#F5F5F5'
        height: 55,                     //（可选项）数字类型；列表项的高度；默认：55
        imgWidth: 40,                   //（可选项）数字类型；列表项配图的宽度；默认：列表项的高度减去10px
        imgHeight: 40,                  //（可选项）数字类型；列表项配图的高度；默认：列表项的高度减去10px
        imgCorner: 4,                   //（可选项）数字类型；列表项配图的圆角大小；默认：0
        placeholderImg: '',             //（可选项）字符串类型；列表项配图的占位图路径（本地路径，fs://、widget://），默认：APICloud 图标
        titleSize: 12,                  //（可选项）数字类型；列表项标题文字大小；默认：12
        titleColor: '#000',             //（可选项）字符串类型；列表项标题文字颜色，支持 rgb，rgba，#；默认：'#000000'
        subTitleSize: 12,               //（可选项）数字类型；列表项子标题文字大小；默认：12
        subTitleColor: '#000',          //（可选项）字符串类型：列表项子标题文字颜色，支持 rgb、rgba、#；默认：'#000000' 
        remarkColor: '#000',            //（可选项）字符串类型；备注的文字颜色，支持 rgb、rgba、#；默认：'#000000'
        remarkSize: 16,                 //（可选项）数字类型；备注的文字大小；默认：16
        remarkIconWidth: 30              //（可选项）数字类型；当备注是图片时，图片的宽度，图片为正方形；默认：30
    }，
    forbidden: {                        //（可选项）JSON对象；屏蔽点击事件的列表项的样式配置
        bgColor: '#B0C4DE',             //（可选项）字符串类型；列表项背景色，支持rgb、#、rgba；默认：#C0C0C0
        titleColor: '800000',           //（可选项）字符串类型；列表项标题色，支持rgb、#、rgba；默认：#808080
        subTitleColor: '#696969',       //（可选项）字符串类型；列表项子标题色，支持rgb、#、rgba；默认：#808080
        remarkColor: '#FFDEAD',         //（可选项）字符串类型；列表项备注色，支持rgb、#、rgba；默认：#808080
        sideslip: false                 //（可选项）布尔类型；是否只屏蔽侧滑；默认：false（侧滑和点击事件都屏蔽）
    }
}
```

fixedOn：

- 类型：字符串类型
- 描述：（可选项）模块视图添加到指定 frame 的名字（只指 frame，传 window 无效）
- 默认：模块依附于当前 window


showScrollBar：

- 类型：布尔
- 描述：（可选项）是否显示滚动条
- 默认：true

## callback(ret)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
    eventType: 'show',  //字符串类型；交互事件类型
                        //取值范围如下：
	                    //show（模块加载成功）
                        //clickRightBtn（点击侧滑出现的右侧按钮）
                        //clickContent（点击列表项的内容，除了配图和备注以外的区域）
                        //clickImg（点击列表项的配图）
                        //clickRemark（点击列表项右侧备注）
	index: 0,           //数字类型；列表项的索引
	btnIndex: 0         //数字类型；列表项侧滑出现的按钮的索引
}
```

## 示例代码

```js
var UIListView = api.require('UIListView');
UIListView.open({
	rect: {
		x: 0,
		y: 0,
		w: api.winWidth,
		h: api.frameHeight
	},
	data: [{
		uid: '1001',
		imgPath: 'widget://res/img/apicloud.png',
		title: '标题',
		subTitle: '子标题，说明文字',
		remark: '备注',
		icon: ''
	}, {
		uid: '1001',
		imgPath: 'widget://res/img/apicloud.png',
		title: '标题',
		subTitle: '子标题，说明文字',
		remark: '备注',
		icon: ''
	}, {
		uid: '1001',
		imgPath: 'widget://res/img/apicloud.png',
		title: '标题',
		subTitle: '子标题，说明文字',
		remark: '备注',
		icon: ''
	}, {
		uid: '1001',
		imgPath: 'widget://res/img/apicloud.png',
		title: '标题',
		subTitle: '子标题，说明文字',
		remark: '备注',
		icon: ''
	}, {
		uid: '1001',
		imgPath: 'widget://res/img/apicloud.png',
		title: '标题',
		subTitle: '子标题，说明文字',
		remark: '备注',
		icon: ''
	}, {
		uid: '1001',
		imgPath: 'widget://res/img/apicloud.png',
		title: '标题',
		subTitle: '子标题，说明文字',
		remark: '备注',
		icon: ''
	}, {
		uid: '1001',
		imgPath: 'widget://res/img/apicloud.png',
		title: '标题',
		subTitle: '子标题，说明文字',
		remark: '备注',
		icon: ''
	}, {
		uid: '1001',
		imgPath: 'widget://res/img/apicloud.png',
		title: '标题',
		subTitle: '子标题，说明文字',
		remark: '备注',
		icon: ''
	}, {
		uid: '1001',
		imgPath: 'widget://res/img/apicloud.png',
		title: '标题',
		subTitle: '子标题，说明文字',
		remark: '备注',
		icon: ''
	}, {
		uid: '1001',
		imgPath: 'widget://res/img/apicloud.png',
		title: '标题',
		subTitle: '子标题，说明文字',
		remark: '备注',
		icon: ''
	}, {
		imgPath: 'widget://res/img/apicloud.png',
		title: '标题',
		subTitle: '子标题，说明文字',
		remark: '备注',
		icon: ''
	}],
	rightBtns: [{
		bgColor: '#388e8e',
		activeBgColor: '',
		width: 70,
		title: '按钮',
		titleSize: 12,
		titleColor: '#fff',
		icon: '',
		iconWidth: 20
	}],
	styles: {
		borderColor: '#696969',
		item: {
			bgColor: '#AFEEEE',
			activeBgColor: '#F5F5F5',
			height: 55.0,
			imgWidth: 40,
			imgHeight: 40,
			imgCorner: 4,
			placeholderImg: '',
			titleSize: 12.0,
			titleColor: '#000',
			subTitleSize: 12.0,
			subTitleColor: '#000',
			remarkColor: '#000',
			remarkSize: 16,
			remarkIconWidth: 30
		}
	},
	fixedOn: api.frameName
}, function(ret, err) {
	if (ret) {
		alert(JSON.stringify(ret));
	} else {
		alert(JSON.stringify(err));
	}
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="close"></div>
# **close**

关闭数据列表模块

close()

## 示例代码

```js
var UIListView = api.require('UIListView');
UIListView.close();
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="show"></div>
# **show**
 
显示 UIListView 模块
 
show()
 
## 示例代码
 
```js    
var UIListView = api.require('UIListView');
UIListView.show();
```
 
## 可用性
 
iOS系统，Android系统
 
可提供的1.0.0及更高版本

<div id="hide"></div>
# **hide**
 
隐藏 UIListView 模块

hide()
 
## 示例代码
 
```js
var UIListView = api.require('UIListView');
UIListView.hide();
```
 
## 可用性
 
iOS系统，Android系统
 
可提供的1.0.0及更高版本

<div id="setAttr"></div>
# **setAttr**

设置列表的纵坐标和高度

setAttr({params}, callback(ret))

##params

y：

- 类型：数字
- 描述：（可选项）模块的纵坐标
- 默认值：原 y 值

h：

- 类型：数字
- 描述：（可选项）模块的高度
- 默认值：原 h 值

## callback(ret)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
    status: true       //布尔型；true||false
}
```

## 示例代码

```js
var UIListView = api.require('UIListView');
UIListView.setAttr({
	y: 40,
	h: 200
}, function(ret, err) {
	if (ret) {
		alert(JSON.stringify(ret));
	} else {
		alert(JSON.stringify(err));
	}
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="getIndex"></div>
# **getIndex**

根据开发者自定义的唯一标识（open 接口的 data 参数中自定义的唯一标识）查找列表项对应的数据

getIndex({params}, callback(ret, err))

## params

key：

- 类型：字符串
- 描述：调用 open 接口时，data 参数传入的开发者自定义的唯一标识的 key

value：

- 类型：字符串
- 描述：调用 open 接口时，data 参数传入的开发者自定义的唯一标识的 value

## callback(ret)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
    index: 0,   //数字类型；当前列表项的索引
    data: []    //数组类型；当前列表项的数据，内部字段与 open 时的 data 参数一致
}
```

## 示例代码

```js
var UIListView = api.require('UIListView');
UIListView.getIndex({
	key: 'uid',
	value: '1001'
}, function(ret, err) {
	if (ret) {
		alert(JSON.stringify(ret));
	} else {
		alert(JSON.stringify(err));
	}
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="getDataByIndex"></div>
# **getDataByIndex**

根据列表项的索引获取对应的数据

getDataByIndex({params}, callback(ret))

## params

index：

- 类型：数字
- 描述：（可选项）列表项的索引
- 默认值：0

## callback(ret)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
	data: []    //数组类型；当前列表项的数据，内部字段与 open 时的 data 参数一致
}
```

## 示例代码

```js
var UIListView = api.require('UIListView');
UIListView.getDataByIndex({
	index: 0
}, function(ret, err) {
	if (ret) {
		alert(JSON.stringify(ret));
	} else {
		alert(JSON.stringify(err));
	}
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="setSwipeBtns"></div>
# **setSwipeBtns**

设置侧滑显示出来的按钮

setSwipeBtns({params}, callback(ret))

## params

index：

- 类型：数字
- 描述：（可选项）列表项的索引
- 默认值：0

btns：

- 类型：数组
- 描述：（可选项）列表项侧滑露出的按钮组
- 内部字段：

```js
[{
    bgColor: '#388e8e', //（可选项）字符串类型；按钮背景色，支持 rgb、rgba、#；默认：'#388e8e'
    activeBgColor: '',  //（可选项）字符串类型；按钮按下时的背景色，支持 rgb、rgba、#
    width: 70,          //（可选项）数字类型；按钮的宽度；默认：w / 4       
    title: '',          //（可选项）字符串类型；按钮标题，水平、垂直居中
    titleSize: 12,      //（可选项）数字类型；按钮标题文字大小；默认：12
    titleColor: '#fff', //（可选项）字符串类型；按钮标题文字颜色，支持 rgb、rgba、#；默认：'#ffffff'
    icon: '',           //（可选项）字符串类型；按钮标题前的图标路径（本地路径，支持fs://、widget://），水平、垂直居中，图标为正方形
    iconWidth: 20       //（可选项）数字类型；按钮标题前的图标宽度，图标为正方形；默认：20
}]
```

## callback(ret)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
    status: true       //布尔型；true||false
}
```

## 示例代码

```js
var UIListView = api.require('UIListView');
UIListView.setSwipeBtns({
	index: 0,
	btns: [{
		bgColor: '#388e8e',
		activeBgColor: '',
		width: 70,
		title: '',
		titleSize: 12,
		titleColor: '#fff',
		icon: '',
		iconWidth: 20
	}]
}, function(ret, err) {
	if (ret) {
		alert(JSON.stringify(ret));
	} else {
		alert(JSON.stringify(err));
	}
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="reloadData"></div>
# **reloadData**

刷新列表数据

reloadData({params}, callback(ret))

## params

data：

- 类型：数组
- 描述：（可选项）列表的数据源，**若不传或传空，仅收起下拉刷新组件**
- 内部字段：

```js
[{
    imgPath: '',            //（可选项）字符串类型；列表项的配图路径，支持http://、https://、widget://、fs://等协议，网络图片会被缓存到本地，若不传则标题和子标题靠最左侧显示
    title: '',              //（可选项）字符串类型；标题，若不传或为空则 subTitle 上下位置居中显示
    subTitle: '',           //（可选项）字符串类型；子标题，若不传或为空则 title 上下位置居中显示
    remark: '',             //（可选项）字符串类型；右边备注文字
    icon: '',               //（可选项）字符串类型；右侧备注的图标路径（本地路径，支持fs://、widget://）
    forbidden: false,       //（可选项）布尔类型；是否屏蔽本条数据的点击事件，屏蔽后的样式可通过 open->styles->forbidden参数配置
    rightBtns: []           //（可选项）数组类型；列表项向左滑动露出的按钮组，配置某一项的特定按钮组，若不传则显示通用按钮，内部字段同 rightBtns 参数
}]
```

## callback(ret)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
    status: true        //布尔型；true||false     
}
```

## 示例代码

```js
var UIListView = api.require('UIListView');
UIListView.reloadData({
	data: [{
		imgPath: 'http://img1.3lian.com/gif/more/11/201206/a5194ba8c27b17def4a7c5495aba5e32.jpg',
		title: '新标题',
		subTitle: '新子标题',
		remark: '新备注',
		icon: ''
	}]
}, function(ret) {
	if (ret) {
		alert(JSON.stringify(ret));
	} else {
		alert(JSON.stringify(err));
	}
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本
 
<div id="deleteItem"></div>
# **deleteItem**

根据索引删除某一条列表的数据

deleteItem({params}, callback(ret))

## params

index：

- 类型：数字
- 描述：（可选项）数据列表的索引
- 默认值：0

## callback(ret)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
    status: true       //布尔型；true||false
}
```

## 示例代码

```js
var UIListView = api.require('UIListView');
UIListView.deleteItem({
	index: 2
}, function(ret, err) {
	if (ret) {
		alert(JSON.stringify(ret));
	} else {
		alert(JSON.stringify(err));
	}
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本
 
<div id="updateItem"></div>
# **updateItem**

根据索引更新某一条列表的数据

updateItem({params}, callback(ret))

## params

index：

- 类型：数字
- 描述：（可选项）数据列表的索引
- 默认值：0

data：

- 类型：JSON 对象
- 描述：列表的数据源
- 内部字段：

```js
{
	imgPath: '',            //（可选项）字符串类型；列表项的配图路径，支持http://、https://、widget://、fs://等协议，网络图片会被缓存到本地，若不传则标题和子标题靠最左侧显示
    title: '',              //（可选项）字符串类型；标题，若不传或为空则 subTitle 上下位置居中显示
    subTitle: '',           //（可选项）字符串类型；子标题，若不传或为空则 title 上下位置居中显示
    remark: '',             //（可选项）字符串类型；右边备注文字
    icon: '',               //（可选项）字符串类型；右侧备注的图标路径（本地路径，支持fs://、widget://）
    forbidden: false,       //（可选项）布尔类型；是否屏蔽本条数据的点击事件，屏蔽后的样式可通过 open->styles->forbidden参数配置
    rightBtns: []           //（可选项）数组类型；列表项向左滑动露出的按钮组，配置某一项的特定按钮组，若不传则显示通用按钮，内部字段同 rightBtns 参数
}
```

## callback(ret)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
    status: true     //布尔型；true||false
}
```

## 示例代码

```js
var UIListView = api.require('UIListView');
UIListView.updateItem({
	index: 2,
	data: {
		imgPath: 'http://img1.3lian.com/gif/more/11/201206/a5194ba8c27b17def4a7c5495aba5e32.jpg',
		title: '刷新标题',
		subTitle: '刷新子标题',
		remark: '刷新备注'
	}
}, function(ret, err) {
	if (ret) {
		alert(JSON.stringify(ret));
	} else {
		alert(JSON.stringify(err));
	}
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本
 
<div id="insertItem"></div>
# **insertItem**

根据索引向某一条列表插入数据

insertItem({params}, callback(ret, err))

## params

index：

- 类型：数字
- 描述：（可选项）数据列表的索引
- 默认值：列表最后一条数据的索引

data：

- 类型：JSON 对象
- 描述：列表的数据源
- 内部字段：

```js
{
    imgPath: '',            //（可选项）字符串类型；列表项的附图路径，支持http://、https://、widget://、fs://等协议，网络图片会被缓存到本地，若不传则标题和子标题靠最左侧显示
    title: '',              //（可选项）字符串类型；标题，若不传或为空则 subTitle 上下位置居中显示
    subTitle: '',           //（可选项）字符串类型；子标题，若不传或为空则 title 上下位置居中显示
    remark: '',             //（可选项）字符串类型；右边备注文字
    icon: '',               //（可选项）字符串类型；右侧备注的图标路径（本地路径，支持fs://、widget://）
    forbidden: false,       //（可选项）布尔类型；是否屏蔽本条数据的点击事件，屏蔽后的样式可通过 open->styles->forbidden参数配置
    rightBtns: []           //（可选项）数组类型；列表项向左滑动露出的按钮组，配置某一项的特定按钮组，内部字段同 open 接口的 rightBtns 参数
}
```

## callback(ret)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
    status: true          //布尔型；true||false
}
```

## 示例代码

```js
var UIListView = api.require('UIListView');
UIListView.insertItem({
	index: 2,
	data: {
		imgPath: 'http://d.hiphotos.baidu.com/image/pic/item/4d086e061d950a7b29a788c209d162d9f2d3c922.jpg',
		title: '12:00',
		subTitle: 'APICloud粉丝互动会',
		remark: '完成'
	}
}, function(ret, err) {
	if (ret) {
		alert(JSON.stringify(ret));
	} else {
		alert(JSON.stringify(err));
	}
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本
 
<div id="appendData"></div>
# **appendData**
 
向列表末端追加数据
 
appendData({params}, callback(ret))
 
## params
 
data：

- 类型：数组对象
- 描述：列表的数据源，**若不传或传空，仅收起上拉加载组件**
- 内部字段：

```js
[{
    imgPath: '',            //（可选项）字符串类型；列表项的附图路径，支持http://、https://、widget://、fs://等协议，网络图片会被缓存到本地，若不传则标题和子标题靠最左侧显示
    title: '',              //（可选项）字符串类型；标题，若不传或为空则 subTitle 上下位置居中显示
    subTitle: '',           //（可选项）字符串类型；子标题，若不传或为空则 title 上下位置居中显示
    remark: '',             //（可选项）字符串类型；右边备注文字
    icon: '',               //（可选项）字符串类型；右侧备注的图标路径（本地路径，支持fs://、widget://）
    forbidden: false,       //（可选项）布尔类型；是否屏蔽本条数据的点击事件，屏蔽后的样式可通过 open->styles->forbidden参数配置
    rightBtns: []           //（可选项）数组类型；列表项向左滑动露出的按钮组，配置某一项的特定按钮组，内部字段同 open 接口的 rightBtns 参数
}]
```
 
## callback(ret)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
    status: true        //布尔型；true||false
}
```

## 示例代码
 
```js
var UIListView = api.require('UIListView');
UIListView.appendData({
	data: [{
		imgPath: 'http://d.hiphotos.baidu.com/image/pic/item/4d086e061d950a7b29a788c209d162d9f2d3c922.jpg',
		title: '新增标题',
		subTitle: '新增子标题',
		remark: '新增备注'
	}]
}, function(ret, err) {
	if (ret) {
		alert(JSON.stringify(ret));
	} else {
		alert(JSON.stringify(err));
	}
});
```
 
## 可用性
 
iOS系统，Android系统
 
可提供的1.0.0及更高版本

<div id="getCount"></div>
# **getCount**
 
获取当前列表的总数据量
 
getCount(callback(ret))
 
 
## callback(ret)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
    count: 21        //数字类型；当前列表包含的数据总数
}
```

## 示例代码
 
```js
var UIListView = api.require('UIListView');
UIListView.getCount(function(ret) {
	alert(JSON.stringify(ret));
});
```
 
## 可用性
 
iOS系统，Android系统
 
可提供的1.0.0及更高版本

<div id="setRefreshHeader"></div>
# **setRefreshHeader**
 
设置下拉刷新，**通过 reloadData 收起下拉刷新组件**
 
setRefreshHeader({params}, callback(ret))
 
## params
 
loadingImg：
 
- 类型：字符串
- 描述：下拉刷新时显示的小箭头图标的本地路径，要求本地路径（fs://、widget://）
 
bgColor：
 
- 类型：字符串
- 描述：（可选项）下拉刷新区域的背景色，支持 rgb、rgba、#
- 默认值：'#f5f5f5'
 
textColor：
 
- 类型：字符串
- 描述：（可选项）提示文字颜色，支持 rgb、rgba、#
- 默认值：'#8e8e8e'
 
textDown：
 
- 类型：字符串
- 描述：（可选项）下拉提示文字
- 默认值：下拉可以刷新...
 
textUp：
 
- 类型：字符串
- 描述：（可选项）松开提示文字
- 默认值：松开开始刷新...

loadingText:

- 类型：字符串
- 描述：（可选项）提示文字
- 默认值：正在加载...

lastUpdateText:

- 类型：字符串
- 描述：（可选项）提示文字
- 默认值：上次更新时间: 
 
showTime：
 
- 类型：布尔值
- 描述：（可选项）是否显示刷新时间
- 默认值：true
 
 
## callback()
 
下拉刷新的事件回调
 
## 示例代码
 
```js
var UIListView = api.require('UIListView');
UIListView.setRefreshHeader({
	loadingImg: 'widget://res/UIListView_arrow.png',
	bgColor: '#F5F5F5',
	textColor: '#8E8E8E',
	textDown: '下拉可以刷新...',
	textUp: '松开开始刷新...',
	showTime: true
}, function(ret, err) {
	if (ret) {
		alert(JSON.stringify(ret));
	} else {
		alert(JSON.stringify(err));
	}
});
```
 
## 可用性
 
iOS系统，Android系统
 
可提供的1.0.0及更高版本

<div id="setRefreshFooter"></div>
# **setRefreshFooter**
 
设置上拉加载，**通过 appendData 收起上拉加载组件**
 
setRefreshFooter({params}, callback())
 
## params
 
loadingImg：
 
- 类型：字符串
- 描述：上拉加载时显示的小箭头图标的本地路径，要求本地路径（fs://、widget://）
 
bgColor：
 
- 类型：字符串
- 描述：（可选项）上拉加载区域的背景色，支持 rgb、rgba、#
- 默认值：'#f5f5f5'
 
textColor：
 
- 类型：字符串
- 描述：（可选项）提示文字颜色，支持 rgb、rgba、#
- 默认值：'#8e8e8e'
 
textUp：
 
- 类型：字符串
- 描述：（可选项）上拉提示文字
- 默认值：'上拉加载更多...'
 
textDown：
 
- 类型：字符串
- 描述：（可选项）松开提示文字
- 默认值：'松开开始加载...'

loadingText:

- 类型：字符串
- 描述：（可选项）提示文字
- 默认值：正在加载...

lastUpdateText:

- 类型：字符串
- 描述：（可选项）提示文字
- 默认值：上次更新时间: 
 
showTime：
 
- 类型：布尔值
- 描述：（可选项）是否显示刷新时间
- 默认值：true
 
## callback(ret)
 
上拉加载的事件回调
 
## 示例代码
 
```js      
var UIListView = api.require('UIListView');
UIListView.setRefreshFooter({
	loadingImg: 'widget://res/UIListView_arrow.png',
	bgColor: '#F5F5F5',
	textColor: '#8E8E8E',
	textUp: '上拉加载更多...',
	textDown: '松开开始加载...',
	showTime: true
}, function(ret, err) {
	if (ret) {
		alert(JSON.stringify(ret));
	} else {
		alert(JSON.stringify(err));
	}
});
```
 
## 可用性
 
iOS系统，Android系统
 
可提供的1.0.0及更高版本
