package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.AddressBookChangedEvent;
import seedu.address.commons.events.model.SalesHistoryChangedEvent;
import seedu.address.commons.events.model.UserDatabaseChangedEvent;
import seedu.address.commons.events.model.UserDeletedEvent;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.model.distributor.Distributor;
import seedu.address.model.login.Password;
import seedu.address.model.login.UniqueUserList;
import seedu.address.model.login.User;
import seedu.address.model.login.Username;
import seedu.address.model.login.exceptions.AuthenticatedException;
import seedu.address.model.login.exceptions.DuplicateUserException;
import seedu.address.model.login.exceptions.UserNotFoundException;
import seedu.address.model.product.Product;
import seedu.address.model.saleshistory.ReadOnlySalesHistory;
import seedu.address.model.saleshistory.SalesHistory;
import seedu.address.model.timeidentifiedclass.Reminder;
import seedu.address.model.timeidentifiedclass.TimeIdentifiedClass;
import seedu.address.model.timeidentifiedclass.Transaction;
import seedu.address.model.timeidentifiedclass.exceptions.DuplicateReminderException;
import seedu.address.model.timeidentifiedclass.exceptions.DuplicateTransactionException;
import seedu.address.model.timeidentifiedclass.exceptions.InvalidTimeFormatException;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.Storage;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final Storage storage;
    private final VersionedProductDatabase versionedAddressBook;
    private final VersionedUserDatabase versionedUserDatabase;
    private final VersionedSalesHistory versionedSalesHistory;

    private final FilteredList<Distributor> filteredDistributors;
    private final FilteredList<Product> filteredProducts;


    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, UserPrefs userPrefs,
                        ReadOnlyUserDatabase userDatabase, ReadOnlySalesHistory salesHistory, Storage storage) {
        super();
        requireAllNonNull(addressBook, userPrefs, userDatabase);
        logger.fine("Initializing with address book: " + addressBook + ", user prefs " + userPrefs
                + ", user database " + userDatabase + ", and sales history " + salesHistory);
        this.storage = storage;
        versionedUserDatabase = new VersionedUserDatabase(userDatabase);
        versionedAddressBook = new VersionedProductDatabase(addressBook);
        versionedSalesHistory = new VersionedSalesHistory(salesHistory);

        filteredDistributors = new FilteredList<>(versionedAddressBook.getDistributorList());
        filteredProducts = new FilteredList<>(versionedAddressBook.getPersonList());
    }

    /**
     * Initializes a ModelManager without sales history.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, UserPrefs userPrefs,
                        ReadOnlyUserDatabase userDatabase, Storage storage) {
        this(addressBook, userPrefs, userDatabase, new SalesHistory(), storage);
    }

    public ModelManager(Storage storage) {
        this(new ProductDatabase(), new UserPrefs(),
                new UserDatabase(), new SalesHistory(),
                storage);
    }

    /**
     * Allows us to set sales history to what is required.
     */
    public void setSalesHistory(ReadOnlySalesHistory salesHistory) {
        versionedSalesHistory.resetData(salesHistory);
    }

    // ============== ProductDatabase Modifiers =============================================================

    @Override
    public void resetData(ReadOnlyAddressBook newData) {
        versionedAddressBook.resetData(newData);
        indicateAddressBookChanged();
    }

    @Override
    public ReadOnlyAddressBook getProductInfoBook() {
        return versionedAddressBook;
    }

    @Override
    public ReadOnlyAddressBook getDistributorInfoBook() {
        return versionedAddressBook;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateAddressBookChanged() {
        raise(new AddressBookChangedEvent(versionedAddressBook));
    }

    @Override
    public boolean hasDistributor(Distributor distributor) {
        requireNonNull(distributor);
        return versionedAddressBook.hasDistributor(distributor);
    }

    @Override
    public boolean hasPerson(Product product) {
        requireNonNull(product);
        return versionedAddressBook.hasPerson(product);
    }

    @Override
    public void deleteDistributor(Distributor target) {
        versionedAddressBook.removeDistributor(target);
        indicateAddressBookChanged();
    }

    @Override
    public void deletePerson(Product target) {
        versionedAddressBook.removePerson(target);
        indicateAddressBookChanged();
    }

    /**
     * Updates the addressBook and its storage path using the {@code username} provided.
     * @param username
     */
    private void reloadAddressBook(Username username) {
        Optional<ReadOnlyAddressBook> addressBookOptional;
        ReadOnlyAddressBook newAddressBook;

        try {
            addressBookOptional = storage.readAddressBook();
            if (!addressBookOptional.isPresent()) {
                logger.info("Data file not found. Will be starting with a sample ProductDatabase");
            }
            newAddressBook = addressBookOptional.orElseGet(SampleDataUtil::getSampleAddressBook);
        } catch (DataConversionException e) {
            newAddressBook = new ProductDatabase();
            logger.warning("Data file not in the correct format. Will be starting with an empty ProductDatabase");
        } catch (IOException e) {
            newAddressBook = new ProductDatabase();
            logger.warning("Problem while reading from the file. Will be starting with an empty ProductDatabase");
        }
        versionedAddressBook.resetData(newAddressBook);
    }

    /**
     * Updates the sales history and its storage path using the {@code username} provided.
     * @param username
     */
    private void reloadSalesHistory(Username username) {
        Optional<ReadOnlySalesHistory> salesHistoryOptional;
        ReadOnlySalesHistory newSalesHistory;

        try {
            salesHistoryOptional = storage.readSalesHistory();
            if(!salesHistoryOptional.isPresent()) {
                logger.info("Data file not found. Will be starting with a sample SalesHistory");
            }
            newSalesHistory = salesHistoryOptional.orElseGet(SampleDataUtil::getSampleSalesHistory);
        } catch (DataConversionException e) {
            newSalesHistory = new SalesHistory();
            logger.warning("Data file not in the correct format. Will be starting with an empty SalesHistory");
        } catch (IOException e) {
            newSalesHistory = new SalesHistory();
            logger.warning("Problem while reading from the file. Will be starting with an empty SalesHistory");
        }

        versionedSalesHistory.resetData(newSalesHistory);
    }

    private void reloadAddressBookAndSalesHistory(Username username) {
        storage.update(versionedUserDatabase.getUser(username));
        reloadAddressBook(username);
        reloadSalesHistory(username);
    }

    //============== UserDatabase Modifiers =============================================================

    @Override
    public ReadOnlyAddressBook getUserDatabase() {
        return versionedAddressBook;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateUserDatabaseChanged() {
        raise(new UserDatabaseChangedEvent(versionedUserDatabase));
    }

    /** Raises an event to indicate a user has been deleted */
    private void indicateUserDeleted(User user) {
        raise(new UserDeletedEvent(user));
    }


    @Override
    public synchronized void deleteUser(User target) throws UserNotFoundException {
        versionedUserDatabase.removeUser(target);
        indicateUserDatabaseChanged();
        indicateUserDeleted(target);
    }

    @Override
    public synchronized void addUser(User person) throws DuplicateUserException {
        versionedUserDatabase.addUser(person);
        indicateUserDatabaseChanged();
    }

    @Override
    public boolean checkAuthentication(Username username, Password password) throws AuthenticatedException {
        boolean result = versionedUserDatabase.checkAuthentication(username, password);
        if (hasLoggedIn() && result) {
            reloadAddressBookAndSalesHistory(username);
        }
        return result;
    }

    @Override
    public boolean checkCredentials(Username username, Password password) throws AuthenticatedException {
        return versionedUserDatabase.checkCredentials(username, password);
    }

    @Override
    public void updateUserPassword(User target, User userWithNewPassword)
            throws UserNotFoundException {
        requireAllNonNull(target, userWithNewPassword);
        versionedUserDatabase.updateUserPassword(target, userWithNewPassword);
        indicateUserDatabaseChanged();
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return null;
    }

    @Override
    public User getLoggedInUser() {
        return versionedUserDatabase.getLoggedInUser();
    }


    @Override
    public boolean hasLoggedIn() {
        return versionedUserDatabase.hasLoggedIn();
    }

    @Override
    public void setLoginStatus(boolean status) {
        versionedUserDatabase.setLoginStatus(status);
    }

    @Override
    public void setUsersList(UniqueUserList uniqueUserList) {
        versionedUserDatabase.setUniqueUserList(uniqueUserList);
    }

    //=========== Filtered Person List Accessors =============================================================

    @Override
    public void addDistributor(Distributor distributor) {
        versionedAddressBook.addDistributor(distributor);
        updateFilteredDistributorList(PREDICATE_SHOW_ALL_DISTRIBUTORS);
        indicateAddressBookChanged();
    }

    @Override
    public void addPerson(Product product) {
        versionedAddressBook.addPerson(product);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        indicateAddressBookChanged();
    }

    @Override
    public void updatePerson(Product target, Product editedProduct) {
        requireAllNonNull(target, editedProduct);
        versionedAddressBook.updatePerson(target, editedProduct);
        indicateAddressBookChanged();
    }

    @Override
    public void updateDistributor(Distributor target, Distributor editedDistributor) {
        requireAllNonNull(target, editedDistributor);

        versionedAddressBook.updateDistributor(target, editedDistributor);
        indicateAddressBookChanged();
    }

    //=========== Filtered Product List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Product} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableList<Distributor> getFilteredDistributorList() {
        return FXCollections.unmodifiableObservableList(filteredDistributors);
    }

    @Override
    public void updateFilteredDistributorList(Predicate<Distributor> predicate) {
        requireNonNull(predicate);
        filteredDistributors.setPredicate(predicate);
    }

    @Override
    public ObservableList<Product> getFilteredPersonList() {
        return FXCollections.unmodifiableObservableList(filteredProducts);
    }

    @Override
    public void updateFilteredPersonList(Predicate<Product> predicate) {
        requireNonNull(predicate);
        filteredProducts.setPredicate(predicate);

    }

    //=========== Undo/Redo =================================================================================

    @Override
    public boolean canUndoAddressBook() {
        return versionedAddressBook.canUndo();
    }

    @Override
    public boolean canRedoAddressBook() {
        return versionedAddressBook.canRedo();
    }

    @Override
    public void undoAddressBook() {
        versionedAddressBook.undo();
        indicateAddressBookChanged();
    }

    @Override
    public void redoAddressBook() {
        versionedAddressBook.redo();
        indicateAddressBookChanged();
    }

    @Override
    public void commitAddressBook() {
        versionedAddressBook.commit();
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return versionedAddressBook.equals(other.versionedAddressBook)
                && filteredDistributors.equals(other.filteredDistributors)
                && filteredProducts.equals(other.filteredProducts);
    }

    //=========================== SalesHistory accessories ===================================

    private void indicateSalesHistoryChanged() {
        raise(new SalesHistoryChangedEvent(versionedSalesHistory));
    }

    public SalesHistory getSalesHistory() {
        return versionedSalesHistory;
    }

    @Override
    public String getDaysTransactions(String day) throws InvalidTimeFormatException {
        try {
            return versionedSalesHistory.getDaysTransactionsAsString(day);
        } catch (InvalidTimeFormatException e) {
            throw e;
        }
    }

    @Override
    public void addTransaction(Transaction transaction) throws InvalidTimeFormatException,
            DuplicateTransactionException {
        try {
            versionedSalesHistory.addTransaction(transaction);
        } catch (DuplicateTransactionException e) {
            throw e;
        } catch (InvalidTimeFormatException e) {
            throw e;
        }
        indicateSalesHistoryChanged();
    }

    @Override
    public void addReminder(Reminder reminder) throws InvalidTimeFormatException, DuplicateReminderException {
        if (!TimeIdentifiedClass.isValidDateAndTime(reminder.getReminderTime())) {
            throw new InvalidTimeFormatException();
        }
        try {
            versionedSalesHistory.addReminder(reminder);
        } catch (InvalidTimeFormatException e) {
            throw e;
        } catch (DuplicateReminderException e) {
            throw e;
        }
        indicateSalesHistoryChanged();
    }

    @Override
    public void removeReminder(String reminderTime) throws InvalidTimeFormatException, NoSuchElementException {
        try {
            versionedSalesHistory.removeReminder(reminderTime);
        } catch (InvalidTimeFormatException e) {
            throw e;
        } catch (NoSuchElementException e) {
            throw e;
        }
        indicateSalesHistoryChanged();
    }

    @Override
    public ArrayList<Reminder> getOverdueReminders() {
        return versionedSalesHistory.getOverdueReminders();
    }

    @Override
    public ArrayList<Reminder> getOverdueRemindersForThread() {
        return versionedSalesHistory.getOverDueRemindersForThread();
    }

    @Override
    public Transaction getLastTransaction() {
        return versionedSalesHistory.getLastTransaction();
    }
}
