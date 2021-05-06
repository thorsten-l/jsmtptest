package l9g.app.jsmtptest;

//~--- JDK imports ------------------------------------------------------------
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@gmail.de>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@ToString
public class Configuration
{
  @Getter
  @Setter
  private String smtpHost;
    
  @Getter
  @Setter
  private int smtpPort;
    
  @Getter
  @Setter
  private boolean startTls;
    
  @Getter
  @Setter
  private boolean ssl;
    
  @Getter
  @Setter
  private String user;

  @Getter
  @Setter
  private String password;

  @Getter
  @Setter
  private String to;
  
  @Getter
  @Setter
  private String from;

  @Getter
  @Setter
  private String bcc;

  @Getter
  @Setter
  private String subject;
  
  @Getter
  @Setter
  private String message;

  @Getter
  @Setter
  private String attachFile;
}
