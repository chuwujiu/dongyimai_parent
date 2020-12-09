app.controller('indexController',function ($scope,$controller,loginService) {

    $scope.showLoginName=function () {
        loginService.loginName().success(
            function (rs) {
                $scope.loginName=rs.loginName;
            }
        )
    }
})