package gyroorm;public class GyroORMException extends Exception {	public GyroORMException() {		super();	}	public GyroORMException(String message) {		super(message);	}	public GyroORMException(String message, Throwable th) {		super(message, th);	}	public GyroORMException(Throwable th) {  	super(th);	}}