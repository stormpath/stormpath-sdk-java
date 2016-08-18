'use strict';

/*
 * Copyright 2016 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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