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

    /*var responsePromise = $http({
      method: 'GET',
	    url: "/patients",
      headers: {'Content-Type':  "application/x-www-form-urlencoded; charset=utf-8"}
    });

    responsePromise.success(function(data, status, headers, config)  {
      console.log(data);
      for (var i = 0; i < data.length; i++) {
	    var patient = {};
      	patient['id'] = data[i].id;
      	patient['name'] = data[i].nameFirst + " " + data[i].nameLast;
      	patient['MRN'] = data[i].id;
		//patient['MRN'] = /\\(\d+)/.data[i].id;
      	console.log(patient.MRN);
      	patient['dob'] = "04/07/2015";
      	patient['from'] = "7 South";
      	patient['primary'] = "Headache";
      	patient['physician'] = "Dr. Braunstein";
      	patients.push(patient);
      }      
	});
	*/
	var responsePromise = $http({
      method: 'GET',
	    url: "http://fhirtest.uhn.ca/baseDstu1/Patient?_format=json",
		//url: "http://fhir.healthintersections.com.au/open/Patient?_format=json",
      headers: {'Content-Type':  "application/x-www-form-urlencoded; charset=utf-8"}
    });

    responsePromise.success(function(data, status, headers, config)  {
      console.log(data);
      for (var i = 0; i < data.entry.length; i++) {
	    var patient = {};
      	patient['id'] = data.entry[i].id;
      	patient['name'] = data.entry[i].content.name[0].given[0] + " " + data.entry[i].content.name[0].family[0];
      	patient['MRN'] = data.entry[i].id;
		patient['MRN'] = data.entry[i].id.match(/Patient\/([^]*)/)[1];
      	console.log(patient.MRN);
      	patient['dob'] = data.entry[i].content.birthDate;
      	patient['from'] = "7 South";
      	patient['primary'] = "Headache";
      	patient['physician'] = "Dr. Braunstein";
      	patients.push(patient);
      }      
    });
    responsePromise.error(function(data, status, headers, config) {
      alert("AJAX failed!");
    });


    // Trying to hit local server
	var patientPromise = $http({
		method: 'GET',
		url: "/patients",
		headers: {'Content-Type':  "application/x-www-form-urlencoded; charset=utf-8"}
	});
	patientPromise.success(function(data, status, headers, config){
		console.log(data);
		for(var i=0; i < data.length; i++){
			var patient = {};
			patient.id = data[i].id;
			patient.MRN = data[i].id;
			patient.name = data[i].nameFirst + " " + data[i].nameLast;
			patient.dob = data[i].dob;
			patients.push(patient);
		}
	});
	patientPromise.error(function(data, status, headers, config){
		alert("Request for patient list failed.");
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

app.controller('PatientDetailsController',function($scope, $http, $attrs){

    //Use FHIR.js or another js package to load data
    $scope.patientDetails = {"firstName": "Akshar",
                                "lastName": "Rawal",
                                "address": "302, 10th St., Atlanta",
                                "email": "akshar@iamsick.com",
                                "phone": "(302) 303 3030"};

    var id = "108";
    var patient = {};

    //////////////////////////////////////////////////////////////////////////////////
    //			Below two methods get all personal details for the patient          //
    //////////////////////////////////////////////////////////////////////////////////
    var responsePromise = $http({
      method: 'GET',
	    //url: "http://fhirtest.uhn.ca/baseDstu1/Patient?_format=json",
		url: "http://fhir.healthintersections.com.au/open/Patient?_format=json&_id=" + id,
      headers: {'Content-Type':  "application/x-www-form-urlencoded; charset=utf-8"}
    });

    responsePromise.success(function(data, status, headers, config)  {
      //console.log(data);
      patient = data.entry[0].content;
      console.log(patient);
      console.log(patient.name[0].given[0] + patient.name[0].family[0]);      
    });    

    //////////////////////////////////////////////////////////////////////////////////
    //    Below two methods get all lab tests/observation details for the patient   //
    //////////////////////////////////////////////////////////////////////////////////
    var observations = {};
    var availableTests = {};

    availableTests['codes'] = [];
    availableTests['data'] = [];
    availableTests['notes'] = [];

    var responsePromise = $http({
      method: 'GET',
	    //url: "http://fhirtest.uhn.ca/baseDstu1/Patient?_format=json",
		url: "http://fhir.healthintersections.com.au/open/Observation?_format=json&id=" + id,
      headers: {'Content-Type':  "application/x-www-form-urlencoded; charset=utf-8"}
    });

    responsePromise.success(function(data, status, headers, config)  {
      //console.log(data);
      observations = data.entry;

      //Loop through all observations and sort by lab tests(data) and notes
      for(var i = 0; i < parseInt(data.totalResults); i++) {      	
      	var index = availableTests.codes.indexOf(observations[i].content.name.coding[0].code);

      	//Check of there is already an  observation with the same code
      	//If there isn't, add a new observation entry
        if(index == -1) { 		  
 		  var document = {};
 		  document['tests'] = [];
 		  var tests = {};
          console.log(i);

          //Add the code to the list of known codes
 		  availableTests.codes.push(observations[i].content.name.coding[0].code);

 		  //Add the code and display names to the new observation
 		  //This data is irrespective of whether the observation is a test or a note
          document.obsCode = observations[i].content.name.coding[0].code;
 		  document.obsName = observations[i].content.name.coding[0].display;

 		  //Check if the observation is a test
 		  if (observations[i].content.valueQuantity != undefined) {
 		    document.obsUnit = observations[i].content.valueQuantity.units;
 		    tests.value = observations[i].content.valueQuantity.value; 		    
 		  }
 		  //If there are no lab test values, this observation is considered as a note
 		  else if (observations[i].content.valueString != undefined) {
 		  	tests.value = observations[i].content.valueString; 		  		 
 		  }

 		  //Check if there is a time that the test was issued or published and add it to the entry
 		  if (observations[i].content.issued != undefined) { 		    
		    tests.obsDate = observations[i].content.issued.match(/([^]*)T/)[1];
		  }
		  else if (observations[i].content.published != undefined) {
	        tests.obsDate = observations[i].content.published.match(/([^]*)T/)[1];
		  }
		  else {
		    tests.obsDate = "No date found.";
		  }
 		  document.tests.push(tests);

 		  //Check if this was a lab test or a note and push accordingly
 		  if (observations[i].content.valueQuantity != undefined) {
 		    availableTests.data.push(document);  		    
 		  }
 		  else {
 		  	availableTests.notes.push(document);
 		  }
        }

 		//If there is already an entry for the current observation code, 
 		//update the observation list with the values for the new entry
        else {          
          var code = observations[i].content.name.coding[0].code;
          var tests = {};	
          var document = {};
          document['tests'] = [];

          //Check if this observation is a note or a lab test
          if (observations[i].content.valueString != undefined) {
            //If it is a note, check if there is another note for the same condition
            if (availableTests.data.indexOf(code) == -1) {
              //If there is a note already, then just append the new note and the date of the new note
              index = availableTests.notes.indexOf(code);
              tests.value = observations[i].content.valueString;

              //Check if there is a time that the test was issued or published and add it to the entry
              if (observations[i].content.issued != undefined) { 		    
		        tests.obsDate = observations[i].content.issued.match(/([^]*)T/)[1];
		      }
		      else if (observations[i].content.published != undefined) {
	            tests.obsDate = observations[i].content.published.match(/([^]*)T/)[1];
		      }
		      else {
		        tests.obsDate = "No date found.";
		      }

		      availableTests.notes[index].tests.push(tests);
            }          	

            else {
              //If this is a note for a new condition, add a new entry to the notes
              document.obsCode = observations[i].content.name.coding[0].code;
 		      document.obsName = observations[i].content.name.coding[0].display;
 		      tests.value = observations[i].content.valueString;

 		      //Check if there is a time that the test was issued or published and add it to the entry
              if (observations[i].content.issued != undefined) { 		    
		        tests.obsDate = observations[i].content.issued.match(/([^]*)T/)[1];
		      }
		      else if (observations[i].content.published != undefined) {
	            tests.obsDate = observations[i].content.published.match(/([^]*)T/)[1];
		      }
		      else {
		        tests.obsDate = "No date found.";
		      }
              
              document.tests.push(tests);
		      availableTests.notes.push(document);
            }
          	
          }

          //If it is not a note, and the data already exists
          //append the new data to the test in the observation list
          else {
          	index = availableTests.data.indexOf(code);

          	//Populate the test values if available, else make it 0
          	if (observations[i].content.valueQuantity != undefined) {
 		      tests.value = observations[i].content.valueQuantity.value; 		    
 		    }
 		    else {
 		      tests.value = "0";
 		    }

            //Check if there is a time that the test was issued or published and add it to the entry
            if (observations[i].content.issued != undefined) { 		    
		      tests.obsDate = observations[i].content.issued.match(/([^]*)T/)[1];
		    }
		    else if (observations[i].content.published != undefined) {
	          tests.obsDate = observations[i].content.published.match(/([^]*)T/)[1];
		    }
		    else {
		      tests.obsDate = "No date found.";
		    }
          }
        }         
      }     
      console.log("available tests");
      console.log(availableTests);
    });    
								
	// var data = [4, 8, 15, 16, 23, 42];
	
	// var w=960,h=500;		 
	
	// var x = d3.scale.linear()
 //    .domain([0, d3.max(data)])
 //    .range([0, 420]); 
	 
	// d3.select(".chart")
	//   .selectAll("div")
	// 	.data(data)
	//   .enter().append("div")
	// 	.style("width", function(d) { return d * 10 + "px"; })
	// 	.text(function(d) { return d; });
});

function listHelper($scope, from) {
	switch(from) {
		case "todo":
			return $scope.todos;
		case "doing":
			return $scope.doings;
		case "done":
			return $scope.dones;
	}
};

app.controller('TaskController',function($scope){
	$scope.todos=["Replace Mr Foley's cathater","Cure the Patients"];
	$scope.doings=["Check respirator labs","Check crocin order"];
	$scope.dones=["Meet My Friends"];
	
	
	$scope.moveToDoing=function(id, from){
			list = listHelper($scope, from);
			$scope.doings.push(list.splice(id, 1)[0]);
	}
	$scope.moveToDone=function(id, from){
			list = listHelper($scope, from);
			$scope.dones.push(list.splice(id, 1)[0]);
	}
	$scope.moveToTodo=function(id, from){
			list = listHelper($scope, from);
			$scope.todos.push(list.splice(id, 1)[0]);	
	}
	$scope.removeFromDone=function(id, from){
			list = listHelper($scope, from);
			list.splice(id, 1);
	}
	$scope.editNote=function(id, from){
		    list = listHelper($scope, from);
			taskItem = list.splice(id, 1)[0];
		    var editedNote = prompt("Please edit your note", taskItem);
			if (editedNote != null) {
				list.push(editedNote);
			} else {
				list.push(taskItem);		
			}
	}
	$scope.addNewNote=function(){
		    var newNote = prompt("Please create a new note");		
			$scope.todos.push(newNote);
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


