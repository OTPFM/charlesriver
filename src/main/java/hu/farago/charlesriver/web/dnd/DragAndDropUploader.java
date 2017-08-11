package hu.farago.charlesriver.web.dnd;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.dnd.FileDropTarget;

import hu.farago.charlesriver.datareader.XLSOrderReader;
import hu.farago.charlesriver.model.dto.XLSOrder;
import hu.farago.charlesriver.service.OrderService;
import hu.farago.charlesriver.web.Callback;

@SpringComponent
@UIScope
public class DragAndDropUploader extends VerticalLayout {

	private static final long serialVersionUID = 4887491810019191056L;

	private Label dropArea = new Label("Drop files here...");
	private Button runOrderPlacing = new Button("Place orders");
	private UploadStreamVariable sv;
	private Grid<XLSOrder> grid = new Grid<>(XLSOrder.class);
	
	private List<XLSOrder> orders;

	@Autowired
	public DragAndDropUploader(XLSOrderReader reader) {
		
		runOrderPlacing.setEnabled(false);
		sv = new UploadStreamVariable(new Callback<File>() {

			@Override
			public void callback(File file) {
				try {
					List<XLSOrder> lst = reader.readFileAndRetrieveOrders(file);
					grid.setItems(lst);
					orders = lst;
					runOrderPlacing.setEnabled(true);
				} catch (Exception e) {
					Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
				}
			}
		});

		FileDropTarget<Label> dropTarget = new FileDropTarget<>(dropArea, event -> {

			Collection<Html5File> files = event.getFiles();
			files.forEach(file -> {
				// Max 1 MB files are uploaded
				if (file.getFileSize() <= 1024 * 1024) {
					sv.setFile(file);
					file.setStreamVariable(sv);
				}
			});
		});

		dropArea.setStyleName("bordered");
		grid.setSizeFull();
		setSpacing(true);
		addComponents(dropArea, grid, runOrderPlacing);
	}
	
	public void addClickListenerToButton(OrderService service) {
		this.runOrderPlacing.addClickListener(new ClickListener() {
			private static final long serialVersionUID = -1945560920577150348L;

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					service.placeOrders(null, orders);
				} catch (Exception e) {
					Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
				}
			}
		});
	}

}
