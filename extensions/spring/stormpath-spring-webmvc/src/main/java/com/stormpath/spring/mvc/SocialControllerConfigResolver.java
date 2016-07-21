///*
// * Copyright 2016 Stormpath, Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.stormpath.spring.mvc;
//
//import com.stormpath.sdk.lang.Assert;
//
///**
// * @since 1.0.0
// */
//public abstract class SocialControllerConfigResolver extends AbstractSpringControllerConfigResolver {
//
//    private boolean enabled;
//
//    private String uri;
//
//    private String scope;
//
//    public SocialControllerConfigResolver(boolean enabled, String uri, String scope) {
////        Assert.hasText(uri, "uri cannot be null or empty");
////        Assert.hasText(uri, "scope cannot be null or empty");
//        this.enabled = enabled;
//        this.uri = uri;
//        this.scope = scope;
//    }
//
//    @Override
//    public String getView() {
//        return null; //Not relevant
//    }
//
//    @Override
//    public String getUri() {
//        return this.uri;
//    }
//
//    @Override
//    public String getNextUri() {
//        return null; //Not relevant;
//    }
//
//    public String getScope() {
//        return this.scope;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return this.enabled;
//    }
//
//    @Override
//    public final String getControllerKey() {
//        return "social." + getSocialId();
//    }
//
//    public abstract String getSocialId();
//
//    @Override
//    protected String[] getDefaultFieldOrder() {
//        return new String[0];
//    }
//}
