package extra;

import ecommander.fwk.IntegrateBase;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import org.xml.sax.helpers.DefaultHandler;

public class TermobrestProductHandler extends DefaultHandler implements CatalogConst {
    private static final ItemType PRODUCT_DESC = ItemTypeRegistry.getItemType(PRODUCT_ITEM);

    private IntegrateBase.Info info;
    private User initiator;


    public TermobrestProductHandler(IntegrateBase.Info info, User initiator) {
        this.info = info;
        this.initiator = initiator;
    }


}
