/*
 * Copyright 2017 Stormpath, Inc.
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
package com.stormpath.tutorial;

/**
 * @since 1.3.0
 */
public class AccountInfo {

    private String href;
    private String fullName;
    private String email;

    public AccountInfo(String email, String fullName, String href) {
        this.email = email;
        this.fullName = fullName;
        this.href = href;
    }

    public String getHref() {
        return href;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }
    
}
