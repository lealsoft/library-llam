package br.tec.llam.biblioteca.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llam.api")
public class LlamApiProperties {

    private ApiConfig classApi = new ApiConfig();
    private ApiConfig dublincore = new ApiConfig();
    private ApiConfig person = new ApiConfig();
    private ApiConfig organizationunit = new ApiConfig();
    private ApiConfig transaction = new ApiConfig();
    private TimeoutConfig timeout = new TimeoutConfig();

    @Data
    public static class ApiConfig {
        private String url;
    }

    @Data
    public static class TimeoutConfig {
        private int connect = 5000;
        private int read = 30000;
    }

    public String getClassUrl() {
        return classApi.getUrl();
    }

    public String getDublincoreUrl() {
        return dublincore.getUrl();
    }

    public String getPersonUrl() {
        return person.getUrl();
    }

    public String getOrganizationunitUrl() {
        return organizationunit.getUrl();
    }

    public String getTransactionUrl() {
        return transaction.getUrl();
    }
}
