'use strict';

angular.module('exampleApp', [
  'ui.router',
  'stormpath',
  'stormpath.templates'
])
  .config(function ($stateProvider, $urlRouterProvider, $locationProvider, STORMPATH_CONFIG) {
    $urlRouterProvider
      .otherwise('/');

    $locationProvider.html5Mode(true);

    /*
     At the moment, JSON is not the default content type when posting forms, but
     most of our framework integrations are expecting JSON, so we need to manually set
     this.  JSON will be the default in the next major release of the Angular SDK.
    */
    STORMPATH_CONFIG.FORM_CONTENT_TYPE = 'application/json';
  })
  .run(function($stormpath,$rootScope,$state){

    /*
      In this example we use UI router, and this
      is how we tell the Stormpath module which
      states we would like to use to use for login
      and post-login
     */

    $stormpath.uiRouter({
      loginState: 'login',
      defaultPostLoginState: 'home'
    });

    /*
      We want to redirect users back to the home
      state after they logout, so we watch for the
      logout event and then transition them to the
      login state
     */
    $rootScope.$on('$sessionEnd',function () {
      $state.transitionTo('home');
    });
  });