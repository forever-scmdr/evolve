package ecommander.persistence.common;

import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * Часть запроса
 * @author EEEE
 *
 */
interface QueryPart  {
	void appendForPrepared(StringBuilder sql);
	void appendSimple(StringBuilder sql);
	int setPrepared(PreparedStatement pstmt, int startIndex) throws SQLException;
	QueryPart createClone();
}
