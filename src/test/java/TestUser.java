import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasLength;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class TestUser {
    static String ct = "application/json";
    static String uriUser = "https://petstore.swagger.io/v2/user";
    static String token;

    public static String lerArquivoCSV(String arquivoJson) throws IOException{
        return new String(Files.readAllBytes(Paths.get(arquivoJson)));
    }
        
    @Test
    public static String testLogin(){
        // Configura
        String username = "charlie";
        String password = "abcdef";

        String resultadoEsperado = "logged in user session:";

        Response resposta = (Response) given()
            .contentType(ct)
            .log().all()
        // Executa
        .when()
            .get(uriUser + "/login?username=" + username + "&password=" + password)
        // Valida
        .then()
            .log().all()
            .statusCode(200)
            .body("code", is(200))
            .body("type", is("unknown"))
            .body("message", containsString(resultadoEsperado)) // Contém
            .body("message", hasLength(36)) // tamanho do campo message
        .extract()
        ;

        // extração
        token = resposta.jsonPath().getString("message").substring(23);
        System.out.println("Conteudo do Token: " + token);
        return token;
    }

    @Test @Order(1)
    public void testPostUser() throws IOException{
        // carregar os dados do arquivo json do pet
        String jsonBody = lerArquivoCSV("src/test/resources/json/user1.json");

        given()
            .contentType(ct)
            .log().all()
            .body(jsonBody)
        .when()
            .post(uriUser)
        .then()
            .log().all()
            .statusCode(200)
            .body("code", is(200))
            .body("type", is("unknown"))
            .body("message", is("740560201"))
        ;
    }

    
}
