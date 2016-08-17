'use strict';

angular.module('exampleApp')
  .config(function ($stateProvider) {
    $stateProvider
      .state('profile', {
        url: '/profile',
        templateUrl: 'profile/profile.html',
        controller: 'ProfileCtrl',
        /**
         * The Stormpath Angular SDK provides a configuration block that informs
         * UI router about protected routes.  When we use `authenticate: true`,
         * the user will be redirected to the login page if they try to access
         * this view but are not logged in.
         */
        sp: {
          authenticate: true
        }
      });
  })
  .controller('ProfileCtrl', function ($scope, $http, $timeout, $user) {
    $scope.saving = false;
    $scope.saved = false;
    $scope.error = null;
    $scope.formModel = {
      givenName: $scope.user.givenName,
      surname: $scope.user.surname,
      favoriteColor: $scope.user.customData.favoriteColor
    };

    $scope.submit = function() {
      $scope.error = null;
      $scope.saving = true;
      $http.post('/profile',$scope.formModel)
        .then(function(){
          $scope.saved = true;
          $user.get(true); // refresh the user context for the entire application
          $timeout(function(){
            $scope.saved = false;
          },2000);
        })
        .catch(function(httpResponse){
          $scope.error = httpResponse &&
            httpResponse.data ? (
              httpResponse.data.userMessage ||
              httpResponse.data.message ||
              'An error has occured'
            ) : 'Server error';
        })
        .finally(function(){
          $scope.saving = false;
        });
    };
  });