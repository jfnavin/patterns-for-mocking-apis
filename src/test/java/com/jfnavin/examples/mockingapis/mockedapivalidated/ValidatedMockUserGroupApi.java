package com.jfnavin.examples.mockingapis.mockedapivalidated;

import com.atlassian.oai.validator.wiremock.junit5.OpenApiValidator;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.jfnavin.examples.mockingapis.client.Group;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.okForJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * A mock of the "User and Groups" API that includes validation against the OpenAPI specification for the API.
 * <p>
 * This mock uses <a href="https://wiremock.org/">wiremock</a> to mock the HTTP responses and
 * <a href="https://bitbucket.org/atlassian/swagger-request-validator/src/master/">swagger-request-validator</a>
 * to validate against the API specification.
 */
public class ValidatedMockUserGroupApi extends WireMockExtension {

    private static final OpenApiValidator API_VALIDATOR = new OpenApiValidator("/api.yml");

    public ValidatedMockUserGroupApi() {
        super(newInstance()
                .options(wireMockConfig().dynamicPort().extensions(API_VALIDATOR))
        );
    }

    @Override
    protected void onAfterEach(final WireMockRuntimeInfo wireMockRuntimeInfo) {
        API_VALIDATOR.assertValidationPassed();
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
