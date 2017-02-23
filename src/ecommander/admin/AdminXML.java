package ecommander.admin;

public interface AdminXML {
	/**
	 * Элементы XML	
	 */
	String ITEM_ELEMENT = "item";
	String ITEM_TO_ADD_ELEMENT = "item-to-add";
	String EXTENDER_ELEMENT = "extender";
	String CREATE_LINK_ELEMENT = "create-link";
	String EDIT_LINK_ELEMENT = "edit-link";
	String EDIT_INLINE_LINK_ELEMENT = "edit-inline-link";
	String DELETE_LINK_ELEMENT = "delete-link";
	String UPLOAD_LINK_ELEMENT = "upload-link";
	String VIEW_TYPE_ELEMENT = "view-type";
	String BASE_ID_ELEMENT = "base-id";
	String BASE_TYPE_ELEMENT = "base-type";
	String DOMAIN_ELEMENT = "domain";
	String PATH_ELEMENT = "path";
	String FORM_ELEMENT = "form";
	String MOUNT_ELEMENT = "mount";
	String MOUNTED_ELEMENT = "mounted";
	String INPUT_ELEMENT = "input";
	String LINK_ELEMENT = "link";
	String TYPE_ELEMENT = "type";
	String OPEN_ASSOC_LINK_ELEMENT = "open-associated-link";
	String COPY_LINK_ELEMENT = "copy-link";
	String PASTE_LINK_ELEMENT = "paste-link";
	String GET_PASTE_LINK_ELEMENT = "get-paste";
	String MESSAGE_ELEMENT = "message";
	String VISUAL_ELEMENT = "visual";
	/**
	 * Атрибут XML
	 */
	String NAME_ATTRIBUTE = "name";
	String TYPE_NAME_ATTRIBUTE = "type-name";
	String CAPTION_ATTRIBUTE = "caption";
	String VIRTUAL_ATTRIBUTE = "virtual";
	String TYPE_CAPTION_ATTRIBUTE = "type-caption";
	String TYPE_INLINE_ATTRIBUTE = "type-inline";
	String ID_ATTRIBUTE = "id";
	String REF_ID_ATTRIBUTE = "ref-id";
	String TYPE_ID_ATTRIBUTE = "type-id";
	String DEFAULT_ATTRIBUTE = "default";
	String COMPATIBLE_ATTRIBUTE = "compatible";
	String WEIGHT_ATTRIBUTE = "weight";
	String ALT_ATTRIBUTE = "alt";
}
