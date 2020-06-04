//购物车服务层
app.service('cartService',function($http){
    //购物车列表
    this.findCartList=function(){
        return $http.get('cart/findCartList.do');
    }

    //添加商品到购物车
    this.addGoodsToCartList=function(itemId,num){
        return $http.get('cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);
    }

    //求合计
    this.sum = function (cartList) {
        var totalValue = {totalNum:0,totalMoney:0}
        for(var i=0;i<cartList.length;++i){
            var cart = cartList[i];//购物车对象
            for(var j=0;j<cart.orderItemList.length;++j){
                var orderItem = cart.orderItemList[j]; //购物车明细
                totalValue.totalNum += orderItem.num; //累计数量
                totalValue.totalMoney += orderItem.totalFee; //累计金额
            }
        }
        return totalValue;
    }

    //获取当前登录用户的收货地址
    this.findAddressList = function () {
        return $http.get("address/findListByLoginUser.do");
    }




    //收货地址操作
    //添加
    this.add = function (address) {
        return $http.post("address/add.do",address);
    }
    this.dele = function (id) {
        return $http.get("address/delete.do?id="+id);
    }

    this.findOne = function (id) {
        return $http.get("address/findOne.do?id="+id);
    }

    this.update = function (address) {
        return $http.post("address/update.do",address);
    }
    this.submitOrder = function (order) {
        return $http.post("order/add.do",order);
    }

    this.address = function (id,index) {
        return $http.get("order/address.do?id="+id+"&index="+index);
    }
    this.saveSpikeAddress = function (seckillOrder) {
        return $http.post("seckillOrder/saveSpikeAddress.do",seckillOrder)
    }
});
