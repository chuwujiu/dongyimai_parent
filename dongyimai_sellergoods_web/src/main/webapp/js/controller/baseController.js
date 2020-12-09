app.controller("baseController",function ($scope) {
    $scope.selectIds=[];
    $scope.searchEntity={};
    $scope.updateSelection=function($event,id){
        if($event.target.checked){
            $scope.selectIds.push(id);
        }else{
            var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx,1);
        }
    }
    $scope.paginationConf={
        currentPage:1,
        totalItems:10,
        itemsPerPage:10,
        perPageOptions:[10,20,30,40,50],
        onChange:function () {
            $scope.reloadList();
        }
    }

    $scope.reloadList=function () {
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage)
    }
})