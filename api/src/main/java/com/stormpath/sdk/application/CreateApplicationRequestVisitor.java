package com.stormpath.sdk.application;

/**
 * @since 0.8
 */
public interface CreateApplicationRequestVisitor {

    void visit(CreateApplicationRequest request);

    void visit(CreateApplicationAndDirectoryRequest request);
}
