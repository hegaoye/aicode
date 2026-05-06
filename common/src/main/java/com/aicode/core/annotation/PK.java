package com.aicode.core.annotation;

import java.io.Serializable;

@FunctionalInterface
public interface PK<T> extends Serializable {

    Object pk(T t);

}
