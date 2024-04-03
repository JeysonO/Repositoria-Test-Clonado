package pe.com.amsac.tramite.api.util;

import java.io.PrintStream;
import java.io.PrintWriter;

public class InternalErrorException extends Exception {

    private static final long serialVersionUID = 612737565022063038L;
    private Exception encapsulatedException;

    public InternalErrorException(String message) {
        this.encapsulatedException = new Exception(message);
    }

    public InternalErrorException(Exception exception) {
        this.encapsulatedException = exception;
    }

    public Exception getEncapsulatedException() {
        return this.encapsulatedException;
    }

    public String getMessage() {
        return this.encapsulatedException.getMessage();
    }

    public void printStackTrace() {
        printStackTrace(System.err);
    }

    public void printStackTrace(PrintStream printStream) {
        super.printStackTrace(printStream);
        printStream.println("*** Information about encapsulated exception ***");
        this.encapsulatedException.printStackTrace(printStream);
    }

    public void printStackTrace(PrintWriter printWriter) {
        super.printStackTrace(printWriter);
        printWriter.println("*** Information about encapsulated exception ***");
        this.encapsulatedException.printStackTrace(printWriter);
    }
}
