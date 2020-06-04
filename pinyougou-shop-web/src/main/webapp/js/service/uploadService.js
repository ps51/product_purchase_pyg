app.service("uploadService",function ($http) {

    //上传文件
    this.uploadFile = function () {
        var formData = new FormData();
        //file 文件上传框的name
        formData.append("file",file.files[0])
        return $http({
           url:"../upload.do",
           method:"post",
           data: formData,
           headers:{"Content-Type":undefined},
            //transformRequest:angular.identity 对整个表单进行二进制序列化
           transformRequest:angular.identity
        });
    }


});