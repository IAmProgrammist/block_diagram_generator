package rchat.info.blockdiagramgenerator.models.bdelements;

import rchat.info.blockdiagramgenerator.Main;

public class BDAddElementModel extends BDProcessModel {
    public BDAddElementModel() {
        this(false);
    }
    public BDAddElementModel(boolean empty) {
        super(empty ? "" : Main.rb.getString("bd_element_add_content"));
    }
}
