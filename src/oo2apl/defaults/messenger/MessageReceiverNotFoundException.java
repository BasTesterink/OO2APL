package oo2apl.defaults.messenger;

// TODO: revise all exceptions
public final class MessageReceiverNotFoundException extends Exception { 
	private final String details;
	
	public MessageReceiverNotFoundException(final String details) { 
		this.details = details;
	}
	
	public final String toString(){ return this.details; }
	
	private static final long serialVersionUID = 1L;

}
