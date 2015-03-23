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
 	$scope.search=true;
 	$scope.searchfunction=function(){
		$scope.search=true;
	}

 	$scope.opensearch=function(){
		$scope.search=false;
	}

	$scope.populatePatients = function () {
		var patients = new Array();
		patients.push(["Name", "MRN", "DOB", "Primary Complaint", "Attending Physician"]);
	    patients.push(["Akshar Rawal", "141542", "02/14/1991", "Headache", "Dr. Braunstein"]);
	    patients.push(["Ben Nguyen", "142324", "05/31/1991", "Stomachache", "Dr. Braunstein"]);
	    patients.push(["Carl Saldanha", "153234", "12/06/1991", "Sprain", "Dr. Braunstein"]);
	    patients.push(["Erik Reinerstein", "153466", "01/24/1991", "Burn", "Dr. Braunstein"]);
	    patients.push(["Vu Nguyen", "153466", "08/10/1991", "Bite", "Dr. Braunstein"]);
		    
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
	            	cell.innerHTML = "<a href=\"patientPage.html?MRN=" + patients[i][1] + "\"/>" + patients[i][j];	                      
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
      	pTable.appendChild(table);
	}

	$scope.patientPopup = function (id) {
		alert("Patient Pop Up.");
		$location.path('#/patientPage.html?MRN=' + id);
	}

	$scope.patientDetails = function () {
		var url = window.location;
		var detailsPane = document.getElementById("details");
		detailsPane.innerHTML = (url.search.split('MRN=')[1] ? url.search.split('MRN=')[1] : "No record found.");
	}

	return {
		populatePatients: $scope.populatePatients
	}
 });

