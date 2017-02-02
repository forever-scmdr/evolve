package ecommander.persistence.common;

import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * Одно значение, подготовленное для установки в PreparedStatement
 * @author EEEE
 *
 */
interface PreparedValue {
	void set(PreparedStatement pstmt, int index) throws SQLException;
	String simpleSql();
	PreparedValue createClone();
}