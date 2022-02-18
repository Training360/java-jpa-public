package activitytracker;

public class ActivityNotFoundException extends RuntimeException {

    public ActivityNotFoundException() {
        super();
    }

    public ActivityNotFoundException(String message) {
        super(message);
    }

    public ActivityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
