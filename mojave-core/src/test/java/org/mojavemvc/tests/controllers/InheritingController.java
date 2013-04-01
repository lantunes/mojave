package org.mojavemvc.tests.controllers;

import org.mojavemvc.annotations.StatelessController;

@StatelessController("inheritance")
public class InheritingController extends AbstractController {

    public String getVal() {
        return "inherited";
    }
}
