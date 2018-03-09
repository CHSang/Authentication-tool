package vn.axonactive.authentication;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class IpStorageTest {
    
    @Test
    public void addCallBack() {
        List<String> ipUrls = new ArrayList<>();
        ipUrls.add("http://localhost:8080/grtool/ip=192.168.73.166");
        IpStorage.getIpApiCallBacks().put("192.168.73.166", ipUrls);
        
        IpStorage.addCallBack("192.168.73.166", "http://localhost:8080/grtool/ip=192.168.73.166");
        
    }

}
