package com.jfnavin.examples.mockingapis.mockedapi;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.jfnavin.examples.mockingapis.client.Group;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * A mock of the "User and Groups" API
 * <p>
 * This mock uses <a href="https://wiremock.org/">wiremock</a> to mock the HTTP responses
 */
public class MockUserGroupApi extends WireMockExtension {

    public MockUserGroupApi() {
        super(newInstance().options(wireMockConfig().dynamicPort()));
    }

    public class GroupResource {

        public class GetGroupOperation {
            /**
             * Set up the mock API to return a success response to the operation
             */
            public void returnsSuccessWith(final Group response) {
                stubFor(get("/group/" + response.id())
                        .willReturn(okForJson(response)));
            }

            /**
             * Set up the mock API to return 404 (not found)
             */
            public void returnsNotFound(final long groupId) {
                stubFor(get("/group/" + groupId)
                        .willReturn(notFound()));
            }
        }

        public GetGroupOperation getGroup() {
            return new GetGroupOperation();
        }
    }

    public GroupResource groupResource() {
        return new GroupResource();
    }

}
