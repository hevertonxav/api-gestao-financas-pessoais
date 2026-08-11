package utils;

import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public final class ControllerTestAssertions {

    private ControllerTestAssertions(){}

    public  static ResultActions validarErro(
            ResultActions resultado,
            int codigoStatus,
            String title,
            String detail,
            String type,
            String instance
    ) throws Exception {

        return resultado
                .andExpect(status().is(codigoStatus))
                .andExpect(jsonPath("$.status").value(codigoStatus))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.detail").value(detail))
                .andExpect(jsonPath("$.type").value(type))
                .andExpect(jsonPath("$.instance").value(instance));
    }

    public static ResultActions validarErroDadosInvalidos(
            ResultActions resultado,
            String instance,
            Map<String, List<String>> errosEsperados) throws Exception {

        ResultActions actions = resultado
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Dados inválidos"))
                .andExpect(jsonPath("$.detail")
                        .value("Um ou mais campos são inválidos."))
                .andExpect(jsonPath("$.type")
                        .value("https://api.financas.com.br/erros/dados-invalidos"))
                .andExpect(jsonPath("$.instance").value(instance))
                .andExpect(jsonPath("$.erros",
                        hasSize(errosEsperados.values()
                                .stream()
                                .mapToInt(List::size)
                                .sum())));

        errosEsperados.forEach((campo, mensagens) -> {

            mensagens.forEach(mensagem -> {
                try {
                    actions.andExpect(jsonPath("$.erros[?(@.campo=='" + campo + "' && @.mensagem=='" + mensagem + "')]")
                            .exists());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        });

        return actions;
    }
}
