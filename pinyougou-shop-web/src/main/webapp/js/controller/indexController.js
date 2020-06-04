app.controller("indexController",function ($scope,shoploginService) {
    //显示当前用户名
    $scope.shoploginName = function () {
        shoploginService.shoplogin().success(function (response) {
            $scope.loginName = response.loginName;
        });
    }
});