/*
O código é um exemplo de como gerar um fractal de Mandelbrot usando MPI em Java. A ideia é dividir a imagem do fractal em partes iguais entre os processos MPI e, em seguida, cada processo calcula a parte do fractal para sua respectiva região. Cada processo cria um arquivo PNG para a parte do fractal que calculou e, em seguida, o MPI é finalizado.

O código começa importando as bibliotecas necessárias, incluindo o MPI e o ImageIO, que é usado para criar o arquivo PNG do fractal.

Em seguida, o código inicializa o MPI e identifica o rank (identificador) do processo atual e o tamanho total de processos. Em seguida, o tamanho da imagem do fractal é definido como 800x800 pixels.

O código divide a imagem em partes iguais entre os processos, calculando o tamanho de cada pedaço da imagem. O cálculo do conjunto de Mandelbrot é feito em um loop duplo que varia as coordenadas x e y da imagem. A parte específica do fractal que será calculada é definida com base no rank do processo atual e no tamanho do pedaço de imagem atribuído a ele.

Depois que cada processo calcula sua parte do fractal, ele cria um arquivo PNG para essa parte do fractal. O nome do arquivo inclui o número do rank do processo atual. Finalmente, o MPI é finalizado.

O código é simples e fácil de entender, demonstrando como MPI pode ser usado para gerar fractais em Java de forma distribuída.*/

import mpi.MPI;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MandelbrotFractalMPI {

    public static void main(String[] args) throws IOException {
        // Inicialização do MPI
        MPI.Init(args);

        // Identificação do processo atual e total de processos
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        // Definição do tamanho da imagem do fractal
        int width = 800;
        int height = 800;

        // Divisão da imagem em partes iguais entre os processos
        int chunkSize = height / size;
        int startY = rank * chunkSize;
        int endY = startY + chunkSize;

        // Cálculo do conjunto de Mandelbrot para a parte da imagem do fractal do processo atual
        BufferedImage image = new BufferedImage(width, chunkSize, BufferedImage.TYPE_INT_RGB);
        double xMin = -2.0;
        double xMax = 2.0;
        double yMin = -2.0;
        double yMax = 2.0;
        for (int x = 0; x < width; x++) {
            for (int y = startY; y < endY; y++) {
                double cReal = (x * (xMax - xMin) / width) + xMin;
                double cImaginary = (y * (yMax - yMin) / height) + yMin;
                double zReal = 0.0;
                double zImaginary = 0.0;
                int iterations = 0;
                while (zReal * zReal + zImaginary * zImaginary <= 4.0 && iterations < 255) {
                    double zrNew = zReal * zReal - zImaginary * zImaginary + cReal;
                    double ziNew = 2 * zReal * zImaginary + cImaginary;
                    zReal = zrNew;
                    zImaginary = ziNew;
                    iterations++;
                }
                Color color = new Color(iterations, iterations, iterations);
                image.setRGB(x, y - startY, color.getRGB());
            }
        }

        // Criação do arquivo de saída do fractal
        File outputFile = new File("mandelbrot_" + rank + ".png");
        ImageIO.write(image, "png", outputFile);

        // Finalização do MPI
        MPI.Finalize();
    }

}
