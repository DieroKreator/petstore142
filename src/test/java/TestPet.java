// 0 - nome do pacote

// 1 - bibliotecas
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

// 2 - classe
public class TestPet {
    // 2.1 atributos
    static String ct = "application/json";
    static String uriPet = "https://petstore.swagger.io/v2/pet";
    static int petId = 602740501;

    // 2.2 funções e métodos
    // 2.2 funções e métodos comuns / uteis

    // função de leitura de Json
    public static String lerArquivoCSV(String arquivoJson) throws IOException{
        return new String(Files.readAllBytes(Paths.get(arquivoJson)));
    }


    @Test
    public void testPostPet() throws IOException{
        // carregar os dados do arquivo json do pet
        String jsonBody = lerArquivoCSV("src/test/resources/json/pet1.json");

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

    @Test
    public void testGetPet(){

        String petName = "Snoopy";
        String categoryName = "cachorro";
        String tagName = "vacinado";

        given()
            .contentType(ct)
            .log().all()
        .when()
            .get(uriPet + "/" + petId)
        .then()
            .log().all()
            .statusCode(200)
            .body("name", is(petName))
            .body("id", is(petId))
            .body("category.name", is(categoryName))
            .body("tags[0].name", is(tagName))
        ;
    }
}
