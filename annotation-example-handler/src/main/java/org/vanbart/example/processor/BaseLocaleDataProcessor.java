package org.vanbart.example.processor;

import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vanbart.example.annotation.Text;

/**
 * Abstract base class for {@link LocaleData} annotation processing.
 * @author Ton van Bart
 * @since 7-5-13 22:31
 */
public abstract class BaseLocaleDataProcessor extends AbstractProcessor {

    protected ProcessingEnvironment processingEnvironment;

    protected Elements elementsUtil;

    private static final boolean DEBUG = true;

    private static final Logger LOG = LoggerFactory.getLogger(BaseLocaleDataProcessor.class);

    public synchronized void init(ProcessingEnvironment pe) {
        debug("init");
        this.processingEnvironment = pe;
        this.elementsUtil = pe.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        debug("process()");
        initialize(roundEnv);
        if (!roundEnv.processingOver()) {
            Set <? extends Element> elements = roundEnv.getElementsAnnotatedWith(LocaleData.class);
            for (Element element : elements) {
                Map<String, Map<String,String>> messagesMap = getMessagesPerLocale(element, roundEnv);
                processMessages(messagesMap, roundEnv);
            }
        } else {
            LOG.info("processingOver() == true, nothing to process.");
        }

        return true;
    }

    /**
     * Perform extra initialization before processing annotations. Subclasses can override this method to perform any needed setup.
     * The deault implementation does nothing.
     */
    protected void initialize(RoundEnvironment roundEnvironment) {
    }

    /**
     * Given a {@link LocaleData} annotated Element, return a map containing the key/message pairs for each {@link Locale}.
     * @param localeAnnotated an Element which has the LocaleData annotation.
     * @return a {@link Map} of key/message pairs, keyed on Locale.
     */
    protected abstract Map<String, Map<String,String>> getMessagesPerLocale(Element localeAnnotated, RoundEnvironment
        roundEnv);

    /**
     * Handle an extracted Map of key/message pairs per Locale.
     * @param messagesPerLocale a Map of key/message pairs, keyed on Locale.
     */
    protected abstract void processMessages(Map<String, Map<String,String>> messagesPerLocale, RoundEnvironment roundEnv);

    private void handleLocaleData(RoundEnvironment roundEnv, Element element) {
        debug("handleLocaleData(roundEnv,"+element.getSimpleName()+")");
        Element packageElement = element.getEnclosingElement();
        if (packageElement.getKind().equals(ElementKind.PACKAGE)) {
            debug("Annotation is in "+((PackageElement) packageElement).getQualifiedName());
        }
        LocaleData localeData = element.getAnnotation(LocaleData.class);
        Locale[] locales = localeData.value();
        debug("Locales in use: ", locales);

        Map<String, Map<String, String>> messagesPerLocale = new HashMap<String, Map<String, String>>();
        for (Locale locale : locales) {
            messagesPerLocale.put(locale.value(), new HashMap<String, String>());
        }

        for (Element enclosedElement : element.getEnclosedElements()) {
            handleEnclosedElement(roundEnv, enclosedElement, messagesPerLocale);
        }

        debugMessagesPerLocale(messagesPerLocale);
    }

    private void debugMessagesPerLocale(Map<String, Map<String, String>> messagesPerLocale) {
        debug("Print messages per locale:\n--------------------------");
        for (String localeName : messagesPerLocale.keySet()) {
            Map<String,String> messages = messagesPerLocale.get(localeName);
            debug(localeName + ":"+ messages.size()+" messages:");
            for (String code : messages.keySet()) {
                debug(String.format("%s:%s\t=%s", localeName, code, messages.get(code)));
            }
        }
    }

    private void handleEnclosedElement(RoundEnvironment roundEnv, Element element, Map<String,Map<String,String>> messagesPerLocale) {
        Text text = element.getAnnotation(Text.class);
        if (text != null) {
            debug("en - put("+element.getSimpleName().toString()+","+text.en()+")");
            messagesPerLocale.get("en").put(element.getSimpleName().toString(), text.en());
            if (text.nl() != null) {
                messagesPerLocale.get("nl").put(element.getSimpleName().toString(), text.nl());
            }
        }


//        debug("handleEnclosedElement("+element+") of kind "+ element.getKind() + " enclosing " + (children == null ? "null" : children.size()) + " elements.");
        // demo: read the javadoc for an element
//        debug("javadoc for this element:"+elementsUtil.getDocComment(element));
    }

    private void debug(String message) {
        if (DEBUG) {
            System.out.println(String.format("BaseLocaleDataProcessor: %s", message));
        }
    }

    private void debug(String msg, Locale[] locales) {
        if (DEBUG) {
            StringBuilder stringBuilder = new StringBuilder(msg).append("[");
            for (Locale locale : locales) {
                stringBuilder.append(locale.value()).append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length()-1).append("]");
            System.out.println("BaseLocaleDataProcessor: " + stringBuilder.toString());
        }
    }

}
