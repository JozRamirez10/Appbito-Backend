package com.appbito.appbito.constants;

import org.springframework.http.MediaType;

/**
 * Constants.
 */
public final class Constants {

    private Constants() { }

    /**
     * App constants.
     */
    public static final class App {

        public static final MediaType APP_JSON = MediaType.APPLICATION_JSON;
    }

    /**
     * Constants generals.
     */
    public static final class General {

        public static final String DATE_PATTERN = "yyyy-MM-dd";

        public static final String HOUR_PATTERN = "HH:mm";

    }

    /**
     * Constants to user.
     */
    public static final class User {

        public static final String LASTNAME_BLANK = "Lastname is required";

        public static final String BIRTHDATE_NULL = "Birthdate is required";

        public static final String EMAIL_BLANK = "Email is required";

        public static final String EMAIL_BAD_FORMAT = "It does not have an email format";

        public static final String PASSWORD_BLANK = "Password is required";

        public static final String EMAIL_ALREADY_EXISTS = "Email already exists";

        public static final String INCORRECT_PASSWORD = "Incorrect password";

        public static final String PASSWORD_DOES_NOT_MATCH = "Password does not match";
    }

    /**
     * Constants to habit.
     */
    public static final class Habit {

        public static final String DAYS_EMPTY = "Days are required";
    }

    /**
     * Constants to habit progress.
     */
    public static final class HabitProgress {

        public static final String DATE_NULL = "The date is required";

        public static final String HABIT_ID_NULL = "The habit ID is required";

        public static final String YEARS_LIST_EMPTY = "The years list cannot be empty";

    }

    /**
     * Constants errors.
     */
    public static final class Errors {

        public static final String ERROR = "Error";

        public static final String USER_NOT_FOUND = "User not found";

        public static final String HABIT_NOT_FOUND = "Habit not found";

        public static final String HABIT_PROGRESS_NOT_FOUND = "Habit Progress not found";

        public static final String MUST_NOT_BE_EMPTY = "Must not be empty";

        public static final String NAME_BLANK = "Name is required";
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

    /**
     * Constants File/Images.
     */
    public static final class Files {

        public static final String JPG_EXTENSION = "jpg";

        public static final String PROFILE_PREFIX = "profile_";

        public static final String FORMAT_NOT_ALLOWED = "Format not allowed. "
            + "Only JPEG and PNG are accepted";

        public static final String EMPTY_FILE = "Failed to store empty file";

        public static final String CANNOT_STORE = "Cannot store file outside current directory";

        public static final String CANNOT_READ = "Cannot read file outside current directory";

        public static final String FAILED_TO_STORE = "Failed to store image";

        public static final String IMAGE_NOT_FOUND = "Image not found";

    }
}
