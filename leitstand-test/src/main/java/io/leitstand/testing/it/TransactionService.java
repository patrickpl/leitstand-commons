/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.testing.it;

import java.util.concurrent.Callable;

public interface TransactionService {
	void begin();
	void commit();
	void rollback();
	default <T> T transactional(Callable<T> call)  {
		begin();
		try{
			try{
				return call.call();
			}finally {
				commit();
			}
		} catch(Exception e){
			rollback();
			throw new IllegalStateException(e);
		}
	}
	default void transactional(Runnable call) {
		begin();
		try{
			try{
				call.run();
			}finally {
				commit();
			}
		} catch(Exception e){
			rollback();
			throw new IllegalStateException(e);
		}
	}
	
}
