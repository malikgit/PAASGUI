var app = angular.module('storage', []);

app.controller('storageCtrl', function($scope, $http) {

	$scope.field = {};
	$scope.regex = '\\d+M';

	/* ================CREATE NAS=================== */
	$scope.createNas = function() {
		console.log("inside createNas ");
		$scope.message = null;
		var nas = JSON.stringify($scope.field);
		var res = $http.post('/paas-gui/rest/freeNasService/createNas', nas);
		res.success(function(data, status, headers, config) {
			console.log("inside nas data value " + data);
			window.location.href = "storage.html";
		});
		res.error(function(data, status, headers, config) {
			alert("failure message : " + JSON.stringify({
				data : data
			}));
		});
	};// end of createNas

	/* =============DROP DOWN MENU FOR SERVICE NAME======== */
	$scope.getAllServiceName = function() {
		console.log("inside getAllServiceName ");
		var res = $http.get('/paas-gui/rest/applicationService/getAllServiceNameApplication');
		res.success(function(data, status, headers, config) {
			console.log("inside getAllServiceName data value " + data);
			$scope.listOfServices = data;
		});
		res.error(function(data, status, headers, config) {
			alert("failure message : " + JSON.stringify({
				data : data
			}));
		});
	};// end of createNas
});
