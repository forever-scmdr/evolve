package ecommander.pages;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Форма на замену ItemHttpPostForm
 * Форма может представлять более одного айтема.
 * Айтемы могут быть выстроены в иерархическую стркутуру
 * Айтемы в структуре могут быть как новыми так и существующими (ранее созданными)
 * Айтем может содержать как параметры так и дополнительные поля (ItemVariables)
 *
 * Структура формы при приходе ее из запроса (от браузера) определяется только названиями полей формы, без
 * использования дополнительных служебных hidden полей.
 *
 * Названия полей определяются изначальным созданием формы (например, в файле pages.xml)
 *
 * Названия полей состоят из следующих составляющих:
 *
 * 1) Код формы айтема. Генерируестя простым счетчиком.  Начинается с f (form)
 * 2) Код родительской формы (в которую вложена текущая, т.е. родительский айтем для текущего) - аналогично 1.
 *    Начинается с s (successor)
 * 3) ID айтема i (item). Форма существующего или нового айтема, задан ID айтема. Новые айтемы
 *    также имеют ID, но он отрицательный
 * 4) ID родительского айтема a (ancestor). Нужна только для новых айтемов
 * 5) Тип айтема (начинается с t)
 * 6) Параметр айтема (начинается с p). Возможно при отсутствии части 6. (parameter)
 * 7) Переменная айтема (начинается с v). Возможно при отсутствии части 5. (variable)
 *
 * Название поля состоит из частей, разделенных подчеркиванием (_), например
 * f0003_s0001_n15_t8_p22
 *
 *
 * Created by E on 17/5/2017.
 */
public class MultipleHttpPostForm implements Serializable{

	private final static char FORM = 'f';
	private final static char CONTAINER_FORM = 'c';
	private final static char ITEM = 'i';
	private final static char ANCESTOR = 'a';
	private final static char TYPE = 't';
	private final static char PARAM = 'p';
	private final static char VAR = 'v';

	private static class InputDesc {
		private int formCode;
		private int parentCode;
		private long parentId;
		private long itemId;
		private int itemType;
		private int paramId;
		private String varName;

		public InputDesc(String inputName) {
			String[] parts = StringUtils.split(inputName);
			for (String part : parts) {
				if (part.length() > 0) {
					char desc = part.charAt(0);
					String value = org.apache.commons.lang3.StringUtils.substring(part, 1);
					switch (desc) {
						case FORM:
							formCode = Integer.parseInt(value);
							break;
						case CONTAINER_FORM:
							parentCode = Integer.parseInt(value);
							break;
						case ITEM:
							itemId = Long.parseLong(value);
							break;
						case ANCESTOR:
							parentId = Long.parseLong(value);
							break;
						case TYPE:
							itemType = Integer.parseInt(value);
							break;
						case PARAM:
							paramId = Integer.parseInt(value);
							break;
						case VAR:
							varName = value;
							break;
					}
				}
			}
		}
	}

}
