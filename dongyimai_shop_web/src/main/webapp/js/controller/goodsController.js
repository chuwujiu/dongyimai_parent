 //控制层 
app.controller('goodsController' ,function($scope,$controller ,$location,typeTemplateService,itemCatService,goodsService,uploadService){
	
	$controller('baseController',{$scope:$scope});//继承
	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[],customAttributeItems:[]}};
    $scope.image_entity = {url:"",color:""};
    $scope.typeTemplate={};
    $scope.status=['未审核','已审核','审核未通过','关闭'];
    $scope.itemCatList=[];


    $scope.findOne=function(){
    	var id =$location.search()['id'];
    	if (id==null){
    		return;
		}
		goodsService.findOne(id).success(
			function (rs) {
				$scope.entity=rs;
				editor.html($scope.entity.goodsDesc.introduction);
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
				for (var i=0;i<$scope.entity.itemList.length;i++){
					$scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
				}
            }
		)
	}

	$scope.checkAttributeValue=function(specName,optionName){
    	var items=$scope.entity.goodsDesc.specificationItems;
    	var obj=$scope.searchObj(items,'attributeName',specName);
    	if (obj==null){
    		return false;
		} else {
    		if (obj.attributeValue.indexOf(optionName)>=0){
    			return true;
			} else {
    			return false;
			}
		}
	}

    $scope.findItemCatList=function(){
    	itemCatService.findAll().success(
    		function (rs) {
				for(var i=0;i<rs.length;i++){
					$scope.itemCatList[rs[i].id]=rs[i].name;
				}
            }
		)
	}

	$scope.add_image_entity=function(){
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}
    $scope.remove_image_entity=function(index){
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}
	 $scope.createItemList=function(){
		$scope.entity.itemList=[{spec:{},price:0,num:10000,status:'0',isDefault:'0'}]
    	var items = $scope.entity.goodsDesc.specificationItems;
		for (var i=0;i<items.length;i++){
			$scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
		}
	}

	addColumn=function(list,columnName,columnValues){
		var newList=[];
		for (var i=0;i<list.length;i++){
			var oldRow = list[i];
			for (var  j=0;j<columnValues.length;j++){
				var newRow  = JSON.parse(JSON.stringify(oldRow));
				newRow.spec[columnName]=columnValues[j];
				newList.push(newRow);
			}
		}
		return newList;
	}


	$scope.searchObj=function(list,key,value){
		for (var i=0;i<list.length;i++){
			if (list[i][key]==value){
				return list[i];
			}
		}
		return null;
	}

	$scope.updateSpecAttribute=function($event,name,value){
		var obj=$scope.searchObj($scope.entity.goodsDesc.specificationItems,'attributeName',name);
		if(obj!=null){
			if ($event.target.checked){
				obj.attributeValue.push(value);
			}else {
				obj.attributeValue.splice(obj.attributeValue.indexOf(value),1);
                if(obj.attributeValue.length == 0){
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(obj),1);
                }
			}
		}else{
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}
	}

	$scope.selectItemCat1List=function(){
		itemCatService.fingByParentId(0).success(
			function (rs) {
				$scope.itemCat1List=rs;
            }
		)
	}
	$scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {
		if (newValue){
			itemCatService.fingByParentId(newValue).success(
				function (rs) {
					$scope.itemCat2List=rs;
                    $scope.itemCat3List=null;
                }
			)
		}
    })
    $scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {
        if (newValue){
            itemCatService.fingByParentId(newValue).success(
                function (rs) {
                    $scope.itemCat3List=rs;
                }
            )
        }
    })
    $scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {
        if (newValue){
            itemCatService.findOne(newValue).success(
                function (rs) {
                    $scope.entity.goods.typeTemplateId=rs.typeId;
                }
            )
        }
    })
    $scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {
        if (newValue){
        	typeTemplateService.findOne(newValue).success(
        		function(rs){
                    $scope.typeTemplate=rs;
                    $scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);
                    if ($location.search()['id']==null){
                        $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);
                    }
				}
			)
			typeTemplateService.findSpecList(newValue).success(
				function (rs) {
					$scope.specList=rs;
                }
			);
        }
    })



	$scope.uploadFile=function(){
        uploadService.upload().success(
			function (response) {
				if (response.success){
					$scope.image_entity.url=response.message;
				}else {
					alert(response.message);
				}
            }
		)
	}

	$scope.add=function(){
		$scope.entity.goodsDesc.introduction=editor.html();
		if ($scope.entity.goods.id!=null) {
            goodsService.update( $scope.entity).success(
                function(response){
                    if(response.success){
                        location.href="goods.html";
                    }else{
                        alert(response.message);
                    }
                }
			)
		}else {
            goodsService.add($scope.entity).success(
                function (rs) {
                    if (rs.success){
                        alert('保存成功');
                        $scope.entity={goodsDesc:{itemImages:[],specificationItems:[]}};
                        editor.html('')
                    }else {
                        alert(rs.message);
                    }
                }
            )
		}

	}

	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	//
    // 	// //查询实体
    // 	// $scope.findOne=function(id){
    // 	// 	goodsService.findOne(id).success(
    // 	// 		function(response){
    // 	// 			$scope.entity= response;
    // 	// 		}
    // 	// 	);
    // 	// }

	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	