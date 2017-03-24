package ecommander.fwk;

/**
 * Created by EEEE on 21.03.2017.
 */
public interface ErrorCodes {
    int ASSOC_NODES_ILLEGAL = 1; // Нельзя создавать ассоциации в иерархии, в которой эти два айтема уже ассоциированы
    int ASSOC_USERS_NO_MATCH = 2; // При создании ассоциации выяснилось, что у ассоциируемого айтема и родителя не совпадают пользователь или группа
    int UNABLE_TO_SAVE_ITEM_INDEX = 3; // Ошбика при записи в индекс параметров айтема
}
