package model;
/**
 * Course: 15-637
 * Assignment: HW4
 * @author Yin Xu
 * Last modified: 03/01/2013
 */
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.genericdao.ConnectionPool;
import org.genericdao.DAOException;

public class Model {
	private PhotoDAO photoDAO;
	private UserDAO  userDAO;

	public Model(ServletConfig config) throws ServletException {
		try {
			String jdbcDriver = config.getInitParameter("jdbcDriverName");
			String jdbcURL    = config.getInitParameter("jdbcURL");
			
			ConnectionPool pool = new ConnectionPool(jdbcDriver, jdbcURL);
			userDAO  = new UserDAO("yinxu_user", pool);
			photoDAO = new PhotoDAO("yinxu_item", pool);
		} catch (DAOException e) {
			throw new ServletException(e);
		}
	}
	
	public PhotoDAO getPhotoDAO() { return photoDAO; }
	public UserDAO  getUserDAO()  { return userDAO;  }
}
