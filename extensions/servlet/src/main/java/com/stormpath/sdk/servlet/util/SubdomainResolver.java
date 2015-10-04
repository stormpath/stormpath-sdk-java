package com.stormpath.sdk.servlet.util;

import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @since 1.0.RC5
 */
public class SubdomainResolver implements Resolver<List<String>> {

    private String baseDomainName;

    public void setBaseDomainName(String baseDomainName) {
        this.baseDomainName = baseDomainName;
    }

    @Override
    public List<String> get(HttpServletRequest request, HttpServletResponse response) {

        String host = getHost(request);

        if (host == null) {
            return java.util.Collections.emptyList();
        }

        host = host.toLowerCase(Locale.ENGLISH);

        String base = baseDomainName;
        if (host.endsWith("localhost")) {
            base = "localhost";
        } else if (host.endsWith("localdomain")) {
            base = "localdomain";
        }

        List<String> domainTokens = toDomainTokens(host);
        if (Collections.isEmpty(domainTokens)) {
            return domainTokens;
        }

        int domainTokensSize = domainTokens.size();

        if (Strings.hasText(base)) {

            String[] baseTokens = tokenize(base);

            int sizeDiff = domainTokensSize - baseTokens.length;

            for (int i = domainTokensSize - 1; i >= sizeDiff; i--) {
                String domainToken = domainTokens.get(i);
                String baseToken = baseTokens[i - sizeDiff];

                if (!baseToken.equalsIgnoreCase(domainToken)) { //should always be equal
                    return java.util.Collections.emptyList();
                }
            }

            domainTokens = domainTokens.subList(0, sizeDiff);

        } else {
            //assume the last 2 tokens are the apex domain.
            if (domainTokens.size() <= 2) {
                return java.util.Collections.emptyList(); //apex domain only - no subdomain:
            } else {
                domainTokens = domainTokens.subList(0, domainTokens.size() - 2);
            }
        }

        return domainTokens;
    }

    private String[] tokenize(String s) {
        return Strings.tokenizeToStringArray(s, ".");
    }

    protected List<String> toDomainTokens(String s) {

        StringBuilder sb = new StringBuilder();

        boolean isIPV4 = true; //true until we know it's not an ipv4 address
        boolean isIPV6 = true; //true until we know it's not an ipv6 address

        List<String> tokens = new ArrayList<String>();

        for (char c : s.toCharArray()) {

            if (c == '.') {
                if (sb.length() > 0) {
                    String token = sb.toString();
                    tokens.add(token);
                    sb = new StringBuilder();
                }
            } else {
                sb.append(c);
            }

            if (c < 48 || c > 57) { //not a digit 0 through 9

                if (c != '.') {
                    isIPV4 = false;
                } else {
                    isIPV6 = false;
                }

                if (c < 97 || c > 102) { //not a lowercase letter a through f

                    if (c != ':') {
                        isIPV6 = false;
                    } else {
                        isIPV4 = false;
                    }
                }
            }
        }

        if (isIPV4 || isIPV6) {
            return java.util.Collections.emptyList();
        }

        //cleanup
        if (sb.length() > 0) {
            String token = sb.toString();
            tokens.add(token);
        }

        return tokens;
    }

    protected String getHost(HttpServletRequest request) {

        String host = request.getHeader("Host");
        if (host == null) { //HTTP 1.0?
            host = request.getServerName();
        }

        if (host.startsWith("[")) { //ipv6 host - we don't care about these for subdomain resolution:
            return null;
        }

        int i = host.lastIndexOf(':');
        if (i > -1) {
            host = host.substring(0, i);
        }

        return host;
    }
}
