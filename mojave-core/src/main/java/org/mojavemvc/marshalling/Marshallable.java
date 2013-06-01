/*
 * Copyright (C) 2011-2013 Mojavemvc.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mojavemvc.marshalling;

/**
 * A class implementing this interface will itself not 
 * be marshalled. Instead, the entity will be retrieved
 * through this interface, and it will be marshalled instead.
 * 
 * @author Luis Antunes
 */
public interface Marshallable<E> {

    /**
     * Returns the entity that is to be marshalled
     * 
     * @return the entity that is to be marshalled
     */
    E getEntity();
}
