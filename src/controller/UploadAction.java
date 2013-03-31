package controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import model.Model;
import model.PhotoDAO;
import model.UserDAO;

import org.genericdao.RollbackException;
import org.mybeans.form.FileProperty;
import org.mybeans.form.FormBeanException;
import org.mybeans.form.FormBeanFactory;

import databeans.Photo;
import databeans.User;
import formbeans.UploadPhotoForm;


/**
 * Course: 15-637
 * Assignment: HW4
 * @author Yin Xu
 * Last modified: 03/01/2013
 */
public class UploadAction extends Action {
	private FormBeanFactory<UploadPhotoForm> formBeanFactory = FormBeanFactory.getInstance(UploadPhotoForm.class);

	private PhotoDAO photoDAO;
	private UserDAO  userDAO;
	
	public UploadAction(Model model) {
    	photoDAO = model.getPhotoDAO();
    	userDAO  = model.getUserDAO();
	}

	public String getName() { return "upload.do"; }

    public String perform(HttpServletRequest request) {
        // Set up the errors list
        List<String> errors = new ArrayList<String>();
        request.setAttribute("errors",errors);
        
		try {
            // Set up user list for nav bar
			request.setAttribute("userList",userDAO.getUsers());

			User user = (User) request.getSession(false).getAttribute("user");
        	Photo[] photoList = photoDAO.getPhotos(user.getUserName());
	        request.setAttribute("photoList",photoList);

			UploadPhotoForm form = formBeanFactory.create(request);
	        errors.addAll(form.getValidationErrors());
	        if (errors.size() > 0) return "error.jsp";

	        FileProperty fileProp = form.getFile();
			Photo photo = new Photo();  // id & position will be set when created
			photo.setBytes(fileProp.getBytes());
			if (form.getCaption().length() > 0) {
				photo.setCaption(fixBadChars(form.getCaption()));
			} else {
				photo.setCaption(fixBadChars(fileProp.getFileName()));
			}
			photo.setContentType(fileProp.getContentType());
			photo.setOwner(user.getUserName());
			photo.setDescription(form.getDescription());
			photo.setPrice(form.getPrice());
			photo.setDate(new Date());
			photoDAO.create(photo);

			// Update photoList (there's now one more on the list)
        	Photo[] newPhotoList = photoDAO.getPhotos(user.getUserName());
	        request.setAttribute("photoList",newPhotoList);
	        return "manage.jsp";
	 	} catch (RollbackException e) {
			errors.add(e.getMessage());
			return "manage.jsp";
	 	} catch (FormBeanException e) {
			errors.add(e.getMessage());
			return "manage.jsp";
		}
    }
    
    private String fixBadChars(String s) {
		if (s == null || s.length() == 0) return s;
		
		Pattern p = Pattern.compile("[<>\"&]");
        Matcher m = p.matcher(s);
        StringBuffer b = null;
        while (m.find()) {
            if (b == null) b = new StringBuffer();
            switch (s.charAt(m.start())) {
                case '<':  m.appendReplacement(b,"&lt;");
                           break;
                case '>':  m.appendReplacement(b,"&gt;");
                           break;
                case '&':  m.appendReplacement(b,"&amp;");
                		   break;
                case '"':  m.appendReplacement(b,"&quot;");
                           break;
                default:   m.appendReplacement(b,"&#"+((int)s.charAt(m.start()))+';');
            }
        }
        
        if (b == null) return s;
        m.appendTail(b);
        return b.toString();
    }
}
