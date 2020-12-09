app.controller('payController',function ($scope,payService) {
    $scope.createNative=function () {
        payService.createNative().success(
            function (rs) {
                $scope.money=(rs.total_fee/100).toFixed(2);
                $scope.out_trade_no=rs.out_trade_no;
                var qr = new QRious({
                    element:document.getElementById('qrious'),
                    size:250,
                    level:'H',
                    value:rs.qrcode
                })
            }
        )
    }
})