package com.jarubert.api.controller;

import com.jarubert.api.ApiApplication;
import com.jarubert.api.model.dto.ApiErrorDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

/**
 * Created by jarubert on 2020-03-26.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes= ApiApplication.class, webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractControllerIntegrationTest {

    @Autowired
    protected TestRestTemplate restTemplate;

    @LocalServerPort
    int randomServerPort;

    protected String baseUrl;

    @Before
    public void setUp() {
        baseUrl = "http://localhost:" + randomServerPort + "/";
    }

    protected void assertErrorHttpStatus(ResponseEntity<ApiErrorDto> errorResponse, HttpStatus status) {
        Assert.assertEquals(status, errorResponse.getStatusCode());
        Assert.assertEquals(status.value(), errorResponse.getBody().getStatus());
        Assert.assertEquals(status.getReasonPhrase(), errorResponse.getBody().getError());
    }

    protected void assertNullField(ResponseEntity<ApiErrorDto> errorResponse, String field) {
        Assert.assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getBody().getStatus());
        Assert.assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorResponse.getBody().getError());
        Assert.assertEquals(Collections.singletonList("The field '"+field+"' can not be null"), errorResponse.getBody().getMessages());
    }
}
