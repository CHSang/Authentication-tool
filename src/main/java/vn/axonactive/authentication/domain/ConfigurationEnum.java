package vn.axonactive.authentication.domain;

public enum ConfigurationEnum {

    CONFIG_PROPERTIES("configuration.properties"),
    LDAP_PROPERTIES("ldap.properties"),
    MESSAGES_PROPERTIES("messages.properties");

    private String value;

    ConfigurationEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
