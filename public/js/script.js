var app=angular.module("coldfront",['ng-polymer-elements'])
.config( [
    '$compileProvider',
    function( $compileProvider )
    {   
        $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|file):/);
        // Angular before v1.2 uses $compileProvider.urlSanitizationWhitelist(...)
    }
]);

app.controller('ToolbarController',  function($scope) {

 	$scope.search=true;
 	$scope.searchfunction=function(){

		$scope.search=true;
	}

 	$scope.opensearch=function(){
		$scope.search=false;
	}
 });


app.controller('PatientController',  function($scope,$http) {
	var patients = new Array();
	var currentPatient=-1;
	var filterArray=["7 South", "8 North", "Recent", "All"];
	var d=document.querySelector('paper-dialog');

	patients.push({"id":0,"from":"7 South","name":"Akshar Rawal", "MRN":"141542","dob": "02/14/1991","primary":"Headache","physician":"Dr. Braunstein"});
	patients.push({"id":1,"from":"7 South","name":"Ben Nguyen",  "MRN":"142324", "dob":"05/31/1991","primary":"Stomachache","physician":"Dr. Braunstein"});
	patients.push({"id":2,"from":"8 North","name":"Carl Saldanha", "MRN":"153234", "dob":"12/06/1991","primary":"Sprain","physician":"Dr. Braunstein"});
	patients.push({"id":3,"from":"8 North","name":"Erik Reinerstein","MRN":"153466","dob": "01/24/1991","primary":"Burn","physician":"Dr. Braunstein"});
	patients.push({"id":4,"from":"7 South","name":"Vu Nguyen","MRN":"153466", "dob":"08/10/1991","primary":"Bite","physician":"Dr. Braunstein"});

    var responsePromise = $http({
      method: 'GET',
      url: "https://taurus.i3l.gatech.edu:8443/HealthPort/fhir/Patient?format=json",
      headers: {'Content-Type':  "application/x-www-form-urlencoded; charset=utf-8"}
    });

    responsePromise.success(function(data, status, headers, config)  {
      console.log(data);
    });
    responsePromise.error(function(data, status, headers, config) {
      alert("AJAX failed!");
    });

	//set up scope variables
	$scope.filter=3;
	$scope.patients=patients;
	console.log($scope.filter);
	$scope.handleFilter=function(){
		$scope.filter=data;
	}
	$scope.populatePatients = function () {
		var populate=new Array();
		switch(filterArray[$scope.filter]){
			case 'All':
						populate=patients;
						break;
			case 'Recent':
						//correct logic to be added later
						populate=patients;
						break;						
			default:
						for(var i=0;i<patients.length;i++)
							if(patients[i]['from']==filterArray[$scope.filter])
								populate.push(patients[i]);
						break;
		}
		$scope.patients=populate;
		/*
	    //Create a HTML Table element.
	    var table = document.createElement("TABLE");
    	//table.border = "1";

    	columnCount = patients[0].length;
	    
	    //Add the header row.
	    var row = table.insertRow(-1);
	    for (var i = 0; i < columnCount; i++) {
	        var headerCell = document.createElement("TH");
	        headerCell.innerHTML = patients[0][i];
	        row.appendChild(headerCell);
	    }

	    //Add the data rows.
	    for (var i = 1; i < patients.length; i++) {
	        row = table.insertRow(-1);
	        for (var j = 0; j < columnCount; j++) {
	            var cell = row.insertCell(-1);
	            if (j == 0) {
	            	//cell.innerHTML = "<a href=\"patientPage.html?MRN=" + patients[i][1] + "\"/>" + patients[i][j];	                      
	            	cell.innerHTML = "<button>" + patients[i][j] + "</button>"
	            }
	            else {
	            	cell.innerHTML = patients[i][j];
	            }
	        }	
	        row.ngClick = "patientPopup(patients[i][1])";   
	    }

	    //Get patient table to populate data
	    var pTable = document.getElementById("patientTable");
	    pTable.innerHTML = "";	    
      	pTable.appendChild(table);*/
	}

	$scope.patientDetails = function (i) {
		$scope.mrn=patients[i].MRN;
		$scope.dob=patients[i].dob;
		$scope.primary=patients[i].primary;
		$scope.physician=patients[i].physician;
		if(d.style.display != 'none'){			
			if(currentPatient==i){
				window.open("patientPage.html?mrn="+$scope.mrn);			
				d.toggle();
			}
		}
		else
			d.toggle();
		currentPatient=i;

	}

	$scope.closePatientView=function(){
		d.toggle();
		currentPatient=-1;
	}

	return {
		populatePatients: $scope.populatePatients
	}
 });

app.controller('PatientDetailsController',function($scope){

    //Use FHIR.js or another js package to load data
    $scope.patientDetails = {"firstName": "Akshar",
                                "lastName": "Rawal",
                                "address": "302, 10th St., Atlanta",
                                "email": "akshar@iamsick.com",
                                "phone": "(302) 303 3030"};
});

app.controller('TaskController',function($scope){

	$scope.todos=["Replace Mr Foley's cathater","Cure the Patients"];
	$scope.doings=["Check respirator labs","Check crocin order"];
	$scope.dones=["Meet My Friends"];
	$scope.removeFromDone=function(id){
		    $scope.dones.splice(id, 1);
	}
	$scope.moveFromDoingToDone=function(id){
		    var x=$scope.doings.splice(id, 1);
		    $scope.dones.push(x[0]);
	}	
	$scope.moveFromTodoToDoing=function(id){
		    var x=$scope.todos.splice(id, 1);
		    $scope.doings.push(x[0]);
	}		
	$scope.moveFromTodoToDone=function(id){
		    var x=$scope.todos.splice(id, 1);
		    $scope.dones.push(x[0]);
	}	
	$scope.moveFromDoingToTodo=function(id){
		    var x=$scope.doings.splice(id, 1);
		    $scope.todos.push(x[0]);		
	}
});

app.controller('OtherController',function($scope){
	$scope.selected=1;
    // TODO: use FHIR or our backend to populate scope variables
    $scope.notes = [{"title":"Update Note","text": "Elaine R. states recent difficulties with sleeping and concentration due to current problem. Cl. states getting only a few hours of sleep each night and complains that “I wake up in the middle of the night thinking about this stuff.” Cl. states change in mood becoming increasingly depressed, tearful at times. Cl. describes no change in eating. Cl. denies any increase/decrease in alcohol/drug use. Cl. states symptoms have lasted at least two months and are getting worse. Cl. denies any current medical concerns but states use of medication to decrease blood pressure. States last physical exam was 6 months ago. Cl. still able to perform duties/responsibilities as required. Cl. has been using social supports, exercise to deal with current stressors.Files", "created": "3/5/2015", "createdBy": "Dr. Evil"},
                    {"title":"Complete Evaluation","text": "History: Anna is a divorced Canadian 59 year old woman. Her chief complaint is, “I am constantly on edge and can't seem to concentrate on even the easiest tasks.\" Anna describes generalized anxiety and worry about events and activities. The source of the anxiety varies but the anxiety is present most days and she finds it difficult to control the worry. These generalized anxiety symptoms have been present for months. Her symptoms include: Sleep Disturbance Excess muscle tension Irritability Difficulty concentrating or mind going blank Being easily fatigued", "created": "3/6/2015", "createdBy": "Vu Nguyen"}];
    $scope.orders = [{"text": "Order 1", "ordered": "3/5/2015", "orderedBy": "Dr. Love", "completed": "", "completedBy": "Uncle Ben", "completedOn": "3/5/2015"}];

    $scope.labs = [{"text": "lab 1", "result": "result text", "created": "3/5/2015","values":[{"name":"WBC Count","val":"6.7","range":"4.5-11.0","units":"K/UL"},{"name":"RBC Count","val":"5.7","range":"3.5-5.50","units":"MIL/UL"}]}];
});
