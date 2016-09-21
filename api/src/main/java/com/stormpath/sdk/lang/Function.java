/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.lang;

/**
 * Represents a function that accepts one argument and produces a result.
 * <p>
 * <p>This is a functional interface whose functional method is {@link #apply(Object)}.</p>
 * <p>
 * <p>This is mostly for internal development needs to allow for certain JDK 8 functionality in a JDK 7 environment.</p>
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/function/Function.html">JDK 8 Function</a>
 * @since 1.1.0
 */
public interface Function<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t);

}