package seedu.address.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import seedu.address.commons.core.GuiSettings;

/**
 * Represents User's preferences.
 */
public class UserPrefs {

    private GuiSettings guiSettings;
    private Path addressBookFilePath = Paths.get("data" , "addressbook.xml");
    private Path distributorBookFilePath = Paths.get("data", "distributorbook.xml");
    private Path usersFilePath = Paths.get("data", "users.xml");
    private Path salesHistoryFilePath = Paths.get("data", "saleshistory.xml");

    public UserPrefs() {
        setGuiSettings(750, 500, 0, 0);
    }

    public GuiSettings getGuiSettings() {
        return guiSettings == null ? new GuiSettings() : guiSettings;
    }

    public void updateLastUsedGuiSetting(GuiSettings guiSettings) {
        this.guiSettings = guiSettings;
    }

    public void setGuiSettings(double width, double height, int x, int y) {
        guiSettings = new GuiSettings(width, height, x, y);
    }

    public Path getAddressBookFilePath() {
        return addressBookFilePath;
    }

    public Path getSalesHistoryFilePath() {
        return salesHistoryFilePath;
    }

    public void setAddressBookFilePath(Path addressBookFilePath) {
        this.addressBookFilePath = addressBookFilePath;
    }

    public Path getDistributorBookFilePath() {
        return distributorBookFilePath;
    }

    public void setDistributorBookFilePath(Path addressBookFilePath) {
        this.distributorBookFilePath = distributorBookFilePath;
    }

    public Path getUsersFilePath() {
        return usersFilePath;
    }

    public void setUsersFilePath(Path usersFilePath) {
        this.usersFilePath = usersFilePath;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UserPrefs)) { //this handles null as well.
            return false;
        }

        UserPrefs o = (UserPrefs) other;

        return Objects.equals(guiSettings, o.guiSettings)
                && Objects.equals(addressBookFilePath, o.addressBookFilePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guiSettings, addressBookFilePath);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Gui Settings : " + guiSettings.toString());
        sb.append("\nLocal data file location : " + addressBookFilePath);
        return sb.toString();
    }

}
