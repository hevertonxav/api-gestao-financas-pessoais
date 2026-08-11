package utils;

import org.hamcrest.collection.IsCollectionWithSize;
import org.springframework.test.web.servlet.ResultActions;
import java.util.Map;

import static com.jayway.jsonpath.internal.filter.RelationalOperator.EXISTS;
import static javax.swing.text.SimpleAttributeSet.EMPTY;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class ControllerTestHelper {

    public static final Object EXISTS = new Object();
    public static final Object EMPTY = new Object();

    private ControllerTestHelper (){}

    public static ResultActions validarResposta(
            int codigoStatus,
            ResultActions response,
            Map<String, Object> camposEsperados
    ) throws  Exception {

        response.andExpect(status().is(codigoStatus));

       for (Map.Entry<String, Object> campo : camposEsperados.entrySet()) {

           if (campo.getValue() == EXISTS) {
               response.andExpect(jsonPath("$." + campo.getKey()).exists());

           } else if (campo.getValue() == EMPTY) {
               response.andExpect(jsonPath("$." + campo.getKey()).isEmpty());

           } else {
               response.andExpect(
                       jsonPath("$." + campo.getKey())
                               .value(campo.getValue())
               );
           }
       }
       return response;
    }

    public static ResultActions validarRespostaPaginada(
            ResultActions response,
            int indice,
            Map<String, Object> camposEsperados
    ) throws Exception {

        for (Map.Entry<String, Object> campo : camposEsperados.entrySet()) {

            String jsonPath = "$.content[" + indice + "]." + campo.getKey();
            Object valor = campo.getValue();

            if (valor == EXISTS) {
                response.andExpect(jsonPath(jsonPath).exists());
            } else if (valor == null) {
                response.andExpect(jsonPath(jsonPath).isEmpty());
            } else if (valor == EMPTY) {
                response.andExpect(jsonPath(jsonPath).isEmpty());
            } else {
                response.andExpect(jsonPath(jsonPath).value(valor));
            }
        }

        return response;
    }
}
