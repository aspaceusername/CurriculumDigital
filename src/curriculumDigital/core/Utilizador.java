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
//::                                                               (c)2024   ::
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//////////////////////////////////////////////////////////////////////////////
package curriculumDigital.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import blockchain.utils.SecurityUtils;

/**
 * Created on 09/10/2024, 17:23:45
 *
 * @author manso - computer
 */
public class Utilizador {

    private String name;

    private PublicKey pub;
    private PrivateKey priv;
    private Key sim;

    public Utilizador(String name) {
        this.name = name;
        this.pub = null;
        this.priv = null;
        this.sim = null;
    }

    public Utilizador() throws Exception {
        this.name = "noName";
        this.pub = null;
        this.priv = null;
        this.sim = null;
    }
    
    

    public void generateKeys() throws Exception {
        this.sim = SecurityUtils.generateAESKey(256);
        KeyPair kp = SecurityUtils.generateECKeyPair(256);
        this.pub = kp.getPublic();
        this.priv = kp.getPrivate();
    }

    public void save(String password) throws Exception {
        //encriptar a chave privada
        byte[] secret = SecurityUtils.encrypt(priv.getEncoded(), password);
        Files.write(Path.of(this.name + ".priv"), secret);
        //encrptar a chave simetrica
        byte[] simData = SecurityUtils.encrypt(sim.getEncoded(), password);
        Files.write(Path.of(this.name + ".sim"), simData);
        //guardar a public
        Files.write(Path.of(this.name + ".pub"), pub.getEncoded());
    }

    public void load(String password) throws Exception {
        //desencriptar a privada
        byte[] privData = Files.readAllBytes(Path.of(this.name + ".priv"));
        privData = SecurityUtils.decrypt(privData, password);
        //desencriptar a privada
        byte[] simData = Files.readAllBytes(Path.of(this.name + ".sim"));
        simData = SecurityUtils.decrypt(simData, password);
        //ler a publica
        byte[] pubData = Files.readAllBytes(Path.of(this.name + ".pub"));
        this.priv = SecurityUtils.getPrivateKey(privData);
        this.pub = SecurityUtils.getPublicKey(pubData);
        this.sim = SecurityUtils.getAESKey(simData);
    }
    
    public void loadPublic() throws Exception{
         //ler a publica
        byte[] pubData = Files.readAllBytes(Path.of(this.name + ".pub"));
        this.pub = SecurityUtils.getPublicKey(pubData);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PublicKey getPub() {
        return pub;
    }

    public void setPub(PublicKey pub) {
        this.pub = pub;
    }

    public PrivateKey getPriv() {
        return priv;
    }

    public void setPriv(PrivateKey priv) {
        this.priv = priv;
    }

    public Key getSim() {
        return sim;
    }

    public void setSim(Key sim) {
        this.sim = sim;
    }

}
