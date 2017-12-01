App.controller('LoginFormController', ['$scope', '$http', '$state', function ($scope, $http, $state) {
    // 拉取数据
    $scope.account = {};
    // 错误信息
    $scope.authMsg = '';
    $scope.login = function () {
        $scope.authMsg = '';
        if ($scope.loginForm.$valid) {
            var pData = {
                username: $scope.user.username,
                password: $scope.user.password
            };
            angularParamString($http); //解决post提交接收问题，json方式改为string方式
            $http({
                url: '/login/login.shtml',
                method: 'POST',
                data: pData
            }).success(function (result) {
                    if(result.success){
                        $state.go('app.index');
                    }else{
                        $("#authMsg").html(result.info);
                    }
                }).error(function (result) { //提交失败
                    $("#authMsg").html("登录失败");
                }
            );
        }
        else {
            $scope.loginForm.username.$dirty = true;
            $scope.loginForm.account_password.$dirty = true;
        }
    };
}]);