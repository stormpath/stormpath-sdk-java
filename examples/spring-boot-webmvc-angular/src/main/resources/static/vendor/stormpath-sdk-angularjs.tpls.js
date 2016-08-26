/**
 * stormpath-sdk-angularjs
 * Copyright Stormpath, Inc. 2016
 * 
 * @version v1.0.0-dev-2016-03-02
 * @link https://github.com/stormpath/stormpath-sdk-angularjs
 * @license Apache-2.0
 */

/* commonjs package manager support (eg componentjs) */
if (typeof module !== "undefined" && typeof exports !== "undefined" && module.exports === exports){
  module.exports = 'stormpath.templates';
}

(function (window, angular, undefined) {

angular.module('stormpath.templates', ['spEmailVerification.tpl.html', 'spLoginForm.tpl.html', 'spPasswordResetForm.tpl.html', 'spPasswordResetRequestForm.tpl.html', 'spRegistrationForm.tpl.html']);

angular.module("spEmailVerification.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("spEmailVerification.tpl.html",
    "<div class=row><div class=\"col-sm-offset-4 col-xs-12 col-sm-4\"><p ng-show=verifying class=\"alert alert-warning\">We are verifying your account</p><p ng-show=verified class=\"alert alert-success\">Your account has has been verified! <a href=/login>Login Now.</a></p><p ng-show=reVerificationSent class=\"alert alert-success\">We have sent a new verification message to your email address, please check your email for this message.</p><div ng-show=showVerificationError class=\"alert alert-danger\">This email verification link is not valid. If you need us to re-send an email verification message, please enter your email address or username below.</div><div ng-show=resendFailed class=\"alert alert-danger\">Sorry, there was a problem with that email or username. Please try again.</div></div></div><div class=row><div class=col-xs-12><form class=form-horizontal ng-show=\"needsReVerification && !reVerificationSent\" ng-submit=submit()><div class=form-group><label for=spEmail class=\"col-xs-12 col-sm-4 control-label\">Email or Username</label><div class=\"col-xs-12 col-sm-4\"><input class=form-control id=spUsername ng-model=formModel.username placeholder=\"Username or Email\" ng-disabled=posting></div></div><div class=form-group><div class=\"col-sm-offset-4 col-xs-12\"><p class=text-danger ng-show=error ng-bind=error></p><button type=submit class=\"btn btn-primary\" ng-disabled=posting>Re-Send Verification</button></div></div></form></div></div>");
}]);

angular.module("spLoginForm.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("spLoginForm.tpl.html",
    "<style>.btn.btn-social {\n" +
    "    margin-right: 7px;\n" +
    "    min-width: 100px;\n" +
    "  }\n" +
    "\n" +
    "  .btn.btn-facebook {\n" +
    "    color: white;\n" +
    "    background-color: #3B5998;\n" +
    "    border-color: #37528C;\n" +
    "  }\n" +
    "  .btn.btn-facebook:hover,\n" +
    "  .btn.btn-facebook:focus {\n" +
    "    color: white;\n" +
    "    background-color: #2F487B;\n" +
    "    border-color: #2F487B;\n" +
    "  }\n" +
    "\n" +
    "  .btn.btn-google {\n" +
    "    color: white;\n" +
    "    background-color: #dc4e41;\n" +
    "    border-color: #C1453A;\n" +
    "  }\n" +
    "  .btn.btn-google:hover,\n" +
    "  .btn.btn-google:focus {\n" +
    "    color: white;\n" +
    "    background-color: #C74539;\n" +
    "    border-color: #AF4138;\n" +
    "  }\n" +
    "\n" +
    "  .sp-loading {\n" +
    "    text-align: center;\n" +
    "  }</style><div class=row><div class=col-xs-12><div ng-show=!viewModel class=sp-loading>Loading...</div><form class=form-horizontal ng-hide=\"accepted || !viewModel\" ng-submit=submit()><div class=form-group ng-repeat=\"field in viewModel.form.fields\"><label for=sp-{{field.name}} class=\"col-xs-12 col-sm-4 control-label\">{{field.label}}</label><div class=\"col-xs-12 col-sm-4\"><input class=form-control name={{field.name}} id=sp-{{field.name}} type={{field.type}} ng-model=formModel[field.name] placeholder={{field.placeholder}} ng-disabled=posting ng-required=field.required></div></div><div class=form-group><div class=\"col-sm-offset-4 col-sm-4\"><p class=text-danger ng-show=error ng-bind=error></p><button type=submit class=\"btn btn-primary\" ng-disabled=posting>Login</button> <a href=/forgot class=pull-right>Forgot Password</a></div></div><div class=form-group ng-show=viewModel.accountStores.length><div class=\"col-sm-offset-4 col-sm-4\"><p>Or login with:</p><button ng-repeat=\"accountStore in viewModel.accountStores\" type=button class=\"btn btn-social btn-{{accountStore.provider.providerId}}\" sp-social-login={{accountStore.provider.providerId}} sp-client-id={{accountStore.provider.clientId}} sp-scope={{accountStore.provider.scope}}>{{providerName}}</button></div></div></form></div></div>");
}]);

angular.module("spPasswordResetForm.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("spPasswordResetForm.tpl.html",
    "<div class=row><div class=\"col-sm-offset-4 col-xs-12 col-sm-4\"><p ng-show=verifying class=\"alert alert-warning text-center\">We are verifying this link</p><p class=\"alert alert-success\" ng-show=reset>Your new password has been set. Please <a href=/login>Login Now</a>.</p><div ng-show=showVerificationError class=\"alert alert-danger\">This password reset link is not valid. You may request another link by <a href=/forgot>clicking here</a>.</div></div></div><div class=row><div class=col-xs-12><form class=form-horizontal ng-show=\"verified && !reset\" ng-submit=submit()><div class=form-group><label for=spEmail class=\"col-xs-12 col-sm-4 control-label\">New Password</label><div class=\"col-xs-12 col-sm-4\"><input class=form-control id=spUsername ng-model=formModel.password placeholder=\"New Password\" type=password ng-disabled=posting></div></div><div class=form-group><label for=spEmail class=\"col-xs-12 col-sm-4 control-label\">Confirm New Password</label><div class=\"col-xs-12 col-sm-4\"><input class=form-control id=spUsername ng-model=formModel.confirmPassword placeholder=\"Confirm New Password\" type=password ng-disabled=posting></div></div><div class=form-group><div class=\"col-sm-offset-4 col-sm-4\"><p class=\"alert alert-danger\" ng-show=error ng-bind=error></p><button type=submit class=\"btn btn-primary\" ng-disabled=posting>Set New Password</button></div></div></form></div></div>");
}]);

angular.module("spPasswordResetRequestForm.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("spPasswordResetRequestForm.tpl.html",
    "<div class=row><div class=\"col-sm-offset-4 col-xs-12 col-sm-4\"><p ng-show=sent class=\"alert alert-success\">We have sent a password reset link to the email address of the account that you specified. Please check your email for this message, then click on the link.</p><p ng-show=sent class=pull-right><a href=/login>Back to Login</a></p><div ng-show=requestFailed class=\"alert alert-danger\">Sorry, there was a problem with that email or username. Please try again.</div></div></div><div class=row><div class=col-xs-12><form class=form-horizontal ng-hide=sent ng-submit=submit()><div class=form-group><label for=spEmail class=\"col-xs-12 col-sm-4 control-label\">Email or Username</label><div class=\"col-xs-12 col-sm-4\"><input class=form-control id=spEmail ng-model=formModel.email placeholder=\"Your Email Address\" ng-disabled=posting></div></div><div class=form-group><div class=\"col-sm-offset-4 col-xs-12\"><p class=text-danger ng-show=error ng-bind=error></p><button type=submit class=\"btn btn-primary\" ng-disabled=posting>Request Password Reset</button></div></div></form></div></div>");
}]);

angular.module("spRegistrationForm.tpl.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("spRegistrationForm.tpl.html",
    "<style>.btn.btn-social {\n" +
    "    margin-right: 7px;\n" +
    "    min-width: 100px;\n" +
    "  }\n" +
    "\n" +
    "  .btn.btn-facebook {\n" +
    "    color: white;\n" +
    "    background-color: #3B5998;\n" +
    "    border-color: #37528C;\n" +
    "  }\n" +
    "  .btn.btn-facebook:hover,\n" +
    "  .btn.btn-facebook:focus {\n" +
    "    color: white;\n" +
    "    background-color: #2F487B;\n" +
    "    border-color: #2F487B;\n" +
    "  }\n" +
    "\n" +
    "  .btn.btn-google {\n" +
    "    color: white;\n" +
    "    background-color: #dc4e41;\n" +
    "    border-color: #C1453A;\n" +
    "  }\n" +
    "  .btn.btn-google:hover,\n" +
    "  .btn.btn-google:focus {\n" +
    "    color: white;\n" +
    "    background-color: #C74539;\n" +
    "    border-color: #AF4138;\n" +
    "  }\n" +
    "\n" +
    "  .sp-loading {\n" +
    "    text-align: center;\n" +
    "  }</style><div class=row><div class=\"col-sm-offset-4 col-xs-12 col-sm-4\"><p class=\"alert alert-success\" ng-show=\"created && !enabled\">Your account has been created. Please check your email for a verification link.</p><p ng-show=\"created && !enabled\" class=pull-right><a href=/login>Back to Login</a></p><p class=\"alert alert-success\" ng-show=\"created && enabled && !authenticating\">Your account has been created. <a href=/login>Login Now</a>.</p></div></div><div class=row><div class=col-xs-12><div ng-show=!viewModel class=sp-loading>Loading...</div><form class=form-horizontal ng-hide=\"!viewModel || (created && !authenticating)\" ng-submit=submit()><div class=form-group ng-repeat=\"field in viewModel.form.fields\"><label for=sp-{{field.name}} class=\"col-xs-12 col-sm-4 control-label\">{{field.label}}</label><div class=\"col-xs-12 col-sm-4\"><input class=form-control name={{field.name}} id=sp-{{field.name}} type={{field.type}} ng-model=formModel[field.name] placeholder={{field.placeholder}} ng-disabled=creating ng-required=field.required></div></div><div class=form-group><div class=\"col-sm-offset-4 col-sm-4\"><p class=\"alert alert-danger\" ng-show=error ng-bind=error></p><button type=submit class=\"btn btn-primary\" ng-disabled=creating>Register</button></div></div><div class=form-group ng-show=viewModel.accountStores.length><div class=\"col-sm-offset-4 col-sm-4\"><p>Or register with:</p><button ng-repeat=\"accountStore in viewModel.accountStores\" type=button class=\"btn btn-social btn-{{accountStore.provider.providerId}}\" sp-social-login={{accountStore.provider.providerId}} sp-client-id={{accountStore.provider.clientId}} sp-scope={{accountStore.provider.scope}}>{{providerName}}</button></div></div></form></div></div>");
}]);
})(window, window.angular);
