package hu.farago.charlesriver.web.credentials;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import hu.farago.charlesriver.model.dto.Credentials;
import hu.farago.charlesriver.service.other.CredentialsService;

@SuppressWarnings("deprecation")
@SpringComponent
@UIScope
public class CredentialsEditor extends VerticalLayout {

	private static final long serialVersionUID = 9120973426705473518L;

	private CredentialsService service;
	private Credentials credentials;

	TextField protocol = new TextField("Protocol");
	TextField hostname = new TextField("Host name");
	TextField port = new TextField("Port");
	TextField username = new TextField("Username");
	PasswordField password = new PasswordField("Password");
	TextField query = new TextField("Query ID");
	TextField basket = new TextField("Basket");
	TextField trader = new TextField("Trader");
	TextField supervisor = new TextField("Manager");

	Button save = new Button("Save", FontAwesome.SAVE);
	CssLayout actions = new CssLayout(save);

	Binder<Credentials> binder = new Binder<>(Credentials.class);

	@Autowired
	public CredentialsEditor(CredentialsService service) {
		this.service = service;

		setSpacing(true);
		actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		addComponents(protocol, hostname, port, username, password, query, basket, trader, supervisor, actions);
		
		credentials = service.getCredentials();
		binder.bindInstanceFields(this);
		binder.setBean(credentials);
		save.addClickListener(e -> {
			service.saveCredentials(credentials);
		});
	}

}
