/**
 * Copyright (c) 2016 Samsung Electronics, Inc.,
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
"use strict";

monitorApp.controller("ProjectManagementCtrl", function($scope, $mdDialog, $log, $location, $sce, ProjectService) {

    initialize();

    function initialize() {
    }
		$scope.$watch('newProject', function(newProject){
			$scope.status =  $sce.trustAsHtml('Server will be created at <a href="' + newProject.hostName + ":" + newProject.portNumber + "/#/\">" + newProject.hostName + ":" + newProject.portNumber + "</a>");
		},true);
	
		$scope.languages = ["CPP", "JAVA"];
		$scope.newProject = {hostName: $location.host(), portNumber:""};
					
		$scope.showCreateProjectDialog = function(ev) {
						
			$mdDialog.show({
			controller: "ProjectManagementCtrl",
			  templateUrl: '/view/createServerDialog.html',
			  parent: angular.element(document.querySelector('#popupContainer')),
			  targetEvent: ev,
			  clickOutsideToClose:true
			});
		};
		
		$scope.createServer = function(newProject) {
			
			validateProjectData(newProject).then(
			(errorMessage) => {
				if (errorMessage) {
					$scope.validationError = errorMessage;
					$log.error($scope.validationError);
				} else {
						$scope.validationError = "";
						$scope.status =  $sce.trustAsHtml("Creating new server...");
					ProjectService.createProject(newProject).then (function(result) {
						if (result.data.status=='ok') {
							$scope.status =  $sce.trustAsHtml('Server created at <a href="' + newProject.hostName + ":" + newProject.portNumber + "/#/\">" + newProject.hostName + ":" + newProject.portNumber + "</a> !");						
						} else {
							$scope.status =  $sce.trustAsHtml("Server creation failed!");
							$scope.validationError = result.data.errorMessage;
						}
					});					
				}
			});						
		};
		
		$scope.clearValidationError = function() {
			$scope.validationError = ""
		}
		
		function validateProjectData(newProject) {

			let validationResult = validateCompletness(newProject) || validateProjectName(newProject);
			
			if (validationResult) {
				return Promise.resolve(validationResult);
			} else {
				return validateProjectNameUsage(newProject).then ( function(validationResult) {
					if (validationResult) {
						return validationResult;
					} else {
						return validatePortNumberUsage(newProject);
					}
				});
			}
		}
		
		function validateCompletness(newProject) {
			if (!newProject.projectName) {
				return "Project name must not empty";
			} else if (!newProject.portNumber) {
				return "Port number must not empty";
			} else if (!newProject.language) {
				return "Project language must not empty";
			} else if (!newProject.adminName) {
				return "Administrator name must not empty";
			} else if (!newProject.adminPassword) {
				return "Administrator password must not empty";
			} 
		}
		
		function validateProjectName(newProject) {
			let projectName = newProject.projectName;
			if (!(projectName.match(/^[a-z0-9]+$/i))) {
				return 'Project name can contain only numbers and letters';
			}
		}
		
		function validateProjectNameUsage(newProject) {
			return ProjectService.isProjectNameUsed(newProject.projectName).then ( function(used) {
				if (used) {
					return "Project Name is already in use";
				}
			});
		}
		
		function validatePortNumberUsage(newProject) {
			return ProjectService.isPortNumberUsed(newProject.portNumber).then ( function(used) {
				if (used) {
					return "Port Number is already in use";
				}
			});
		}

		
});