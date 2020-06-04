app.controller("baseController",function ($scope) {

    //分页控件配置  currentPage：当前页 totalItems：总记录数 itemsPerPage：每页显示多少条
    //perPageOptions：分页选项 onChange：当页码变更后自动触发的方法
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            //页面加载也会触发
            $scope.reloadList();
        }
    };

    //刷新列表
    $scope.reloadList = function(){
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

    //用户勾选的id集合
    $scope.selectIds = [];
    $scope.updateSelection = function ($event,id) {
        if($event.target.checked){
            //向集合中添加元素
            $scope.selectIds.push(id);
        }else{
            //查找值的位置
            var index = $scope.selectIds.indexOf(id);
            //splice(要移除的位置,移除的数量)
            $scope.selectIds.splice(index,1);
        }
    };


    $scope.jsonToString = function (jsonString,key) {
        var json = JSON.parse(jsonString);
        var value = "";
        for(var i = 0;i < json.length;++i){
            if(i > 0){
                value += ",";
            }
            value += json[i][key];
        }
        return value;
    }

    //在list集合中根据某key的值查询对象
    $scope.searchObjectByKey = function (list,key,value) {
        for(var i = 0; i < list.length; ++i){
            if(list[i][key] == value){
                return list[i];
            }
        }
        return null;
    }

});