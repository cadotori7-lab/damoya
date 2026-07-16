package com.soldesk.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;

@Configuration
public class ElasticSearchConfig {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchConfig.class);

    @Value("${elasticsearch.scheme:${ELASTICSEARCH_SCHEME:http}}")
    private String scheme;

    @Value("${elasticsearch.host:${ELASTICSEARCH_HOST:localhost}}")
    private String host;

    @Value("${elasticsearch.port:${ELASTICSEARCH_PORT:9200}}")
    private int port;

    @Value("${elasticsearch.username:${ELASTICSEARCH_USERNAME:}}")
    private String username;

    @Value("${elasticsearch.password:${ELASTICSEARCH_PASSWORD:}}")
    private String password;

    @Bean(destroyMethod = "close")
    public RestClient restClient() {
        log.info("Elasticsearch 클라이언트 생성: {}://{}:{}", scheme, host, port);

        var builder = RestClient.builder(new HttpHost(host, port, scheme));
        builder.setRequestConfigCallback(config -> config
            .setConnectTimeout(3_000)
            .setSocketTimeout(5_000));

        if (username != null && !username.isBlank()) {
            BasicCredentialsProvider credentials = new BasicCredentialsProvider();
            credentials.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(username, password)
            );
            builder.setHttpClientConfigCallback(httpClient ->
                httpClient.setDefaultCredentialsProvider(credentials));
        }

        return builder.build();
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient) {
        ElasticsearchTransport transport = new RestClientTransport(
            restClient,
            new JacksonJsonpMapper()
        );
        return new ElasticsearchClient(transport);
    }
}
