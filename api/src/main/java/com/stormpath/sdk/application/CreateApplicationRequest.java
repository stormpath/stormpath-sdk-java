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
package com.stormpath.sdk.application;

import com.stormpath.sdk.lang.Assert;

/**
 * @since 0.9
 */
public class CreateApplicationRequest {

    private final Application application;

    public CreateApplicationRequest(Application application) {
        Assert.notNull(application, "Application argument cannot be null.");
        this.application = application;
    }

    public Application getApplication() {
        return application;
    }

    public void accept(CreateApplicationRequestVisitor visitor) {
        visitor.visit(this);
    }

    public static Builder with(Application application) {
        return new Builder().with(application);
    }

    public static class Builder {

        private Application application;
        private boolean createDirectory;
        private String directoryName;

        public Builder() {
        }

        public Builder with(Application application) {
            this.application = application;
            return this;
        }

        public Builder createDirectory(boolean createDirectory) {
            this.createDirectory = createDirectory;
            return this;
        }

        public Builder withDirectoryName(String directoryName) {
            if (directoryName != null) {
                this.createDirectory = true;
            }
            this.directoryName = directoryName;
            return this;
        }

        public CreateApplicationRequest build() {
            if (createDirectory) {
                return new CreateApplicationAndDirectoryRequest(application, directoryName);
            }
            return new CreateApplicationRequest(application);
        }
    }
}
