package com.appbito.appbito.constants;

/**
 * Constants.
 */
public final class Constants {

    private Constants() { }

    public static final class Habit {

        public static final String NAME_BLANK = "Name is required";

        public static final String HOUR_PATTERN = "HH:mm";

        public static final String DAYS_EMPTY = "Days are required";
    }

    /**
     * Constants errors.
     */
    public static final class Errors {

        public static final String ERROR = "Error";

        public static final String USER_NOT_FOUND = "User not found";

        public static final String HABIT_NOT_FOUND = "Habit not found";
    }

    /**
     * Contants exceptions.
     */
    public static final class Exceptions {

        public static final String INVALID_FORMAT_TOKEN = "Invalid format token";

        public static final String RESOURCE_NOT_FOUND = "Resource not found";

        public static final String ACCESS_DENIED = "Access denied: "
            + "You do not have permission for this resource";
    }

}
