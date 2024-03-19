package com.microservices.composite.product.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.api.composite.product.ReviewSummary;
import com.microservices.api.core.product.Product;
import com.microservices.api.core.product.ProductService;
import com.microservices.api.core.recommendation.Recommendation;
import com.microservices.api.core.recommendation.RecommendationService;
import com.microservices.api.core.review.Review;
import com.microservices.api.core.review.ReviewService;
import com.microservices.api.exceptions.InvalidInputException;
import com.microservices.api.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String productServiceUrl;
    private final String recommendationServiceUrl;
    private final String reviewServiceUrl;


    @Autowired
    public ProductCompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper mapper,
            @Value("${app.product-service.host}")
            String productServiceHost,
            @Value("${app.product-service.port}")
            int productServicePort,
            @Value("${app.recommendation-service.host}")
            String recommendationServiceHost,
            @Value("${app.recommendation-service.port}")
            int recommendationServicePort,
            @Value("${app.review-service.host}")
            String reviewServiceHost,
            @Value("${app.review-service.port}")
            int reviewServicePort
    ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product/";
        recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation?productId=";
        reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review?productId=";
    }

    @Override
    public Product getProduct(int productId) {
        try {
            String url = productServiceUrl + productId;
            Product product = restTemplate.getForObject(url, Product.class);

            return product;
        } catch (HttpClientErrorException ex) {
            switch (HttpStatus.resolve(ex.getStatusCode().value())) {
                case NOT_FOUND:
                    throw new NotFoundException(getErrorMessage(ex));
                case UNPROCESSABLE_ENTITY:
                    throw new InvalidInputException(getErrorMessage(ex));
                default:
                    
            }
        }
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {
        String url = recommendationServiceUrl + productId;
        List<Recommendation> recommendations = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Recommendation>>(){}
        ).getBody();
        return recommendations;
    }

    @Override
    public List<Review> getReviews(int productId) {
        String url = reviewServiceUrl + productId;
        List<Review> reviews = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Review>>(){}).getBody();

        return reviews;
    }
}
