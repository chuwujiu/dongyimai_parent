app.controller("pageController",function ($scope ,$http) {



    $scope.specItem={}//用户所选的规格

    $scope.selectItem=function(name,value){
        $scope.specItem[name]=value;
        searchSku();
    }

    $scope.addToCart=function(){
        $http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='+$scope.sku.id+'&num='+$scope.num,{withCredentials:true}).success(
            function (rs) {
                if (rs.success){
                    location.href='http://localhost:9107/cart.html';
                } else {
                    alert(rs.message);
                }
            }
        )
    }

    $scope.loadSku=function(){
        $scope.sku=skuList[0];
        $scope.specItem=JSON.parse(JSON.stringify($scope.sku.spec))
    }

    matchObject=function(map1,map2){
        for(var k in map1){
            if (map1[k]!=map2[k]){
                return false;
            }
        }
        return true;
    }
    searchSku=function(){
        for (var i=0;i<skuList.length;i++){
            if (matchObject(skuList[i].spec,$scope.specItem)){
                $scope.sku=skuList[i];
                return;
            }
        }
    }



    $scope.isSelected=function(name,value){
        if ($scope.specItem[name]==value){
            return true;
        } else {
            return false;
        }
    }


    $scope.addNum=function (num) {
        $scope.num=$scope.num+num;
        if ($scope.num<1){
            $scope.num=1;
        }
    }
})