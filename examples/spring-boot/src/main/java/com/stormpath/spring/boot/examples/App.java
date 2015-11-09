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
package com.stormpath.spring.boot.examples;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class App {

    public static void main(String[] args) {

        ApplicationContext ctx = SpringApplication.run(App.class, args);

        //You can interact with your application's record in Stormpath:
        Application myApp = ctx.getBean(Application.class);

        System.out.println("\n");
        System.out.println("Welcome to the '" + myApp.getName() + "' application!");

        //You can also obtain the Stormpath Client to interact with your tenant too:
        Client stormpathClient = ctx.getBean(Client.class);
        System.out.println("My tenant info: " + stormpathClient.getCurrentTenant());
        System.out.println("\n");
    }

}
