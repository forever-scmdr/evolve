package ecommander.model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Описание для вычисляемого параметра.
 * Хранит вид базовых параметров (родительские или дочерние), функцию
 * и список базовых параметров. Каждый базовый параметр может быть представлен списком значений или одним
 * значением. Базовые параметры - это параметры айтемов, значения которых определяют значение вычисляемого
 * параметра, т. е. для вычисления вычисляемого параметра исползуются значения базовых параметров
 *
 * Created by E on 22/2/2017.
 */
public class ComputedDescription {
	/**
	 * Базовые айтемы для вычисляемого патаметра
	 * Родительские или дочерние
	 */
	public enum Type {
		parent, child;
		public static Type get(String typeStr) {
			if (StringUtils.equalsIgnoreCase(typeStr, "base-child"))
				return child;
			if (StringUtils.equalsIgnoreCase(typeStr, "base-parent"))
				return parent;
			return null;
		}
	}

	/**
	 * Агрегатная функция для вычисляемого параметра
	 * Применяется если получено много значений (в основном так и бывает, в этом и суть)
	 * базовых параметров
	 */
	public enum Func {
		COUNT {
			@Override
			public String toString() {
				return "COUNT";
			}
		},
		MAX {
			@Override
			public String toString() {
				return "MAX";
			}
		},
		MIN {
			@Override
			public String toString() {
				return "MIN";
			}
		},
		SUM {
			@Override
			public String toString() {
				return "SUM";
			}
		},
		AVG {
			@Override
			public String toString() {
				return "AVG";
			}
		},
		FIRST {
			@Override
			public String toString() {
				return "FIRST";
			}
		},
		JOIN {
			@Override
			public String toString() {
				return "JOIN";
			}
		};
		public static Func get(String funcStr) {
			if (StringUtils.equalsIgnoreCase(funcStr, "count"))
				return COUNT;
			if (StringUtils.equalsIgnoreCase(funcStr, "max"))
				return MAX;
			if (StringUtils.equalsIgnoreCase(funcStr, "min"))
				return MIN;
			if (StringUtils.equalsIgnoreCase(funcStr, "sum"))
				return SUM;
			if (StringUtils.equalsIgnoreCase(funcStr, "avg"))
				return AVG;
			if (StringUtils.equalsIgnoreCase(funcStr, "first"))
				return FIRST;
			if (StringUtils.equalsIgnoreCase(funcStr, "join"))
				return JOIN;
			return null;
		}
	}

	/**
	 * Описание базового параметра
	 */
	public static class Ref {
		public final Type type;
		public final String item;
		public final String param;
		public final String assoc;

		Ref(Type type, String item, String param, String assoc) {
			this.type = type;
			this.item = item;
			this.param = param;
			if (StringUtils.isBlank(assoc))
				this.assoc = AssocRegistry.PRIMARY_NAME;
			else
				this.assoc = assoc;
		}
	}
	// Функция агрегации
	private final Func func;
	private ArrayList<Ref> basicParams = new ArrayList<>();
	//private Ref basicParam;

	ComputedDescription(Func func) {
		this.func = func;
	}

	/**
	 * Добавить базовый параметр
	 * @param type
	 * @param itemName
	 * @param paramName
	 * @param assoc
	 */
	void addBasic(Type type, String itemName, String paramName, String assoc) {
		basicParams.add(new Ref(type, itemName, paramName, assoc));
	}

	/**
	 * Получить базовые параметры
	 * @return
	 */
	public ArrayList<Ref> getBasicParams() {
		return basicParams;
	}

	/**
	 * Получить функцию агрегации
	 * @return
	 */
	public Func getFunc() {
		return func;
	}
}
