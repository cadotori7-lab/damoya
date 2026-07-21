package com.soldesk.config;

import org.apache.http.HttpHost;
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

    @Value("${elasticsearch.scheme}")
    private String scheme;

    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.port}")
    private int port;

    // 클라이언트 생성
    @Bean(destroyMethod = "close")
    public RestClient restClient() {
        log.info("Elasticsearch 클라이언트 생성: {}://{}:{}", scheme, host, port);

        var builder = RestClient.builder(new HttpHost(host, port, scheme));
        builder.setRequestConfigCallback(config -> config
            .setConnectTimeout(3_000)
            .setSocketTimeout(5_000));

        return builder.build();
    }
    //엘라스틱서치 클라이언트 생성
    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient) {
        ElasticsearchTransport transport = new RestClientTransport(
            restClient,
            new JacksonJsonpMapper()
        );
        return new ElasticsearchClient(transport);
    }
}
