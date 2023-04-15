/*
O código implementa um controlador de uma aplicação Spring Boot que calcula a integral de uma função usando diferentes métodos de integração. A função a ser integrada e o método a ser utilizado podem ser especificados como parâmetros da solicitação HTTP GET para o endpoint /integral. O valor da integral calculada é retornado como uma string formatada.

O método calculateIntegral é responsável por lidar com a solicitação HTTP GET. Ele extrai os valores dos parâmetros a, b e n e a função a ser integrada, function, e o método de integração, method, se nenhum valor padrão for especificado. O método de integração é escolhido usando uma instrução switch e o cálculo da integral é delegado ao método apropriado.

Os métodos calculateSimpson e calculateTrapezoidal são responsáveis pelo cálculo da integral usando o método de Simpson e o método trapezoidal, respectivamente. Eles recebem a função a ser integrada e os limites de integração a e b, além do número de subintervalos n. Eles usam as fórmulas de Simpson e trapezoidal para calcular a integral e retornam o resultado.

O método parseFunction converte uma string que representa uma função em uma função real que pode ser usada para calcular a integral. Ele substitui a variável x na string pelo valor real de x passado como parâmetro e usa a API de script JavaScript para avaliar a expressão da função e retornar o resultado.

Por fim, o método main inicializa a aplicação Spring Boot.*/

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;

@SpringBootApplication
@RestController
public class SimpsonRuleController {

    @GetMapping("/integral")
    public String calculateIntegral(@RequestParam(value = "a") double a,
                                     @RequestParam(value = "b") double b,
                                     @RequestParam(value = "n") int n,
                                     @RequestParam(value = "function", defaultValue = "x^2 + 2*x + 3") String function,
                                     @RequestParam(value = "method", defaultValue = "simpson") String method) {
        Function<Double, Double> f = parseFunction(function);
        double sum = 0;
        switch(method) {
            case "simpson":
                sum = calculateSimpson(f, a, b, n);
                break;
            case "trapezoidal":
                sum = calculateTrapezoidal(f, a, b, n);
                break;
            // adicione aqui outros métodos de integração
            default:
                throw new IllegalArgumentException("Método de integração inválido");
        }
        return String.format("O valor da integral é: %.6f", sum);
    }

    private double calculateSimpson(Function<Double, Double> f, double a, double b, int n) {
        double h = (b - a) / n;
        double sum = 0;
        for (int i = 0; i < n; i++) {
            double x1 = a + i * h;
            double x2 = a + (i + 1) * h;
            double f1 = f.apply(x1);
            double f2 = f.apply(x2);
            double area = (h / 6) * (f1 + 4 * f.apply((x1 + x2) / 2) + f2);
            sum += area;
        }
        return sum;
    }

    private double calculateTrapezoidal(Function<Double, Double> f, double a, double b, int n) {
        double h = (b - a) / n;
        double sum = (f.apply(a) + f.apply(b)) / 2;
        for (int i = 1; i < n; i++) {
            double x = a + i * h;
            sum += f.apply(x);
        }
        return h * sum;
    }

    private Function<Double, Double> parseFunction(String function) {
        // exemplo de função aceita: x^2 + 2*x + 3
        return x -> {
            try {
                String expr = function.replace("x", String.format("%.6f", x));
                Object result = new javax.script.ScriptEngineManager()
                        .getEngineByName("JavaScript")
                        .eval(expr);
                return Double.parseDouble(result.toString());
            } catch (Exception e) {
                throw new IllegalArgumentException("Função inválida");
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(SimpsonRuleController.class, args);
    }
}
