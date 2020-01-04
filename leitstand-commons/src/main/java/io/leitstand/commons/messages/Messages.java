/*
 * (c) RtBrick, Inc - All rights reserved, 2015 - 2019
 */
package io.leitstand.commons.messages;

import static java.util.Collections.unmodifiableList;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;

/**
 * A container to collect all messages created during the processing of a single request.
 * <p>
 * <code>Messages</code> is a <code>{@literal @RequestScoped}</code> managed CDI bean. A service
 * must obtain the current <code>Messages</code> instance and add all messages accordingly.
 * </p>
 * @see Message
 */
@RequestScoped
public class Messages implements Iterable<Message> {
	
	private List<Message> messageList;
	
	/**
	 * Returns a predicate to search for error messages.
	 * @return a predicate to search for error messages.
	 */
	public static final Predicate<Message> errors(){
		return message -> message.getSeverity() == Message.Severity.ERROR;
	}
	
	@PostConstruct
	void init(){
		messageList = new LinkedList<>();
	}
	
	/**
	 * Adds a new message.
	 * @param message - the message to be added.
	 */
	public void add(Message message){
		messageList.add(message);
	}

	/**
	 * Returns an unmodifiable iterator over all message in the order as they have been added to this collection.
	 * @return an unmodifiable iterator over all existing messages.
	 */
	@Override
	public Iterator<Message> iterator() {
		return unmodifiableList(messageList).iterator();
	}
	
	/**
	 * Returns <code>true</code> when no message has been added to this container, 
	 * returns <code>false</code> otherwise.
	 * @return <code>true</code> if this container contains at least on message.
	 */
	public boolean isEmpty(){
		return messageList.isEmpty();
	}
	
	/**
	 * Returns <code>true</code> if this container contains at least one message that has the specified predicate.
	 * @param predicate - the predicate a message must satisfy
	 * @return <code>true</code> if this contains contains at least on message with the specified predicate
	 */
	public boolean contains(Predicate<Message> predicate) {
		for(Message message : messageList) {
			if(predicate.test(message)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the number of stored messages.
	 * @return the number of stored messages.
	 */
	public int size(){
		return messageList.size();
	}
	
}
