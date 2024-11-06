// 0 - nome do pacote

// 1 - bibliotecas
import io.restassured.response.Response;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

// 2 - classe
public class TestPet {
    // 2.1 atributos
    static String ct = "application/json";
    static String uriPet = "https://petstore.swagger.io/v2/pet";

    // 2.2 funções e métodos
    // 2.2 funções e métodos comuns / uteis

    // função de leitura de Json
    public static String lerArquivoCSV(String arquivoJson) throws IOException{
        return new String(Files.readAllBytes(Paths.get(arquivoJson)));
    }


    @Test
    public void testPostPet() throws IOException{
        // carregar os dados do arquivo json do pet
        String jsonBody = lerArquivoCSV("/src/test/resources/json/pet1.json");
        String petId = "602740501";

        given()
            .contentType(ct)
            .log().all()
            .body(jsonBody)
        .when()
            .post(uriPet)
        .then()
            .log().all()
            .statusCode(200)
            .body("name", is("Snoopy"))
            .body("id", is(petId))
            .body("category.name", is("cachorro"))
            .body("tags[0].name", is("vacinado"))
        ;
    }
}
