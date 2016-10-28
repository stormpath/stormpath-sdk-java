package com.stormpath.sdk.servlet.mvc.provider;

import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.provider.OAuthProvider;
import com.stormpath.sdk.provider.Provider;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 1.2.0
 */
public abstract class BaseAuthorizationEndpointResolver implements ProviderAuthorizationEndpointResolver {
    private static final String[] RESERVED_PARAMETERS = {"client_id", "response_type", "scope", "redirect_uri", "state"};
    protected String nextUri;
    protected String callback;

    static String encode(String unencoded) {
        try {
            return URLEncoder.encode(unencoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setNextUri(String nextUri) {
        this.nextUri = nextUri;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    protected abstract String getBaseUri();

    @Override
    public String getEndpoint(HttpServletRequest request, Provider provider) {
        Assert.isInstanceOf(OAuthProvider.class, provider);
        OAuthProvider oAuthProvider = (OAuthProvider) provider;
        return getBaseUri() + '?' +
                "client_id=" + oAuthProvider.getClientId() +
                "&response_type=code" +
                "&scope=" + getScopeString(request, oAuthProvider) +
                "&redirect_uri=" + encode(getFullyQualifiedUri(request, callback)) +
                "&state=" + getState(request, provider) +
                extraParameters(request);
    }

    private String getFullyQualifiedUri(HttpServletRequest request, String path) {
        URI uri = URI.create(request.getRequestURL().toString());

        if (!path.startsWith("/")) {
            return path;
        } else {
            try {
                URI baseUri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), path, null, null);
                return baseUri.toString();
            } catch (URISyntaxException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private String extraParameters(HttpServletRequest request) {
        Map<String, String[]> extraParameters = new HashMap<>(request.getParameterMap());
        for (String key : RESERVED_PARAMETERS) {
            extraParameters.remove(key);
        }
        StringBuilder parameterBuilder = new StringBuilder();
        for (Map.Entry<String, String[]> parameterEntry : extraParameters.entrySet()) {
            for (String value : parameterEntry.getValue()) {
                parameterBuilder.append("&").append(encode(parameterEntry.getKey())).append("=").append(encode(value));
            }
        }
        return parameterBuilder.toString();
    }

    private String getState(HttpServletRequest request, Provider provider) {
        Client client = (Client) request.getAttribute(Client.class.getName());
        Assert.notNull(client, "client must be available in request attributes");
        String signingKey = client.getApiKey().getSecret();
        JwtBuilder jwtBuilder = Jwts.builder()
                .claim("redirect_uri", getRedirectUri(request))
                .claim("provider", provider.getProviderId());
        String stateParameter = request.getParameter("state");
        if (stateParameter != null) {
            jwtBuilder.claim("state", stateParameter);
        }
        return jwtBuilder
                .signWith(SignatureAlgorithm.HS256, signingKey).compact();
    }

    private String getRedirectUri(HttpServletRequest request) {
        String redirectUriParam = request.getParameter("redirect_uri");
        String path = redirectUriParam == null ? nextUri : redirectUriParam;
        return getFullyQualifiedUri(request, path);
    }

    private String getScopeString(HttpServletRequest request, OAuthProvider linkedInProvider) {
        String scopeStr = request.getParameter("scope");
        if (scopeStr == null) {
            List<String> scopeList = linkedInProvider.getScope();
            StringBuilder scopeBuilder = new StringBuilder();
            for (String scope : scopeList) {
                scopeBuilder.append(scope).append(" ");
            }
            scopeStr = scopeBuilder.toString().trim();
        }
        return encode(scopeStr);
    }
}
