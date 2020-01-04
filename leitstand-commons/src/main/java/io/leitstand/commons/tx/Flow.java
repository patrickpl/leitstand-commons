/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.tx;

/**
 * A flow is a combination of a {@link Transaction} and the corresponding {@link Resume} operation for the suspended transaction.
 * @param <T>
 */
public interface Flow<T> extends Transaction,Resume<T> {

}
