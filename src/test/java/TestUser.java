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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import com.google.gson.Gson;

public class TestUser {
    static String ct = "application/json";
    static String uriUser = "https://petstore.swagger.io/v2/user";
    static String username = "zeca";
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

    @Test @Order(2)
    public void testGetUser(){

        given()
            .contentType(ct)
            .log().all()
            .header("api_key: ", testLogin())
        .when()
            .get(uriUser + "/" + username)
        .then()
            .log().all()
            .statusCode(200)
            .body("username", is("zeca"))
            .body("firstName", is("Zehn"))
            .body("lastName", is("Carlton"))
            .body("email", is("zeca@gmail.com"))
            .body("phone", is("555258465275"))
            .body("userStatus", is(1))
        ;
    }

    @Test @Order(3)
    public void testPutUser() throws IOException{
        // Configura
        String jsonBody = lerArquivoCSV("src/test/resources/json/user2.json");

        given()
            .contentType(ct)
            .log().all()
            .body(jsonBody)
        // Executa
        .when()
            .put(uriUser+ "/" + username)
        // Valida
        .then()
            .log().all()
            .statusCode(200)
            .body("code", is(200))
            .body("type", is("unknown"))
            .body("message", is("740560201"))
        ;
    }

    @Test @Order(4)
    public void testDeleteUser(){
        // Configura --> Dados de entrada e saída no começo da Classe

        given()
            .contentType(ct)
            .log().all()
        // Executa
        .when()
            .delete(uriUser + "/" + username)
        // Valida
        .then()
            .log().all()
            .statusCode(200) // se comunicou e processou
            .body("code", is(200))   // se apagou
            .body("type", is("unknown"))
            .body("message", is("zeca"))
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
        Pet.Category category = pet.new Category(); // instanccia a subclasse Category
        Pet.Tag[] tags = new Pet.Tag[2]; // instanccia a subclasse Tag
        tags[0] = pet.new Tag();
        tags[1] = pet.new Tag();

        pet.id = petId;
        pet.category = category; // associar a pet.category com a subclasse category
        pet.category.id = catId;
        pet.category.name = catName;
        pet.name = petName;
        // pet.photoUrls esta vazio
        pet.tags = tags; // associar a pet.tags com a subclasse tags
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
