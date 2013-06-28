package com.stormpath.sdk.application;

/**
 * @since 2013-06-28
 */
public interface CreateApplicationRequestVisitor {

    void visit(CreateApplicationRequest request);

    void visit(CreateApplicationAndDirectoryRequest request);
}
