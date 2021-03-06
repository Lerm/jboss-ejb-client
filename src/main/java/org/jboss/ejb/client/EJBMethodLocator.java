/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.ejb.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;

import org.jboss.marshalling.FieldSetter;
import org.wildfly.common.Assert;

/**
 * A locator for a specific EJB method.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class EJBMethodLocator<T> implements Serializable {

    private static final long serialVersionUID = -1387266421025030533L;

    private final Class<T> viewType;
    private final String methodName;
    private final String[] parameterTypeNames;
    private final transient int hashCode;

    private static final FieldSetter hashCodeSetter = FieldSetter.get(EJBMethodLocator.class, "hashCode");

    public EJBMethodLocator(final Class<T> viewType, final String methodName, final String... parameterTypeNames) {
        Assert.checkNotNullParam("viewType", viewType);
        Assert.checkNotNullParam("methodName", methodName);
        Assert.checkNotNullParam("parameterTypeNames", parameterTypeNames);
        this.viewType = viewType;
        this.methodName = methodName;
        String[] clone = (this.parameterTypeNames = parameterTypeNames.clone());
        for (int i = 0; i < clone.length; i++) {
            Assert.checkNotNullArrayParam("parameterTypeNames", i, clone[i]);
        }
        hashCode = calcHashCode(viewType, methodName, parameterTypeNames);
    }

    private static int calcHashCode(final Class<?> viewType, final String methodName, final String[] parameterTypeNames) {
        return viewType.hashCode() * 13 + (methodName.hashCode() * 13 + (Arrays.hashCode(parameterTypeNames) * 13));
    }

    public Class<T> getViewType() {
        return viewType;
    }

    public String getMethodName() {
        return methodName;
    }

    public int getParameterCount() {
        return parameterTypeNames.length;
    }

    public String getParameterTypeName(int index) {
        return parameterTypeNames[index];
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        Assert.checkNotNullParam("viewType", viewType);
        Assert.checkNotNullParam("methodName", methodName);
        Assert.checkNotNullParam("parameterTypeNames", parameterTypeNames);
        for (int i = 0; i < parameterTypeNames.length; i++) {
            Assert.checkNotNullArrayParam("parameterTypeNames", i, parameterTypeNames[i]);
        }
        hashCodeSetter.setInt(this, calcHashCode(viewType, methodName, parameterTypeNames));
    }

    /**
     * Determine whether this object is equal to another.
     *
     * @param other the other object
     * @return {@code true} if they are equal, {@code false} otherwise
     */
    public boolean equals(Object other) {
        return other instanceof EJBMethodLocator && equals((EJBMethodLocator<?>)other);
    }

    /**
     * Determine whether this object is equal to another.
     *
     * @param other the other object
     * @return {@code true} if they are equal, {@code false} otherwise
     */
    public boolean equals(EJBMethodLocator<?> other) {
        return this == other || other != null && hashCode == other.hashCode && viewType == other.viewType && methodName.equals(other.methodName) && Arrays.equals(parameterTypeNames, other.parameterTypeNames);
    }

    public int hashCode() {
        return hashCode;
    }
}
