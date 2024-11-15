package curriculumDigital.core;

import blockchain.utils.Block;
import blockchain.utils.BlockChain;
import blockchain.utils.MerkleTreeBytes;
import blockchain.utils.ObjectUtils;
import curriculumDigital.gui.GUI;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;


import javax.swing.*;
import java.util.List;
import blockchain.utils.SecurityUtils;
import curriculumDigital.gui.GUI;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author Rúben
 */
public class CurriculumDigital implements Serializable {
    private ArrayList<Evento> ledger;
    
    blockchain.utils.BlockChain bc ;
    MerkleTreeBytes<String> merkleTree;
    int DIFICULTY = 4;
    List<Evento> eventosList = new ArrayList<>();
    
    List<Object> elements = new ArrayList<>();
    List<String> elementsTree = new ArrayList<>();
    List<String> elementsProof = new ArrayList<>();
    Elemento ultimoElemento = new Elemento();
    
    public CurriculumDigital() throws Exception {
        ledger = new ArrayList<>();
        bc = new BlockChain();
        /*User usystem = new User("System");
        usystem.load("123qwe");
        User umaster = new User("Master");
        umaster.loadPublic();
        Transaction t = new Transaction(usystem, umaster, 1000);
        ledger.add(t);
        bc = new BlockChain();
        bc.add(ObjectUtils.convertObjectToBase64(t), DIFICULTY);
        */
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, Exception {
        GUI inic = new GUI();
        inic.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        inic.setVisible(true);
    }
    
    
        public void save(String fileName) throws IOException {
        try ( ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(fileName))) {
            out.writeObject(this);
        }
    }

    public static CurriculumDigital load(String fileName) throws IOException, ClassNotFoundException {
        try ( ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(fileName))) {
            return (CurriculumDigital) in.readObject();
        }
    }
    
    /*    public void add(Evento t) throws Exception {
        if (isValid(t)) {
            ledger.add(t);  
            String txtTransaction = ObjectUtils.convertObjectToBase64(t);
            bc.add(txtTransaction, DIFICULTY);
        } else {
            throw new Exception("Transaction not valid");
        }
    }
    */
    public boolean isValid(Evento t) throws Exception {
        // Check if the event value (e.g., "bachelors in science") is empty or null
        if (t.getValue() == null || t.getValue().trim().isEmpty()) {
            throw new Exception("Event description is empty or null.");
        }

        // Check if the 'from' field (the user adding the event) is empty or null
        if (t.getFrom() == null || t.getFrom().trim().isEmpty()) {
            throw new Exception("The 'from' field (user who added the event) is empty or null.");
        }

        // Check if the 'to' field (the user receiving the event on their curriculum) is empty or null
        if (t.getTo() == null || t.getTo().trim().isEmpty()) {
            throw new Exception("The 'to' field (user who receives the event) is empty or null.");
        }

        // All checks passed, the event is valid
        return true;
    }
        public List<Evento> getTransactionBlockchain() throws Exception{
        List<Evento> lst = new ArrayList<>();
        for(Block b : bc.getChain()){
            Evento t = (Evento) ObjectUtils.convertBase64ToObject(b.getData());
            lst.add(t);
        }
        return lst;
    }
    /**
     * Method to register a new transaction in the blockchain
     * @param t evento a adicionar à blockchain
     * @throws java.lang.Exception
     */
        public void add(Evento t) throws Exception {
        if (isValid(t)) {
            ledger.add(t);  
            String txtTransaction = ObjectUtils.convertObjectToBase64(t);
            bc.add(txtTransaction, DIFICULTY);
        } else {
            throw new Exception("Transaction not valid");
        }
    }
            
    /**
     * Method to add data to the blockchain with a specified difficulty.
     *
     * @param dados The data to add.
     * @param dificuldade The difficulty level for the proof-of-work.
     */
    public void guardarDadosBlockchain(String dados, int dificuldade) {
        bc.add(dados, dificuldade);
        
        // Update the Merkle tree
        if (merkleTree == null) {
            merkleTree = new MerkleTreeBytes<>(new String[]{dados});
        } else {
            merkleTree.add(dados);
        }
    }

    /**
     * Method to get the current state of the blockchain as an array of Strings.
     *
     * Each block is represented as a string in the format:
     * "Block Index: [index], Data: [data], Hash: [hash], Previous Hash: [prevHash]"
     *
     * @return A string array of the blockchain data.
     */
    public String[] getBlockchainData() {
        List<Block> blocks = bc.getChain();
        String[] blockData = new String[blocks.size()];

        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            blockData[i] = String.format(block.toString());
        }

        return blockData;
    }

    /**
     * Method to get the proof for a specific data element from the Merkle tree.
     *
     * @param data The data to get proof for.
     * @return The proof as a list of byte arrays.
     */
    public List<byte[]> getMerkleProof(String data) {
        return merkleTree.getProof(data);
    }

    /**
     * Method to verify the proof for a specific data element.
     *
     * @param data The data to verify.
     * @param proof The proof to verify against.
     * @return True if the proof is valid, false otherwise.
     */
    public boolean verifyMerkleProof(String data, List<byte[]> proof) {
        return MerkleTreeBytes.isProofValid(data, proof);
    }
    
        // Método para adicionar um elemento ao currículo

    
    public String displayElements() {
        String elementsString = merkleTree.toString(); // Get the elements as a string
        return elementsString; // Print or use the string in your GUI
        // You may need to update your GUI component to show this string
    }
    
    @Override
        public String toString() {
        StringBuilder txt = new StringBuilder();
        for( Block b : bc.getChain()){
            Evento t = (Evento) ObjectUtils.convertBase64ToObject(b.getData());
            txt.append(b.getPreviousHash() + " " +
                    t.toString() + " "
                    + b.getNonce() +" "
                    + b.getCurrentHash()
                    +"\n"
                            );
        }

        return txt.toString();
    }
        
    public String getCurriculum(){
        StringBuilder txtCurr = new StringBuilder();
        for (Block b : bc.getChain()){
            Evento t = (Evento) ObjectUtils.convertBase64ToObject(b.getData());
            txtCurr.append(t.toString() + "\n");
        }
        return txtCurr.toString();
    }
    
    /*
        Set e Get para eventosList, o array que irá guardar os eventos
        que serão alimentados à merkletree.
    */
    
    public void addEventosList(Evento event){
        eventosList.add(event);
    }
    public String getEventosList() {
        StringBuilder eventosValues = new StringBuilder();

        for (Evento evento : eventosList) {
            eventosValues.append(evento.getFrom()).append("->").append(evento.getValue()).append("\n");
        }

        return eventosValues.toString().trim(); // Remove trailing newline
    }

}
