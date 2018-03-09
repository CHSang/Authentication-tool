package vn.axonactive.authentication.cache;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import vn.axonactive.authentication.domain.ConfigurationEnum;
import vn.axonactive.authentication.domain.utils.ConfigPropertiesUtils;

@ManagedBean
@ApplicationScoped
public class CacheController {
	private static final String VERSION = ConfigPropertiesUtils.getProperty(ConfigurationEnum.CONFIG_PROPERTIES.getValue(), "version");
        
	public String getProjectVersion() {
		return VERSION;
	}
}
