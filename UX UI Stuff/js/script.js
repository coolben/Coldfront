var app=angular.module("coldfront",[]);

var controllers={};

controllers.AppCtrl=function($scope){

	function sayHi(){
		alert("Hi");
	}
}


app.controllers(controllers);