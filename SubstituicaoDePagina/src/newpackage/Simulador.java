package newpackage;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

class Pagina {

    int numeroPagina;
    String instrucao;
    String dado;
    int bitAcessoR;
    int bitModificacaoM;
    int tempoEnvelhecimento;

    public Pagina(int numeroPagina, String i, String d, int r, int m, int t) {
        this.numeroPagina = numeroPagina;
        this.instrucao = i;
        this.dado = d;
        this.bitAcessoR = r;
        this.bitModificacaoM = m;
        this.tempoEnvelhecimento = t;
    }
}

public class Simulador {

    public static void imprimirInstrucoesMatriz(int[][] matriz) {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.print(matriz[i][j] + " ");
            }
            System.out.println(); // Pule para a próxima linha após imprimir uma linha da matriz
        }
    }

    public static int algoritmoNRU(int[][] ramMatrix) {
        int[][] classes = new int[4][10]; // Matriz para classificar as páginas em 4 classes (0 a 3)
        int[] classCounts = new int[4]; // Contagem de páginas em cada classe

        // Classificar as páginas nas classes com base nos bits R e M
        for (int i = 0; i < 10; i++) {
            int r = ramMatrix[i][3]; // Bit de acesso R
            int m = ramMatrix[i][4]; // Bit de modificação M
            int pageClass = (r << 1) | m; // Calcular a classe com base em R e M
            classes[pageClass][classCounts[pageClass]] = i; // Armazenar o índice da página na classe
            classCounts[pageClass]++; // Incrementar a contagem da classe
        }

        // Escolher aleatoriamente uma página da classe de menor prioridade (classe com menor índice)
        int classeEscolhida = 0;
        while (classCounts[classeEscolhida] == 0) {
            classeEscolhida++; // Passar para a próxima classe até encontrar uma classe com páginas
        }

        // Escolher aleatoriamente uma página da classe
        Random random = new Random();
        int indicePaginaEscolhida = classes[classeEscolhida][random.nextInt(classCounts[classeEscolhida])];

        return indicePaginaEscolhida; // Retornar o índice da página a ser substituída
    }

    public static int algoritmoFIFO(int[][] ramMatrix, Queue<Integer> fifoQueue, int[][] swapMatrix) {
        int paginaSubstituida;

        if (!fifoQueue.isEmpty()) {
            // Obtém a página mais antiga da fila
            paginaSubstituida = fifoQueue.poll();

            // Imprimir a instrução que está sendo substituída
            int instrucaoSubstituida = ramMatrix[paginaSubstituida][1];
            System.out.println("Instrução " + instrucaoSubstituida + " foi substituída.");

            // Atualiza a fila para refletir a página recém-substituída
            fifoQueue.offer(paginaSubstituida);
        } else {
            // Se a fila estiver vazia, substituir a primeira página da RAM
            paginaSubstituida = 0;

            // Imprimir a instrução que está sendo substituída
            int instrucaoSubstituida = ramMatrix[paginaSubstituida][1];
            System.out.println("Instrução " + instrucaoSubstituida + " foi substituída.");
        }

        return paginaSubstituida;
    }

    public static int algoritmoFIFOSC(int[][] ramMatrix, int[] secondChanceBits) {
        int paginaSubstituida = -1;

        // Verifique se há uma página com bit 0
        for (int i = 0; i < 10; i++) {
            if (secondChanceBits[i] == 0) {
                paginaSubstituida = i;
                break;
            }
        }

        if (paginaSubstituida == -1) {
            // Se nenhuma página com bit 0 for encontrada, escolha a primeira página com bit 1
            for (int i = 0; i < 10; i++) {
                if (secondChanceBits[i] == 1) {
                    paginaSubstituida = i;
                    secondChanceBits[i] = 0; // Defina o bit como 0
                    break;
                }
            }
        }

        return paginaSubstituida;
    }

    public static int algoritmoRelogio(int[][] ramMatrix, int[] bitRelogio, int hand) {
        int paginaSubstituida = -1;

        // Percorre as páginas da RAM em um ciclo circular começando na posição do ponteiro "hand"
        while (true) {
            if (bitRelogio[hand] == 0) {
                // Se o bit de relógio for 0, a página está pronta para substituição
                paginaSubstituida = hand;
                break;
            } else {
                // Se o bit de relógio for 1, redefine-o para 0 e move o ponteiro adiante
                bitRelogio[hand] = 0;
                hand = (hand + 1) % 10;
            }
        }

        return paginaSubstituida;
    }

    public static int algoritmoWSClock(int[][] ramMatrix, int[] bitRelogio, int hand) {
        int paginaSubstituida = -1;
        int totalPaginas = ramMatrix.length;

        // Enquanto não encontrarmos uma página para substituir, faça a varredura
        while (paginaSubstituida == -1) {
            if (bitRelogio[hand] == 0) {
                // Se o bit de relógio for 0, a página está pronta para substituição
                paginaSubstituida = hand;
            } else {
                // Se o bit de relógio for 1, redefine-o para 0 e move o ponteiro adiante
                bitRelogio[hand] = 0;
                hand = (hand + 1) % totalPaginas;
            }
        }

        return paginaSubstituida;
    }

    public static void main(String[] args) {
        // Matriz para Páginas em SWAP (100 linhas e 6 colunas)
        int[][] swapMatrix = new int[100][6];
        Random random = new Random();

        for (int i = 0; i < 100; i++) {
            // Coluna N: Preencher com números de 0 a 99 sequencialmente
            swapMatrix[i][0] = i;

            // Coluna I: Preencher com números de 1 a 100 sequencialmente
            swapMatrix[i][1] = i + 1;

            // Coluna D: Preencher com números aleatórios de 1 a 50
            swapMatrix[i][2] = random.nextInt(50) + 1;

            // Coluna R: Preencher com 0
            swapMatrix[i][3] = 0;

            // Coluna M: Preencher com 0
            swapMatrix[i][4] = 0;

            // Coluna T: Preencher com números aleatórios de 100 a 9999
            swapMatrix[i][5] = random.nextInt(9900) + 100;
        }
        System.out.println("Matriz SWAP (Início):");
        imprimirInstrucoesMatriz(swapMatrix);

        int[][] ramMatrix = new int[10][6];

        // Preencher a matriz RAM com dados da matriz SWAP
        for (int i = 0; i < 10; i++) {
            int indiceSorteado = random.nextInt(100); // Sortear um número de 0 a 99

            for (int j = 0; j < 6; j++) {
                ramMatrix[i][j] = swapMatrix[indiceSorteado][j];
            }
        }

        System.out.println("\nMatriz RAM (Inicio):");
        imprimirInstrucoesMatriz(ramMatrix);

        int instrucoesExecutadas = 10;
        int[] bitRelogio = new int[10]; // Array de bits de relógio com 10 elementos
        int[] secondChanceBits = new int[10]; // Inicialmente, todos os bits são 0

        for (int i = 0; i < instrucoesExecutadas; i++) {
            int instrucaoRequisitada = random.nextInt(100) + 1;

            // Verificar se a instrução está carregada na memória RAM
            boolean instrucaoCarregada = false;
            int indiceInstrucaoCarregada = -1;

            for (int j = 0; j < 10; j++) {
                if (ramMatrix[j][1] == instrucaoRequisitada) {
                    instrucaoCarregada = true;
                    indiceInstrucaoCarregada = j;
                    break;
                }
            }

            if (instrucaoCarregada) {
                // A instrução está carregada na memória RAM
                // Atualize o bit de acesso R, se necessário
                int paginaCarregada = indiceInstrucaoCarregada;
                ramMatrix[paginaCarregada][3] = 1; // Defina o bit de acesso R como 1

                // ... (atualização do bit de modificação M e outros, se necessário)
            } else {
                // A instrução não está carregada na memória RAM
                // Aplicar o algoritmo NRU para substituição
                int paginaSubstituida = algoritmoNRU(ramMatrix);

                // Atualizar a matriz RAM com a página substituída
                int paginaSWAP = ramMatrix[paginaSubstituida][0];
                for (int k = 0; k < 6; k++) {
                    ramMatrix[paginaSubstituida][k] = swapMatrix[paginaSWAP][k];
                }

                System.out.println("Instrucao " + instrucaoRequisitada + " nao esta carregada na memoria RAM. Pagina " + paginaSWAP + " substituida.");
            }
        }

        /* Imprimir as matrizes RAM e SWAP no final
        System.out.println("\nMatriz SWAP (Final):");
        imprimirInstrucoesMatriz(swapMatrix);
        */
        System.out.println("\nMatriz RAM (Final):");
        imprimirInstrucoesMatriz(ramMatrix);
    }
}
