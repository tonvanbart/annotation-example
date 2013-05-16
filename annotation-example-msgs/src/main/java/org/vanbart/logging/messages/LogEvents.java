package org.vanbart.logging.messages;

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;
import org.vanbart.example.annotation.Text;

/**
 * Example LogEvents enum which specifies message texts both in annotations and Javadoc.
 * Which one is used will depend on the annotation preprocessor activated in the build.
 * @author Ton van Bart
 * @since 21-4-13 22:00
 */
@BaseName("org.vanbart.logging.messages")
@LocaleData({@Locale("en"),@Locale("nl")})
public enum LogEvents {

    /**
     * nl=Boodschap een uit javadoc
     * en=Message one from javadoc
     */
    @Text(en="Message one, from annotation: boo!",nl="Boodschap een, uit annotatie")
    MESSAGE_ONE("1"),

    /**
     * nl=Tekst voor boodschap twee
     * en=Text for message two in javadoc
     */
    @Text(en="English message two, used for both locales")
    MESSAGE_TWO("2");

    private String id;

    private LogEvents(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

}
