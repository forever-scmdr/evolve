package ecommander.model;
/**
 * Способ сравнения с образцом.
 * Образец - одно или несколько значений некоторого параметра айтема.
 * Результат сравнения - множество айтемов, которые соответствуют образцу (имеют соответствующий образцу параметр).
 * 
 * ANY		параметр должен соответствовать одному из переданного множества образцов (любому из списка).
 * 			Если список образцов пустой, каждый айтем считается соответствующим
 * 
 * SOME		параметр должен соответствовать одному из переданного множества образцов (любому из списка).
 * 			Если список образцов пустой, то ни один айтем не считается соответствующим (результат сравнения - пустое множество)
 * 
 * ALL		параметр должен соответствовать каждому образцу из переданного множества образцов (всем образцам).
 * 			Если список образцов пустой, каждый айтем считается соответствующим
 * 
 * EVERY	параметр должен соответствовать каждому образцу из переданного множества образцов (всем образцам).
 * 			Если список образцов пустой, то ни один айтем не считается соответствующим (результат сравнения - пустое множество)
 * 
 * @author E
 *
 */
public enum Compare {
	ANY, ALL, SOME, EVERY
}
