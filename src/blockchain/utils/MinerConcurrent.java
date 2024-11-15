/**
 * Classe responsável por implementar um minerador concorrente de blocos
 * utilizando múltiplas threads para calcular o nonce. A classe permite 
 * encontrar o nonce que, juntamente com os dados fornecidos, gera um hash 
 * que satisfaz a dificuldade especificada.
 * 
 * Esta implementação explora os núcleos disponíveis no processador para 
 * acelerar o processo de mineração.
 * 
 * @author Rúben
 */
package blockchain.utils;

import static blockchain.utils.Miner.MAX_NONCE;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MinerConcurrent {

    /**
     * Classe interna que representa uma Thread que participa no processo 
     * concorrente de mineração. Cada instância trabalha em conjunto com outras 
     * threads para encontrar o nonce correto.
     */
    private static class Thr extends Thread {
        /**
         * Variável partilhada entre as threads para controlar a atribuição de nonces.
         */
        AtomicInteger ticketNonce;

        /**
         * Variável partilhada que armazena o nonce correto encontrado por qualquer thread.
         */
        AtomicInteger trueNonce;

        /**
         * Dados que serão transformados em hash.
         */
        String data;

        /**
         * Valor da dificuldade da mineração, representado pelo número de zeros no 
         * final do hash.
         */
        int difficulty;

        /**
         * Construtor da classe Thr.
         * 
         * @param nonce       Variável partilhada para atribuição de nonces.
         * @param truenonce   Variável partilhada para armazenar o nonce correto.
         * @param data        Dados a serem utilizados no cálculo do hash.
         * @param difficulty  Dificuldade do bloco, definida pelo número de zeros 
         *                    no final do hash.
         */
        public Thr(AtomicInteger nonce, AtomicInteger truenonce, String data, int difficulty) {
            this.ticketNonce = nonce;
            this.trueNonce = truenonce;
            this.data = data;
            this.difficulty = difficulty;
        }

        /**
         * Método que realiza o cálculo do nonce numa thread.
         * Continua a calcular o hash até que o nonce correto seja encontrado.
         */
        public void run() {
            // String de zeros com base na dificuldade
            String zeros = String.format("%0" + difficulty + "d", 0);

            // Continua a calcular enquanto o nonce correto não for encontrado
            while (trueNonce.get() == 0) {
                // Obtém o próximo nonce
                int nonce = ticketNonce.getAndIncrement();
                // Calcula o hash com os dados e o nonce atual
                String hash = Hash.getHash(data + nonce);

                // Verifica se o hash termina com a quantidade necessária de zeros
                if (hash.endsWith(zeros)) {
                    trueNonce.set(nonce); // Define o nonce correto
                }
            }
        }
    }

    /**
     * Método responsável por calcular o nonce de forma concorrente.
     * Utiliza múltiplas threads para explorar todos os núcleos disponíveis 
     * no processador.
     * 
     * @param data       Dados a serem utilizados no cálculo do hash.
     * @param difficulty Dificuldade do bloco, definida pelo número de zeros 
     *                   no final do hash.
     * @return O nonce correto que gera um hash válido.
     */
    public static int getNonce(String data, int difficulty) {
        AtomicInteger sharedNonce = new AtomicInteger(0);
        AtomicInteger truNonce = new AtomicInteger(0);

        // Determina o número de núcleos disponíveis
        int cores = Runtime.getRuntime().availableProcessors();

        // Cria e inicia as threads
        Thr thr[] = new Thr[cores];
        for (int i = 0; i < thr.length; i++) {
            thr[i] = new Thr(sharedNonce, truNonce, data, difficulty);
            thr[i].start();
        }

        // Aguarda que a primeira thread termine (nonce encontrado)
        try {
            thr[0].join();
        } catch (InterruptedException ex) {
            Logger.getLogger(MinerConcurrent.class.getName()).log(Level.SEVERE, null, ex);
        }

        return truNonce.get();
    }

    /**
     * Método principal para testar a classe MinerConcurrent.
     * Compara o tempo de execução de mineração concorrente e sequencial.
     * 
     * @param args Argumentos da linha de comandos (não utilizados).
     */
    public static void main(String[] args) {
        String txt = "hello secure world!!!!";
        int dif = 7;

        // Mineração sequencial
        long timeS = System.currentTimeMillis();
        int nonces = Miner.getNonce(txt, dif);
        timeS = System.currentTimeMillis() - timeS;
        String hash = Hash.getHash(nonces + txt);
        System.out.println(nonces + " " + hash);

        // Mineração concorrente
        long timeP = System.currentTimeMillis();
        long nonce = getNonce(txt, dif);
        timeP = System.currentTimeMillis() - timeP;
        String hash2 = Hash.getHash(nonce + txt);
        System.out.println(nonce + " " + hash2);

        // Exibe a aceleração obtida
        System.out.println("Aceleração = " + timeS / (double) timeP);
    }
}
