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
$('.btn-google').click(function (event) {
    event.preventDefault();
    var $btn = $('.btn-google');
    oktaLogin($btn.data('authorize_uri'), $btn.data('scope'), $btn.data('idp'));
});

$('.btn-facebook').click(function (event) {
    event.preventDefault();
    var $btn = $('.btn-facebook');
    oktaLogin($btn.data('authorize_uri'), $btn.data('scope'), $btn.data('idp'));
});

$('.btn-github').click(function (event) {
    event.preventDefault();
    var $btn = $('.btn-github');
    githubLogin($btn.attr('id'), $btn.data('scope'));
});

$('.btn-linkedin').click(function (event) {
    event.preventDefault();
    var $btn = $('.btn-linkedin');
    oktaLogin($btn.data('authorize_uri'), $btn.data('scope'), $btn.data('idp'));
});

$('.btn-saml').click(function (event) {
    event.preventDefault();
    samlLogin(event.target.id);
});

function baseUrl() {
    return $('#baseUrl').val();
}

function samlUri() {
    return $('#samlUri').val();
}

function linkedinLogin(clientId, scope) {
    scope = scope || 'r_emailaddress r_basicprofile';
    window.location.replace(
        buildUrl('https://www.linkedin.com/uas/oauth2/authorization',
            {
                client_id: clientId,
                response_type: 'code',
                scope: scope,
                redirect_uri: baseUrl() + '/callbacks/linkedin',
                state: 'oauthState' //linkedin requires state to be pass all the time.
            }
        )
    );
}

function oktaLogin(authorizeUri, scope, idp) {

    var params = {
        scope: scope,
        idp: idp,
        redirect_uri: baseUrl() + '/callbacks/okta'
    };

    window.location.replace(
        buildUrl(authorizeUri, params)
    );
}

function googleLogin(clientId, scope, hd, display, accessType) {
    scope = scope || 'email';
    var params = {
        response_type: 'code',
        client_id: clientId,
        scope: scope,
        redirect_uri: baseUrl() + '/callbacks/google'
    };
    if (hd) {
        params.hd = hd;
    }
    if (display) {
        params.display = display;
    }
    if (accessType) {
        params.access_type = accessType;
    }
    window.location.replace(
        buildUrl(
            'https://accounts.google.com/o/oauth2/auth',
            params
        )
    );
}

function githubLogin(clientId, scope) {
    window.location.replace(buildUrl('https://github.com/login/oauth/authorize', {client_id: clientId, scope: scope}));
}

function samlLogin(href) {
    window.location.replace(buildUrl(baseUrl() + samlUri(), {'href': href}));
}

function facebookLogin(appId, scope) {
    var FB = window.FB;
    scope = scope || 'email';
    scope = scope.replace(' ', ',');
    FB.init({
        appId: appId,
        cookie: true,
        xfbml: true,
        version: 'v2.4'
    });
    FB.login(function (response) {
        if (response.status === 'connected') {
            var queryStr = window.location.search.replace('?', '');
            if (queryStr) {
                window.location.replace(buildUrl(baseUrl() + '/callbacks/facebook?queryStr', {accessToken: FB.getAuthResponse()['accessToken']}));
            } else {
                window.location.replace(buildUrl(baseUrl() + '/callbacks/facebook', {accessToken: FB.getAuthResponse()['accessToken']}));
            }
        }
    }, {scope: scope});
}

(function (d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) {
        return;
    }
    js = d.createElement(s);
    js.id = id;
    js.src = '//connect.facebook.net/en_US/sdk.js';
    fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));

function getParameterByName(name) {
    var match = new RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
    return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}

function buildUrl(url, params) {
    var next = getParameterByName('next');

    if (next) {
        params.state = next;
    }

    if (!params.state) {
        params.state = '/';
    }

    if (url.includes('?')) {
        return url + '&' + $.param(params);
    }
    return url + '?' + $.param(params);
}