'use strict';
var app = angular.module('confifyControllers',['ngCookies']);

app.controller('LoginController', ['$scope','$location','AuthenticationService','$window',
    function($scope, $location, AuthenticationService,$window){
        AuthenticationService.clearCredentials(); // reset login status
        $scope.userType = "user";
        $scope.login = function(){
        $scope.dataLoading = true;

        var userType = $scope.userType.toLowerCase();
        console.log("User Type " + userType);
        AuthenticationService.login($scope.email, $scope.password, userType, function(response,status) {
            console.log(response);
            console.log(status);
            if(status==200) {
                AuthenticationService.setCredentials($scope.email, $scope.password, userType);


                if((response.firstName == "" && response.lastName == "") ||(response.firstName==null && response.lastName==null))
                    $location.path('/profile')
                else {
                    $window.location.reload();
                    $location.path('/conferences');
                }
            }
            else if(status == 401) {
                $scope.error = "Invalid credentials";
            }
            else{
                $scope.error = response.message;
            }
            $scope.dataLoading = false;
        });
    }
}]);

app.controller('RegisterController', ['$scope','$location','AuthenticationService','RegisterService',
    function($scope, $location, AuthenticationService, RegisterService){
    AuthenticationService.clearCredentials(); // reset login status

    $scope.register = function(){
        $scope.dataLoading = true;
        var $user = {

                email:$scope.email,
                password:$scope.password,
                firstName:$scope.firstname,
                lastName:$scope.lastname,
                organization:$scope.organization,
                profession:$scope.profession,
                bio:$scope.bio
            };

        RegisterService.register($user, function(response,status) {
            if(status==200) {
                $location.path('/');
            }
            else{
                console.log(response.status);
                $scope.error = response.message;
            }
            $scope.dataLoading = false;
        });
    }
}]);

app.controller('ConferenceController', ['$scope','ConferenceService',
    function($scope, ConferenceService){

        $scope.$on("$destroy",function(){
            ConferenceService.clearObservers();
        });

        ConferenceService.getConferences(function(response,status) {
            if(status==200) {
                console.log("Conference retrieved from api");
                ConferenceService.registerObserverCallback(function(){
                    $scope.conferences = ConferenceService.getDisplayingConferences();

                    console.log("Conference Observer received notification");
                    console.log($scope.conferences);

                })
                ConferenceService.setConferences(response);

            }
            else if(status == 401) {
                $scope.error = "Invalid credentials";
            }
            else{
                $scope.error = response.message;
            }
        });

        $scope.deleteConferenceWithId = function(id){
            ConferenceService.deleteConferenceWithId(id,function(response,status){
                ConferenceService.getConferences(function (response, status) {
                    ConferenceService.setConferences(response);
                });
            });
        }


}]);
app.controller('SideMenuController', ['$scope', '$rootScope', 'ConferenceService',
    function($scope, $rootScope, ConferenceService){
        $scope.isLoggedIn = $rootScope.globals.currentUser;

        if($scope.isLoggedIn != "undefined" && $scope.isLoggedIn != null){
            var type = $rootScope.globals.currentUser.accounttype;
            $scope.createbutton = (type == "admin");

        }

        $scope.showOngoingConferences = function(){
            ConferenceService.displayOngoing();
        }
        $scope.showUpcomingConferences = function(){
            ConferenceService.displayUpcoming();
        }
        $scope.showRecentConferences = function(){
            ConferenceService.displayRecent();
        }

    }]);

app.controller('TopMenuController', ['$scope', '$rootScope', 'AuthenticationService','$window',
    function($scope, $rootScope, AuthenticationService,$window){
        $scope.logout = function(){
            AuthenticationService.clearCredentials();
            $window.location.reload();
        }
        $scope.profile = function(){
            $window.location.reload();
        }
    }]);

app.controller('ConferenceTabController', ['$scope', '$routeParams','ConferenceService', 'ValidateService', '$cookieStore','$interval',
    function($scope, $routeParams, ConferenceService, ValidateService, $cookieStore,$interval){
        var fetchConference = function(){
            ConferenceService.getConferenceWithId($routeParams.id, function(response){
                $scope.conference = response;
            });
        };
        fetchConference();
        var autoRefreshConference = $interval(function(){
            fetchConference();
        }, 3000);

        var cookie = $cookieStore.get('globals');
        var type = cookie.currentUser.accounttype;
        console.log("User Type~~~" + JSON.stringify(type));
        if(type == "user") {
            $scope.showButton = false;
            $scope.tabs = [
                {
                    title:"Detail",
                    url:"partials/conference_detail_tab.html"
                },
                {
                    title:"Attendees",
                        url:"partials/attendees_tab.html"
                },
                {
                    title:"Speakers",
                    url:"partials/attendees_speakers_tab.html"
                }
            ];
        }else {
            $scope.showButton = true;
            $scope.tabs = [
                {
                    title:"Detail",
                    url:"partials/conference_detail_tab.html"
                },
                {
                    title:"Invited",
                    url:"partials/attendees_invite_tab.html"
                },
                {
                    title:"Accepted",
                    url:"partials/attendees_invite_tab.html"
                },
                {
                    title:"Checked In",
                    url:"partials/attendees_invite_tab.html"
                },
                {
                    title:"Speakers",
                    url:"partials/attendees_speakers_tab.html"
                }
            ];
        }

        $scope.currentTab = 'partials/conference_detail_tab.html';
        $scope.actionButtonText = "";
        $scope.filterStatus = 0;
        $scope.onClickTab = function (tab) {
            $scope.currentTab = tab.url;
            if(tab.title == "Invited") {
                $scope.actionButton = true;
                $scope.actionButtonText = "Accept"
                $scope.filterStatus = 1;
            }else if(tab.title == "Accepted") {
                $scope.actionButton = true;
                $scope.actionButtonText = "Check In";
                $scope.filterStatus = 2;
            }else if(tab.title == "Checked In") {
                $scope.actionButton = false;
                $scope.actionButtonText = "Send Email";
                $scope.filterStatus = 3;
            }else if(tab.title == "Speakers") {
                // data is in conference.speakers
            }
        };

        $scope.changeStatus = function(uid, toStatus, cid){
            ConferenceService.changeStatus(uid, toStatus,  cid, function(response, status){
                if( status == 200 )
                    fetchConference();
            });
        };
        $scope.isActiveTab = function(tabUrl) {
            return tabUrl == $scope.currentTab;
        };
        $scope.removeSpeaker = function(cid, sid){
            ConferenceService.removeSpeaker(cid,sid,function(response, status){
                if(status == 200){
                    for( var i = 0; i < $scope.conference.speakers.length; i++) {
                        if ($scope.conference.speakers[i].id == sid) {
                            $scope.conference.speakers.splice(i, 1);
                        }
                    }
                }
                console.log("response = " + JSON.stringify(response) +"  ; " + "status = " + status);
            })
        };

        $scope.inviteSpeaker = function(email, cid) {
            if(!ValidateService.validateEmail(email)){
                $scope.error1 = true;
                $scope.message1 = false;
                $scope.error1 = "Email is invalid! Please follow the format : xxx@yyy.zzz";
                return;
            }
            ConferenceService.inviteSpeaker(email, cid, function(response, status) {
                console.log("email:"+email+"cid:"+cid);
                if(status == 200) {
                    $scope.message1 = true;
                    $scope.error1 = false;
                    $scope.message1 = "Invitation has been sent !";
                    fetchConference();
                }
                else if(status == 409) {
                    $scope.error1 = true;
                    $scope.message1 = false;
                    $scope.error1 = "This speaker is already in the conference!";
                }else {
                    $scope.error1 = true;
                    $scope.message1 = false;
                    $scope.error1 = response.message;
                }
            });
        };

        $scope.inviteAttendee = function(email, cid) {
            if(!ValidateService.validateEmail(email)){
                $scope.error = true;
                $scope.message = false;
                $scope.error = "Email is invalid! Please follow the format : xxx@yyy.zzz";
                return;
            }
            ConferenceService.inviteAttendee(email, cid, function(response, status) {
                console.log("status:" + status);
                if(status == 200) {
                    $scope.message = true;
                    $scope.error = false;
                    $scope.message = "Invitation has been sent !";
                    fetchConference();
                }else if(status == 409) {
                    $scope.error = true;
                    $scope.message = false;
                    $scope.error = "This user is already in the conference!";
                }else {
                    $scope.error = true;
                    $scope.message = false;
                    $scope.error = response.message;
                }
            });
            //console.log(email+ " "+cid);
        };

        $scope.removeAttendee = function(aid, cid) {
            ConferenceService.removeAttendee(aid, cid, function(response, status) {
                if(status == 200) {
                    fetchConference();
                }else {
                    $scope.error = response.message;
                }
            });
        };

        $scope.$on('$destroy', function() {
            // Make sure that the interval is destroyed too
            $interval.cancel(autoRefreshConference);
            autoRefreshConference = undefined;
        });

    }]);


app.controller('ProfileController', ['$scope', "$rootScope", 'ProfileService', 'AuthenticationService',
    function($scope, $rootScope, ProfileService,AuthenticationService){
        //get profile

        var type = $rootScope.globals.currentUser.accounttype;
        $scope.type = (type == 'admin');

        var org;
        ProfileService.getProfile(type, function(response,status) {

            if(status==200) {
                console.log("Profile retrieved from api");
                console.log("response: "+ response);
                //$scope.profile = response;
                $scope.email = response.email;
                $scope.password = response.password;
                $scope.firstname = response.firstName;
                $scope.lastname = response.lastName;
                $scope.bio = response.bio;
                $scope.id = response.id;
                $scope.profession = response.profession;


                if (type == "user"){
                    $scope.userOrganization = response.organization;


                }
                else{
                    $scope.adminOrganization = response.organization.name;
                    org = response.organization;
                }

                console.log("=======get======== " );
                console.log("password" + response.password);
                console.log("=======get end======== " );
            }
            else if(status == 401) {
                $scope.error = "Invalid credentials";
                console.log($scope.error);

            }
            else {
                $scope.error = response.message;
            }

        });

        //update profile
        $scope.updateProfile = function (){
            if (type == "user"){
                org = $scope.userOrganization;
            }


            var $user = {
                email:$scope.email,
                firstName:$scope.firstname,
                lastName:$scope.lastname,
                bio:$scope.bio,
                profession: $scope.profession,
                organization:org,
                id: $scope.id

            };
            if ($scope.password!=null){
                $user.password = $scope.password;
                //AuthenticationService.setCredentials($scope.email, $scope.password, type);

            }

            console.log("=============== " );
            console.log("$user: " + $user);
            console.log("Password: " + $user.password);
            console.log("=============== " );

            ProfileService.updateProfile(type, $user, function(response, status){
                if(status==200) {
                    console.log("Profile updated");
                    if (type=="user"){
                        $scope.message = "You profile is updated successfully.\nPlease log into our Mobile APP!";
                    }
                    else{
                        $scope.message = "You profile is updated successfully.";
                    }

                }
                else if(status == 401) {
                    $scope.error = "You profile is not updated";
                    console.log($scope.error);
                }
                else {
                    $scope.error = response.message;
                }
            });
        };

    }]);


app.controller('CreateConferenceController', ['$scope','ConferenceService','$location',
    function($scope, ConferenceService,$location){
        //Create a conference
        $scope.createConference = function (){

            Date.prototype.yyyymmdd = function() {

                var yyyy = this.getFullYear().toString();
                var mm = (this.getMonth()+1).toString(); // getMonth() is zero-based
                var dd  = this.getDate().toString();

                return yyyy + '-' + (mm[1]?mm:"0"+mm[0]) + '-' + (dd[1]?dd:"0"+dd[0]);
            };

            var $conference = {
                name: $scope.name,
                description: $scope.description,
                startTime: $scope.startTime.yyyymmdd(),
                endTime: $scope.endTime.yyyymmdd(),
                venue: {
                    street: $scope.street,
                    city: $scope.city,
                    state: $scope.state,
                    zip: $scope.zip
                }
            };
            console.log($conference);
            ConferenceService.createConference($conference, function(response, status){
                if(status==200) {
                    $location.path('/conferences/'+response.id);
                    console.log("Conference Created");
                }
                else if(status == 401) {
                    $scope.error = "Conference is not created";
                    console.log($scope.error);
                }
                else {
                    $scope.error = response;
                }
            });
        };

    }]);