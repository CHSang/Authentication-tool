package vn.axonactive.authentication.domain.utils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.axonactive.authentication.domain.validation.Assert;

public final class FacesContextUtils {

	private static final Logger logger = LoggerFactory.getLogger(FacesContextUtils.class);
	
	private FacesContextUtils() {

	}

	public static void redirect(String toUrl) {
		Assert.assertNotEmpty(toUrl, "Redirect parameter must not be empty!!!");
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(toUrl);
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	public static void addErrorMessage(String clientId, String message) {
		FacesContext.getCurrentInstance().addMessage(clientId,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, message, ""));
	}

	public static void addWarningMessage(String clientId, String message) {
		FacesContext.getCurrentInstance().addMessage(clientId,
				new FacesMessage(FacesMessage.SEVERITY_WARN, message, ""));
	}

	public static void addInfoMessage(String clientId, String message) {
		FacesContext.getCurrentInstance().addMessage(clientId,
				new FacesMessage(FacesMessage.SEVERITY_INFO, message, ""));
	}

	public static void setKeepMessage(boolean isKeep) {
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(isKeep);
	}

	public static void putSessionMap(String key, Object value) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(key, value);
	}
	
	public static String getMessage(String key) {
	    FacesContext context = FacesContext.getCurrentInstance();
	    ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
	    return bundle.getString(key);
	}
	
	public static String getMessage(String key, Object[] params) {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
        return MessageFormat.format(bundle.getString(key), params);
    }
	
	public static Flash getFlash() {
		return FacesContext.getCurrentInstance().getExternalContext().getFlash();
	}

}