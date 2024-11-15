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
package curriculumDigital.core;

import curriculumDigital.core.Utilizador;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import blockchain.utils.SecurityUtils;

/**
 *
 * @author manso
 */
public class Evento implements Serializable {

    private String from;

    private String to;

    private String fromPub;
    private String toPub;

    private String value;
    private String signature;

    public Evento(String from, String to, String value) {
        this.from = from;
        this.to = to;
        this.value = value;
    }

    public Evento(Utilizador from, Utilizador to, String value) throws Exception {
        this.from = from.getName();
        this.fromPub = Base64.getEncoder().encodeToString(from.getPub().getEncoded());
        this.to = to.getName();
        this.toPub = Base64.getEncoder().encodeToString(to.getPub().getEncoded());
        this.value = value;
        sign(from.getPriv());
    }

    public void sign(PrivateKey priv) throws Exception {
        byte[] dataSign = SecurityUtils.sign(
                (fromPub + toPub + value).getBytes(),
                priv);
        this.signature = Base64.getEncoder().encodeToString(dataSign);
    }

    public boolean isValid() {
        try {
            PublicKey pub = SecurityUtils.getPublicKey(Base64.getDecoder().decode(fromPub));
            byte[] data = (fromPub + toPub + value).getBytes();
            byte[] sign = Base64.getDecoder().decode(signature);
            return SecurityUtils.verifySign(data, sign, pub);
        } catch (Exception ex) {
            return false;
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
    
  

    @Override
    public String toString() {
        return String.format("%-10s -> %s %s -> %s", from, value, isValid() + "", to);
        //return from + "\t : " + to + "\t -> " + value;
    }

    public static long serialVersionUID = 123;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getFromPub() {
        return fromPub;
    }

    public void setFromPub(String fromPub) {
        this.fromPub = fromPub;
    }

    public String getToPub() {
        return toPub;
    }

    public void setToPub(String toPub) {
        this.toPub = toPub;
    }

}
