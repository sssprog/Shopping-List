package com.sssprog.shoppingliststandalone.mvp;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface PresenterClass {
    Class<? extends Presenter> value();
}
