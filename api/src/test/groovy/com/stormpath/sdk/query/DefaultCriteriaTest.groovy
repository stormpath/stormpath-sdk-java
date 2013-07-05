/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.sdk.query

import org.testng.annotations.Test

/**
 *
 * @since 0.9
 */
class DefaultCriteriaTest {


    @Test
    void test() {

        /*

        criteria
            .property("username").iContains("foo")
            .givenName().iEquals("blah")
            .status().equals(Status.ENABLED);
            .expandDirectories(10, 20)
            .limit(10)
            .offset(50)

        criteria
            .property("username").containsIgnoreCase("foo")
            .givenName().equalsIgnoreCase("blah")
            .expandDirectories(10, 20)
            .limit(10)
            .offset(50)

        criteria
            .eqIgnoreCase(Application.USERNAME, "foo")
            .startsWithIgnoreCase(Application.DESCRIPTION, "blah")
            .expand("directories", 10, 20)
            .expand("tenant")

        criteria
            .add(Application.USERNAME.iContains("foo"))
            .add(Application.DESCRIPTION.iStartsWith("Hello"))
            .add(Application.STATUS.equals(Status.DISABLED))
            .expandDirectories(25)
            .offset(10)
            .limit(50)

        criteria
            .add(DefaultApplicationCriteria.USERNAME.iContains("foo"))
            .add(DefaultApplicationCriteria.DESCRIPTION.iStartsWith("Hello"))
            .add(DefaultApplicationCriteria.STATUS.equals(Status.DISABLED))
            .expandDirectories(25)
            .offset(10)
            .limit(50)

        criteria
            .add(USERNAME.iContains("foo"))
            .add(DESCRIPTION.iStartsWith("Hello"))
            .add(STATUS.equals(Status.DISABLED))
            .expandDirectories(25)
            .offset(10)
            .limit(50)

           c.add(c.username().iContains("foo"))
            .add(c.description().iStartsWith("Hello"))
            .add(c.status().equals(Status.DISABLED))
            .expandDirectories(25)
            .offset(10)
            .limit(50)

            Applications.criteria()
          .add(Applications.NAME.ieq("foo"))
          .name().ieq("foo")
          .


        .name().equals

         */





    }


}
