$('.btn-google').click(function (event) {
    event.preventDefault();
    googleLogin($('.btn-google').attr('id'));
});

$('.btn-facebook').click(function (event) {
    event.preventDefault();
    facebookLogin($('.btn-facebook').attr('id'));
});

$('.btn-github').click(function (event) {
    event.preventDefault();
    githubLogin($('.btn-github').attr('id'));
});

$('.btn-linkedin').click(function (event) {
    event.preventDefault();
    linkedinLogin($('.btn-linkedin').attr('id'));
});

$('.btn-saml').click(function (event) {
    event.preventDefault();
    samlLogin();
});

function baseUrl() {
    return $('#baseUrl').val();
}

function linkedinLogin(clientId) {
    window.location.replace('https://www.linkedin.com/uas/oauth2/authorization' +
        '?client_id=' + clientId +
        '&response_type=code' +
        '&scope=' + encodeURIComponent('r_emailaddress r_basicprofile') +
        '&redirect_uri=' + encodeURIComponent(baseUrl() + '/callbacks/linkedin') +
        '&state=${oauthStateToken}');
}

function googleLogin(clientId) {
    window.location.replace('https://accounts.google.com/o/oauth2/auth?response_type=code' +
        '&client_id=' + clientId + '&scope=email' +
        '&redirect_uri=' + encodeURIComponent(baseUrl() + '/callbacks/google'));
}

function githubLogin(clientId) {
    window.location.replace('https://github.com/login/oauth/authorize?client_id=' + clientId);
}

function samlLogin() {
    window.location.replace(baseUrl() + '/saml');
}

function facebookLogin(appId) {
    var FB = window.FB;
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
                window.location.replace('/callbacks/facebook?queryStr&accessToken=' + FB.getAuthResponse()['accessToken']);
            } else {
                window.location.replace('/callbacks/facebook?accessToken=' + FB.getAuthResponse()['accessToken']);
            }
        }
    }, {scope: 'email'});
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