package ecommander.fwk;

import com.sun.codemodel.*;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.ParameterDescription;
import ecommander.model.datatypes.DataType.Type;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

/**
 * Генерация СТроковых констант айтемов и классов айтемов
 * @author E
 *
 */
public class CodeGenerator {
	public static void createJavaConstants() {
		try {
			File srcDir = new File(AppContext.getModelPath());
			JCodeModel jCodeModel = new JCodeModel();
			JPackage jp = jCodeModel._package("ecommander.extra._generated");
			buildConstants(jp);
			buildJavaClasses(jp, jCodeModel);
			jCodeModel.build(srcDir);
		} catch (Exception e) {
			ServerLogger.error("Constants class autogeneration failed", e);
		}
	}
	
	private static void buildConstants(JPackage pack) throws JClassAlreadyExistsException {
		String className = AppContext.getItemNamesClassName();
		if (StringUtils.isBlank(className))
			className = "ItemNames";
		JDefinedClass itemsInterface = pack._getClass(className);
		if (itemsInterface != null)
			pack.remove(itemsInterface);
		itemsInterface = pack._interface(className);
		for (String itemName : ItemTypeRegistry.getItemNames()) {
			ItemType item = ItemTypeRegistry.getItemType(itemName);
			if (!item.isUserDefined()) {
				String interfaceName = createBigJavaName(itemName);
				try {
					// Создать константу - имя типа
					JFieldVar itemNameConst = itemsInterface.field(JMod.NONE, String.class, interfaceName.toUpperCase());
					itemNameConst.init(JExpr.lit(item.getName()));
					// Создать интерфейс
					JDefinedClass itemInterface = itemsInterface._interface(interfaceName + "_");
					JFieldVar itemNameField = itemInterface.field(JMod.NONE, String.class, "_ITEM_NAME");
					itemNameField.init(JExpr.lit(item.getName()));
					for (ParameterDescription param : item.getParameterList()) {
						String paramName = createBigJavaName(param.getName());
						JFieldVar paramNameField = itemInterface.field(JMod.NONE, String.class, paramName.toUpperCase());
						paramNameField.init(JExpr.lit(param.getName()));
					}
				} catch (Exception e) {
					// просто пропустить
				}
			}
		}
	}
	
	public static void buildJavaClasses(JPackage pack, JCodeModel codeModel) {
		for (String itemName : ItemTypeRegistry.getItemNames()) {
			ItemType item = ItemTypeRegistry.getItemType(itemName);
			if (!item.isUserDefined()) {
				try {
					JDefinedClass itemsClass = pack._getClass(itemName);
					if (itemsClass != null) 
						pack.remove(itemsClass);
					JDefinedClass itemClass = pack._class(WordUtils.capitalize(itemName));
					itemClass._extends(Item.class);
					// Конструктор
					JMethod constructor = itemClass.constructor(JMod.PRIVATE);
					JVar srcItemVar = constructor.param(Item.class, "item");
					constructor.body().invoke("super").arg(srcItemVar);
					// Константа - название класса
					JVar typeNameVar = itemClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, String.class, "_NAME").init(JExpr.lit(itemName));
					// Константы - названия всех параметров
					for (ParameterDescription param : item.getParameterList()) {
						String paramName = createBigJavaName(param.getName());
						JFieldVar paramNameField = itemClass.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, String.class, paramName.toUpperCase());
						paramNameField.init(JExpr.lit(param.getName()));
					}
					// Статический метод для получения оболочки
					JMethod getInstance = itemClass.method(JMod.PUBLIC | JMod.STATIC, itemClass, "get");
					JVar instSrc = getInstance.param(Item.class, "item");
					JInvocation testCompat = codeModel.ref(ItemTypeRegistry.class).staticInvoke("getItemPredecessorsExt")
							.arg(instSrc.invoke("getTypeName")).invoke("contains").arg(typeNameVar);
					String errorMessage = "Wrapper '" + itemName + "' can not be created around '";
					getInstance
							.body()
							._if(instSrc.eq(JExpr._null()))
							._then()._return(JExpr._null());

					JVar isCompatible = getInstance.body().decl(codeModel.BOOLEAN, "isCompatible", testCompat);
					getInstance
							.body()
							._if(isCompatible.not())
							._then()
							._throw(JExpr._new(codeModel._ref(ClassCastException.class)).arg(
									JExpr.lit(errorMessage).plus(instSrc.invoke("getTypeName").plus(JExpr.lit("' object")))));
					
					getInstance
							.body()
							._return(JExpr._new(itemClass).arg(instSrc));
					// Статический метод создания нового айтема с известным предком
					JMethod newDirectChildInstance = itemClass.method(JMod.PUBLIC | JMod.STATIC, itemClass, "newChild");
					JVar directParent = newDirectChildInstance.param(Item.class, "parent");
					JInvocation getItemType = codeModel.ref(ItemTypeRegistry.class).staticInvoke("getItemType").arg(typeNameVar);
					newDirectChildInstance.body()._return(JExpr.invoke("get").arg(JExpr.invoke("newChildItem").arg(getItemType).arg(directParent)));
					// Все параметры
					for (ParameterDescription param : item.getParameterList()) {
						JInvocation getterInvoke = null;
						JMethod setterDirect = null;
						JMethod setterUI = null;
						Class<?> type = null;
						String methodName = null;
						boolean needUIMethod = true;
						boolean isFile = false;
						if (param.getType() == Type.DATE || param.getType() == Type.LONG) {
							type = Long.class;
							methodName = "getLongValue";
						} else if (param.getType() == Type.BYTE) {
							type = Byte.class;
							methodName = "getByteValue";
						} else if (param.getType() == Type.INTEGER) {
							type = Integer.class;
							methodName = "getIntValue";
						} else if (param.getType() == Type.DOUBLE) {
							type = Double.class;
							methodName = "getDoubleValue";
						} else if (param.getType() == Type.DECIMAL || param.getType() == Type.CURRENCY || param.getType() == Type.CURRENCY_PRECISE) {
							type = BigDecimal.class;
							methodName = "getDecimalValue";
						} else if (param.getType() == Type.STRING || param.getType() == Type.PLAIN_TEXT || param.getType() == Type.SHORT_TEXT
								|| param.getType() == Type.TEXT || param.getType() == Type.TINY_TEXT || param.getType() == Type.XML) {
							type = String.class;
							methodName = "getStringValue";
							needUIMethod = false;
						} else if (param.getType() == Type.PICTURE
								|| param.getType() == Type.FILE) {
							type = File.class;
							methodName = "getFileValue";
							needUIMethod = false;
							isFile = true;
						}
						if (type == null)
							continue;
						if (param.isMultiple()) {
							setterDirect = itemClass.method(JMod.PUBLIC, codeModel.VOID, "add_" + param.getName());
							if (needUIMethod)
								setterUI = itemClass.method(JMod.PUBLIC, codeModel.VOID, "addUI_" + param.getName());

							JType returnType = codeModel.ref(List.class).narrow(type);
							JMethod getter = itemClass.method(JMod.PUBLIC, returnType, "getAll_" + param.getName());
							getterInvoke = JExpr.invoke(methodName + "s").arg(param.getName());
							getter.body()._return(getterInvoke);
							
							// Добавить метод удаления значения
							JMethod remover = itemClass.method(JMod.PUBLIC, codeModel.VOID, "remove_" + param.getName());
							JVar val = remover.param(type, "value");
							remover.body().invoke("removeEqualValue").arg(param.getName()).arg(val);
						} else {
							setterDirect = itemClass.method(JMod.PUBLIC, codeModel.VOID, "set_" + param.getName());
							if (needUIMethod)
								setterUI = itemClass.method(JMod.PUBLIC, codeModel.VOID, "setUI_" + param.getName());
							
							JMethod getter = itemClass.method(JMod.PUBLIC, type, "get_" + param.getName());
							getterInvoke = JExpr.invoke(methodName).arg(param.getName());
							getter.body()._return(getterInvoke);
							
							if (!isFile) {
								JMethod defaultGetter = itemClass.method(JMod.PUBLIC, type, "getDefault_" + param.getName());
								JVar defaultVal = defaultGetter.param(type, "defaultVal");
								defaultGetter.body()._return(JExpr.invoke(methodName).arg(param.getName()).arg(defaultVal));
							}
						}
						if (isFile) {
							getterInvoke.arg(codeModel.ref(AppContext.class).staticInvoke("getCommonFilesDirPath"));
						}
						JVar valDirect = setterDirect.param(type, "value");
						setterDirect.body().invoke("setValue").arg(param.getName()).arg(valDirect);
						if (setterUI != null) {
							JVar valUI = setterUI.param(String.class, "value");
							setterUI.body().invoke("setValueUI").arg(param.getName()).arg(valUI);
							setterUI._throws(Exception.class);
						}
						// Проверка наличия значения параметра
						JMethod checker = itemClass.method(JMod.PUBLIC, codeModel.BOOLEAN, "contains_" + param.getName());
						JVar checkVal = checker.param(type, "value");
						checker.body()._return(JExpr.invoke("containsValue").arg(param.getName()).arg(checkVal));
					}
				} catch (Exception e) {
					ServerLogger.error(e);
				}
			}
		}			
	}
	
	private static String createBigJavaName(String name) {
		return StringUtils.replaceChars(name, " .-", "___");
	}
}
