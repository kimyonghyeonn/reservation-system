package kr.co.wikibook.backend.common.exception;

public class UnauthorizedException  extends RuntimeException{

    public UnauthorizedException(String message) {
        super(message);
    }
}
