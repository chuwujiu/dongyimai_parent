app.service("uploadService",function($http){

    this.upload = function(){

        var formData = new FormData();
        formData.append("file",file.files[0]);

        return $http({
            method:'POST',
            url:"../upload.do",
            headers: {'Content-Type':undefined}, // 将表单的提交类型 multipart/form-data
            transformRequest:angular.identity,
            data:formData
        })
    }

})