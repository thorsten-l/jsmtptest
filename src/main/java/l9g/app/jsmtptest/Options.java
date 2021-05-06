package l9g.app.jsmtptest;

import lombok.Getter;
import lombok.Setter;
import org.kohsuke.args4j.Option;

/**
 *
 * @author Thorsten Ludewig <t.ludewig@ostfalia.de>th
 */
public class Options
{
  @Setter
  @Getter
  @Option(name = "--help", aliases = "-h", usage = "Display this help",
          required = false)
  private boolean help = false;

  @Setter
  @Getter
  @Option(name = "--init", aliases = "-i", usage = "writing init config.xml file",
          required = false)
  private boolean init = false;

  @Setter
  @Getter
  @Option(name = "--debug", aliases = "-d", usage = "debug transmission",
          required = false)
  private boolean debug = false;

  @Setter
  @Getter
  @Option(name = "--run", aliases = "-r", usage = "run transmit process",
          required = false)
  private boolean sendMail = false;

  @Getter
  @Option(name = "--config", aliases = "-c", usage = "Config file name",
          required = false)
  private String configFileName;

  @Getter
  @Option(name = "--to", aliases = "-t", usage = "To email addresses",
          required = false)
  private String mailTo;

  @Getter
  @Option(name = "--bcc", aliases = "-b", usage = "BCC email addresses",
          required = false)
  private String mailBcc;

  @Getter
  @Option(name = "--subject", aliases = "-s", usage = "mail subject",
          required = false)
  private String mailSubject;

  @Getter
  @Option(name = "--message", aliases = "-m", usage = "mail message",
          required = false)
  private String mailMessage;
  
  
  @Setter
  @Getter
  @Option(name = "--starttls", usage = "force use STARTTLS",
          required = false)
  private boolean starttls = false;

  @Getter
  @Option(name = "--tls-protocols", usage = "force use of specific TLS prtocols",
          required = false)
  private String tlsProtocols;


}
