var mycloudprovider = angular.module('getstartedApp', []);

mycloudprovider.controller('MainCtrl', function ($scope,srvShareData, $location,$http) {
	$scope.field = {};
	$scope.service = {};
	$scope.dataToShare = [];
	$scope.objToStore=[];
	 
	  
	/*$scope.service = {env:[]};
	$scope.env = [{envkey:'',envvalue:''}];*/
	
	//tab section index set and get method
	 
	$scope.sharedData = srvShareData.getData();
//	$scope.service = $scope.sharedData[0];
	$scope.service = $scope.sharedData[$scope.sharedData.length - 3]
//	$scope.indexVal = $scope.sharedData[1];
	$scope.indexVal = $scope.sharedData[$scope.sharedData.length - 2];
	
	
	
	if(angular.isUndefined($scope.indexVal) && $scope.indexVal == ''){
//		alert("if");
		this.tab = 1;
    }else if(angular.isNumber($scope.indexVal) && $scope.indexVal > 1){
    	//alert("else if");
    	console.log("comming to eslse if"+$scope.indexVal);
    	this.tab = $scope.indexVal;
    }else{
//    	alert("else");
    	this.tab = 1;
    }
	
	this.setTab = function (tabId) {
		this.tab = tabId;
    };

    this.isSet = function (tabId) {
        return this.tab === tabId;
    };
	
    
  //This method is used to share data within multiple controller
	  $scope.shareMyData = function (myValue,serviceObj) {
		console.log("myvalue "+myValue);
		console.log("serviceObj "+serviceObj);
	    /*$scope.dataToShare = myValue;
	    srvShareData.addData($scope.dataToShare);*/
	    $scope.objToStore = serviceObj;
	    srvShareData.addData($scope.objToStore);
	    console.log("srvShareData "+srvShareData);
	    window.location.href = "subnet-wizard2.html";
	  }	
	  
	  $scope.shareImgRegData = function (myValue,serviceObj) {
			console.log("myvalue "+myValue);
			console.log("serviceObj "+serviceObj);
		    /*$scope.dataToShare = myValue;
		    srvShareData.addData($scope.dataToShare);*/
		    $scope.objToStore = serviceObj;
		    srvShareData.addData($scope.objToStore);
		    console.log("srvShareData "+srvShareData);
		    window.location.href = "imageRegistry-wizard2.html";
		  }
	  
	//To show the modal
    $scope.showModal = false;
    $scope.toggleModal = function(){
        $scope.showModal = !$scope.showModal;
    };
    
    //To select All vpc by using tenant_id used in wizard 
    $scope.selectVpc = function() {
     	var response = $http.get('/paas-gui/rest/vpcService/getAllVPC');
     	response.success(function(data){
     		$scope.vpcObject = data;
     	});
     	response.error(function(data, status, headers, config) {
                 alert("Error in Fetching Data");
         });
     };
     
     // To get all Subnet details with current user 
    $scope.selectSubnetnew = function() {
    var vpcName = $scope.service.vpc_name;
    console.log("vpcName "+vpcName);
    var response = $http.post('/paas-gui/rest/subnetService/getSubnetNameByVpc',vpcName); 
   	response.success(function(data){
   		$scope.subnetObject = data;
   		console.log("data given>>>");
   	});
   	response.error(function(data, status, headers, config) {
        // alert("Error in Fetching subnet Data"+data);
       });
   };  // END OF selectSubnetnew 
    
   // To get respective cidr value of subnet
   $scope.selectCidrforSubent = function(subnet_name) {
	   
	   	 $scope.service.cidr;
	  		 angular.forEach($scope.subnetObject,function(item){
	   			   if(item.subnetName == subnet_name){
	   				  
	   				   $scope.service.cidr = item.cidr;
	   				   console.log("tag"+$scope.service.cidr);
	   			   	}
	  		 });
	}	// End of selectCidrforSubent
   
   	//To Get all imageRegistry by using tenant_id 
	$scope.selectImageRegistry = function() {
  	var response = $http.get('/paas-gui/rest/imageRegistry/getAllImageRegistry');
  	response.success(function(data){
  		$scope.imageRegObject = data;
  		console.log("data given");
  	});
  	response.error(function(data, status, headers, config) {
              alert("Error in Fetching Data of selectImageRegistry");
      });
  }; // End of selectImageRegistry           

  
  // TO GET THE CONTAINER TYPE 
  //NEED TO SHOW IN DROP-DOWN LIST OF CONTAINER_TYPE FIELD IN THE SERVICE.HTML PAGE
  $scope.getAllRelatedContainerTypes = function() {
  	console.log("getAllRelatedContainerTypes ");
	   var response = $http.get('/paas-gui/rest/containersService/getContainerTypesByTenantId');
	   response.success(function(data){
		$scope.containerObject = data;
	    	console.log("return data from db: "+$scope.image);
	    });
	    response.error(function(data, status, headers, config) {
	        alert("Error in Fetching Data");
	    });
	 };	// END OF getAllRelatedContainerTypes 
  
	 
	 // To get the Dockerhub data
	 //NEED TO SHOW IN DROP-DOWN LIST OF TAGS FIELD IN THE IMAGERESISTRY.HTML PAGE
	$scope.selectSummary = function(reponame) {
	$scope.reponames;
	//JSON.stringify(data);
	$scope.isImg=true;
	var response = $http.post('/paas-gui/rest/imageRegistry/getDockerHubRegistryTags',reponame);
	
	response.success(function(data){
		$scope.isImg=false;
		$scope.reponames = data;
		console.log("selectRepo >>>> "+$scope.reponames);
	});
	response.error(function(data, status, headers, config) {
		alert("Error in Fetching Application Summary"+data);
	});
	};	// End of selectSummary 
		     
	//if Environment Variable needed then uncomment below lines
	/* TO ADD DYNAMICA TABLE ROW FOR ENVIRONMENT VARIABLE */
	/*$scope.addNewEnvirnament = function() {
	$scope.env.push({envkey:'',envvalue:''});
	};*/
	/*TO REMOVE THE SPECIFIC NEWLY ADDED ENVIRONMENT VARIABLE */
	/* $scope.removeEnvirnament = function(index) {
	$scope.env.splice(index,1);
	};*/
	      
	$scope.createApplicationByName = function() {
	
	/*if(angular.isUndefined($scope.service.applicantionName && $scope.service.applicantionName == '')){
		alert("Please Enter valid Application name.");
	}else{*/
		 
	/*var res = $http.post('/paas-gui/rest/applicationService/createApplicationByName', $scope.service.applicantionName);
	console.log(userData);
		res.success(function(data, status, headers, config) {
			$scope.message = data;		   	  
			window.location = "html/applicantmain.html";
			   	 
		});
		res.error(function(data, status, headers, config) {
			alert("failure message : " + JSON.stringify({
				data : data
			}));
		});*/
			   	  
	/*}*///END OF ELSE
	};	//createApplicationByName
		     
	// function to process the form
	$scope.processForm = function() {
		          
	//if Environment variable needed then uncomment it
	/*angular.forEach($scope.env,function(value){
		$scope.service.env.push(value);            
		}) */      
	//Need to call application createApplicationByName() method and and then only call below
		var res = $http.post('/paas-gui/rest/applicationService/createApplicationByName', $scope.service.applicantionName);
		 
			res.success(function(data, status, headers, config) {
				$scope.message = data;
				$scope.service.appsId = data;
				userData = angular.toJson($scope.service);
				var res = $http.post('/paas-gui/rest/applicationService/addService', userData);
				console.log(userData);
					res.success(function(data, status, headers, config) {
						$scope.message = data;
						
						var mydata = [];
						mydata.push(1);
						srvShareData.addData(1);
						            
						mydata.push($scope.serviceObj);
						srvShareData.addData($scope.serviceObj);
						//console.log("mydata.length "+mydata.length);
						window.location.href = "getstarted_application.html";
						
						 
					 });
					 res.error(function(data, status, headers, config) {
//					 	alert("Error in storing Application Summary "+data);
					 });
				//window.location = "html/getstarted_application.html";
					 
				   	 
			});
			res.error(function(data, status, headers, config) {
				alert("failure message : " + JSON.stringify({
					data : data
				}));
			});	
	
	};	// End of processForm method
		      
	//check vpcname exist or not
	$scope.vpcValidation = function(vpc) {
		var res = $http.get('/paas-gui/rest/vpcService/checkVPC/'+vpc);
		res.success(function(data, status, headers, config) {
			if(data == 'success'){
				document.getElementById('errfn').innerHTML="data exist enter different name";
		    	document.getElementById("myvpcbtn").disabled = true;
		    }
		    else{
		    	  document.getElementById('errfn').innerHTML="";
		    	  /*
		    	  if(document.getElementById('aclerr').innerHTML =="")
		    	  document.getElementById("myvpcbtn").disabled = true;

		    	  else
		    	  document.getElementById("myvpcbtn").disabled =false;

		    	  if(document.getElementById('errfn').innerHtml && document.getElementById('aclerr').innerHtml =="")*/
		    	  document.getElementById("myvpcbtn").disabled =false;
		    }
		});
		res.error(function(data, status, headers, config) {
			alert("failure message: " + JSON.stringify({
		    data : data
			}));
		});
	}; //End of vpcValidation method
	
	// application validation 	    	    							  	
	$scope.applicationValidation = function(userName) {
	var res = $http.get('/paas-gui/rest/applicationService/checkingApplication/'+userName);
		res.success(function(data, status, headers, config) {
			if(data == 'success'){
		    	document.getElementById('apperror').innerHTML="data exist enter different name";
		    	document.getElementById("appbtn").disabled = true;
		    } else{
		    	document.getElementById('apperror').innerHTML="";
		    	document.getElementById("appbtn").disabled =false;
		    	}
		 });
		 res.error(function(data, status, headers, config) {
			 alert("failure message: " + JSON.stringify({
		    	data : data
		    	}));
		 });
	};	// End of applicationValidation method		
		    	    							  		
	// VPC REGISTRATION 		    	   							  		   
	$scope.regVpc = function() {
		if ($scope.vpcWizardForm.$valid) {
			console.log($scope.vpc);		    	    							  		  	 
		    var userData = JSON.stringify($scope.vpc);
		    	var res = $http.post('/paas-gui/rest/vpcService/addVPC', userData);
		    		console.log(userData);
		    	    res.success(function(data, status, headers, config) {
		    	    	$scope.message = data;
		    	    	window.location.href = "getstarted_application.html"; 
		    	    });
		    	    res.error(function(data, status, headers, config) {
		    	    	alert("failure message: " + JSON.stringify({
		    	    	data : data
		    	    	}));
		    	    });
		    	    }
	};// End of regVpc method		    	    							  		
		    	    							  	
	// POPULATE DATA TO TABLE 
	$scope.selectAcl = function() {
	var response = $http.get('/paas-gui/rest/aclService/getAllACL');
		response.success(function(data){
			$scope.aclObj = data;
		    console.log($scope.aclObj);		    	    	
		    console.log("data given");
		});
		response.error(function(data, status, headers, config) {
			alert("Error in Fetching Data");
		});
	};	// End of selectAcl method          
	
	   
		    	   	  	
	
		    	   	    		    	   	    
		    	    	
  });  
/**====================================== END OF CONTROLLER MainCtrl =====================================*/
 

//Controller2: createNewSubnetCtrl 
mycloudprovider.controller('createNewSubnetCtrl', function($scope, srvShareData,$location,$http) {
	$scope.sharedData = srvShareData.getData();
	//$scope.tabidval = $scope.sharedData[0];
	//$scope.serviceObj = $scope.sharedData[1];
	
	// To add subnet details into db used subnet-wizard2.html page
	$scope.regSubnet = function() {
		console.log($scope.subnet);
		
		console.log("tabidval "+$scope.tabidval +"serviceObj $scope.serviceObj");
		//var userData = JSON.stringify($scope.field);
		var userData = angular.toJson($scope.subnet);
		var res = $http.post('/paas-gui/rest/subnetService/addSubnet', userData);
		console.log(userData);
		res.success(function(data, status, headers, config) {
		 
			//console.log("success "+$scope.tabidval);
			var mydata = [];
            mydata.push(2);
            srvShareData.addData(2);
            
            mydata.push($scope.serviceObj);
            srvShareData.addData($scope.serviceObj);
            console.log("mydata.length "+mydata.length);
			window.location.href = "getstarted_application.html"; 
		});
		res.error(function(data, status, headers, config) {
			alert("Error in registering subnet  " +data );
		});
	}; // END OF regSubnet 	  
	
	$scope.getAllVpc = function() {     							  
     	var response = $http.get('/paas-gui/rest/vpcService/getAllVPC');     	
     	response.success(function(data){     	
     		$scope.vpcObj = data;
     		/*console.log(">>>>>>>  >>>  "+$scope.fields);*/
     	});
     	response.error(function(data, status, headers, config) {
                 alert("Error in Fetching subnet Data"+data);
         });
     };
	
     $scope.selectEnvironment_name = function() {
    		var response = $http.get('/paas-gui/rest/environmentTypeService/getAllEnvironmentType');
    		
    			response.success(function(data){
    				$scope.environment = data;
    			    console.log(">>>>>>> >>> "+$scope.environment);
    			});
    			response.error(function(data, status, headers, config) {
    				alert("Error in Fetching environment Data"+data);
    			});
    		};
    		
  $scope.selectACL_name = function() {
  var response = $http.get('/paas-gui/rest/aclService/getAllACL');
    			
  	response.success(function(data){
  		$scope.acl = data;
  		console.log(">>>>>>> >>> "+$scope.acl);
    });
    response.error(function(data, status, headers, config) {
    	alert("Error in Fetching environment Data"+data);
    });
   };    
			    			 // Add SUBNET validation 
   $scope.subnetValidation = function(subnet) {
	   console.log("subnet "+subnet);
	   var res = $http.get('/paas-gui/rest/subnetService/checkSubnet/'+subnet);
	   res.success(function(data, status, headers, config) {
		   if(data == 'success'){
			   document.getElementById('subneterror').innerHTML="data exist enter different name";
			   document.getElementById("mysubnetbtn").disabled = true;
    		} else{
    			document.getElementById('subneterror').innerHTML="";
    			document.getElementById("mysubnetbtn").disabled =false;
    		}
    	});
    	res.error(function(data, status, headers, config) {
    		alert("failure message: " + JSON.stringify({
    			data : data
    		}));
    	});

    };	// subnetValidation			
 

    
    $scope.sharDataToAclPage = function () {
		 
    
		/*$scope.objToStore = serviceObj;
    	srvShareData.addData($scope.objToStore);
    	console.log("srvShareData "+srvShareData);*/
    	window.location.href = "create-new-acl-wizard.html";
	}

	$scope.sharDataToEnvironmtPage = function () {
		 
		/*$scope.objToStore = serviceObj;
    	srvShareData.addData($scope.objToStore);
    	console.log("srvShareData "+srvShareData);*/
		window.location.href = "create-new-environment-wizard.html";
	}
	
	$scope.cancelSubnet = function () {
		 
		var mydata = [];
        mydata.push(2);
        srvShareData.addData(2);
        
        mydata.push($scope.serviceObj);
        srvShareData.addData($scope.serviceObj);
        console.log("mydata.length "+mydata.length);
		window.location.href = "getstarted_application.html"; 
	}
	
});	
/** ==================================== End of createNewSubnetCtrl ======================================*/

//Controller3: createNewImgRegCtrl 
mycloudprovider.controller('createNewImgRegCtrl', function($scope, srvShareData,$location,$http) {
	$scope.sharedData = srvShareData.getData();
	//$scope.tabidval = $scope.sharedData[0];
	//$scope.serviceObj = $scope.sharedData[1];
	  
	// ImageRegistry REG
	$scope.regImageRegistry = function() {
	console.log($scope.field);
	var userData = JSON.stringify($scope.field);
	var response = $http.post('/paas-gui/rest/imageRegistry/addImageRegistry', userData);
	JSON.stringify(response);
	console.log(userData);
		response.success(function(data, status) {		    	    							  			 
			var mydata = [];
			mydata.push(3);
			srvShareData.addData(3);
			            
			mydata.push($scope.serviceObj);
			srvShareData.addData($scope.serviceObj);
			console.log("mydata.length "+mydata.length);
			window.location.href = "getstarted_application.html"; 	    	   
		});
		response.error(function(data, status, headers, config) {
			alert("failure message: " + JSON.stringify({
				data : data
			 	}));
		});
	};	// End of regImageRegistry
    			
	// User name validation		    	    						  	
    $scope.registUsernameValidation = function(userName) {
    console.log("<<<<<< acl validation >>>>>>>>>" +name);
    var res = $http.get('/paas-gui/rest/imageRegistry/checkingUserName/'+userName);
    res.success(function(data, status, headers, config) {
    	if(data == 'success'){
    			document.getElementById('userNameerror').innerHTML="data exist enter different name";
    			document.getElementById("registrybtn").disabled = true;		    	    							   	    	
    	}
    	else{
    		document.getElementById('userNameerror').innerHTML="";
    		if(document.getElementById('registryerror').value ==''){		    	    							   				 
    			document.getElementById("registrybtn").disabled =false;
    		}
    	}
    
    });
    res.error(function(data, status, headers, config) {
    	alert("failure message: " + JSON.stringify({
    		data : data
    	}));
    });		    	    							  	 
   };	// END of user validation
    			
   // Image validation 
   $scope.registryValidation = function(name) {
   console.log("<<<<<< acl validation >>>>>>>>>" + name);
   var res = $http.get('/paas-gui/rest/imageRegistry/checkimageRegistry/' + name);
   res.success(function(data, status, headers, config) {
	   if (data == 'success') {
		   document.getElementById('registryerror').innerHTML = "data exist enter different name";
		   document.getElementById("registrybtn").disabled = true;
	   } else {
		   document.getElementById('registryerror').innerHTML = "";
		   if (document.getElementById('location').value
				   && document.getElementById('userNameerror').value != '') {
			   document.getElementById("registrybtn").disabled = false;
		   }
	   }
   });
   res.error(function(data, status, headers, config) {
	   console.log("failure message: " + JSON.stringify({
		   data : data
	   }));
   });
};	// End of registryValidation	
 

$scope.cancelImageRegistry = function () {
	 
	var mydata = [];
	mydata.push(3);
	srvShareData.addData(3);
	            
	mydata.push($scope.serviceObj);
	srvShareData.addData($scope.serviceObj);
	console.log("mydata.length "+mydata.length);
	window.location.href = "getstarted_application.html"; 	
}



});	
/** ================================== End of createNewImgRegCtrl =======================================*/


//Controller4: createNewACLCtrl 
mycloudprovider.controller('createNewACLCtrl', function($scope, srvShareData,$location,$http) {
	  
	
	//regAcl method is to Register Acl into data base using tenant id
	$scope.regAcl = function() {
	console.log($scope.acl);
	var userData = JSON.stringify($scope.acl);
	var res = $http.post('/paas-gui/rest/aclService/addACLRule', userData);
	console.log(userData);
		res.success(function(data, status, headers, config) {	    	   	  		  
			console.log("data "+data+" status "+status+" headers "+headers+" config "+config);
		    if(data!='failed'){
		    	console.log("login success");
		    		/*document.location.href = '/paas-gui/html/acl.html'; also working*/ 
		    	   	window.location.href = "subnet-wizard2.html";
		    }else{
		    	console.log("Login Error Please Enter Proper Details");
		    		/*document.location.href = '/paas-gui/html/acl_wizard.html'; also working*/
		    	   	window.location.href = "create-new-acl-wizard.html";
		    }
		});
		res.error(function(data, status, headers, config) {
			alert("failure message: " + JSON.stringify({
		    data : data
		    }));
		});
	};	// End of regAcl method    	
	
	
	//ACL Server side validation
    $scope.aclValidation = function(acl) {
	  	 console.log("<<<<<< acl validation >>>>>>>>>" +acl);
	  	  var res = $http.get('/paas-gui/rest/aclService/checkAcl/'+acl);
	  	  res.success(function(data, status, headers, config) {
	  		  
	  		if(data == 'success'){
	   	    	document.getElementById('aclerror').innerHTML="data exist enter different name";
	   	    	document.getElementById("myaclbtn").disabled = true;
	   	    	
	   		  }
	   		  else{
	   			 document.getElementById('aclerror').innerHTML="";
	   			document.getElementById("myaclbtn").disabled =false;
	   		  }
 	  		 
	  	  });
	  	  res.error(function(data, status, headers, config) {
	  	    alert("failure message: " + JSON.stringify({
	  	      data : data
	  	    }));
	  	  });
	  	 
	  	}; //End of aclValidation method
	  	

});	
/** ==================================== End of createNewACLCtrl =========================================*/



//Controller5: createNewEnvironmemt 
mycloudprovider.controller('createNewEnvironmemt', function($scope, srvShareData,$location,$http) {
	  
	// ENVIRONMENT TYPES REG	    	   	    
	$scope.regEnvironmentTypes = function() {	    	   	    	 
	console.log($scope.field);
	var userData = JSON.stringify($scope.field);
	var res = $http.post('/paas-gui/rest/environmentTypeService/insertEnvironmentType', userData);
	console.log(userData);
		res.success(function(data, status, headers, config) {
			if(data =='success'){
		    	console.log("login success");
		    	/*document.location.href = '/paas-gui/html/acl.html'; also working*/ 
		    	window.location.href = "subnet-wizard2.html";
			}else{
		    	console.log("Login Error Please Enter Proper Details");
		    	/*document.location.href = '/paas-gui/html/acl_wizard.html'; also working*/
		    	window.location.href = "create-new-environment-wizard.html";
		    }
		});
		res.error(function(data, status, headers, config) {
			alert("failure message: " + JSON.stringify({
		    	data : data
		    }));
		});
	}; // END OF THE INSERT ENVIRONMENT TYPE 
	
	// Add Environments validation
	   $scope.environmentTypesValidation = function(environment) {
	   	
	   	var res = $http.get('/paas-gui/rest/environmentTypeService/checknvironmentType/'+environment);

	   	res.success(function(data, status, headers, config) {
	   		
	  		  if(data == 'success'){
	  	    	document.getElementById('environmenterr').innerHTML="data exist enter different name";
	  	    	document.getElementById("myenvbtn").disabled = true;
	  		  }
	  		  else{
	  			 document.getElementById('environmenterr').innerHTML="";
	  			document.getElementById("myenvbtn").disabled =false;
	  		  }
	   	});
	   	res.error(function(data, status, headers, config) {
	   		alert("failure message: " + JSON.stringify({
	   			data : data
	   		}));
	   	});
	   }; //END OF THE VALIDATION FOR ENVIRONMENT TYPES NAME 
	  	

});	
/** ======================================= End of createNewEnvironmemt ==================================*/
 


mycloudprovider.service('srvShareData', function($window) {
    var KEY = 'App.SelectedValue';

    var addData = function(newObj) {
		var mydata = $window.sessionStorage.getItem(KEY);
            if (mydata) {
                mydata = JSON.parse(mydata);
            } else {
                mydata = [];
            }
    	//mydata = [];
        mydata.push(newObj);
        $window.sessionStorage.setItem(KEY, JSON.stringify(mydata));
    };

    var getData = function(){
        var mydata = $window.sessionStorage.getItem(KEY);
        if (mydata) {
            mydata = JSON.parse(mydata);
        }
        return mydata || [];
    };

    return {
        addData: addData,
        getData: getData
    };
});
























