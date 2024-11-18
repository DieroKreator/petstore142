// 0 - nome do pacote

// 1 - bibliotecas
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.google.gson.Gson;

// 2 - classe
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestPet {
    // 2.1 atributos
    static String ct = "application/json"; // content-type
    static String uriPet = "https://petstore.swagger.io/v2/pet";
    static int petId = 602740501;
    String petName = "Snoopy";
    String categoryName = "cachorro";
    String tagName = "vacinado";
    String[] status = {"available","sold"};

    // 2.2 funções e métodos
    // 2.2 funções e métodos comuns / uteis

    // função de leitura de Json
    public static String lerArquivoCSV(String arquivoJson) throws IOException{
        return new String(Files.readAllBytes(Paths.get(arquivoJson)));
    }


    @Test @Order(1)
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

    @Test @Order(2)
    public void testGetPet(){

        given()
            .contentType(ct)
            .log().all()
            .header("api_key: ", TestUser.testLogin())
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

    @Test @Order(3)
    public void testPutPet() throws IOException{
        // Configura
        String jsonBody = lerArquivoCSV("src/test/resources/json/pet2.json");

        given()
            .contentType(ct)
            .log().all()
            .body(jsonBody)
        // Executa
        .when()
            .put(uriPet)
        // Valida
        .then()
            .log().all()
            .statusCode(200)
            .body("name", is(petName))    // verifica se o nome é Snoopy
            .body("id", is(petId))         // verifique o código do pet
            .body("category.name", is(categoryName)) // se é cachorro
            .body("tags[0].name", is(tagName))  // se está vacinado
            .body("status", is(status[1])) // status do pet na loja
        ;
    }

    @Test @Order(4)
    public void testDeletePet(){
        // Configura --> Dados de entrada e saída no começo da Classe

        given()
            .contentType(ct)
            .log().all()
        // Executa
        .when()
            .delete(uriPet + "/" + petId)
        // Valida
        .then()
            .log().all()
            .statusCode(200) // se comunicou e processou
            .body("code", is(200))   // se apagou
            .body("type", is("unknown"))
            .body("message", is(String.valueOf(petId)))
        ;
    }

    // Data Driven Testing (DDT) / Teste Direcionado por Dados / Teste com Massa
    // Teste com Json parametrizado
    @ParameterizedTest @Order(5)
    @CsvFileSource(resources = "/csv/petMassa.csv", numLinesToSkip = 1, delimiter = ',')
    public void testPostPetDDT(
        int petId,
        String petName,
        int catId,
        String catName,
        String status1,
        String status2
    ) 
    {
        Pet pet = new Pet();

        pet.id = petId;
        pet.category.id = catId;
        pet.category.name = catName;
        pet.name = petName;
        // pet.photoUrls esta vazio
        pet.tags[0].id = 9;
        pet.tags[0].name = "vacinado";
        pet.tags[1].id = 8;
        pet.tags[1].name = "vermifugado";
        pet.status = status1;

        // Criar um Json para o Body ser enviado a partir da classe Pet e do CSV
        Gson gson = new Gson();
        String jsonBody = gson.toJson(pet);
        
        given()
            .contentType(ct)
            .log().all()
            .body(jsonBody)
        .when()
            .post(uriPet)
        .then()
            .log().all()
            .statusCode(200)
            .body("id", is(petId))
            .body("name", is(petName))
            .body("category.id", is(catId))
            .body("category.name", is(catName))
            .body("status", is(status1));

    }

}
