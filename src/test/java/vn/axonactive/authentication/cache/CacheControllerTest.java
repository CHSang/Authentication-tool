package vn.axonactive.authentication.cache;

import org.junit.Assert;
import org.junit.Test;


public class CacheControllerTest {
    
    @Test
    public void getProjectVersion_Should_ReturnCorrectVersion() {
        CacheController cacheController = new CacheController();
        String version = cacheController.getProjectVersion();
        Assert.assertEquals("${project.version}", version);
    }

}
