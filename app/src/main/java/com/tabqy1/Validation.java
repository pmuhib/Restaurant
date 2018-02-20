package com.tabqy1;

/**
 * Created by lenovo on 4/6/2017.
 */

public class Validation {

    public static boolean checkValidEmail(final String email)
    {
        if(email!=null){
            String regularExpression = "^(<([a-z0-9_\\.\\-]+)\\@([a-z0-9_\\-]+\\.)+[a-z]{2,6}>|([a-z0-9_\\.\\-]+)\\@([a-z0-9_\\-]+\\.)+[a-z]{2,6})$";
            return email.trim().matches(regularExpression) ;
        }
        return false ;
    }
}
