//服务层
app.service('seckillGoodsService',function($http){
    //读取列表数据绑定到表单中
    this.findList=function(){
        return $http.get('seckillGoods/findList.do');
    }
    //查询商品详情页
    this.findOne = function (id) {
        return $http.get("seckillGoods/findOenFromRedis.do?id=" + id);
    }

    //提交订单
    this.submitOrder=function(seckillId){
        return $http.get('seckillOrder/submitOrder.do?seckillId='+seckillId);
    }

    //提交订单
    this.commit=function(orderId){
        return $http.get('seckillOrder/saveOrderFromRedisToDb.do?orderId='+orderId);
    }

    this.timedelete = function (orderId) {
        return $http.get("seckillOrder/timedelete.do?orderId="+orderId);
    }
});
