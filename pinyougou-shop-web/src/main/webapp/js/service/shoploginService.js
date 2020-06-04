//服务层
app.service('shoploginService',function($http){
    //显示用户名称
    this.shoplogin = function () {
        return $http.get("../shoplogin/name.do");
    }
});