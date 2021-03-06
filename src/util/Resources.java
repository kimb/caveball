package util;

import java.util.*;
import org.apache.log4j.Logger;

/**
 * Toteuttaa tuen eri kielille käntämiseen.
 */
public class Resources {

	private final Logger log = Logger.getLogger(this.getClass());

	/** Kirjasto käännetyistä sanoista */
	protected ResourceBundle resources;
	
	public Resources(ResourceBundle resources) {
		this.resources = resources;
	}

	public String getString(String key) {
		try {
			return resources.getString(key);
		} catch(MissingResourceException e) {
			// log.debug("Resurssia ei ole käännetty: "+key);
		}
		return key;
	}
}
