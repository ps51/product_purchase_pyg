app.controller("contentController",function ($scope,contentService) {


    //所有广告列表
    $scope.contentList = [];
    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(function (response) {
            $scope.contentList[categoryId] = response;
        })
    }


    //搜索  传递参数 (关键字)
    $scope.search = function () {
        location.href = "http://localhost:8003/search.html#?keywords="+$scope.keywords;
    }

});