package network;

/**
 * Kuuntelijaa ei ole rekister�ity.
 */
public class NoListenerRegisteredException extends RuntimeException {
    
    /**
     * Luo NoListenerRegisteredException.
     * @param message Exceptionin syy tai muu viesti.
     */
    public NoListenerRegisteredException(String message) {
	super(message);
    }
}
