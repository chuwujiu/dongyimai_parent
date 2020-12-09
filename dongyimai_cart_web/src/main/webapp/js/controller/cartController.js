app.controller('cartController',function ($scope,cartService) {

    $scope.order={paymentType:'1'};
    $scope.address={};

    $scope.selectPayType=function(type){
       $scope.order.paymentType=type;
    }

    $scope.submitOrder=function(){
        $scope.order.receiverAreaName=$scope.address.address;
        $scope.order.receiverMobile=$scope.address.mobile;
        $scope.order.receiver=$scope.address.contact;
        cartService.submitOrder($scope.order).success(
            function (rs) {
               if (rs.success){
                   if ($scope.order.paymentType=='1'){
                       location.href="pay.html";
                   } else {
                       location.href="paysuccess.html";
                   }
               }else {
                   alert(rs.message);
               }
            }
        )
    }

    $scope.selectAddress=function(address){
        $scope.address=address;
    }
    $scope.isSelectedAddress=function(address){
        if (address==$scope.address){
            return true;
        } else {
            return false;
        }
    }

    $scope.findCartList=function () {
        cartService.findCartList().success(function (rs) {
            $scope.cartList=rs;
            $scope.totalValue=cartService.sum($scope.cartList);
        })
    }

    $scope.findAddressList=function(){
        cartService.findAddressList().success(
            function (rs) {
                $scope.addressList=rs;
                for (var i=0;i<$scope.addressList.length;i++){
                    if ($scope.addressList[i].isDefined=='1'){
                        $scope.address=$scope.addressList[i];
                        break;
                    }
                }
            }
        )
    }


    $scope.addGoodsToCartList=function (itemId,num) {
        cartService.addGoodsToCartList(itemId,num).success(
            function (rs) {
               if (rs.success){
                   $scope.findCartList();
               } else {
                   alert(rs.message);
               }
            }
        )
    }
})