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

    public Pagina(int n, String i, String d, int r, int m, int t) {
        this.numeroPagina = n;
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

    public static int algoritmoFIFO(Queue<Integer> fifoQueue) {
        if (!fifoQueue.isEmpty()) {
            int paginaSubstituida = fifoQueue.poll(); // Retorna a posição da página mais antiga na fila
            fifoQueue.offer(paginaSubstituida); // Adicione a posição da página de volta à fila para rastrear a próxima posição mais antiga
            return paginaSubstituida;
        } else {
            System.out.println("Erro: Tentativa de substituir página em uma fila vazia.");
            return -1;
        }
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

        while (paginaSubstituida == -1) {
            if (bitRelogio[hand] == 0 && ramMatrix[hand][3] == 0) {
                // Se o bit de relógio e o bit de acesso R forem 0, a página está pronta para substituição
                paginaSubstituida = hand;
            } else if (bitRelogio[hand] == 0 && ramMatrix[hand][3] == 1) {
                // Se o bit de relógio for 0 e o bit de acesso R for 1, dê uma segunda chance
                bitRelogio[hand] = 1; // Defina o bit de relógio como 1
                hand = (hand + 1) % totalPaginas; // Avance o ponteiro
            } else if (bitRelogio[hand] == 1) {
                // Se o bit de relógio for 1, redefine-o para 0 e avance o ponteiro
                bitRelogio[hand] = 0;
                hand = (hand + 1) % totalPaginas;
            }
        }

        return paginaSubstituida;
    }

    public static void main(String[] args) {
        // Matriz para Páginas em SWAP (100 linhas e 6 colunas)(100 linhas e 6 
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
        Queue<Integer> fila = new LinkedList();

// Preencher a matriz RAM com dados da matriz SWAP
        for (int i = 0; i < 10; i++) {
            int indiceSorteado = random.nextInt(100); // Sortear um número de 0 a 99

            // Adicione o índice da página à fila FIFO
            fila.offer(indiceSorteado);

            for (int j = 0; j < 6; j++) {
                ramMatrix[i][j] = swapMatrix[indiceSorteado][j];
            }
        }
        System.out.println("\n queue: " + fila);
        System.out.println("\nMatriz RAM (Inicio):");
        imprimirInstrucoesMatriz(ramMatrix);

        int instrucoesExecutadas = 50;
        int[] bitRelogio = new int[10]; // Array de bits de relógio com 10 elementos
        int[] secondChanceBits = new int[10]; // Inicialmente, todos os bits são 0
        int hand = 0;

        for (int i = 0; i < instrucoesExecutadas; i++) {
            int instrucaoRequisitada = random.nextInt(1, 100);

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

                // Verificar a probabilidade de modificação (M) e atualizar o campo Dado (D) e Modificado (M) se necessário
                double chanceModificacao = 0.3; // 30% de chance de modificação
                if (random.nextDouble() < chanceModificacao) {
                    // Atualizar o campo Dado (D)
                    ramMatrix[indiceInstrucaoCarregada][2] += 1;

                    // Atualizar o campo Modificado (M) para 1
                    ramMatrix[indiceInstrucaoCarregada][4] = 1;
                }
            } else {
                // A instrução não está carregada na memória RAM

//NRU
                /*int paginaSubstituida = algoritmoNRU(ramMatrix);
                // Atualizar a matriz RAM com a página substituída
                // int paginaSWAP = ramMatrix[paginaSubstituida][0];
                int paginaSWAP = instrucaoRequisitada;
                for (int k = 0; k < 6; k++) {
                    ramMatrix[paginaSubstituida][k] = swapMatrix[paginaSWAP][k];
                }

                System.out.println("Instrucao " + instrucaoRequisitada + " nao esta carregada na memoria RAM. Pagina " + paginaSubstituida + " substituida.");
                 
 //FIFO
                int paginaSubstituida = algoritmoFIFO(fila);
                int paginaSWAP = instrucaoRequisitada;
                for (int k = 0; k < 6; k++) {
                    ramMatrix[paginaSubstituida][k] = swapMatrix[paginaSWAP][k];
                }
                fila.offer(paginaSubstituida); // Adicione a página substituída de volta à fila
                System.out.println("Instrucao " + instrucaoRequisitada + " nao esta carregada na memoria RAM. Pagina " + paginaSubstituida + " substituida.");
                 
 // RELOGIO
                /*int paginaSubstituida = algoritmoRelogio(ramMatrix, bitRelogio, hand);
                int paginaSWAP = instrucaoRequisitada;
                for (int k = 0; k < 6; k++) {
                    ramMatrix[paginaSubstituida][k] = swapMatrix[paginaSWAP][k];
                }
                bitRelogio[paginaSubstituida] = 1; // Atualizar o bit de relógio para a página substituída
                hand = (paginaSubstituida + 1) % 10; // Atualizar a posição do ponteiro "hand"
                System.out.println("Instrucao " + instrucaoRequisitada + " nao esta carregada na memoria RAM. Pagina " + paginaSubstituida + " substituida.");
                 */ 
//WS CLOCK
                int paginaSubstituida = algoritmoWSClock(ramMatrix, bitRelogio, hand);
                int paginaSWAP = instrucaoRequisitada - 1;
                for (int k = 0; k < 6; k++) {
                    ramMatrix[paginaSubstituida][k] = swapMatrix[paginaSWAP][k];
                }
                bitRelogio[paginaSubstituida] = 1; // Atualizar o bit de relógio para a página substituída
                hand = (paginaSubstituida + 1) % 10; // Atualizar a posição do ponteiro "hand"
                System.out.println("Instrucao " + instrucaoRequisitada + " nao esta carregada na memoria RAM. Pagina " + paginaSubstituida + " substituida.");
                
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
