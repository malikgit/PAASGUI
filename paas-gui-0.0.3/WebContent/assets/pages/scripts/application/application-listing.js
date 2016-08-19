var applicationWizards = angular.module('applicationListing', []);

applicationWizards.controller('MainCtrl', function ($scope,$http) {
	
	$scope.paasUserRegister = {};
	$scope.sysaidIp="";
	$scope.openChatWindow= function() {		
		console.log("userName "+$scope.paasUserRegister.tenant_name+" email "+$scope.paasUserRegister.email+" sysaid ip "+$scope.sysaidIp);
	    var myWindow = window.open($scope.sysaidIp+"ChatEnduserWelcomePage.jsp?queue=1&notAddingIndexJSP=true&email="+$scope.paasUserRegister.email+"&userName="+$scope.paasUserRegister.tenant_name,"chatBox","&quot;&quot;,&quot;width=400px,height=660px,toolbar=no,resizable=yes,scrollbars=yes,directories=no,menubar=no,location=no&quot;");
	}

	
    //For sysaid chat box functionality
	$scope.getLoginDetails = function() {
   	var response = $http.get('/paas-gui/rest/registerAndLoginService/getLoginDetails');
   	response.success(function(data){
   		$scope.paasUserRegister = data;
   		console.log("data given");
   	});
   	response.error(function(data, status, headers, config) {
               alert("Error in Fetching Data");
       });
   };  /**END OF THE SELECT THE DATA FROM ENVIRONMENT TYPE */
     
   $scope.getSysaidIp = function() {
	   $scope.sysaidIp;
	   	var response = $http.get('/paas-gui/rest/registerAndLoginService/getSysaidIp');
	   	response.success(function(data){
	   		$scope.sysaidIp = data;
	   	 
	   	});
	   	response.error(function(data, status, headers, config) {
	               alert("Error in Fetching Data");
	       });
	   };
	   
  });   /*================end of controllers===================*/


