app.controller("brabdController",function ($scope,$controller,brandService) {

    $controller("baseController",{$scope:$scope});

    /*查询品牌表*/
    $scope.findAll = function () {
        brandService.findAll().success(function (response) {
            $scope.brandList = response;
        })
    }

    //分页
    $scope.findPage = function (page,size) {
        brandService.findPage(page,size).success(function (response) {
            //当前页数据
            $scope.brandList = response.rows;
            //更新总记录数
            $scope.paginationConf.totalItems = response.total;
        });
    }

    //新增
    $scope.saveBrand = function () {
        var object = null;
        if($scope.brand.id!=null){
            object = brandService.update($scope.brand)
        }else{
            object = brandService.add($scope.brand)
        }
        object.success(function (response) {
            if(response.success){
                $scope.paginationConf.currentPage = 9999;
                //刷新
                $scope.reloadList();
            }else{
                alert(response.message);
            }
        });
    }

    //根据id查询一个品牌信息
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.brand = response;
        });
    }



    $scope.deleteBrands = function () {
        brandService.delete($scope.selectIds).success(function (response) {
            if(response.success){
                $scope.reloadList();
            }else{
                alert(response.message);
            }
        })
    };

    $scope.searchBrand = {};
    //搜索查询
    $scope.search = function (page,size) {
        brandService.search(page,size,$scope.searchBrand).success(function (response) {
            //当前页数据
            $scope.brandList = response.rows;
            //更新总记录数
            $scope.paginationConf.totalItems = response.total;
        });
    }

});
