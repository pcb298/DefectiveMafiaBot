package net.zrx;

public class UniquenessConstraintViolationException extends Exception {
	private static final long serialVersionUID = 4060558494487861249L;

	public UniquenessConstraintViolationException(String msg) {
		super(msg);
	}
}
