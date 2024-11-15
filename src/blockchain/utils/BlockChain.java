//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: 
//::                                                                         ::
//::     Antonio Manuel Rodrigues Manso                                      ::
//::                                                                         ::
//::     I N S T I T U T O    P O L I T E C N I C O   D E   T O M A R        ::
//::     Escola Superior de Tecnologia de Tomar                              ::
//::     e-mail: manso@ipt.pt                                                ::
//::     url   : http://orion.ipt.pt/~manso                                  ::
//::                                                                         ::
//::     This software was build with the purpose of investigate and         ::
//::     learning.                                                           ::
//::                                                                         ::
//::                                                               (c)2022   ::
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//////////////////////////////////////////////////////////////////////////////
package blockchain.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 22/08/2022, 10:09:17
 *
 * @author IPT - computer
 * @version 1.0
 */
public class BlockChain implements Serializable {

    ArrayList<Block> chain = new ArrayList<>();

    /**
     * gets the last block hash of the chain
     * @return last hash in the chain
     */
    public String getLastBlockHash() {
        //Genesis block
        if (chain.isEmpty()) {
            return String.format("%08d", 0);
        }
        //hash of the last in the list
        return chain.get(chain.size() - 1).currentHash;
    }
    /**
     * Adiciona dados à blockchain, criando e minerando um novo bloco.
     * O processo de mineração utiliza Proof of Work (POW) para encontrar o nonce
     * que satisfaz a dificuldade especificada.
     *
     * @param data       Dados a serem adicionados ao novo bloco.
     * @param dificulty  Dificuldade da mineração do bloco, definida pelo número 
     *                   de zeros no final do hash.
     */
    public void add(String data, int dificulty) {
        // Hash do bloco anterior
        String prevHash = getLastBlockHash();
        // Mineração do bloco
        int nonce = MinerConcurrent.getNonce(prevHash + data, dificulty);
        // Construção de um novo bloco
        Block newBlock = new Block(prevHash, data, nonce);
        // Adição do novo bloco à cadeia
        chain.add(newBlock);
    }


    public Block get(int index) {
        return chain.get(index);
    }

    public String toString() {
        StringBuilder txt = new StringBuilder();
        txt.append("Blochain size = " + chain.size() + "\n");
        for (Block block : chain) {
            txt.append(block.toString() + "\n");
        }
        return txt.toString();
    }
    public List<Block> getChain() {
       return chain;
    }

    public void save(String fileName) throws Exception {
        try ( ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(chain);
        }
    }

    public void load(String fileName) throws Exception {
        try ( ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            this.chain = (ArrayList<Block>) in.readObject();
        }
    }

    public boolean isValid() {
        //Validate blocks
        for (Block block : chain) {
            if (!block.isValid()) {
                return false;
            }
        }
        //validate Links
        //starts in the second block
        for (int i = 1; i < chain.size(); i++) {
            //previous hash !=  hash of previous
            if (chain.get(i).getPreviousHash() != chain.get(i - 1).currentHash) {
                return false;
            }
        }
        return true;
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    private static final long serialVersionUID = 202208221009L;
    //:::::::::::::::::::::::::::  Copyright(c) M@nso  2022  :::::::::::::::::::
    ///////////////////////////////////////////////////////////////////////////
}
