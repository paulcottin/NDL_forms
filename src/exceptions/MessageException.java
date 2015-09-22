package exceptions;

public class MessageException extends MyException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MessageException(String message) {
		super(message);
	}
	
	public MessageException(Exception e){
		super(e);
	}
	
	public void printMessage() {
		displayMessage();
	}

}
