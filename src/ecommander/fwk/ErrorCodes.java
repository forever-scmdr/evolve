package ecommander.fwk;

/**
 * Коды ошибок
 * Created by EEEE on 21.03.2017.
 */
public interface ErrorCodes {
    int NO_SPECIAL_ERROR = 0; // Когда нет смысла присваивать код ошибке
    int ASSOC_NODES_ILLEGAL = 1; // Нельзя создавать ассоциации в иерархии, в которой эти два айтема уже ассоциированы
    int ASSOC_USERS_NO_MATCH = 2; // При создании ассоциации выяснилось, что у ассоциируемого айтема и родителя не совпадают пользователь или группа
    int UNABLE_TO_SAVE_ITEM_INDEX = 3; // Ошбика при записи в индекс параметров айтема
    int USER_NOT_ALLOWED = 4; // Действие недоступно для пользователя
	int VALIDATION_FAILED = 5; // Ошбика предварительной валидации. Нужно читать сообщение об ошибке валидвации
	int PAGE_NOT_FOUND = 6; // Страница не найдена
	int USER_ALREADY_EXISTS = 7; // Пользователь с таким логином уже зарегистрирован
	int FILTER_FORMAT_ERROR = 8; // Ошибка формата пользовательского фильтра
	int FILE_ERROR = 9; // Ошибка при работе с файлами
	int INCOMPATIBLE_ITEM_TYPES = 10; // Несовместимые типы айтемов для операции
}
