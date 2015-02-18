package in.iitkaa.mail;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import in.iitkaa.mail.model.Alumnus;
import in.iitkaa.mail.util.AppConfig;
import in.iitkaa.mail.util.AppUtils;
import in.iitkaa.mail.util.GlobalConstants;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.lang.String.format;

/**
 * @author Alok
 * @since 17-02-2015
 * @deprecated In the view of new Struts framework
 */
public class MailSender {

    private static final String BODY_TEXT_TOKEN = "TEXT_CONTENT_GOES_HERE";

    /**
     * Sends mail to all the alumni in the database
     * @param subject        Subject of the mail
     * @param bodyText       Body of the mail
     */
    public void sendMail(String subject, String bodyText) {
        Objects.requireNonNull(subject);
        Objects.requireNonNull(bodyText);

        Base64.Decoder decoder = Base64.getDecoder();
        SendGrid sendGrid = new SendGrid(new String(decoder.decode(AppConfig.SEND_GRID_USERNAME)),
                new String(decoder.decode(AppConfig.SEND_GRID_PASSWORD)));
        SendGrid.Email email = new SendGrid.Email();
        Set<Alumnus> alumni = this.getAlumni();
        alumni.forEach(alumnus -> email.addBcc(alumnus.getEmail()));

//        Alumnus a= new Alumnus();
//        ArrayList<Alumnus> sd= new ArrayList<>();
//        sd.add(new Alumnus("Sandeep", "asd"));
//        sd.add(new Alumnus("Alok", "adadw"));
//        List<Alumnus> filtered = sd.stream().filter(x->x.getName().equals("Sandeep")).collect(Collectors.toList());
//        sd = sd.where(x->x.getName().equals("Sandeep"));

//        System.out.println(sd);
//        System.out.println(filtered);


        email.setFrom(AppConfig.MailConfig.FROM_EMAIL);
        email.setFromName(AppConfig.MailConfig.FROM_NAME);
        email.setSubject(subject);
        this.setHtmlBody(bodyText, email);

        try {
            SendGrid.Response response = null;
            if (!AppConfig.DEV_MODE) {
                response = sendGrid.send(email);
            } else {
                response = new SendGrid.Response(200, "Dev mode ON.");
            }
            System.out.println(response.getMessage());
        } catch (SendGridException e) {
//            if (AppConfig.DEV_MODE) {
//                ScriptEngineManager javascriptEngineManager = new ScriptEngineManager();
//                ScriptEngine scriptEngine = javascriptEngineManager.getEngineByName(AppConfig.JAVASCRIPT_ENGINE);
//                try {
//                    scriptEngine.eval("print(e.errors)");
//                } catch (ScriptException e1) {
//                    e1.printStackTrace();
//                }
//            } else {
            e.printStackTrace();
//            }
        }
    }

    private void setHtmlBody(String bodyText, SendGrid.Email email) {
        String filePath = format("%s%s%s%s%d%s%s", AppConfig.RESOURCE_DIRECTORY, GlobalConstants.SLASH,
                AppConfig.TEMPLATE_FILE, GlobalConstants.UNDERSCORE, AppConfig.TEMPLATE_FILE_VERSION,
                GlobalConstants.DOT, GlobalConstants.HTML);
        try {
            String htmlBody = AppUtils.readFile(new File(filePath));
            htmlBody = htmlBody.replace(MailSender.BODY_TEXT_TOKEN, bodyText);
            email.setHtml(htmlBody);

            // Adding AA logo
            email.addAttachment("logo.png", new File(AppConfig.RESOURCE_DIRECTORY+"/images/logo.png"));
            email.addContentId("logo.png", "logo");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Set<Alumnus> getAlumni() {
        return new HashSet<Alumnus>() {{
            add(new Alumnus("Alok Shukla", "alok@simsbuild.com"));
//            add(new Alumnus("Prakhar Gupta", "prakhar@innosols.co.in"));
        }};

    }

}
