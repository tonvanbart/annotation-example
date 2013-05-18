package org.vanbart.example.processor;

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;

import org.vanbart.example.annotation.Text;

/**
 * BaseLocaleDataProcessor which retrieves the message texts from @Text annotations in the enum.
 *
 * @author Ton van Bart
 * @since 7-5-13 22:42
 */
@SupportedAnnotationTypes({"org.vanbart.example.annotation.Text"})
public class TextAnnotationProcessor extends BaseLocaleDataProcessor {

    /**
     * package for generated property files, defaults to default package.
     */
    private String localeBasename = "";

    /**
     * Retrieve the value of the BaseName annotation and keep it.
     */
    protected void initialize(RoundEnvironment roundEnvironment) {
        Set<? extends Element> baseNamed = roundEnvironment.getElementsAnnotatedWith(BaseName.class);
        Element[] baseNamedArray = baseNamed.toArray(new Element[baseNamed.size()]);
        if (baseNamedArray.length > 0) {
            BaseName baseName = baseNamedArray[0].getAnnotation(BaseName.class);
            localeBasename = baseName.value();
        }
    }

    /**
     * {@inheritDoc}
     * Retrieves the message texts by reading the content of {@link Text} annotations in the message enum.
     */
    @Override
    protected Map<String, Map<String, String>> getMessagesPerLocale(Element localeAnnotated,
            RoundEnvironment roundEnv) {
        debug("getMessagesPerLocale");
        Element packageElement = localeAnnotated.getEnclosingElement();
        if (packageElement.getKind().equals(ElementKind.PACKAGE)) {
            debug("Annotation is in " + ((PackageElement) packageElement).getQualifiedName());
        }
        LocaleData localeData = localeAnnotated.getAnnotation(LocaleData.class);
        Locale[] locales = localeData.value();
        debug("Locales in use: ", locales);

        Map<String, Map<String, String>> messagesPerLocale = new HashMap<String, Map<String, String>>();
        for (Locale locale : locales) {
            messagesPerLocale.put(locale.value(), new HashMap<String, String>());
        }

        for (Element enclosedElement : localeAnnotated.getEnclosedElements()) {
            handleEnclosedElement(roundEnv, enclosedElement, messagesPerLocale);
        }

        return messagesPerLocale;

    }

    /**
     * {@inheritDoc
     */
    @Override
    protected void processMessages(Map<String, Map<String, String>> messagesPerLocale, RoundEnvironment roundEnv) {
        debug("processMessages --> " + messagesPerLocale.entrySet().size() + " locales");
        Filer filer = processingEnvironment.getFiler();
        try {
            for (String localeName : messagesPerLocale.keySet()) {
                debug("attempt generate of logevents_" + localeName + ".properties");
                Writer propsWriter = filer.createResource(StandardLocation.SOURCE_OUTPUT, localeBasename,
                        "logevents_" + localeName + ".properties", null).openWriter();
                propsWriter.write(String.format("# Generated properties, see @Text annotation in original enum%n"));
                Map<String, String> messages = messagesPerLocale.get(localeName);
                for (String key : messages.keySet()) {
                    propsWriter.write(String.format("%s=%s%n", key, messages.get(key)));
                }
                propsWriter.close();
            }
        } catch (IOException e) {
            processingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "I/O exception while writing properties files:" + e.getMessage());
        }
    }

    private void handleEnclosedElement(RoundEnvironment roundEnv, Element element,
            Map<String, Map<String, String>> messagesPerLocale) {
        Text text = element.getAnnotation(Text.class);
        if (text != null) {
            debug("en - put(" + element.getSimpleName().toString() + "," + text.en() + ")");
            messagesPerLocale.get("en").put(element.getSimpleName().toString(), text.en());
            if (text.nl() != null) {
                messagesPerLocale.get("nl").put(element.getSimpleName().toString(), text.nl());
            }
        }
    }
}
