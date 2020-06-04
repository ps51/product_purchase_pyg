 //控制层 
app.controller('itemController' ,function($scope,$http){

	$scope.specificationItems = {}; //存储用户选择的规格

	//数量加减
	$scope.addNum = function(x){
		if(($scope.num + x) >= 1){
			$scope.num += x;
		}
	}

	//用户选择规格
	$scope.selectSpecification = function(key,value){
		$scope.specificationItems[key] = value;
		searchSku();
	}

	//判断某规格是否被选择
    $scope.isSelected = function(key,value){
		if($scope.specificationItems[key] == value){
			return true;
		}
		return false;
	}

	$scope.sku = {};

	$scope.loadSku = function(){
		$scope.sku = skuList[0];
		$scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
	}


	//匹配两个对象是否相等
	matchObject = function(map1,map2){
		for(var key in map1){
			if(map1[key] != map2[key]){
				return false;
			}
		}
		return true;
	}


	searchSku = function(){
		for(var i = 0;i < skuList.length;++i){
			if(matchObject(skuList[i].spec,$scope.specificationItems)){
				$scope.sku = skuList[i];
				return;
			}
		}
		$scope.sku = {id:0,title:'暂无商品 - ',price:0};
	}


	//添加购物车
	$scope.addTocart = function(){
		$http.get(
			"http://localhost:8006/cart/addGoodsToCartList.do?itemId="+$scope.sku.id+"&num="+$scope.num,
			{'withCredentials':true}  //同意跨域操作cookie
		).success(function(response){
			if(response.success){
				location.href="http://localhost:8006/cart.html";
			}else{
				alert(message);
			}
		});
	}
    
});	
