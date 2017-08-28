package hu.farago.charlesriver.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Image;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import hu.farago.charlesriver.service.OrderService;
import hu.farago.charlesriver.web.credentials.CredentialsEditor;
import hu.farago.charlesriver.web.dnd.DragAndDropUploader;

@Theme("mytheme")
@SpringUI
public class VaadinUI extends UI {

	private static final long serialVersionUID = 673516373579025498L;

	// @Autowired
	// private ComplianceService complianceService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private DragAndDropUploader uploader;
	
	@Autowired
	private CredentialsEditor credentialsEditor;

	private TabSheet tab = new TabSheet();

	@Override
	protected void init(VaadinRequest request) {
		uploader.addClickListenerToButton(orderService);
		buildTabHome();
		buildTab1();
		buildTab2();
		setContent(tab);
	}

	private void buildTabHome() {
		Image mainImage = new Image(null, new ThemeResource("img/river.jpg"));
		VerticalLayout mainLayout = new VerticalLayout();

		mainLayout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
		mainLayout.addComponent(mainImage);

		tab.addTab(mainLayout, "Home");
	}

	private void buildTab1() {
		VerticalLayout mainLayout = new VerticalLayout(uploader);
		tab.addTab(mainLayout, "Orders");
	}

	private void buildTab2() {
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.addComponent(credentialsEditor);
		tab.addTab(mainLayout, "Credentials");
	}

}