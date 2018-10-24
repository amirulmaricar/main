package seedu.address.testutil;

import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

<<<<<<< HEAD
import seedu.address.model.DistributorBook;
=======
import seedu.address.model.ProductDatabase;
>>>>>>> upstream/master
import seedu.address.model.distributor.Distributor;

/**
 * A utility class containing a list of {@code Product} objects to be used in tests.
 */
public class TypicalDistributors {

    public static final Distributor AHA = new DistributorBuilder().withName("Ah Bee").withPhone("94351253").build();
    public static final Distributor AHB = new DistributorBuilder().withName("Ah Cee").withPhone("94351258").build();
    public static final Distributor AHC = new DistributorBuilder().withName("Ah Dee").withPhone("95352563").build();
    public static final Distributor AHD = new DistributorBuilder().withName("Ah Eee").withPhone("87652533").build();
    public static final Distributor AHE = new DistributorBuilder().withName("Elle Meyer").withPhone("9482224").build();
    public static final Distributor AHF = new DistributorBuilder().withName("Fiona Kunz").withPhone("9482427").build();
    public static final Distributor AHG = new DistributorBuilder().withName("George Best").withPhone("9482442").build();

    // Manually added
    public static final Distributor AHH = new DistributorBuilder().withName("Hoon Meier").withPhone("8482424").build();
    public static final Distributor AHI = new DistributorBuilder().withName("Ida Mueller").withPhone("8482131").build();

    // Manually added - Distributor's details found in {@code CommandTestUtil}
    public static final Distributor AHJ = new DistributorBuilder().withName(VALID_NAME_AMY)
            .withPhone(VALID_PHONE_AMY).build();
    public static final Distributor AHK = new DistributorBuilder().withName(VALID_NAME_BOB)
            .withPhone(VALID_PHONE_BOB).build();

    public static final String KEYWORD_MATCHING_EEE = "Eee"; // A keyword that matches MEIER

    private TypicalDistributors() {} // prevents instantiation

    /**
<<<<<<< HEAD
     * Returns an {@code DistributorBook} with all the typical distributors.
     */
    public static DistributorBook getTypicalDistributorBook() {
        DistributorBook db = new DistributorBook();
=======
     * Returns an {@code ProductDatabase} with all the typical distributors.
     */
    public static ProductDatabase getTypicalAddressBook() {
        ProductDatabase ab = new ProductDatabase();
>>>>>>> upstream/master
        for (Distributor distributor : getTypicalDistributors()) {
            db.addDistributor(distributor);
        }
        return db;
    }

    public static List<Distributor> getTypicalDistributors() {
        return new ArrayList<>(Arrays.asList(AHA, AHB, AHC, AHD, AHE, AHF, AHG));
    }
}
