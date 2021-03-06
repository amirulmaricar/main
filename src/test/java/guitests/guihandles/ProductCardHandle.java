package guitests.guihandles;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMultiset;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.product.Product;

//@@author Gara
/**
 * Provides a handle to a product card in the product list panel.
 */
public class ProductCardHandle extends NodeHandle<Node> {
    private static final String ID_FIELD_ID = "#id";
    private static final String NAME_FIELD_ID = "#name";
    private static final String ADDRESS_FIELD_ID = "#address";
    private static final String SERIAL_NUMBER_FIELD_ID = "#phone";
    private static final String DIST_FIELD_ID = "#distname";
    private static final String REMAINING_ITEMS_ID = "#remainingItems";
    private static final String TAGS_FIELD_ID = "#tags";

    private final Label idLabel;
    private final Label nameLabel;
    private final Label addressLabel;
    private final Label serialNumberLabel;
    private final Label emailLabel;
    private final Label remainingItemsLabel;
    private final List<Label> tagLabels;

    public ProductCardHandle(Node cardNode) {
        super(cardNode);

        idLabel = getChildNode(ID_FIELD_ID);
        nameLabel = getChildNode(NAME_FIELD_ID);
        addressLabel = getChildNode(ADDRESS_FIELD_ID);
        serialNumberLabel = getChildNode(SERIAL_NUMBER_FIELD_ID);
        emailLabel = getChildNode(DIST_FIELD_ID);
        remainingItemsLabel = getChildNode(REMAINING_ITEMS_ID);
        Region tagsContainer = getChildNode(TAGS_FIELD_ID);
        tagLabels = tagsContainer
                .getChildrenUnmodifiable()
                .stream()
                .map(Label.class::cast)
                .collect(Collectors.toList());
    }

    public String getId() {
        return idLabel.getText();
    }

    public String getName() {
        return nameLabel.getText();
    }

    public String getProductInfo() {
        return addressLabel.getText();
    }

    public String getSerialNumber() {
        return serialNumberLabel.getText();
    }

    public String getDistributor() {
        return emailLabel.getText();
    }

    public String getRemainingItems() {
        return remainingItemsLabel.getText();
    }

    public List<String> getTags() {
        return tagLabels
                .stream()
                .map(Label::getText)
                .collect(Collectors.toList());
    }

    /**
     * Returns true if this handle contains {@code product}.
     */
    public boolean equals(Product product) {
        return getName().equals(product.getName().fullName)
                && getProductInfo().equals(product.getProductInfo().value)
                && getSerialNumber().equals(product.getSerialNumber().value)
                && getDistributor().equals(product.getDistributor().fullDistName)
                && getRemainingItems().equals(product.getRemainingItems().value)
                && ImmutableMultiset.copyOf(getTags()).equals(ImmutableMultiset.copyOf(product.getTags().stream()
                        .map(tag -> tag.tagName)
                        .collect(Collectors.toList())));
    }
}
