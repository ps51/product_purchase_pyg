app.controller("searchController",function ($scope,$location,searchService) {


    //定义搜索对象的结构
    $scope.searchMap = {"keywords":"","category":"","brand":"","spec":{}
    ,"price":"","pageNo":1,"pageSize":15,"sort":"","sortField":""};
    //建立空模板，除了第一次使用关键字搜索时，都为它赋予关键字keywords 然后重新赋给主模版  作为搜索条件
    $scope.searchMap1 = JSON.parse(JSON.stringify($scope.searchMap));


        $scope.Reset = function(){
            if($scope.resultMap.hasOwnProperty("total")){
                if($scope.keywordsIsBrand()){
                    $scope.searchMap1.keywords = $scope.searchMap.keywords;
                    $scope.searchMap = JSON.parse(JSON.stringify($scope.searchMap1));
                }
            }

        }
    //搜索
        $scope.search = function () {
            $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);//转换为数字
            searchService.search($scope.searchMap).success(function (response) {
                $scope.resultMap = response;
                //构建分页栏
                buildPageLabel();
            })
        }

        buildPageLabel = function(){
            //构建分页栏
            $scope.pageLabel = [];
            var firstPage = 1; //开始页码
        var lastPage = $scope.resultMap.totalPages; //截止页码
        $scope.firstDot = true; //前面有点
        $scope.lastDot = true; //后面有点
        if($scope.resultMap.totalPages > 5){//如果总页数大于5页,显示部分页码
            if($scope.searchMap.pageNo <= 3){//如果当前页小于等于3
                lastPage = 5;//前5页
                $scope.firstDot = false; //前面没点
            }else if($scope.searchMap.pageNo >= $scope.resultMap.totalPages-2){//如果当前页大于等于最大页码-2
                firstPage = $scope.resultMap.totalPages-4;//显示当前页为中心的5页
                $scope.lastDot = false; //后面没点
            }else{
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        }else{
            $scope.firstDot = false; //前面无点
            $scope.lastDot = false; //后面无点
        }
        //循环产生页码标签
        for(var i = firstPage;i<=lastPage;++i){
            $scope.pageLabel.push(i);
        }
    }




    //添加搜索项  更改searchMap的值
    $scope.addSearchItem = function (key,value) {
        if(key == "category" || key == "brand"|| key == "price"){
            //如果用户点击的是分类或品牌
            $scope.searchMap[key] = value;
        }else{
            //用户点击的是规格
            $scope.searchMap.spec[key] = value;
        }
        //查询
        $scope.search();
    }

    //搜索条件撤销
    $scope.removeSearchItem = function (key) {
        if(key == "category" || key == "brand"|| key == "price"){
            //如果用户点击的是分类或品牌
            $scope.searchMap[key] = "";
        }else{
            //用户点击的是规格
           delete $scope.searchMap.spec[key];
        }
        //查询
        $scope.search();
    }

    
    //分页查询
    $scope.queryByPage = function (pageNo) {
        if(pageNo < 1 || pageNo > $scope.resultMap.totalPages){
            return;
        }
        $scope.searchMap.pageNo = pageNo;
        $scope.search();//查询
    }


    $scope.isTopPage = function () {
        if($scope.searchMap.pageNo == 1){
            return true;
        }
        return false;
    }

    $scope.isEndPage = function () {
        if($scope.searchMap.pageNo == $scope.resultMap.totalPages){
            return true;
        }
        return false;
    }
    $scope.isHighlightPage = function (page) {
        if($scope.searchMap.pageNo == page){
            return true;
        }
        return false;
    }

    $scope.sortSearch = function (sortField,sort) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;
        $scope.searchMap.pageNo = 1;
        $scope.search();//查询
    }

    //判断关键字是不是品牌
    $scope.keywordsIsBrand = function () {
        for(var i = 0;i < $scope.resultMap.brandList.length;++i){
            //A.indexOf(B)  A字符串是否包含B字符串  返回的是字符串的位置
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >= 0){
                return true;
            }
        }
        return false;
    }

    //接收参数 加载关键字
    $scope.loadkeywords = function () {
        $scope.searchMap.keywords = $location.search()["keywords"];
        $scope.search();//查询
    }
});