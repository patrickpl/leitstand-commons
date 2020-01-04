/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.flow;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Stereotype;

/**
 * The <code>ControlFlow</code> stereotype.
 * <p>
 * A control flow is a stateless dependent services that orchestrates the execution of transactional services in a certain order.
 * A sample flow is the completion of a scheduler task: first, a transaction updates the state of the completed task. 
 * Once this is done, a subsequent transaction searches the next tasks to be executed.
 * </p>
 */
@Retention(RUNTIME)
@Target(TYPE)
@Stereotype
@Dependent
public @interface ControlFlow {

}