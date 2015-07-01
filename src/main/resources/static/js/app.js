'use strict';

var app = angular.module('confify',[
    'app.config',
    'ngRoute',
    'ngCookies',
    'confifyControllers',
    'confifyServices'
]);

app.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.
        when('/profile', {
            templateUrl: 'partials/profile.html',
            controller: 'ProfileController'
        }).
        when('/login', {
            templateUrl: 'partials/login.html',
            controller: 'LoginController'
        }).
        when('/register', {
            templateUrl: 'partials/register.html',
            controller: 'RegisterController'
        }).
        when('/conferences', {
            templateUrl: 'partials/conferences.html',
            controller: 'ConferenceController'
        }).
        when('/conferences/:id', {
            templateUrl: 'partials/conference_tab_view.html',
            controller: 'ConferenceTabController'
        }).
        when('/conference/create', {
            templateUrl: 'partials/create_conference.html',
            controller: 'CreateConferenceController'
        }).
        otherwise({
            redirectTo: '/'
        });
    }
]);
angular.module('app.config',[])
    .value('app.config', {
        apiBasePath:'http://localhost:8080/api/'
    });

app.run(['$rootScope', '$location', '$cookieStore', '$http',
    function ($rootScope, $location, $cookieStore, $http) {
        // keep user logged in after page refresh
        $rootScope.globals = $cookieStore.get('globals') || {};
        if ($rootScope.globals.currentUser) {
            $http.defaults.headers.common['Authorization'] = 'Basic ' + $rootScope.globals.currentUser.authdata;
            $http.defaults.headers.common['AccountType'] = $rootScope.globals.currentUser.accounttype;
        }

        $rootScope.$on('$locationChangeStart', function (event, next, current) {
            // redirect to login page if not logged in
            if (($location.path() !== '/login' && $location.path() !== '/register') && !$rootScope.globals.currentUser) {
                $location.path('/login');
            }
        });
    }])

//$cookieStore.get('globals').currentUser.accountType
