'use strict';

angular.module('exampleApp')
  .config(function ($stateProvider) {
    $stateProvider
      .state('change', {
        url: '/change?sptoken',
        templateUrl: 'change-password/change-password.html'
      });
  });