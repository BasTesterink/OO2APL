package oo2apl.agent;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
 

/**
 * An agent ID is used as an identifier throughout the agent platform for an individual 
 * agent. Hence take care that each agent has a unique id.
 * @author Bas Testerink
 * @author Arie van den Berg
 */
public final class AgentID { 
	/** The random generator that generates pseudo unique keys. */
	private static final Random keyGenerator = new Random();
	
	/** The serial number generator. */
	private static final AtomicLong serialGenerator = new AtomicLong(1);
	
	/** The serial number of this key. */
	private final long serial;
	
	/** The value of this key instance. */
	private final int key;
	
	/** Private constructor: enforces immutability. Returns a new key object. */
	private AgentID(final int key, final long serial){
		this.key = key;
		this.serial = serial;
	}
	
	/** Returns a new unique instance of this key object. Use the returned key in a final
	 * field.
	 */
	public final static AgentID newInstance(){
		final int key = keyGenerator.nextInt();
		final long serial = serialGenerator.getAndIncrement();
		return new AgentID(key,serial);
	}
	
	@Override
	public final String toString(){
		return "Immutable Key Object #" + this.serial;
	}
	
	@Override
	public final boolean equals(final Object obj){
		if(obj == this) return true;
		if(!(obj instanceof AgentID)) return false;
		final AgentID other = (AgentID)obj;
		return other.serial == this.serial;
	}
	
	@Override
	public final int hashCode(){
		return this.key;
	}
}
