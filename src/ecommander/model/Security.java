package ecommander.model;

import ecommander.fwk.UserNotAllowedException;

import java.util.HashSet;

/**
 * Проверка привилегий пользователя доступ к айтемам и другим пользователям
 *
 * !!!  Этот класс следует использовать ТОЛЬКО в админской части и командах (Command) со сложной логикой
 *
 * В других случаях достаточно использовать проверки, встроенные в соответствующие команды CommandUnit
 *
 * Created by E on 10/5/2017.
 */
public class Security {
	public static void testPrivileges(User admin, ItemBasics object) throws UserNotAllowedException {
		// Если айтем персональный
		if (object.isPersonal()) {
			if (object.getOwnerUserId() != admin.getUserId() && !admin.isAdmin(object.getOwnerGroupId()))
				throw new UserNotAllowedException("Action is not allowed to user " + admin.getName());
			// Если айтем общий (нет владельца)
		} else {
			if (!admin.inGroup(object.getOwnerGroupId()))
				throw new UserNotAllowedException("Action is not allowed to user " + admin.getName());
		}
	}
}
