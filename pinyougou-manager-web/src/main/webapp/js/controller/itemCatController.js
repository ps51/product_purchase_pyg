 //控制层 
app.controller('itemCatController' ,function($scope,$controller,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}


	//查询实体 
	$scope.findOne=function(id){

		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//$scope.typeTemplateIds();
				var str = "{\"id\":0,\"text\":\"" + response.typeId+"\"}";
				$("#types").trigger("change");
				$("#types").select2("data",JSON.parse(str));

			}
		);
	};
	
	//保存 
	$scope.save=function(){
		$scope.entity.typeId = $scope.entity.typeId.text;
		var serviceObject;//服务层对象
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			$scope.entity.parentId = $scope.parentId;
			serviceObject=itemCatService.add( $scope.entity);//增加
		}
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.selectList({id:$scope.parentId,name:$scope.name});//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框
		if($scope.selectIds != null){
			itemCatService.dele( $scope.selectIds ).success(
				function(response){
					if(response.success){
						$scope.selectList({id:$scope.parentId,name:$scope.name});
						$scope.selectIds=[];
					}else{
						alert(response.message);
						$scope.selectList({id:$scope.parentId,name:$scope.name});
					}
				}
			);
		}

	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}


	//根据上级分类id查询商品分类列表
	$scope.findByParentId = function (parentId) {
		itemCatService.findByParentId(parentId).success(function (response) {
			$scope.parentId = parentId;
			$scope.list=response;
		})
	}

	$scope.grade = 1;
	$scope.name = null;
	$scope.setGrade = function (value) {
		$scope.grade = value;
	}

	$scope.selectList = function (p_entity) {
		if($scope.grade == 1){
			$scope.entity_1 = null;
			$scope.entity_2 = null;
		}
		if($scope.grade == 2){
			$scope.entity_1 = p_entity;
			$scope.entity_2 = null;
		}
		if($scope.grade == 3){
			$scope.entity_2 = p_entity;
		}
		$scope.name = p_entity.name;
		$scope.findByParentId(p_entity.id)

	}


	$scope.parentId = 0;

	$scope.typeIds={data:[]};//规格列表
	
	$scope.typeTemplateIds = function () {
		typeTemplateService.findAll().success(function (response) {
			var str =  "[";
			for (var i =0;i<response.length;++i){
				/*$scope.typeIds = {data:[response[i].id]};*/
				str += "{\"id\":" + (i+1) + ",\"text\":\"" + [response[i].id]+"\"}";
				if(i+1 != response.length){
					str += ",";
				}
			}
			str += "]";
			$scope.typeIds={data:JSON.parse(str)};
		});
	}
    
});	
