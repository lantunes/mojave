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
package org.mojavemvc.tests.controllers;

import org.mojavemvc.annotations.Action;
import org.mojavemvc.annotations.DefaultAction;
import org.mojavemvc.annotations.Model;
import org.mojavemvc.annotations.Param;
import org.mojavemvc.annotations.StatelessController;
import org.mojavemvc.tests.forms.SomeForm;
import org.mojavemvc.tests.forms.SomeFormWithBoolean;
import org.mojavemvc.tests.forms.SubmittableForm;
import org.mojavemvc.views.JSP;
import org.mojavemvc.views.View;

/**
 * @author Luis Antunes
 */
@StatelessController("form-controller")
public class FormController {

    @Action("process")
    public View processForm(@Model SomeForm form) {

        return new JSP("form-result").withAttribute("username", form.getUserName()).withAttribute("password",
                form.getPassword());
    }

    @Action("form2")
    public View form2() {
        return new JSP("form2");
    }

    @Action("process2")
    public View processForm2(@Model SomeForm form, @Param("p1") String p1) {

        return new JSP("form-result2").withAttribute("username", form.getUserName())
                .withAttribute("password", form.getPassword()).withAttribute("p1", p1);
    }

    @Action("form3")
    public View form3() {
        return new JSP("form3");
    }

    @Action("process3")
    public View processForm3(@Model SubmittableForm form) {

        return new JSP("form-result3").withAttribute("username", form.getUserName()).withAttribute("password",
                form.getPassword());
    }

    @Action("populated")
    public View populatedForm() {

        SomeForm form = new SomeForm();
        form.setPassword("pswd");
        form.setUserName("uname");

        return new JSP("form").withModel(form);
    }

    @Action("process4")
    public View processFormWithBoolean(@Model SomeFormWithBoolean form) {

        return new JSP("form-result4").withAttribute("flag", form.isSomeFlag());
    }

    @Action("form4")
    public View form4() {
        return new JSP("form4");
    }

    @DefaultAction
    public View someDefaultAction() {

        return new JSP("form");
    }

}
