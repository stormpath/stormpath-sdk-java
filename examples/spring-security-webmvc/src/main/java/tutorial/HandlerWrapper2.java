/*
 * Copyright 2015 Stormpath, Inc.
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
package tutorial;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.AuthenticationResultVisitor;
import com.stormpath.sdk.servlet.mvc.LoginController;
import com.stormpath.spring.config.StormpathWebMvcConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Date: 5/20/15
 */
@Component
public class HandlerWrapper2 extends SavedRequestAwareAuthenticationSuccessHandler {


    @Autowired
    protected StormpathWebMvcConfiguration stormpathWebMvcConfiguration;

    @Autowired
    protected HandlerWrapper handlerWrapper;

    public HandlerWrapper2() {
        System.out.println();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {

//        AuthenticationResult authencationResult = new AuthenticationResult() {
//            @Override
//            public Account getAccount() {
//                return (Account) authentication.getPrincipal();
//            }
//
//            @Override
//            public void accept(AuthenticationResultVisitor visitor) {
//            }
//
//            @Override
//            public String getHref() {
//                return getAccount().getHref();
//            }
//        };

        handlerWrapper.onAuthenticationSuccess(request, response, authentication);
        //this.saveResult(request, response, authencationResult);
    }


}
