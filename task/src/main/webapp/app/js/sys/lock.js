App.controller('LockFormController', ['$scope', '$http', '$state', function ($scope, $http, $state) {
    // 拉取数据
    $scope.account = {};
    // 错误信息
    $scope.authMsg = '';
    $scope.lock = function () {
        $scope.authMsg = '';
        if ($scope.lockForm.$valid) {
            var oldpassword = $scope.oldpassword;
            oldpassword = window.btoa(oldpassword);
            var newpassword1 = $scope.newpassword1;
            newpassword1 = window.btoa(newpassword1);
            var newpassword2 = $scope.newpassword2;
            newpassword2 = window.btoa(newpassword2);

            if(newpassword1!=newpassword2){
                $("#authMsg").html("两次密码输入不一致");
            }else {
                var pData = {
                    oldpassword: oldpassword,
                    newpassword: newpassword1
                };
                angularParamString($http); //解决post提交接收问题，json方式改为string方式
                $http({
                    url: '/sys/user/lock.shtml',
                    method: 'POST',
                    data: pData
                }).success(function (result) {
                    if (result.success) {
                        $state.go('page.login');
                    } else {
                        $("#authMsg").html(result.info);
                    }
                }).error(function (result) { //提交失败
                        $("#authMsg").html("修改密码失败");
                    }
                );
            }
        }
    };

}]);