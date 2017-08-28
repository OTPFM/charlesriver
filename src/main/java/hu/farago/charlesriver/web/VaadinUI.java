package hu.farago.charlesriver.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import hu.farago.charlesriver.service.OrderService;
import hu.farago.charlesriver.web.dnd.DragAndDropUploader;

@Theme("mytheme")
@SpringUI
public class VaadinUI extends UI {

	private static final long serialVersionUID = 673516373579025498L;

//	@Autowired
//	private ComplianceService complianceService;
	
	@Autowired
	private OrderService orderService;

	@Autowired
	private DragAndDropUploader uploader;
	
	@Override
	protected void init(VaadinRequest request) {
		VerticalLayout mainLayout = new VerticalLayout(uploader);
		uploader.addClickListenerToButton(orderService);
		setContent(mainLayout);
	}

}