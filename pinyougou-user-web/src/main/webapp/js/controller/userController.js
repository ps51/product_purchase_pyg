 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	

    $scope.reg = function () {
        //比较两次的密码是否一致
        if($scope.entity.password != $scope.password){
            alert("密码不一致 - ");
            $scope.entity.password = "";
            $scope.password = "";
            return;
        }
        userService.add($scope.entity,$scope.smscode).success(function (response) {
            alert(response.message);
        })
    }
    
    $scope.sendCode = function () {
        if($scope.entity.phone == null || $scope.entity.phone == ""){
            alert("请填写手机号码");
            return;
        }

        userService.sendCode($scope.entity.phone).success(function (response) {
            alert(response.message);
        })
    }
});	
