app.service('brandService',function ($http) {

    this.findByPage= function(searchEntity,pageNum,pageSize){
        return $http.post("../brand/findPage.do?pageNum=" + pageNum + "&pageSize=" + pageSize, searchEntity);
    }

    this.add = function(entity){
        return $http.post("../brand/add.do", entity);
    }

    this.update = function(entity){
        return $http.post("../brand/update.do",entity);
    }

    this.findOne = function(id){
        return $http.get("../brand/findOne.do?id=" + id);
    }

    this.delete = function(ids){
        return $http.get("../brand/delete.do?ids=" + ids)
    }

    this.selectOptions = function(){
        return $http.get("../brand/selectOptions.do");
    }


    this.selectOptionsList=function () {
        return $http.get("../brand/selectOptionList.do");
    }





})