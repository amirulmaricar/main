package seedu.address.model.distributor.exceptions;

/**
 * Signals that the operation will result in duplicate Distributors (Distributors are considered duplicates if
 * they have the same identity).
 */
public class DuplicateDistributorException extends RuntimeException {
    public DuplicateDistributorException() {
        super("Operation would result in duplicate distributors");
    }
}
