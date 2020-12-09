app.controller('searchController',function($scope,$location,searchService){
    //搜索
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':'1','pageSize':'20','sort':'','sortField':''};

    $scope.keyboardSearch = function($event){
        if($event.keyCode == 13){ // 回车
            $scope.search();
        }
    }
    $scope.loadKeyWords=function(){
        $scope.searchMap.keywords=$location.search()['keywords'];
        $scope.search();
    }

    $scope.sortSearch=function(sortField,sort){
        $scope.searchMap.sort=sort;
        $scope.searchMap.sortField=sortField;
        $scope.search();
    }

    $scope.keywordIsBrand=function(){
        for (var i=0;i<$scope.resultMap.brandList.length;i++){
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    }

    $scope.queryByPage=function(pageNo){
        if (pageNo<1){
            pageNo=1;
        }
        if (pageNo>$scope.resultMap.totalPages){
            pageNo=$scope.resultMap.totalPages;
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    }

    $scope.isTopPage=function(){
        if ($scope.searchMap.pageNo==1){
            return true;
        }else {
            return false;
        }
    }

    buildPageBox=function(){
        $scope.pageBox=[];
        var maxPageNo=$scope.resultMap.totalPages;
        var firstPage=1;
        var lastPage=maxPageNo;
        $scope.firstDot=true;
        $scope.lastDot=true;
        if ($scope.resultMap.totalPages>5){
            if ($scope.searchMap.pageNo<=3){
                lastPage=5;
                $scope.firstDot=false;
            }else if ($scope.searchMap.pageNo>=lastPage-2){
                firstPage=maxPageNo-4;
                $scope.lastDot=false;
            }else {
                firstPage=$scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
            }
        }else {
            $scope.firstDot=false;
            $scope.lastDot=false;
        }
        for (var i=firstPage;i<=lastPage;i++){
            $scope.pageBox.push(i);
        }
    }

    $scope.addSearchItem=function(key,value){
        if (key=="category"||key=="brand"||key=='price'){
            $scope.searchMap[key]=value;
        }else {
            $scope.searchMap.spec[key]=value;
        }
        $scope.searchMap.pageNo=1;
        $scope.search();
    }
    $scope.removeSearchItem=function(key){
        if (key=="category"||key=="brand"||key=='price'){
            $scope.searchMap[key]="";
        } else {
            delete $scope.searchMap.spec[key];
        }
        $scope.searchMap.pageNo=1;
        $scope.search();
    }

    $scope.isPage=function(page){
        if (parseInt(page)==parseInt($scope.searchMap.pageNo)){
            return true;
        } else {
            return false;
        }
    }

    $scope.search=function(){
        $scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
        $scope.searchMap.pageSize=parseInt($scope.searchMap.pageSize);

        searchService.search( $scope.searchMap ).success(
            function(rs){
                $scope.resultMap=rs;//搜索返回的结果
                buildPageBox();
            }
        );
    }
});