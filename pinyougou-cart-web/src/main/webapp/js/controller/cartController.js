//购物车控制层
app.controller('cartController',function($scope,cartService){
    //查询购物车列表
    $scope.findCartList=function(){
        cartService.findCartList().success(
            function(response){
                $scope.cartList=response;
                $scope.totalValue = cartService.sum($scope.cartList);
            }
        );
    }

    //添加商品到购物车
    $scope.addGoodsToCartList=function(itemId,num){
        cartService.addGoodsToCartList(itemId,num).success(
            function(response){
                if(response.success){
                    $scope.findCartList();//刷新列表
                }else{
                    alert(response.message);//弹出错误提示
                }
            }
        );
    }

    //获取当前用户的地址列表
    $scope.findAddressList = function () {
        cartService.findAddressList().success(function (response) {
            $scope.addressList = response;
            for(var i=0;i<response.length;++i){
                if($scope.addressList[i].isDefault == "1"){
                    $scope.address = $scope.addressList[i];
                    break;
                }
            }
        })
    }
    
    $scope.selectAddress = function (address) {
        $scope.address = address;
    }
    
    $scope.isSelectedAddress = function (address) {
        if($scope.address == address){
            return true;
        }
        return false;
    }

    //添加 & 修改
    $scope.add = function () {
        console.log($scope.addAddress);
        var regExp = /^[1][3,4,5,7,8][0-9]{9}$/;
        if(regExp.test($scope.addAddress.mobile)){
            if($scope.addAddress.id == null){
                //添加
                cartService.add($scope.addAddress).success(function (response) {
                    if(response.success){
                        $scope.findAddressList();//刷新列表
                    }else{
                        alert(response.message);//弹出错误提示
                    }
                });
            }else {
                cartService.update($scope.addAddress).success(function (response) {
                    if(response.success){
                        $scope.findAddressList();//刷新列表
                    }else{
                        alert(response.message);//弹出错误提示
                    }
                });
            }
        }else{
            alert("手机号格式不正确 - ");
        }
    }
    //删除
    $scope.dele = function(id){
        cartService.dele(id).success(function (response) {
            if(response.success){
                $scope.findAddressList();//刷新列表
            }else{
                alert(response.message);//弹出错误提示
            }
        });
    }
    //修改 *
    $scope.findOne = function(id){
        cartService.findOne(id).success(function (response) {
            $scope.addAddress = response;
        });
    };
    //别名选择
    $scope.Alias = function (Alias) {
        $scope.addAddress.alias = Alias;
    }


    $scope.order = {paymentType:'1'}; //订单对象

    //选择支付类型
    $scope.selectPayType = function (type) {
        $scope.order.paymentType = type;
    }


    //提交订单
    $scope.submitOrder = function () {
        $scope.order.receiverAreaName=$scope.address.address;//地址
        $scope.order.receiverMobile=$scope.address.mobile;//手机
        $scope.order.receiver=$scope.address.contact;//联系人

        cartService.submitOrder($scope.order).success(function (response) {
            if(response.success){
                //页面跳转
                if($scope.order.paymentType == "1"){
                    //如果是微信支付 跳转到支付页面
                    location.href = "pay.html";
                }else {
                    //如果是货到付款 跳转到提示页面
                    location.href = "paysuccess.html";
                }
            }else{
                location.href = "payfail.html";
            }
        });
    }
    
    
    $scope.address = function () {
        cartService.address(null,null).success(function (response) {
            $scope.provinces = response;
        })
    }

    $scope.$watch("addAddress.provinceId",function (newValue,oldValue) {
        cartService.address(newValue,"2").success(function (response) {
            $scope.cities = response;
        })
    });

    //查询三级商品分类列表
    $scope.$watch("addAddress.cityId",function (newValue,oldValue) {
        cartService.address(newValue,"3").success(function (response) {
            $scope.areas = response;
        })
    });
});
