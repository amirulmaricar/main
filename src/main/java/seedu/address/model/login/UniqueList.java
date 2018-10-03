package seedu.address.model.login;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.login.exceptions.DuplicateUserException;
import seedu.address.model.login.exceptions.UserNotFoundException;

import java.util.Iterator;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

public class UniqueList implements Iterable<User> {
    private final ObservableList<User> internalList = FXCollections.observableArrayList();

    /**
     * Returns true if the list contains an equivalent user as the given argument.
     */
    public boolean contains(User toCheck) {
        requireNonNull(toCheck);
        return internalList.contains(toCheck);
    }

    /**
     * Adds a user to the list.
     *
     * @throws DuplicateUserException if the user to add is a duplicate of an existing user in the list.
     */
    public void add(User toAdd) throws DuplicateUserException {
        requireNonNull(toAdd);
        for (User user : internalList) {
            if (user.getUsername().equals(toAdd.getUsername())) {
                throw new DuplicateUserException();
            }
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the user {@code target} in the list with {@code editedUser}.
     *
     * @throws DuplicateUserException if the replacement is equivalent to another existing user in the list.
     * @throws UserNotFoundException if {@code target} could not be found in the list.
     */
    public void setUser(User target, User editedUser)
            throws UserNotFoundException {
        requireNonNull(editedUser);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new UserNotFoundException();
        }

        internalList.set(index, editedUser);
    }

    public User getUser(String username) {
        for (User user :internalList) {
            if (username.equals(user.getUsername().toString())) {
                return user;
            }
        }
        return null;
    }

    /**
     * Removes the equivalent user from the list.
     *
     * @throws UserNotFoundException if no such user could be found in the list.
     */
    public boolean remove(User toRemove) throws UserNotFoundException {
        requireNonNull(toRemove);
        final boolean userFoundAndDeleted = internalList.remove(toRemove);
        if (!userFoundAndDeleted) {
            throw new UserNotFoundException();
        }
        return userFoundAndDeleted;
    }

    public void setUsers(UniqueList replacement) {
        this.internalList.setAll(replacement.internalList);
    }

    public void setUsers(List<User> users) throws DuplicateUserException {
        requireAllNonNull(users);
        final UniqueList replacement = new UniqueList();
        for (User user : users) {
            replacement.add(user);
        }
        setUsers(replacement);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<User> asObservableList() {
        return FXCollections.unmodifiableObservableList(internalList);
    }

    @Override
    public Iterator<User> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof UniqueList
                && this.internalList.equals(((UniqueList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }
}
