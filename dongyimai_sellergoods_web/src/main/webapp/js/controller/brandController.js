app.controller("brandController",function ($scope,brandService,$controller) {
    $controller('baseController',{$scope:$scope});//继承
    $scope.search=function(page,rows){
        brandService.findByPage($scope.searchEntity,page,rows).success(
            function (rs) {
                $scope.paginationConf.totalItems=rs.total;
                $scope.list=rs.rows;
            }
        )
    }

    $scope.findPage=function(page,rows){
        brandService.findByPage($scope.searchEntity,page,rows).success(
            function (rs) {
                $scope.paginationConf.totalItems=rs.total;
                $scope.list=rs.rows;
            }
        )
    }
    $scope.dele=function () {
        brandService.delete($scope.selectIds).success(
            function (rs) {
                if (rs.success){
                    $scope.reloadList();
                    $scope.selectIds=[];
                }else {
                    alert(rs.message);
                }
            }
        )
    }
    $scope.add=function () {
        if ($scope.entity.id==null){
            brandService.add($scope.entity).success(
                function (rs) {
                    if (rs.success){
                        $scope.reloadList();
                    } else {
                        alert(rs.message);
                    }
                }
            )
        } else {
            brandService.update($scope.entity).success(
                function (rs) {
                    if (rs.success){
                        $scope.reloadList();
                    } else {
                        alert(rs.message);
                    }
                }
            )
        }

    }
    $scope.findOne=function (id) {
        brandService.findOne(id).success(
            function (rs) {
                $scope.entity=rs;
            }
        )
    }
})