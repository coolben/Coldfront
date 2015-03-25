var app=angular.module("coldfront",[])
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


app.controller('PatientController',  function($scope) {
	var patients = new Array();
	var d=document.querySelector('paper-dialog');
	//patients.push(["Name", "MRN", "DOB", "Primary Complaint", "Attending Physician"]);
	patients.push({"name":"Akshar Rawal", "MRN":"141542","dob": "02/14/1991","primary":"Headache","physician":"Dr. Braunstein"});
	patients.push({"name":"Ben Nguyen",  "MRN":"142324", "dob":"05/31/1991","primary":"Stomachache","physician":"Dr. Braunstein"});
	patients.push({"name":"Carl Saldanha", "MRN":"153234", "dob":"12/06/1991","primary":"Sprain","physician":"Dr. Braunstein"});
	patients.push({"name":"Erik Reinerstein","MRN":"153466","dob": "01/24/1991","primary":"Burn","physician":"Dr. Braunstein"});
	patients.push({"name":"Vu Nguyen","MRN":"153466", "dob":"08/10/1991","primary":"Bite","physician":"Dr. Braunstein"});
	console.log(patients);

	$scope.patients=patients;

	$scope.populatePatients = function () {
		var patients = new Array();
		//patients.push(["Name", "MRN", "DOB", "Primary Complaint", "Attending Physician"]);
	    patients.push({"name":"Akshar Rawal", "MRN":"141542","dob": "02/14/1991","primary":"Headache","physician":"Dr. Braunstein"});
	    patients.push({"name":"Ben Nguyen",  "MRN":"142324", "dob":"05/31/1991","primary":"Stomachache","physician":"Dr. Braunstein"});
	    patients.push({"name":"Carl Saldanha", "MRN":"153234", "dob":"12/06/1991","primary":"Sprain","physician":"Dr. Braunstein"});
	    patients.push({"name":"Erik Reinerstein","MRN":"153466","dob": "01/24/1991","primary":"Burn","physician":"Dr. Braunstein"});
	    patients.push({"name":"Vu Nguyen","MRN":"153466", "dob":"08/10/1991","primary":"Bite","physician":"Dr. Braunstein"});
		  
		$scope.patients=patients;
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

	$scope.patientDetails = function (mrn) {
		console.log(mrn);
		//var url = window.location;
		//var detailsPane = document.getElementById("details");
		//detailsPane.innerHTML = (url.search.split('MRN=')[1] ? url.search.split('MRN=')[1] : "No record found.");
		d.toggle();
		console.log(d);
		if(d.style.display != 'none'){			
			window.open("patientPage.html");			
		
		}

	}

	$scope.closePatientView=function(){
		d.toggle();
	}

	return {
		populatePatients: $scope.populatePatients
	}
 });

app.controller('PatientDetailsController',function($scope){

});

app.controller('OtherController',function($scope){
    // TODO: use FHIR or our backend to populate scope variables
    $scope.notes = [{"text": "Note 1", "created": "3/5/2015", "createdBy": "Dr. Evil"},
                    {"text": "Note 2", "created": "3/6/2015", "createdBy": "Vu Nguyen"}];
    $scope.orders = [{"text": "Order 1", "ordered": "3/5/2015", "orderedBy": "Dr. Love", "completed": "", "completedBy": "Uncle Ben", "completedOn": "3/5/2015"}];

    $scope.labs = [{"text": "lab 1", "result": "result text", "created": "3/5/2015"}];
});