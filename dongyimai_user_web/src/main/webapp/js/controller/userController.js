 //用户表控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	
	
	$controller('baseController',{$scope:$scope});//继承
	$scope.entity={};


    $scope.showName=function () {
        userService.showName().success(
            function (rs) {
                $scope.usName=rs.loginName;
            }
        )
    }

	$scope.sendCode=function(){
		if ($scope.entity.phone==null||$scope.entity.phone==''){
			alert("请输入手机号")
			return;
		}
		userService.sendCode($scope.entity.phone).success(
			function (rs) {
				alert(rs.message);
            }
		)
	}

	$scope.regist=function(){
		if ($scope.entity.username==''||$scope.entity.username==null){
			alert("请输入用户名");
			return;
		}
        if ($scope.entity.password==''||$scope.entity.password==null){
            alert("密码不能为空");
            return;
        }
        if ($scope.repassword!=$scope.entity.password) {
        	alert("密码输入不一致");
        	return;
		}
		userService.add($scope.entity,$scope.code).success(
			function (rs) {
				if (rs.success){
					alert("注册成功")
				} else {
					alert(rs.message);
				}
            }
		)
    }
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		userService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		userService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		userService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=userService.update( $scope.entity ); //修改  
		}else{
			serviceObject=userService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		userService.dele( $scope.selectIds ).success(
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
		userService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	