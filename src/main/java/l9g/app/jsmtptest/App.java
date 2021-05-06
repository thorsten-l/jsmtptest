package l9g.app.jsmtptest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.xml.bind.JAXB;
import lombok.Getter;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Strings;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>
 */
public class App
{
  final static Logger LOGGER = LoggerFactory.getLogger(App.class.
    getName());

  private static final String CONFIGURATION = "config.xml";

  @Getter
  private static Configuration config;
  
  @Getter
  private static Options options = new Options();

  public static Message createMimeMessage(String smtpHost, int smtpPort,
    String subject, String from, String to, String bcc)
    throws AddressException, MessagingException
  {

    ///////////////////////////////////////////////////////////////////////////
    Properties properties = new Properties();

    properties.put("mail.smtp.auth", "true");
    properties.put("mail.smtp.host", smtpHost);
    properties.put("mail.smtp.port", smtpPort);
    properties.put("mail.smtp.starttls.enable", "true");
    
    if( options.isDebug())
    {
      LOGGER.info("enable mail debug");
      properties.put("mail.debug", "true");    
    }

    Session session = Session.getDefaultInstance(properties);

    ///////////////////////////////////////////////////////////////////////////
    Message message = new MimeMessage(session);

    message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

    if (!Strings.isNullOrEmpty(bcc))
    {
      message.setFrom(new InternetAddress(bcc + " <" + from + ">"));
      message.setReplyTo(InternetAddress.parse(bcc));
      message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(
        bcc));
    }
    else
    {
      message.setFrom(new InternetAddress(from));
    }

    message.setSubject(subject);
    message.setSentDate(new Date());

    return message;
  }

  private static void readConfiguration(String configFileName)
  {
    LOGGER.info("reading configuration file {}", configFileName);

    try
    {
      Configuration c;
      File configFile = new File(configFileName);

      LOGGER.info("Config file: {}", configFile.getAbsolutePath());

      if (configFile.exists() && configFile.canRead())
      {
        c = JAXB.unmarshal(new FileReader(configFile), Configuration.class);

        LOGGER.debug("new config <{}>", c);

        if (c != null)
        {
          LOGGER.info("setting config");
          config = c;
        }
      }
      else
      {
        LOGGER.info("Can NOT read config file");
      }

      if (config == null)
      {
        LOGGER.error("config file NOT found");
        System.exit(2);
      }
    }
    catch (FileNotFoundException e)
    {
      LOGGER.error("Configuratione file config.xml not found ", e);
      System.exit(2);
    }
  }

  private static void writeConfiguration()
  {
    Configuration c = new Configuration();
    c.setAttachFile("");
    c.setBcc("bcc email address");
    c.setFrom("from email address");
    c.setMessage("sample message");
    c.setPassword("your password");
    c.setUser("your user");
    c.setSmtpHost("smtp host");
    c.setSmtpPort(587);
    c.setSsl(false);
    c.setStartTls(true);
    c.setSubject("subject");
    c.setTo("to email address");

    File configFile = new File(CONFIGURATION);

    LOGGER.info("Writing config file: {}", configFile.getAbsolutePath());
    JAXB.marshal(c, configFile);
  }

  public static void main(String[] args)
  {
    CmdLineParser parser = new CmdLineParser(options);

    try
    {
      parser.parseArgument(args);
    }
    catch (CmdLineException e)
    {
      System.out.println(e.getMessage());
      options.setHelp(true);
    }


    BuildProperties build = BuildProperties.getInstance();
    LOGGER.info("Project Name    : {}", build.getProjectName());
    LOGGER.info("Project Version : {}", build.getProjectVersion());
    LOGGER.info("Build Timestamp : {}", build.getTimestamp());

    if (options.isInit())
    {
      writeConfiguration();
      System.exit(0);
    }

    if (options.isHelp() || !options.isSendMail())
    {
      System.out.println("jSMTPtest usage:");
      parser.printUsage(System.out);
      System.exit(0);
    }
    
    if (Strings.isNullOrEmpty(options.getConfigFileName()))
    {
      readConfiguration(CONFIGURATION);
    }
    else
    {
      readConfiguration(options.getConfigFileName());
    }
    
    if ( options.isStarttls() || config.isStartTls())
    {
      LOGGER.info( "Force use of STARTTLS");
      System.getProperties().put("mail.smtp.starttls.enable", "true");
    }
    
    if ( !Strings.isNullOrEmpty(options.getTlsProtocols()))
    {
      LOGGER.info( "Force use of TLS protocols: {}", options.getTlsProtocols());
      System.getProperties().put("mail.smtp.ssl.protocols", options.getTlsProtocols());
    }
    
    if (!Strings.isNullOrEmpty(options.getMailBcc())) config.setBcc(options.getMailBcc());
    if (!Strings.isNullOrEmpty(options.getMailMessage())) config.setMessage(options.getMailMessage());
    if (!Strings.isNullOrEmpty(options.getMailSubject())) config.setSubject(options.getMailSubject());
    if (!Strings.isNullOrEmpty(options.getMailTo())) config.setTo(options.getMailTo());
    
    try
    {
      Message message = createMimeMessage(config.getSmtpHost(),
        config.getSmtpPort(), config.getSubject(), config.getFrom(), config.
        getTo(), config.getBcc());

      boolean hasAttachment = !Strings.isNullOrEmpty(config.getAttachFile());

      if (hasAttachment)
      {
        Multipart multipart = new MimeMultipart();

        // body
        BodyPart messageBodyPart = new MimeBodyPart();

        messageBodyPart.setContent(config.getMessage(),
          "text/plain; charset=utf-8");
        multipart.addBodyPart(messageBodyPart);

        // attachment
        messageBodyPart = new MimeBodyPart();

        DataSource source = new FileDataSource(config.getAttachFile());

        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(config.getAttachFile());

        multipart.addBodyPart(messageBodyPart);

        message.setContent(multipart);
      }
      else
      {
        message.setContent(config.getMessage(), "text/plain; charset=utf-8");
      }

      LOGGER.info("sending message.");
      Transport.send(message, message.getAllRecipients(), config.getUser(),
        config.getPassword());
      LOGGER.info("done.");
    }
    catch (Exception ex)
    {
      java.util.logging.Logger.getLogger(App.class.getName()).log(Level.SEVERE,
        "Address error",
        ex);
    }

  }

}
