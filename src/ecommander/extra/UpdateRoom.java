package ecommander.extra;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import ecommander.application.extra.ItemUtils;
import ecommander.extra._generated.ItemNames;
import ecommander.model.item.Item;
import ecommander.pages.elements.Command;
import ecommander.pages.elements.ResultPE;
import ecommander.persistence.commandunits.SaveNewItemDBUnit;
import ecommander.persistence.commandunits.UpdateItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.users.User;

public class UpdateRoom extends Command {

	private static final String ACTION = "action";
	private static final String TOGGLE = "toggle";
	private static final String UPDATE = "update";
	private static final String ROOM = "room";
	
	@Override
	public ResultPE execute() throws Exception {
		String action = getVarSingleValueDefault(ACTION, UPDATE);
		if (StringUtils.equalsIgnoreCase(action, UPDATE)) {
			Item room = getItemForm().createItem(User.getDefaultUser());
			if (room.isNew()) {
				Item booking = ItemUtils.ensureSingleRootItem(ItemNames.BOOKING, User.getDefaultUser(), false);
				Item freeRooms = ItemUtils.ensureSingleItem(ItemNames.FREE_ROOMS, booking.getId(), User.getDefaultUser(), false);
				room.setDirectParentId(freeRooms.getId());
				room.setValue(ItemNames.free_room.SHOW, (byte)1);
			}
			Item roomType = ItemQuery.loadById(room.getLongValue(ItemNames.free_room.TYPE, -1));
			if (roomType != null)
				room.setValue(ItemNames.free_room.TYPE_NAME, roomType.getValue(ItemNames.room.NAME));
			if (StringUtils.isBlank(room.getStringValue(ItemNames.free_room.NUM, "")))
				room.setValue(ItemNames.free_room.NUM, "Не задан");
			if (room.isNew())
				executeAndCommitCommandUnits(new SaveNewItemDBUnit(room));
			else
				executeAndCommitCommandUnits(new UpdateItemDBUnit(room));
			return getResult("success");
		} else if (StringUtils.equalsIgnoreCase(action, TOGGLE)) {
			ArrayList<Item> rooms = ItemQuery.loadByIdsString(getVarValues(ROOM), ItemNames.FREE_ROOM);
			for (Item room : rooms) {
				byte show = room.getByteValue(ItemNames.free_room.SHOW, (byte)0);
				room.setValue(ItemNames.free_room.SHOW, show == (byte)0 ? (byte)1 : (byte)0);
				executeCommandUnit(new UpdateItemDBUnit(room));
			}
			commitCommandUnits();
			return getResult("success");
		}
		return getResult("success");
	}

}
