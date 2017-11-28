package util;

import java.util.*;
import org.apache.log4j.Logger;

/**
 * Toteuttaa tuen eri kielille k‰nt‰miseen.
 */
public class Resources {

	private final Logger log = Logger.getLogger(this.getClass());

	/** Kirjasto k‰‰nnetyist‰ sanoista */
	protected ResourceBundle resources;
	
	public Resources(ResourceBundle resources) {
		this.resources = resources;
	}

	public String getString(String key) {
		try {
			return resources.getString(key);
		} catch(MissingResourceException e) {
			// log.debug("Resurssia ei ole k‰‰nnetty: "+key);
		}
		return key;
	}
}
