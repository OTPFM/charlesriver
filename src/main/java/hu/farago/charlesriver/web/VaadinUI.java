package hu.farago.charlesriver.web;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

@Theme("valo")
@SpringUI
public class VaadinUI extends UI {

	private static final long serialVersionUID = 673516373579025498L;

	@Override
    protected void init(VaadinRequest request) {
        setContent(new Label("HELLO THERE!"));
    }

}