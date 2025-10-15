package com.romiiis.service.interfaces;

import java.util.Locale;
import java.util.Set;

public interface IAuthService {

    String registerCustomer(String name, String email, String hashPassword);
    String registerTranslator(String name, String email, Set<Locale> langs, String hashPassword);

}
