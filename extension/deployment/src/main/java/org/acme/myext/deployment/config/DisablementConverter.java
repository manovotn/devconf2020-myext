package org.acme.myext.deployment.config;

import org.eclipse.microprofile.config.spi.Converter;

public class DisablementConverter implements Converter<Boolean> {

    public DisablementConverter() {
    }

    @Override
    public Boolean convert(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }

        switch (s) {
            case "YES":
            case "yes":
                return true;
            case "NO":
            case "no":
                return false;
        }

        throw new IllegalArgumentException("Unsupported value " + s + " given");
    }
}
