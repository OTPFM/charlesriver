package hu.farago.charlesriver.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

import hu.farago.charlesriver.model.dto.Check;
import hu.farago.charlesriver.service.ComplianceService;

@Theme("valo")
@SpringUI
public class VaadinUI extends UI {

	private static final long serialVersionUID = 673516373579025498L;

	@Autowired
	private ComplianceService complianceService;

	private Grid<Check> grid = new Grid<>(Check.class);

	@Override
	protected void init(VaadinRequest request) {
		setContent(new Label("Hello"));
	}

}