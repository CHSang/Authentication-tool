package vn.axonactive.authentication.base;


import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;

public class ArchiveBuilder {
    public static WebArchive create() {
        File[] files = Maven.resolver().loadPomFromFile("pom.xml")
                .importRuntimeDependencies()
                .resolve()
                .withTransitivity()
                .asFile();

        return ShrinkWrap.create(WebArchive.class, "authentication.war")
                .addPackages(true, "vn.axonactive.authentication")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource("configuration.properties")
                .addAsResource("messages.properties")
                .addAsResource("ldap.properties")
                .addAsResource("test.properties")
                .addAsResource("log4j.properties")
                .addAsLibraries(files);
    }
}
