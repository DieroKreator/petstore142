import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasLength;
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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    @CsvFileSource(resources = "/csv/userMassa.csv", numLinesToSkip = 1, delimiter = ',')
    public void testPostUserDDT(
        int userId,
        String username,
        String firstName,
        String lastName,
        String email,
        String password,
        String phone,
        int userStatus
    ) 
    {
        User user = new User();

        user.id = userId;
        user.username = username;
        user.firstName = firstName;
        user.lastName = lastName;
        user.email = email;
        user.password = password; 
        user.phone = phone; 
        user.userStatus = userStatus; 

        // Criar um Json para o Body ser enviado a partir da classe User e do CSV
        Gson gson = new Gson();
        String jsonBody = gson.toJson(user);
        
        given()
            .contentType(ct)
            .log().all()
            .body(jsonBody)
        .when()
            .post(uriUser)
        .then()
            .log().all()
            .statusCode(200)
            .body("id", is(userId))
            .body("username", is(username))
            .body("firstName", is(firstName))
            .body("lastName", is(lastName))
            .body("email", is(email))
            .body("password", is(password))
            .body("phone", is(phone))
            .body("userStatus", is(userStatus));

    }
}
