package com.piccollage.util.config;

/**
 * Created by luyolung on 16/06/2017.
 */

public class ExceptionConsts {

    public static class CBError extends Exception {
        private static final long serialVersionUID = -1885809983328119545L;

        public CBError(String content) {
            super(content);
        }
    }

    public static class CBInvalidRequestParamsException extends CBError {
        private static final long serialVersionUID = -6185174512224237097L;

        public CBInvalidRequestParamsException(String content) {
            super(content);
        }
    }

    public static class CBUnauthorizedException extends CBError {
        private static final long serialVersionUID = 8102438337971119861L;

        public CBUnauthorizedException(String content) {
            super(content);
        }
    }

    public static class CBForbiddenException extends CBError {
        private static final long serialVersionUID = 1L;

        public CBForbiddenException(String content) {
            super(content);
        }
    }

    public static class CBObjectNotFoundException extends CBError {
        private static final long serialVersionUID = 8302746607026549722L;

        public CBObjectNotFoundException(String content) {
            super(content);
        }
    }

    public static class CBUnExpectedResponseException extends CBError {
        private static final long serialVersionUID = 2L;

        public CBUnExpectedResponseException(String content) {
            super(content);
        }
    }

    public static class CBServerMaintenanceException extends CBError {
        private static final long serialVersionUID = 3L;

        public CBServerMaintenanceException(String content) {
            super(content);
        }
    }

    public static class CBServerMaintenanceRuntimeException extends RuntimeException{
        public CBServerMaintenanceRuntimeException(String content){ super(content); }
    }

    public static class UnknownRequestActionException extends CBError {
        public UnknownRequestActionException(String message) {
            super(message);
        }
    }
}
