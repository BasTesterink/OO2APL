package oo2apl.agent;
/**
 * Context arguments are used by an agent component factory to create a context 
 * container. The use of a marker interface is to guide developers for specifying what
 * data is needed for building a context container. This prevents the use of an Object 
 * array as arguments, and hence allows compile-time type checks.
 * 
 * @author Bas Testerink
 */
public interface ContextArguments {}
