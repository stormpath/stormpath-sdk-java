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
import java.util.UUID;

/**
 * @since 1.2.0
 */
public abstract class BaseAuthorizationEndpointResolver implements ProviderAuthorizationEndpointResolver {
    private static final String[] RESERVED_PARAMETERS = {"client_id", "response_type", "scope", "redirect_uri",
            "state", "organization_name_key", "organization_href"};
    protected String callback;

    static String encode(String unencoded) {
        try {
            return URLEncoder.encode(unencoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    protected abstract String getBaseUri();

    @Override
    public String getEndpoint(HttpServletRequest request, String applicationCallbackUri, Provider provider) {
        Assert.isInstanceOf(OAuthProvider.class, provider);
        OAuthProvider oAuthProvider = (OAuthProvider) provider;
        return getBaseUri() + '?' +
                "client_id=" + oAuthProvider.getClientId() +
                "&response_type=code" +
                "&scope=" + getScopeString(request, oAuthProvider) +
                "&redirect_uri=" + encode(getFullyQualifiedUri(request, callback)) +
                "&state=" + getState(request, applicationCallbackUri, provider) +
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

    private String getState(HttpServletRequest request, String applicationCallbackUri, Provider provider) {
        String redirectUriQueryParam = request.getParameter("redirect_uri");
        String redirectUri = redirectUriQueryParam == null ? applicationCallbackUri :
                getFullyQualifiedUri(request, redirectUriQueryParam);

        Client client = (Client) request.getAttribute(Client.class.getName());
        Assert.notNull(client, "client must be available in request attributes");
        String signingKey = client.getApiKey().getSecret();
        JwtBuilder jwtBuilder = Jwts.builder()
                .claim("jti", UUID.randomUUID().toString())
                .claim("redirect_uri", redirectUri)
                .claim("provider", provider.getProviderId());
        addOptionalClaim(jwtBuilder, "state", request);
        String orgHref = request.getParameter("organization_href");
        String orgNameKey = request.getParameter("organization_name_key");
        Assert.isTrue(orgHref == null || orgNameKey == null,
                "Cannot specify both organization_href and organization_name_key");
        addOptionalClaim(jwtBuilder, "organization_href", orgHref);
        addOptionalClaim(jwtBuilder, "organization_name_key", orgNameKey);
        return jwtBuilder
                .signWith(SignatureAlgorithm.HS256, signingKey).compact();
    }

    private void addOptionalClaim(JwtBuilder jwtBuilder, String claimKey, HttpServletRequest request) {
        addOptionalClaim(jwtBuilder, claimKey, request.getParameter(claimKey));
    }

    private void addOptionalClaim(JwtBuilder jwtBuilder, String claimKey, String claim) {
        if (claim != null) {
            jwtBuilder.claim(claimKey, claim);
        }
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
