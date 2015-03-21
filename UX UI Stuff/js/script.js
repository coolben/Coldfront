var app=angular.module("coldfront",[]);

app.controller('ToolbarController',  function($scope) {
 	$scope.search=true;
 	$scope.searchfunction=function(){
 		console.log("SD");
		$scope.search=true;
	}

 	$scope.opensearch=function(){
		$scope.search=false;
	}
 });


app.controller('PatientController',  function($scope) {
 	$scope.search=true;
 	$scope.searchfunction=function(){
		$scope.search=true;
	}

 	$scope.opensearch=function(){
		$scope.search=false;
	}
 });

