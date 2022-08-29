package ecommander.model;

import ecommander.controllers.AppContext;

import java.io.File;

/**
 *
 <authority>
	 <groups>
		 <group name="common"/>
		 <group name="registered"/>
		 <group name="editor"/>
	 </groups>
	 <users>
		 <user name="admin" password="eeee" description="Мега админ">
			 <group name="common" role="admin"/>
			 <group name="registered" role="admin"/>
			 <group name="editor" role="admin"/>
		 </user>
		 <user name="test" password="test" description="test">
			 <group name="registered"/>
		 </user>
		 <user name="super_editor" password="qwerty" description="Главный редактор">
			 <group name="editor" role="admin"/>
			 <group name="common" role="simple"/>
		 </user>
		 <user name="common_editor" password="1234" description="Обычный редактор новостей и текстов">
		    <group name="editor" role="simple"/>
		 </user>
	 </users>
 </authority>
 * 
 * @author EEEE
 *
 */
public class UserModelBuilder {

	public static final long MODIFIED_TEST_INTERVAL = 30000; // время, через которое проводится проверка обновления users.xsl
	private static long fileLastChecked = Long.MIN_VALUE;
	private static long fileLastModified = Long.MIN_VALUE;
	
	private static final Object SEMAPHORE = new Object();
	
	/**
	 * Проверяет актуальность файла и выполняет еро разбор при необходимости
	 * @throws Exception
	 */
	public static void testActuality() throws Exception {
		if (Math.abs(System.currentTimeMillis() - fileLastChecked) > MODIFIED_TEST_INTERVAL) {
			synchronized (SEMAPHORE) {
				if (Math.abs(System.currentTimeMillis() - fileLastChecked) > MODIFIED_TEST_INTERVAL) {
					File usersFile = new File(AppContext.getUsersPath());
					if (!usersFile.exists()) {
							fileLastChecked = Long.MAX_VALUE;
							return;
					}
					if (usersFile.lastModified() > fileLastModified) {
						UserModelCreateCommandUnit.createUsers();
						fileLastModified = usersFile.lastModified();
					}
					fileLastChecked = System.currentTimeMillis();
				}
			}
		}
	}

}
