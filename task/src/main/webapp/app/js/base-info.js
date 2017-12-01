/**
 * @author 立坤 创建于2016.06.15
 * @remark 该文件用于记录平台的一些基础信息
 *
 */
var baseInfo = (function() {
    var constants = {//定义常量
        name: "ponddy.com",
        phone: "400-116-7955",
        description: "任务调度管理平台",
        keyword:"任务系统,智能,调度,任务管理",
        unjquery:"未引入jquery，请引入",
        loadFailure:"数据加载失败"
    }
    var Test={};
    // 定义了一个静态方法
    Test.getInfo=function(name){//获取常量的方法
        return constants[name];
    }
    return Test
})();