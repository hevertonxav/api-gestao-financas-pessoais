package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public final class MockMvcHelper {

    private  MockMvcHelper(){}

    public static ResultActions realizarRequisicao(
            MockMvc mockMvc,
            MockHttpServletRequestBuilder requestBuilder,
            Object body,
            ObjectMapper objectMapper) throws  Exception {

        return mockMvc.perform(
                requestBuilder
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andDo(print()
                );
    }

    public static ResultActions realizarRequisicao(
            MockMvc mockMvc,
            MockHttpServletRequestBuilder requestBuilder) throws Exception {

        return  mockMvc.perform(requestBuilder).andDo(print());
    }
}
