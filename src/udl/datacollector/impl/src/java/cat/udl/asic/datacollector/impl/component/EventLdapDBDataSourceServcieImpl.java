/**
 * Copyright (c) 2011 Universitat de Lleida
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors: Alex Ballesté,
 * Contact: David Barroso (david@asic.udl.cat) , Alex Ballesté (alex@asic.udl.cat) and usuaris-cvirtual@llistes.udl.cat
 * Universitat de Lleida  Plaça Víctor Siurana, 1  25005 LLEIDA SPAIN
 *
 **/

package cat.udl.asic.datacollector.impl.component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Timestamp;

public class EventLdapDBDataSourceServcieImpl extends
		EventDBDataSourceServiceImpl {

	private String userName = null;
	private String password = null;
	private String url = null;

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	protected Connection getSimpleConnection() {

		Connection con = null;

		try {
			Class.forName("com.octetstring.jdbcLdap.sql.JdbcLdapDriver");
			con = DriverManager.getConnection(url, userName, password);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;

	}

	protected void returnConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
