'use strict';
var app = angular.module('confifyServices',[]);

app.factory('AuthenticationService',
    ['Base64', '$http', '$cookieStore', '$rootScope','$timeout','app.config',
    function (Base64,$http,$cookieStore,$rootScope,$timeout,config){
        var service = {};
        service.login = function (email,password,accountType, callback){
//        $timeout(function(){
//                        var response = { success: email === 'test@test.com' && password === 'test' };
//                        if(!response.success) {
//                            response.message = 'email or password is incorrect';
//                        }
//                        callback(response);
//                    }, 1000);
            $http.defaults.headers.common['AccountType']=accountType;
            $http.post(config.apiBasePath+'authenticate', { email: email, password: password })
            .success(function (response,status) {
                callback(response,status);
            })
            .error(function(response,status) {
                callback(response);
            });

        };

        service.setCredentials = function (email, password, accountType) {
            var authdata = Base64.encode(email + ':' + password);

            $rootScope.globals = {
                currentUser: {
                    email: email,
                    authdata: authdata,
                    accounttype: accountType
                }
            };

            $http.defaults.headers.common['Authorization'] = 'Basic ' + authdata;
            $http.defaults.headers.common['AccountType'] = accountType;
            $cookieStore.put('globals', $rootScope.globals);
        };

        service.clearCredentials = function () {
            $rootScope.globals = {};
            $cookieStore.remove('globals');
            $http.defaults.headers.common.Authorization = 'Basic ';
        };

        return service;
    }
]);

app.factory('RegisterService',
    ['$http','app.config',
    function ($http,config){
        var service = {};
        service.register = function ($user, callback){

            $http.post(config.apiBasePath+'users', $user)
            .success(function (response,status) {
                callback(response,status);
            })
            .error(function(response,status) {
                callback(response,status);
            });

        };

        return service;
    }
]);

app.factory('ConferenceService',
    ['$http','$cookieStore','app.config',
        function ($http,$cookieStore,config){
            var service = {};
            var upcomingConferences =[];
            var ongoingConferences =[];
            var recentConferences =[];
            var displayingConferences =[];
            var allConferences = [];
            var observerCallbacks = [];
            var observingConferenceType ='';
            //register an observer
            service.registerObserverCallback = function(callback){
                observerCallbacks.push(callback);
            };
            service.clearObservers = function(){
                observerCallbacks = [];
                console.log("Observers have been cleared");
            };
            //call this when you know 'foo' has been changed
            var notifyObservers = function(){
                angular.forEach(observerCallbacks, function(callback){
                    callback();
                });
            };
            service.removeSpeaker = function(conferenceId, speakerId, callback){
                $http.delete(config.apiBasePath+'conferences/'+conferenceId+'/speakers/'+speakerId)
                    .success(function(response, status){
                        callback(response, status);
                    })
                    .error(function(response, status){
                        callback(response, status);
                    });
            };
            service.getConferences = function (callback){
                $http.get(config.apiBasePath+'conferences')
                    .success(function (response,status) {
                        callback(response,status);
                    })
                    .error(function(response,status) {
                        callback(response,status);
                    });
            };

            service.changeStatus = function(uid, toStatus, conferenceID,  callback){
                var url = config.apiBasePath+'conferences/'+conferenceID+'/attendance';
                console.log(url);
                $http.put( url ,{status: toStatus, 'userId': uid})
                    .success(function (response,status) {
                        callback(response,status);
                    })
                    .error(function(response,status) {
                        callback(response,status);
                    });
            };

            service.inviteAttendee = function(email, cid, callback) {
                var url = config.apiBasePath+'conferences/'+cid+'/invitations';
                console.log(url);
                $http.post(url, [email])
                    .success(function (response,status) {
                        callback(response,status);
                    })
                    .error(function(response,status) {
                        callback(response,status);
                    });
            };

            service.inviteSpeaker = function(email, cid, callback) {
                var url = config.apiBasePath+'conferences/'+cid+'/speakers';
                console.log(url);
                $http.post(url, [email])
                    .success(function (response,status) {
                        callback(response,status);
                    })
                    .error(function(response,status) {
                        callback(response,status);
                    });
            };

            service.removeAttendee = function(uid, cid, callback) {
                var url = config.apiBasePath+'conferences/'+cid+'/attendees/'+uid;
                console.log(url);
                $http.delete(url)
                    .success(function(response,status) {
                        callback(response,status);
                    })
                    .error(function(response,status) {
                        callback(response,status);
                    });
            };

            service.displayOngoing = function(){
                displayingConferences = [];
                allConferences.forEach(function(conference) {
                    if (conference.startTime < Date.now() && conference.endTime > Date.now()) {
                        if (typeof displayingConferences[displayingConferences.length - 1] == "undefined"
                            || displayingConferences[displayingConferences.length - 1].length == 3) {
                            displayingConferences.push([conference]);
                        }
                        else {
                            displayingConferences[displayingConferences.length - 1].push(conference);
                        }
                    }
                })
                observingConferenceType = "ongoing";
                notifyObservers();
            };
            service.displayUpcoming = function(){
                displayingConferences = [];
                allConferences.forEach(function(conference) {
                    if (conference.startTime > Date.now()) {
                        if (typeof displayingConferences[displayingConferences.length - 1] == "undefined"
                            || displayingConferences[displayingConferences.length - 1].length == 3) {
                            displayingConferences.push([conference]);
                        }
                        else {
                            displayingConferences[displayingConferences.length - 1].push(conference);
                        }
                    }
                })
                observingConferenceType = "upcoming";
                notifyObservers();
            };
            service.displayRecent = function(){
                displayingConferences = [];
                allConferences.forEach(function(conference) {
                    if (conference.endTime < Date.now()) {
                        if (typeof displayingConferences[displayingConferences.length - 1] == "undefined"
                            || displayingConferences[displayingConferences.length - 1].length == 3) {
                            displayingConferences.push([conference]);
                        }
                        else {
                            displayingConferences[displayingConferences.length - 1].push(conference);
                        }
                    }
                })
                observingConferenceType = "recent";
                notifyObservers();
            };

            service.getDisplayingConferences = function(){
                return displayingConferences;
            }

            service.setConferences = function (conferences){
                allConferences = conferences;
                if(observingConferenceType=='upcoming'){
                    service.displayUpcoming();
                }
                else if(observingConferenceType=='ongoing'){
                    service.displayOngoing();
                }
                else if(observingConferenceType=='recent'){
                    service.displayRecent();
                }
                else{
                    service.displayOngoing();
                }
            };
            service.getConferenceWithId = function(id,callback){
                $http.get(config.apiBasePath+'conferences/'+id)
                    .success(function (response,status) {
                        callback(response,status);
                    })
                    .error(function(response,status) {
                        callback(response,status);
                    });

            };


            service.deleteConferenceWithId = function(id,callback) {
                $http.delete(config.apiBasePath + 'conferences/' + id)
                    .success(function (reponse, status) {
                        callback(reponse, status);
                    })
                    .error(function (response, status) {
                        callback(response, status);
                    });

            }

            service.createConference = function($conference, callback) {
                var url = config.apiBasePath+'conferences';
                console.log("server " + $conference);
                console.log(url);
                $http.post(url, $conference)
                    .success(function (response,status) {
                        callback(response,status);
                    })
                    .error(function(response,status) {
                        callback(response,status);
                    });

            };
            return service;

        }
    ]);


app.factory('ValidateService', [function(){
    var service = {};
    service.validateEmail = function(email){
        var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
        return re.test(email);
    };
    return service;
}]);
app.factory('Base64', function () {
        /* jshint ignore:start */

        var keyStr = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';

        return {
            encode: function (input) {
                var output = "";
                var chr1, chr2, chr3 = "";
                var enc1, enc2, enc3, enc4 = "";
                var i = 0;

                do {
                    chr1 = input.charCodeAt(i++);
                    chr2 = input.charCodeAt(i++);
                    chr3 = input.charCodeAt(i++);

                    enc1 = chr1 >> 2;
                    enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                    enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                    enc4 = chr3 & 63;

                    if (isNaN(chr2)) {
                        enc3 = enc4 = 64;
                    } else if (isNaN(chr3)) {
                        enc4 = 64;
                    }

                    output = output +
                        keyStr.charAt(enc1) +
                        keyStr.charAt(enc2) +
                        keyStr.charAt(enc3) +
                        keyStr.charAt(enc4);
                    chr1 = chr2 = chr3 = "";
                    enc1 = enc2 = enc3 = enc4 = "";
                } while (i < input.length);

                return output;
            },

            decode: function (input) {
                var output = "";
                var chr1, chr2, chr3 = "";
                var enc1, enc2, enc3, enc4 = "";
                var i = 0;

                // remove all characters that are not A-Z, a-z, 0-9, +, /, or =
                var base64test = /[^A-Za-z0-9\+\/\=]/g;
                if (base64test.exec(input)) {
                    window.alert("There were invalid base64 characters in the input text.\n" +
                        "Valid base64 characters are A-Z, a-z, 0-9, '+', '/',and '='\n" +
                        "Expect errors in decoding.");
                }
                input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");

                do {
                    enc1 = keyStr.indexOf(input.charAt(i++));
                    enc2 = keyStr.indexOf(input.charAt(i++));
                    enc3 = keyStr.indexOf(input.charAt(i++));
                    enc4 = keyStr.indexOf(input.charAt(i++));

                    chr1 = (enc1 << 2) | (enc2 >> 4);
                    chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
                    chr3 = ((enc3 & 3) << 6) | enc4;

                    output = output + String.fromCharCode(chr1);

                    if (enc3 != 64) {
                        output = output + String.fromCharCode(chr2);
                    }
                    if (enc4 != 64) {
                        output = output + String.fromCharCode(chr3);
                    }

                    chr1 = chr2 = chr3 = "";
                    enc1 = enc2 = enc3 = enc4 = "";

                } while (i < input.length);

                return output;
            }
        };

        /* jshint ignore:end */
    });

app.factory('ProfileService',
    ['$http','app.config',
        function ($http,config){
            var service = {};
            service.getProfile = function (type, callback){
            $http.get(config.apiBasePath+ type + "s/me")
                .success(function (response,status) {
                    callback(response,status);
                })
                .error(function(response,status) {
                    callback(response,status);
                });
            };

            service.updateProfile = function (type, $user, callback){
                var url = config.apiBasePath+ type + "s/" +$user.id;
                console.log(url);
                $http.post(url,$user)
                    .success(function (response,status) {
                        callback(response,status);
                    })
                    .error(function(response,status) {
                        callback(response,status);
                    });
            };

            return service;
        }
    ]);